/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.ui;

import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript2.editor.JsPreferences;
import org.netbeans.modules.javascript2.editor.JsVersion;
import org.netbeans.modules.javascript2.json.spi.support.JsonPreferences;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class ECMAScriptPanel extends javax.swing.JPanel implements HelpCtx.Provider {

    public static final String IDENTIFIER = "ecma"; // NOI18N
    
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final Project project;
    private final ProjectCustomizer.Category category;
    /**
     * Creates new form OJETPanel
     */
    public ECMAScriptPanel(Project project, ProjectCustomizer.Category category) {
        this.project = project;
        this.category = category;
        initComponents();
        initData();
    }

    private void initData() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (JsVersion version: JsPreferences.getECMAScriptAvailableVersions()) {
            model.addElement(new DisplayVersion(version));
        }
        model.setSelectedItem(new DisplayVersion(JsPreferences.getECMAScriptVersion(project)));
        cbVersion.setModel(model);
        allowJsonComments.setSelected(JsonPreferences.forProject(project).isCommentSupported());
        category.setStoreListener((e) -> save());
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbVersion = new javax.swing.JComboBox();
        lVersion = new javax.swing.JLabel();
        allowJsonComments = new javax.swing.JCheckBox();

        cbVersion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbVersionItemStateChanged(evt);
            }
        });

        lVersion.setLabelFor(cbVersion);
        org.openide.awt.Mnemonics.setLocalizedText(lVersion, org.openide.util.NbBundle.getMessage(ECMAScriptPanel.class, "ECMAScriptPanel.lVersion.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(allowJsonComments, org.openide.util.NbBundle.getMessage(ECMAScriptPanel.class, "ECMAScriptPanel.allowJsonComments.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lVersion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbVersion, 0, 265, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(allowJsonComments)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allowJsonComments)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        allowJsonComments.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ECMAScriptPanel.class, "ECMAScriptPanel.allowJsonComments.ad")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void cbVersionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbVersionItemStateChanged
        changeSupport.fireChange();
    }//GEN-LAST:event_cbVersionItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowJsonComments;
    private javax.swing.JComboBox cbVersion;
    private javax.swing.JLabel lVersion;
    // End of variables declaration//GEN-END:variables

    public String getIdentifier() {
        return IDENTIFIER;
    }

    @NbBundle.Messages("ECMAScriptPanel.name=ECMAScript")
    public String getDisplayName() {
        return Bundle.ECMAScriptPanel_name();
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }
    
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public void save() {
        JsPreferences.putECMAScriptVersion(project, ((DisplayVersion) cbVersion.getSelectedItem()).getVersion());
        JsonPreferences.forProject(project).setCommentSupported(allowJsonComments.isSelected());
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.javascript2.editor.ui.ECMAScriptPanel"); // NOI18N
    }

    private static class DisplayVersion {

        private final JsVersion version;

        public DisplayVersion(JsVersion version) {
            this.version = version;
        }

        public JsVersion getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return version.getDisplayName();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 41 * hash + Objects.hashCode(this.version);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DisplayVersion other = (DisplayVersion) obj;
            if (this.version != other.version) {
                return false;
            }
            return true;
        }
    }
}
