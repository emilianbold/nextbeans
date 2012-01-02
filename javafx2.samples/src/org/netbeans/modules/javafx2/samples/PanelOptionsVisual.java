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

package org.netbeans.modules.javafx2.samples;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.PlatformsCustomizer;
import org.netbeans.modules.javafx2.platform.api.JavaFXPlatformUtils;
import org.netbeans.modules.javafx2.project.api.JavaFXProjectUtils;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.WeakListeners;


/**
 * @author Anton Chechel 
 */
// TODO mnemonics
public class PanelOptionsVisual extends JPanel implements TaskListener {

    private static final Logger LOGGER = Logger.getLogger("javafx"); // NOI18N

    private PanelConfigureProject panel;

    private volatile RequestProcessor.Task task;
    private DetectPlatformTask detectPlatformTask;
    
    private ComboBoxModel platformsModel;
    private ListCellRenderer platformsCellRenderer;
    private JavaPlatformChangeListener jpcl;


    /** Creates new form PanelOptionsVisual */
    public PanelOptionsVisual(PanelConfigureProject panel) {
        this.panel = panel;

        detectPlatformTask = new DetectPlatformTask();
        
        preInitComponents();
        initComponents();
        postInitComponents();
    }

    private void preInitComponents() {
        platformsModel = JavaFXProjectUtils.createPlatformComboBoxModel();
        platformsCellRenderer = JavaFXProjectUtils.createPlatformListCellRenderer();
    }
    
    private void postInitComponents() {
        // copied from CustomizerLibraries
        platformComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE); // NOI18N
        jpcl = new JavaPlatformChangeListener();
        JavaPlatformManager.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(jpcl, JavaPlatformManager.getDefault()));
        
        selectJavaFXEnabledPlatform();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setAsMainCheckBox = new javax.swing.JCheckBox();
        lblPlatform = new javax.swing.JLabel();
        platformComboBox = new javax.swing.JComboBox();
        btnManagePlatforms = new javax.swing.JButton();

        setAsMainCheckBox.setMnemonic(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_SetAsMain_CheckBoxMnemonic").charAt(0));
        setAsMainCheckBox.setSelected(true);
        setAsMainCheckBox.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_SetAsMain_CheckBox")); // NOI18N
        setAsMainCheckBox.setActionCommand("Set as Main Project");

        lblPlatform.setLabelFor(platformComboBox);
        lblPlatform.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_Platform_ComboBox")); // NOI18N

        platformComboBox.setModel(platformsModel);
        platformComboBox.setRenderer(platformsCellRenderer);
        platformComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                platformComboBoxItemStateChanged(evt);
            }
        });

        btnManagePlatforms.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_PanelOptions_Manage_Button")); // NOI18N
        btnManagePlatforms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManagePlatformsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(setAsMainCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPlatform)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(platformComboBox, 0, 315, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnManagePlatforms))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPlatform)
                    .addComponent(platformComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnManagePlatforms))
                .addGap(18, 18, 18)
                .addComponent(setAsMainCheckBox)
                .addContainerGap(198, Short.MAX_VALUE))
        );

        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NWP1_SetAsMain_A11YDesc")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void btnManagePlatformsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManagePlatformsActionPerformed
        PlatformsCustomizer.showCustomizer(getSelectedPlatform());
    }//GEN-LAST:event_btnManagePlatformsActionPerformed

    private void platformComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_platformComboBoxItemStateChanged
        this.panel.fireChangeEvent();
    }//GEN-LAST:event_platformComboBoxItemStateChanged
    
    private JavaPlatform getSelectedPlatform() {
        Object selectedItem = this.platformComboBox.getSelectedItem();
        JavaPlatform platform = (selectedItem == null ? null : JavaFXProjectUtils.getPlatform(selectedItem));
        return platform;
    }

    private void selectJavaFXEnabledPlatform() {
        for (int i = 0; i < platformsModel.getSize(); i++) {
            JavaPlatform platform = JavaFXProjectUtils.getPlatform(platformsModel.getElementAt(i));
            if (JavaFXPlatformUtils.isJavaFXEnabled(platform)) {
                platformComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    boolean valid(WizardDescriptor wizardDescriptor) {
        if (!JavaFXPlatformUtils.isJavaFXEnabled(getSelectedPlatform())) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PanelOptionsVisual.class, "WARN_PanelOptionsVisual.notFXPlatform")); // NOI18N
            return false;
        }
        return true;
    }

    void store(WizardDescriptor d) {
        d.putProperty(WizardProperties.SET_AS_MAIN, setAsMainCheckBox.isSelected() ? Boolean.TRUE : Boolean.FALSE);
        String platformName = getSelectedPlatform().getProperties().get(JavaFXPlatformUtils.PLATFORM_ANT_NAME);
        d.putProperty(JavaFXProjectUtils.PROP_JAVA_PLATFORM_NAME, platformName);
    }

    void read(WizardDescriptor d) {
        if (task == null) {
            checkPlatforms();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnManagePlatforms;
    private javax.swing.JLabel lblPlatform;
    private javax.swing.JComboBox platformComboBox;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables

    private void checkPlatforms() {
        if (!JavaFXPlatformUtils.isThereAnyJavaFXPlatform()) {
            task = RequestProcessor.getDefault().create(detectPlatformTask);
            task.addTaskListener(this);
            task.schedule(0);
        }
    }

    @Override
    public synchronized void taskFinished(Task task) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JavaPlatform platform = detectPlatformTask.getPlatform();
                if (platform != null) {
                    // reload platform combo box model
                    platformComboBox.setModel(platformsModel);

                    // select javafx platform
                    selectJavaFXEnabledPlatform();
                }
            }
        });
        this.task.removeTaskListener(this);
        this.task = null;
    }
    
    private class JavaPlatformChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            PanelOptionsVisual.this.panel.fireChangeEvent();
        }
    }
    
    private class DetectPlatformTask implements Runnable {
        private JavaPlatform platform;

        public JavaPlatform getPlatform() {
            return platform;
        }

        @Override
        public void run() {
            try {
                platform = JavaFXPlatformUtils.createDefaultJavaFXPlatform();
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Can't create Java Platform instance: {0}", ex); // NOI18N
            }
        }
    }
    
}

