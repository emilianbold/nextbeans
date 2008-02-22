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
package org.netbeans.modules.mercurial.options;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.mercurial.options.PropertiesTable;
import org.netbeans.modules.versioning.util.ListenersSupport;

/**
 *
 * @author  Peter Pis
 */
public class PropertiesPanel extends javax.swing.JPanel implements PreferenceChangeListener, TableModelListener {

    private static final Object EVENT_SETTINGS_CHANGED = new Object();
    private PropertiesTable propertiesTable;
    private ListenersSupport listenerSupport = new ListenersSupport(this);
    
    /** Creates new form PropertiesPanel */
    public PropertiesPanel() {
        initComponents();
    }
    
    public javax.swing.JTextArea getTxtAreaValue() {
        return txtAreaValue;
    }

    public javax.swing.JComboBox getComboName() {
        return comboName;
    }

    public javax.swing.JButton getBtnAdd() {
        return btnAdd;
    }

    public javax.swing.JButton getBtnRemove() {
        return btnRemove;
    }

    public void setPropertiesTable(PropertiesTable propertiesTable){
        this.propertiesTable = propertiesTable;
    }
    
    public void addNotify() {
        super.addNotify();
        HgModuleConfig.getDefault().getPreferences().addPreferenceChangeListener(this);        
        propertiesTable.getTableModel().addTableModelListener(this);
        listenerSupport.fireVersioningEvent(EVENT_SETTINGS_CHANGED);
        txtAreaValue.selectAll();
    }

    public void removeNotify() {
        propertiesTable.getTableModel().removeTableModelListener(this);
        HgModuleConfig.getDefault().getPreferences().removePreferenceChangeListener(this);
        super.removeNotify();
    }
    
    public void preferenceChange(PreferenceChangeEvent evt) {
        if (evt.getKey().startsWith(HgModuleConfig.PROP_COMMIT_EXCLUSIONS)) {
            propertiesTable.dataChanged();
            listenerSupport.fireVersioningEvent(EVENT_SETTINGS_CHANGED);
        }
    }

    public void tableChanged(TableModelEvent e) {
        listenerSupport.fireVersioningEvent(EVENT_SETTINGS_CHANGED);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        propsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelForTable = new javax.swing.JLabel();

        jLabel2.setLabelFor(comboName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel2.text")); // NOI18N

        jLabel1.setLabelFor(txtAreaValue);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout propsPanelLayout = new org.jdesktop.layout.GroupLayout(propsPanel);
        propsPanel.setLayout(propsPanelLayout);
        propsPanelLayout.setHorizontalGroup(
            propsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 427, Short.MAX_VALUE)
        );
        propsPanelLayout.setVerticalGroup(
            propsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 99, Short.MAX_VALUE)
        );

        txtAreaValue.setColumns(20);
        txtAreaValue.setRows(5);
        jScrollPane1.setViewportView(txtAreaValue);
        txtAreaValue.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "ACSD_txtAreaValue")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnRemove, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.btnRemove.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnAdd, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.btnAdd.text")); // NOI18N
        btnAdd.setMaximumSize(new java.awt.Dimension(75, 23));
        btnAdd.setMinimumSize(new java.awt.Dimension(75, 23));

        org.openide.awt.Mnemonics.setLocalizedText(labelForTable, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "jLabel3.txt")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(propsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(labelForTable)
                            .add(jLabel1))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, comboName, 0, 307, Short.MAX_VALUE)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnRemove)))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAdd, btnRemove}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(comboName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(99, 99, 99)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(btnRemove))
                        .add(26, 26, 26)
                        .add(labelForTable))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 25, Short.MAX_VALUE)
                .add(propsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        btnRemove.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "ACSD_btnRemove")); // NOI18N
        btnAdd.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "ACSD_btnAdd")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton btnAdd = new javax.swing.JButton();
    final javax.swing.JButton btnRemove = new javax.swing.JButton();
    final javax.swing.JComboBox comboName = new javax.swing.JComboBox();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel labelForTable;
    public javax.swing.JPanel propsPanel;
    final javax.swing.JTextArea txtAreaValue = new javax.swing.JTextArea();
    // End of variables declaration//GEN-END:variables
    
}
