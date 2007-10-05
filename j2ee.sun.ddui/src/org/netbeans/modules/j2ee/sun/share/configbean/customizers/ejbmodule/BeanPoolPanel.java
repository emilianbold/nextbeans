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
 * BeanPoolPanel.java        October 21, 2003, 11:31 AM
 *
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule;

import java.util.ResourceBundle;
import javax.swing.JPanel;

import org.netbeans.modules.j2ee.sun.dd.api.ejb.BeanPool;
import org.netbeans.modules.j2ee.sun.share.configbean.BaseEjb;
import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.ValidationError;

/**
 *
 * @author  Rajeshwar Patil
 * @version %I%, %G%
 */
public class BeanPoolPanel extends JPanel {

    private EjbCustomizer masterPanel;

    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule.Bundle"); // NOI18N
   

    /** Creates new form BeanPoolPanel */
    public BeanPoolPanel(EjbCustomizer src) {
        this.masterPanel = src;
        
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

        steadyPoolSizeLabel = new javax.swing.JLabel();
        steadyPoolSizeTextField = new javax.swing.JTextField();
        resizeQuantityLabel = new javax.swing.JLabel();
        resizeQuantityTextField = new javax.swing.JTextField();
        maxPoolSizeLabel = new javax.swing.JLabel();
        maxPoolSizeTextField = new javax.swing.JTextField();
        poolIdleTimeoutInSecondsLabel = new javax.swing.JLabel();
        poolIdleTimeoutInSecondsTextField = new javax.swing.JTextField();
        fillerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        steadyPoolSizeLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Steady_Pool_Size").charAt(0));
        steadyPoolSizeLabel.setLabelFor(steadyPoolSizeTextField);
        steadyPoolSizeLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Steady_Pool_Size_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(steadyPoolSizeLabel, gridBagConstraints);
        steadyPoolSizeLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Steady_Pool_Size_Acsbl_Name"));
        steadyPoolSizeLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Steady_Pool_Size_Acsbl_Desc"));

        steadyPoolSizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                steadyPoolSizeKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(steadyPoolSizeTextField, gridBagConstraints);
        steadyPoolSizeTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Steady_Pool_Size_Acsbl_Name"));
        steadyPoolSizeTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Steady_Pool_Size_Acsbl_Desc"));

        resizeQuantityLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Resize_Quantity").charAt(0));
        resizeQuantityLabel.setLabelFor(resizeQuantityTextField);
        resizeQuantityLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Resize_Quantity_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(resizeQuantityLabel, gridBagConstraints);
        resizeQuantityLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Resize_Quantity_Acsbl_Name"));
        resizeQuantityLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Bean_Pool_Resize_Quantity_Acsbl_Desc"));

        resizeQuantityTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Bean_Pool_Resize_Quantity_Tool_Tip"));
        resizeQuantityTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                resizeQuantityKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(resizeQuantityTextField, gridBagConstraints);
        resizeQuantityTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Resize_Quantity_Acsbl_Name"));
        resizeQuantityTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Bean_Pool_Resize_Quantity_Acsbl_Desc"));

        maxPoolSizeLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Max_Pool_Size").charAt(0));
        maxPoolSizeLabel.setLabelFor(maxPoolSizeTextField);
        maxPoolSizeLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Max_Pool_Size_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(maxPoolSizeLabel, gridBagConstraints);
        maxPoolSizeLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Max_Pool_Size_Acsbl_Name"));
        maxPoolSizeLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Max_Pool_Size_Acsbl_Desc"));

        maxPoolSizeTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Max_Pool_Size_Tool_Tip"));
        maxPoolSizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                maxPoolSizeKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(maxPoolSizeTextField, gridBagConstraints);
        maxPoolSizeTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Max_Pool_Size_Acsbl_Name"));
        maxPoolSizeTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Max_Pool_Size_Acsbl_Desc"));

        poolIdleTimeoutInSecondsLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Pool_Idle_Timeout_In_Seconds").charAt(0));
        poolIdleTimeoutInSecondsLabel.setLabelFor(poolIdleTimeoutInSecondsTextField);
        poolIdleTimeoutInSecondsLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Pool_Idle_Timeout_In_Seconds_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(poolIdleTimeoutInSecondsLabel, gridBagConstraints);
        poolIdleTimeoutInSecondsLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pool_Idle_Timeout_In_Seconds_Acsbl_Name"));
        poolIdleTimeoutInSecondsLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pool_Idle_Timeout_In_Seconds_Acsbl_Desc"));

        poolIdleTimeoutInSecondsTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pool_Idle_Timeout_In_Seconds_Tool_Tip"));
        poolIdleTimeoutInSecondsTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                poolIdleTimeoutInSecondsKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(poolIdleTimeoutInSecondsTextField, gridBagConstraints);
        poolIdleTimeoutInSecondsTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pool_Idle_Timeout_In_Seconds_Acsbl_Name"));
        poolIdleTimeoutInSecondsTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pool_Idle_Timeout_In_Seconds_Acsbl_Desc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(fillerPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void poolIdleTimeoutInSecondsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_poolIdleTimeoutInSecondsKeyReleased
        BaseEjb theBean = masterPanel.getBean();
        if(theBean != null) {
            BeanPool beanPool = theBean.getBeanPool();
            String newPoolIdleTimeout = poolIdleTimeoutInSecondsTextField.getText();
            String oldPoolIdleTimeout = beanPool.getPoolIdleTimeoutInSeconds();

            if(!Utils.strEquivalent(oldPoolIdleTimeout, newPoolIdleTimeout)) {
                if(Utils.notEmpty(newPoolIdleTimeout)) {
                    beanPool.setPoolIdleTimeoutInSeconds(newPoolIdleTimeout);
                } else {
                    beanPool.setPoolIdleTimeoutInSeconds(null);
                }

                theBean.firePropertyChange("beanPoolIdleTimeout", oldPoolIdleTimeout, newPoolIdleTimeout); // NOI18N
                masterPanel.validateField(BaseEjb.FIELD_BEANPOOL_IDLETIMEOUT);
            }
        }
    }//GEN-LAST:event_poolIdleTimeoutInSecondsKeyReleased

    private void maxPoolSizeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxPoolSizeKeyReleased
        BaseEjb theBean = masterPanel.getBean();
        if(theBean != null) {
            BeanPool beanPool = theBean.getBeanPool();
            String newMaxPoolSize = maxPoolSizeTextField.getText();
            String oldMaxPoolSize = beanPool.getMaxPoolSize();

            if(!Utils.strEquivalent(oldMaxPoolSize, newMaxPoolSize)) {
                if(Utils.notEmpty(newMaxPoolSize)) {
                    beanPool.setMaxPoolSize(newMaxPoolSize);
                } else {
                    beanPool.setMaxPoolSize(null);
                }

                theBean.firePropertyChange("beanPoolMaxPoolSize", oldMaxPoolSize, newMaxPoolSize); // NOI18N
                masterPanel.validateField(BaseEjb.FIELD_BEANPOOL_MAXPOOLSIZE);
            }
        }
    }//GEN-LAST:event_maxPoolSizeKeyReleased

    private void resizeQuantityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resizeQuantityKeyReleased
        BaseEjb theBean = masterPanel.getBean();
        if(theBean != null) {
            BeanPool beanPool = theBean.getBeanPool();
            String newResizeQuantity = resizeQuantityTextField.getText();
            String oldResizeQuantity = beanPool.getResizeQuantity();

            if(!Utils.strEquivalent(oldResizeQuantity, newResizeQuantity)) {
                if(Utils.notEmpty(newResizeQuantity)) {
                    beanPool.setResizeQuantity(newResizeQuantity);
                } else {
                    beanPool.setResizeQuantity(null);
                }

                theBean.firePropertyChange("beanPoolResizeQuantity", oldResizeQuantity, newResizeQuantity); // NOI18N
                masterPanel.validateField(BaseEjb.FIELD_BEANPOOL_RESIZEQUANTITY);
            }
        }
    }//GEN-LAST:event_resizeQuantityKeyReleased

    private void steadyPoolSizeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_steadyPoolSizeKeyReleased
        BaseEjb theBean = masterPanel.getBean();
        if(theBean != null) {
            BeanPool beanPool = theBean.getBeanPool();
            String newSteadyPoolSize = steadyPoolSizeTextField.getText();
            String oldSteadyPoolSize = beanPool.getSteadyPoolSize();

            if(!Utils.strEquivalent(oldSteadyPoolSize, newSteadyPoolSize)) {
                if(Utils.notEmpty(newSteadyPoolSize)) {
                    beanPool.setSteadyPoolSize(newSteadyPoolSize);
                } else {
                    beanPool.setSteadyPoolSize(null);
                }

                theBean.firePropertyChange("beanPoolSteadyPoolSize", oldSteadyPoolSize, newSteadyPoolSize); // NOI18N
                masterPanel.validateField(BaseEjb.FIELD_BEANPOOL_STEADYPOOLSIZE);
            }
        }
    }//GEN-LAST:event_steadyPoolSizeKeyReleased
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JLabel maxPoolSizeLabel;
    private javax.swing.JTextField maxPoolSizeTextField;
    private javax.swing.JLabel poolIdleTimeoutInSecondsLabel;
    private javax.swing.JTextField poolIdleTimeoutInSecondsTextField;
    private javax.swing.JLabel resizeQuantityLabel;
    private javax.swing.JTextField resizeQuantityTextField;
    private javax.swing.JLabel steadyPoolSizeLabel;
    private javax.swing.JTextField steadyPoolSizeTextField;
    // End of variables declaration//GEN-END:variables

    private void initUserComponents() {
        putClientProperty(EjbCustomizer.PARTITION_KEY, ValidationError.PARTITION_EJB_BEANPOOL);
    }
    
    public void initFields(BeanPool beanPool) {
        if(beanPool != null) {
            steadyPoolSizeTextField.setText(beanPool.getSteadyPoolSize());
            resizeQuantityTextField.setText(beanPool.getResizeQuantity());
            maxPoolSizeTextField.setText(beanPool.getMaxPoolSize());
            poolIdleTimeoutInSecondsTextField.setText(beanPool.getPoolIdleTimeoutInSeconds());
        }
    }    
}
