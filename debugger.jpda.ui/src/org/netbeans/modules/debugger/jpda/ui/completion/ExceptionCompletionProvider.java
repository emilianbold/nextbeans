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
package org.netbeans.modules.debugger.jpda.ui.completion;

import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScopeType;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Entlicher
 */
@MimeRegistration(mimeType = ExceptionClassNbDebugEditorKit.MIME_TYPE, service = CompletionProvider.class)
public class ExceptionCompletionProvider implements CompletionProvider {
    
    private static final Logger LOG = Logger.getLogger(ExceptionCompletionProvider.class.getName());
    
    private static final Set<? extends SearchScopeType> scopeAll = Collections.singleton(new ClassSearchScopeType());

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            
            @Override
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                try {
                    if (caretOffset < 0) caretOffset = 0;
                    String text;
                    try {
                        text = doc.getText(0, caretOffset);
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                        text = "";
                    }
                    LOG.log(Level.FINE, "Completion query for ''{0}''", text);
                    ClasspathInfo cpi = ClassCompletionProvider.getClassPathInfo();
                    JavaSource jsrc = JavaSource.create(cpi);
                    try {
                        jsrc.runUserActionTask(new CompletionUserTask(resultSet, cpi, text, caretOffset), true);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } finally {
                    LOG.fine("Completion result set finished.");
                    resultSet.finish();
                }
            }
        }, component);
    }
    
    private static class CompletionUserTask implements Task<CompilationController> {//extends UserTask {
        
        private final CompletionResultSet resultSet;
        private final ClasspathInfo cpi;
        private final String prefix;
        private final int lastPrefixDot;
        private final int caretOffset;

        private CompletionUserTask(CompletionResultSet resultSet, ClasspathInfo cpi, String prefix, int caretOffset) {
            this.resultSet = resultSet;
            this.cpi = cpi;
            this.prefix = prefix;
            this.lastPrefixDot = prefix.lastIndexOf('.');
            this.caretOffset = caretOffset;
        }

        @Override
        public void run(CompilationController cc) throws Exception {
            LOG.log(Level.FINE, "  Running CompletionUserTask, compilation controller = {0}", cc);
            cc.toPhase(Phase.RESOLVED);
            TypeElement thr = cc.getElements().getTypeElement("java.lang.Throwable"); //NOI18N
            LOG.log(Level.FINE, "  Filling results, throwable element = {0}", thr);
            if (thr != null) {
                DeclaredType type = cc.getTypes().getDeclaredType(thr);
                LOG.log(Level.FINE, "  Filling results, throwable declared type = {0}", type);
                if (type != null) {
                    
                    Set<? extends SearchScopeType> scope = Collections.singleton(new ClassSearchScopeType(prefix));
                    int n = prefix.length();
                    ClassIndex classIndex = cpi.getClassIndex();
                    Set<String> packageNames = classIndex.getPackageNames(prefix, false, scope);
                    LOG.log(Level.FINE, "  Have package names = {0}", packageNames);
                    Set<String> resultPackages = new HashSet<>();
                    Set<String> fullResultPackages = new HashSet<>();
                    for (String pn : packageNames) {
                        int dot = pn.indexOf('.', n);
                        if (dot > 0) pn = pn.substring(0, dot);
                        String fpn = pn;
                        if (lastPrefixDot > 0) pn = pn.substring(lastPrefixDot + 1);
                        if (!resultPackages.contains(pn)) {
                            resultPackages.add(pn);
                            fullResultPackages.add(fpn);
                            LOG.log(Level.FINE, "  Considering package: ''{0}''", pn);
                        }
                    }
                    
                    List<DeclaredType> types = fillSubTypes(cc, type);
                    if (lastPrefixDot > 0) {
                        Set<String> classPackages = new HashSet<>(); // Packages containing a class
                        for (DeclaredType dt : types) {
                            TypeElement elem = (TypeElement) dt.asElement();
                            
                            String fqn = elem.getQualifiedName().toString();
                            String className = fqn;
                            className = className.substring(lastPrefixDot + 1);
                            if (fqn.length() > n) {
                                int dot = fqn.indexOf('.', n);
                                if (dot > 0) {
                                    String pn = fqn.substring(0, dot);
                                    pn = pn.substring(lastPrefixDot + 1);
                                    if (resultPackages.contains(pn)) {
                                        classPackages.add(pn);
                                    }
                                }
                                if (dot < 0 || !fullResultPackages.contains(fqn.substring(0, dot))) {
                                    // Class in this package
                                    LOG.log(Level.FINE, "  Adding class name ''{0}''", className);
                                    ElementCompletionItem eci = new ElementCompletionItem(className, elem.getKind(), caretOffset);
                                    eci.setElement(elem);
                                    resultSet.addItem(eci);
                                }
                            }
                        }
                        for (String pn : classPackages) {
                            resultSet.addItem(new ElementCompletionItem(pn, ElementKind.PACKAGE, caretOffset));
                        }
                    } else {
                        for (DeclaredType dt : types) {
                            TypeElement elem = (TypeElement) dt.asElement();
                            String className = elem.getSimpleName().toString();
                            LOG.log(Level.FINE, "  Adding class name ''{0}''", className);
                            ElementCompletionItem eci = new ElementCompletionItem(className, elem.getKind(), caretOffset);
                            eci.setElement(elem);
                            resultSet.addItem(eci);
                        }
                        for (String pn : resultPackages) {
                            resultSet.addItem(new ElementCompletionItem(pn, ElementKind.PACKAGE, caretOffset));
                        }
                    }
                }
            }
        }
        
        private List<DeclaredType> fillSubTypes(CompilationController cc, DeclaredType dType) {
            List<DeclaredType> subtypes = new ArrayList<>();
            //Set<? extends SearchScopeType> scope = Collections.singleton(new ClassSearchScopeType(prefix));
            Types types = cc.getTypes();
            if (prefix != null && prefix.length() > 2 && lastPrefixDot < 0) {
                //Trees trees = cc.getTrees();
                ClassIndex.NameKind kind = ClassIndex.NameKind.CASE_INSENSITIVE_PREFIX;
                for (ElementHandle<TypeElement> handle : cpi.getClassIndex().getDeclaredTypes(prefix, kind, EnumSet.allOf(ClassIndex.SearchScope.class))) {
                    TypeElement te = handle.resolve(cc);
                    if (te != null && /*trees.isAccessible(scope, te) &&*/ types.isSubtype(types.getDeclaredType(te), dType)) {
                        subtypes.add(types.getDeclaredType(te));
                    }
                }
            } else {
                HashSet<TypeElement> elems = new HashSet<>();
                LinkedList<DeclaredType> bases = new LinkedList<>();
                bases.add(dType);
                ClassIndex index = cpi.getClassIndex();
                while (!bases.isEmpty()) {
                    DeclaredType head = bases.remove();
                    TypeElement elem = (TypeElement) head.asElement();
                    if (!elems.add(elem)) {
                        continue;
                    }
                    if (accept(elem)) {
                        subtypes.add(head);
                    }
                    //List<? extends TypeMirror> tas = head.getTypeArguments();
                    //boolean isRaw = !tas.iterator().hasNext();
                    for (ElementHandle<TypeElement> eh : index.getElements(ElementHandle.create(elem), EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS), EnumSet.allOf(ClassIndex.SearchScope.class))) {
                        TypeElement e = eh.resolve(cc);
                        if (e != null) {
                            DeclaredType dt = types.getDeclaredType(e);
                            bases.add(dt);
                        }
                    }
                }
            }
            return subtypes;
        }
        
        private boolean accept(TypeElement elem) {
            String className;
            if (lastPrefixDot < 0) {
                className = elem.getSimpleName().toString();
            } else {
                className = elem.getQualifiedName().toString();
            }
            return className.length() >= prefix.length() &&
                   className.substring(0, prefix.length()).equalsIgnoreCase(prefix);
        }

    }
    
    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return COMPLETION_QUERY_TYPE;
    }
    
}
