/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.struts.dialogs;

import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.web.struts.StrutsConfigDataObject;
import org.netbeans.modules.web.struts.StrutsConfigUtilities;
import org.netbeans.modules.web.struts.config.model.Action;
import org.openide.util.NbBundle;

/**
 *
 * @author  petr
 */
public class AddFIActionPanel extends javax.swing.JPanel implements ValidatingPanel {
    private StrutsConfigDataObject config;
    /** Creates new form AddFIActionPanel */
    public AddFIActionPanel(StrutsConfigDataObject dObject) {
        config = dObject;
        initComponents();
        List actions = StrutsConfigUtilities.getAllActionsInModule(config);
        DefaultComboBoxModel model = (DefaultComboBoxModel)cbAction.getModel();
        //model.removeAllElements();
        Iterator iter = actions.iterator();
        while (iter.hasNext())
            model.addElement(((Action)iter.next()).getAttributeValue("path"));
    }

    public String validatePanel() {
        String actionPath = getActionPath();
        if (actionPath==null || actionPath.equals("/")) //NOI18N
            return NbBundle.getMessage(AddFIActionPanel.class,"MSG_EmptyActionPath");
        if (!actionPath.startsWith("/") ) //NOI18N
            return NbBundle.getMessage(AddFIActionPanel.class,"MSG_IncorrectActionPath", actionPath);
        if (containsActionPath(actionPath)) //NOI18N
            return NbBundle.getMessage(AddFIActionPanel.class,"MSG_DupliciteActionPath",actionPath);
        if (rbResourceFile.isSelected() && tResourceFile.getText().trim().length()==0) {
            return NbBundle.getMessage(AddFIActionPanel.class,"MSG_EmptyResourceFile");
        } else if (rbAction.isSelected() && cbAction.getSelectedItem()==null) {
            return NbBundle.getMessage(AddFIActionPanel.class,"MSG_EmptyAction");
        } else return null;
    }

    public javax.swing.AbstractButton[] getStateChangeComponents() {
        return new javax.swing.AbstractButton[]{ rbResourceFile };
    }

    public javax.swing.text.JTextComponent[] getDocumentChangeComponents() {
        return new javax.swing.text.JTextComponent[]{jTextFieldPath, tResourceFile};
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bgActionType = new javax.swing.ButtonGroup();
        bgCall = new javax.swing.ButtonGroup();
        lActionType = new javax.swing.JLabel();
        rbIncludeAction = new javax.swing.JRadioButton();
        rbForwardAction = new javax.swing.JRadioButton();
        lCall = new javax.swing.JLabel();
        rbResourceFile = new javax.swing.JRadioButton();
        rbAction = new javax.swing.JRadioButton();
        tResourceFile = new javax.swing.JTextField();
        bBrowse = new javax.swing.JButton();
        cbAction = new javax.swing.JComboBox();
        jLabelPath = new javax.swing.JLabel();
        jTextFieldPath = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        lActionType.setLabelFor(rbIncludeAction);
        lActionType.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "LBL_ActionType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(lActionType, gridBagConstraints);

        bgActionType.add(rbIncludeAction);
        rbIncludeAction.setMnemonic(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_Include_mnem").charAt(0));
        rbIncludeAction.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_Include"));
        rbIncludeAction.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        rbIncludeAction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 0, 0);
        add(rbIncludeAction, gridBagConstraints);

        bgActionType.add(rbForwardAction);
        rbForwardAction.setMnemonic(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_Forward_mnem").charAt(0));
        rbForwardAction.setSelected(true);
        rbForwardAction.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_Forward"));
        rbForwardAction.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        rbForwardAction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 0, 0);
        add(rbForwardAction, gridBagConstraints);

        lCall.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "LBL_Call"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(lCall, gridBagConstraints);

        bgCall.add(rbResourceFile);
        rbResourceFile.setMnemonic(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_ResourceFile_mnem").charAt(0));
        rbResourceFile.setSelected(true);
        rbResourceFile.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_ResourceFile"));
        rbResourceFile.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        rbResourceFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbResourceFile.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbResourceFileItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 0, 0);
        add(rbResourceFile, gridBagConstraints);

        bgCall.add(rbAction);
        rbAction.setMnemonic(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_Action_mnem").charAt(0));
        rbAction.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "RB_Action"));
        rbAction.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        rbAction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAction.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbActionItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 20, 0, 0);
        add(rbAction, gridBagConstraints);

        tResourceFile.setMinimumSize(new java.awt.Dimension(200, 24));
        tResourceFile.setPreferredSize(new java.awt.Dimension(200, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(tResourceFile, gridBagConstraints);

        bBrowse.setMnemonic(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "LBL_Browse_mnem").charAt(0));
        bBrowse.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "LBL_BrowseButton"));
        bBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(bBrowse, gridBagConstraints);

        cbAction.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(cbAction, gridBagConstraints);

        jLabelPath.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "LBL_ActionPath_mnem").charAt(0));
        jLabelPath.setLabelFor(jTextFieldPath);
        jLabelPath.setText(org.openide.util.NbBundle.getMessage(AddFIActionPanel.class, "LBL_ActionPath"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabelPath, gridBagConstraints);

        jTextFieldPath.setColumns(30);
        jTextFieldPath.setText("/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(jTextFieldPath, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void bBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBrowseActionPerformed
        try{
        org.netbeans.api.project.SourceGroup[] groups = StrutsConfigUtilities.getDocBaseGroups(config.getPrimaryFile());
            org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
            if (fo!=null) {
                String res = "/"+StrutsConfigUtilities.getResourcePath(groups,fo,'/',true);
                tResourceFile.setText(res);
            }
        } catch (java.io.IOException ex) {}
    }//GEN-LAST:event_bBrowseActionPerformed

    private void rbResourceFileItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbResourceFileItemStateChanged
        tResourceFile.setEnabled(true);
        bBrowse.setEnabled(true);
        cbAction.setEnabled(false);
    }//GEN-LAST:event_rbResourceFileItemStateChanged

    private void rbActionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbActionItemStateChanged
        tResourceFile.setEnabled(false);
        bBrowse.setEnabled(false);
        cbAction.setEnabled(true);
    }//GEN-LAST:event_rbActionItemStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    private javax.swing.ButtonGroup bgActionType;
    private javax.swing.ButtonGroup bgCall;
    private javax.swing.JComboBox cbAction;
    private javax.swing.JLabel jLabelPath;
    private javax.swing.JTextField jTextFieldPath;
    private javax.swing.JLabel lActionType;
    private javax.swing.JLabel lCall;
    private javax.swing.JRadioButton rbAction;
    private javax.swing.JRadioButton rbForwardAction;
    private javax.swing.JRadioButton rbIncludeAction;
    private javax.swing.JRadioButton rbResourceFile;
    private javax.swing.JTextField tResourceFile;
    // End of variables declaration//GEN-END:variables
    
    public String getActionPath() {
        String path = jTextFieldPath.getText().trim();
        return path.length()==0?null:path;
    }
    
    public boolean isForward() {
        return rbForwardAction.isSelected();
    }
    
    public String getResource() {
        if (rbResourceFile.isSelected()) {
            String resource=tResourceFile.getText().trim();
            return resource.length()==0?null:resource;
        } else {
            return (String)cbAction.getSelectedItem();
        }
    }
    
    private boolean containsActionPath(String path) {
        DefaultComboBoxModel model = (DefaultComboBoxModel)cbAction.getModel();
        for (int i=0; i<model.getSize(); i++) {
            if (path.equals(model.getElementAt(i))) return true;
        }
        return false;
    }
}
