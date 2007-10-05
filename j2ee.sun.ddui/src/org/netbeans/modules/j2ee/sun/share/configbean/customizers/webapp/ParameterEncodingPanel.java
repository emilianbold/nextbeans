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
 * ParameterEncodingPanel.java
 *
 * Created on August 31, 2005, 2:27 PM
 */
package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.share.CharsetMapping;
import org.openide.util.NbBundle;


/**
 *
 * @author  Peter Williams
 */
public class ParameterEncodingPanel extends javax.swing.JPanel {

    private final ResourceBundle webappBundle = NbBundle.getBundle(
        "org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp.Bundle");	// NOI18N
    
    public static final String PROP_FORM_HINT_FIELD = "formHintField"; // NOI18N
    public static final String PROP_DEFAULT_CHARSET = "defaultCharset"; // NOI18N
    
    // Temporary storage for quick determination of presence of parameter encoding
    // entry
    private String defaultCharset;
    private String formHintField;

    private DefaultComboBoxModel defaultCharsetCbxModel;

    // Listens for selections in the charset combobox.
    private ActionListener defaultCharsetActionListener;
    
    // Listens for changes to the default list of charsets
    private PropertyChangeListener charsetChangeListener;

    // true if AS 8.1+ fields are visible.
    private boolean as81FeaturesVisible;
    
    /** Creates new form ParameterEncodingPanel */
    public ParameterEncodingPanel() {
        initComponents();
        initUserComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLblParameterEncoding = new javax.swing.JLabel();
        jPnlParameterEncoding = new javax.swing.JPanel();
        jLblDefaultCharset = new javax.swing.JLabel();
        jCbxDefaultCharset = new javax.swing.JComboBox();
        jLblFormHintField = new javax.swing.JLabel();
        jTxtFormHintField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        jLblParameterEncoding.setLabelFor(jPnlParameterEncoding);
        jLblParameterEncoding.setText(webappBundle.getString("LBL_ParameterEncoding"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jLblParameterEncoding, gridBagConstraints);

        jPnlParameterEncoding.setLayout(new java.awt.GridBagLayout());

        jPnlParameterEncoding.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLblDefaultCharset.setLabelFor(jCbxDefaultCharset);
        jLblDefaultCharset.setText(webappBundle.getString("LBL_DefaultCharset_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPnlParameterEncoding.add(jLblDefaultCharset, gridBagConstraints);

        jCbxDefaultCharset.setPrototypeDisplayValue("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        jPnlParameterEncoding.add(jCbxDefaultCharset, gridBagConstraints);

        jLblFormHintField.setLabelFor(jTxtFormHintField);
        jLblFormHintField.setText(webappBundle.getString("LBL_FormHintField_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        jPnlParameterEncoding.add(jLblFormHintField, gridBagConstraints);

        jTxtFormHintField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtFormHintFieldKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPnlParameterEncoding.add(jTxtFormHintField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 5);
        add(jPnlParameterEncoding, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jTxtFormHintFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtFormHintFieldKeyReleased
        String oldFormHintField = formHintField;
        formHintField = jTxtFormHintField.getText();
        firePropertyChange(PROP_FORM_HINT_FIELD, oldFormHintField, formHintField);
    }//GEN-LAST:event_jTxtFormHintFieldKeyReleased
    
    private void jCbxDefaultCharsetActionPerformed(java.awt.event.ActionEvent evt) {
        String oldDefaultCharset = defaultCharset;
        Object item = defaultCharsetCbxModel.getSelectedItem();
        if(item instanceof CharsetMapping) {
            defaultCharset = ((CharsetMapping) item).getAlias();
        } else {
            defaultCharset = null;
        }
        firePropertyChange(PROP_DEFAULT_CHARSET, oldDefaultCharset, defaultCharset);        
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jCbxDefaultCharset;
    private javax.swing.JLabel jLblDefaultCharset;
    private javax.swing.JLabel jLblFormHintField;
    private javax.swing.JLabel jLblParameterEncoding;
    private javax.swing.JPanel jPnlParameterEncoding;
    private javax.swing.JTextField jTxtFormHintField;
    // End of variables declaration//GEN-END:variables

    private void initUserComponents() {
        // Init default charset combo box
        defaultCharsetCbxModel = new DefaultComboBoxModel();
        defaultCharsetCbxModel.addElement(""); // NOI18N
        SortedMap charsets = CharsetMapping.getSortedAvailableCharsetMappings();
        for(Iterator iter = charsets.entrySet().iterator(); iter.hasNext(); ) {
            CharsetMapping cm = (CharsetMapping) ((Map.Entry) iter.next()).getValue();
            defaultCharsetCbxModel.addElement(cm);
        }
        jCbxDefaultCharset.setModel(defaultCharsetCbxModel);

        defaultCharsetActionListener = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCbxDefaultCharsetActionPerformed(evt);
            }
        };
        
        charsetChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                updateDefaultCharsetModel();
            }
        };
    }
    
    public void addListeners() {
        jCbxDefaultCharset.addActionListener(defaultCharsetActionListener);
        CharsetMapping.addPropertyChangeListener(charsetChangeListener);
    }

    public void removeListeners() {
        jCbxDefaultCharset.removeActionListener(defaultCharsetActionListener);
        CharsetMapping.removePropertyChangeListener(charsetChangeListener);
    }
    
    public void initFields(ASDDVersion asVersion, String defaultCharset, String formHintField, boolean enabled) {
        if(ASDDVersion.SUN_APPSERVER_8_0.compareTo(asVersion) >= 0) {
            showAS81Fields();
        } else {
            hideAS81Fields();
        }
        
        enableFields(enabled);
        
        if(enabled) {
            // init parameter encoding fields
            this.defaultCharset = defaultCharset;
            this.formHintField = formHintField;

            if(as81FeaturesVisible) {
                defaultCharsetCbxModel.setSelectedItem(CharsetMapping.getCharsetMapping(defaultCharset));
            }
            jTxtFormHintField.setText(formHintField);
        } else {
            if(as81FeaturesVisible) {
                jCbxDefaultCharset.setSelectedItem(null);
            }
            jTxtFormHintField.setText("");
        }
    }
        
    // TODO after 5.0, generalize version based field display for multiple (> 2)
    // appserver versions.
    private void showAS81Fields() {
        if(!as81FeaturesVisible) {
            jLblDefaultCharset.setVisible(true);
            jCbxDefaultCharset.setVisible(true);
            as81FeaturesVisible = true;
        }
    }
    
    private void hideAS81Fields() {
        if(as81FeaturesVisible) {
            jLblDefaultCharset.setVisible(false);
            jCbxDefaultCharset.setVisible(false);
            as81FeaturesVisible = false;
        }
    }
    
    public String getFormHintField() {
        return formHintField;
    }
    
    public String getDefaultCharset() {
        return defaultCharset;
    }
    
    private void enableFields(boolean enable) {
        jLblParameterEncoding.setEnabled(enable);
        jLblDefaultCharset.setEnabled(enable);
        jCbxDefaultCharset.setEnabled(enable);
        jLblFormHintField.setEnabled(enable);
        jTxtFormHintField.setEnabled(enable);
        jTxtFormHintField.setEditable(enable);
    }
    
    private void updateDefaultCharsetModel() {
        Object mapping = defaultCharsetCbxModel.getSelectedItem();
        CharsetMapping oldMapping;

        if(mapping instanceof CharsetMapping) {
            oldMapping = (CharsetMapping) mapping;
        } else {
            oldMapping = null;
        }

        defaultCharsetCbxModel = new DefaultComboBoxModel();
        defaultCharsetCbxModel.addElement(""); // NOI18N
        SortedMap charsets = CharsetMapping.getSortedAvailableCharsetMappings();
        for(Iterator iter = charsets.entrySet().iterator(); iter.hasNext(); ) {
            CharsetMapping cm = (CharsetMapping) ((Map.Entry) iter.next()).getValue();
            defaultCharsetCbxModel.addElement(cm);
        }
        jCbxDefaultCharset.setModel(defaultCharsetCbxModel);

        if(oldMapping != null) {
            oldMapping = CharsetMapping.getCharsetMapping(oldMapping.getCharset());
        }

        defaultCharsetCbxModel.setSelectedItem(oldMapping);
    }
    
}
