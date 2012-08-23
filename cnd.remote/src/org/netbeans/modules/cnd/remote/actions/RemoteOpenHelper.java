/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.remote.actions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileView;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.cnd.api.remote.RemoteFileUtil;
import org.netbeans.modules.cnd.utils.CndPathUtilitities;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager.CancellationException;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.netbeans.modules.remote.spi.FileSystemProvider;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * @author Vladimir Kvashin
 */
public class RemoteOpenHelper {
    
    private final ExecutionEnvironment env;

    private static final Logger LOGGER = Logger.getLogger("cnd.remote.logger"); //NOI18N
    private static boolean isRunning = false;
    private static final Object lock = new Object();
    private static RequestProcessor RP = new RequestProcessor("Opening remote project", 1); //NOI18N
    private static Map<ExecutionEnvironment, String> lastUsedDirs = new HashMap<ExecutionEnvironment, String>();
    
    private RemoteOpenHelper(ExecutionEnvironment env) {
        this.env = env;
    }

    public static void openFile(ExecutionEnvironment env) {
        new RemoteOpenHelper(env).openFile();
    }

    public static void openProject(ExecutionEnvironment env) {
        new RemoteOpenHelper(env).openProject();
    }
    
    private void openFile() {
        Runnable edtWorker = new Runnable() {
            @Override
            public void run() {
                openRemoteFile(env);
            }
        };
        openImpl(edtWorker);
    }

    private void openProject() {
        Runnable edtWorker = new Runnable() {
            @Override
            public void run() {
                openRemoteProject(env);
            }
        };
        openImpl(edtWorker);
    }
    
    private void openImpl(final Runnable edtWorker) {        
        final Runnable connectWrapper = new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionManager.getInstance().connectTo(env);
                    SwingUtilities.invokeLater(edtWorker);
                } catch (final IOException ex) {
                    LOGGER.log(Level.INFO, "Error connecting " + env, ex);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), 
                                NbBundle.getMessage(OpenRemoteProjectAction.class, "ErrorConnectingHost", env.getDisplayName(), ex.getMessage()));
                        }
                    });
                } catch (CancellationException ex) {
                    // don't report CancellationException
                } finally {
                    synchronized (lock) {
                        isRunning = false;
                    }                    
                }
            }
        };
        synchronized (lock) {
            if (!isRunning) {
                isRunning = true;
                RP.post(connectWrapper);
            }
        }        
    }
    
    private static String getCurrentDirectory(ExecutionEnvironment env) {
        if (Boolean.getBoolean("netbeans.openfile.197063")) {
            // Prefer to open from parent of active editor, if any.
            TopComponent activated = TopComponent.getRegistry().getActivated();
            if (activated != null && WindowManager.getDefault().isOpenedEditorTopComponent(activated)) {
                DataObject d = activated.getLookup().lookup(DataObject.class);
                if (d != null) {
                    FileObject primaryFile = d.getPrimaryFile();
                    if (primaryFile != null && primaryFile.isValid()) {
                        try {
                            if (primaryFile.getFileSystem().equals(FileSystemProvider.getFileSystem(env))) {
                                FileObject parent = primaryFile.getParent();
                                if (parent != null && parent.isValid()) {
                                    return parent.getPath();
                                }
                            }
                        } catch (FileStateInvalidException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }
        String homeDir = lastUsedDirs.get(env);
        if (homeDir == null) {
            homeDir = getRemoteHomeDir(env);
        }            
        return homeDir;
    }

    private void openRemoteFile(final ExecutionEnvironment env) {            
        String homeDir = getCurrentDirectory(env);
        final JFileChooser fileChooser = RemoteFileUtil.createFileChooser(env,
            NbBundle.getMessage(OpenRemoteProjectAction.class, "OpenFileTitle"),
            NbBundle.getMessage(OpenRemoteProjectAction.class, "OpenFileButtonText"),
            JFileChooser.FILES_ONLY, null, homeDir, true);
        int ret = fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (ret == JFileChooser.CANCEL_OPTION) {
            return;
        }
        Runnable worker = new Runnable() {
            @Override
            public void run() {
                FileObject fo = FileSystemProvider.fileToFileObject(fileChooser.getSelectedFile());
                lastUsedDirs.put(env, fo.getParent().getPath());
                if (fo == null || !fo.isValid() || ! fo.isData()) {
                    return;
                }
                try {
                    DataObject dob = DataObject.find(fo);
                    EditorCookie ec = dob.getCookie(EditorCookie.class);
                    if (ec != null) {
                        ec.open();
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        RP.post(worker);
    }

    private void openRemoteProject(final ExecutionEnvironment env) {            
        String homeDir = lastUsedDirs.get(env);
        if (homeDir == null) {
            homeDir = getRemoteProjectDir(env);
        }            
        final JFileChooser fileChooser = RemoteFileUtil.createFileChooser(env,
            NbBundle.getMessage(OpenRemoteProjectAction.class, "OpenProjectTitle"),
            NbBundle.getMessage(OpenRemoteProjectAction.class, "OpenProjectButtonText"),
            JFileChooser.DIRECTORIES_ONLY, null, homeDir, true);
        fileChooser.setFileView(new ProjectSelectionFileView(fileChooser));
        int ret = fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (ret == JFileChooser.CANCEL_OPTION) {
            return;
        }
        Runnable worker = new Runnable() {
            @Override
            public void run() {
                FileObject remoteProjectFO = FileSystemProvider.fileToFileObject(fileChooser.getSelectedFile());
                lastUsedDirs.put(env, remoteProjectFO.getParent().getPath());
                if (remoteProjectFO == null || !remoteProjectFO.isFolder()) {
                    return;
                }
                Project project;
                try {
                    project = ProjectManager.getDefault().findProject(remoteProjectFO);
                    if (project != null) {
                        OpenProjects.getDefault().open(new Project[] {project}, false, true);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        RP.post(worker);
    }
    
    private static String getRemoteProjectDir(ExecutionEnvironment env) {
        try {
            return HostInfoUtils.getHostInfo(env).getUserDir() + '/' + ProjectChooser.getProjectsFolder().getName() + '/';  //NOI18N
        } catch (IOException ex) {
            ex.printStackTrace(System.err); // it doesn't make sense to disturb user
        } catch (CancellationException ex) {
            ex.printStackTrace(System.err); // it doesn't make sense to disturb user
        }
        return null;
    }
    
    private static String getRemoteHomeDir(ExecutionEnvironment env) {
        try {
            return HostInfoUtils.getHostInfo(env).getUserDir() + '/';  //NOI18N
        } catch (IOException ex) {
            ex.printStackTrace(System.err); // it doesn't make sense to disturb user
        } catch (CancellationException ex) {
            ex.printStackTrace(System.err); // it doesn't make sense to disturb user
        }
        return null;
    }

    private static final class ProjectSelectionFileView extends FileView implements Runnable {

        private final JFileChooser chooser;
        private final Map<File, Icon> knownProjectIcons = new HashMap<File, Icon>();
        private final RequestProcessor.Task task = new RequestProcessor("ProjectIconFileView").create(this);//NOI18N
        private File lookingForIcon;

        public ProjectSelectionFileView(JFileChooser chooser) {
            this.chooser = chooser;
        }

        @Override
        public Icon getIcon(File f) {
            if (f.isDirectory() && // #173958: do not call ProjectManager.isProject now, could block
                    !f.toString().matches("/[^/]+") && // Unix: /net, /proc, etc. //NOI18N
                    f.getParentFile() != null) { // do not consider drive roots
                synchronized (this) {
                    Icon icon = knownProjectIcons.get(f);
                    if (icon != null) {
                        return icon;
                    } else if (lookingForIcon == null) {
                        lookingForIcon = f;
                        task.schedule(20);
                        // Only calculate one at a time.
                        // When the view refreshes, the next unknown icon
                        // should trigger the task to be reloaded.
                    }
                }
            }
            return chooser.getFileSystemView().getSystemIcon(f);
        }

        @Override
        public void run() {
            String path = lookingForIcon.getAbsolutePath();
            String project = path + "/nbproject"; // NOI18N
            File projectDir = chooser.getFileSystemView().createFileObject(project);
            Icon icon = chooser.getFileSystemView().getSystemIcon(lookingForIcon);
            if (projectDir.exists() && projectDir.isDirectory() && projectDir.canRead()) {
                String projectXml = path + "/nbproject/project.xml"; // NOI18N
                File projectFile = chooser.getFileSystemView().createFileObject(projectXml);
                if (projectFile.exists()) {
                    String conf = path + "/nbproject/configurations.xml"; // NOI18N
                    File configuration = chooser.getFileSystemView().createFileObject(conf);
                    if (configuration.exists()) {
                        icon = ImageUtilities.loadImageIcon("org/netbeans/modules/cnd/makeproject/ui/resources/makeProject.gif", true); // NOI18N
                    }
                }
            }
            synchronized (this) {
                knownProjectIcons.put(lookingForIcon, icon);
                lookingForIcon = null;
            }
            chooser.repaint();
        }
    }    
}