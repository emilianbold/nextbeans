/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.tools.generator;

import java.awt.*;
import java.util.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.table.*;

import org.openide.util.*;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * Customizes element => (method, method type) bindings in visual way.
 *
 * @author  Petr Kuzel
 * @version 
 */
public final class SAXGeneratorMethodPanel extends SAXGeneratorAbstractPanel {

    /** Serial Version UID */
    private static final long serialVersionUID =-4925652670676144240L;    


    private TableModel tableModel;
    private MethodsTable table;   

    // constants
    
    static final int ELEMENT_COLUMN = 0;
    static final int TYPE_COLUMN = 1;
    static final int METHOD_COLUMN = 2;
    static final int COLUMNS = 3;
    
    private final String[] COLUMN_NAMES = new String[] {
        Util.getString ("SAXGeneratorMethodPanel.table.column1"),
        Util.getString ("SAXGeneratorMethodPanel.table.column2"),
        Util.getString ("SAXGeneratorMethodPanel.table.column3"),
    };

    

    private final ValidatingTextField.Validator METHOD_VALIDATOR = new ValidatingTextField.Validator() {
        public boolean isValid(String text) {
            boolean ret = Utilities.isJavaIdentifier("_" + text); // NOI18N
            setValid(ret);
            return ret;
        }        
        
        public String getReason() {
            return Util.getString("MSG_method_err_1");
        }
    };
    
    /** Creates new form SAXGeneratorMethodPanel */
    public SAXGeneratorMethodPanel() {
//        try {
//            this.putClientProperty("WizardPanel_helpURL", new URL("nbresloc:/org/netbeans/modules/xml/tools/generator/SAXGeneratorMethodPanel.html"));  //NOI18N
//        } catch (MalformedURLException ex) {
//        }            
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        descTextArea = new javax.swing.JTextArea();
        tableScrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(480, 350));
        setName(Util.getString ("SAXGeneratorMethodPanel.Form.name"));
        descTextArea.setWrapStyleWord(true);
        descTextArea.setLineWrap(true);
        descTextArea.setEditable(false);
        descTextArea.setForeground(new java.awt.Color(102, 102, 153));
        descTextArea.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/xml/tools/generator/Bundle").getString("DESC_saxw_methods"));
        descTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(descTextArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tableScrollPane, gridBagConstraints);

    }//GEN-END:initComponents

    private void initModels() {
        tableModel = new MethodsTableModel();
    }

    protected void initView() {
        initModels();
        initComponents ();

        table = new MethodsTable();
        table.setModel(tableModel);        
        tableScrollPane.setViewportView(table);  //install it

        initAccessibility();
    }
    
    protected void updateView() {
        checkNames();
    }
    
    protected void updateModel() {
        //tablemodel writes directly to model, no need for update
    }
    
    private void checkNames() {
    }
    
    // ~~~~~~~~~~~~~~~~~ Table models ~~~~~~~~~~~~~~`
        
    /**
     * The table using dynamic cell editors for TYPE_COLUMN
     */
    private class MethodsTable extends JTable {
        
        /** Serial Version UID */
        private static final long serialVersionUID =-8352980237774025436L;
        
        public MethodsTable() {
            getTableHeader().setReorderingAllowed(false);
        }
        
        /** 
         * We need a cell editor that is initialized again and again to
         * that it contains fresh values.
         */
        public TableCellEditor getCellEditor(int row, int column) {
            if (column == TYPE_COLUMN) {                
                ElementBindings.Entry entry = model.getElementBindings().getEntry(row);
                final String element = entry.getElement();
                final JComboBox editor = 
                    new JComboBox(entry.displayTypesFor(model.getElementDeclarations().getEntry(element)));
                
                return new DefaultCellEditor(editor);
            } else if (column == METHOD_COLUMN) {
                ValidatingTextField input = new ValidatingTextField();
                input.setValidator(METHOD_VALIDATOR);
                return new DefaultCellEditor(input);
            } else {
                return super.getCellEditor(row, column);
            }
        }
                
    }

    /**
     * TableModel that directly access <tt>model</tt> field
     */
    private class MethodsTableModel extends AbstractTableModel {

        /** Serial Version UID */
        private static final long serialVersionUID =7287934953974099492L;
        
        public String getColumnName(int col) {
            return COLUMN_NAMES[col];
        }

        public int getRowCount() {
            if (model == null) return 0;
            return model.getElementBindings().size();
        }

        public int getColumnCount() {
            return COLUMNS;
        }

        /**
         * Return String (ELEMENT) or String (TYPE) or String (METHOD).
         */
        public Object getValueAt(int row, int column) {
            ElementBindings.Entry entry = model.getElementBindings().getEntry(row);
            switch (column) {
                case ELEMENT_COLUMN: 
                    return entry.getElement();
                case TYPE_COLUMN: 
                    return entry.displayTypeFor(entry.getType());
                case METHOD_COLUMN: 
                    return entry.getMethod();
                default: 
                    return null;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            
            ElementBindings.Entry entry = model.getElementBindings().getEntry(row);
            switch (col) {
                case TYPE_COLUMN:
                    entry.setType(entry.typeFor((String) value));
                    return;
                case METHOD_COLUMN:
                    // the "_" emulates actual prefix added by generator
                    if (Utilities.isJavaIdentifier("_" + (String) value) == false) { // NOI18N
                        setValid(false);
                        return;
                    } else {
                        checkNames();
                    }
                    entry.setMethod((String) value);  //!!! check for duplicities
                    return;
            }
        }

        public boolean isCellEditable(int row, int col) {
            return col != ELEMENT_COLUMN;
        }
    }

    // ~~~~~~~~~~~~~~~~~~~ Conversion routines ~~~~~~~~~~~~~~`
    
    


        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables

    /** Initialize accesibility
     */
    public void initAccessibility(){

        this.getAccessibleContext().setAccessibleDescription(Util.getString("ACSD_SAXGeneratorMethodPanel"));
        table.getAccessibleContext().setAccessibleDescription(Util.getString("ACSD_table"));
        table.getAccessibleContext().setAccessibleName(Util.getString("ACSN_table"));
    }    
}
