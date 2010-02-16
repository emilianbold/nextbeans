/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

/*
 * RESTResourcesPanel.java
 *
 * Created on 02-Feb-2010, 10:21:21
 */

package org.netbeans.modules.websvc.rest.client;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.websvc.rest.model.api.RestServiceDescription;
import org.netbeans.modules.websvc.saas.model.WadlSaas;
import org.netbeans.modules.websvc.saas.model.WadlSaasResource;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author mkuchtiak
 */
public class RESTResourcesPanel extends javax.swing.JPanel {

    private Node resourceNode;
    private DialogDescriptor descriptor;
    private boolean nameChangedByUser = false;

    /** Creates new form RESTResourcesPanel */
    public RESTResourcesPanel() {
        initComponents();
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                resourceChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resourceChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                resourceChanged();
            }
        });
        jTextField2.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                nameChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                nameChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                nameChanged();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RESTResourcesPanel.class, "RESTResourcesPanel.jLabel1.text")); // NOI18N

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton1, org.openide.util.NbBundle.getMessage(RESTResourcesPanel.class, "RESTResourcesPanel.jRadioButton1.text")); // NOI18N

        buttonGroup1.add(jRadioButton2);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton2, org.openide.util.NbBundle.getMessage(RESTResourcesPanel.class, "RESTResourcesPanel.jRadioButton2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(RESTResourcesPanel.class, "RESTResourcesPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(RESTResourcesPanel.class, "RESTResourcesPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(RESTResourcesPanel.class, "RESTResourcesPanel.jLabel3.text")); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(4, 4, 4)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    void setDescriptor(DialogDescriptor descriptor) {
        this.descriptor = descriptor;
        descriptor.setValid(false);
    }

    private void resourceChanged() {
        if (!nameChangedByUser || jTextField2.getText().trim().length() == 0) {
            if (resourceNode != null) {
                WadlSaasResource saasResource = resourceNode.getLookup().lookup(WadlSaasResource.class);
                if (saasResource != null) {
                    jTextField2.setText(ClientJavaSourceHelper.getClientClassName(saasResource));
                } else {
                    RestServiceDescription restServiceDesc = resourceNode.getLookup().lookup(RestServiceDescription.class);
                    if (restServiceDesc != null) {
                        jTextField2.setText(restServiceDesc.getName()+"_JerseyClient"); //NOI18N
                    }
                }
            }
        }
    }
    private void nameChanged() {
        if (jTextField2.getText().trim().length() == 0) {
            descriptor.setValid(false);
        } else if (resourceNode == null) {
            descriptor.setValid(false);
        } else {
            descriptor.setValid(true);
        }
        nameChangedByUser = true;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jRadioButton1.isSelected()) {
            RESTExplorerPanel explorerPanel = new RESTExplorerPanel();
            DialogDescriptor desc = new DialogDescriptor(explorerPanel,
                    NbBundle.getMessage(RESTResourcesPanel.class,"TTL_RESTResources")); //NOI18N
            explorerPanel.setDescriptor(desc);
            if (DialogDisplayer.getDefault().notify(desc).equals(NotifyDescriptor.OK_OPTION)) {
                resourceNode = explorerPanel.getSelectedService();
                boolean isChangedByUser = nameChangedByUser;
                jTextField1.setText(resourceNode.getDisplayName());
                if (!isChangedByUser) {
                    nameChangedByUser = false;
                }
            }
            if (resourceNode != null) {
                descriptor.setValid(true);
            }
        } else {
            SaasExplorerPanel explorerPanel = new SaasExplorerPanel();
            DialogDescriptor desc = new DialogDescriptor(explorerPanel,
                    NbBundle.getMessage(RESTResourcesPanel.class,"TTL_RESTResources")); //NOI18N
            explorerPanel.setDescriptor(desc);
            if (DialogDisplayer.getDefault().notify(desc).equals(NotifyDescriptor.OK_OPTION)) {
                resourceNode = explorerPanel.getSelectedService();
                boolean isChangedByUser = nameChangedByUser;
                jTextField1.setText(getSaasResourceName(resourceNode));
                if (!isChangedByUser) {
                    nameChangedByUser = false;
                }
            }
            if (resourceNode != null) {
                descriptor.setValid(true);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private String getSaasResourceName(Node node) {
        WadlSaasResource saasResource = node.getLookup().lookup(WadlSaasResource.class);

        String resourceName = saasResource.getResource().getPath();
        if (resourceName.startsWith("/")) { //NOI18N
            resourceName = resourceName.substring(1);
        }

        Node saasNode = node.getParentNode();
        while (saasNode != null && saasNode.getLookup().lookup(WadlSaas.class) == null) {
            saasResource = saasNode.getLookup().lookup(WadlSaasResource.class);
            if (saasResource != null) {
                String path = saasResource.getResource().getPath();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length()-1);
                }
                if (path.length() > 0) {
                    resourceName = path+"/"+resourceName;
                }
            } else {
                resourceName = saasNode.getDisplayName()+"/"+resourceName;
            }
            saasNode = saasNode.getParentNode();
        }
        if (saasNode != null) {
            resourceName = saasNode.getDisplayName()+" ["+resourceName+"]"; //NOI18N
        }
        return resourceName;
    }

    public Node getResourceNode() {
        return resourceNode;
    }

    public String getClassName() {
        return jTextField2.getText().trim();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

}
