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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.earproject.ui.wizards;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

final class PanelProjectImportVisual extends SettingsPanel implements DocumentListener {
    private static final long serialVersionUID = 1L;
    
    private final PanelConfigureProject panel;
    private final String propNameIndex;
    private final ResourceBundle customBundle;
    private final boolean importStyle;
    private String lastComputedPrjName;
    private String lastComputedPrjFolder;
    private boolean ignoreEvent;

    // wheter this panel is being shown first time
    private boolean firstTime = true;
    
    /** Creates new form PanelProjectLocationVisual */
    public PanelProjectImportVisual(PanelConfigureProject panel, String propNameIndex, ResourceBundle customBundle, boolean importStyle) {
        this.customBundle = customBundle;
        initComponents();
        this.panel = panel;
        this.propNameIndex = propNameIndex;
        
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener(this);
        projectLocationTextField.getDocument().addDocumentListener(this);
        this.importStyle = importStyle;
        createdFolderTextField.setEditable(importStyle);
        if (importStyle) {
            createdFolderTextField.getDocument().addDocumentListener(this);
        }
        browseFolderButton.setVisible(importStyle);
    }
    
    void addNameListener(DocumentListener listener) {
        projectNameTextField.getDocument().addDocumentListener(listener);        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseLocationButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();
        browseFolderButton = new javax.swing.JButton();
        instructionLabel = new javax.swing.JLabel();
        descriptionArea = new javax.swing.JTextArea();
        spaceFiller = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_NWP1_ProjectName_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(projectNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 0);
        add(projectNameTextField, gridBagConstraints);
        projectNameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACS_LBL_NWP1_ProjectName_A11YDesc")); // NOI18N

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_ImportLocation_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(projectLocationLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 0);
        add(projectLocationTextField, gridBagConstraints);
        projectLocationTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACS_LBL_NPW1_ProjectLocation_A11YDesc")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseLocationButton, org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_NWP1_BrowseLocation_Button")); // NOI18N
        browseLocationButton.setActionCommand("BROWSE");
        browseLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLocationAction(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(browseLocationButton, gridBagConstraints);
        browseLocationButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACS_LBL_NWP1_BrowseLocation_A11YDesc")); // NOI18N

        createdFolderLabel.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel, org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_NWP1_CreatedProjectFolder_Lablel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(createdFolderLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(createdFolderTextField, gridBagConstraints);
        createdFolderTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACS_LBL_NWP1_CreatedProjectFolder_A11YDesc")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseFolderButton, org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_NWP1_BrowseProjectFolder_Button")); // NOI18N
        browseFolderButton.setActionCommand("BROWSE");
        browseFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFolderAction(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(browseFolderButton, gridBagConstraints);
        browseFolderButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACSN_LBL_NWP1_BrowseProjectFolder_Button")); // NOI18N
        browseFolderButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACSD_LBL_NWP1_BrowseProjectFolder_Button")); // NOI18N

        instructionLabel.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(instructionLabel, org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_ImportInstructions2")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(instructionLabel, gridBagConstraints);

        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(2);
        descriptionArea.setText(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "LBL_ImportInstructions1")); // NOI18N
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(descriptionArea, gridBagConstraints);
        descriptionArea.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACSN_LBL_ImportInstructions1")); // NOI18N
        descriptionArea.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectImportVisual.class, "ACSD_LBL_ImportInstructions1")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(spaceFiller, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void browseFolderAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFolderAction
        String command = evt.getActionCommand();
        
        if ("BROWSE".equals(command)) { //NOI18N
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(customBundle.getString("LBL_NWP1_BrowseProjectFolder")); //NOI18N
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = getCreatedFolderText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            } else {
                chooser.setSelectedFile(ProjectChooser.getProjectsFolder());
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                createdFolderTextField.setText(projectDir.getAbsolutePath());
            }            
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_browseFolderAction

    private void browseLocationAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLocationAction
        String command = evt.getActionCommand();
        
        if ("BROWSE".equals(command)) { //NOI18N
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(customBundle.getString("LBL_NWP1_SelectProjectLocation")); //NOI18N
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            } else {
                chooser.setSelectedFile(ProjectChooser.getProjectsFolder());
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText(projectDir.getAbsolutePath());
            }            
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_browseLocationAction
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        String sourceLocationPath = projectLocationTextField.getText().trim();
        if (sourceLocationPath.length() == 0
                || !new File(sourceLocationPath).isDirectory()) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", //NOI18N
                    customBundle.getString("MSG_ProvideExistingSourcesLocation")); //NOI18N
            return false;
        }
        
        if (!SettingsPanel.isValidProjectName(projectNameTextField.getText())) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", //NOI18N
                    customBundle.getString("MSG_IllegalProjectName")); //NOI18N
            return false;
        }
        
        if (importStyle && getCreatedFolderText().length() == 0) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", //NOI18N
                    customBundle.getString("MSG_EmptyProjectFolder"));//NOI18N
            return false;
        }
        
        File destParent = SettingsPanel.findExistingParent(getCreatedFolderText());
        
        File destDir = new File(getCreatedFolderText());
        if (destDir.exists() && !destDir.isDirectory()) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", //NOI18N
            MessageFormat.format(customBundle.getString("MSG_WillOverwrite"),new Object[] {destDir.getAbsolutePath() })); //NOI18N
            return false;
        }
        File buildXml = new File(getCreatedFolderText()+
            File.separator+"build.xml"); // NOI18N
        if (buildXml.exists()) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", //NOI18N
            MessageFormat.format(customBundle.getString("MSG_WillOverwrite"),new Object[] {buildXml.getAbsolutePath() })); //NOI18N
            return false;
        }
        File tFile = null; //new File(getCreatedFolderText()+
//            File.separator+"src"+File.separator+"conf"+File.separator+ //NOI18N
//            "application.xml"); //NOI18N
//        if (tFile.exists()) {
//            wizardDescriptor.putProperty("WizardPanel_errorMessage",
//            MessageFormat.format(customBundle.getString("MSG_WillOverwrite"),new Object[] {tFile.getAbsolutePath()})); //NOI18N
//            return false;
//        }
        
        tFile = new File(getCreatedFolderText()+
            File.separator+"nbproject"+File.separator+"project.xml"); //NOI18N
        if (tFile.exists()) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", // NOI18N
            MessageFormat.format(customBundle.getString("MSG_AlreadyExixts"),new Object[] {tFile.getAbsolutePath()})); //NOI18N
            return false;
        }
        tFile = new File(getCreatedFolderText()+
            File.separator+"nbproject"+File.separator+"project.properties"); //NOI18N
        if (tFile.exists()) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", // NOI18N
            MessageFormat.format(customBundle.getString("MSG_AlreadyExixts"),new Object[] {tFile.getAbsolutePath()})); //NOI18N
            return false;
        }
        if (null != destParent && destParent.exists() && !destParent.canWrite()) {
            // Read only project location
            wizardDescriptor.putProperty("WizardPanel_errorMessage", // NOI18N
                MessageFormat.format(customBundle.getString("MSG_ProjectLocationRO"),new Object[] {destParent.getAbsolutePath()})); //NOI18N
            return false;
        }
                
        wizardDescriptor.putProperty("WizardPanel_errorMessage", ""); //NOI18N
        return true;
    }
    
    void store(WizardDescriptor d) {
        String name = projectNameTextField.getText().trim();
        
        d.putProperty(WizardProperties.PROJECT_DIR, new File(getCreatedFolderText()));
        d.putProperty(WizardProperties.NAME, name);
        File srcRoot = null;
        String srcPath = projectLocationTextField.getText();
        if (srcPath.length() > 0) {
            srcRoot = FileUtil.normalizeFile(new File(srcPath));
        }
        d.putProperty(WizardProperties.SOURCE_ROOT, srcRoot);
    }
    
    void read(WizardDescriptor settings) {
        if (!firstTime) {
            return;
        }
        firstTime = false;
        File projectLocation = getPreferredProjectLocation(settings);
        
        if (null != projectLocation) {
            //projectLocationTextField.setText(projectLocation.getAbsolutePath());
            String projectName = (String) settings.getProperty(WizardProperties.NAME);
            if (projectName == null) {
                // XXX re-examine this
                int baseCount = 1; // FoldersListSettings.getDefault().getNewProjectCount() + 1;
                String formater = customBundle.getString( "TXT_DefaultProjectName");
                while ((projectName = validFreeProjectName(projectLocation, formater, baseCount)) == null) {
                    baseCount++;
                }
                //settings.putProperty(NewEarProjectWizardIterator.PROP_NAME_INDEX, new Integer(baseCount));
                settings.putProperty(propNameIndex, baseCount);
            }
            //projectNameTextField.setText(projectName);
            //if (importStyle) {
            //    createdFolderTextField.setText(System.getProperty("user.home") +
            //            File.separator+projectName);
            //}
        }
        
        projectNameTextField.selectAll();
    }
    
    private static File getPreferredProjectLocation(final WizardDescriptor settings) {
        File projectLocation = null;
        FileObject existingSourcesFO = Templates.getExistingSourcesFolder(settings);
        if (existingSourcesFO != null) {
            File existingSourcesFile = FileUtil.toFile(existingSourcesFO);
            if (existingSourcesFile != null && existingSourcesFile.isDirectory()) {
                projectLocation = existingSourcesFile;
            }
        } else {
            projectLocation = (File) settings.getProperty(WizardProperties.PROJECT_DIR);
            if (projectLocation == null) {
                projectLocation = ProjectChooser.getProjectsFolder();
            } else {
                projectLocation = projectLocation.getParentFile();
            }
        }
        return projectLocation;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseFolderButton;
    private javax.swing.JButton browseLocationButton;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JLabel instructionLabel;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JPanel spaceFiller;
    // End of variables declaration//GEN-END:variables
        
//    private static JFileChooser createChooser() {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        chooser.setAcceptAllFileFilterUsed(false);
//        
//        return chooser;
//    }

    private String validFreeProjectName(final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format(formater, index);                
        File file = new File(parentFolder, name);
        return file.exists() ? null : name;
    }

    // Implementation of DocumentListener --------------------------------------
    public void changedUpdate(final DocumentEvent e) {
        updateTexts(e);
    }
    
    public void insertUpdate(final DocumentEvent e) {
        updateTexts(e);
    }
    
    public void removeUpdate(final DocumentEvent e) {
        updateTexts(e);
    }
    // End of implementation of DocumentListener -------------------------------

    /** Handles changes in the project name and the project directory. */
    private void updateTexts(final DocumentEvent e) {
        if (!importStyle) {
            createdFolderTextField.setText(getCreatedFolderPath());
        } else {
            if (ignoreEvent) {
                return;
            }
            ignoreEvent = true;
            if (e.getDocument() != projectNameTextField.getDocument()) { // #81196
                updateProjectName();
            }
            if (e.getDocument() != createdFolderTextField.getDocument()) { // #81196
                updateProjectFolder();
            }
            ignoreEvent = false;
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    private void updateProjectName() {
        String prjName = computeProjectName();
        if ((lastComputedPrjName != null) && (!lastComputedPrjName.equals(projectNameTextField.getText().trim()))) {
            return;
        }
        lastComputedPrjName = prjName;
        if (prjName != null) {
            projectNameTextField.setText(prjName);
        }
    }
    
    private String computeProjectName() {
        String cPrjName = null;
        File f = FileUtil.normalizeFile(new File(projectLocationTextField.getText()));
        FileObject fo = FileUtil.toFileObject(f);
        if (fo != null) {
            cPrjName = fo.getName();
        }
        return cPrjName;
    }
    
    private void updateProjectFolder() {
        String prjFolder = computeProjectFolder();
        if ((lastComputedPrjFolder != null) && (!lastComputedPrjFolder.equals(createdFolderTextField.getText().trim()))) {
            return;
        }
        lastComputedPrjFolder = prjFolder;
        if (prjFolder != null) {
            createdFolderTextField.setText(prjFolder);
        } else {
            createdFolderTextField.setText(""); // NOI18N
        }
    }
    
    private String computeProjectFolder() {
        return FileUtil.normalizeFile(new File(projectLocationTextField.getText())).getAbsolutePath();
    }
    
    private String getCreatedFolderPath() {
        StringBuffer folder = new StringBuffer(projectLocationTextField.getText().trim());
        if (!importStyle) {
            if (!projectLocationTextField.getText().endsWith(File.separator)) {
                folder.append(File.separatorChar);
            }
            folder.append(projectNameTextField.getText().trim());
        }
        
        return folder.toString();
    }
    
    private String getCreatedFolderText() {
        return createdFolderTextField.getText().trim();
    }
    
}

//TODO implement check for project folder name and location
