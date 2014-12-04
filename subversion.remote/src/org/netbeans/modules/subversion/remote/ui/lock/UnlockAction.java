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
package org.netbeans.modules.subversion.remote.ui.lock;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.subversion.remote.FileInformation;
import org.netbeans.modules.subversion.remote.Subversion;
import org.netbeans.modules.subversion.remote.api.ISVNNotifyListener;
import org.netbeans.modules.subversion.remote.api.ISVNStatus;
import org.netbeans.modules.subversion.remote.api.SVNClientException;
import org.netbeans.modules.subversion.remote.api.SVNNodeKind;
import org.netbeans.modules.subversion.remote.api.SVNUrl;
import org.netbeans.modules.subversion.remote.client.SvnClient;
import org.netbeans.modules.subversion.remote.client.SvnClientExceptionHandler;
import org.netbeans.modules.subversion.remote.client.SvnProgressSupport;
import org.netbeans.modules.subversion.remote.ui.actions.ContextAction;
import org.netbeans.modules.subversion.remote.util.Context;
import org.netbeans.modules.subversion.remote.util.SvnUtils;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author ondra
 */
public class UnlockAction extends ContextAction {
    
    private final static Logger LOG = Logger.getLogger(UnlockAction.class.getName());
    
    @Override
    protected int getDirectoryEnabledStatus () {
        return 0;
    }

    @Override
    protected int getFileEnabledStatus () {
        return FileInformation.STATUS_LOCKED | FileInformation.STATUS_LOCKED_REMOTELY;
    }

    @Override
    protected String getBaseName (Node[] activatedNodes) {
        return "CTL_Unlock_Title"; //NOI18N
    }

    @Override
    protected void performContextAction (Node[] nodes) {
        Context ctx = getContext(nodes);
        if(!Subversion.getInstance().checkClientAvailable(ctx)) {
            return;
        }
        final VCSFileProxy[] files = ctx.getFiles();
        if (files.length == 0) {
            return;
        }
        final SVNUrl url;
        try {
            url = SvnUtils.getRepositoryRootUrl(files[0]);
        } catch (SVNClientException ex) {
            LOG.log(Level.INFO, "No url for {0}", files[0]); //NOI18N
            return;
        }
        new SvnProgressSupport() {
            @Override
            protected void perform () {
                try {
                    SvnClient client = Subversion.getInstance().getClient(new Context(files), url, this);
                    Map<VCSFileProxy, String> paths = new HashMap<VCSFileProxy, String>();
                    for (VCSFileProxy f : files) {
                        paths.put(f, f.getPath());
                    }
                    boolean cont;
                    boolean force = false;
                    do {
                        cont = false;
                        boolean resumeAuth = true;
                            LockedFilesListener list;
                            do {
                                list = new LockedFilesListener(paths);
                                client.addNotifyListener(list);
                                client.unlock(files, force);
                                if (list.isAuthError() && (resumeAuth = SvnClientExceptionHandler.handleAuth(url))) {
                                    client.removeNotifyListener(list);
                                    client = Subversion.getInstance().getClient(new Context(files), url, this);
                                } else {
                                    break;
                                }
                            } while (resumeAuth);
                            if (!resumeAuth) {
                                break;
                            }
                        client.removeNotifyListener(list);
                        if (!force && !list.lockedFiles.isEmpty() && !lockedByOther(client, list.lockedFiles).isEmpty()) {
                            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(NbBundle.getMessage(UnlockAction.class, "MSG_UnlockAction.lockedFiles.description"), //NOI18N
                                    NbBundle.getMessage(UnlockAction.class, "MSG_UnlockAction.lockedFiles.title"), //NOI18N
                                    NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.WARNING_MESSAGE);
                            if (NotifyDescriptor.YES_OPTION == DialogDisplayer.getDefault().notify(nd)) {
                                cont = force = true;
                            }
                        }
                    } while (cont);
                    Subversion.getInstance().getStatusCache().refreshAsync(files);
                } catch (SVNClientException ex) {
                    SvnClientExceptionHandler.notifyException(ex, true, false);
                }
            }

            private Collection<VCSFileProxy> lockedByOther (SvnClient client, Set<VCSFileProxy> lockedFiles) throws SVNClientException {
                List<VCSFileProxy> lockedByOtherFiles = new LinkedList<VCSFileProxy>();
                ISVNStatus[] statuses = client.getStatus(lockedFiles.toArray(new VCSFileProxy[lockedFiles.size()]));
                for (ISVNStatus status : statuses) {
                    if (status.getLockOwner() == null) {
                        // not locked in this WC
                        lockedByOtherFiles.add(status.getFile());
                    }
                }
                return lockedByOtherFiles;
            }
        }.start(Subversion.getInstance().getRequestProcessor(url), url, NbBundle.getMessage(UnlockAction.class, "LBL_Unlock_Progress")); //NOI18N
    }
    
    private static class LockedFilesListener implements ISVNNotifyListener {
        private final Map<VCSFileProxy, String> paths;
        private final Set<VCSFileProxy> lockedFiles = new HashSet<VCSFileProxy>();
        private boolean authError;

        public LockedFilesListener (Map<VCSFileProxy, String> paths) {
            this.paths = paths;
        }
        
        @Override
        public void setCommand (ISVNNotifyListener.Command i) {
        }

        @Override
        public void logCommandLine (String string) {
        }

        @Override
        public void logMessage (String string) {
        }

        @Override
        public void logError (String error) {
            if (error == null) {
                // not interested
            } else if (error.contains("is not locked in this working copy")) { //NOI18N
                for (Iterator<Map.Entry<VCSFileProxy, String>> it = paths.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<VCSFileProxy, String> e = it.next();
                    String path = e.getValue();
                    VCSFileProxy file = e.getKey();
                    if (error.contains(MessageFormat.format("''{0}'' is not locked in this working copy", path))) { //NOI18N
                        // svnkit
                        lockedFiles.add(new File(path));
                        it.remove();
                        break;
                    } else if (error.contains(MessageFormat.format("''{0}'' is not locked in this working copy", file.getName()))) { //NOI18N
                        // javahl
                        lockedFiles.add(file);
                        it.remove();
                        break;
                    }
                }
            } else if (SvnClientExceptionHandler.isAuthentication(error)) {
                authError = true;
            }
        }

        @Override
        public void logRevision (long l, String string) {
        }

        @Override
        public void logCompleted (String string) {
        }

        @Override
        public void onNotify (VCSFileProxy file, SVNNodeKind svnnk) {
        }

        private boolean isAuthError () {
            return authError;
        }
    }
}
