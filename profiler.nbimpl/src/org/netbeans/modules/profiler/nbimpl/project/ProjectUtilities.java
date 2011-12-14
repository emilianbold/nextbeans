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

package org.netbeans.modules.profiler.nbimpl.project;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.*;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.lib.profiler.ProfilerLogger;
import org.netbeans.lib.profiler.client.ClientUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.w3c.dom.Element;
import java.awt.Dialog;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.profiler.NetBeansProfiler;
import org.netbeans.modules.profiler.api.ProfilerDialogs;
import org.netbeans.modules.profiler.api.project.AntProjectSupport;
import org.netbeans.modules.profiler.api.project.ProjectProfilingSupport;
import org.netbeans.modules.profiler.projectsupport.utilities.AppletSupport;
import org.netbeans.modules.profiler.utils.MainClassWarning;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;


/**
 * Utilities for interaction with the NetBeans IDE, specifically related to Projects
 *
 * @author Ian Formanek
 * @deprecated
 */
@NbBundle.Messages({
    "ProjectUtilities_UnknownProjectString=Unknown",
    "ProjectUtilities_FailedCreateClassesDirMsg=Failed to create classes directory: {0}",
    "ProjectUtilities_FailedGenerateAppletFileMsg=Failed to generate Applet HTML file: {0}",
    "ProjectUtilities_FailedCopyAppletFileMsg=Failed to copy Applet HTML file: {0}",
    "ProjectUtilities_FailedCreateOutputFolderMsg=Failed to create build output folder: {0}",
    "ProjectUtilities_ProfilerWillBeUnintegratedMsg=<html><br><strong>Profiler integration will be removed from {0}.</strong><br><br>The backup of the original build script was created when the project was<br>modified to enable profiling. Modifications made to the build script after<br>the backup was created will be lost. Do you want to continue?</html>",
    "ProjectUtilities_ProfilerIsntIntegratedMsg=<html><br><strong>Profiler does not seem to be integrated with {0}.<br>Do you still want to perform the unintegration?</strong><br><br>The backup of the original build script was created when the project was<br>modified to enable profiling. Modifications made to the build script after<br>the backup was created may be lost. Do you want to continue?</html>",
    "ProjectUtilities_RenamingBuildFailedMsg=Renaming build-before-profiler.xml to build.xml failed: {0}\n",
    "ProjectUtilities_RemovingBuildFailedMsg=Removing profiler-build-impl.xml failed: {0}\n",
    "ProjectUtilities_RemovingDataFailedMsg=Removing <data> section from private/private.xml failed: {0}\n",
    "ProjectUtilities_UnintegrationErrorsOccuredMsg=Errors occurred during unintegration. Details:\n\n{0}",
    "ProjectUtilities_UnintegrationSuccessfulMsg=Unintegration from {0} performed successfully."
})
@Deprecated
public final class ProjectUtilities {
    @ProjectServiceProvider(service=ProjectOpenedHook.class, 
                            projectType={
                                "org-netbeans-modules-java-j2seproject",
                                "org-netbeans-modules-j2ee-earproject",
                                "org-netbeans-modules-j2ee-ejbjarproject",
                                "org-netbeans-modules-web-project"})
    final public static class IntegrationUpdater extends ProjectOpenedHook {
        private Project prj;

        public IntegrationUpdater(Project prj) {
            this.prj = prj;
        }

        @Override
        protected void projectClosed() {
            // ignore
        }

        @Override
        protected void projectOpened() {
            Element e = ProjectUtils.getAuxiliaryConfiguration(prj)
                           .getConfigurationFragment("data", ProjectUtilities.PROFILER_NAME_SPACE, false); // NOI18N

            if (e != null) {
                unintegrateProfiler(prj);
            }
        }
    }
    //~ Static fields/initializers -----------------------------------------------------------------------------------------------

    private static final Logger LOGGER = Logger.getLogger(ProjectUtilities.class.getName());
    public static final String PROFILER_NAME_SPACE = "http://www.netbeans.org/ns/profiler/1"; // NOI18N
    //~ Methods ------------------------------------------------------------------------------------------------------------------

    /**
     * Provides list of all ProjectTypeProfiler SPI implementations currently installed.
     *
     * @return Collection<ProjectTypeProfiler> of all ProjectTypeProfilers currently installed
     */
//    public static Collection<?extends ProjectTypeProfiler> getAllProjectTypeProfilers() {
//        final Lookup lookup = Lookup.getDefault();
//        final Lookup.Template<ProjectTypeProfiler> template = new Lookup.Template<ProjectTypeProfiler>(ProjectTypeProfiler.class);
//        final Lookup.Result<ProjectTypeProfiler> result = lookup.lookup(template);
//
//        if (result == null) {
//            return new ArrayList<ProjectTypeProfiler>();
//        }
//
//        return result.allInstances();
//    }

    public static String getDefaultPackageClassNames(Project project) {
        return org.netbeans.modules.profiler.projectsupport.utilities.ProjectUtilities.getDefaultPackageClassNames(project);
    }

    // Returns true if the project contains any Java sources (does not check subprojects!)
    public static boolean isJavaProject(Project project) {
        if (project == null) return false;

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        return sourceGroups.length > 0;
    }

    /**
     * @return The current main project or null if no project is main.
     */
    public static Project getMainProject() {
        return OpenProjects.getDefault().getMainProject();
    }

    public static Project[] getOpenedProjects() {
        return OpenProjects.getDefault().getOpenProjects();
    }

    public static Project[] getOpenedProjectsForAttach() {
        Project[] projects = getOpenedProjects();
        ArrayList<Project> projectsArray = new ArrayList(projects.length);

        for (int i = 0; i < projects.length; i++) {
            if (ProjectUtilities.isProjectTypeSupportedForAttach(projects[i])) {
                projectsArray.add(projects[i]);
            }
        }

        return projectsArray.toArray(new Project[projectsArray.size()]);
    }

    public static FileObject getOrCreateBuildFolder(Project project, String buildDirProp) {
        FileObject buildDir = FileUtil.toFileObject(PropertyUtils.resolveFile(FileUtil.toFile(project.getProjectDirectory()),
                                                                              buildDirProp));

        if (buildDir == null) {
            try {
                // TODO: if buildDirProp is absolute, relativize via PropertyUtils
                buildDir = FileUtil.createFolder(project.getProjectDirectory(), buildDirProp);
            } catch (IOException e) {
                ErrorManager.getDefault()
                            .annotate(e, Bundle.ProjectUtilities_FailedCreateOutputFolderMsg(e.getMessage()));
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);

                return null;
            }
        }

        return buildDir;
    }

    final private static Pattern PROFILER_INIT = Pattern.compile("<\\s*target\\s+.*?name\\s*=\\s*\"profile-init\"", Pattern.DOTALL | Pattern.MULTILINE);
    public static boolean isProfilerIntegrated(Project project) {
        String buildXml = ProjectUtilities.getProjectBuildScript(project, "nbproject/build-impl.xml"); // NOI18N
        Matcher m = PROFILER_INIT.matcher(buildXml);
        return m.find();
    }

    public static String getProjectBuildScript(final Project project) {
        return getProjectBuildScript(project, "build.xml");
    }

    public static String getProjectBuildScript(final Project project, final String buildXml) {
        final FileObject buildFile = AntProjectSupport.get(project).getProjectBuildScript(buildXml);
        if (buildFile == null) {
            return null;
        }

        RandomAccessFile file = null;
        byte[] data = null;

        try {
            file = new RandomAccessFile(FileUtil.toFile(buildFile), "r");
            data = new byte[(int) buildFile.getSize()];
            file.readFully(data);
        } catch (FileNotFoundException e2) {
            ProfilerLogger.log(e2);

            return null;
        } catch (IOException e2) {
            ProfilerLogger.log(e2);

            return null;
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e2) {
                    ProfilerLogger.log(e2);
                }
            }
        }

        try {
            return new String(data, "UTF-8" //NOI18N
            ); // According to Issue 65557, build.xml uses UTF-8, not default encoding!
        } catch (UnsupportedEncodingException ex) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);

            return null;
        }
    }

//    Now available using AntProjectSupport
//
//    public static FileObject findBuildFile(final Project project) {
//        FileObject buildFile = null;
//
//        Properties props = org.netbeans.modules.profiler.projectsupport.utilities.ProjectUtilities.getProjectProperties(project);
//        String buildFileName = props != null ? props.getProperty("buildfile") : null; // NOI18N
//        if (buildFileName != null) {
//            buildFile = findBuildFile(project, buildFileName);
//        }
//        if (buildFile == null) {
//            buildFile = findBuildFile(project, "build.xml"); //NOI18N
//        }
//        return buildFile;
//    }
//
//    public static FileObject findBuildFile(final Project project, final String buildFileName) {
//        return project.getProjectDirectory().getFileObject(buildFileName);
//    }

//    public static java.util.List<SimpleFilter> getProjectDefaultInstrFilters(Project project) {
//        java.util.List<SimpleFilter> v = new ArrayList<SimpleFilter>();
//
//        if (ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA).length > 0) {
//            v.add(FILTER_PROJECT_ONLY);
//        }
//
//        if (hasSubprojects(project)) {
//            v.add(FILTER_PROJECT_SUBPROJECTS_ONLY);
//        }
//
//        return v;
//    }

    public static ClientUtils.SourceCodeSelection[] getProjectDefaultRoots(Project project, String[][] projectPackagesDescr) {
        computeProjectPackages(project, true, projectPackagesDescr);

        ClientUtils.SourceCodeSelection[] ret = new ClientUtils.SourceCodeSelection[projectPackagesDescr[1].length];

        for (int i = 0; i < projectPackagesDescr[1].length; i++) {
            if ("".equals(projectPackagesDescr[1][i])) { //NOI18N
                ret[i] = new ClientUtils.SourceCodeSelection("", "", ""); //NOI18N
            } else {
                ret[i] = new ClientUtils.SourceCodeSelection(projectPackagesDescr[1][i] + ".", "", ""); //NOI18N
            }
        }

        return ret;
    }

    public static Project getProjectForBuildScript(String fileName) {
        FileObject projectFO = FileUtil.toFileObject(new File(fileName));

        while (projectFO != null) {
            try {
                if (projectFO.isFolder()) {
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("Trying: " + projectFO); //NOI18N
                    }

                    Project p = ProjectManager.getDefault().findProject(projectFO);

                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("Got: " + ((p != null) ? getProjectName(p) : null)); //NOI18N
                    }

                    if (p != null) {
                        return p;
                    }
                }

                projectFO = projectFO.getParent();
            } catch (IOException e) {
                ProfilerLogger.severe("Got: IOException : " + e.getMessage()); //NOI18N
            }
        }

        return null;
    }

    public static Icon getProjectIcon(Project project) {
        ProjectInformation info = project.getLookup().lookup(ProjectInformation.class);

        if (info == null) {
            return new ImageIcon();
        } else {
            return info.getIcon();
        }
    }

    public static String getProjectName(Project project) {
        ProjectInformation info = project.getLookup().lookup(ProjectInformation.class);

        if (info == null) {
            return Bundle.ProjectUtilities_UnknownProjectString();
        } else {
            return info.getDisplayName();
        }
    }

    /**
     * Provides a list of all fully qualified packages within the project that contain some sources.
     *
     * @param project The project
     * @return a list of fully qualified String names of packages within the provided project that contain sources
     */
    public static String[] getProjectPackages(final Project project) {
        ArrayList<String> packages = new ArrayList<String>();

        for (FileObject root : getSourceRoots(project, true)) {
            addSubpackages(packages, "", root); //NOI18N
        }

        return packages.toArray(new String[0]);
    }

//    /**
//     * Checks if ProjectTypeProfiler capable of profiling the provided project exists and if so, returns it.
//     *
//     * @param project The project
//     * @return ProjectTypeProfiler capable of profiling the project or null if none of the installed PTPs supports it
//     */
//    public static ProjectTypeProfiler getProjectTypeProfiler(final Project project) {
//        if (project == null) {
//            return ProjectTypeProfiler.DEFAULT; // global attach
//        }
//
//        ProjectTypeProfiler fromProject = project.getLookup().lookup(ProjectTypeProfiler.class);
//        
//        return fromProject != null ? (fromProject.isProfilingSupported(project) ? fromProject : ProjectTypeProfiler.DEFAULT) : ProjectTypeProfiler.DEFAULT;
////        final Collection c = getAllProjectTypeProfilers();
////
////        for (Iterator i = c.iterator(); i.hasNext();) {
////            final ProjectTypeProfiler ptp = (ProjectTypeProfiler) i.next();
////
////            if (ptp.isProfilingSupported(project)) {
////                return ptp; // project type profiler for provided project
////            }
////        }
//    }

    /**
     * @return true if there is a ProjectTypeProfilers capable of profiling the provided project using the Profile (Main) Project action, false otherwise.
     */
    public static boolean isProjectTypeSupported(final Project project) {
//        ProjectTypeProfiler ptp = getProjectTypeProfiler(project);
//
//        if (ptp.isProfilingSupported(project)) {
//            return true;
//        }
        return ProjectProfilingSupport.get(project).isProfilingSupported() ||
                hasAction(project, "profile"); //NOI18N
    }

    /**
     * @return true if the project can be used for profiling using the Attach Profiler action (== Java project), false otherwise.
     */
    public static boolean isProjectTypeSupportedForAttach(Project project) {
//        return getProjectTypeProfiler(project).isAttachSupported(project);
        return ProjectProfilingSupport.get(project).isAttachSupported();
    }

    /**
     * Checks which of the provided source roots contains the given file.
     *
     * @param roots A list of source roots
     * @param file A FileObject to look for
     * @return The source roots that contains the specified file or null if none of them contain it.
     */
    public static FileObject getRootOf(final FileObject[] roots, final FileObject file) {
        FileObject srcDir = null;

        for (int i = 0; i < roots.length; i++) {
            if (FileUtil.isParentOf(roots[i], file)) {
                srcDir = roots[i];

                break;
            }
        }

        return srcDir;
    }

    public static Project[] getSortedProjects(Project[] projects) {
        ArrayList projectsArray = new ArrayList(projects.length);

        for (int i = 0; i < projects.length; i++) {
            projectsArray.add(projects[i]);
        }

        try {
            Collections.sort(projectsArray,
                             new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Project p1 = (Project) o1;
                        Project p2 = (Project) o2;

                        return ProjectUtils.getInformation(p1).getDisplayName().toLowerCase()
                                           .compareTo(ProjectUtils.getInformation(p2).getDisplayName().toLowerCase());
                    }
                });
        } catch (Exception e) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, e.getMessage()); // just in case ProjectUtils doesn't provide expected information
        }

        ;

        projectsArray.toArray(projects);

        return projects;
    }

    /**
     * Provides a list of source roots for the given project.
     *
     * @param project The project
     * @return an array of FileObjects that are the source roots for this project
     */
    public static FileObject[] getSourceRoots(final Project project) {
        return getSourceRoots(project, true);
    }

    /**
     * Provides a list of source roots for the given project.
     *
     * @param project The project
     * @param traverse Include subprojects
     * @return an array of FileObjects that are the source roots for this project
     */
    public static FileObject[] getSourceRoots(final Project project, final boolean traverse) {
        Set<FileObject> set = new HashSet<FileObject>();
        Set<Project> projects = new HashSet<Project>();

        projects.add(project);
        getSourceRoots(project, traverse, projects, set);

        return set.toArray(new FileObject[set.size()]);
    }

    public static boolean backupBuildScript(final Project project) {
        final FileObject buildFile = AntProjectSupport.get(project).getProjectBuildScript();
        final FileObject buildBackupFile = project.getProjectDirectory().getFileObject("build-before-profiler.xml"); //NOI18N

        if (buildFile != null) {
            if (buildBackupFile != null) {
                // clean up the old backup file
                try {
                    buildBackupFile.delete();
                } catch (IOException e) {
                    ProfilerLogger.log(e);

                    return false;

                    // cannot delete already existing backup
                }
            }
            // copy over the new build file
            try {
                buildFile.copy(project.getProjectDirectory(), "build-before-profiler", "xml"); //NOI18N
            } catch (IOException e1) {
                ProfilerLogger.log(e1);

                return false;
            }
            return true;
        }

        return false;
    }

//    public static SimpleFilter computeProjectOnlyInstrumentationFilter(Project project, SimpleFilter predefinedInstrFilter,
//                                                                       String[][] projectPackagesDescr) {
//        // TODO: projectPackagesDescr[1] should only contain packages from subprojects, currently contains also toplevel project packages
//        if (FILTER_PROJECT_ONLY.equals(predefinedInstrFilter)) {
//            computeProjectPackages(project, false, projectPackagesDescr);
//
//            StringBuffer projectPackages = new StringBuffer();
//
//            for (int i = 0; i < projectPackagesDescr[0].length; i++) {
//                projectPackages.append("".equals(projectPackagesDescr[0][i]) ? getDefaultPackageClassNames(project)
//                                                                             : (projectPackagesDescr[0][i] + ". ")); //NOI18N
//            }
//
//            return new SimpleFilter(PROFILE_PROJECT_CLASSES_STRING, SimpleFilter.SIMPLE_FILTER_INCLUSIVE,
//                                    projectPackages.toString().trim());
//        } else if (FILTER_PROJECT_SUBPROJECTS_ONLY.equals(predefinedInstrFilter)) {
//            computeProjectPackages(project, true, projectPackagesDescr);
//
//            StringBuffer projectPackages = new StringBuffer();
//
//            for (int i = 0; i < projectPackagesDescr[1].length; i++) {
//                projectPackages.append("".equals(projectPackagesDescr[1][i]) ? getDefaultPackageClassNames(project)
//                                                                             : (projectPackagesDescr[1][i] + ". ")); //NOI18N // TODO: default packages need to be processed also for subprojects!!!
//            }
//
//            return new SimpleFilter(PROFILE_PROJECT_SUBPROJECT_CLASSES_STRING, SimpleFilter.SIMPLE_FILTER_INCLUSIVE,
//                                    projectPackages.toString().trim());
//        }
//
//        return null;
//    }

    public static void computeProjectPackages(final Project project, boolean subprojects, String[][] storage) {
        if ((storage == null) || (storage.length != 2)) {
            throw new IllegalArgumentException("Storage must be a non-null String[2][] array"); // NOI18N
        }

        if (storage[0] == null) {
            ArrayList<String> packages1 = new ArrayList<String>();

            for (FileObject root : getSourceRoots(project, false)) {
                addSubpackages(packages1, "", root); //NOI18N
            }

            storage[0] = packages1.toArray(new String[0]);
        }

        if (subprojects && (storage[1] == null)) {
            FileObject[] srcRoots2 = getSourceRoots(project, true); // TODO: should be computed based on already known srcRoots1
            ArrayList<String> packages2 = new ArrayList<String>();

            for (FileObject root : srcRoots2) {
                addSubpackages(packages2, "", root); //NOI18N
            }

            storage[1] = packages2.toArray(new String[0]);
        }
    }

    public static URL copyAppletHTML(Project project, PropertyEvaluator props, FileObject profiledClassFile, String value) {
        try {
            String buildDirProp = props.getProperty("build.dir"); //NOI18N

            FileObject buildFolder = getOrCreateBuildFolder(project, buildDirProp);

            FileObject htmlFile = null;
            htmlFile = profiledClassFile.getParent().getFileObject(profiledClassFile.getName(), "html"); //NOI18N

            if (htmlFile == null) {
                htmlFile = profiledClassFile.getParent().getFileObject(profiledClassFile.getName(), "HTML"); //NOI18N
            }

            if (htmlFile == null) {
                return null;
            }

            FileObject existingFile = buildFolder.getFileObject(htmlFile.getName(), htmlFile.getExt());

            if (existingFile != null) {
                existingFile.delete();
            }

            htmlFile.copy(buildFolder, profiledClassFile.getName(), value).getURL();

            if (htmlFile != null) {
                return htmlFile.getURL();
            }
        } catch (IOException e) {
            ErrorManager.getDefault()
                        .annotate(e, Bundle.ProjectUtilities_FailedCopyAppletFileMsg(e.getMessage()));
            ErrorManager.getDefault().notify(ErrorManager.ERROR, e);

            return null;
        }

        return null;
    }

    public static void fetchSubprojects(final Project project, final Set<Project> projects) {
        // process possible subprojects
        SubprojectProvider spp = project.getLookup().lookup(SubprojectProvider.class);

        if (spp != null) {
            for (Iterator it = spp.getSubprojects().iterator(); it.hasNext();) {
                Project p = (Project) it.next();

                if (p != null) // NOTE: workaround for Issue 121157 for NetBeans 6.0 FCS branch, will be removed in trunk!!!
                    if (projects.add(p)) {
                        fetchSubprojects(p, projects);
                    }
            }
        }
    }

    public static URL generateAppletHTML(Project project, PropertyEvaluator props, FileObject profiledClassFile) {
        String buildDirProp = props.getProperty("build.dir"); //NOI18N
        String classesDirProp = props.getProperty("build.classes.dir"); //NOI18N
        String activePlatformName = props.getProperty("platform.active"); //NOI18N

        FileObject buildFolder = getOrCreateBuildFolder(project, buildDirProp);
        FileObject classesDir = FileUtil.toFileObject(PropertyUtils.resolveFile(FileUtil.toFile(project.getProjectDirectory()),
                                                                                classesDirProp));

        if (classesDir == null) {
            try {
                classesDir = FileUtil.createFolder(project.getProjectDirectory(), classesDirProp);
            } catch (IOException e) {
                ErrorManager.getDefault()
                            .annotate(e, Bundle.ProjectUtilities_FailedCreateClassesDirMsg(e.getMessage()));
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);

                return null;
            }
        }

        try {
            return AppletSupport.generateHtmlFileURL(profiledClassFile, buildFolder, classesDir, activePlatformName);
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault()
                        .annotate(e, Bundle.ProjectUtilities_FailedGenerateAppletFileMsg(e.getMessage()));
            ErrorManager.getDefault().notify(ErrorManager.ERROR, e);

            return null;
        }
    }

    public static boolean hasAction(Project project, String actionName) {
        ActionProvider ap = project.getLookup().lookup(ActionProvider.class);

        if (ap == null) {
            return false; // return false if no ActionProvider available
        }

        String[] actions = ap.getSupportedActions();

        for (int i = 0; i < actions.length; i++) {
            if ((actions[i] != null) && actionName.equals(actions[i])) {
                return true;
            }
        }

        return false;
    }

    public static void invokeAction(Project project, String s) {
        ActionProvider ap = project.getLookup().lookup(ActionProvider.class);

        if (ap == null) {
            return; // fail early
        }

        Lookup lkp = null;
        if (NetBeansProfiler.getDefaultNB().getProfiledSingleFile() != null) {
            try {
                lkp = new ProxyLookup(Lookup.getDefault(), Lookups.fixed(DataObject.find(NetBeansProfiler.getDefaultNB().getProfiledSingleFile())));
            } catch (DataObjectNotFoundException ex) {
                lkp = Lookup.getDefault();
            }
        } else {
            lkp = Lookup.getDefault();
        }

        ap.invokeAction(s, lkp);
    }

    /**
     * Asks user for name of main class
     *
     * @param project     the project in question
     * @param mainClass   current main class
     * @param projectName the name of project
     * @param messageType type of dialog -1 when the main class is not set, -2 when the main class in not valid
     * @return true if user selected main class
     */
    public static String selectMainClass(Project project, String mainClass, String projectName, int messageType) {
        boolean canceled;
        final JButton okButton = new JButton(NbBundle.getMessage(MainClassWarning.class, "LBL_MainClassWarning_ChooseMainClass_OK") //NOI18N
        ); // NOI18N
        okButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MainClassWarning.class,
                                                                                     "AD_MainClassWarning_ChooseMainClass_OK") //NOI18N
        ); // NOI18N

        // main class goes wrong => warning
        String message;

        switch (messageType) {
            case -1:
                message = MessageFormat.format(NbBundle.getMessage(MainClassWarning.class, "LBL_MainClassNotFound"),
                                               new Object[] {  // NOI18N
                    projectName });

                break;
            case -2:
                message = MessageFormat.format(NbBundle.getMessage(MainClassWarning.class, "LBL_MainClassWrong"),
                                               new Object[] {  // NOI18N
                    mainClass, projectName });

                break;
            default:
                throw new IllegalArgumentException();
        }

        final MainClassWarning panel = new MainClassWarning(message, project);
        Object[] options = new Object[] { okButton, DialogDescriptor.CANCEL_OPTION };

        panel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (e.getSource() instanceof MouseEvent && MouseUtils.isDoubleClick(((MouseEvent) e.getSource()))) {
                        // click button and the finish dialog with selected class
                        okButton.doClick();
                    } else {
                        okButton.setEnabled(panel.getSelectedMainClass() != null);
                    }
                }
            });

        okButton.setEnabled(false);

        DialogDescriptor desc = new DialogDescriptor(panel,
                                                     NbBundle.getMessage(MainClassWarning.class, "CTL_MainClassWarning_Title",
                                                                         ProjectUtils.getInformation(project).getDisplayName() //NOI18N
        ), // NOI18N
                                                     true, options, options[0], DialogDescriptor.BOTTOM_ALIGN, null, null);
        desc.setMessageType(DialogDescriptor.INFORMATION_MESSAGE);

        Dialog dlg = DialogDisplayer.getDefault().createDialog(desc);
        dlg.setVisible(true);

        if (desc.getValue() != options[0]) {
            canceled = true;
        } else {
            mainClass = panel.getSelectedMainClass();
            canceled = false;
        }

        dlg.dispose();

        if (canceled) {
            return null;
        } else {
            return mainClass;
        }
    }

    public static void unintegrateProfiler(Project project) {
        String projectName = ProjectUtils.getInformation(project).getDisplayName();

        if (isProfilerIntegrated(project)) {
            if (!ProfilerDialogs.displayConfirmation(Bundle.ProjectUtilities_ProfilerWillBeUnintegratedMsg(projectName))) {
                return; // cancelled by the user
            }
        } else {
            if (!ProfilerDialogs.displayConfirmation(Bundle.ProjectUtilities_ProfilerIsntIntegratedMsg(projectName))) {
                return; // cancelled by the user
            }
        }

        boolean failed = false;
        StringBuilder exceptionsReport = new StringBuilder();

        // Move build-before-profiler.xml back to build.xml
        FileLock buildBackupFileLock = null;
        FileLock buildBackup2FileLock = null;

        try {
            final FileObject buildFile = AntProjectSupport.get(project).getProjectBuildScript();
            final FileObject buildBackupFile = project.getProjectDirectory().getFileObject("build-before-profiler.xml"); //NOI18N

            if (buildFile != null && (buildBackupFile != null && buildBackupFile.isValid())) {
                try {
                    buildBackupFileLock = buildBackupFile.lock();

                    if ((buildFile != null) && buildFile.isValid()) {
                        buildFile.delete();
                    }

                    buildBackupFile.rename(buildBackupFileLock, "build", "xml"); //NOI18N
                } catch (Exception e) {
                    failed = true;
                    exceptionsReport.append(Bundle.ProjectUtilities_RenamingBuildFailedMsg(e.getMessage()));
                    ProfilerLogger.log(e);
                }
            }
        } finally {
            if (buildBackupFileLock != null) {
                buildBackupFileLock.releaseLock();
            }
        }

        // Remove profiler-build-impl.xml
        FileLock buildImplFileLock = null;

        try {
            final FileObject buildImplFile = project.getProjectDirectory().getFileObject("nbproject")
                                                    .getFileObject("profiler-build-impl.xml"); //NOI18N

            try {
                if ((buildImplFile != null) && buildImplFile.isValid()) {
                    buildImplFile.delete();
                }
            } catch (Exception e) {
                failed = true;
                exceptionsReport.append(Bundle.ProjectUtilities_RemovingBuildFailedMsg(e.getMessage()));
                ProfilerLogger.log(e);
            }
        } finally {
            if (buildImplFileLock != null) {
                buildImplFileLock.releaseLock();
            }
        }

        // Remove data element from private/private.xml
        ProjectUtils.getAuxiliaryConfiguration(project).removeConfigurationFragment("data", PROFILER_NAME_SPACE, false); // NOI18N

        try {
            ProjectManager.getDefault().saveProject(project);
        } catch (Exception e) {
            failed = true;
            exceptionsReport.append(Bundle.ProjectUtilities_RemovingDataFailedMsg(e.getMessage()));
            ProfilerLogger.log(e);
        }

        if (failed) {
            ProfilerDialogs.displayError(Bundle.ProjectUtilities_UnintegrationErrorsOccuredMsg(exceptionsReport.toString()));
        } else {
            ProfilerDialogs.displayInfo(Bundle.ProjectUtilities_UnintegrationSuccessfulMsg(projectName));
        }
    }
    
    private static void getSourceRoots(final Project project, final boolean traverse, Set<Project> projects, Set<FileObject> roots) {
        final Sources sources = ProjectUtils.getSources(project);

        for (SourceGroup sg : sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            roots.add(sg.getRootFolder());
        }

        if (traverse) {
            // process possible subprojects
            SubprojectProvider spp = project.getLookup().lookup(SubprojectProvider.class);

            if (spp != null) {
                for (Project p : spp.getSubprojects()) {
                    if (projects.add(p)) {
                        getSourceRoots(p, traverse, projects, roots);
                    }
                }
            }
        }
    }

    // --- private part ----------------------------------------------------------------------------------------------------
    private static void addSubpackages(ArrayList<String> packages, String prefix, FileObject packageFO) {
        if (!packageFO.isFolder()) { // not a folder

            return;
        }

        FileObject[] children = packageFO.getChildren();

        // 1. check if there are java sources in this folder and if so, add to the list of packages
        if (!packages.contains(prefix)) { // already in there, skip this

            for (int i = 0; i < children.length; i++) {
                FileObject child = children[i];

                if (child.getExt().equals("java")) { //NOI18N
                    packages.add(prefix);

                    break;
                }
            }
        }

        // 2. recurse into subfolders
        for (int i = 0; i < children.length; i++) {
            FileObject child = children[i];

            if (child.isFolder()) {
                if ("".equals(prefix)) { //NOI18N
                    addSubpackages(packages, child.getName(), child);
                } else {
                    addSubpackages(packages, prefix + "." + child.getName(), child); //NOI18N
                }
            }
        }
    }

    private static boolean hasSubprojects(Project project) {
        SubprojectProvider spp = project.getLookup().lookup(SubprojectProvider.class);

        if (spp == null) {
            return false;
        }

        return spp.getSubprojects().size() > 0;
    }
}
