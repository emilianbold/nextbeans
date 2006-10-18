/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.wsitconf.ui.service.subpanels;

import java.awt.Component;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import org.netbeans.modules.websvc.wsitconf.ui.security.listmodels.*;
import org.netbeans.modules.websvc.wsitconf.wsdlmodelext.SecurityPolicyModelHelper;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.util.Vector;

/**
 *
 * @author  Martin Grebac
 */
public class TargetsPanel extends javax.swing.JPanel {
    
    private WSDLModel model;
    private WSDLComponent comp;
    
    private Vector<Vector> targetsModel;
    private MessagePartsModel targetsTableDataModel;
    private Vector columnNames = new Vector();

    private AddHeaderPanel addHeaderPanel;
    
    boolean inSync = false;
    
    /**
     * Creates new form TargetsPanel
     */
    public TargetsPanel(WSDLComponent c) {
        super();
        this.model = c.getModel();
        this.comp = c;
        initComponents();

        columnNames.add(NbBundle.getMessage(TargetsPanel.class, "LBL_Targets_MessagePart"));    //NOI18N
        columnNames.add(NbBundle.getMessage(TargetsPanel.class, "LBL_Targets_Sign"));           //NOI18N
        columnNames.add(NbBundle.getMessage(TargetsPanel.class, "LBL_Targets_Encrypt"));        //NOI18N
        columnNames.add(NbBundle.getMessage(TargetsPanel.class, "LBL_Targets_Require"));        //NOI18N
        
        sync();
    }
    
    private void sync() {
        inSync = true;
        
        targetsModel = SecurityPolicyModelHelper.getTargets(comp);
        targetsTableDataModel = new MessagePartsModel(getTargetsModel(), columnNames);
        jTable1.setModel(targetsTableDataModel);
        jTable1.doLayout();
        jTable1.setDefaultEditor(MessageElement.class, new XPathTableCellEditor());
        jTable1.getColumnModel().getColumn(TargetElement.DATA).setCellEditor(new XPathTableCellEditor());
        
        enableDisable();
        
        inSync = false;
    }
    
    private AddHeaderPanel getAddHeaderPanel() {
        if (this.addHeaderPanel == null) {
            addHeaderPanel = new AddHeaderPanel();
        }
        return addHeaderPanel;
    }
    
    private void saveTargetsModel() {
        if (!inSync) {
            SecurityPolicyModelHelper.setTargets(comp, getTargetsModel());
            jTable1.setModel(new MessagePartsModel(getTargetsModel(), columnNames));
        }
    }

//    private void disableComponents(JComponent c, boolean enable) {
//        Component[] comps = c.getComponents();
//        if ((comps != null) && (comps.length > 0)) {
//            for (Component comp : comps) {
//                if (comp instanceof JComponent) {
//                    disableComponents((JComponent)comp, enable);
//                }
//                comp.setEnabled(enable);
//            }
//        }
//        c.setEnabled(enable);
//    }
    
    private void enableDisable() {
//        jTable1.setEnabled(true);
//        jScrollPane2.setEnabled(!defaults);
//        jScrollPane2.getViewport().getView().setEnabled(!defaults);
//        jScrollPane2.getHorizontalScrollBar().setEnabled(!defaults);
//        jScrollPane2.getVerticalScrollBar().setEnabled(!defaults);
//        disableComponents(jScrollPane2, !defaults);
//        addBodyButton.setEnabled(!defaults);
//        addHeaderButton.setEnabled(!defaults);
//        addPartButton.setEnabled(!defaults);
//        removeButton.setEnabled(!defaults);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        addHeaderButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        addPartButton = new javax.swing.JButton();
        addBodyButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        addHeaderButton.setText(org.openide.util.NbBundle.getMessage(TargetsPanel.class, "LBL_AddHeader")); // NOI18N
        addHeaderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHeaderButtonActionPerformed(evt);
            }
        });

        removeButton.setText(org.openide.util.NbBundle.getMessage(TargetsPanel.class, "LBL_Remove")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        addPartButton.setText(org.openide.util.NbBundle.getMessage(TargetsPanel.class, "LBL_AddXPath")); // NOI18N
        addPartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPartButtonActionPerformed(evt);
            }
        });

        addBodyButton.setText(org.openide.util.NbBundle.getMessage(TargetsPanel.class, "LBL_AddBody")); // NOI18N
        addBodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBodyButtonActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setShowVerticalLines(false);
        jTable1.setVerifyInputWhenFocusTarget(false);
        jScrollPane2.setViewportView(jTable1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addBodyButton)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(addPartButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(removeButton))
                    .add(addHeaderButton))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {addBodyButton, addHeaderButton, addPartButton, removeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addBodyButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addHeaderButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addPartButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addBodyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBodyButtonActionPerformed
        MessageBody body = new MessageBody();
        if (!(SecurityPolicyModelHelper.targetExists(getTargetsModel(), body) != null)) {
            Vector row = new Vector();
            row.add(TargetElement.DATA, body);
            row.add(TargetElement.SIGN, Boolean.TRUE);
            row.add(TargetElement.ENCRYPT, Boolean.FALSE);
            row.add(TargetElement.REQUIRE, Boolean.FALSE);
            getTargetsModel().add(row);
            saveTargetsModel();
        }
    }//GEN-LAST:event_addBodyButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int[] rows = jTable1.getSelectedRows();
        for (int i=0; i<rows.length; i++) {
            getTargetsModel().remove(rows[i]);
        }
        saveTargetsModel();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addPartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPartButtonActionPerformed
        MessageElement e = new MessageElement("/S:Envelope/S:Body"); //NOI18N
        if (!(SecurityPolicyModelHelper.targetExists(targetsModel, e) != null)) {
            Vector row = new Vector();
            row.add(TargetElement.DATA, e);
            row.add(TargetElement.SIGN, Boolean.FALSE);
            row.add(TargetElement.ENCRYPT, Boolean.FALSE);
            row.add(TargetElement.REQUIRE, Boolean.TRUE);
            getTargetsModel().add(row);
            jTable1.updateUI();
//            jTable1.editCellAt(getTargetsModel().size()+1, TargetElement.DATA);
        }
    }//GEN-LAST:event_addPartButtonActionPerformed

    private void addHeaderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHeaderButtonActionPerformed
        AddHeaderPanel addHeaderPanel = getAddHeaderPanel();
        DialogDescriptor dd = new DialogDescriptor(
                addHeaderPanel, 
                NbBundle.getMessage(TargetsPanel.class, "LBL_SignEncryptChooser_AddHeaderTitle"),  //NOI18N
                true, 
                DialogDescriptor.OK_CANCEL_OPTION, 
                DialogDescriptor.CANCEL_OPTION, 
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(TargetsPanel.class),
                null);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION)) {
            if (addHeaderPanel != null) {
                if (addHeaderPanel.isAllHeaders()) {
                    for (String s : MessageHeader.ALL_HEADERS) {
                        MessageHeader h = new MessageHeader(s);
                        if (!(SecurityPolicyModelHelper.targetExists(getTargetsModel(), h) != null)) {
                            Vector row = new Vector();
                            row.add(TargetElement.DATA, h);
                            row.add(TargetElement.SIGN, Boolean.TRUE);
                            row.add(TargetElement.ENCRYPT, Boolean.FALSE);
                            row.add(TargetElement.REQUIRE, Boolean.FALSE);
                            getTargetsModel().add(row);
                        }
                    }
                } else {
                    String header = addHeaderPanel.getHeader();
                    MessageHeader h = new MessageHeader(header);
                    if (!(SecurityPolicyModelHelper.targetExists(getTargetsModel(), h) != null)) {
                        Vector row = new Vector();
                        row.add(TargetElement.DATA, h);
                        row.add(TargetElement.SIGN, Boolean.TRUE);
                        row.add(TargetElement.ENCRYPT, Boolean.FALSE);
                        row.add(TargetElement.REQUIRE, Boolean.FALSE);
                        getTargetsModel().add(row);
                    }
                }
                saveTargetsModel();
            }
        }
    }//GEN-LAST:event_addHeaderButtonActionPerformed

    public class XPathTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        // This is the component that will handle the editing of the cell value
        JComponent component = new JTextField();
    
        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
    
            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }
    
            // Configure the component with the specified value
            ((JTextField)component).setText(((MessageElement)value).getElement());
    
            // Return the configured component
            return component;
        }
    
        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        public Object getCellEditorValue() {
            return new MessageElement(((JTextField)component).getText());
        }
        
        @Override
        public boolean stopCellEditing() {
//            String s = (String)getCellEditorValue();    
            return true;
        }

    }

    private class MessagePartsModel extends DefaultTableModel {

        public MessagePartsModel (Vector<Vector> data, Vector columnNames) {
            super (data, columnNames);//NOI18N
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (column > 0) return true;
            Vector rowVector = getTargetsModel().get(row);
            if (rowVector.get(TargetElement.DATA) instanceof MessageElement) {
                return true;
            }
            return false;
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return TargetElement.class;
            }
            return Boolean.class;
        }
    }

    public Vector<Vector> getTargetsModel() {
        return targetsModel;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBodyButton;
    private javax.swing.JButton addHeaderButton;
    private javax.swing.JButton addPartButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
}

