/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.java.source.tasklist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.java.source.usages.RepositoryUpdater;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Jan Lahoda
 */
public class TasklistOptionsPanel extends javax.swing.JPanel {
    
    private ChangeSupport cs;
    private Map<JCheckBox, String> compilerSettings;
    
    /** Creates new form TasklistOptionsPanel */
    public TasklistOptionsPanel() {
        cs = new ChangeSupport(this);
        initComponents();
        
        compilerSettings = new HashMap<JCheckBox, String>();
        
        compilerSettings.put(enableLint, CompilerSettings.ENABLE_LINT);
        compilerSettings.put(enableLintDeprecated, CompilerSettings.ENABLE_LINT_DEPRECATION);
        compilerSettings.put(enableLintFallThrough, CompilerSettings.ENABLE_LINT_FALLTHROUGH);
        compilerSettings.put(enableLintFinally, CompilerSettings.ENABLE_LINT_FINALLY);
        compilerSettings.put(enableLintSerial, CompilerSettings.ENABLE_LINT_SERIAL);
        compilerSettings.put(enableLintUnchecked, CompilerSettings.ENABLE_LINT_UNCHECKED);
        compilerSettings.put(enableLintCast, CompilerSettings.ENABLE_LINT_CAST);
        compilerSettings.put(enableLintDivZero, CompilerSettings.ENABLE_LINT_DIVZERO);
        compilerSettings.put(enableLintEmpty, CompilerSettings.ENABLE_LINT_EMPTY);
        compilerSettings.put(enableLintOverrides, CompilerSettings.ENABLE_LINT_OVERRIDES);
    }
    
    void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        enableTasklist = new javax.swing.JCheckBox();
        enableDependencies = new javax.swing.JCheckBox();
        enableBadges = new javax.swing.JCheckBox();
        enableLint = new javax.swing.JCheckBox();
        enableLintDeprecated = new javax.swing.JCheckBox();
        enableLintUnchecked = new javax.swing.JCheckBox();
        enableLintFallThrough = new javax.swing.JCheckBox();
        enableLintSerial = new javax.swing.JCheckBox();
        enableLintFinally = new javax.swing.JCheckBox();
        enableLintCast = new javax.swing.JCheckBox();
        enableLintEmpty = new javax.swing.JCheckBox();
        enableLintOverrides = new javax.swing.JCheckBox();
        enableLintDivZero = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(enableTasklist, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("jCheckBox1.text")); // NOI18N
        enableTasklist.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableTasklist.setMargin(new java.awt.Insets(0, 0, 0, 0));
        enableTasklist.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                enableTasklistStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(enableTasklist, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableDependencies, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("jCheckBox2.text")); // NOI18N
        enableDependencies.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableDependencies.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableDependencies, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableBadges, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("jCheckBox1.text_1")); // NOI18N
        enableBadges.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableBadges.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableBadges, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLint, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLint.text")); // NOI18N
        enableLint.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLint.setMargin(new java.awt.Insets(0, 0, 0, 0));
        enableLint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                enableLintStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 12, 0);
        add(enableLint, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintDeprecated, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintDeprecated.text")); // NOI18N
        enableLintDeprecated.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintDeprecated.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintDeprecated, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintUnchecked, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintUnchecked.text")); // NOI18N
        enableLintUnchecked.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintUnchecked.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintUnchecked, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintFallThrough, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintFallThrough.text")); // NOI18N
        enableLintFallThrough.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintFallThrough.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintFallThrough, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintSerial, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintSerial.text")); // NOI18N
        enableLintSerial.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintSerial.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintSerial, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintFinally, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintFinally.text")); // NOI18N
        enableLintFinally.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintFinally.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintFinally, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintCast, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintCast.text")); // NOI18N
        enableLintCast.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintCast.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintCast, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintEmpty, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintEmpty.text")); // NOI18N
        enableLintEmpty.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintEmpty.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintEmpty, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintOverrides, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintOverrides.text")); // NOI18N
        enableLintOverrides.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintOverrides.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintOverrides, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(enableLintDivZero, org.openide.util.NbBundle.getBundle(TasklistOptionsPanel.class).getString("enableLintDivZero.text")); // NOI18N
        enableLintDivZero.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLintDivZero.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 0);
        add(enableLintDivZero, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void enableLintStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_enableLintStateChanged
    // TODO add your handling code here:
    updateCompilerSettingsEnabled();
}//GEN-LAST:event_enableLintStateChanged

private void enableTasklistStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_enableTasklistStateChanged
    // TODO add your handling code here:
    updateEnabled();
}//GEN-LAST:event_enableTasklistStateChanged
    
    //XXX: shoudl fire changes
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox enableBadges;
    private javax.swing.JCheckBox enableDependencies;
    private javax.swing.JCheckBox enableLint;
    private javax.swing.JCheckBox enableLintCast;
    private javax.swing.JCheckBox enableLintDeprecated;
    private javax.swing.JCheckBox enableLintDivZero;
    private javax.swing.JCheckBox enableLintEmpty;
    private javax.swing.JCheckBox enableLintFallThrough;
    private javax.swing.JCheckBox enableLintFinally;
    private javax.swing.JCheckBox enableLintOverrides;
    private javax.swing.JCheckBox enableLintSerial;
    private javax.swing.JCheckBox enableLintUnchecked;
    private javax.swing.JCheckBox enableTasklist;
    // End of variables declaration//GEN-END:variables
    
    public void setTasklistEnabled(boolean enabled) {
        enableTasklist.setSelected(enabled);
        updateEnabled();
    }
    
    public void setDependenciesEnabled(boolean enabled) {
        enableDependencies.setSelected(enabled);
    }
    
    public void setBadgesEnabled(boolean enabled) {
        enableBadges.setSelected(enabled);
    }
    
    public boolean getTasklistEnabled() {
        return enableTasklist.isSelected();
    }

    public boolean getDependenciesEnabled() {
        return enableDependencies.isSelected();
    }

    public boolean getBadgesEnabled() {
        return enableBadges.isSelected();
    }

    private void updateEnabled() {
        enableDependencies.setEnabled(enableTasklist.isSelected());
        enableBadges.setEnabled(enableTasklist.isSelected());
    }
    
    public void fillInCompilerSettings() {
        Preferences p = CompilerSettings.getNode();
        
        for (Entry<JCheckBox, String> e : compilerSettings.entrySet()) {
            e.getKey().setSelected(CompilerSettings.get(p, e.getValue()));
        }
        
        updateCompilerSettingsEnabled();
    }
    
    public boolean isCompilerSettingsChanged() {
        Preferences p = CompilerSettings.getNode();
        
        for (Entry<JCheckBox, String> e : compilerSettings.entrySet()) {
            if (e.getKey().isSelected() != CompilerSettings.get(p, e.getValue()))
                return true;
        }
        
        return false;
    }
    
    public void storeCompilerSettings() {
        Preferences p = CompilerSettings.getNode();
        
        for (Entry<JCheckBox, String> e : compilerSettings.entrySet()) {
            p.putBoolean(e.getValue(), e.getKey().isSelected());
        }
        RepositoryUpdater.getDefault().rebuildAll(false);
    }
    
    private void updateCompilerSettingsEnabled() {
        boolean enable = enableLint.isSelected();
        
        for (JCheckBox b : compilerSettings.keySet()) {
            if (b != enableLint)
                b.setEnabled(enable);
        }
    }
}
