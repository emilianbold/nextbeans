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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.projectopener;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.netbeans.projectopener.Utils.DialogDescriptor;
import org.netbeans.projectopener.Utils.NBInstallDir;

/**
 *
 * @author  Milan Kubec
 */
public class BrowseNetBeansDialog extends javax.swing.JDialog {
    
    private DialogDescriptor dialogDesc;
    private NBInstallDir nbDir;
    private String nbVersion;
    
    public BrowseNetBeansDialog(DialogDescriptor dd, NBInstallDir dir, String nbVer) {
        super((JFrame) null, true);
        dialogDesc = dd;
        nbDir = dir;
        nbVersion = nbVer;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            //
        }
        initComponents();
        getRootPane().setDefaultButton(contButton);
        setLocation(getCenterPoint());
    }
    
    private String getMessage() {
        return "<html>Required version " + nbVersion + " of NetBeans IDE was not found on your computer.<br>" +
                "Do you want to browse required version yourself?</html>";
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dirTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        exitButton = new javax.swing.JButton();
        contButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        linkLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/projectopener/Bundle"); // NOI18N
        setTitle(bundle.getString("BrowseNBDialogTitle")); // NOI18N
        setMinimumSize(new java.awt.Dimension(450, 23));

        jPanel1.setMinimumSize(new java.awt.Dimension(250, 23));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(getMessage());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 12, 8);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText(bundle.getString("InstallDirLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        dirTextField.setMinimumSize(new java.awt.Dimension(250, 20));
        dirTextField.setPreferredSize(new java.awt.Dimension(300, 20));
        dirTextField.getDocument().addDocumentListener(new DocListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel1.add(dirTextField, gridBagConstraints);

        browseButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/projectopener/Bundle").getString("BrowseButtonLabel").charAt(0));
        browseButton.setText(bundle.getString("BrowseButtonLabel")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 1, 8);
        jPanel1.add(browseButton, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        exitButton.setText(bundle.getString("ExitButtonLabel")); // NOI18N
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        jPanel2.add(exitButton, gridBagConstraints);

        contButton.setText(bundle.getString("ContinueButtonLabel")); // NOI18N
        contButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        jPanel2.add(contButton, gridBagConstraints);
        contButton.setEnabled(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("NetBeans IDE Installer can be downloaded from");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel3.add(jLabel3, gridBagConstraints);

        linkLabel.setText("<html><a href=\"#anchor\">NetBeans Download Site</a></html>");
        linkLabel.setToolTipText(bundle.getString("URL_Download_NB")); // NOI18N
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                linkLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
        jPanel3.add(linkLabel, gridBagConstraints);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 4, 8);
        jPanel1.add(jPanel3, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void linkLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkLabelMouseClicked
    dialogDesc.setValue(DialogDescriptor.DOWNLOAD);
    setVisible(false);
}//GEN-LAST:event_linkLabelMouseClicked

private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
    dialogDesc.setValue(DialogDescriptor.EXIT);
    setVisible(false);
}//GEN-LAST:event_exitButtonActionPerformed

private void contButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contButtonActionPerformed
    dialogDesc.setValue(DialogDescriptor.CONTINUE);
    nbDir.setInstallDir(new File(dirTextField.getText()));
    setVisible(false);
}//GEN-LAST:event_contButtonActionPerformed

private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
    chooser.setMultiSelectionEnabled(false);
    // chooser.setSelectedFile(new File(""));
    chooser.setDialogTitle("Browse NetBeans installation");
    if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
        File file = chooser.getSelectedFile();
        // XXX do validation of entered userdir ???
        dirTextField.setText(file.getAbsolutePath());
    }
}//GEN-LAST:event_browseButtonActionPerformed
    
    // XXX this method should be moved to Utils
    private Point getCenterPoint() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth() - getInsets().left - getInsets().right) / 2;
        int y = (screenSize.height - getHeight() - getInsets().top - getInsets().bottom) / 2;
        return new Point(x, y);
    }
    
    private class DocListener implements DocumentListener {
        public void insertUpdate(DocumentEvent de) {
            update(de);
        }
        public void removeUpdate(DocumentEvent de) {
            update(de);
        }
        public void changedUpdate(DocumentEvent de) {
            update(de);
        }
        private void update(DocumentEvent de) {
            String text = null;
            try {
                text = de.getDocument().getText(0, de.getDocument().getLength());
            } catch (BadLocationException ex) {
                //
            }
            File f = new File(text);
            if (f.exists() && f.isDirectory()) {
                contButton.setEnabled(true);
            } else {
                contButton.setEnabled(false);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton contButton;
    private javax.swing.JTextField dirTextField;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel linkLabel;
    // End of variables declaration//GEN-END:variables
    
}
