/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.platform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicVisualPanel;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * First panel from <em>Adding New Platform</em> wizard panels. Allows user to
 * choose platform directory.
 *
 * @author Martin Krauskopf
 */
public class PlatformChooserVisualPanel extends BasicVisualPanel
        implements PropertyChangeListener {
    
    /** Creates new form BasicInfoVisualPanel */
    public PlatformChooserVisualPanel(WizardDescriptor setting) {
        super(setting);
        initComponents();
        platformChooser.setAcceptAllFileFilterUsed(false);
        platformChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f)  {
                return f.isDirectory();
            }
            public String getDescription() {
                return NbBundle.getMessage(PlatformChooserVisualPanel.class, "CTL_PlatformFolder"); // NOI18N
            }
        });
        platformChooser.addPropertyChangeListener(this);
        setName(NbPlatformCustomizer.CHOOSER_STEP);
    }
    
    /** Stores collected data into model. */
    void storeData() {
        File file = platformChooser.getSelectedFile();
        if (file != null) {
            getSetting().putProperty(NbPlatformCustomizer.PLAF_DIR_PROPERTY,
                    file.getAbsolutePath());
        } // when wizard is cancelled file is null
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (propName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            File plafDir = platformChooser.getSelectedFile();
            if (/* #60133 */ plafDir != null && NbPlatform.isPlatformDirectory(plafDir)) {
                try {
                    plafLabelValue.setText(NbPlatform.computeDisplayName(plafDir));
                } catch (IOException e) {
                    plafLabelValue.setText(plafDir.getAbsolutePath());
                }
                setValid(Boolean.TRUE);
            } else {
                setValid(Boolean.FALSE);
                plafLabelValue.setText(null);
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        infoPanel = new javax.swing.JPanel();
        inner = new javax.swing.JPanel();
        plafLabel = new javax.swing.JLabel();
        plafLabelValue = new javax.swing.JLabel();
        platformChooser = new javax.swing.JFileChooser();

        infoPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        inner.setLayout(new java.awt.GridLayout(2, 1, 0, 6));

        org.openide.awt.Mnemonics.setLocalizedText(plafLabel, org.openide.util.NbBundle.getMessage(PlatformChooserVisualPanel.class, "LBL_PlatformName_P"));
        inner.add(plafLabel);

        inner.add(plafLabelValue);

        infoPanel.add(inner);

        setLayout(new java.awt.BorderLayout());

        platformChooser.setAccessory(infoPanel);
        platformChooser.setControlButtonsAreShown(false);
        platformChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        add(platformChooser, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel inner;
    private javax.swing.JLabel plafLabel;
    private javax.swing.JLabel plafLabelValue;
    private javax.swing.JFileChooser platformChooser;
    // End of variables declaration//GEN-END:variables
}
