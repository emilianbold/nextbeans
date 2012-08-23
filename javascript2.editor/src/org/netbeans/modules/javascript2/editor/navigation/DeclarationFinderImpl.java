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
package org.netbeans.modules.javascript2.editor.navigation;

import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.DeclarationFinder;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.javascript2.editor.jquery.JQueryDeclarationFinder;
import org.netbeans.modules.javascript2.editor.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.lexer.LexUtilities;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.Model;
import org.netbeans.modules.javascript2.editor.model.Occurrence;
import org.netbeans.modules.javascript2.editor.model.OccurrencesSupport;
import org.netbeans.modules.javascript2.editor.parser.JsParserResult;

/**
 *
 * @author Petr Pisl
 */
public class DeclarationFinderImpl implements DeclarationFinder{

    @Override
    public DeclarationLocation findDeclaration(ParserResult info, int caretOffset) {
        JsParserResult jsResult = (JsParserResult)info;
        Model model = jsResult.getModel();
        OccurrencesSupport os = model.getOccurrencesSupport();
        Occurrence occurrence = os.getOccurrence(caretOffset);
        if (occurrence != null) {
            JsObject object = occurrence.getDeclarations().iterator().next();
            return new DeclarationLocation(object.getFileObject(), object.getDeclarationName().getOffsetRange().getStart());
        }
        JQueryDeclarationFinder jQueryFinder = new JQueryDeclarationFinder();
        return jQueryFinder.findDeclaration(info, caretOffset);
    }

    @Override
    public OffsetRange getReferenceSpan(Document doc, int caretOffset) {
        OffsetRange result = null;
        TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(doc, caretOffset);
        if (ts != null) {
            ts.move(caretOffset);
            if (ts.moveNext() && ts.token().id() == JsTokenId.IDENTIFIER) {
                result =  new OffsetRange(ts.offset(), ts.offset() + ts.token().length());
            }
        }
        if(result == null) {
            JQueryDeclarationFinder jQueryFinder = new JQueryDeclarationFinder();
            return jQueryFinder.getReferenceSpan(doc, caretOffset);
        }
        return OffsetRange.NONE;
    }
    
}