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
package org.netbeans.modules.profiler.j2ee.tomcat;

import java.awt.Font;
import javax.swing.ComboBoxModel;
import javax.swing.UIManager;
import org.netbeans.modules.profiler.attach.panels.components.DirectorySelector;
import org.netbeans.modules.profiler.attach.panels.components.JavaPlatformPanelComponent;
import org.netbeans.modules.profiler.attach.providers.TargetPlatform;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class TomcatIntegrationPanelUI extends javax.swing.JPanel {

    private ComboBoxModel jvmComboModel = null;
    private TomcatIntegrationPanel.Model model = null;

    /** Creates new form TomcatIntegrationPanelUI */
    public TomcatIntegrationPanelUI(TomcatIntegrationPanel.Model model) {
        this.model = model;
        initComponents();
        loadModel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        platformSelector = new org.netbeans.modules.profiler.attach.panels.components.JavaPlatformPanelComponent();
        pathsPanel = new javax.swing.JPanel();
        directoryTomcat = new org.netbeans.modules.profiler.attach.panels.components.DirectorySelector();
        labelTomcat = new javax.swing.JLabel();
        labelCatalina = new javax.swing.JLabel();
        directoryCatalina = new org.netbeans.modules.profiler.attach.panels.components.DirectorySelector();

        setMaximumSize(new java.awt.Dimension(800, 1200));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(500, 300));

        platformSelector.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(TomcatIntegrationPanelUI.class, "TomcatIntegrationPanelUI.border.platformSelector.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N
        platformSelector.setPlatformFilter(this.model.getPlatformFilter());
        platformSelector.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                platformSelectorPropertyChange(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/j2ee/tomcat/Bundle"); // NOI18N
        pathsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("InformationIntegrationLabel"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N

        org.jdesktop.layout.GroupLayout pathsPanelLayout = new org.jdesktop.layout.GroupLayout(pathsPanel);
        pathsPanel.setLayout(pathsPanelLayout);
        pathsPanelLayout.setHorizontalGroup(
            pathsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 488, Short.MAX_VALUE)
        );
        pathsPanelLayout.setVerticalGroup(
            pathsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 35, Short.MAX_VALUE)
        );

        directoryTomcat.setHint(org.openide.util.NbBundle.getMessage(TomcatIntegrationPanelUI.class, "TomcatIntegrationPanelUI.directoryTomcat.hint")); // NOI18N
        directoryTomcat.setHintForeground(new java.awt.Color(89, 79, 191));
        directoryTomcat.setPath(org.openide.util.NbBundle.getMessage(TomcatIntegrationPanelUI.class, "TomcatIntegrationPanelUI.directoryTomcat.path")); // NOI18N
        directoryTomcat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                directoryTomcatPropertyChange(evt);
            }
        });

        labelTomcat.setLabelFor(directoryTomcat);
        org.openide.awt.Mnemonics.setLocalizedText(labelTomcat, bundle.getString("TomcatIntegrationProvider_TomcatInstallLabelText")); // NOI18N

        labelCatalina.setLabelFor(directoryCatalina);
        org.openide.awt.Mnemonics.setLocalizedText(labelCatalina, bundle.getString("TomcatIntegrationProvider_TomcatBaseLabelText")); // NOI18N

        directoryCatalina.setHint(org.openide.util.NbBundle.getMessage(TomcatIntegrationPanelUI.class, "TomcatIntegrationPanelUI.directoryCatalina.hint")); // NOI18N
        directoryCatalina.setHintForeground(new java.awt.Color(89, 79, 191));
        directoryCatalina.setPath(org.openide.util.NbBundle.getMessage(TomcatIntegrationPanelUI.class, "TomcatIntegrationPanelUI.directoryCatalina.path")); // NOI18N
        directoryCatalina.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                directoryCatalinaPropertyChange(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(platformSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(labelTomcat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 488, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(directoryTomcat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 488, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(directoryCatalina, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 488, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(labelCatalina, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 488, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pathsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pathsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labelTomcat)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(directoryTomcat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labelCatalina)
                    .add(directoryCatalina, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(platformSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(24, 24, 24))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void platformSelectorPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_platformSelectorPropertyChange
        if (evt.getPropertyName().equals(JavaPlatformPanelComponent.JAVA_PLATFORM_PROPERTY)) {
            this.model.setSelectedPlatform((TargetPlatform) evt.getNewValue());
        }
    }//GEN-LAST:event_platformSelectorPropertyChange

    private void directoryCatalinaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_directoryCatalinaPropertyChange
        if (evt.getPropertyName().equals(DirectorySelector.PATH_PROPERTY)) {
            this.model.setCatalinaBase(directoryCatalina.getPath());
            directoryCatalina.setHint(this.model.getCatalinaBaseHint());
        } else if (evt.getPropertyName().equals(DirectorySelector.LAYOUT_CHANGED_PROPERTY)) {
            validate();
        }
    }//GEN-LAST:event_directoryCatalinaPropertyChange

    private void directoryTomcatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_directoryTomcatPropertyChange
        if (evt.getPropertyName().equals(DirectorySelector.PATH_PROPERTY)) {
            this.model.setTomcatInstall(directoryTomcat.getPath());
            directoryTomcat.setHint(this.model.getTomcatInstallHint());
        } else if (evt.getPropertyName().equals(DirectorySelector.LAYOUT_CHANGED_PROPERTY)) {
            validate();
        }
    }//GEN-LAST:event_directoryTomcatPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.profiler.attach.panels.components.DirectorySelector directoryCatalina;
    private org.netbeans.modules.profiler.attach.panels.components.DirectorySelector directoryTomcat;
    private javax.swing.JLabel labelCatalina;
    private javax.swing.JLabel labelTomcat;
    private javax.swing.JPanel pathsPanel;
    private org.netbeans.modules.profiler.attach.panels.components.JavaPlatformPanelComponent platformSelector;
    // End of variables declaration//GEN-END:variables
    public void refreshJvmList(final TargetPlatform preselectedPlatform) {
        platformSelector.refresh(preselectedPlatform);
        platformSelector.repaint();
    }
//  public void setModelDefaults() {
//    TargetPlatform selectedPlatform = model.getSelectedPlatform();
//    platformSelector.refresh(); // refresh selects the default JDK by default
//    platformSelector.setSelectedPlatform(selectedPlatform); // so it needs to be restored
////    this.model.setTomcatInstall(directoryTomcat.getPath());
////    this.model.setCatalinaBase(directoryCatalina.getPath());
////    this.model.setSelectedPlatform(platformSelector.getSelectedPlatform());
//  }
    public void loadModel() {
        directoryTomcat.setPath(this.model.getTomcatInstall());
        directoryCatalina.setPath(this.model.getCatalinaBase());
        platformSelector.setSelectedPlatform(this.model.getSelectedPlatform());
        updateHints();
    }

    private void updateHints() {
        directoryTomcat.setHint(this.model.getTomcatInstallHint());
        directoryCatalina.setHint(this.model.getCatalinaBaseHint());
    }
}
