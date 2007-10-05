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
/*
 * AppClientRootCustomizer.java
 *
 * Created on September 4, 2003, 5:28 PM
 */
package org.netbeans.modules.j2ee.sun.share.configbean.customizers.other;

import java.util.ArrayList;
import java.util.ResourceBundle;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;
import org.netbeans.modules.j2ee.sun.dd.api.common.MessageDestination;

import org.netbeans.modules.j2ee.sun.share.configbean.AppClientRoot;
import org.netbeans.modules.j2ee.sun.share.configbean.StorageBeanFactory;
import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTableModel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTablePanel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.BaseCustomizer;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.HelpContext;


/**
 *
 * @author Peter Williams
 */
public class AppClientRootCustomizer extends BaseCustomizer implements TableModelListener {
	
    private final ResourceBundle otherBundle = ResourceBundle.getBundle(
        "org.netbeans.modules.j2ee.sun.share.configbean.customizers.other.Bundle"); // NOI18N

    private AppClientRoot theBean;

    // Table for editing MessageDestination entries
    private GenericTableModel messageDestinationModel;
    private GenericTablePanel messageDestinationPanel;	

//    // Table for editing MessageDestinationRef entries
//    private GenericTableModel messageDestinationRefModel;
//    private GenericTablePanel messageDestinationRefPanel;
	
    // true if AS 9.0+ fields are visible.
    private boolean as90FeaturesVisible;
    
    // if in setup
    private boolean setup;
    
    /** Creates new form AppClientRootCustomizer */
    public AppClientRootCustomizer() {
        initComponents();
        initUserComponents();
    }

    public AppClientRoot getBean() {
        return theBean;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPnlJws = new javax.swing.JPanel();
        jLblJavaWebStartAccess = new javax.swing.JLabel();
        jLblContextRoot = new javax.swing.JLabel();
        jTxtContextRoot = new javax.swing.JTextField();
        jLblVendor = new javax.swing.JLabel();
        jTxtVendor = new javax.swing.JTextField();
        jLblEligible = new javax.swing.JLabel();
        jChkEligible = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        jPnlJws.setLayout(new java.awt.GridBagLayout());

        jLblJavaWebStartAccess.setText(otherBundle.getString("LBL_JavaWebStartAccess"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPnlJws.add(jLblJavaWebStartAccess, gridBagConstraints);

        jLblContextRoot.setLabelFor(jTxtContextRoot);
        jLblContextRoot.setText(otherBundle.getString("LBL_ContextRoot_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlJws.add(jLblContextRoot, gridBagConstraints);

        jTxtContextRoot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtContextRootKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPnlJws.add(jTxtContextRoot, gridBagConstraints);
        jTxtContextRoot.getAccessibleContext().setAccessibleName(otherBundle.getString("ContextRoot_Acsbl_Name"));
        jTxtContextRoot.getAccessibleContext().setAccessibleDescription(otherBundle.getString("ContextRoot_Acsbl_Desc"));

        jLblVendor.setLabelFor(jTxtVendor);
        jLblVendor.setText(otherBundle.getString("LBL_Vendor_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlJws.add(jLblVendor, gridBagConstraints);

        jTxtVendor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtVendorKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPnlJws.add(jTxtVendor, gridBagConstraints);
        jTxtVendor.getAccessibleContext().setAccessibleName(otherBundle.getString("ASCN_Vendor"));
        jTxtVendor.getAccessibleContext().setAccessibleDescription(otherBundle.getString("ASCD_Vendor"));

        jLblEligible.setLabelFor(jChkEligible);
        jLblEligible.setText(otherBundle.getString("LBL_Eligible_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlJws.add(jLblEligible, gridBagConstraints);

        jChkEligible.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChkEligible.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChkEligible.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jChkEligibleItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPnlJws.add(jChkEligible, gridBagConstraints);
        jChkEligible.getAccessibleContext().setAccessibleName(otherBundle.getString("ASCN_Eligible"));
        jChkEligible.getAccessibleContext().setAccessibleDescription(otherBundle.getString("ASCD_Eligible"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 5);
        add(jPnlJws, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jChkEligibleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jChkEligibleItemStateChanged
        if(!setup && jChkEligible.isVisible() && theBean != null) {
            boolean newState = Utils.interpretCheckboxState(evt);
            if(newState != theBean.isEligible()) {
                try {
                    theBean.setEligible(newState);
                } catch(java.beans.PropertyVetoException exception) {
                    jChkEligible.setSelected(theBean.isEligible());
                }
            }
        }
    }//GEN-LAST:event_jChkEligibleItemStateChanged

    private void jTxtVendorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtVendorKeyReleased
        if(!setup && jTxtVendor.isVisible() && theBean != null) {
            String newVendor = jTxtVendor.getText();
            if(newVendor != null) {
                newVendor = newVendor.trim();
            }
            String oldVendor = theBean.getVendor();
            if(!Utils.strEquivalent(newVendor, oldVendor)) {
                try {
                    theBean.setVendor(newVendor);
                } catch(java.beans.PropertyVetoException exception) {
                    jTxtVendor.setText(oldVendor);
                }
            }
        }
    }//GEN-LAST:event_jTxtVendorKeyReleased

    private void jTxtContextRootKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtContextRootKeyReleased
        if(!setup && jTxtContextRoot.isVisible() && theBean != null) {
            String newContextRoot = jTxtContextRoot.getText();
            if(newContextRoot != null) {
                newContextRoot = newContextRoot.trim();
            }
            String oldContextRoot = theBean.getContextRoot();
            if(!Utils.strEquivalent(newContextRoot, oldContextRoot)) {
                try {
                    theBean.setContextRoot(newContextRoot);
//                    theBean.validateField(AppClientRoot.FIELD_JWSA_CONTEXT_ROOT);
                } catch(java.beans.PropertyVetoException exception) {
                    jTxtContextRoot.setText(oldContextRoot);
                }
            }
        }
    }//GEN-LAST:event_jTxtContextRootKeyReleased
		
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jChkEligible;
    private javax.swing.JLabel jLblContextRoot;
    private javax.swing.JLabel jLblEligible;
    private javax.swing.JLabel jLblJavaWebStartAccess;
    private javax.swing.JLabel jLblVendor;
    private javax.swing.JPanel jPnlJws;
    private javax.swing.JTextField jTxtContextRoot;
    private javax.swing.JTextField jTxtVendor;
    // End of variables declaration//GEN-END:variables

    private void initUserComponents() {
        as90FeaturesVisible = true;
        
        // Add title panel
        addTitlePanel(otherBundle.getString("TITLE_SunApplicationClient"));	// NOI18N
        getAccessibleContext().setAccessibleName(otherBundle.getString("ACSN_SunApplicationClient")); // NOI18N
        getAccessibleContext().setAccessibleDescription(otherBundle.getString("ACSD_SunApplicationClient")); // NOI18N

        /* Add message destination table panel :
         * TableEntry list has two properties: destination name, jndi name
         */
        ArrayList tableColumns = new ArrayList(2);
        tableColumns.add(new GenericTableModel.ValueEntry(null, MessageDestination.MESSAGE_DESTINATION_NAME, 
            otherBundle, "MessageDestinationName", true, true));   // NOI18N - property name
        tableColumns.add(new GenericTableModel.ValueEntry(null, MessageDestination.JNDI_NAME,
            otherBundle, "JNDIName", true, false));    // NOI18N - property name

        messageDestinationModel = new GenericTableModel(messageDestinationFactory, tableColumns);
        messageDestinationPanel = new GenericTablePanel(messageDestinationModel, 
            otherBundle, "MessageDestination", // NOI18N - property name
            HelpContext.HELP_APPCLIENT_MESSAGE_DESTINATION_POPUP);
		
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 6, 5, 5);
        add(messageDestinationPanel, gridBagConstraints);	

        /* Add message destination ref table panel :
         * TableEntry list has two properties: destination ref name, jndi name
         */
//        tableColumns = new ArrayList(2);
//        tableColumns.add(new GenericTableModel.ValueEntry(null, MessageDestinationRef.MESSAGE_DESTINATION_REF_NAME, 
//            otherBundle, "MessageDestinationRefName", true, true));	// NOI18N - property name
//        tableColumns.add(new GenericTableModel.ValueEntry(null, MessageDestinationRef.JNDI_NAME,
//            otherBundle, "JNDIName", true, false));	// NOI18N - property name
		
//        messageDestinationRefModel = new GenericTableModel(messageDestinationRefFactory, tableColumns);
//        messageDestinationRefPanel = new GenericTablePanel(messageDestinationRefModel, 
//            otherBundle, "MessageDestinationRef",	// NOI18N - property name
//            HelpContext.HELP_WEBAPP_MESSAGE_DESTINATION_REF_POPUP);
//
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
//        gridBagConstraints.fill = GridBagConstraints.BOTH;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//            gridBagConstraints.insets = new Insets(6, 6, 5, 5);
//            add(messageDestinationRefPanel, gridBagConstraints);		
        
        // Add error panel
        addErrorPanel();		
    }
    
    protected void initFields() {
        try {
            setup = true;
            messageDestinationPanel.setModel(theBean.getMessageDestinations(), theBean.getAppServerVersion());
            if(ASDDVersion.SUN_APPSERVER_9_0.compareTo(theBean.getAppServerVersion()) <= 0) {
                jTxtContextRoot.setText(theBean.getContextRoot());
                jTxtVendor.setText(theBean.getVendor());
                jChkEligible.setSelected(theBean.isEligible());
//                messageDestinationRefPanel.setModel(theBean.getMessageDestinationRefs(), theBean.getAppServerVersion());		
                showAS90Fields(true);
            } else {
                jTxtContextRoot.setText("");
                jTxtVendor.setText("");
                jChkEligible.setSelected(false);
                showAS90Fields(false);
            }
        } finally {
            setup = false;
        }
    }
	
    private void showAS90Fields(boolean show) {
        if(as90FeaturesVisible != show) {
            as90FeaturesVisible = show;
            jPnlJws.setVisible(show);
//            messageDestinationRefPanel.setVisible(show);
        }
    }
    
    public void addListeners() {
    super.addListeners();

        messageDestinationModel.addTableModelListener(this);
//        messageDestinationRefModel.addTableModelListener(this);
    }
	
    public void removeListeners() {
    super.removeListeners();

//        messageDestinationRefModel.removeTableModelListener(this);
        messageDestinationModel.removeTableModelListener(this);
    }

    protected boolean setBean(Object bean) {
        boolean result = super.setBean(bean);

        if(bean instanceof AppClientRoot) {
            theBean = (AppClientRoot) bean;
            result = true;
        } else {
            // if bean is not an AppClientRoot, then it shouldn't have passed Base either.
            assert (result == false) : 
                "AppClientRootCustomizer was passed wrong bean type in setBean(Object bean)";	// NOI18N

            theBean = null;
            result = false;
        }

        return result;
    }

    /** ----------------------------------------------------------------------- 
     *  Implementation of javax.swing.event.TableModelListener
     */
    public void tableChanged(TableModelEvent e) {
        if(theBean != null) {
            try {
                Object eventSource = e.getSource();
                if(eventSource == messageDestinationModel) {
                    theBean.setMessageDestinations(messageDestinationModel.getData());
                    theBean.firePropertyChange("messageDestination", null, messageDestinationModel.getData());
//                } else if(eventSource == messageDestinationRefModel) {
//                    theBean.setMessageDestinationRefs(messageDestinationRefModel.getData());
//                    theBean.firePropertyChange("messageDestinationRef", null, messageDestinationRefModel.getData());
                }
            } catch(PropertyVetoException ex) {
                // FIXME undo whatever changed... how?
            }
        }
    }	

    public String getHelpId() {
        return "AS_CFG_AppClient";
    }

    // New for migration to sun DD API model.  Factory instance to pass to generic table model
    // to allow it to create messageDestination beans.
    static GenericTableModel.ParentPropertyFactory messageDestinationFactory =
        new GenericTableModel.ParentPropertyFactory() {
            public CommonDDBean newParentProperty(ASDDVersion asVersion) {
                return StorageBeanFactory.getStorageBeanFactory(asVersion).createMessageDestination();
            }
        };
        
//	static GenericTableModel.ParentPropertyFactory messageDestinationRefFactory =
//        new GenericTableModel.ParentPropertyFactory() {
//            public CommonDDBean newParentProperty(ASDDVersion asVersion) {
//                return StorageBeanFactory.getStorageBeanFactory(asVersion).createMessageDestinationRef();
//            }
//        };
}
