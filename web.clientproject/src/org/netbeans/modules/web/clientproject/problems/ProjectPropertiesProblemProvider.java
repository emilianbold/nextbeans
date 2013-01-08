/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.problems;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.netbeans.modules.web.clientproject.ClientSideProject;
import org.netbeans.modules.web.clientproject.ClientSideProjectConstants;
import org.netbeans.modules.web.clientproject.ui.customizer.CompositePanelProviderImpl;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.ProjectProblemsProvider;
import org.netbeans.spi.project.ui.support.ProjectProblemsProviderSupport;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 * Problems in project properties.
 */
public final class ProjectPropertiesProblemProvider implements ProjectProblemsProvider {

    // set would be better but it is fine to use a list for small number of items
    static final List<String> WATCHED_PROPERTIES = new CopyOnWriteArrayList<String>(Arrays.asList(
            ClientSideProjectConstants.PROJECT_SITE_ROOT_FOLDER,
            ClientSideProjectConstants.PROJECT_TEST_FOLDER,
            ClientSideProjectConstants.PROJECT_CONFIG_FOLDER));

    final ProjectProblemsProviderSupport problemsProviderSupport = new ProjectProblemsProviderSupport(this);
    private final ClientSideProject project;
    private final PropertyChangeListener projectPropertiesListener = new ProjectPropertiesListener();

    private volatile FileChangeListener fileChangesListener = new FileChangesListener();


    private ProjectPropertiesProblemProvider(ClientSideProject project) {
        this.project = project;
    }

    public static ProjectPropertiesProblemProvider createForProject(ClientSideProject project) {
        ProjectPropertiesProblemProvider projectProblems = new ProjectPropertiesProblemProvider(project);
        projectProblems.addProjectPropertiesListeners();
        projectProblems.addFileChangesListeners();
        return projectProblems;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        problemsProviderSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        problemsProviderSupport.removePropertyChangeListener(listener);
    }

    @Override
    public Collection<? extends ProjectProblem> getProblems() {
        return problemsProviderSupport.getProblems(new ProjectProblemsProviderSupport.ProblemsCollector() {
            @Override
            public Collection<ProjectProblemsProvider.ProjectProblem> collectProblems() {
                Collection<ProjectProblemsProvider.ProjectProblem> currentProblems = new ArrayList<ProjectProblem>(3);
                checkSiteRootDir(currentProblems);
                checkTestDir(currentProblems);
                checkConfigDir(currentProblems);
                return currentProblems;
            }
        });
    }

    @NbBundle.Messages({
        "ProjectPropertiesProblemProvider.invalidSiteRootDir.title=Invalid Site Root",
        "# {0} - src dir path",
        "ProjectPropertiesProblemProvider.invalidSiteRootDir.description=The directory \"{0}\" does not exist and cannot be used for Site Root."
    })
    private void checkSiteRootDir(Collection<ProjectProblem> currentProblems) {
        File invalidDirectory = getInvalidDirectory(project.getSiteRootFolder(), ClientSideProjectConstants.PROJECT_SITE_ROOT_FOLDER);
        if (invalidDirectory != null) {
            ProjectProblem problem = ProjectProblem.createError(
                    Bundle.ProjectPropertiesProblemProvider_invalidSiteRootDir_title(),
                    Bundle.ProjectPropertiesProblemProvider_invalidSiteRootDir_description(invalidDirectory.getAbsolutePath()),
                    new CustomizerProblemResolver(project, CompositePanelProviderImpl.SOURCES, ClientSideProjectConstants.PROJECT_SITE_ROOT_FOLDER));
            currentProblems.add(problem);
        }
    }

    @NbBundle.Messages({
        "ProjectPropertiesProblemProvider.invalidTestDir.title=Invalid Unit Tests",
        "# {0} - test dir path",
        "ProjectPropertiesProblemProvider.invalidTestDir.description=The directory \"{0}\" does not exist and cannot be used for Unit Tests."
    })
    private void checkTestDir(Collection<ProjectProblem> currentProblems) {
        File invalidDirectory = getInvalidDirectory(project.getTestsFolder(), ClientSideProjectConstants.PROJECT_TEST_FOLDER);
        if (invalidDirectory != null) {
            ProjectProblem problem = ProjectProblem.createError(
                    Bundle.ProjectPropertiesProblemProvider_invalidTestDir_title(),
                    Bundle.ProjectPropertiesProblemProvider_invalidTestDir_description(invalidDirectory.getAbsolutePath()),
                    new CustomizerProblemResolver(project, CompositePanelProviderImpl.SOURCES, ClientSideProjectConstants.PROJECT_TEST_FOLDER));
            currentProblems.add(problem);
        }
    }

    @NbBundle.Messages({
        "ProjectPropertiesProblemProvider.invalidConfigDir.title=Invalid Configuration Files",
        "# {0} - src dir path",
        "ProjectPropertiesProblemProvider.invalidConfigDir.description=The directory \"{0}\" does not exist and cannot be used for Configuration Files.",
        "# {0} - project name",
        "ProjectPropertiesProblemProvider.invalidConfigDir.dialog.title=Select Configuration Files for {0}"
    })
    private void checkConfigDir(Collection<ProjectProblem> currentProblems) {
        File invalidDirectory = getInvalidDirectory(project.getConfigFolder(), ClientSideProjectConstants.PROJECT_CONFIG_FOLDER);
        if (invalidDirectory != null) {
            ProjectProblem problem = ProjectProblem.createError(
                    Bundle.ProjectPropertiesProblemProvider_invalidConfigDir_title(),
                    Bundle.ProjectPropertiesProblemProvider_invalidConfigDir_description(invalidDirectory.getAbsolutePath()),
                    new CustomizerProblemResolver(project, CompositePanelProviderImpl.SOURCES, ClientSideProjectConstants.PROJECT_CONFIG_FOLDER));
            currentProblems.add(problem);
        }
    }

    private File getInvalidDirectory(FileObject directory, String propertyName) {
        assert WATCHED_PROPERTIES.contains(propertyName) : "Property '" + propertyName + "' should be watched for changes";
        if (directory != null) {
            if (directory.isValid()) {
                // ok
                return null;
            }
            // invalid fo
            return FileUtil.toFile(directory);
        }
        File dir = resolveFile(propertyName);
        if (dir != null && dir.isDirectory()) {
            // meanwhile resolved
            return null;
        }
        return dir;
    }

    private File resolveFile(String propertyName) {
        String propValue = project.getEvaluator().getProperty(propertyName);
        if (propValue == null) {
            return null;
        }
        return project.getProjectHelper().resolveFile(propValue);
    }

    private void addProjectPropertiesListeners() {
        PropertyEvaluator evaluator = project.getEvaluator();
        evaluator.addPropertyChangeListener(WeakListeners.propertyChange(projectPropertiesListener, evaluator));
    }

    private void addFileChangesListeners() {
        addFileChangeListener(resolveFile(ClientSideProjectConstants.PROJECT_SITE_ROOT_FOLDER));
        addFileChangeListener(resolveFile(ClientSideProjectConstants.PROJECT_TEST_FOLDER));
        addFileChangeListener(resolveFile(ClientSideProjectConstants.PROJECT_CONFIG_FOLDER));
    }

    private void addFileChangeListener(File file) {
        if (file == null) {
            return;
        }
        try {
            FileUtil.addFileChangeListener(fileChangesListener, file);
        } catch (IllegalArgumentException ex) {
            // already listenening, ignore
        }
    }

    void propertiesChanged() {
        // release the current listener
        fileChangesListener = new FileChangesListener();
        addFileChangesListeners();
    }

    //~ Inner classes

    private final class ProjectPropertiesListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (WATCHED_PROPERTIES.contains(evt.getPropertyName())) {
                problemsProviderSupport.fireProblemsChange();
                propertiesChanged();
            }
        }

    }

    private final class FileChangesListener implements FileChangeListener {

        @Override
        public void fileFolderCreated(FileEvent fe) {
            problemsProviderSupport.fireProblemsChange();
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            // noop
        }

        @Override
        public void fileChanged(FileEvent fe) {
            // noop
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            problemsProviderSupport.fireProblemsChange();
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            problemsProviderSupport.fireProblemsChange();
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
            // noop
        }

    }

}
