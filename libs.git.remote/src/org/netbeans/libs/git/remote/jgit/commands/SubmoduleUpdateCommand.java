/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.libs.git.remote.jgit.commands;

import java.net.URISyntaxException;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.netbeans.libs.git.remote.GitException;
import org.netbeans.libs.git.remote.GitSubmoduleStatus;
import org.netbeans.libs.git.remote.jgit.DelegatingGitProgressMonitor;
import org.netbeans.libs.git.remote.jgit.DelegatingProgressMonitor;
import org.netbeans.libs.git.remote.jgit.GitClassFactory;
import org.netbeans.libs.git.remote.jgit.JGitRepository;
import org.netbeans.libs.git.remote.jgit.Utils;
import org.netbeans.libs.git.remote.progress.ProgressMonitor;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;

/**
 *
 * @author Ondrej Vrabec
 */
public class SubmoduleUpdateCommand extends TransportCommand {
    
    private final VCSFileProxy[] roots;
    private final SubmoduleStatusCommand statusCmd;
    private final ProgressMonitor monitor;

    public SubmoduleUpdateCommand (JGitRepository repository, GitClassFactory classFactory,
            VCSFileProxy[] roots, ProgressMonitor monitor) {
        super(repository, classFactory, "origin", monitor);
        this.monitor = monitor;
        this.roots = roots;
        this.statusCmd = new SubmoduleStatusCommand(repository,
                    getClassFactory(), roots, new DelegatingGitProgressMonitor(monitor));
    }

    @Override
    protected void runTransportCommand () throws GitException {
        Repository repository = getRepository().getRepository();
        VCSFileProxy workTree = getRepository().getLocation();
        org.eclipse.jgit.api.SubmoduleUpdateCommand cmd = new Git(repository).submoduleUpdate();
        for (VCSFileProxy root : roots) {
            cmd.addPath(Utils.getRelativePath(workTree, root));
            try {
                cmd.setProgressMonitor(new DelegatingProgressMonitor(monitor));
                cmd.setCredentialsProvider(getCredentialsProvider());
                cmd.setTimeout(45);
                // if needed, transport can be set up using: cmd.setTransportConfigCallback();
                cmd.call();
            } catch (TransportException e) {
                URIish uriish = null;
                try {
                    uriish = getUriWithUsername(false);
                } catch (URISyntaxException ex) {
                    throw new GitException(e.getMessage(), e);
                }
                Utils.deleteRecursively(VCSFileProxy.createFileProxy(root, Constants.DOT_GIT));
                handleException(new org.eclipse.jgit.errors.TransportException(e.getMessage(), e), uriish);
            } catch (GitAPIException | JGitInternalException ex) {
                throw new GitException(ex);
            }
        }
        statusCmd.run();
    }

    @Override
    protected String getCommandDescription () {
        StringBuilder sb = new StringBuilder("git submodule update"); //NOI18N
        for (VCSFileProxy root : roots) {
            sb.append(" ").append(root.getPath());
        }
        return sb.toString();
    }

    public Map<VCSFileProxy, GitSubmoduleStatus> getStatuses () {
        return statusCmd.getStatuses();
    }
    
}
