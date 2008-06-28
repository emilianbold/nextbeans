/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.libraries.ui.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryChooser;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.javascript.libraries.util.JSLibraryProjectUtils;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  Quy Nguyen <quynguyen@netbeans.org>
 */
public final class CustomizerJSLibraries extends JPanel implements ActionListener {

    private final ProjectCustomizer.Category category;
    private final Project project;
    private final DefaultListModel libraryListModel;
    private final Set<Library> originalLibraries;

    /** Creates new form JavaScriptLibrariesCustomizer */
    public CustomizerJSLibraries(ProjectCustomizer.Category category, Project project) {
        this.category = category;
        this.project = project;
        this.originalLibraries = new LinkedHashSet<Library>();
        this.libraryListModel = new DefaultListModel();
        
        initComponents();

        List<Library> libraries = JSLibraryProjectUtils.getJSLibraries(project);

        for (Library library : libraries) {
            NamedLibrary namedLib = new NamedLibrary(library);
            libraryListModel.addElement(namedLib);
            originalLibraries.add(library);
        }

        librariesJList.setModel(libraryListModel);
        librariesJList.addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        updateRemoveButtonState();
                    }
                });

        updateRemoveButtonState();
        category.setStoreListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        libLocationJLabel = new javax.swing.JLabel();
        libLocationJTextField = new javax.swing.JTextField();
        librariesListLabel = new javax.swing.JLabel();
        libTableScrollPane = new javax.swing.JScrollPane();
        librariesJList = new javax.swing.JList();
        addLibraryJButton = new javax.swing.JButton();
        removeLibraryJButton = new javax.swing.JButton();

        libLocationJLabel.setLabelFor(libLocationJTextField);
        org.openide.awt.Mnemonics.setLocalizedText(libLocationJLabel, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.libLocationJLabel.text")); // NOI18N

        libLocationJTextField.setEditable(false);
        libLocationJTextField.setText(JSLibraryProjectUtils.getJSLibrarySourcePath(project));

        librariesListLabel.setLabelFor(librariesJList);
        org.openide.awt.Mnemonics.setLocalizedText(librariesListLabel, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.librariesListLabel.text")); // NOI18N

        librariesJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        libTableScrollPane.setViewportView(librariesJList);

        org.openide.awt.Mnemonics.setLocalizedText(addLibraryJButton, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.addLibraryJButton.text")); // NOI18N
        addLibraryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLibraryJButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeLibraryJButton, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.removeLibraryJButton.text")); // NOI18N
        removeLibraryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLibraryJButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(libLocationJLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(libLocationJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(librariesListLabel)
                            .add(libTableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(addLibraryJButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(removeLibraryJButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(libLocationJLabel)
                    .add(libLocationJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(38, 38, 38)
                .add(librariesListLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addLibraryJButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeLibraryJButton))
                    .add(libTableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void addLibraryJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLibraryJButtonActionPerformed

    // TODO don't recompute this every time
    Object[] objs = libraryListModel.toArray();
    Set<Library> currentLibs = new LinkedHashSet<Library>();
    for (Object o : objs) {
        currentLibs.add(((NamedLibrary)o).getLibrary());
    }
    
    LibraryChooser.Filter filter = JSLibraryProjectUtils.createDefaultFilter(currentLibs);
    LibraryManager manager = JSLibraryProjectUtils.getLibraryManager(project);
    LibraryChooser.LibraryImportHandler sharedLibHandler = JSLibraryProjectUtils.getSharedLibraryHandler(project);
    
    Set<Library> addedLibraries = LibraryChooser.showDialog(manager, filter, sharedLibHandler);
    
    if (addedLibraries != null) {
        for (Library library : addedLibraries) {
            libraryListModel.addElement(new NamedLibrary(library));
        }
    }
}//GEN-LAST:event_addLibraryJButtonActionPerformed

private void removeLibraryJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLibraryJButtonActionPerformed

    int[] removedLibIndices = librariesJList.getSelectedIndices();
    assert removedLibIndices.length > 0;
    
    for (int i = removedLibIndices.length-1; i >= 0; i--) {
        libraryListModel.remove(removedLibIndices[i]);
    }
}//GEN-LAST:event_removeLibraryJButtonActionPerformed

    private static final class NamedLibrary {

        private Library library;

        public NamedLibrary(Library lib) {
            this.library = lib;
        }

        public Library getLibrary() {
            return library;
        }

        @Override
        public String toString() {
            return library.getDisplayName();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLibraryJButton;
    private javax.swing.JLabel libLocationJLabel;
    private javax.swing.JTextField libLocationJTextField;
    private javax.swing.JScrollPane libTableScrollPane;
    private javax.swing.JList librariesJList;
    private javax.swing.JLabel librariesListLabel;
    private javax.swing.JButton removeLibraryJButton;
    // End of variables declaration//GEN-END:variables

    public void actionPerformed(ActionEvent e) {
        Set<Library> removedLibraries = originalLibraries;
        Set<Library> addedLibraries = new LinkedHashSet<Library>();
        List<Library> allLibraries = new ArrayList<Library>();

        for (int i = 0; i < libraryListModel.getSize(); i++) {
            Library currentLib = ((NamedLibrary) libraryListModel.getElementAt(i)).getLibrary();
            allLibraries.add(currentLib);

            if (!originalLibraries.contains(currentLib)) {
                addedLibraries.add(currentLib);
            } else {
                originalLibraries.remove(currentLib);
            }
        }

        if (addedLibraries.size() > 0 || removedLibraries.size() > 0) {
            Library[] libsArray = allLibraries.toArray(new Library[allLibraries.size()]);
            JSLibraryProjectUtils.setJSLibraries(project, libsArray);
        }

        // TODO extract/delete need to be done asynchronously with a progress bar
        for (Library library : addedLibraries) {
            boolean addLibrary = true;
            
            if (!JSLibraryProjectUtils.isLibraryFolderEmpty(project, library)) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(CustomizerJSLibraries.class, "ExtractLibraries_Overwrite_Msg"),
                        NbBundle.getMessage(CustomizerJSLibraries.class, "ExtractLibraries_Overwrite_Title"),
                        NotifyDescriptor.YES_NO_OPTION);
                
                Object result = DialogDisplayer.getDefault().notify(nd);
                addLibrary = (result == NotifyDescriptor.YES_OPTION);
            }
            
            if (addLibrary) {
                JSLibraryProjectUtils.extractLibraryToProject(project, library);
            }
        }
        
        for (Library library : removedLibraries) {
            JSLibraryProjectUtils.deleteLibraryFromProject(project, library);
        }
        
    }

    
    
    private void updateRemoveButtonState() {
        removeLibraryJButton.setEnabled(librariesJList.getSelectedIndex() >= 0);
    }
}
