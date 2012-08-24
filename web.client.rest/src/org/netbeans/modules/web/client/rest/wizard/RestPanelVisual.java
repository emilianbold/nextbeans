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
package org.netbeans.modules.web.client.rest.wizard;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import org.netbeans.api.project.Project;
import org.netbeans.modules.websvc.rest.client.RESTExplorerPanel;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;

/**
 *
 * @author ads
 */
public final class RestPanelVisual extends JPanel  {
    
    private static final String UNDERSCORE = "underscore";          // NOI18N
    private static final String UNDERSCORE_JS_ = UNDERSCORE+".js-"; // NOI18N
    private static final String JQUERY_JS = "jquery";               // NOI18N
    private static final String JQUERY_JS_ = JQUERY_JS+'-';        
    
    private static final String BACKBONE = "backbone";              // NOI18N
    private static final String BACKBONE_JS_ = BACKBONE+".js-";     // NOI18N
    
    private static String REST_CLIENT = "RestClient";               // NOI18N
    private static String JS          = ".js";                      // NOI18N

    public RestPanelVisual(RestPanel panel) {
        myPanel = panel;
        initComponents();
        String jsName = suggestJsName(panel.getDescriptor());
        Templates.setTargetName(panel.getDescriptor(), jsName);
        
        ui.setModel( new DefaultComboBoxModel( RestPanel.JsUi.values()));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        restProjectResource = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        backboneCheckBox = new javax.swing.JCheckBox();
        projectLbl = new javax.swing.JLabel();
        uiLbl = new javax.swing.JLabel();
        ui = new javax.swing.JComboBox();

        restProjectResource.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "LBL_BrowseProject")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(backboneCheckBox, org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "LBL_Backbone")); // NOI18N

        projectLbl.setLabelFor(restProjectResource);
        org.openide.awt.Mnemonics.setLocalizedText(projectLbl, org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "LBL_Project")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(uiLbl, org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "LBL_GeneratedUI")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(projectLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 119, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(uiLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(restProjectResource, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .addComponent(ui, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backboneCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restProjectResource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton)
                    .addComponent(projectLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(backboneCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        restProjectResource.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSN_ProjectPath")); // NOI18N
        restProjectResource.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSD_ProjectPath")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleName("Browse");
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(RestPanelVisual.class).getString("A11Y_BrowseWSDLProject")); // NOI18N
        backboneCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSN_Backbone")); // NOI18N
        backboneCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSD_Backbone")); // NOI18N
        projectLbl.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSN_Project")); // NOI18N
        projectLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSD_Project")); // NOI18N
        uiLbl.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSN_GeneratedUI")); // NOI18N
        uiLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "ACSD_GeneratedUI")); // NOI18N

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RestPanelVisual.class, "LBL_RestSource")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        browseProjectServices();
        myPanel.fireChangeEvent();
        
    }//GEN-LAST:event_browseButtonActionPerformed
    
    private void browseProjectServices() {
        RESTExplorerPanel panel = new RESTExplorerPanel();
        DialogDescriptor descriptor = new DialogDescriptor(panel,
                NbBundle.getMessage(RestPanelVisual.class,"TTL_RESTResources")); //NOI18N
        panel.setDescriptor(descriptor);
        if (DialogDisplayer.getDefault().notify(descriptor).equals(NotifyDescriptor.OK_OPTION)) {
            myRestNode = panel.getSelectedService();
            restProjectResource.setText(myRestNode.getDisplayName());
        }
    }
                    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox backboneCheckBox;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel projectLbl;
    private javax.swing.JTextField restProjectResource;
    private javax.swing.JComboBox ui;
    private javax.swing.JLabel uiLbl;
    // End of variables declaration//GEN-END:variables
    
    void store(WizardDescriptor descriptor) {
        descriptor.putProperty(RestPanel.ADD_BACKBONE, backboneCheckBox.isSelected());
        descriptor.putProperty(RestPanel.EXISTED_BACKBONE, myBackbone);
        descriptor.putProperty(RestPanel.EXISTED_UNDERSCORE, myUnderscore);
        descriptor.putProperty(RestPanel.EXISTED_JQUERY, myJQuery);
        descriptor.putProperty(RestPanel.UI, ui.getSelectedItem());
    }
    
    void read(WizardDescriptor wizardDescriptor) {
        myBackbone = null;
        Project project = Templates.getProject(wizardDescriptor);
        FileObject projectDirectory = project.getProjectDirectory();
        FileObject libs = projectDirectory.getFileObject("js/libs");        // NOI18N
        boolean backboneExists = false; 
        if ( libs != null ){
            FileObject[] children = libs.getChildren();
            for (FileObject child : children) {
                String name = child.getName();
                if (child.isFolder()) {
                    if (name.startsWith(BACKBONE_JS_)) {
                        backboneExists = true;
                        myBackbone = getFile(child, BACKBONE);
                    }
                    else if (name.startsWith(UNDERSCORE_JS_)) {
                        myUnderscore = getFile(child, UNDERSCORE);
                    }
                    else if (name.startsWith(JQUERY_JS_)) {
                        myJQuery = getFile(child, JQUERY_JS);
                    }
                }
                else {
                    if (name.startsWith(BACKBONE)) {
                        backboneExists = true;
                        myBackbone = child;
                    }
                    else if (name.startsWith(UNDERSCORE)) {
                        myUnderscore = child;
                    }
                    else if (name.startsWith(JQUERY_JS)) {
                        myJQuery = child;
                    }
                }
            }
        }
        if ( backboneExists ){
            Mutex.EVENT.readAccess( new Runnable() {
                @Override
                public void run() {
                    backboneCheckBox.setVisible(false);                    
                }
            });
        }
        Object fileName = wizardDescriptor.getProperty(RestPanel.FILE_NAME);
        String jsName=null;
        if ( fileName == null ){
            jsName = suggestJsName(wizardDescriptor);
        }
        else {
            jsName = fileName.toString();
        }
        
        Templates.setTargetName(wizardDescriptor, jsName);
    }
    
    private FileObject getFile(FileObject folder , String prefix){
        FileObject[] children = folder.getChildren();
        for (FileObject child : children) {
            String name = child.getName();
            if ( name.startsWith(prefix)){
                return child;
            }
        }
        return null;
    }

    private String suggestJsName(  WizardDescriptor descriptor ) {
        FileObject targetFolder = Templates.getTargetFolder(descriptor);
        
        String suggestName = REST_CLIENT;          
        FileObject restClient = null;
        int count =0;
        String result = null;
        while( true ){
            restClient = targetFolder.getFileObject(suggestName+JS);
            if ( restClient == null){
                result = suggestName;
                break;
            }
            else {
                count++;
                suggestName = REST_CLIENT+count;
            }
        }
        return result;
    }

    boolean valid(final WizardDescriptor wizardDescriptor) {
        if ( wizardDescriptor == null ){
            return true;
        }
        wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);    
        if ( getRestNode() == null ){
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, 
                    NbBundle.getMessage(RestPanelVisual.class, "ERR_NoRestResource"));    // NOI18N
            return false;
        }
        String targetName = Templates.getTargetName(wizardDescriptor);
        FileObject targetFolder = Templates.getTargetFolder(wizardDescriptor);
        if ( targetFolder.getFileObject(targetName)!=null){
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, 
                    NbBundle.getMessage(RestPanelVisual.class, "ERR_ExistedFile",targetName));    // NOI18N
            return false;
        }
        return true;
    }
    
    Node getRestNode(){
        return myRestNode;
    }

    private RestPanel myPanel;
    private Node myRestNode;
    private FileObject myBackbone;
    private FileObject myUnderscore;
    private FileObject myJQuery;
}
