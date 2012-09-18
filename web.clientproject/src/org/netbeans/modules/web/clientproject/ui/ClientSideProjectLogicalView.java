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
package org.netbeans.modules.web.clientproject.ui;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.clientproject.ClientSideProject;
import org.netbeans.modules.web.clientproject.ClientSideProjectConstants;
import org.netbeans.modules.web.clientproject.ClientSideProjectType;
import org.netbeans.modules.web.clientproject.remote.RemoteFS;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.actions.FileSystemAction;
import org.openide.actions.FindAction;
import org.openide.actions.PasteAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class ClientSideProjectLogicalView implements LogicalViewProvider {

    private ClientSideProject project;

    public ClientSideProjectLogicalView(ClientSideProject project) {
        this.project = project;
    }

    @Override
    public Node createLogicalView() {
        try {
            FileObject root = project.getProjectDirectory();

            DataFolder df =
                    DataFolder.findFolder(root);

            Node node = df.getNodeDelegate();

            return new ClientSideProjectNode(node, project);

        } catch (DataObjectNotFoundException e) {
            Exceptions.printStackTrace(e);
            return new AbstractNode(Children.LEAF);
        }
    }

    @Override
    public Node findPath(Node root, Object target) {
        Project prj = root.getLookup().lookup(Project.class);
        if (prj == null) {
            return null;
        }

        FileObject fo;
        if (target instanceof FileObject) {
            fo = (FileObject) target;
        } else if (target instanceof DataObject) {
            fo = ((DataObject) target).getPrimaryFile();
        } else {
            // unsupported object
            return null;
        }
        // first check project
        Project owner = FileOwnerQuery.getOwner(fo);
        if (!prj.equals(owner)) {
            // another project
            return null;
        }

        // XXX later, use source roots here
        for (Node node : root.getChildren().getNodes(true)) {
            FileObject kid = node.getLookup().lookup(FileObject.class);
            if (kid == null) {
                continue;
            }
            if (kid == fo) {
                return node;
            } else if (FileUtil.isParentOf(kid, fo)) {
                Node found = findNode(node, kid, fo);
                if (found != null && hasObject(found, target)) {
                    return found;
                }
            }
        }
        return null;
    }

    private static Node findNode(Node node, FileObject root, FileObject fo) {
        String relPath = FileUtil.getRelativePath(root, fo);

        // first, try to find the file without extension (more common case)
        String[] path = relPath.split("/"); // NOI18N
        path[path.length - 1] = fo.getName();
        Node found = findNode(node, path);
        if (found != null) {
            return found;
        }
        // file not found, try to search for the name with the extension
        path[path.length - 1] = fo.getNameExt();
        return findNode(node, path);
    }

    private static Node findNode(Node start, String[] path) {
        Node found = null;
        try {
            found = NodeOp.findPath(start, path);
        } catch (NodeNotFoundException ex) {
            // ignored
        }
        return found;
    }

    private boolean hasObject(Node node, Object obj) {
        if (obj == null) {
            return false;
        }
        FileObject fileObject = node.getLookup().lookup(FileObject.class);
        if (fileObject == null) {
            return false;
        }
        if (obj instanceof DataObject) {
            DataObject dataObject = node.getLookup().lookup(DataObject.class);
            if (dataObject == null) {
                return false;
            }
            if (dataObject.equals(obj)) {
                return true;
            }
            return hasObject(node, ((DataObject) obj).getPrimaryFile());
        } else if (obj instanceof FileObject) {
            return obj.equals(fileObject);
        }
        return false;
    }


/** This is the node you actually see in the project tab for the project */
    private static final class ClientSideProjectNode extends FilterNode {

        final ClientSideProject project;

        public ClientSideProjectNode(Node node, ClientSideProject project) throws DataObjectNotFoundException {
            super(node, new ClientSideProjectChildren(project),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both
                    new ProxyLookup(new Lookup[]{Lookups.singleton(project),
                        node.getLookup()
                    }));
            this.project = project;
        }

        @Override
        public Action[] getActions(boolean arg0) {
            return CommonProjectActions.forType(ClientSideProjectType.TYPE);
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(ClientSideProject.PROJECT_ICON);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getDisplayName() {
            return project.getName();
        }

        @NbBundle.Messages({
            "# {0} - project directory",
            "ClientSideProjectNode.description=HTML5 application in {0}"
        })
        @Override
        public String getShortDescription() {
            return Bundle.ClientSideProjectNode_description(FileUtil.getFileDisplayName(project.getProjectDirectory()));
        }

        @Override
        public boolean canCopy() {
            return false;
        }

        @Override
        public boolean canCut() {
            return false;
        }

        @Override
        public boolean canDestroy() {
            return false;
        }

        @Override
        public boolean canRename() {
            return false;
        }

    }

    private static enum BasicNodes {
        Sources,
        Tests,
        RemoteFiles,
        Configuration;
    }
    private static class ClientSideProjectChildren extends Children.Keys<BasicNodes> {

        // XXX threading! for all fields
        private ClientSideProject project;
        private FileObject siteRootFolder;
        private FileObject testsFolder;
        private FileObject configFolder;
        private boolean siteRootFolderEmpty;
        private boolean testsFolderEmpty;
        private boolean configFolderEmpty;

        public ClientSideProjectChildren(ClientSideProject p) {
            this.project = p;
            updateKeys();
            project.getRemoteFiles().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    updateKeys();
                }
            });
            siteRootFolder = project.getSiteRootFolder();
            siteRootFolderEmpty = siteRootFolder != null && siteRootFolder.getChildren().length == 0;
            testsFolder = project.getTestsFolder();
            testsFolderEmpty = testsFolder != null && testsFolder.getChildren().length == 0;
            configFolder = project.getConfigFolder();
            configFolderEmpty = configFolder != null && configFolder.getChildren().length == 0;
            project.getEvaluator().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (ClientSideProjectConstants.PROJECT_SITE_ROOT_FOLDER.equals(evt.getPropertyName())) {
                        refreshKeyInAWT(BasicNodes.Sources);
                    }
                    if (ClientSideProjectConstants.PROJECT_TEST_FOLDER.equals(evt.getPropertyName())) {
                        refreshKeyInAWT(BasicNodes.Tests);
                    }
                    if (ClientSideProjectConstants.PROJECT_CONFIG_FOLDER.equals(evt.getPropertyName())) {
                        refreshKeyInAWT(BasicNodes.Tests);
                    }
                }
            });

            // XXX: refactor listening; it is ugly!!
            project.getProjectDirectory().addRecursiveListener(new FileChangeAdapter() {
                @Override
                public void fileFolderCreated(FileEvent fe) {
                    if (siteRootFolder == null) {
                        siteRootFolder = project.getSiteRootFolder();
                        if (siteRootFolder != null) {
                            refreshKeyInAWT(BasicNodes.Sources);
                        }
                    } else if (siteRootFolderEmpty) {
                        siteRootFolderEmpty = siteRootFolder.getChildren().length == 0;
                        if (!siteRootFolderEmpty) {
                            refreshKeyInAWT(BasicNodes.Sources);
                        }
                    }
                    if (testsFolder == null) {
                        testsFolder = project.getTestsFolder();
                        if (testsFolder != null) {
                            refreshKeyInAWT(BasicNodes.Tests);
                        }
                    } else if (testsFolderEmpty) {
                        testsFolderEmpty = testsFolder.getChildren().length == 0;
                        if (!testsFolderEmpty) {
                            refreshKeyInAWT(BasicNodes.Tests);
                        }
                    }
                    if (configFolder == null) {
                        configFolder = project.getConfigFolder();
                        if (configFolder != null) {
                            refreshKeyInAWT(BasicNodes.Configuration);
                        }
                    } else if (configFolderEmpty) {
                        configFolderEmpty = configFolder.getChildren().length == 0;
                        if (!configFolderEmpty) {
                            refreshKeyInAWT(BasicNodes.Configuration);
                        }
                    }
                }

                @Override
                public void fileDataCreated(FileEvent fe) {
                    if (siteRootFolder != null && siteRootFolderEmpty) {
                        siteRootFolderEmpty = siteRootFolder.getChildren().length == 0;
                        if (!siteRootFolderEmpty) {
                            refreshKeyInAWT(BasicNodes.Sources);
                        }
                    }
                    if (testsFolder != null && testsFolderEmpty) {
                        testsFolderEmpty = testsFolder.getChildren().length == 0;
                        if (!testsFolderEmpty) {
                            refreshKeyInAWT(BasicNodes.Tests);
                        }
                    }
                    if (configFolder != null && configFolderEmpty) {
                        configFolderEmpty = configFolder.getChildren().length == 0;
                        if (!configFolderEmpty) {
                            refreshKeyInAWT(BasicNodes.Configuration);
                        }
                    }
                }


                @Override
                public void fileDeleted(FileEvent fe) {
                    if (siteRootFolder != null) {
                        if (!siteRootFolder.isValid()) {
                            siteRootFolder = null;
                            siteRootFolderEmpty = true;
                            refreshKeyInAWT(BasicNodes.Sources);
                        } else {
                            if (!siteRootFolderEmpty) {
                                siteRootFolderEmpty = siteRootFolder.getChildren().length == 0;
                                if (siteRootFolderEmpty) {
                                    refreshKeyInAWT(BasicNodes.Sources);
                                }
                            }
                        }
                    }
                    if (testsFolder != null) {
                        if (!testsFolder.isValid()) {
                            testsFolder = null;
                            testsFolderEmpty = true;
                            refreshKeyInAWT(BasicNodes.Tests);
                        } else {
                            if (!testsFolderEmpty) {
                                testsFolderEmpty = testsFolder.getChildren().length == 0;
                                if (testsFolderEmpty) {
                                    refreshKeyInAWT(BasicNodes.Tests);
                                }
                            }
                        }
                    }
                    if (configFolder != null) {
                        if (!configFolder.isValid()) {
                            configFolder = null;
                            configFolderEmpty = true;
                            refreshKeyInAWT(BasicNodes.Configuration);
                        } else {
                            if (!configFolderEmpty) {
                                configFolderEmpty = configFolder.getChildren().length == 0;
                                if (configFolderEmpty) {
                                    refreshKeyInAWT(BasicNodes.Configuration);
                                }
                            }
                        }
                    }
                }
            });
        }

        private void refreshKeyInAWT(final BasicNodes type) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshKey(type);
                }
            });
        }

        @Override
        protected Node[] createNodes(BasicNodes k) {
            switch (k) {
                case Sources:
                    return createNodeForFolder(k, project.getSiteRootFolder(), new String[]{"nbproject", "build"}); //NOI18N
                case Tests:
                    return createNodeForFolder(k, project.getTestsFolder(), new String[]{"nbproject", "build"}); //NOI18N
                case RemoteFiles:
                    return new Node[]{new RemoteFilesNode(project)};
                case Configuration:
                    return createNodeForFolder(k, project.getConfigFolder(), new String[0]);
                default:
                    return new Node[0];
            }
        }

        private Node[] createNodeForFolder(BasicNodes type, FileObject root, String[] ignoreList) {
            if (root != null && root.isValid()) {
                DataFolder df = DataFolder.findFolder(root);
                if (df.getChildren().length > 0 || type == BasicNodes.Sources) {
                    return new Node[]{new FolderFilterNode(type, df.getNodeDelegate(), ignoreList)};
                }
            }
            // missing root should be solved by project problems
            return new Node[0];
        }

        private void updateKeys() {
            ArrayList<BasicNodes> keys = new ArrayList<BasicNodes>();
            keys.add(BasicNodes.Sources);
            keys.add(BasicNodes.Tests);
            if (!project.getRemoteFiles().getRemoteFiles().isEmpty()) {
                keys.add(BasicNodes.RemoteFiles);
            }
            keys.add(BasicNodes.Configuration);
            setKeys(keys);
        }

    }

    private static final class FolderFilterNode extends FilterNode {

        private ClientSideProject project;
        private BasicNodes nodeType;
        private Node iconDelegate;
        private static final Image SOURCES_FILES_BADGE = ImageUtilities.loadImage("org/netbeans/modules/web/clientproject/ui/resources/sources-badge.gif", true); // NOI18N
        private static final Image TESTS_FILES_BADGE = ImageUtilities.loadImage("org/netbeans/modules/web/clientproject/ui/resources/tests-badge.gif", true); // NOI18N
        private static final Image CONFIGS_FILES_BADGE = ImageUtilities.loadImage("org/netbeans/modules/web/clientproject/ui/resources/config-badge.gif", true); // NOI18N

        public FolderFilterNode(BasicNodes nodeType, Node folderNode, String[] ignoreList) {
            super(folderNode, folderNode.isLeaf() ? Children.LEAF :
                    new FolderFilterChildren(folderNode, ignoreList));
            this.nodeType = nodeType;
            iconDelegate = DataFolder.findFolder (FileUtil.getConfigRoot()).getNodeDelegate();
        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.add(CommonProjectActions.newFileAction());
            actions.add(null);
            actions.add(SystemAction.get(FindAction.class));
            actions.add(null);
            actions.add(SystemAction.get(FileSystemAction.class));
            actions.add(null);
            actions.add(SystemAction.get(PasteAction.class));
            actions.add(null);
            actions.add(SystemAction.get(ToolsAction.class));
            actions.add(null);
            actions.add(CommonProjectActions.customizeProjectAction());
            return actions.toArray(new Action[actions.size()]);
        }

        @Override
        public boolean canRename() {
            return false;
        }

        @Override
        public Image getIcon(int type) {
            return computeIcon(nodeType, false, type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return computeIcon(nodeType, true, type);
        }

        private Image computeIcon(BasicNodes node, boolean opened, int type) {
            Image image;
            Image badge = null;
            switch (nodeType) {
                case Sources:
                    badge = SOURCES_FILES_BADGE;
                    break;
                case Tests:
                    badge = TESTS_FILES_BADGE;
                    break;
                case Configuration:
                    badge = CONFIGS_FILES_BADGE;
                    break;
            }

            image = opened ? iconDelegate.getOpenedIcon(type) : iconDelegate.getIcon(type);
            if (badge != null) {
                image = ImageUtilities.mergeImages(image, badge, 7, 7);
            }

            return image;
        }

        @Override
        public String getDisplayName() {
            switch (nodeType) {
                case Sources:
                    return java.util.ResourceBundle.getBundle("org/netbeans/modules/web/clientproject/ui/Bundle").getString("SITE_ROOT");
                case Tests:
                    return java.util.ResourceBundle.getBundle("org/netbeans/modules/web/clientproject/ui/Bundle").getString("UNIT_TESTS");
                case Configuration:
                    return java.util.ResourceBundle.getBundle("org/netbeans/modules/web/clientproject/ui/Bundle").getString("CONFIGURATION_FILES");
                default:
                    throw new AssertionError(nodeType.name());
            }
        }

    }

    private static class FolderFilterChildren extends FilterNode.Children {

        private String[] ignoreList;

        public FolderFilterChildren(Node n, String[] ignoreList) {
            super(n);
            this.ignoreList = ignoreList;
        }

        @Override
        protected Node[] createNodes(Node key) {
            for (String ignore : ignoreList) {
                if (key.getDisplayName().equals(ignore)) {
                    return new Node[0];
                }
            }
            return super.createNodes(key);
        }

    }

    @NbBundle.Messages("LBL_RemoteFiles=Remote Files")
    private static final class RemoteFilesNode extends AbstractNode {

        final ClientSideProject project;

        public RemoteFilesNode(ClientSideProject project) {
            super(new RemoteFilesChildren(project));
            this.project = project;
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage("org/netbeans/modules/web/clientproject/ui/resources/remotefiles.png"); //NOI18N
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getDisplayName() {
            return Bundle.LBL_RemoteFiles();
        }

    }

    private static class RemoteFilesChildren extends Children.Keys<RemoteFile> implements ChangeListener {

        final ClientSideProject project;

        public RemoteFilesChildren(ClientSideProject project) {
            this.project = project;
        }

        @Override
        protected Node[] createNodes(RemoteFile key) {
            FileObject fo = RemoteFS.getDefault().getFileForURL(key.getUrl());
            DataObject dobj;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException ex) {
                return new Node[] {};
            }
            return new Node[] { dobj.getNodeDelegate() };
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            project.getRemoteFiles().addChangeListener(this);
            updateKeys();
        }

        @Override
        protected void removeNotify() {
            super.removeNotify();
            project.getRemoteFiles().removeChangeListener(this);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            updateKeys();
        }

        private void updateKeys() {
            List<RemoteFile> keys = new ArrayList<RemoteFile>();
            for (URL u : project.getRemoteFiles().getRemoteFiles()) {
                keys.add(new RemoteFile(u));
            }
            Collections.sort(keys, new Comparator<RemoteFile>() {
                    @Override
                    public int compare(RemoteFile o1, RemoteFile o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
            setKeys(keys);
        }

    }
    
    public static class RemoteFile {
        private URL url;
        private String name;
        private String urlAsString;

        public RemoteFile(URL url) {
            this.url = url;
            urlAsString = url.toExternalForm();
            int index = urlAsString.lastIndexOf('/');
            if (index != -1) {
                name = urlAsString.substring(index+1);
            }
        }

        public URL getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return urlAsString;
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + (this.urlAsString != null ? this.urlAsString.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RemoteFile other = (RemoteFile) obj;
            if ((this.urlAsString == null) ? (other.urlAsString != null) : !this.urlAsString.equals(other.urlAsString)) {
                return false;
            }
            return true;
        }
        
    }

}