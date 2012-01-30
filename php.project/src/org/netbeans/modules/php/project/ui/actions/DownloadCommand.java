/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.actions;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.ProjectPropertiesSupport;
import org.netbeans.modules.php.project.ProjectSettings;
import org.netbeans.modules.php.project.connections.RemoteClient;
import org.netbeans.modules.php.project.connections.RemoteException;
import org.netbeans.modules.php.project.connections.common.RemoteUtils;
import org.netbeans.modules.php.project.connections.transfer.TransferFile;
import org.netbeans.modules.php.project.connections.transfer.TransferInfo;
import org.netbeans.modules.php.project.connections.ui.transfer.TransferFilesChooser;
import org.netbeans.modules.php.project.runconfigs.RunConfigRemote;
import org.netbeans.modules.php.project.ui.actions.support.CommandUtils;
import org.netbeans.modules.php.project.ui.actions.support.Displayable;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

/**
 * Download files from remote connection.
 * @author Tomas Mysik
 */
public class DownloadCommand extends RemoteCommand implements Displayable {
    public static final String ID = "download"; // NOI18N
    public static final String DISPLAY_NAME = NbBundle.getMessage(DownloadCommand.class, "LBL_DownloadCommand");

    public DownloadCommand(PhpProject project) {
        super(project);
    }

    @Override
    public String getCommandId() {
        return ID;
    }

    @Override
    protected Runnable getContextRunnable(final Lookup context) {
        return new Runnable() {
            @Override
            public void run() {
                invokeActionImpl(context);
            }
        };
    }

    void invokeActionImpl(Lookup context) {
        FileObject[] selectedFiles = CommandUtils.filesForContextOrSelectedNodes(context);
        // #161202
        if (selectedFiles.length == 0) {
            // one selects project node e.g.
            return;
        }

        FileObject sources = ProjectPropertiesSupport.getSourcesDirectory(getProject());
        if (!sourcesFilesOnly(sources, selectedFiles)) {
            return;
        }

        InputOutput remoteLog = getRemoteLog(RunConfigRemote.forProject(getProject()).getRemoteConfiguration().getDisplayName());
        RemoteClient remoteClient = getRemoteClient(remoteLog);
        String projectName = getProject().getName();
        download(remoteClient, remoteLog, projectName, true, sources, selectedFiles, null, getProject());
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public static void download(RemoteClient remoteClient, InputOutput remoteLog, String projectName,
            FileObject sources, Set<TransferFile> forDownload) {
        download(remoteClient, remoteLog, projectName, false, sources, null, forDownload, null);
    }

    private static void download(RemoteClient remoteClient, InputOutput remoteLog, String projectName,
            boolean showDownloadDialog, FileObject sources, FileObject[] filesToDownload, Set<TransferFile> transferFilesToDownload, PhpProject project) {

        Set<TransferFile> forDownload = prepareDownload(transferFilesToDownload, sources, filesToDownload, project, projectName, showDownloadDialog, remoteClient);
        download(forDownload, project, projectName, sources, filesToDownload, remoteLog, remoteClient);
    }

    private static Set<TransferFile> prepareDownload(Set<TransferFile> transferFilesToDownload, FileObject sources, FileObject[] filesToDownload,
            PhpProject project, String projectName, boolean showDownloadDialog, RemoteClient remoteClient) {
        Set<TransferFile> forDownload = Collections.emptySet();
        ProgressHandle progressHandle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(DownloadCommand.class, "MSG_DownloadingFiles", projectName), remoteClient);
        try {
            progressHandle.start();
            forDownload = transferFilesToDownload != null ? transferFilesToDownload : remoteClient.prepareDownload(sources, filesToDownload);
            if (showDownloadDialog) {
                boolean reallyShowDialog = true;
                if (forDownload.size() == 1
                        && forDownload.iterator().next().isFile()) {
                    // do not show transfer dialog for exactly one file (not folder!)
                    reallyShowDialog = false;
                }

                if (reallyShowDialog) {
                    long timestamp = project != null ? ProjectSettings.getLastDownload(project) : -1;
                    forDownload = TransferFilesChooser.forDownload(forDownload, timestamp).showDialog();
                }
            }
        } catch (RemoteException ex) {
            RemoteUtils.processRemoteException(ex);
        } finally {
            progressHandle.finish();
        }
        return forDownload;
    }

    private static void download(Set<TransferFile> forDownload, PhpProject project, String projectName, FileObject sources, FileObject[] filesToDownload,
            InputOutput remoteLog, RemoteClient remoteClient) {
        TransferInfo transferInfo = null;
        try {
            if (forDownload.size() > 0) {
                ProgressHandle progressHandle = ProgressHandleFactory.createHandle(
                        NbBundle.getMessage(DownloadCommand.class, "MSG_DownloadingFiles", projectName), remoteClient);
                DefaultOperationMonitor downloadOperationMonitor = new DefaultOperationMonitor(progressHandle, forDownload);
                remoteClient.setOperationMonitor(downloadOperationMonitor);
                transferInfo = remoteClient.download(forDownload);
                remoteClient.setOperationMonitor(null);
                StatusDisplayer.getDefault().setStatusText(
                        NbBundle.getMessage(DownloadCommand.class, "MSG_DownloadFinished", projectName));
                if (project != null
                        && !remoteClient.isCancelled()
                        && transferInfo.hasAnyTransfered()) { // #153406
                    rememberLastDownload(project, sources, filesToDownload);
                }
            }
        } catch (RemoteException ex) {
            RemoteUtils.processRemoteException(ex);
        } finally {
            try {
                remoteClient.disconnect();
            } catch (RemoteException ex) {
                RemoteUtils.processRemoteException(ex);
            }
            if (transferInfo != null) {
                processTransferInfo(transferInfo, remoteLog);
            }
        }
    }

    // #142955 - but remember only if one of the selected files is source directory
    //  (otherwise it would make no sense, consider this scenario: upload just one file -> remember timestamp
    //  -> upload another file or the whole project [timestamp is irrelevant])
    private static void rememberLastDownload(PhpProject project, FileObject sources, FileObject[] selectedFiles) {
        for (FileObject fo : selectedFiles) {
            if (sources.equals(fo)) {
                ProjectSettings.setLastDownload(project, TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
                return;
            }
        }
    }
}
