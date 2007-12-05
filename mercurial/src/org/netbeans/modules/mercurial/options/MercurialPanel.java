/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.mercurial.options;

import org.netbeans.modules.mercurial.HgModuleConfig;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

final class MercurialPanel extends javax.swing.JPanel {
    
    private final MercurialOptionsPanelController controller;
    private final DocumentListener listener;
    
    MercurialPanel(MercurialOptionsPanelController controller) {
        this.controller = controller;
        this.listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { nameChange(); }
            public void removeUpdate(DocumentEvent e) { nameChange(); }
            public void changedUpdate(DocumentEvent e) { nameChange(); }
        };
        initComponents();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        userNameTextField.getDocument().addDocumentListener(listener);
    }

    @Override
    public void removeNotify() {
        userNameTextField.getDocument().removeDocumentListener(listener);
        super.removeNotify();
    }


        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        exportFilename = new javax.swing.JLabel();
        backupOnRevertModifications = new javax.swing.JCheckBox();

        jLabel1.setLabelFor(userNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel1.text")); // NOI18N

        jLabel2.setLabelFor(executablePathTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(execPathBrowseButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.browseButton.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jPanel1.border.title"))); // NOI18N

        jLabel3.setLabelFor(annotationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel3.text")); // NOI18N

        annotationTextField.setText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.annotationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.addButton.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(annotationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 405, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 98, Short.MAX_VALUE)
                .add(addButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(addButton)
                    .add(annotationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MecurialPanel.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.manageButton.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 111, Short.MAX_VALUE)
                .add(manageButton)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(manageButton))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        exportFilename.setLabelFor(exportFilenameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(exportFilename, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.ExportFilename.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(exportFilenameBrowseButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.browseButton2.text")); // NOI18N

        backupOnRevertModifications.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(backupOnRevertModifications, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jCheckBox1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(backupOnRevertModifications)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(exportFilename, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(userNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(exportFilenameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, executablePathTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(execPathBrowseButton)
                                    .add(exportFilenameBrowseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(execPathBrowseButton)
                    .add(jLabel2)
                    .add(executablePathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(exportFilename)
                    .add(exportFilenameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(exportFilenameBrowseButton))
                .add(10, 10, 10)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(backupOnRevertModifications)
                .addContainerGap(35, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void nameChange() {
        controller.changed();
    }

    void load() {
        // TODO read settings and initialize GUI
        // Example:
        // someCheckBox.setSelected(Preferences.userNodeForPackage(MercurialPanel.class).getBoolean("someFlag", false)); // NOI18N
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(MercurialPanel.class).getBoolean("someFlag", false)); // NOI18N
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
        userNameTextField.setText(HgModuleConfig.getDefault().getUserName());
        executablePathTextField.setText(HgModuleConfig.getDefault().getExecutableBinaryPath());
        exportFilenameTextField.setText(HgModuleConfig.getDefault().getExportFilename());
        annotationTextField.setText(HgModuleConfig.getDefault().getAnnotationFormat());
        backupOnRevertModifications.setSelected(HgModuleConfig.getDefault().getBackupOnRevertModifications());
    }
    
    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(MercurialPanel.class).putBoolean("someFlag", someCheckBox.isSelected()); // NOI18N
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(MercurialPanel.class).putBoolean("someFlag", someCheckBox.isSelected()); // NOI18N
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
        HgModuleConfig.getDefault().setUserName(userNameTextField.getText());
        HgModuleConfig.getDefault().setExecutableBinaryPath(executablePathTextField.getText());
        HgModuleConfig.getDefault().setExportFilename(exportFilenameTextField.getText());
        HgModuleConfig.getDefault().setAnnotationFormat(annotationTextField.getText());
        HgModuleConfig.getDefault().setBackupOnRevertModifications(backupOnRevertModifications.isSelected());
    }
    
    boolean valid() {
        // TODO check whether form is consistent and complete
        //return true;
        String username = userNameTextField.getText();
        Boolean valid;
        valid =  HgModuleConfig.getDefault().isUserNameValid(username);
        if (!valid) return false;
        String execpath = executablePathTextField.getText();
        valid = HgModuleConfig.getDefault().isExecPathValid(execpath);
        return valid;
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton addButton = new javax.swing.JButton();
    final javax.swing.JTextField annotationTextField = new javax.swing.JTextField();
    private javax.swing.JCheckBox backupOnRevertModifications;
    final javax.swing.JButton execPathBrowseButton = new javax.swing.JButton();
    final javax.swing.JTextField executablePathTextField = new javax.swing.JTextField();
    private javax.swing.JLabel exportFilename;
    final javax.swing.JButton exportFilenameBrowseButton = new javax.swing.JButton();
    final javax.swing.JTextField exportFilenameTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    final javax.swing.JButton manageButton = new javax.swing.JButton();
    final javax.swing.JTextField userNameTextField = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables
    
}
