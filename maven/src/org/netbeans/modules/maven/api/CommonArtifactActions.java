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
 * Contributor(s): theanuradha@netbeans.org
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.api;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.netbeans.modules.maven.actions.ViewBugTrackerAction;
import org.netbeans.modules.maven.actions.ViewJavadocAction;
import org.netbeans.modules.maven.actions.ViewProjectHomeAction;
import org.netbeans.modules.maven.actions.scm.CheckoutAction;
import org.netbeans.modules.maven.actions.scm.SCMActions;
import org.netbeans.modules.maven.actions.usages.FindArtifactUsages;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.indexer.api.ui.ArtifactViewer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class CommonArtifactActions {
    
    public static Action createViewProjectHomeAction(Artifact artifact, List<ArtifactRepository> repos) {
        return new ViewProjectHomeAction(artifact, repos);
    }
    /**
     * @deprecated use th method with list of remote repos.
     * @param artifact
     * @return
     */
    public @Deprecated static Action createViewProjectHomeAction(Artifact artifact) {
       return createViewProjectHomeAction(artifact,
               Collections.singletonList(
               EmbedderFactory.createRemoteRepository(EmbedderFactory.getProjectEmbedder(),
               "http://repo1.maven.org/maven2", "central"))); //NOI18N
    }
    
    public static Action createViewJavadocAction(Artifact artifact) {
       return new ViewJavadocAction(artifact);

    }
    
    public static Action createViewBugTrackerAction(Artifact artifact, List<ArtifactRepository> repos) {
        return new ViewBugTrackerAction(artifact, repos);
    }

    /**
     * @deprecated
     * @param artifact
     * @return
     */
    public @Deprecated static Action createViewBugTrackerAction(Artifact artifact) {
       return createViewBugTrackerAction(artifact,
               Collections.singletonList(
               EmbedderFactory.createRemoteRepository(EmbedderFactory.getProjectEmbedder(),
               "http://repo1.maven.org/maven2", "central"))); //NOI18N
    }
    
    public static Action createSCMActions(Artifact artifact, List<ArtifactRepository> repos) {
        return new SCMActions(artifact, repos);
    }

    /**
     * @deprecated
     * @param artifact
     * @return
     */
    public @Deprecated static Action createSCMActions(Artifact artifact) {
       return createSCMActions(artifact,
               Collections.singletonList(
               EmbedderFactory.createRemoteRepository(EmbedderFactory.getProjectEmbedder(),
               "http://repo1.maven.org/maven2", "central"))); //NOI18N
    }

    public static Action createFindUsages(Artifact artifact) {
        
        return new FindArtifactUsages(artifact);
    }

    public static Action createViewArtifactDetails(Artifact art, List<ArtifactRepository> remoteRepos) {
        return new ShowArtifactAction(art, remoteRepos);
    }

    /**
     * create an action instance that performs scm checkout based on the MavenProject
     * instance provided in the lookup parameter. If no MavenProject is provided
     * up front it will listen on addition later. Without a MavenProject instance, it's disabled.
     * @param lkp
     * @return
     */
    public static Action createScmCheckoutAction(Lookup lkp) {
        return new CheckoutAction(lkp);
    }

    private static class ShowArtifactAction extends AbstractAction {
        private Artifact artifact;
        private List<ArtifactRepository> repos;

        ShowArtifactAction(Artifact art, List<ArtifactRepository> repos) {
            this.artifact = art;
            this.repos = repos;
            putValue(NAME, NbBundle.getMessage(ShowArtifactAction.class, "ACT_View_Details"));
        }

        public void actionPerformed(ActionEvent e) {
            ArtifactViewer.showArtifactViewer(artifact, repos);
        }
    }
    
}
