/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.examples;

import javax.swing.JPanel;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class PanelConfigureProjectVisual extends JPanel implements HelpCtx.Provider {

    private PanelConfigureProject panel;

    private PanelProjectLocationVisual projectLocationPanel;
    private PanelOptionsVisual optionsPanel;

    /** Creates new form PanelInitProject */
    public PanelConfigureProjectVisual(PanelConfigureProject panel) {
        this.panel = panel;
        initComponents();
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PanelConfigureProjectVisual.class, "ACS_NWP1_NamePanel_A11YDesc"));  // NOI18N
        
        projectLocationPanel = new PanelProjectLocationVisual(panel);
        locationContainer.add(projectLocationPanel, java.awt.BorderLayout.NORTH);

        optionsPanel = new PanelOptionsVisual(panel);
        optionsContainer.add(optionsPanel, java.awt.BorderLayout.NORTH);

        // Provide a name in the title bar.
        setName(NbBundle.getMessage(PanelConfigureProjectVisual.class, "LBL_NWP1_ProjectTitleName")); //NOI18N
        putClientProperty ("NewProjectWizard_Title", NbBundle.getMessage(PanelConfigureProjectVisual.class, "TXT_NewWebApp")); //NOI18N
    }

    boolean valid(WizardDescriptor wizardDescriptor) {
        return projectLocationPanel.valid(wizardDescriptor) && optionsPanel.valid(wizardDescriptor);
    }

    void read (WizardDescriptor d) {
        projectLocationPanel.read(d);
        optionsPanel.read(d);
    }

    void store(WizardDescriptor d) {
        projectLocationPanel.store(d);
        optionsPanel.store(d);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        locationContainer = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        optionsContainer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        setRequestFocusEnabled(false);
        locationContainer.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(locationContainer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 0);
        add(jSeparator1, gridBagConstraints);

        optionsContainer.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(optionsContainer, gridBagConstraints);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel locationContainer;
    private javax.swing.JPanel optionsContainer;
    // End of variables declaration//GEN-END:variables

    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(PanelConfigureProjectVisual.class);
    }    
}
