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

package org.netbeans.modules.palette.ui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.NbBundle;

/**
 *
 * @author  S. Aubrecht
 */
class TextImporterUI extends javax.swing.JPanel {

    private String smallIconPath;
    private String largeIconPath;
    
    /** Creates new form TextImporterDlg */
    public TextImporterUI( String content, final JButton btnOk ) {
        initComponents();
        txtContent.setText(content);
        txtContent.setCaretPosition(0);
        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateButton( btnOk );
            }

            public void removeUpdate(DocumentEvent e) {
                updateButton( btnOk );
            }

            public void changedUpdate(DocumentEvent e) {
                updateButton( btnOk );
            }
        };
        updateButton(btnOk);
        txtName.getDocument().addDocumentListener(dl);
        txtTooltip.getDocument().addDocumentListener(dl);
        txtContent.getDocument().addDocumentListener(dl);
    }
    
    String getItemName() {
        return txtName.getText();
    }
    
    String getItemTooltip() {
        return txtTooltip.getText();
    }
    
    String getItemContent() {
        return txtContent.getText();
    }
    
    String getItemSmallIconPath() {
        return smallIconPath;
    }
    
    String getItemLargeIconPath() {
        return largeIconPath;
    }
    
    private void updateButton( final JButton btn ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                boolean enable = txtName.getText().length() > 0;
                enable &= txtTooltip.getText().length() > 0;
                enable &= txtContent.getText().length() > 0;
                btn.setEnabled(enable);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTooltip = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();
        lblLargeIcon = new javax.swing.JLabel();
        btnSelectLargeIcon = new javax.swing.JButton();
        lblSmallIcon = new javax.swing.JLabel();
        btnSelectSmallIcon = new javax.swing.JButton();

        jLabel1.setLabelFor(txtName);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.jLabel1.text")); // NOI18N

        txtName.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.txtName.text")); // NOI18N

        jLabel2.setLabelFor(txtTooltip);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.jLabel2.text")); // NOI18N

        txtTooltip.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.txtTooltip.text")); // NOI18N

        jLabel3.setLabelFor(lblSmallIcon);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.jLabel3.text")); // NOI18N

        jLabel4.setLabelFor(lblLargeIcon);
        jLabel4.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.jLabel4.text")); // NOI18N

        jLabel5.setLabelFor(txtContent);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.jLabel5.text")); // NOI18N

        txtContent.setColumns(20);
        txtContent.setRows(5);
        jScrollPane1.setViewportView(txtContent);
        txtContent.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.txtContent.AccessibleContext.accessibleDescription")); // NOI18N

        lblLargeIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLargeIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/palette/resources/unknown32.gif"))); // NOI18N
        lblLargeIcon.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.lblLargeIcon.text")); // NOI18N
        lblLargeIcon.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblLargeIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblLargeIcon.setIconTextGap(0);
        lblLargeIcon.setPreferredSize(new java.awt.Dimension(40, 40));

        btnSelectLargeIcon.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.btnSelectLargeIcon.text")); // NOI18N
        btnSelectLargeIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectLargeIconActionPerformed(evt);
            }
        });

        lblSmallIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSmallIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/palette/resources/unknown16.gif"))); // NOI18N
        lblSmallIcon.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.lblSmallIcon.text")); // NOI18N
        lblSmallIcon.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblSmallIcon.setFocusable(false);
        lblSmallIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblSmallIcon.setIconTextGap(0);
        lblSmallIcon.setPreferredSize(new java.awt.Dimension(40, 40));

        btnSelectSmallIcon.setText(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.btnSelectSmallIcon.text")); // NOI18N
        btnSelectSmallIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectSmallIconActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtTooltip, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblSmallIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnSelectSmallIcon)
                        .add(14, 14, 14)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblLargeIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnSelectLargeIcon))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel5))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(txtTooltip, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSmallIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(btnSelectSmallIcon)
                    .add(jLabel4)
                    .add(lblLargeIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnSelectLargeIcon))
                .add(18, 18, 18)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.txtName.AccessibleContext.accessibleName")); // NOI18N
        txtName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.txtName.AccessibleContext.accessibleDescription")); // NOI18N
        txtTooltip.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.txtTooltip.AccessibleContext.accessibleDescription")); // NOI18N
        lblLargeIcon.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.lblLargeIcon.AccessibleContext.accessibleName")); // NOI18N
        lblLargeIcon.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.lblLargeIcon.AccessibleContext.accessibleDescription")); // NOI18N
        btnSelectLargeIcon.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.btnSelectLargeIcon.AccessibleContext.accessibleDescription")); // NOI18N
        lblSmallIcon.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.lblSmallIcon.AccessibleContext.accessibleName")); // NOI18N
        lblSmallIcon.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.lblSmallIcon.AccessibleContext.accessibleDescription")); // NOI18N
        btnSelectSmallIcon.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TextImporterUI.class, "TextImporterUI.btnSelectSmallIcon.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void btnSelectLargeIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectLargeIconActionPerformed
    File iconFile = selectIconFile();
    if( null != iconFile ) {
        Icon icon = readIconFromFile( iconFile );
        if( null != icon ) {
            lblLargeIcon.setIcon(icon);
            try {
                largeIconPath = iconFile.toURL().toExternalForm();
            } catch (MalformedURLException ex) {
                //TODO log error
            }
        }
    }
}//GEN-LAST:event_btnSelectLargeIconActionPerformed

private void btnSelectSmallIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectSmallIconActionPerformed
    File iconFile = selectIconFile();
    if( null != iconFile ) {
        Icon icon = readIconFromFile( iconFile );
        if( null != icon ) {
            lblSmallIcon.setIcon(icon);
            try {
                smallIconPath = iconFile.toURL().toExternalForm();
            } catch (MalformedURLException ex) {
                //TODO log error
            }
        }
    }
}//GEN-LAST:event_btnSelectSmallIconActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelectLargeIcon;
    private javax.swing.JButton btnSelectSmallIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLargeIcon;
    private javax.swing.JLabel lblSmallIcon;
    private javax.swing.JTextArea txtContent;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtTooltip;
    // End of variables declaration//GEN-END:variables

    private static File defaultFolder;
    
    private File selectIconFile() {
        JFileChooser dlg = new JFileChooser( defaultFolder );
        dlg.setAcceptAllFileFilterUsed( true );
        dlg.setMultiSelectionEnabled( false );
        if( dlg.showOpenDialog(this) != JFileChooser.APPROVE_OPTION )
            return null;
        defaultFolder = dlg.getCurrentDirectory();
        return dlg.getSelectedFile();
    }
    
    private Icon readIconFromFile( File iconFile ) {
        try {
            Image img = ImageIO.read( iconFile.toURL() );
            if( null != img ) {
                ImageIcon res = new ImageIcon( img );
                if( res.getIconWidth() > 32 || res.getIconHeight() > 32 )  {
                    JOptionPane.showMessageDialog(this, NbBundle.getMessage(TextImporterUI.class, "Err_IconTooBig"), //NOI18N
                            NbBundle.getMessage(TextImporterUI.class, "Err_Title"), JOptionPane.ERROR_MESSAGE  ); //NOI18N
                    return null;
                }
                return res;
            }
        } catch( IOException ioE ) {
            //ignore
        }
        JOptionPane.showMessageDialog(this, 
                NbBundle.getMessage(TextImporterUI.class, "Err_CannotLoadIconFromFile", iconFile.getName()), //NOI18N
                NbBundle.getMessage(TextImporterUI.class, "Err_Title"), JOptionPane.ERROR_MESSAGE  ); //NOI18N
        return null;
    }
}
