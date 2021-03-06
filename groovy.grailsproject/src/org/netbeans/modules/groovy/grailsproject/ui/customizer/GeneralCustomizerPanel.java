/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.groovy.grailsproject.ui.customizer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author  schmidtm
 */
public class GeneralCustomizerPanel extends javax.swing.JPanel implements HelpCtx.Provider, DocumentListener {

    private final GrailsProjectProperties uiProperties;

    /** Creates new form GeneralCustomizerPanel */
    public GeneralCustomizerPanel(GrailsProjectProperties uiProperties) {
        this.uiProperties = uiProperties;

        initComponents();

        projectFolderTextField.setText(FileUtil.getFileDisplayName(uiProperties.getProject().getProjectDirectory()));

        // populating the port field

        grailsServerPort.getDocument().addDocumentListener(this);
        grailsServerPort.setText(uiProperties.getPort());

        vmOptionsTextField.getDocument().addDocumentListener(this);
        vmOptionsTextField.setText(uiProperties.getVmOptions());

        // Here we define the indexes for the default enviroments as this:
        // 0 : "Development",
        // 1 : "Production",
        // 2 : "Test"

        grailsEnvChooser.setModel(uiProperties.getEnvironmentModel());
        grailsDisplayBrowser.setModel(uiProperties.getDisplayBrowserModel());
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(GeneralCustomizerPanel.class);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        activeGrailsEnvironmentLabel = new javax.swing.JLabel();
        grailsEnvChooser = new javax.swing.JComboBox();
        projectFolderLabel = new javax.swing.JLabel();
        projectFolderTextField = new javax.swing.JTextField();
        grailsServerPortLabel = new javax.swing.JLabel();
        grailsServerPort = new javax.swing.JTextField();
        grailsDisplayBrowser = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        vmOptionsLabel = new javax.swing.JLabel();
        vmOptionsTextField = new javax.swing.JTextField();
        vmOptionsTipLabel = new javax.swing.JLabel();

        activeGrailsEnvironmentLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.activeGrailsEnvironmentLabel.text")); // NOI18N

        grailsEnvChooser.setMaximumRowCount(3);

        projectFolderLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.projectFolderLabel.text")); // NOI18N

        projectFolderTextField.setEditable(false);
        projectFolderTextField.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.projectFolderTextField.text")); // NOI18N

        grailsServerPortLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.grailsServerPortLabel.text")); // NOI18N

        grailsServerPort.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.grailsServerPort.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(grailsDisplayBrowser, NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.grailsDisplayBrowser.text")); // NOI18N
        grailsDisplayBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grailsDisplayBrowserActionPerformed(evt);
            }
        });

        vmOptionsLabel.setLabelFor(vmOptionsTextField);
        org.openide.awt.Mnemonics.setLocalizedText(vmOptionsLabel, org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.vmOptionsLabel.text")); // NOI18N

        vmOptionsTipLabel.setText(org.openide.util.NbBundle.getMessage(GeneralCustomizerPanel.class, "GeneralCustomizerPanel.vmOptionsTipLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(activeGrailsEnvironmentLabel)
                    .addComponent(projectFolderLabel)
                    .addComponent(grailsServerPortLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grailsServerPort, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addComponent(projectFolderTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addComponent(grailsEnvChooser, javax.swing.GroupLayout.Alignment.TRAILING, 0, 431, Short.MAX_VALUE)))
            .addComponent(grailsDisplayBrowser, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vmOptionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vmOptionsTipLabel)
                        .addContainerGap())
                    .addComponent(vmOptionsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectFolderLabel)
                    .addComponent(projectFolderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(activeGrailsEnvironmentLabel)
                    .addComponent(grailsEnvChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grailsServerPortLabel)
                    .addComponent(grailsServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(grailsDisplayBrowser)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vmOptionsLabel)
                    .addComponent(vmOptionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vmOptionsTipLabel))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(112, 112, 112)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(41, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void grailsDisplayBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grailsDisplayBrowserActionPerformed

}//GEN-LAST:event_grailsDisplayBrowserActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activeGrailsEnvironmentLabel;
    private javax.swing.JCheckBox grailsDisplayBrowser;
    private javax.swing.JComboBox grailsEnvChooser;
    private javax.swing.JTextField grailsServerPort;
    private javax.swing.JLabel grailsServerPortLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel projectFolderLabel;
    private javax.swing.JTextField projectFolderTextField;
    private javax.swing.JLabel vmOptionsLabel;
    private javax.swing.JTextField vmOptionsTextField;
    private javax.swing.JLabel vmOptionsTipLabel;
    // End of variables declaration//GEN-END:variables

    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    private void updateTexts( DocumentEvent e ) {
        Document doc = e.getDocument();

        // FIXME proper document model would be better
        if (doc == grailsServerPort.getDocument()) {
            uiProperties.setPort(grailsServerPort.getText().trim());
        } else if (doc == vmOptionsTextField.getDocument()) {
            uiProperties.setVmOptions(vmOptionsTextField.getText().trim());
        }
    }

}
