/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.wizard.options;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author Radek Matous
 */
final class OptionsPanel0 extends BasicWizardIterator.Panel {
    private NewOptionsIterator.DataModel data;
    private DocumentListener fieldsDL;
    
    public OptionsPanel0(final WizardDescriptor setting, final NewOptionsIterator.DataModel data) {
        super(setting);
        this.data = data;
        initComponents();
        initAccessibility();
        putClientProperty("NewFileWizard_Title",// NOI18N
                NbBundle.getMessage(OptionsPanel0.class,"LBL_OptionsWizardTitle")); // NOI18N
        
    }
    
    private void addListeners() {
        if (fieldsDL == null) {
            fieldsDL = new UIUtil.DocumentAdapter() {
                public void insertUpdate(DocumentEvent e) { updateData(); }
            };
            
            categoryNameField.getDocument().addDocumentListener(fieldsDL);
            descriptionField.getDocument().addDocumentListener(fieldsDL);
            displayNameField1.getDocument().addDocumentListener(fieldsDL);
            iconField.getDocument().addDocumentListener(fieldsDL);
            titleField.getDocument().addDocumentListener(fieldsDL);
            tooltipField1.getDocument().addDocumentListener(fieldsDL);
        }
    }
    
    private void removeListeners() {
        if (fieldsDL != null) {        
            categoryNameField.getDocument().removeDocumentListener(fieldsDL);
            descriptionField.getDocument().removeDocumentListener(fieldsDL);
            displayNameField1.getDocument().removeDocumentListener(fieldsDL);
            iconField.getDocument().removeDocumentListener(fieldsDL);
            titleField.getDocument().removeDocumentListener(fieldsDL);
            tooltipField1.getDocument().removeDocumentListener(fieldsDL);
            fieldsDL = null;
        }
    }
    
    
    protected void storeToDataModel() {
        removeListeners();
        updateData();
    }
    protected void readFromDataModel() {
        addListeners();
    }
    
    private void updateData() {
        int errCode = 0;
        if (AdvancedButton.isSelected()) {
            assert !OptinsCategoryButton.isSelected();
            errCode = data.setDataForAdvanced(displayNameField1.getText(), tooltipField1.getText());
        } else {
            assert OptinsCategoryButton.isSelected();
            errCode = data.setDataForOptionCategory(titleField.getText(), descriptionField.getText(),
                    categoryNameField.getText(), iconField.getText());
        }
        if (errCode == 0) {
            setErrorMessage(null);
        } else {
            setErrorMessage(data.getErrorMessage(errCode));
        }
    }
    
    protected String getPanelName() {
        return NbBundle.getMessage(OptionsPanel0.class,"LBL_OptionsPanel0_Title"); // NOI18N
    }
    
    
    protected HelpCtx getHelp() {
        return new HelpCtx(OptionsPanel0.class);
    }
    
    private static String getMessage(String key) {
        return NbBundle.getMessage(OptionsPanel0.class, key);
    }
    
    private void initAccessibility() {
        titleField.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Title"));
        tooltipField1.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Tooltip"));
        descriptionField.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Description"));
        displayNameField1.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_DisplayName"));
        categoryNameField.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_CategoryName"));
        iconField.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_IconPath"));
    }
    
    public void addNotify() {
        super.addNotify();
        addListeners();
        updateData();
    }
    
    private void enableDisable() {
        boolean advancedEnabled = AdvancedButton.isSelected();
        assert advancedEnabled != OptinsCategoryButton.isSelected();
        
        categoryNameField.setEnabled(!advancedEnabled);
        categoryNameLbl.setEnabled(!advancedEnabled);
        descriptionField.setEnabled(!advancedEnabled);
        descriptionLbl.setEnabled(!advancedEnabled);
        iconButton.setEnabled(!advancedEnabled);
        iconField.setEnabled(!advancedEnabled);
        iconLbl.setEnabled(!advancedEnabled);
        titleField.setEnabled(!advancedEnabled);
        titleLbl.setEnabled(!advancedEnabled);
        
        displayNameField1.setEnabled(advancedEnabled);
        displayNameLbl1.setEnabled(advancedEnabled);
        tooltipField1.setEnabled(advancedEnabled);
        tooltipLbl1.setEnabled(advancedEnabled);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        AdvancedButton = new javax.swing.JRadioButton();
        OptinsCategoryButton = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        categoryNameLbl = new javax.swing.JLabel();
        categoryNameField = new javax.swing.JTextField();
        displayNameLbl1 = new javax.swing.JLabel();
        displayNameField1 = new javax.swing.JTextField();
        tooltipLbl1 = new javax.swing.JLabel();
        tooltipField1 = new javax.swing.JTextField();
        descriptionLbl = new javax.swing.JLabel();
        titleLbl = new javax.swing.JLabel();
        descriptionField = new javax.swing.JTextField();
        titleField = new javax.swing.JTextField();
        iconLbl = new javax.swing.JLabel();
        iconField = new javax.swing.JTextField();
        iconButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(AdvancedButton);
        AdvancedButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(AdvancedButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_Advanced"));
        AdvancedButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        AdvancedButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        AdvancedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdvancedButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(AdvancedButton, gridBagConstraints);

        buttonGroup1.add(OptinsCategoryButton);
        org.openide.awt.Mnemonics.setLocalizedText(OptinsCategoryButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_OptionsCategory"));
        OptinsCategoryButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        OptinsCategoryButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        OptinsCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OptinsCategoryButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(OptinsCategoryButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        categoryNameLbl.setLabelFor(categoryNameField);
        org.openide.awt.Mnemonics.setLocalizedText(categoryNameLbl, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_CategoryName"));
        categoryNameLbl.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 6, 12);
        add(categoryNameLbl, gridBagConstraints);

        categoryNameField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(categoryNameField, gridBagConstraints);

        displayNameLbl1.setLabelFor(displayNameField1);
        org.openide.awt.Mnemonics.setLocalizedText(displayNameLbl1, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_DisplaName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 6, 12);
        add(displayNameLbl1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(displayNameField1, gridBagConstraints);

        tooltipLbl1.setLabelFor(tooltipField1);
        org.openide.awt.Mnemonics.setLocalizedText(tooltipLbl1, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_Tooltip"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 6, 12);
        add(tooltipLbl1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(tooltipField1, gridBagConstraints);

        descriptionLbl.setLabelFor(descriptionField);
        org.openide.awt.Mnemonics.setLocalizedText(descriptionLbl, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_Description"));
        descriptionLbl.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 6, 12);
        add(descriptionLbl, gridBagConstraints);

        titleLbl.setLabelFor(titleField);
        org.openide.awt.Mnemonics.setLocalizedText(titleLbl, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/options/Bundle").getString("LBL_Title"));
        titleLbl.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 6, 12);
        add(titleLbl, gridBagConstraints);

        descriptionField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(descriptionField, gridBagConstraints);

        titleField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(titleField, gridBagConstraints);

        iconLbl.setLabelFor(iconField);
        org.openide.awt.Mnemonics.setLocalizedText(iconLbl, org.openide.util.NbBundle.getMessage(OptionsPanel0.class, "LBL_Icon"));
        iconLbl.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 18, 0, 0);
        add(iconLbl, gridBagConstraints);

        iconField.setEditable(false);
        iconField.setText(org.openide.util.NbBundle.getMessage(OptionsPanel0.class, "CTL_None"));
        iconField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(iconField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(iconButton, org.openide.util.NbBundle.getMessage(OptionsPanel0.class, "LBL_Icon_Browse"));
        iconButton.setEnabled(false);
        iconButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iconButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        add(iconButton, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void OptinsCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OptinsCategoryButtonActionPerformed
        enableDisable();
        updateData();
    }//GEN-LAST:event_OptinsCategoryButtonActionPerformed
    
    private void AdvancedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdvancedButtonActionPerformed
        enableDisable();
        updateData();
    }//GEN-LAST:event_AdvancedButtonActionPerformed
    
    private void iconButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iconButtonActionPerformed
        JFileChooser chooser = UIUtil.getIconFileChooser(iconField.getText());
        int ret = chooser.showDialog(this, getMessage("LBL_Select")); // NOI18N
        if (ret == JFileChooser.APPROVE_OPTION) {
            File iconFile =  chooser.getSelectedFile();
            iconField.setText(iconFile.getAbsolutePath());
            //updateData();
        }
    }//GEN-LAST:event_iconButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton AdvancedButton;
    private javax.swing.JRadioButton OptinsCategoryButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField categoryNameField;
    private javax.swing.JLabel categoryNameLbl;
    private javax.swing.JTextField descriptionField;
    private javax.swing.JLabel descriptionLbl;
    private javax.swing.JTextField displayNameField1;
    private javax.swing.JLabel displayNameLbl1;
    private javax.swing.JButton iconButton;
    private javax.swing.JTextField iconField;
    private javax.swing.JLabel iconLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLbl;
    private javax.swing.JTextField tooltipField1;
    private javax.swing.JLabel tooltipLbl1;
    // End of variables declaration//GEN-END:variables
    
}
