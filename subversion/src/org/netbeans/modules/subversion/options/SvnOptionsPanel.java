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
package org.netbeans.modules.subversion.options;

import org.openide.util.Utilities;

/**
 *
 * @author Tomas Stupka
 */
public class SvnOptionsPanel extends javax.swing.JPanel {

    /** Creates new form SvnOptionsPanel */
    public SvnOptionsPanel() {
        initComponents();
        if(Utilities.isWindows()) {
            jLabel5.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel5.windows.text"));
        } else {
            jLabel5.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel5.unix.text"));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();

        jLabel1.setLabelFor(executablePathTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel5.windows.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.browseButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageConnSettingsButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.manageConnSettingsButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel4.text")); // NOI18N

        jLabel2.setLabelFor(annotationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel2.text")); // NOI18N

        annotationTextField.setText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.annotationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.addButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageLabelsButton, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.manageLabelsButton.text")); // NOI18N

        jScrollPane2.setBorder(null);

        jLabel6.setLabelFor(manageConnSettingsButton);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel6.text")); // NOI18N

        jLabel7.setLabelFor(manageLabelsButton);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel8.text")); // NOI18N

        cbOpenOutputWindow.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbOpenOutputWindow, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.cbOpenOutputWindow.text")); // NOI18N
        cbOpenOutputWindow.setToolTipText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.cbOpenOutput.text")); // NOI18N
        cbOpenOutputWindow.setBorder(null);

        org.openide.awt.Mnemonics.setLocalizedText(excludeNewFiles, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.excludeNewFiles.text")); // NOI18N
        excludeNewFiles.setToolTipText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.excludeNewFiles.toolTipText")); // NOI18N
        excludeNewFiles.setBorder(null);

        org.openide.awt.Mnemonics.setLocalizedText(prefixRepositoryPath, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.prefixRepositoryPath.text")); // NOI18N
        prefixRepositoryPath.setToolTipText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.prefixRepositoryPath.toolTipText")); // NOI18N
        prefixRepositoryPath.setBorder(null);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbGetRemoteLocks, org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.cbGetRemoteLocks.text")); // NOI18N
        cbGetRemoteLocks.setToolTipText(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.cbGetRemoteLocks.TTtext")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .add(213, 213, 213))
            .add(layout.createSequentialGroup()
                .add(73, 73, 73)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 301, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(executablePathTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(annotationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 22, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(manageLabelsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(manageConnSettingsButton))
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(cbGetRemoteLocks)
                .addContainerGap(248, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(cbOpenOutputWindow)))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(excludeNewFiles)
                .addContainerGap(94, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(prefixRepositoryPath)
                .addContainerGap(167, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(browseButton)
                    .add(executablePathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel3)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(manageConnSettingsButton))
                    .add(layout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLabel4))
                    .add(layout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(annotationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(14, 14, 14)
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(addButton)
                        .add(18, 18, 18)
                        .add(manageLabelsButton)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9)
                    .add(layout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cbGetRemoteLocks)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel8))
                    .add(layout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbOpenOutputWindow)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(excludeNewFiles)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(prefixRepositoryPath)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        executablePathTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.executablePathTextField.text")); // NOI18N
        executablePathTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.executablePathTextField.text")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.browseButton.text")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.browseButton.text")); // NOI18N
        manageConnSettingsButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.manageConnSettingsButton.text")); // NOI18N
        manageConnSettingsButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.manageConnSettingsButton.text")); // NOI18N
        annotationTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.annotationTextField.text")); // NOI18N
        annotationTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.annotationTextField.text")); // NOI18N
        addButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.addButton.text")); // NOI18N
        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.addButton.text")); // NOI18N
        manageLabelsButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSN_SvnOptionsPanel.manageLabelsButton.text")); // NOI18N
        manageLabelsButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.manageLabelsButton.text")); // NOI18N
        jLabel6.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel6.AccessibleContext.accessibleName")); // NOI18N
        jLabel6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel6.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel7.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel7.AccessibleContext.accessibleName")); // NOI18N
        jLabel7.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "SvnOptionsPanel.jLabel7.AccessibleContext.accessibleDescription")); // NOI18N
        cbOpenOutputWindow.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SvnOptionsPanel.class, "ACSD_SvnOptionsPanel.cbOpenOutput.text")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton addButton = new javax.swing.JButton();
    final javax.swing.JTextField annotationTextField = new javax.swing.JTextField();
    final javax.swing.JButton browseButton = new javax.swing.JButton();
    final javax.swing.JCheckBox cbGetRemoteLocks = new javax.swing.JCheckBox();
    final javax.swing.JCheckBox cbOpenOutputWindow = new javax.swing.JCheckBox();
    final javax.swing.JCheckBox excludeNewFiles = new javax.swing.JCheckBox();
    final javax.swing.JTextField executablePathTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    final javax.swing.JButton manageConnSettingsButton = new javax.swing.JButton();
    final javax.swing.JButton manageLabelsButton = new javax.swing.JButton();
    final javax.swing.JCheckBox prefixRepositoryPath = new javax.swing.JCheckBox();
    // End of variables declaration//GEN-END:variables

}
