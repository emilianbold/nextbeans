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

package org.netbeans.modules.websvc.api.support;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Milan Kuchtiak
 */
public class RefreshClientDialog extends javax.swing.JPanel {
    
    private String url;
    private boolean downloadWsdl;
    
    private RefreshClientDialog(boolean downloadWsdl, String url) {
        this.downloadWsdl=downloadWsdl;
        this.url=url;
        initComponents();
        // display the delete_wsdl checkbox only if wsdl exists
        if (!downloadWsdl) {
            downloadWsdlCheckBox.setVisible(false);
            jLabel1.setVisible(false);
            jTextField1.setVisible(false);
        } else {
            jTextField1.setText(url);
            downloadWsdlCheckBox.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (((javax.swing.JCheckBox)e.getSource()).isSelected())
                        jTextField1.setEditable(true);
                    else 
                        jTextField1.setEditable(false);
                }

            });            
        }
    }
 
    public static RefreshClientDialog.Result open(boolean downloadWsdl, String url) {
        String title = NbBundle.getMessage(RefreshClientDialog.class, "MSG_ConfirmClientRefresh");
        RefreshClientDialog refreshDialog = new RefreshClientDialog(downloadWsdl, url);
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(refreshDialog, title, NotifyDescriptor.YES_NO_OPTION);
        Object result = DialogDisplayer.getDefault().notify(desc);
        if (result.equals(NotifyDescriptor.CLOSED_OPTION) || result.equals(NotifyDescriptor.NO_OPTION)) {
            return Result.CLOSE;
        } else if (refreshDialog.downloadWsdl()) {
            Result res = Result.DOWNLOAD_AND_REFRESH;
            res.setWsdlUrl(refreshDialog.getWsdlUrl());
            return res;
        } else return Result.REFRESH_ONLY;
        
    }
    
    
    private boolean downloadWsdl() {
        if (!downloadWsdl) return false;
        return downloadWsdlCheckBox.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        downloadWsdlCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(downloadWsdlCheckBox, org.openide.util.NbBundle.getMessage(RefreshClientDialog.class, "MSG_DownloadWsdl", new Object[] {url})); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(downloadWsdlCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RefreshClientDialog.class, "HINT_DownloadWsdl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 22, 0, 0);
        add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(RefreshClientDialog.class, "HINT_RefreshClient")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        add(jLabel3, gridBagConstraints);

        jTextField1.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 22, 0, 22);
        add(jTextField1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox downloadWsdlCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    
    private String getWsdlUrl() {
        return jTextField1.getText().trim();
    }
    
    public static enum Result {
        CLOSE,
        DOWNLOAD_AND_REFRESH,
        REFRESH_ONLY;
        
        private String wsdlUrl;
        
        public void setWsdlUrl(String wsdlUrl) {
            this.wsdlUrl = wsdlUrl;
        }
        
        public String getWsdlUrl() {
            return wsdlUrl;
        }
    }
}
