/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.i18n.wizard;


import java.awt.Component;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.netbeans.modules.i18n.FactoryRegistry;
import org.netbeans.modules.i18n.I18nUtil;

import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.RepositoryNodeFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAcceptor;
import org.openide.nodes.NodeOperation;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;
import org.openide.util.Lookup;
import org.openide.WizardDescriptor;

/**
 * First panel used in I18N (test) Wizard.
 *
 * @author  Peter Zavadsky
 * @see Panel
 */
final class SourceWizardPanel extends JPanel {

    /** Sources selected by user. */
    private final Map sourceMap = Util.createWizardSettings();
    
    /** This component panel wizard descriptor.
     * @see org.openide.WizardDescriptor.Panel 
     * @see Panel */
    private final Panel descPanel;

    /**
     * Panel role true (test wizard) false (i18n) wizard
     */
    private boolean testRole = false;
    
    /** Creates new form SourceChooserPanel.
     * @param it's panel wizard descriptor */
    private SourceWizardPanel(Panel descPanel, boolean testRole) {
        this.descPanel = descPanel;
        this.testRole = testRole;
        
        initComponents();        

        initAccessibility ();
        
        setPreferredSize(I18nWizardDescriptor.PREFERRED_DIMENSION);
        
        initList();
        
        putClientProperty("WizardPanel_contentSelectedIndex", new Integer(0)); // NOI18N
        
        if (testRole) {
            setName(Util.getString("TXT_SelecTestSources"));
        } else {
            setName(Util.getString("TXT_SelectSources"));                
        }        
    }
    

    /** Getter for <code>sources</code> property. */
    public Map getSourceMap() {
        return sourceMap;
    }
    
    /** Setter for <code>sources</code> property. */
    public void setSourceMap(Map sourceMap) {
        this.sourceMap.clear();
        this.sourceMap.putAll(sourceMap);
        
        sourcesList.setListData(sourceMap.keySet().toArray());
        
        descPanel.fireStateChanged();
    }

    /**
     * Panel description depend of its container test or i18n role
     */
    private String getPanelDescription() {
        if (testRole == false)   {
            return Util.getString("MSG_SourcesPanel_desc");
        } else {
            return Util.getString("MSG_SourcesPanel_test_desc");
        }        
    }
    
    /** 
     * List content drives remove button enableness.
     */
    private void initList() {
        sourcesList.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    removeButton.setEnabled(!sourcesList.isSelectionEmpty());
                }
            }
        );
        
        removeButton.setEnabled(!sourcesList.isSelectionEmpty());
    }
    
    private void initAccessibility() {        
        getAccessibleContext().setAccessibleDescription(getPanelDescription());
        
        addButton.setToolTipText(Util.getString("CTL_AddSource_desc"));
        addButton.setMnemonic(Util.getChar("CTL_AddSource_Mnem"));
        
        removeButton.setToolTipText(Util.getString("CTL_RemoveSource_desc"));
        removeButton.setMnemonic(Util.getChar("CTL_RemoveSource_Mnem"));
        
        sourcesList.getAccessibleContext().setAccessibleDescription(Util.getString("ACS_sourcesList"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        descTextArea = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        sourcesList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setText(getPanelDescription());
        descTextArea.setWrapStyleWord(true);
        descTextArea.setDisabledTextColor(new JLabel().getForeground());
        descTextArea.setEnabled(false);
        descTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(descTextArea, gridBagConstraints);

        sourcesList.setCellRenderer(new DataObjectListCellRenderer());
        jScrollPane1.setViewportView(sourcesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        addButton.setText(NbBundle.getBundle(SourceWizardPanel.class).getString("CTL_AddSource"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 0, 0);
        add(addButton, gridBagConstraints);

        removeButton.setText(NbBundle.getBundle(SourceWizardPanel.class).getString("CTL_RemoveSource"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 0, 0);
        add(removeButton, gridBagConstraints);

    }//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Object[] selected = sourcesList.getSelectedValues();
        
        for(int i=0; i<selected.length; i++) {
            sourceMap.remove(selected[i]);
        }

        sourcesList.setListData(sourceMap.keySet().toArray());
        
        descPanel.fireStateChanged();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        DataFilter dataFilter = new DataFilter() {
            public boolean acceptDataObject (DataObject dataObject) {
                return (dataObject instanceof DataFolder
                 || FactoryRegistry.hasFactory(dataObject.getClass()));
            }
        };

        Node repositoryNode = RepositoryNodeFactory.getDefault().repository(dataFilter);

        // Selects source data objects which could be i18n-ized.
        try {
            Node[] selectedNodes= NodeOperation.getDefault().select(
                Util.getString("LBL_SelectSources"),
                Util.getString("LBL_Filesystems"),
                repositoryNode,
                new NodeAcceptor() {
                    public boolean acceptNodes(Node[] nodes) {
                        if(nodes == null || nodes.length == 0) {
                            return false;
                        }

                        for(int i=0; i<nodes.length; i++) {
                            // Has to be data object.
                            DataObject dataObject = (DataObject)nodes[i].getCookie(DataObject.class);
                            if(dataObject == null)
                                return false;

                            // if it is folder and constains some our data object.
                            if(dataObject instanceof DataFolder && I18nUtil.containsAcceptedDataObject((DataFolder)dataObject))
                                return true;
                            
                            // Has to have registered i18n factory for that data object class name.
                            if(FactoryRegistry.hasFactory(dataObject.getClass()))
                                return true;
                        }
                        
                        return false;
                    }                    
                }
            );
            
            for(int i=0; i<selectedNodes.length; i++) {
                DataObject dataObject = (DataObject)selectedNodes[i].getCookie(DataObject.class);

                if (dataObject instanceof DataFolder) {
                    // recursively add folder content
                    Iterator it = I18nUtil.getAcceptedDataObjects((DataFolder)dataObject).iterator();
                    while (it.hasNext()) {
                        Util.addSource(sourceMap, (DataObject)it.next());
                    }
                } else {
                    Util.addSource(sourceMap, dataObject);
                }
            }
            
            sourcesList.setListData(sourceMap.keySet().toArray());
           
            descPanel.fireStateChanged();
        } catch (UserCancelException uce) {
            if(Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                System.err.println("I18N: User cancelled selection"); // NOI18N
        }
    }//GEN-LAST:event_addButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList sourcesList;
    private javax.swing.JButton addButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextArea descTextArea;
    // End of variables declaration//GEN-END:variables


    /** List cell rendrerer which uses data object as values. */
    public static class DataObjectListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
        int index,               // cell index
        boolean isSelected,      // is the cell selected
        boolean cellHasFocus)    // the list and the cell have the focus
        {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            DataObject dataObject = (DataObject)value;

            if(dataObject != null) {
                label.setText(dataObject.getPrimaryFile().getPackageName('.'));
                label.setIcon(new ImageIcon(dataObject.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16)));
            } else {
                label.setText(""); // NOI18N
                label.setIcon(null);
            }

            return label;
        }
    }

    
    
    /** <code>WizardDescriptor.Panel</code> used for <code>SourceChooserPanel</code>.
     * @see I18nWizardDescriptorPanel
     * @see org.openide.WizardDescriptor.Panel */
    public static class Panel extends I18nWizardDescriptor.Panel {

        /** Test wizard flag. */
        private final boolean testWizard;
        
        
        /** Constructor for i18n wizard. */
        public Panel() {
            this(false);
        }
        
        /** Constructor for specified i18n wizard. */
        public Panel(boolean testWizard) {
            this.testWizard = testWizard;
        }
        
        
        /** Gets component to display. Implements superclass abstract method. 
         * @return this instance */
        protected Component createComponent() {                                    
            Component component = new SourceWizardPanel(this, testWizard);            
            
            return component;
        }

        /** Gets if panel is valid. Overrides superclass method. */
        public boolean isValid() {
            return !((SourceWizardPanel)getComponent()).getSourceMap().isEmpty();
        }
        
        /** Reads settings at the start when the panel comes to play. Overrides superclass method. */
        public void readSettings(Object settings) {
            ((SourceWizardPanel)getComponent()).setSourceMap((Map)settings);
        }

        /** Stores settings at the end of panel show. Overrides superclass method. */
        public void storeSettings(Object settings) {
            // Update sources.
            ((Map)settings).clear();
            ((Map)settings).putAll(((SourceWizardPanel)getComponent()).getSourceMap());
        }
        
        /** Gets help. Implements superclass abstract method. */
        public HelpCtx getHelp() {
            if(testWizard)
                return new HelpCtx(I18nUtil.HELP_ID_TESTING);
            else
                return new HelpCtx(I18nUtil.HELP_ID_WIZARD);
        }

    } // End of nested Panel class.
    
}
