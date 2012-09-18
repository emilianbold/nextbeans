/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cordova.ios;

import java.awt.Component;
import java.io.IOException;
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cordova.project.ClientProjectConfigurationImpl;
import org.netbeans.modules.web.clientproject.spi.platform.ProjectConfigurationCustomizer;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Becicka
 */
public class IOSConfigurationPanel extends javax.swing.JPanel {

    private ClientProjectConfigurationImpl config;
    final RequestProcessor RP = new RequestProcessor(IOSConfigurationPanel.class);
    
    private void refreshDeviceCombo(Collection sdKs) {
        final SDK[] sdks = (SDK[]) sdKs.toArray(new SDK[sdKs.size()]);
        sdkCombo.setEnabled(true);
        sdkCombo.setRenderer(new SDKRenderer());
        sdkCombo.setModel(new DefaultComboBoxModel(sdks));
        for (SDK sdk : sdks) {
            final String sdkProp = config.getProperty(ConfigConstants.sdk.name());
            if (sdk.getName().equals(sdkProp)) {
                sdkCombo.setSelectedItem(sdk);
                break;
            }
        }
        final String deviceProp = config.getProperty("vd");
        if (deviceProp !=null)
            virtualDeviceCombo.setSelectedItem(Device.valueOf(deviceProp));
    }

    public static class IOSConfigurationCustomizer implements ProjectConfigurationCustomizer {
        
        private final Project p;
        private ClientProjectConfigurationImpl config;
        
        public IOSConfigurationCustomizer(Project p, ClientProjectConfigurationImpl config) {
            this.p = p;
            this.config = config;
        }
        
        

        @Override
        public JPanel createPanel() {
            return new IOSConfigurationPanel(config);
        }
    }
    
    private static class SDKRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof SDK) {
                setText(((SDK) value).getName());
            }
            return this;
        }
    }
    
    private static class DeviceRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Device) {
                setText(((Device) value).getDisplayName());
            }
            return this;
        }
    }
    
    
    /**
     * Creates new form AndroidConfigurationCustomizer
     */
    public IOSConfigurationPanel(ClientProjectConfigurationImpl config) {
        this.config = config;
        initComponents();
        virtualDeviceCombo.setModel(new DefaultComboBoxModel(new Object[]{Device.IPHONE, Device.IPHONE_RETINA, Device.IPAD, Device.IPAD_RETINA}));
        virtualDeviceCombo.setRenderer(new DeviceRenderer());
        String device = config.getProperty("device");
        if ("device".equals(device)) {
            deviceCombo.setSelectedIndex("device".equals(device)?1:0);
        }
        setCombosVisible(!"device".equals(device));

        
    }

    private void setCombosVisible(boolean visible) {
        if (visible) {
        sdkCombo.setModel(new DefaultComboBoxModel(new Object[]{"Please Wait..."}));
        sdkCombo.setEnabled(false);
        RP.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final Collection<SDK> sdKs = IOSPlatform.getDefault().getSDKs();
                    refreshDeviceCombo(sdKs);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        }
        virtualDeviceCombo.setVisible(visible);
        virtualDeviceLabel.setVisible(visible);
        sdkCombo.setVisible(visible);
        sdkLabel.setVisible(visible);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sdkLabel = new javax.swing.JLabel();
        sdkCombo = new javax.swing.JComboBox();
        virtualDeviceLabel = new javax.swing.JLabel();
        virtualDeviceCombo = new javax.swing.JComboBox();
        deviceLabel = new javax.swing.JLabel();
        deviceCombo = new javax.swing.JComboBox();
        debuggerCheckBox = new javax.swing.JCheckBox();

        sdkLabel.setLabelFor(sdkCombo);
        org.openide.awt.Mnemonics.setLocalizedText(sdkLabel, org.openide.util.NbBundle.getMessage(IOSConfigurationPanel.class, "IOSConfigurationPanel.sdkLabel.text")); // NOI18N

        sdkCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Please Wait..." }));
        sdkCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdkComboActionPerformed(evt);
            }
        });

        virtualDeviceLabel.setLabelFor(virtualDeviceCombo);
        org.openide.awt.Mnemonics.setLocalizedText(virtualDeviceLabel, org.openide.util.NbBundle.getMessage(IOSConfigurationPanel.class, "IOSConfigurationPanel.virtualDeviceLabel.text")); // NOI18N

        virtualDeviceCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                virtualDeviceComboActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deviceLabel, org.openide.util.NbBundle.getMessage(IOSConfigurationPanel.class, "IOSConfigurationPanel.deviceLabel.text")); // NOI18N

        deviceCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Simulator", "Connected Device" }));
        deviceCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceComboActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(debuggerCheckBox, org.openide.util.NbBundle.getMessage(IOSConfigurationPanel.class, "IOSConfigurationPanel.debuggerCheckBox.text")); // NOI18N
        debuggerCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debuggerCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(virtualDeviceLabel)
                    .add(sdkLabel)
                    .add(deviceLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(virtualDeviceCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(sdkCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(deviceCombo, 0, 360, Short.MAX_VALUE))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(debuggerCheckBox)
                .add(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deviceLabel)
                    .add(deviceCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sdkLabel)
                    .add(sdkCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(virtualDeviceLabel)
                    .add(virtualDeviceCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(debuggerCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sdkComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdkComboActionPerformed
        config.putProperty(ConfigConstants.sdk.name(), ((SDK)sdkCombo.getSelectedItem()).getName());
    }//GEN-LAST:event_sdkComboActionPerformed

    private void virtualDeviceComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_virtualDeviceComboActionPerformed
        final Device val = (Device)virtualDeviceCombo.getSelectedItem();
        if (val != null)
            config.putProperty("vd", val.name());
    }//GEN-LAST:event_virtualDeviceComboActionPerformed

    private void deviceComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceComboActionPerformed
        if (deviceCombo.getSelectedIndex() == 0) {
            config.putProperty("device", "emulator");
            setCombosVisible(true);
        } else {
            config.putProperty("device", "device");
            setCombosVisible(false);
        }
    }//GEN-LAST:event_deviceComboActionPerformed

    private void debuggerCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debuggerCheckBoxActionPerformed
        config.putProperty("debug.enabled", Boolean.toString(debuggerCheckBox.isSelected()));
    }//GEN-LAST:event_debuggerCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox debuggerCheckBox;
    private javax.swing.JComboBox deviceCombo;
    private javax.swing.JLabel deviceLabel;
    private javax.swing.JComboBox sdkCombo;
    private javax.swing.JLabel sdkLabel;
    private javax.swing.JComboBox virtualDeviceCombo;
    private javax.swing.JLabel virtualDeviceLabel;
    // End of variables declaration//GEN-END:variables
}