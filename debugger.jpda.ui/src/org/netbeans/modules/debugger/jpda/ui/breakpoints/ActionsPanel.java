/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.debugger.jpda.ui.breakpoints;

import java.awt.Dimension;

import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import org.netbeans.api.debugger.jpda.JPDABreakpoint;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * @author  jj97931
 */
public class ActionsPanel extends javax.swing.JPanel {

    // [TODO] move property name constant to JPDABreakpoint
    private static final String DEFAULT_SUSPEND_ACTION = "default.suspend.action"; // NOI18N
    
    private JPDABreakpoint  breakpoint;
    private int defaultSuspendAction;
    private int checkedSuspendAction;
    private Preferences preferences = NbPreferences.forModule(JPDABreakpoint.class).node("debugging"); // NOI18N

    /** Creates new form LineBreakpointPanel */
    public ActionsPanel (JPDABreakpoint b) {
        breakpoint = b;
        initComponents ();

        ResourceBundle bundle = NbBundle.getBundle(ActionsPanel.class);
        org.openide.awt.Mnemonics.setLocalizedText(defaultActionCheckBox, bundle.getString("LBL_Use_As_Default_Option")); // NOI18N
        defaultActionCheckBox.setToolTipText(bundle.getString("TTT_Use_As_Default_Option"));
        checkBoxPanel.setPreferredSize(defaultActionCheckBox.getPreferredSize());
        
        cbSuspend.addItem (bundle.getString("LBL_CB_Actions_Panel_Suspend_None"));
        cbSuspend.addItem (bundle.getString("LBL_CB_Actions_Panel_Suspend_Current"));
        cbSuspend.addItem (bundle.getString("LBL_CB_Actions_Panel_Suspend_All"));
        switch (b.getSuspend ()) {
            case JPDABreakpoint.SUSPEND_NONE:
                cbSuspend.setSelectedIndex (0);
                break;
            case JPDABreakpoint.SUSPEND_EVENT_THREAD:
                cbSuspend.setSelectedIndex (1);
                break;
            case JPDABreakpoint.SUSPEND_ALL:
                cbSuspend.setSelectedIndex (2);
                break;
        }
        defaultSuspendAction = preferences.getInt(DEFAULT_SUSPEND_ACTION, 1);
        checkedSuspendAction = defaultSuspendAction;
        
        if (defaultSuspendAction == cbSuspend.getSelectedIndex()) {
            defaultActionCheckBox.setVisible(false);
        } else {
            defaultActionCheckBox.setVisible(true);
            defaultActionCheckBox.setSelected(false);
        }
        
        if (b.getPrintText () != null)
            tfPrintText.setText (b.getPrintText ());
        tfPrintText.setPreferredSize(new Dimension(
                30*tfPrintText.getFontMetrics(tfPrintText.getFont()).charWidth('W'),
                tfPrintText.getPreferredSize().height));
        tfPrintText.setCaretPosition(0);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tfPrintText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cbSuspend = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        checkBoxPanel = new javax.swing.JPanel();
        defaultActionCheckBox = new javax.swing.JCheckBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle"); // NOI18N
        setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("L_Actions_Panel_BorderTitle"))); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        tfPrintText.setToolTipText(bundle.getString("TTT_TF_Actions_Panel_Print_Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(tfPrintText, gridBagConstraints);
        tfPrintText.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TF_Actions_Panel_Print_Text")); // NOI18N

        jLabel1.setLabelFor(cbSuspend);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, bundle.getString("L_Actions_Panel_Suspend")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_L_Actions_Panel_Suspend")); // NOI18N

        cbSuspend.setToolTipText(bundle.getString("TTT_CB_Actions_Panel_Suspend")); // NOI18N
        cbSuspend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSuspendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(cbSuspend, gridBagConstraints);
        cbSuspend.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_CB_Actions_Panel_Suspend")); // NOI18N

        jLabel2.setLabelFor(tfPrintText);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, bundle.getString("L_Actions_Panel_Print_Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ActionsPanel.class, "ACSD_PrintText")); // NOI18N

        checkBoxPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(defaultActionCheckBox, "jCheckBox1");
        defaultActionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultActionCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        checkBoxPanel.add(defaultActionCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(checkBoxPanel, gridBagConstraints);

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ActionsPanel.class, "ACSD_Actions")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void defaultActionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultActionCheckBoxActionPerformed
    checkedSuspendAction = cbSuspend.getSelectedIndex();
}//GEN-LAST:event_defaultActionCheckBoxActionPerformed

private void cbSuspendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSuspendActionPerformed
    int selectedIndex = cbSuspend.getSelectedIndex();
    if (defaultSuspendAction == selectedIndex) {
        defaultActionCheckBox.setVisible(false);
    } else {
        defaultActionCheckBox.setVisible(true);
        defaultActionCheckBox.setSelected(false);
    }
    checkedSuspendAction = defaultSuspendAction;
}//GEN-LAST:event_cbSuspendActionPerformed
    
    /**
     * Called when "Ok" button is pressed.
     */
    public void ok () {
        String printText = tfPrintText.getText ();
        if (printText.trim ().length () > 0)
            breakpoint.setPrintText (printText.trim ());
        else
            breakpoint.setPrintText (null);
        
        switch (cbSuspend.getSelectedIndex ()) {
            case 0:
                breakpoint.setSuspend (JPDABreakpoint.SUSPEND_NONE);
                break;
            case 1:
                breakpoint.setSuspend (JPDABreakpoint.SUSPEND_EVENT_THREAD);
                break;
            case 2:
                breakpoint.setSuspend (JPDABreakpoint.SUSPEND_ALL);
                break;
        }
        if (checkedSuspendAction != defaultSuspendAction) {
            preferences.putInt(DEFAULT_SUSPEND_ACTION, checkedSuspendAction);
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbSuspend;
    private javax.swing.JPanel checkBoxPanel;
    private javax.swing.JCheckBox defaultActionCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField tfPrintText;
    // End of variables declaration//GEN-END:variables
    
}
