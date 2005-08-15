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
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.web.struts.StrutsConfigDataObject;
import org.netbeans.modules.web.struts.StrutsConfigUtilities;
import org.netbeans.modules.web.struts.config.model.FormBean;
import org.openide.util.NbBundle;

/**
 *
 * @author  mkuchtiak
 */
public class AddFormPropertyPanel extends javax.swing.JPanel implements ValidatingPanel {
    StrutsConfigDataObject config;
    /** Creates new form AddForwardDialog */
    public AddFormPropertyPanel(StrutsConfigDataObject config, String targetFormName) {
        this.config=config;
        initComponents();
        List beans = StrutsConfigUtilities.getAllFormBeansInModule(config);
        DefaultComboBoxModel model = (DefaultComboBoxModel)jComboBoxFormName.getModel();
        Iterator iter = beans.iterator();
        while (iter.hasNext()) {
            String name=((FormBean)iter.next()).getAttributeValue("name"); //NOI18N
            model.addElement(name);
        }
        if (targetFormName != null) {
            jComboBoxFormName.setSelectedItem(targetFormName);
        }
    }
    
    public AddFormPropertyPanel(StrutsConfigDataObject config) {
        this(config,null);
    }

    public String validatePanel() {
        if (getPropertyName()==null)
            return NbBundle.getMessage(AddFormPropertyPanel.class,"MSG_EmptyPropertyName");
        if (getFormName()==null)
            return NbBundle.getMessage(AddFormPropertyPanel.class,"MSG_EmptyFormName");
        if (getPropertyType()==null)
            return NbBundle.getMessage(AddFormPropertyPanel.class,"MSG_EmptyPropertyType");
        if (jRadioButtonArray.isSelected() && getArraySize()==null) {
                return NbBundle.getMessage(AddFormPropertyPanel.class,"MSG_IncorrectSize");
        }
        return null;
    }

    public AbstractButton[] getStateChangeComponents() {
        return new AbstractButton[] {jRadioButtonSingle};
    }

    public JTextComponent[] getDocumentChangeComponents() {
        return new JTextComponent[]{jTextFieldPropertyName, jTextFieldSize, (JTextComponent)jComboBoxPropertyType.getEditor().getEditorComponent()};
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
        jLabelPropertyName = new javax.swing.JLabel();
        jTextFieldPropertyName = new javax.swing.JTextField();
        jRadioButtonSingle = new javax.swing.JRadioButton();
        jTextFieldSize = new javax.swing.JTextField();
        jComboBoxFormName = new javax.swing.JComboBox();
        jLabelFormName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxPropertyType = new javax.swing.JComboBox();
        jLabelInitValue = new javax.swing.JLabel();
        jLabelSize = new javax.swing.JLabel();
        jRadioButtonArray = new javax.swing.JRadioButton();
        jTextFieldInitValue = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        jLabelPropertyName.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_PropertyName_mnem").charAt(0));
        jLabelPropertyName.setLabelFor(jTextFieldPropertyName);
        jLabelPropertyName.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_PropertyName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabelPropertyName, gridBagConstraints);

        jTextFieldPropertyName.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(jTextFieldPropertyName, gridBagConstraints);

        buttonGroup1.add(jRadioButtonSingle);
        jRadioButtonSingle.setMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_SingleType_mnem").charAt(0));
        jRadioButtonSingle.setSelected(true);
        jRadioButtonSingle.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_SingleType"));
        jRadioButtonSingle.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        jRadioButtonSingle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonSingle.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonSingleItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jRadioButtonSingle, gridBagConstraints);

        jTextFieldSize.setColumns(5);
        jTextFieldSize.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(jTextFieldSize, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(jComboBoxFormName, gridBagConstraints);

        jLabelFormName.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_FormName_mnem").charAt(0));
        jLabelFormName.setLabelFor(jComboBoxFormName);
        jLabelFormName.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_FormName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabelFormName, gridBagConstraints);

        jLabel2.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_PropertyType_mnem").charAt(0));
        jLabel2.setLabelFor(jComboBoxPropertyType);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_PropertyType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jLabel2, gridBagConstraints);

        jComboBoxPropertyType.setEditable(true);
        jComboBoxPropertyType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "java.lang.String", "int", "byte", "long", "float", "double", "boolean", "char", "java.lang.Integer", "java.lang.Byte", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean", "java.lang.Char" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(jComboBoxPropertyType, gridBagConstraints);

        jLabelInitValue.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_InitValue_mnem").charAt(0));
        jLabelInitValue.setLabelFor(jTextFieldInitValue);
        jLabelInitValue.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_InitValue"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 12, 0);
        add(jLabelInitValue, gridBagConstraints);

        jLabelSize.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_Size_mnem").charAt(0));
        jLabelSize.setLabelFor(jTextFieldSize);
        jLabelSize.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_Size"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        add(jLabelSize, gridBagConstraints);

        buttonGroup1.add(jRadioButtonArray);
        jRadioButtonArray.setMnemonic(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_ArrayType_mnem").charAt(0));
        jRadioButtonArray.setText(org.openide.util.NbBundle.getMessage(AddFormPropertyPanel.class, "LBL_ArrayType"));
        jRadioButtonArray.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        jRadioButtonArray.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jRadioButtonArray, gridBagConstraints);

        jTextFieldInitValue.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(jTextFieldInitValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        add(jPanel1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void jRadioButtonSingleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonSingleItemStateChanged
// TODO add your handling code here:
        jTextFieldSize.setEditable(!jRadioButtonSingle.isSelected());
    }//GEN-LAST:event_jRadioButtonSingleItemStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBoxFormName;
    private javax.swing.JComboBox jComboBoxPropertyType;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelFormName;
    private javax.swing.JLabel jLabelInitValue;
    private javax.swing.JLabel jLabelPropertyName;
    private javax.swing.JLabel jLabelSize;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButtonArray;
    private javax.swing.JRadioButton jRadioButtonSingle;
    private javax.swing.JTextField jTextFieldInitValue;
    private javax.swing.JTextField jTextFieldPropertyName;
    private javax.swing.JTextField jTextFieldSize;
    // End of variables declaration//GEN-END:variables
    
    public String getFormName() {
        return (String)jComboBoxFormName.getSelectedItem();
    }
    
    public String getPropertyName() {
        String name = jTextFieldPropertyName.getText().trim();
        return name.length()==0?null:name;
    }
    
    public String getPropertyType() {
        javax.swing.text.Document doc = ((JTextComponent)jComboBoxPropertyType.getEditor().getEditorComponent()).getDocument();
        try {
            String propType = doc.getText(0,doc.getLength());
            return propType==null?null:(isArray()?propType+"[]":propType); //NOi18N
        } catch (javax.swing.text.BadLocationException ex) {
            return null;
        }
    }
    
    public boolean isArray() {
        return jRadioButtonArray.isSelected();
    }
    
    public String getInitValue() {
        return jTextFieldInitValue.getText().trim();
    }
     
    public String getArraySize() {
        String text = jTextFieldSize.getText().trim();
        try {
            Integer size = new Integer(text);
            return text;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
