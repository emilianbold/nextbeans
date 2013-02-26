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

package org.netbeans.modules.j2ee.persistence.wizard.dbscript;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.core.api.support.SourceGroups;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.persistence.provider.Provider;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.SourceGroupUISupport;
import org.netbeans.modules.j2ee.persistence.wizard.library.PersistenceLibrarySupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 */
public class DBScriptPanel extends javax.swing.JPanel {

    private final static Logger LOGGER = Logger.getLogger(DBScriptPanel.class.getName());

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private JTextComponent packageComboBoxEditor;

    private Project project;

    private DBScriptPanel() {
 
        initComponents();

        packageComboBoxEditor = ((JTextComponent)packageComboBox.getEditor().getEditorComponent());
        Document packageComboBoxDocument = packageComboBoxEditor.getDocument();
        packageComboBoxDocument.addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                packageChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                packageChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                packageChanged();
            }
        });
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    boolean isBeanValidationSupported() {
        if (project == null) {
            return false;
        }
        
        final String notNullAnnotation = "javax.validation.constraints.NotNull";    //NOI18N
        Sources sources=ProjectUtils.getSources(project);
        SourceGroup groups[]=sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if(groups == null || groups.length<1){
            return false;
        }
        SourceGroup firstGroup=groups[0];
        FileObject fo=firstGroup.getRootFolder();
        ClassPath compile=ClassPath.getClassPath(fo, ClassPath.COMPILE);
        if (compile == null) {
            return false;
        }
        return compile.findResource(notNullAnnotation.replace('.', '/')+".class")!=null;//NOI18N
    }

    public void initialize(Project project, FileObject targetFolder) {
        this.project = project;

        projectTextField.setText(ProjectUtils.getInformation(project).getDisplayName());

        SourceGroup[] sourceGroups = SourceGroups.getJavaSourceGroups(project);
        SourceGroupUISupport.connect(locationComboBox, sourceGroups);

        packageComboBox.setRenderer(PackageView.listRenderer());

        updatePackageComboBox();

        if (targetFolder != null) {
            // set default source group and package cf. targetFolder
            SourceGroup targetSourceGroup = SourceGroups.getFolderSourceGroup(sourceGroups, targetFolder);
            if (targetSourceGroup != null) {
                locationComboBox.setSelectedItem(targetSourceGroup);
                String targetPackage = SourceGroups.getPackageForFolder(targetSourceGroup, targetFolder);
                if (targetPackage != null) {
                    packageComboBoxEditor.setText(targetPackage);
                }
            }
        }


        Sources sources=ProjectUtils.getSources(project);
        SourceGroup groups[]=sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        SourceGroup firstGroup=groups[0];
        FileObject fo=firstGroup.getRootFolder();
        ClasspathInfo classpathInfo = ClasspathInfo.create(fo);
        JavaSource javaSource = JavaSource.create(classpathInfo);
        try {
            javaSource.runUserActionTask(new Task<CompilationController>() {

                @Override
                public void run(CompilationController controller) throws IOException {
                    controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                    TypeElement jc = controller.getElements().getTypeElement("javax.xml.bind.annotation.XmlTransient"); //NOI18N
                }
            }, true);
        } catch (IOException ex) {
            //no need to throw exception as it just will not disable option possibly unsupported, it's not severe
            LOGGER.log(Level.FINE, "Fail to check if jaxb is supported");//NOI18N
        }
    }


    public SourceGroup getLocationValue() {
        return (SourceGroup)locationComboBox.getSelectedItem();
    }

    public String getPackageName() {
        return packageComboBoxEditor.getText();
    }

    public boolean getGenerateValidationConstraints() {
        return isBeanValidationSupported();
    }
    
    public boolean getCreatePersistenceUnit() {
        return createDropScriptCheckbox.isVisible() && createDropScriptCheckbox.isSelected();
    }

    private void locationChanged() {
        updatePackageComboBox();
        changeSupport.fireChange();
    }

    private void packageChanged() {
        changeSupport.fireChange();
    }

    private void updatePackageComboBox() {
        SourceGroup sourceGroup = (SourceGroup)locationComboBox.getSelectedItem();
        if (sourceGroup != null) {
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSelectedItem()!= null && model.getSelectedItem().toString().startsWith("META-INF")
                    && model.getSize() > 1) { // NOI18N
                model.setSelectedItem(model.getElementAt(1));
            }
            packageComboBox.setModel(model);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableActionsPopup = new javax.swing.JPopupMenu();
        scriptNameLabel = new javax.swing.JLabel();
        scriptNameTextField = new javax.swing.JTextField();
        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        createScriptWarningLabel = new ShyLabel();
        createDropScriptCheckbox = new javax.swing.JCheckBox();

        setName(org.openide.util.NbBundle.getMessage(DBScriptPanel.class, "LBL_EntityClasses")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(scriptNameLabel, org.openide.util.NbBundle.getMessage(DBScriptPanel.class, "LBL_ScriptName")); // NOI18N

        scriptNameTextField.setColumns(40);

        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(DBScriptPanel.class, "LBL_Project")); // NOI18N

        projectTextField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(DBScriptPanel.class, "LBL_SrcLocation")); // NOI18N

        locationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(DBScriptPanel.class, "LBL_Package")); // NOI18N

        packageComboBox.setEditable(true);

        org.openide.awt.Mnemonics.setLocalizedText(createScriptWarningLabel, "  ");
        createScriptWarningLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        createScriptWarningLabel.setMaximumSize(new java.awt.Dimension(1000, 29));

        org.openide.awt.Mnemonics.setLocalizedText(createDropScriptCheckbox, org.openide.util.NbBundle.getMessage(DBScriptPanel.class, "LBL_CreateDropScript")); // NOI18N
        createDropScriptCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        createDropScriptCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                createDropScriptCheckboxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectLabel)
                    .addComponent(locationLabel)
                    .addComponent(packageLabel))
                .addGap(54, 54, 54)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(locationComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectTextField)
                    .addComponent(packageComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(createScriptWarningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(createDropScriptCheckbox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scriptNameLabel)
                .addGap(18, 18, 18)
                .addComponent(scriptNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scriptNameLabel)
                    .addComponent(scriptNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packageLabel))
                .addGap(18, 18, 18)
                .addComponent(createDropScriptCheckbox)
                .addGap(18, 18, 18)
                .addComponent(createScriptWarningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void locationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationComboBoxActionPerformed
        locationChanged();
    }//GEN-LAST:event_locationComboBoxActionPerformed

    private void createDropScriptCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_createDropScriptCheckboxItemStateChanged
        if(createDropScriptCheckbox.isVisible() && createDropScriptCheckbox.isSelected()){
        } else {
        }
    }//GEN-LAST:event_createDropScriptCheckboxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox createDropScriptCheckbox;
    private javax.swing.JLabel createScriptWarningLabel;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JLabel scriptNameLabel;
    private javax.swing.JTextField scriptNameTextField;
    private javax.swing.JPopupMenu tableActionsPopup;
    // End of variables declaration//GEN-END:variables

    public static final class WizardPanel implements WizardDescriptor.Panel, WizardDescriptor.FinishablePanel, ChangeListener {

        private final ChangeSupport changeSupport = new ChangeSupport(this);

        private boolean componentInitialized;
        private DBScriptPanel component;
        private WizardDescriptor wizardDescriptor;
        private Project project;

        private List<Provider> providers;
        
        public WizardPanel()
        {
        }
        
        @Override
        public DBScriptPanel getComponent() {
            if (component == null) {
                component = new DBScriptPanel();
                component.addChangeListener(this);
            }
            return component;
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public HelpCtx getHelp() {
                return new HelpCtx(DBScriptPanel.class);
        }

        @Override
        public void readSettings(Object settings) {
            wizardDescriptor = (WizardDescriptor)settings;
            

            if (!componentInitialized) {
                componentInitialized = true;

                project = Templates.getProject(wizardDescriptor);
                FileObject targetFolder = Templates.getTargetFolder(wizardDescriptor);

                getComponent().initialize(project, targetFolder);
            }

        
//
//            getComponent().update();
        }

        @Override
        public boolean isValid() {
            SourceGroup sourceGroup = getComponent().getLocationValue();
            if (sourceGroup == null) {
                setErrorMessage(NbBundle.getMessage(DBScriptPanel.class, "ERR_JavaTargetChooser_SelectSourceGroup"));
                return false;
            }

            String packageName = getComponent().getPackageName();
            if (packageName.trim().equals("")) { // NOI18N
                setErrorMessage(NbBundle.getMessage(DBScriptPanel.class, "ERR_JavaTargetChooser_CantUseDefaultPackage"));
                return false;
            }

            if (!JavaIdentifiers.isValidPackageName(packageName)) {
                setErrorMessage(NbBundle.getMessage(DBScriptPanel.class,"ERR_JavaTargetChooser_InvalidPackage")); //NOI18N
                return false;
            }

            if (!SourceGroups.isFolderWritable(sourceGroup, packageName)) {
                setErrorMessage(NbBundle.getMessage(DBScriptPanel.class, "ERR_JavaTargetChooser_UnwritablePackage")); //NOI18N
                return false;
            }

            // issue 92192: need to check that we will have a persistence provider
            // available to add to the classpath while generating entity classes (unless
            // the classpath already contains one)
            ClassPath classPath = null;
            try {
                FileObject packageFO = SourceGroups.getFolderForPackage(sourceGroup, packageName, false);
                if (packageFO == null) {
                    packageFO = sourceGroup.getRootFolder();
                }
                classPath = ClassPath.getClassPath(packageFO, ClassPath.COMPILE);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, null, e);
            }
            if (classPath != null) {
                if (classPath.findResource("javax/persistence/EntityManager.class") == null) { // NOI18N
                    // initialize the provider list lazily
                    if (providers == null) {
                        providers = PersistenceLibrarySupport.getProvidersFromLibraries();
                    }
                    if (providers.size() == 0) {
                        setErrorMessage(NbBundle.getMessage(DBScriptPanel.class, "ERR_NoJavaPersistenceAPI")); // NOI18N
                        return false;
                    }
                }
            } else {
                LOGGER.warning("Cannot get a classpath for package " + packageName + " in " + sourceGroup); // NOI18N
            }

            setErrorMessage(" "); // NOI18N
            return true;
        }

        @Override
        public void storeSettings(Object settings) {
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            changeSupport.fireChange();
        }

        private void setErrorMessage(String errorMessage) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, errorMessage); // NOI18N
        }

        @Override
        public boolean isFinishPanel() {
            return true;
        }
    }

    /**
     * A crude attempt at a label which doesn't expand its parent.
     */
    private static final class ShyLabel extends JLabel {

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.width = 0;
            return size;
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension size = super.getMinimumSize();
            size.width = 0;
            return size;
        }
    }

  
}
