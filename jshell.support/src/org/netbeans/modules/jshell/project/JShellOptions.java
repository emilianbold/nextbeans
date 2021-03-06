/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jshell.project;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.netbeans.api.project.Project;

/**
 *
 * @author sdedic
 */
public class JShellOptions extends javax.swing.JPanel implements ItemListener {
    private RunOptionsModel model;
    private Project         project;
    
    /**
     * Creates new form JShellOptions
     */
    public JShellOptions(RunOptionsModel model, Project project) {
        this.model = model;
        this.project = project;
        initComponents();
        updateValues();
        enableDisable();
        
        loaderClass.addItemListener(this);
        loaderSystem.addItemListener(this);
        loaderGet.addItemListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        checkEnable = new javax.swing.JCheckBox();
        classLoaderOptions = new javax.swing.JPanel();
        loaderSystem = new javax.swing.JRadioButton();
        loaderClass = new javax.swing.JRadioButton();
        loaderGet = new javax.swing.JRadioButton();
        methodName = new javax.swing.JTextField();
        className = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(checkEnable, org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.checkEnable.text")); // NOI18N
        checkEnable.setBorder(null);
        checkEnable.setLabel(org.openide.util.NbBundle.getMessage(JShellOptions.class, "CHECK_EnableJShell")); // NOI18N
        checkEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkEnableActionPerformed(evt);
            }
        });
        add(checkEnable, new java.awt.GridBagConstraints());

        classLoaderOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.classLoaderOptions.border.title"))); // NOI18N

        buttonGroup1.add(loaderSystem);
        loaderSystem.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(loaderSystem, org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.loaderSystem.text")); // NOI18N

        buttonGroup1.add(loaderClass);
        org.openide.awt.Mnemonics.setLocalizedText(loaderClass, org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.loaderClass.text")); // NOI18N

        buttonGroup1.add(loaderGet);
        org.openide.awt.Mnemonics.setLocalizedText(loaderGet, org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.loaderGet.text")); // NOI18N

        methodName.setText(org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.methodName.text")); // NOI18N
        methodName.setEnabled(false);

        className.setText(org.openide.util.NbBundle.getMessage(JShellOptions.class, "JShellOptions.className.text")); // NOI18N
        className.setEnabled(false);

        javax.swing.GroupLayout classLoaderOptionsLayout = new javax.swing.GroupLayout(classLoaderOptions);
        classLoaderOptions.setLayout(classLoaderOptionsLayout);
        classLoaderOptionsLayout.setHorizontalGroup(
            classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(classLoaderOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loaderSystem)
                    .addGroup(classLoaderOptionsLayout.createSequentialGroup()
                        .addGroup(classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loaderGet)
                            .addComponent(loaderClass))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(className, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(methodName))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        classLoaderOptionsLayout.setVerticalGroup(
            classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(classLoaderOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loaderSystem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loaderClass)
                    .addComponent(className, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(classLoaderOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loaderGet)
                    .addComponent(methodName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.ipady = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 20, 0);
        add(classLoaderOptions, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void checkEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkEnableActionPerformed
        boolean enable = checkEnable.isSelected();
        model.setJshellEnabled(enable);
        enableDisable();
    }//GEN-LAST:event_checkEnableActionPerformed

    private boolean updating = false;
    
    private void updateValues() {
        if (updating) {
            return;
        }
        updating = true;
        
        RunOptionsModel.LoaderPolicy pol = model.getPolicy();
        String cn = model.getLoadClassName();
        String eval = model.getFieldName();
        if (cn == null) {
            pol = RunOptionsModel.LoaderPolicy.SYSTEM;
        } else if (eval != null) {
            eval = cn + "." + eval;
        } else {
            eval = model.getMethodName();
            if (eval != null) {
                eval = cn + "." + eval + "()";
            }
        }
        if (pol == RunOptionsModel.LoaderPolicy.EVAL && eval == null) {
            pol = RunOptionsModel.LoaderPolicy.CLASS;
        }
        switch (pol) {
            case CLASS:
                className.setText(cn);
                loaderClass.setSelected(true);
                break;
            case EVAL:
                loaderGet.setSelected(true);
                break;
            case SYSTEM:
                loaderSystem.setSelected(true);
                break;
        }
        
        updating = false;
        enableDisable();
    }
    
    private void enableDisable() {
        if (model.isJshellEnabled()) {
            classLoaderOptions.setVisible(true);
            
            switch (model.getPolicy()) {
                case CLASS:
                    className.setEnabled(true);
                    methodName.setEnabled(false);
                    break;
                case SYSTEM:
                    className.setEnabled(false);
                    methodName.setEnabled(false);
                    break;
                case EVAL:
                    className.setEnabled(false);
                    methodName.setEnabled(true);
                    break;
            }
        } else {
            classLoaderOptions.setVisible(false);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        RunOptionsModel.LoaderPolicy pol;
        
        if (loaderSystem.isSelected()) {
            pol = RunOptionsModel.LoaderPolicy.SYSTEM;
        } else if (loaderClass.isSelected()) {
            pol = RunOptionsModel.LoaderPolicy.CLASS;
        } else if (loaderGet.isSelected()) {
            pol = RunOptionsModel.LoaderPolicy.EVAL;
        } else {
            return;
        }
        model.setPolicy(pol);
        enableDisable();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox checkEnable;
    private javax.swing.JPanel classLoaderOptions;
    private javax.swing.JTextField className;
    private javax.swing.JRadioButton loaderClass;
    private javax.swing.JRadioButton loaderGet;
    private javax.swing.JRadioButton loaderSystem;
    private javax.swing.JTextField methodName;
    // End of variables declaration//GEN-END:variables
}
