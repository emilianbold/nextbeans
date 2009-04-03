/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.repository.register;

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.netbeans.modules.maven.indexer.api.RepositoryIndexer;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.openide.util.NbBundle;

/**
 *
 * @author  Anuradha
 */
public class RepositoryRegisterUI extends javax.swing.JPanel {

    private static File lastFolder = new File(System.getProperty("user.home")); //NOI18N
    private boolean modify = false;
    private boolean singleType = false;

    /** Creates new form RepositoryRegisterUI */
    public RepositoryRegisterUI() {
        initComponents();
        validateInfo();
        String[] types = RepositoryIndexer.getAvailableTypes();
        if (types.length == 1) {
            lblType.setVisible(false);
            comType.setVisible(false);
            singleType = true;
        } 
        comType.setModel(new DefaultComboBoxModel(types));
        comType.setSelectedItem(RepositoryPreferences.TYPE_NEXUS);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btnOK = new javax.swing.JButton();
        lblHeader = new javax.swing.JLabel();
        lblRepoId = new javax.swing.JLabel();
        txtRepoId = new javax.swing.JTextField();
        lblRepoName = new javax.swing.JLabel();
        txtRepoName = new javax.swing.JTextField();
        lblType = new javax.swing.JLabel();
        comType = new javax.swing.JComboBox();
        lblRepoType = new javax.swing.JLabel();
        jraLocal = new javax.swing.JRadioButton();
        jraRemote = new javax.swing.JRadioButton();
        lblRepoPath = new javax.swing.JLabel();
        txtRepoPath = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        lblRepoUrl = new javax.swing.JLabel();
        lblIndexUrl = new javax.swing.JLabel();
        txtIndexUrl = new javax.swing.JTextField();
        txtRepoUrl = new javax.swing.JTextField();
        lblValidate = new javax.swing.JLabel();

        btnOK.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "CMB_Repo_ADD", new Object[] {})); // NOI18N
        btnOK.setEnabled(false);

        lblHeader.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Register_Header", new Object[] {})); // NOI18N

        lblRepoId.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_ID", new Object[] {})); // NOI18N

        txtRepoId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRepoIdKeyReleased(evt);
            }
        });

        lblRepoName.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Name", new Object[] {})); // NOI18N

        txtRepoName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRepoNameKeyReleased(evt);
            }
        });

        lblType.setLabelFor(comType);
        lblType.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "RepositoryRegisterUI.lblType.text")); // NOI18N

        comType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblRepoType.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Type", new Object[] {})); // NOI18N

        buttonGroup1.add(jraLocal);
        jraLocal.setSelected(true);
        jraLocal.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Type_Local", new Object[] {})); // NOI18N
        jraLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jraLocalActionPerformed(evt);
            }
        });

        buttonGroup1.add(jraRemote);
        jraRemote.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Type_Remote", new Object[] {})); // NOI18N
        jraRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jraRemoteActionPerformed(evt);
            }
        });

        lblRepoPath.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Path", new Object[] {})); // NOI18N

        txtRepoPath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtRepoPathKeyTyped(evt);
            }
        });

        btnBrowse.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "CMD_Repo_Path_Browse", new Object[] {})); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        lblRepoUrl.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_URL", new Object[] {})); // NOI18N

        lblIndexUrl.setText(org.openide.util.NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Index_Url", new Object[] {})); // NOI18N

        txtIndexUrl.setEditable(false);
        txtIndexUrl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIndexUrlKeyReleased(evt);
            }
        });

        txtRepoUrl.setEditable(false);
        txtRepoUrl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRepoUrlKeyReleased(evt);
            }
        });

        lblValidate.setForeground(new java.awt.Color(204, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblValidate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                            .add(lblHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblRepoName)
                                    .add(lblRepoId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                                    .add(lblType))
                                .add(14, 14, 14)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(txtRepoId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                                    .add(txtRepoName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, comType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblRepoType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jraLocal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(layout.createSequentialGroup()
                                        .add(21, 21, 21)
                                        .add(lblRepoPath))
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jraRemote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                            .add(21, 21, 21)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                .add(org.jdesktop.layout.GroupLayout.LEADING, lblIndexUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(org.jdesktop.layout.GroupLayout.LEADING, lblRepoUrl)))))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(txtIndexUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                            .add(txtRepoUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(txtRepoPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnBrowse)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lblHeader)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblRepoId)
                    .add(txtRepoId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblRepoName)
                    .add(txtRepoName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblType)
                    .add(comType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblRepoType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jraLocal)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblRepoPath)
                    .add(btnBrowse)
                    .add(txtRepoPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jraRemote)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblRepoUrl)
                    .add(txtRepoUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblIndexUrl)
                    .add(txtIndexUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                .add(lblValidate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        JFileChooser chooser = new JFileChooser(lastFolder);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Path_Header", new Object[] {}));

        chooser.setMultiSelectionEnabled(false);
        if (txtRepoPath.getText().trim().length() > 0) {
            File fil = new File(txtRepoPath.getText().trim());
            if (fil.exists()) {
                chooser.setSelectedFile(fil);
            }
        }
        int ret = chooser.showDialog(SwingUtilities.getWindowAncestor(this), NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_SELECT", new Object[] {}));
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtRepoPath.setText(chooser.getSelectedFile().getAbsolutePath());
            txtRepoPath.requestFocusInWindow();
        }
        validateInfo();
}//GEN-LAST:event_btnBrowseActionPerformed

private void jraLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jraLocalActionPerformed
    txtRepoUrl.setEditable(false);
    txtIndexUrl.setEditable(false);
    txtRepoPath.setEditable(true);
    btnBrowse.setEnabled(true);
    validateInfo();
}//GEN-LAST:event_jraLocalActionPerformed

private void jraRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jraRemoteActionPerformed
    selectRemoteRepo(true);
}//GEN-LAST:event_jraRemoteActionPerformed

private void selectRemoteRepo(boolean checkValidity) {
    txtRepoPath.setEditable(false);
    btnBrowse.setEnabled(false);
    txtRepoUrl.setEditable(true);
    txtIndexUrl.setEditable(true);                                         
    if (checkValidity) {
        validateInfo();
    }
}

private void txtRepoIdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRepoIdKeyReleased
    validateInfo();
}//GEN-LAST:event_txtRepoIdKeyReleased

private void txtRepoPathKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRepoPathKeyTyped
    validateInfo();
}//GEN-LAST:event_txtRepoPathKeyTyped

private void txtRepoNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRepoNameKeyReleased
    validateInfo();
}//GEN-LAST:event_txtRepoNameKeyReleased

private void txtRepoUrlKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRepoUrlKeyReleased
    validateInfo();
}//GEN-LAST:event_txtRepoUrlKeyReleased

private void txtIndexUrlKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIndexUrlKeyReleased
    validateInfo();
}//GEN-LAST:event_txtIndexUrlKeyReleased

    public void modify(RepositoryInfo info) {
        modify = true;
        txtRepoId.setEditable(false);
        txtRepoId.setText(info.getId());
        txtRepoName.setText(info.getName());
        if (singleType && info.getType() != null) {
            comType.setSelectedItem(info.getType());
        }
        if (info.isLocal()) {
            jraLocal.setSelected(true);
            txtRepoPath.setText(info.getRepositoryPath());
            jraLocalActionPerformed(null);
        } else if (info.isRemoteDownloadable()) {
            jraRemote.setSelected(true);
            txtRepoUrl.setText(info.getRepositoryUrl());
            txtIndexUrl.setText(info.getIndexUpdateUrl());
            jraRemoteActionPerformed(null);
        }
    }
    
    public RepositoryInfo getRepositoryInfo(){
      RepositoryInfo info=new RepositoryInfo(txtRepoId.getText().trim(),
              (String)comType.getSelectedItem(),
              txtRepoName.getText().trim(),
              jraLocal.isSelected()  ? txtRepoPath.getText().trim() : null,
              jraRemote.isSelected() ? txtRepoUrl.getText().trim()  : null,
              jraRemote.isSelected() ? txtIndexUrl.getText().trim() : null);
    
     return info;
    }

    private void validateInfo() {
        //check repo id
        if (txtRepoId.getText().trim().length() == 0) {
            btnOK.setEnabled(false);
            lblValidate.setText(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_id_Error1"));
            return;
        }
        if (!modify) {
            RepositoryInfo info = RepositoryPreferences.getInstance().getRepositoryInfoById(txtRepoId.getText().trim());
            if (info != null && (info.isLocal() || info.isRemoteDownloadable())) {
                btnOK.setEnabled(false);
                lblValidate.setText(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_id_Error2"));
                return;
            } else if (info != null) {
                txtRepoUrl.setText(info.getRepositoryUrl());
                txtRepoName.setText(info.getName());
                jraRemote.setSelected(true);
                selectRemoteRepo(false);
            }
        }

        //check repo name
        if (txtRepoName.getText().trim().length() == 0) {
            btnOK.setEnabled(false);
            lblValidate.setText(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Name_Error1"));
            return;
        }
        if (jraLocal.isSelected()) {
            //check repo url
            if (txtRepoPath.getText().trim().length() == 0 || !new File(txtRepoPath.getText().trim()).exists()) {
                btnOK.setEnabled(false);
                lblValidate.setText(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Path_Error"));
                return;
            }
        } else {
            //check repo url
            if (txtRepoUrl.getText().trim().length() == 0) {
                btnOK.setEnabled(false);
                lblValidate.setText(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Url_Error"));
                return;
            }
            //check repo index url
            if (txtIndexUrl.getText().trim().length() == 0) {
                btnOK.setEnabled(false);
                lblValidate.setText(NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_Index_Url_Error"));
                return;
            }

        }

        lblValidate.setText("");
        btnOK.setEnabled(true);
    }

    public JButton getButton() {
        return btnOK;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnOK;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox comType;
    private javax.swing.JRadioButton jraLocal;
    private javax.swing.JRadioButton jraRemote;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblIndexUrl;
    private javax.swing.JLabel lblRepoId;
    private javax.swing.JLabel lblRepoName;
    private javax.swing.JLabel lblRepoPath;
    private javax.swing.JLabel lblRepoType;
    private javax.swing.JLabel lblRepoUrl;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblValidate;
    private javax.swing.JTextField txtIndexUrl;
    private javax.swing.JTextField txtRepoId;
    private javax.swing.JTextField txtRepoName;
    private javax.swing.JTextField txtRepoPath;
    private javax.swing.JTextField txtRepoUrl;
    // End of variables declaration//GEN-END:variables

}
