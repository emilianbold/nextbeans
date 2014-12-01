/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javascript.nodejs.ui.libraries;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript.nodejs.file.PackageJson;
import org.netbeans.modules.javascript.nodejs.platform.NodeJsSupport;
import org.openide.util.RequestProcessor;

/**
 * Panel for customization of npm dependencies/library.
 *
 * @author Jan Stola
 */
public class LibrariesPanel extends javax.swing.JPanel {
    /** Request processor used by this class. */
    private static final RequestProcessor RP = new RequestProcessor(LibrariesPanel.class);

    /**
     * Creates a new {@code LibrariesPanel}.
     * 
     * @param project project whose libraries should be customized.
     */
    public LibrariesPanel(Project project) {
        initComponents();
        PackageJson packagejson = getPackageJson(project);
        if (packagejson.exists()) {
            PackageJson.NpmDependencies dependencies = packagejson.getDependencies();
            regularPanel.setProject(project);
            developmentPanel.setProject(project);
            optionalPanel.setProject(project);
            regularPanel.setDependencies(toLibraries(dependencies.dependencies));
            developmentPanel.setDependencies(toLibraries(dependencies.devDependencies));
            optionalPanel.setDependencies(toLibraries(dependencies.optionalDependencies));
            loadInstalledLibraries(project);
        } else {
            GroupLayout layout = (GroupLayout)getLayout();
            layout.replace(tabbedPane, messageLabel);
        }
    }

    private PackageJson getPackageJson(Project project) {
        NodeJsSupport nodeJsSupport = project.getLookup().lookup(NodeJsSupport.class);
        if (nodeJsSupport != null) {
            return nodeJsSupport.getPackageJson();
        }
        return new PackageJson(project.getProjectDirectory());
    }

    /**
     * Converts the library-to-version map to the list of {@code Library.Version}s.
     * 
     * @param map maps library name to library version.
     * @return list of {@code Library.Version}s that corresponds to the given map.
     */
    static List<Library.Version> toLibraries(Map<String,String> map) {
        List<Library.Version> libraries = new ArrayList<>(map.size());
        for (Map.Entry<String,String> entry : map.entrySet()) {
            Library library = new Library(entry.getKey());
            Library.Version version = new Library.Version(library, entry.getValue());
            libraries.add(version);
        }
        return libraries;
    }

    /**
     * Loads the libraries installed in the given project. Updates
     * the view once the installed libraries are determined.
     * 
     * @param project project we are interested in.
     */
    private void loadInstalledLibraries(final Project project) {
        RP.post(new Runnable() {
            @Override
            public void run() {
                LibraryProvider provider = LibraryProvider.forProject(project);
                Library.Version[] libraries = provider.installedLibraries();
                final Map<String,Library.Version> map;
                if (libraries == null) {
                    map = null;
                } else {
                    map = new HashMap<>();
                    for (Library.Version libraryVersion : libraries) {
                        map.put(libraryVersion.getLibrary().getName(), libraryVersion);
                    }
                }

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        regularPanel.setInstalledLibraries(map);
                        developmentPanel.setInstalledLibraries(map);
                        optionalPanel.setInstalledLibraries(map);
                    }
                });
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        messageLabel = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        regularPanel = new org.netbeans.modules.javascript.nodejs.ui.libraries.DependenciesPanel();
        developmentPanel = new org.netbeans.modules.javascript.nodejs.ui.libraries.DependenciesPanel();
        optionalPanel = new org.netbeans.modules.javascript.nodejs.ui.libraries.DependenciesPanel();

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(messageLabel, org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.messageLabel.text")); // NOI18N
        messageLabel.setEnabled(false);
        messageLabel.setMaximumSize(new java.awt.Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.regularPanel.TabConstraints.tabTitle"), regularPanel); // NOI18N
        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.developmentPanel.TabConstraints.tabTitle"), developmentPanel); // NOI18N
        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.optionalPanel.TabConstraints.tabTitle"), optionalPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.javascript.nodejs.ui.libraries.DependenciesPanel developmentPanel;
    private javax.swing.JLabel messageLabel;
    private org.netbeans.modules.javascript.nodejs.ui.libraries.DependenciesPanel optionalPanel;
    private org.netbeans.modules.javascript.nodejs.ui.libraries.DependenciesPanel regularPanel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
