/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

/*
 * JFXApplicationParametersPanel.java
 *
 * Created on 22.8.2011, 13:53:57
 */
package org.netbeans.modules.javafx2.project.ui;

import java.awt.FontMetrics;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.javafx2.project.JFXProjectProperties.PropertiesTableModel;
import org.netbeans.modules.javafx2.project.JFXProjectProperties.PropertyCellEditor;
import org.netbeans.modules.javafx2.project.JFXProjectProperties.PropertyCellRenderer;
import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Somol
 */
public class JFXApplicationMultiPropertyPanel extends javax.swing.JPanel implements TableModelListener {

    private PropertiesTableModel tableModel;
    private PropertyCellEditor cellEditor;
    private DialogDescriptor desc;
    
    /** Creates new form JFXApplicationParametersPanel */
    public JFXApplicationMultiPropertyPanel(@NonNull PropertiesTableModel mdl) {
        this.tableModel = mdl;
        initComponents();
        cellEditor = new PropertyCellEditor();
        tableMultiProperties.setDefaultRenderer(Object.class, new PropertyCellRenderer());
        tableMultiProperties.setDefaultEditor(Object.class, cellEditor);
        tableMultiProperties.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        updateRemoveButton();
        updateDefaultButtonLabel();
        updateDefaultButtonState();
        FontMetrics fm = tableMultiProperties.getFontMetrics(tableMultiProperties.getFont());
        tableMultiProperties.setRowHeight(fm.getHeight() + 5);
    }
    
    public void setTableTitle(String label) {
        if(label == null) {
            labelTable.setText(""); // NOI18N
            labelTable.setVisible(false);
        } else {
            labelTable.setText(label);
        }
    }
    
    public void setRemark(String remark) {
        if(remark == null) {
            labelRemark.setText(""); //NOI18N
            labelRemark.setVisible(false);
        } else {
            labelRemark.setText(remark);
        }
    }

    void setDialogDescriptor(DialogDescriptor desc) {
        this.desc = desc;
        updateDialogButtons();
        updateAddButton();
    }

    void registerListeners() {
        tableModel.addTableModelListener(this);
        tableMultiProperties.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    updateRemoveButton();
                }
            }
        });
        cellEditor.registerCellEditorListener();
    }
    
    void unregisterListeners() {
        tableModel.removeTableModelListener(this);
        cellEditor.unregisterCellEditorListener();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelTable = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableMultiProperties = new javax.swing.JTable();
        buttonAdd = new javax.swing.JButton();
        buttonRemove = new javax.swing.JButton();
        labelRemark = new javax.swing.JLabel();
        buttonDefault = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(400, 250));
        setLayout(new java.awt.GridBagLayout());

        labelTable.setLabelFor(tableMultiProperties);
        org.openide.awt.Mnemonics.setLocalizedText(labelTable, org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "LBL_JFXApplicationParametersPanel.labelParams.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 0);
        add(labelTable, gridBagConstraints);
        labelTable.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AN_JFXApplicationParametersPanel.labelParams.text")); // NOI18N
        labelTable.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AD_JFXApplicationParametersPanel.labelParams.text")); // NOI18N

        tableMultiProperties.setModel(tableModel);
        jScrollPane1.setViewportView(tableMultiProperties);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(buttonAdd, org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "LBL_JFXApplicationParametersPanel.buttonAdd.text")); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        add(buttonAdd, gridBagConstraints);
        buttonAdd.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AN_JFXApplicationParametersPanel.buttonAdd.text")); // NOI18N
        buttonAdd.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AD_JFXApplicationParametersPanel.buttonAdd.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(buttonRemove, org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "LBL_JFXApplicationParametersPanel.buttonRemove.text")); // NOI18N
        buttonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        add(buttonRemove, gridBagConstraints);
        buttonRemove.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AN_JFXApplicationParametersPanel.buttonRemove.text")); // NOI18N
        buttonRemove.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AD_JFXApplicationParametersPanel.buttonRemove.text")); // NOI18N

        labelRemark.setText(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "JFXApplicationMultiPropertyPanel.labelRemark.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(labelRemark, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(buttonDefault, org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "JFXApplicationMultiPropertyPanel.buttonDefault.text")); // NOI18N
        buttonDefault.setEnabled(false);
        buttonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDefaultActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        add(buttonDefault, gridBagConstraints);
        buttonDefault.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AN_JFXApplicationParametersPanel.buttonDefault.text")); // NOI18N
        buttonDefault.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXApplicationMultiPropertyPanel.class, "AD_JFXApplicationParametersPanel.buttonDefault.text")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
    tableModel.addRow();
    tableMultiProperties.requestFocusInWindow();
    //tableParams.changeSelection(WIDTH, WIDTH, true, true);
}//GEN-LAST:event_buttonAddActionPerformed

private void buttonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveActionPerformed
    int selIndex = tableMultiProperties.getSelectedRow();
    if (selIndex != -1) {
        tableModel.removeRow(selIndex);
        tableMultiProperties.requestFocusInWindow();
    }
}//GEN-LAST:event_buttonRemoveActionPerformed

    private void buttonDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDefaultActionPerformed
        tableModel.reset();
    }//GEN-LAST:event_buttonDefaultActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonDefault;
    private javax.swing.JButton buttonRemove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelRemark;
    private javax.swing.JLabel labelTable;
    private javax.swing.JTable tableMultiProperties;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {
        updateDialogButtons();
        updateAddButton();
        updateDefaultButtonState();
    }

    private void updateDialogButtons() {
        if(tableModel.isValid()) {
           desc.setValid(true);
        } else {
           desc.setValid(false);
        }
    }
    
    private void updateRemoveButton() {
        int selIndex = tableMultiProperties.getSelectedRow();
        buttonRemove.setEnabled(selIndex != -1);
    }
    
    private void updateAddButton() {
        if(tableModel.isLastRowEmpty()) {
            buttonAdd.setEnabled(false);
        } else {
            buttonAdd.setEnabled(true);
        }
    }
    
    private void updateDefaultButtonState() {
        buttonDefault.setEnabled(tableModel.isResettable());
    }
    
    private void updateDefaultButtonLabel() {
        if(tableModel.hasDefaultProperties()) {
            buttonDefault.setText(NbBundle.getMessage(JFXRunPanel.class, "JFXApplicationMultiPropertyPanel.buttonDefault.text.default")); //NOI18N
            buttonDefault.setMnemonic(java.awt.event.KeyEvent.VK_D);
        } else {
            buttonDefault.setText(NbBundle.getMessage(JFXRunPanel.class, "JFXApplicationMultiPropertyPanel.buttonDefault.text.clean")); //NOI18N
            buttonDefault.setMnemonic(java.awt.event.KeyEvent.VK_C);
        }
    }

}
