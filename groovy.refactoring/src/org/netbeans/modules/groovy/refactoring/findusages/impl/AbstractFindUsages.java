/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.groovy.refactoring.findusages.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.groovy.editor.api.AstUtilities;
import org.netbeans.modules.groovy.editor.api.parser.GroovyParserResult;
import org.netbeans.modules.groovy.editor.api.parser.SourceUtils;
import org.netbeans.modules.groovy.refactoring.GroovyRefactoringElement;
import org.netbeans.modules.groovy.refactoring.findusages.FindUsagesElement;
import org.netbeans.modules.groovy.refactoring.utils.GroovyProjectUtil;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.UserTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Base class for a various type of find usage strategies.
 *
 * It might be implemented in a different ways (e.g. we are looking only for
 * subclasses and we are not interested in other usages etc.)
 *
 * @author Martin Janicek
 */
public abstract class AbstractFindUsages {

    protected final GroovyRefactoringElement element;
    protected final List<FindUsagesElement> usages;

    
    protected AbstractFindUsages(GroovyRefactoringElement element) {
        this.element = element;
        this.usages = new ArrayList<FindUsagesElement>();
    }

    protected abstract AbstractFindUsagesVisitor getVisitor(ModuleNode moduleNode, String defClass);


    /**
     * Collects find usages for a given <code>FileObject</code>
     * @param fo file where we are looking for usages
     */
    public final void findUsages(FileObject fo) {
        try {
            SourceUtils.runUserActionTask(fo, new AddFindUsagesElementsTask(fo, element.getDeclaringClass().getName()));
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public final void clear() {
        usages.clear();
    }

    public final List<FindUsagesElement> getResults() {
        return usages;
    }

    
    protected final class AddFindUsagesElementsTask extends UserTask {

        private final FileObject fo;
        private final String defClass;

        /**
         * Creates find usages task for a specific <code>FileObject</code>
         *
         * @param fo file where we are looking for usages
         * @param defClass fully qualified name of the class for which we are
         * trying to find usages
         */
        public AddFindUsagesElementsTask(FileObject fo, String defClass) {
            this.fo = fo;
            this.defClass = defClass;
        }

        @Override
        public void run(ResultIterator resultIterator) throws Exception {
            GroovyParserResult result = AstUtilities.getParseResult(resultIterator.getParserResult());
            ModuleNode moduleNode = result.getRootElement().getModuleNode();
            BaseDocument doc = GroovyProjectUtil.getDocument(result, fo);

            for (ASTNode node : getVisitor(moduleNode, defClass).findUsages()) {
                if (node.getLineNumber() != -1 && node.getColumnNumber() != -1) {
                    usages.add(new FindUsagesElement(new GroovyRefactoringElement(result, moduleNode, node, fo), doc));
                }
            }
            Collections.sort(usages, new Comparator<FindUsagesElement>() {

                @Override
                public int compare(FindUsagesElement o1, FindUsagesElement o2) {
                    return o1.getLineNumber() - o2.getLineNumber();
                }
            });
        }
    }
}
