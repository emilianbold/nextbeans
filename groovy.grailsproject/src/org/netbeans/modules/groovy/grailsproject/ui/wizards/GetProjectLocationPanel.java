/*
 * NewGrailsProjectPanel.java
 *
 * Created on October 1, 2007, 2:49 PM
 */

package org.netbeans.modules.groovy.grailsproject.ui.wizards;

import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import javax.swing.JFileChooser;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import java.io.File;
import java.text.MessageFormat;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


/**
 *
 * @author  schmidtm
 */
public class GetProjectLocationPanel extends WizardSettingsPanel implements DocumentListener {

    private GetProjectLocationStep parentStep;

    boolean valid(WizardDescriptor settings) {
        if(projectNameTextField.getText().length() > 0
                && (new File(projectLocationTextField.getText()).isDirectory())) {
            return true;
        }

        return false;
    }
    
    void read (WizardDescriptor d) {
        File projectLocation = (File) d.getProperty ("projectFolder");  //NOI18N
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory ()) {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        
        Integer count = (Integer) d.getProperty("WizardPanel_GrailsProjectCounter");
        String formater = NbBundle.getMessage(GetProjectLocationPanel.class, "TXT_GrailsApplication");
        
        int baseCount = count.intValue();
        
        String newPrjName = (String) d.getProperty ("name"); //NOI18N
        if (newPrjName == null) {        
            while ((newPrjName = validFreeProjectName(projectLocation, formater, baseCount)) == null) {
                baseCount++;
            }
        }
        
        projectLocationTextField.setText(projectLocation.getAbsolutePath());
        projectFolderTextField.setText( projectLocation.getAbsolutePath() + File.separatorChar + projectNameTextField.getText() );        
        projectNameTextField.setText(newPrjName);
    }
    
    void validate (WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    void store( WizardDescriptor d ) {
        // d.putProperty( "setAsMain", setAsMainCheckBox.isSelected() && setAsMainCheckBox.isVisible() ? Boolean.TRUE : Boolean.FALSE ); // NOI18N
        d.putProperty( "projectFolder", new File(projectFolderTextField.getText().trim()) ); // NOI18N
        d.putProperty( "projectName", projectNameTextField.getText() ); // NOI18N
        parentStep.fireChangeEvent();
    }
    
    
    
    /** Creates new form NewGrailsProjectPanel */
    public GetProjectLocationPanel(GetProjectLocationStep parentStep) {
        this.parentStep = parentStep;
        initComponents();
        
        setName(NbBundle.getMessage(GetProjectLocationPanel.class,"LAB_ConfigureProject")); // NOI18N
        
        // register event listeners to auto-update some fields.
        
        projectLocationTextField.getDocument().addDocumentListener( this );
        projectNameTextField.getDocument().addDocumentListener( this );

        putClientProperty("NewProjectWizard_Title", NbBundle.getMessage(NewGrailsProjectWizardIterator.class,"TXT_NewGrailsApp")); // NOI18N
        getAccessibleContext ().setAccessibleName (NbBundle.getMessage(NewGrailsProjectWizardIterator.class,"TXT_NewGrailsApp")); // NOI18N
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        projectLocationLabel = new javax.swing.JLabel();
        projectFolderLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationTextField = new javax.swing.JTextField();
        projectFolderTextField = new javax.swing.JTextField();
        browsLocationJButton = new javax.swing.JButton();
        setAsMainCheckBox = new javax.swing.JCheckBox();

        projectNameLabel.setDisplayedMnemonic('N');
        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectNameLabel.text")); // NOI18N

        projectLocationLabel.setDisplayedMnemonic('L');
        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectLocationLabel.text")); // NOI18N

        projectFolderLabel.setDisplayedMnemonic('F');
        projectFolderLabel.setLabelFor(projectFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectFolderLabel, org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectFolderLabel.text")); // NOI18N

        projectNameTextField.setText(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectNameTextField.text")); // NOI18N

        projectLocationTextField.setText(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectLocationTextField.text")); // NOI18N

        projectFolderTextField.setEditable(false);
        projectFolderTextField.setText(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectFolderTextField.text")); // NOI18N

        browsLocationJButton.setMnemonic('o');
        org.openide.awt.Mnemonics.setLocalizedText(browsLocationJButton, org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.browsLocationJButton.text")); // NOI18N
        browsLocationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browsLocationJButtonActionPerformed(evt);
            }
        });

        setAsMainCheckBox.setMnemonic('M');
        setAsMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.setAsMainCheckBox.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(setAsMainCheckBox)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectLocationLabel)
                    .add(projectFolderLabel)
                    .add(projectNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browsLocationJButton))
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectNameLabel)
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectLocationLabel)
                    .add(browsLocationJButton)
                    .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectFolderLabel)
                    .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(setAsMainCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        projectNameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectNameLabel.AccessibleContext.accessibleDescription")); // NOI18N
        projectLocationLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectLocationLabel.AccessibleContext.accessibleDescription")); // NOI18N
        projectFolderLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectFolderLabel.AccessibleContext.accessibleDescription")); // NOI18N
        projectNameTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectNameTextField.AccessibleContext.accessibleName")); // NOI18N
        projectNameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectNameTextField.AccessibleContext.accessibleDescription")); // NOI18N
        projectLocationTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectLocationTextField.AccessibleContext.accessibleName")); // NOI18N
        projectLocationTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectLocationTextField.AccessibleContext.accessibleDescription")); // NOI18N
        projectFolderTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectFolderTextField.AccessibleContext.accessibleName")); // NOI18N
        projectFolderTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.projectFolderTextField.AccessibleContext.accessibleDescription")); // NOI18N
        browsLocationJButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.browsLocationJButton.AccessibleContext.accessibleDescription")); // NOI18N
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.setAsMainCheckBox.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GetProjectLocationPanel.class, "GetProjectLocationPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void browsLocationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browsLocationJButtonActionPerformed
            JFileChooser chooser = new JFileChooser ();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle(NbBundle.getMessage(GetProjectLocationPanel.class,"GetProjectLocationPanel.FileChooserTitle"));
            chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            String path = projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File (path);
                if (f.exists ()) {
                    chooser.setSelectedFile(f);
                }
            }
            if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) { //NOI18N
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText( projectDir.getAbsolutePath() );
            }   
}//GEN-LAST:event_browsLocationJButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browsLocationJButton;
    private javax.swing.JLabel projectFolderLabel;
    private javax.swing.JTextField projectFolderTextField;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables

    
    public void insertUpdate(DocumentEvent e) {
        updateTexts( e ) ;
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts( e ) ;
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts( e ) ;
    }
    
    /** Handles changes in the Project name and project directory
     */
    private void updateTexts( DocumentEvent e ) {
        
        Document doc = e.getDocument();
                
        if ( doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument() ) {
            // Change in the project name
        
            String projectName = projectNameTextField.getText();
            String projectFolder = projectLocationTextField.getText(); 
             
            getProjectFolderTextField().setText( new File(projectFolder, projectName).getAbsolutePath() );
            
            parentStep.fireChangeEvent();
            
        }                
  
    }

    public javax.swing.JTextField getProjectFolderTextField() {
        return projectFolderTextField;
    }
    
    private String validFreeProjectName (final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format(formater, index);
        File file = new File (parentFolder, name);
        return file.exists() ? null : name;
    }
    
}
