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
package org.netbeans.modules.cordova.project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.cordova.CordovaPerformer;
import org.netbeans.modules.cordova.CordovaPlatform;
import org.netbeans.modules.cordova.updatetask.CordovaPlugin;
import org.netbeans.modules.cordova.wizard.CordovaProjectExtender;
import org.netbeans.modules.cordova.updatetask.SourceConfig;
import org.netbeans.modules.cordova.wizard.CordovaTemplate;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.filesystems.FileObject;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Becicka
 */
public class CordovaCustomizerPanel extends javax.swing.JPanel implements ActionListener, HelpCtx.Provider {

    private Project project;
    private SourceConfig config;
    private final Category cat;
    /**
     * Creates new form CordovaCustomizerPanel
     */
    public CordovaCustomizerPanel(Project p, Category cat) {
        this.project = p;
        this.cat = cat;
        if (!CordovaPlatform.getDefault().isReady()) {
            setLayout(new BorderLayout());
            add(new CordovaNotFound(), BorderLayout.CENTER);
            validate();
            CordovaPlatform.getDefault().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (CordovaPlatform.getDefault().isReady()) {
                        if (CordovaPlatform.isCordovaProject(project)) {
                            config = CordovaPerformer.getConfig(project);
                        }
                        removeAll();
                        initControls();
                    }
                }
            });
            
        } else {
            if (CordovaPlatform.isCordovaProject(project) && CordovaPlatform.getDefault().isReady()) {
                config = CordovaPerformer.getConfig(project);
            }
            initControls();
        }
        cat.setStoreListener(this);
    }
    

    private void createMobileConfigs() {
        try {
            CordovaProjectExtender.createMobileConfigs(project.getProjectDirectory());
            CordovaPerformer.getDefault().createPlatforms(project).waitFinished();
            config = CordovaPerformer.getConfig(project);
            setVisibility();
            CordovaTemplate.CordovaExtender.setPhoneGapBrowser(project);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        generatePanel = new javax.swing.JPanel();
        createConfigs = new javax.swing.JButton();
        createConfigsLabel = new javax.swing.JLabel();
        mobilePlatformsSetup = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        cordovaPanel = new org.netbeans.modules.cordova.project.CordovaPanel();
        pluginsPanel1 = new org.netbeans.modules.cordova.project.PluginsPanel();

        org.openide.awt.Mnemonics.setLocalizedText(createConfigs, org.openide.util.NbBundle.getMessage(CordovaCustomizerPanel.class, "CordovaPanel.createConfigs.text")); // NOI18N
        createConfigs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createConfigsActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(createConfigsLabel, org.openide.util.NbBundle.getMessage(CordovaCustomizerPanel.class, "CordovaCustomizerPanel.createConfigsLabel.text")); // NOI18N

        javax.swing.GroupLayout generatePanelLayout = new javax.swing.GroupLayout(generatePanel);
        generatePanel.setLayout(generatePanelLayout);
        generatePanelLayout.setHorizontalGroup(
            generatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generatePanelLayout.createSequentialGroup()
                .addComponent(createConfigs)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(generatePanelLayout.createSequentialGroup()
                .addComponent(createConfigsLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        generatePanelLayout.setVerticalGroup(
            generatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generatePanelLayout.createSequentialGroup()
                .addComponent(createConfigsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createConfigs)
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(mobilePlatformsSetup, org.openide.util.NbBundle.getMessage(CordovaCustomizerPanel.class, "CordovaCustomizerPanel.mobilePlatformsSetup.text")); // NOI18N
        mobilePlatformsSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mobilePlatformsSetupActionPerformed(evt);
            }
        });

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CordovaCustomizerPanel.class, "CordovaCustomizerPanel.cordovaPanel.TabConstraints.tabTitle"), cordovaPanel); // NOI18N
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CordovaCustomizerPanel.class, "CordovaCustomizerPanel.pluginsPanel1.TabConstraints.tabTitle"), pluginsPanel1); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mobilePlatformsSetup))
            .addComponent(generatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(generatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobilePlatformsSetup))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void createConfigsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createConfigsActionPerformed
        createConfigs.setVisible(false);
        createConfigsLabel.setVisible(false);
        mobilePlatformsSetup.setVisible(true);
        jTabbedPane1.setVisible(true);
        ProgressUtils.showProgressDialogAndRun(new Runnable() {

            @Override
            public void run() {
                createMobileConfigs();
            }
        }, NbBundle.getMessage(CordovaCustomizerPanel.class, "CordovaPanel.createConfigsProgress.text"));
        validate();
    }//GEN-LAST:event_createConfigsActionPerformed

    private void mobilePlatformsSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mobilePlatformsSetupActionPerformed
        OptionsDisplayer.getDefault().open("Advanced/MobilePlatforms");//NOI18N
    }//GEN-LAST:event_mobilePlatformsSetupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.cordova.project.CordovaPanel cordovaPanel;
    private javax.swing.JButton createConfigs;
    private javax.swing.JLabel createConfigsLabel;
    private javax.swing.JPanel generatePanel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton mobilePlatformsSetup;
    private org.netbeans.modules.cordova.project.PluginsPanel pluginsPanel1;
    // End of variables declaration//GEN-END:variables

    private void initControls() {
        initComponents();
        cordovaPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                checkIdValid(cordovaPanel.getPackageName());
            }
        });
        try {
            pluginsPanel1.init(getCurrent(), getAll());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        setVisibility();
    }
    
    private List<CordovaPlugin> getCurrent() throws IOException {
        List<CordovaPlugin> requestedPlugins = new ArrayList<CordovaPlugin>();
        FileObject fileObject = project.getProjectDirectory().getFileObject("nbproject/plugins.properties");

        if (fileObject == null) {
            return Collections.EMPTY_LIST;
        }
        Properties props = new Properties();
        try (InputStream inputStream = fileObject.getInputStream()) {
            props.load(inputStream);
        }
        for (String name : props.stringPropertyNames()) {
            requestedPlugins.add(new CordovaPlugin(name, props.getProperty(name)));
        }
        requestedPlugins.retainAll(getAll());
        return requestedPlugins;
    }
    private List<CordovaPlugin> getAll() throws IOException {
        List<CordovaPlugin> requestedPlugins = new ArrayList<CordovaPlugin>();

        Properties props = new Properties();
        props.load(CordovaPerformer.class.getResourceAsStream("plugins.properties"));
        for (String name : props.stringPropertyNames()) {
            requestedPlugins.add(new CordovaPlugin(name, props.getProperty(name)));
        }
        return requestedPlugins;
    }
    
    @NbBundle.Messages({
            "ERR_InvalidAppId={0} is not a valid Application ID"
        })
    private void checkIdValid(String packageName) {
        if (SourceConfig.isValidId(packageName)) {
            cat.setValid(true);
            cat.setErrorMessage("");
        } else {
            cat.setValid(false);
            cat.setErrorMessage(Bundle.ERR_InvalidAppId(packageName));
        }
    }

    @Override
    /**
     * Store listener
     */
    public void actionPerformed(ActionEvent e) {

        try {
            if (cordovaPanel == null) {
                return;
            }
            Preferences preferences = ProjectUtils.getPreferences(project, CordovaPlatform.class, true);
            preferences.put("phonegap", Boolean.toString(cordovaPanel.isPanelEnabled())); // NOI18N

            List<CordovaPlugin> selected = pluginsPanel1.getSelectedPlugins();
            
            EditableProperties props = new EditableProperties(false);
            FileObject fileObject = project.getProjectDirectory().getFileObject("nbproject/plugins.properties");

            if (fileObject != null) {
                try (InputStream inputStream = fileObject.getInputStream()) {
                    props.load(inputStream);
                }
            }


            HashSet<CordovaPlugin> pluginsToAdd = new HashSet();
            pluginsToAdd.addAll(selected);

            //plugins to install
            pluginsToAdd.removeAll(getCurrent());

            //plugins to remove
            HashSet<CordovaPlugin> pluginsToRemove = new HashSet();
            pluginsToRemove.addAll(getCurrent());
            pluginsToRemove.removeAll(selected);

            for (CordovaPlugin plugin : pluginsToAdd) {
                props.put(plugin.getId(), plugin.getUrl());
            }

            for (CordovaPlugin plugin : pluginsToRemove) {
                props.remove(plugin.getId());
            }
            
            try (OutputStream outputStream = fileObject.getOutputStream()) {
                props.store(outputStream);
            }
            try {
                cordovaPanel.save(config);
            } catch (IOException iOException) {
                Exceptions.printStackTrace(iOException);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void setVisibility() {
        boolean platformsReady = CordovaPlatform.getDefault().isReady();
        boolean isCordovaProject = CordovaPlatform.isCordovaProject(project);
        
        createConfigs.setVisible(!isCordovaProject && platformsReady);
        createConfigsLabel.setVisible(!isCordovaProject && platformsReady);
        jTabbedPane1.setVisible(isCordovaProject && platformsReady);
        mobilePlatformsSetup.setVisible(isCordovaProject && platformsReady);

        cordovaPanel.update();
        if (config!=null)
            cordovaPanel.load(config);
        validate();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.cordova.project.CordovaCustomizerPanel");
    }
}
