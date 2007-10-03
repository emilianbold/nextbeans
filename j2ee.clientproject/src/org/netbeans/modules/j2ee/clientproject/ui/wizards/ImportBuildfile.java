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

package org.netbeans.modules.j2ee.clientproject.ui.wizards;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openide.util.NbBundle;

import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;

public class ImportBuildfile extends javax.swing.JPanel implements DocumentListener {
    private static final long serialVersionUID = 1L;
    
    private JButton ok;
    private String filePath;
    
    /** Creates new form ImportBuildfile */
    public ImportBuildfile(String filePath, JButton okButton) {
        initComponents();
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ImportBuildfile.class, "ACS_IW_BuildFileDialog_A11YDesc"));  // NOI18N
        
        ok = okButton;
        this.filePath = filePath;
        ok.setEnabled(false);
        
        String fileName = filePath + File.separator + GeneratedFilesHelper.BUILD_XML_PATH;
        String msg = MessageFormat.format(
                NbBundle.getMessage(ImportBuildfile.class, "LBL_IW_BuildfileDesc_Label"),
                fileName);
        jLabelDesc.setText(msg);
        jTextFieldBuildName.getDocument().addDocumentListener(this);
        jTextFieldBuildName.setText(NbBundle.getMessage(ImportBuildfile.class, "LBL_IW_ProposedName_TextField")); //NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelDesc = new javax.swing.JLabel();
        jLabelBuildName = new javax.swing.JLabel();
        jTextFieldBuildName = new javax.swing.JTextField();
        jLabelCreatedFile = new javax.swing.JLabel();
        jTextFieldCreatedFile = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        add(jLabelDesc, gridBagConstraints);

        jLabelBuildName.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ImportBuildfile.class, "LBL_IW_BuildFilename_LabelMnemonic").charAt(0));
        jLabelBuildName.setLabelFor(jTextFieldBuildName);
        jLabelBuildName.setText(NbBundle.getMessage(ImportBuildfile.class, "LBL_IW_BuildFilename_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(jLabelBuildName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(jTextFieldBuildName, gridBagConstraints);
        jTextFieldBuildName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ImportBuildfile.class, "ACS_LBL_IW_BuildFilename_A11YDesc"));

        jLabelCreatedFile.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ImportBuildfile.class, "LBL_IW_CreatedFile_LabelMnemonic").charAt(0));
        jLabelCreatedFile.setLabelFor(jTextFieldCreatedFile);
        jLabelCreatedFile.setText(NbBundle.getMessage(ImportBuildfile.class, "LBL_IW_CreatedFile_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(jLabelCreatedFile, gridBagConstraints);

        jTextFieldCreatedFile.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(jTextFieldCreatedFile, gridBagConstraints);
        jTextFieldCreatedFile.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ImportBuildfile.class, "ACS_LBL_IW_CreatedFile_A11YDesc"));

    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelBuildName;
    private javax.swing.JLabel jLabelCreatedFile;
    private javax.swing.JLabel jLabelDesc;
    private javax.swing.JTextField jTextFieldBuildName;
    private javax.swing.JTextField jTextFieldCreatedFile;
    // End of variables declaration//GEN-END:variables

    protected String getBuildName() {
        return jTextFieldBuildName.getText().trim();
    }
    
    // Implementation of DocumentListener --------------------------------------
    public void changedUpdate(DocumentEvent e) {
        updateButton();
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateButton();
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateButton();
    }
    // End if implementation of DocumentListener -------------------------------

    private void updateButton() {
        jTextFieldCreatedFile.setText(filePath + File.separator + jTextFieldBuildName.getText());

        if (jTextFieldBuildName.getText().trim().length() == 0 || jTextFieldBuildName.getText().trim().equals(GeneratedFilesHelper.BUILD_XML_PATH)) {
            ok.setEnabled(false);
        } else {
            ok.setEnabled(true);
        }
    }    
}
