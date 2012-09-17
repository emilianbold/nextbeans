/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.refactoring.java.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.source.*;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.modules.refactoring.api.Scope;
import org.netbeans.modules.refactoring.java.Pair;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.Lookups;


/**
 * @author  Jan Becicka
 * @author  Ralph Ruijs
 */
public class WhereUsedPanel extends JPanel implements CustomRefactoringPanel {
    
    private final transient TreePathHandle element;
    private final transient ChangeListener parent;
    private boolean enableScope;

    private final WhereUsedInnerPanel panel;
    
    /** Creates new form WhereUsedPanel */
    private WhereUsedPanel(String name, TreePathHandle e, WhereUsedInnerPanel panel, ChangeListener parent) {
        setName(NbBundle.getMessage(WhereUsedPanel.class,"LBL_WhereUsed")); // NOI18N
        this.element = e;
        this.parent = parent;
        this.enableScope = true;
        this.panel = panel;
        initComponents();
    }
    
    public static WhereUsedPanel create(String name, TreePathHandle e, ElementKind kind, List<Pair<Pair<String, Icon>, TreePathHandle>> classes, ChangeListener parent) {
        final WhereUsedInnerPanel panel;
        switch (kind) {
            case CONSTRUCTOR:
            case METHOD: {
                panel = new WhereUsedPanelMethod(parent, e, classes);
                break;
            }
            case CLASS:
            case ENUM:
            case INTERFACE:
            case ANNOTATION_TYPE: {
                panel = new WhereUsedPanelClass(parent);
                break;
            }
            case PACKAGE: {
                panel = new WhereUsedPanelPackage(parent);
                break;
            }
            case FIELD:
            case ENUM_CONSTANT:
            default: {
                panel = new WhereUsedPanelVariable(parent);
                break;
            }
        }
        return new WhereUsedPanel(name, e, panel, parent);
    }
    
    public Scope getCustomScope() {
        FileObject file = WhereUsedPanel.this.element.getFileObject();
        
        if(!enableScope) {
            return Scope.create(null, null, Arrays.asList(file));
        }

        return scope.getSelectedScope();
    }

    private boolean initialized = false;
    
    String getMethodDeclaringClass() {
        if(panel instanceof WhereUsedPanelMethod) {
            WhereUsedPanelMethod whereUsedPanelMethod = (WhereUsedPanelMethod) panel;
            return whereUsedPanelMethod.getMethodDeclaringClass();
        }
        return null;
    }
    
    @Override
    public void initialize() {
        if (initialized) {
            return;
        }
        JavaSource source = JavaSource.forFileObject(element.getFileObject());
        CancellableTask<CompilationController> task =new CancellableTask<CompilationController>() {
            @Override
            public void cancel() {
                throw new UnsupportedOperationException("Not supported yet."); // NOI18N
            }
            
            @Override
            public void run(CompilationController info) throws Exception {
                info.toPhase(Phase.RESOLVED);

                final Element element = WhereUsedPanel.this.element.resolveElement(info);
                panel.initialize(element, info);

                if((element.getKind().equals(ElementKind.LOCAL_VARIABLE) || element.getKind().equals(ElementKind.PARAMETER))
                        || element.getModifiers().contains(Modifier.PRIVATE)) {
                    enableScope = false;
                } else {
                    enableScope = scope.initialize(Lookups.fixed(WhereUsedPanel.this.element.getFileObject(), WhereUsedPanel.this.element), new AtomicBoolean());
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        innerPanel.removeAll();
                        innerPanel.add(panel, BorderLayout.CENTER);
                        panel.setVisible(true);
                        scope.setVisible(enableScope);
                    }
                });
            }};
            try {
                source.runUserActionTask(task, true);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            initialized = true;
    }

    static abstract class WhereUsedInnerPanel extends JPanel {
        abstract boolean isSearchInComments();
        abstract void initialize(Element element, CompilationController info);
    }
    
//    static String getHeader(Element call, CompilationInfo info) {
//        String result = ElementHeaders.getHeader(call, info, ElementHeaders.NAME + ElementHeaders.PARAMETERS);
//        if (result.length() > MAX_NAME) {
//            result = result.substring(0,MAX_NAME-1) + "..."; // NOI18N
//        }
//        return UIUtilities.htmlize(result);
//    }
    
    public TreePathHandle getMethodHandle() {
        if(panel instanceof WhereUsedPanelMethod) {
            WhereUsedPanelMethod whereUsedPanelMethod = (WhereUsedPanelMethod) panel;
            return whereUsedPanelMethod.getMethodHandle();
        }
        return null;
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        innerPanel = new javax.swing.JPanel();
        scope = new org.netbeans.modules.refactoring.spi.ui.ScopePanel(WhereUsedPanel.class.getCanonicalName().replace('.', '-'), NbPreferences.forModule(WhereUsedPanel.class), "whereUsed.scope");
        jLabel1 = new javax.swing.JLabel();

        innerPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setLabelFor(scope);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_Scope")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(innerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scope, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(innerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scope, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel innerPanel;
    private javax.swing.JLabel jLabel1;
    private org.netbeans.modules.refactoring.spi.ui.ScopePanel scope;
    // End of variables declaration//GEN-END:variables

    public boolean isMethodFromBaseClass() {
        if(panel instanceof WhereUsedPanelMethod) {
            WhereUsedPanelMethod methodPanel = (WhereUsedPanelMethod) panel;
            return methodPanel.isMethodFromBaseClass();
        }
        return false;
    }
    
    public boolean isMethodOverriders() {
        if(panel instanceof WhereUsedPanelMethod) {
            WhereUsedPanelMethod methodPanel = (WhereUsedPanelMethod) panel;
            return methodPanel.isMethodOverriders();
        }
        return false;
    }

    public boolean isMethodFindUsages() {
        if(panel instanceof WhereUsedPanelMethod) {
            WhereUsedPanelMethod methodPanel = (WhereUsedPanelMethod) panel;
            return methodPanel.isMethodFindUsages();
        }
        return false;
    }

    public boolean isClassSubTypes() {
        if(panel instanceof WhereUsedPanelClass) {
            WhereUsedPanelClass classPanel = (WhereUsedPanelClass) panel;
            return classPanel.isClassSubTypes();
        }
        return false;
    }
    
    public boolean isClassSubTypesDirectOnly() {
        if(panel instanceof WhereUsedPanelClass) {
            WhereUsedPanelClass classPanel = (WhereUsedPanelClass) panel;
            return classPanel.isClassSubTypesDirectOnly();
        }
        return false;
    }

    public boolean isClassFindUsages() {
        if(panel instanceof WhereUsedPanelClass) {
            WhereUsedPanelClass classPanel = (WhereUsedPanelClass) panel;
            return classPanel.isClassFindUsages();
        }
        return false;
    }
    
    public boolean isSearchInComments() {
        return panel.isSearchInComments();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
