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

package org.netbeans.modules.subversion.ui.checkout;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 *
 * @author  Petr Kuzel
 */
final class CheckoutCompletedPanel extends javax.swing.JPanel {
    
    /** Creates new form CheckoutCompletedPanel */
    public CheckoutCompletedPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CheckoutCompletedPanel.class, "BK3001"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
        add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(againCheckBox, org.openide.util.NbBundle.getMessage(CheckoutCompletedPanel.class, "BK3002"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        add(againCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(openButton, org.openide.util.NbBundle.getMessage(CheckoutCompletedPanel.class, "BK3003"));
        openButton.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/subversion/ui/checkout/Bundle").getString("TT_OpenProject"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 3);
        add(openButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(createButton, org.openide.util.NbBundle.getMessage(CheckoutCompletedPanel.class, "BK3004"));
        createButton.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/subversion/ui/checkout/Bundle").getString("TT_CreateProject"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
        add(createButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(closeButton, org.openide.util.NbBundle.getMessage(CheckoutCompletedPanel.class, "BK3005"));
        closeButton.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/subversion/ui/checkout/Bundle").getString("TT_Close"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(closeButton, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JCheckBox againCheckBox = new javax.swing.JCheckBox();
    final javax.swing.JButton closeButton = new javax.swing.JButton();
    final javax.swing.JButton createButton = new javax.swing.JButton();
    final javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    final javax.swing.JButton openButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables
    
}
