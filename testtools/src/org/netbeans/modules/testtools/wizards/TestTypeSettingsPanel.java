/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.testtools.wizards;

/*
 * TestTypeSettingsPanel.java
 *
 * Created on April 10, 2002, 1:44 PM
 */

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import javax.swing.event.ChangeListener;
import org.openide.loaders.TemplateWizard;
import java.awt.CardLayout;
import org.openide.util.Utilities;

/**
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 */
public class TestTypeSettingsPanel extends javax.swing.JPanel implements WizardDescriptor.Panel {
    
    private boolean stop=true;
    private String name=null;
    
    /** Creates new form TestTypePanel */
    public TestTypeSettingsPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        defaultCheck = new javax.swing.JCheckBox();
        systemLabel = new javax.swing.JLabel();
        sdiRadio = new javax.swing.JRadioButton();
        mdiRadio = new javax.swing.JRadioButton();
        jemmyCheck = new javax.swing.JCheckBox();
        stopLabel = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        panel.setLayout(new java.awt.GridBagLayout());

        defaultCheck.setMnemonic('d');
        defaultCheck.setSelected(true);
        defaultCheck.setText("Set Test Type as default in Test Workspace");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(defaultCheck, gridBagConstraints);

        systemLabel.setText("Windows System: ");
        systemLabel.setDisplayedMnemonic(87);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 4, 4);
        panel.add(systemLabel, gridBagConstraints);

        sdiRadio.setMnemonic('s');
        sdiRadio.setSelected(true);
        sdiRadio.setText("SDI (Multiple Smaller Windows Mode)");
        buttonGroup.add(sdiRadio);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(sdiRadio, gridBagConstraints);

        mdiRadio.setMnemonic('m');
        mdiRadio.setText("MDI (Full Screen Mode)");
        buttonGroup.add(mdiRadio);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        panel.add(mdiRadio, gridBagConstraints);

        jemmyCheck.setMnemonic('j');
        jemmyCheck.setSelected(true);
        jemmyCheck.setText("Use Jemmy and Jelly libraries");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(jemmyCheck, gridBagConstraints);

        add(panel, "ok");

        stopLabel.setText("Test Type with this name already exists or has invalid name.");
        stopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(stopLabel, "stop");

    }//GEN-END:initComponents

    public void addChangeListener(javax.swing.event.ChangeListener l) {
    }    
    
    public java.awt.Component getComponent() {
        return this;
    }    
    
    public org.openide.util.HelpCtx getHelp() {
        return new HelpCtx(TestTypeSettingsPanel.class);
    }
    
    public void readSettings(Object obj) {
        TemplateWizard wizard=(TemplateWizard)obj;
        WizardSettings set=WizardSettings.get(obj);
        try {
            if (set.startFromType) {
                name=wizard.getTargetName();
                stop=(name!=null) && (!Utilities.isJavaIdentifier(name));
                if (name==null)
                    name=wizard.getTemplate().getPrimaryFile().getName();
                stop=stop||WizardIterator.detectTestType(wizard.getTargetFolder(), name);
            } else {
                name=set.typeName;
                stop=(name!=null) && (!Utilities.isJavaIdentifier(name));
                if (name==null)
                    name=set.typeTemplate.getPrimaryFile().getName();
            }
        } catch (Exception e) {}
        if (stop)
            ((CardLayout)getLayout()).show(this, "stop");
        else {
            ((CardLayout)getLayout()).show(this, "ok");
            jemmyCheck.setSelected(set.typeUseJemmy);
            sdiRadio.setSelected(set.typeSDI);
            mdiRadio.setSelected(!set.typeSDI);
        }
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener l) {
    }
    
    public void storeSettings(Object obj) {
        WizardSettings set=WizardSettings.get(obj);
        set.typeUseJemmy=jemmyCheck.isSelected();
        set.typeSDI=sdiRadio.isSelected();
        if (defaultCheck.isSelected())
            set.defaultType=name;
    }

    public boolean isValid() {
        return !stop;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton mdiRadio;
    private javax.swing.JLabel stopLabel;
    private javax.swing.JCheckBox defaultCheck;
    private javax.swing.JPanel panel;
    private javax.swing.JLabel systemLabel;
    private javax.swing.JRadioButton sdiRadio;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JCheckBox jemmyCheck;
    // End of variables declaration//GEN-END:variables
    
}
