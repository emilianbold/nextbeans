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
 *
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
package org.netbeans.modules.mercurial.remote;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.Action;
import org.netbeans.modules.mercurial.remote.options.AnnotationColorProvider;
import org.netbeans.modules.mercurial.remote.ui.add.AddAction;
import org.netbeans.modules.mercurial.remote.ui.annotate.AnnotateAction;
import org.netbeans.modules.mercurial.remote.ui.branch.HgBranch;
import org.netbeans.modules.mercurial.remote.ui.commit.CommitAction;
import org.netbeans.modules.mercurial.remote.ui.log.HgLogMessage;
import org.netbeans.modules.mercurial.remote.ui.log.LogAction;
import org.netbeans.modules.mercurial.remote.ui.menu.BranchMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.ConflictsMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.DiffMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.IgnoreMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.PatchesMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.QueuesMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.RecoverMenu;
import org.netbeans.modules.mercurial.remote.ui.menu.RemoteMenu;
import org.netbeans.modules.mercurial.remote.ui.properties.PropertiesAction;
import org.netbeans.modules.mercurial.remote.ui.status.StatusAction;
import org.netbeans.modules.mercurial.remote.ui.update.RevertModificationsAction;
import org.netbeans.modules.mercurial.remote.ui.update.UpdateAction;
import org.netbeans.modules.mercurial.remote.util.HgUtils;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.api.VersioningSupport;
import org.netbeans.modules.versioning.core.spi.VCSAnnotator;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.versioning.util.SystemActionBridge;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.awt.Actions;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 * Responsible for coloring file labels and file icons in the IDE and providing IDE with menu items.
 *
 * @author Maros Sandor
 */
public class MercurialAnnotator extends VCSAnnotator implements PropertyChangeListener {
    
    private static final int INITIAL_ACTION_ARRAY_LENGTH = 25;
    
    private static final int STATUS_TEXT_ANNOTABLE = 
            FileInformation.STATUS_NOTVERSIONED_EXCLUDED |
            FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY | 
            FileInformation.STATUS_VERSIONED_UPTODATE |
            FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY | 
            FileInformation.STATUS_VERSIONED_CONFLICT |
            FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY | 
            FileInformation.STATUS_VERSIONED_DELETEDLOCALLY |
            FileInformation.STATUS_VERSIONED_ADDEDLOCALLY;

    private static final Pattern lessThan = Pattern.compile("<");  // NOI18N
    
    private static final int STATUS_BADGEABLE = 
            FileInformation.STATUS_VERSIONED_UPTODATE |
            FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY |
            FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY;

    private static final int STATUS_IS_IMPORTANT =
                        FileInformation.STATUS_VERSIONED_UPTODATE |
                        FileInformation.STATUS_LOCAL_CHANGE |
                        FileInformation.STATUS_NOTVERSIONED_EXCLUDED;

    public static final String ANNOTATION_STATUS      = "status";       //NOI18N
    public static final String ANNOTATION_FOLDER      = "folder";       //NOI18N

    public static String[] LABELS = new String[] {ANNOTATION_STATUS, ANNOTATION_FOLDER};
    public static final String ACTIONS_PATH_PREFIX = "Actions/MercurialRemote/"; //NOI18N

    private final FileStatusCache cache;

    private static final String badgeModified = "org/netbeans/modules/mercurial/remote/resources/icons/modified-badge.png"; //NOI18N
    private static final String badgeConflicts = "org/netbeans/modules/mercurial/remote/resources/icons/conflicts-badge.png"; //NOI18N
    private static final String toolTipModified = "<img src=\"" + MercurialAnnotator.class.getClassLoader().getResource(badgeModified) + "\">&nbsp;"
            + NbBundle.getMessage(MercurialAnnotator.class, "MSG_Contains_Modified_Locally");
    private static final String toolTipConflict = "<img src=\"" + MercurialAnnotator.class.getClassLoader().getResource(badgeConflicts) + "\">&nbsp;"
            + NbBundle.getMessage(MercurialAnnotator.class, "MSG_Contains_Conflicts");
    private static final Logger LOG = Logger.getLogger(MercurialAnnotator.class.getName());
    
    private final Map<FileSystem, AnnotationFormat> annotationFormat = new HashMap<FileSystem, AnnotationFormat>();

    public static final class AnnotationFormat {
        private MessageFormat format;
        private String emptyFormat;
    }

    MercurialAnnotator(FileStatusCache cache) {
        this.cache = cache;
        initDefaults();
    }
    
    private void initDefaults() {
        refresh();
    }

    public void refresh() {
        for(FileSystem fileSystem : VCSFileProxySupport.getFileSystems()) {
            initFormat(fileSystem);
        }
    }

    private AnnotationFormat initFormat(FileSystem fileSystem) {
        AnnotationFormat af = annotationFormat.get(fileSystem);
        if (af == null) {
            af = new AnnotationFormat();
            annotationFormat.put(fileSystem, af);
        }
        String string = HgModuleConfig.getDefault(VCSFileProxy.createFileProxy(fileSystem.getRoot())).getAnnotationFormat();
        if (string != null && !string.trim().equals("")) { // NOI18N
            string = HgUtils.createAnnotationFormat(string);
            if (!HgUtils.isAnnotationFormatValid(string))   {
                // see #136440
                Mercurial.LOG.log(Level.WARNING, "Bad annotation format, switching to defaults");
                string = org.openide.util.NbBundle.getMessage(MercurialAnnotator.class, "MercurialAnnotator.defaultFormat"); // NOI18N
            }
            af.format = new MessageFormat(string);
            af.emptyFormat = af.format.format(new String[] {"", "", ""} , new StringBuffer(), null).toString().trim(); // NOI18N
        }
        return af;
    }

    public AnnotationFormat getAnnotationFormat(FileSystem fileSystem) {
        AnnotationFormat af = annotationFormat.get(fileSystem);
        if (af == null) {
            af = initFormat(fileSystem);
            annotationFormat.put(fileSystem, af);
        }
        return af;
    }
    
    @Override
    public String annotateName(String name, VCSContext context) {
        FileInformation mostImportantInfo = null;
        VCSFileProxy mostImportantFile = null;
        boolean folderAnnotation = false;
                
        for (final VCSFileProxy file : context.getRootFiles()) {
            FileInformation info = cache.markAsSeenInUI(file);
            int status = info.getStatus();
            if ((status & STATUS_IS_IMPORTANT) == 0) {
                continue;
            }
            
            if (isMoreImportant(info, mostImportantInfo)) {
                mostImportantInfo = info;
                mostImportantFile = file;
                folderAnnotation = file.isDirectory();
            }
        }
        
        if (folderAnnotation == false && context.getRootFiles().size() > 1) {
            folderAnnotation = !VCSFileProxySupport.isFromMultiFileDataObject(context);
        }
        
        if (mostImportantInfo == null) {
            return null;
        }
        return folderAnnotation ?
            annotateFolderNameHtml(name, context, mostImportantInfo, mostImportantFile) :
            annotateNameHtml(name, mostImportantInfo, mostImportantFile);
    }
                
    @Override
    public Image annotateIcon(Image icon, VCSContext context) {
        boolean folderAnnotation = false;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "annotateIcon(): for {0}", new Object[] { context.getRootFiles() });
        }
        for (VCSFileProxy file : context.getRootFiles()) {
            if (file.isDirectory()) {
                folderAnnotation = true;
                break;
            }
        }

        if (folderAnnotation == false && context.getRootFiles().size() > 1) {
            folderAnnotation = !VCSFileProxySupport.isFromMultiFileDataObject(context);
        }
        
        if (folderAnnotation == false) {
            icon = annotateFileIcon(context, icon);
        } else {
            icon = annotateFolderIcon(context, icon);
        }
        return addToolTip(icon, context);
    }

    private Image annotateFileIcon(VCSContext context, Image icon) throws IllegalArgumentException {
        FileInformation mostImportantInfo = null;
        VCSFileProxy mostImportantFile = null;
        for (final VCSFileProxy file : context.getRootFiles()) {
            FileInformation info = cache.markAsSeenInUI(file);
            int status = info.getStatus();
            if ((status & STATUS_IS_IMPORTANT) == 0) {
                continue;
            }
            if (isMoreImportant(info, mostImportantInfo)) {
                mostImportantInfo = info;
                mostImportantFile = file;
            }
        }
        if(mostImportantInfo == null) {
            return icon;
        } 
        String statusText = null;
        int status = mostImportantInfo.getStatus();
        if (0 != (status & FileInformation.STATUS_NOTVERSIONED_EXCLUDED)) {
            statusText = getAnnotationProvider().EXCLUDED_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_DELETEDLOCALLY)) {
            statusText = getAnnotationProvider().DELETED_LOCALLY_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY)) {
            statusText = getAnnotationProvider().REMOVED_LOCALLY_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) {
            statusText = getAnnotationProvider().NEW_LOCALLY_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_ADDEDLOCALLY)) {
            FileStatus fileStatus = mostImportantInfo.getStatus(mostImportantFile);
            if (fileStatus != null && fileStatus.isCopied()) {
                statusText = (!EventQueue.isDispatchThread() && !fileStatus.getOriginalFile().exists()
                        ? getAnnotationProvider().MOVED_LOCALY_FILE_TOOLTIP
                        : getAnnotationProvider().COPIED_LOCALLY_FILE_TOOLTIP).getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
            } else {
                statusText = getAnnotationProvider().ADDED_LOCALLY_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
            }
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY)) {
            statusText = getAnnotationProvider().MODIFIED_LOCALLY_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_UPTODATE)) {
            statusText = null;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_CONFLICT)) {
            statusText = getAnnotationProvider().CONFLICT_FILE_TOOLTIP.getFormat().format(new Object[]{mostImportantInfo.getStatusText()});
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NOTMANAGED)) {
            statusText = null;
        } else if (status == FileInformation.STATUS_UNKNOWN) {
            statusText = null;
        } else {
            throw new IllegalArgumentException("Uncomparable status: " + status); // NOI18N
        }
        return statusText != null ? ImageUtilities.addToolTipToImage(icon, statusText) : icon;
    }

    private Image annotateFolderIcon(VCSContext context, Image icon) {
        boolean isVersioned = false;
        for (Iterator i = context.getRootFiles().iterator(); i.hasNext();) {
            VCSFileProxy file = (VCSFileProxy) i.next();
            FileInformation info = cache.markAsSeenInUI(file);
            if (info != null && (info.getStatus() & STATUS_BADGEABLE) != 0) {
                isVersioned = true;
                break;
            }
        }
        if (!isVersioned) {
            return icon;
        }
        Image badge = null;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "annotateFolderIcon(): for {0}", new Object[] { context.getRootFiles() });
        }
        if (cache.containsFileOfStatus(context, FileInformation.STATUS_VERSIONED_CONFLICT, true)) {
            badge = ImageUtilities.assignToolTipToImage(
                    ImageUtilities.loadImage(badgeConflicts, true), toolTipConflict);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "annotateFolderIcon(): contains conflict");
            }
        } else if (cache.containsFileOfStatus(context, FileInformation.STATUS_LOCAL_CHANGE, true)) {
            badge = ImageUtilities.assignToolTipToImage(
                    ImageUtilities.loadImage(badgeModified, true), toolTipModified);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "annotateFolderIcon(): contains local change");
            }
        }
        if (badge != null) {
            return ImageUtilities.mergeImages(icon, badge, 16, 9);
        } else {
            return icon;
        }
    }

    @Override
    public Action[] getActions(VCSContext ctx, VCSAnnotator.ActionDestination destination) {
        // TODO: get resource strings for all actions:
        ResourceBundle loc = NbBundle.getBundle(MercurialAnnotator.class);
        Node [] nodes = ctx.getElements().lookupAll(Node.class).toArray(new Node[0]);
        Set<VCSFileProxy> files = ctx.getRootFiles();
        Set<VCSFileProxy> roots = HgUtils.getRepositoryRoots(ctx);
        boolean noneVersioned = (roots == null || roots.isEmpty());

        List<Action> actions = new ArrayList<Action>(INITIAL_ACTION_ARRAY_LENGTH);
        if (destination == VCSAnnotator.ActionDestination.MainMenu) {
            // XXX use Actions.forID
            Action a;
            if (noneVersioned) {
                a = Utils.getAcceleratedAction("Actions/MercurialRemote/org-netbeans-modules-mercurial-remote-ui-create-CreateAction.instance"); //NOI18N
                if(a instanceof ContextAwareAction) {
                    a = ((ContextAwareAction)a).createContextAwareInstance(Lookups.fixed(files.toArray()));
                }
                if(a != null) {
                    actions.add(a);
                }
                a = (Action) Utils.getAcceleratedAction("Actions/MercurialRemote/org-netbeans-modules-mercurial-remote-ui-clone-CloneExternalAction.instance"); //NOI18N
                if(a != null) {
                    actions.add(a);
                }
            } else {
                actions.add(SystemAction.get(StatusAction.class));
                actions.add(new DiffMenu(null));
                actions.add(SystemAction.get(AddAction.class));
                actions.add(SystemAction.get(CommitAction.class));
                actions.add(SystemAction.get(UpdateAction.class));
                actions.add(SystemAction.get(RevertModificationsAction.class));
                actions.add(SystemAction.get(AnnotateAction.class));
                actions.add(SystemAction.get(LogAction.class));                
                actions.add(new ConflictsMenu(null));
                actions.add(null);
                actions.add(new IgnoreMenu(null, null));
                actions.add(new PatchesMenu(null));
                actions.add(null);
                actions.add(new BranchMenu(null));
                actions.add(new QueuesMenu(null));
                actions.add(new RemoteMenu(null));
                actions.add(new RecoverMenu(null));
                actions.add(null);
                actions.add(SystemAction.get(PropertiesAction.class));
            }
            Utils.setAcceleratorBindings(ACTIONS_PATH_PREFIX, actions.toArray(new Action[actions.size()]));
        } else {
            Lookup context = ctx.getElements();
            if (noneVersioned){
                Action a = Actions.forID("MercurialRemote", "org.netbeans.modules.mercurial.remote.ui.create.CreateAction"); //NOI18N
                if(a instanceof ContextAwareAction) {
                    a = ((ContextAwareAction)a).createContextAwareInstance(Lookups.fixed(files.toArray()));
                }            
                if(a != null) {
                    actions.add(a);
                }
            } else {
                actions.add(SystemActionBridge.createAction(SystemAction.get(StatusAction.class), loc.getString("CTL_PopupMenuItem_Status"), context)); //NOI18N
                actions.add(new DiffMenu(context));
                actions.add(SystemActionBridge.createAction(SystemAction.get(AddAction.class), NbBundle.getMessage(AddAction.class, "CTL_PopupMenuItem_Add"), context)); //NOI18N
                actions.add(SystemActionBridge.createAction(SystemAction.get(CommitAction.class), loc.getString("CTL_PopupMenuItem_Commit"), context)); //NOI18N
                actions.add(SystemActionBridge.createAction(SystemAction.get(UpdateAction.class), loc.getString("CTL_PopupMenuItem_Update"), context)); //NOI18N
                actions.add(SystemActionBridge.createAction(SystemAction.get(RevertModificationsAction.class), loc.getString("CTL_PopupMenuItem_Revert"), context)); //NOI18N
                actions.add(SystemActionBridge.createAction(SystemAction.get(AnnotateAction.class),
                        ((AnnotateAction)SystemAction.get(AnnotateAction.class)).visible(nodes)
                        ? loc.getString("CTL_PopupMenuItem_HideAnnotations") //NOI18N
                        : loc.getString("CTL_PopupMenuItem_ShowAnnotations"), context)); //NOI18N
                actions.add(SystemActionBridge.createAction(SystemAction.get(LogAction.class), loc.getString("CTL_PopupMenuItem_Log"), context)); //NOI18N
                actions.add(new ConflictsMenu(context));
                actions.add(null);
                actions.add(new IgnoreMenu(context, ctx));
                actions.add(new PatchesMenu(context));
                actions.add(null);
                actions.add(new BranchMenu(context, ctx));
                actions.add(new QueuesMenu(context));
                actions.add(new RemoteMenu(context));
                actions.add(new RecoverMenu(context));
                actions.add(null);
                actions.add(SystemActionBridge.createAction(SystemAction.get(PropertiesAction.class), loc.getString("CTL_PopupMenuItem_Properties"), context)); //NOI18N
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }
    
    /**
     * Applies custom format.
     */
    private String formatAnnotation(FileInformation info, VCSFileProxy file) {
        String statusString = "";  // NOI18N
        int status = info.getStatus();
        if (status != FileInformation.STATUS_VERSIONED_UPTODATE) {
            statusString = info.getShortStatusText();
        }

        //String stickyString = SvnUtils.getCopy(file);
        String stickyString = null;
        if (stickyString == null) {
            stickyString = ""; // NOI18N
        }

        Object[] arguments = new Object[] {
            statusString,
            stickyString,
        };
        AnnotationFormat af = getAnnotationFormat(VCSFileProxySupport.getFileSystem(file));
        String annotation = af.format.format(arguments, new StringBuffer(), null).toString().trim();
        if(annotation.equals(af.emptyFormat)) {
            return ""; // NOI18N
        } else {
            return " " + annotation; // NOI18N
        }
    }

    public String annotateNameHtml(VCSFileProxy file, FileInformation info) {
        return annotateNameHtml(file.getName(), info, file);
    }

    public String annotateNameHtml(String name, FileInformation mostImportantInfo, VCSFileProxy mostImportantFile) {
        // Hg: The codes used to show the status of files are:
        // M = modified
        // A = added
        // R = removed
        // C = clean
        // ! = deleted, but still tracked
        // ? = not tracked
        // I = ignored (not shown by default)
        
        name = htmlEncode(name);
        
        String textAnnotation;
        boolean annotationsVisible = VersioningSupport.getPreferences().getBoolean(VersioningSupport.PREF_BOOLEAN_TEXT_ANNOTATIONS_VISIBLE, false);
        int status = mostImportantInfo.getStatus();
        
        if (annotationsVisible && mostImportantFile != null && (status & STATUS_TEXT_ANNOTABLE) != 0) {
            AnnotationFormat af = getAnnotationFormat(VCSFileProxySupport.getFileSystem(mostImportantFile));
            if (af.format != null) {
                textAnnotation = formatAnnotation(mostImportantInfo, mostImportantFile);
            } else {
                //String sticky = SvnUtils.getCopy(mostImportantFile);
                String sticky = null;
                if (status == FileInformation.STATUS_VERSIONED_UPTODATE && sticky == null) {
                    textAnnotation = "";  // NOI18N
                } else if (status == FileInformation.STATUS_VERSIONED_UPTODATE) {
                    textAnnotation = " [" + sticky + "]"; // NOI18N
                } else if (sticky == null) {
                    String statusText = mostImportantInfo.getShortStatusText();
                    if(!statusText.equals("")) { // NOI18N
                        textAnnotation = " [" + mostImportantInfo.getShortStatusText() + "]"; // NOI18N
                    } else {
                        textAnnotation = ""; // NOI18N
                    }
                } else {
                    textAnnotation = " [" + mostImportantInfo.getShortStatusText() + "; " + sticky + "]"; // NOI18N
                }
            }
        } else {
            textAnnotation = ""; // NOI18N
        }

        if (textAnnotation.length() > 0) {
            textAnnotation = getAnnotationProvider().TEXT_ANNOTATION.getFormat().format(new Object[] { textAnnotation });
        }

        if (0 != (status & FileInformation.STATUS_NOTVERSIONED_EXCLUDED)) {
            return getAnnotationProvider().EXCLUDED_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_DELETEDLOCALLY)) {
            return getAnnotationProvider().DELETED_LOCALLY_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY)) {
            return getAnnotationProvider().REMOVED_LOCALLY_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) {
            return getAnnotationProvider().NEW_LOCALLY_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_ADDEDLOCALLY)) {
            FileStatus fileStatus = mostImportantInfo.getStatus(mostImportantFile);
            if (fileStatus != null && fileStatus.isCopied()) {
                return (!EventQueue.isDispatchThread() && !fileStatus.getOriginalFile().exists()
                        ? getAnnotationProvider().MOVED_LOCALY_FILE
                        : getAnnotationProvider().COPIED_LOCALLY_FILE).getFormat().format(new Object [] { name, textAnnotation });
            } else {
                return getAnnotationProvider().ADDED_LOCALLY_FILE.getFormat().format(new Object [] { name, textAnnotation });
            }
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY)) {
            return getAnnotationProvider().MODIFIED_LOCALLY_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_UPTODATE)) {
            return getAnnotationProvider().UP_TO_DATE_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_CONFLICT)) {
            return getAnnotationProvider().CONFLICT_FILE.getFormat().format(new Object [] { name, textAnnotation });
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NOTMANAGED)) {
            return name;
        } else if (status == FileInformation.STATUS_UNKNOWN) {
            return name;
        } else {
            throw new IllegalArgumentException("Uncomparable status: " + status); // NOI18N
        }
    }
    
    public Color getAnnotatedColor (FileInformation mostImportantInfo) {
        int status = mostImportantInfo.getStatus();
        if (0 != (status & FileInformation.STATUS_NOTVERSIONED_EXCLUDED)) {
            return getAnnotationProvider().EXCLUDED_FILE.getActualColor();
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_DELETEDLOCALLY)) {
            return getAnnotationProvider().DELETED_LOCALLY_FILE.getActualColor();
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY)) {
            return getAnnotationProvider().REMOVED_LOCALLY_FILE.getActualColor();
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) {
            return getAnnotationProvider().NEW_LOCALLY_FILE.getActualColor();
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_ADDEDLOCALLY)) {
            FileStatus fileStatus = mostImportantInfo.getStatus(null);
            if (fileStatus != null && fileStatus.isCopied()) {
                return (!EventQueue.isDispatchThread() && !fileStatus.getOriginalFile().exists()
                        ? getAnnotationProvider().MOVED_LOCALY_FILE
                        : getAnnotationProvider().COPIED_LOCALLY_FILE).getActualColor();
            } else {
                return getAnnotationProvider().ADDED_LOCALLY_FILE.getActualColor();
            }
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY)) {
            return getAnnotationProvider().MODIFIED_LOCALLY_FILE.getActualColor();
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_UPTODATE)) {
            return getAnnotationProvider().UP_TO_DATE_FILE.getActualColor();
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_CONFLICT)) {
            return getAnnotationProvider().CONFLICT_FILE.getActualColor();
        } else {
            return null;
        }
    }
    
    private String htmlEncode(String name) {
        if (name.indexOf('<') == -1) {
            return name;
        }
        return lessThan.matcher(name).replaceAll("&lt;"); // NOI18N
    }
    
    private String annotateFolderNameHtml(String name, VCSContext context, FileInformation mostImportantInfo, VCSFileProxy mostImportantFile) {
        String nameHtml = htmlEncode(name);
        if (mostImportantInfo.getStatus() == FileInformation.STATUS_NOTVERSIONED_EXCLUDED){
            return getAnnotationProvider().EXCLUDED_FILE.getFormat().format(new Object [] { nameHtml, ""}); // NOI18N
        }
        boolean annotationsVisible = VersioningSupport.getPreferences().getBoolean(VersioningSupport.PREF_BOOLEAN_TEXT_ANNOTATIONS_VISIBLE, false);
        MessageFormat uptodateFormat = getAnnotationProvider().UP_TO_DATE_FILE.getFormat();

        final Set<VCSFileProxy> rootFiles = context.getRootFiles();
        VCSFileProxy repo = null;        
        String folderAnotation = ""; //NOI18N
        VCSFileProxy root = null; 
        boolean isRepoRoot = false;
        if(rootFiles.size() == 1) {
            VCSFileProxy file = rootFiles.iterator().next();
            repo = Mercurial.getInstance().getRepositoryRoot(file);
            if(repo == null) {
                Mercurial.LOG.log(Level.WARNING, "Couldn''t find repository root for file {0}", file);
            } else {
                root = file;
                isRepoRoot = repo.equals(root);
            }
            // repo = null iff the file' status is actually unversioned, but cache has not yet have the right value
            if (repo == null || !isRepoRoot) {
                // not from repo root => do not annnotate with folder name 
                return uptodateFormat.format(new Object [] { nameHtml, ""});
            }             
        } else {
            // Label top level repository nodes with a repository name label when:
            // Display Name (name) is different from its repo name (repo.getName())        
            VCSFileProxy parentFile = null;
            for (VCSFileProxy file : rootFiles) {            
                if(parentFile == null) {
                    parentFile = file.getParentFile();
                } else {
                    VCSFileProxy p = file.getParentFile();
                    if(p == null || !parentFile.equals(p)) {
                        // not comming from the same parent => do not annnotate with folder name
                        return uptodateFormat.format(new Object [] { nameHtml, ""});
                    }
                }
            }
            for (VCSFileProxy file : rootFiles) {            
                repo = Mercurial.getInstance().getRepositoryRoot(file);
                if(repo == null) {
                    Mercurial.LOG.log(Level.WARNING, "Couldn''t find repository root for file {0}", file);
                } else {
                    root = file;
                    break;
                }
            }
            isRepoRoot = parentFile != null && parentFile.equals(repo);
        }

        if (annotationsVisible && repo != null) {
            if (isRepoRoot && !repo.getName().equals(name)) {
                folderAnotation = repo.getName();
            }
            WorkingCopyInfo info = WorkingCopyInfo.getInstance(repo);
            addFileWithRepositoryAnnotation(info, root);
            HgLogMessage[] parents = info.getWorkingCopyParents();
            StringBuilder label = new StringBuilder();
            if (parents.length == 1) {
                HgLogMessage parent = parents[0];
                for (String b : parent.getBranches()) {
                    label.append(b);
                }
                if (label.length() == 0) {
                    label.append(HgBranch.DEFAULT_NAME);
                }
                if (parent.getTags().length == 0) {
                    label.append(' ').append(parent.getCSetShortID().substring(0, Math.min(7, parent.getCSetShortID().length())));
                } else {
                    for (String b : parent.getTags()) {
                        label.append(' ').append(b);
                    }
                }
            } else if (parents.length > 1) {
                String b1 = parents[0].getBranches().length == 0 ? HgBranch.DEFAULT_NAME : parents[0].getBranches()[0];
                String b2 = parents[1].getBranches().length == 0 ? HgBranch.DEFAULT_NAME : parents[1].getBranches()[0];
                if (b1.equals(b2)) {
                    label.append(NbBundle.getMessage(MercurialAnnotator.class, "LBL_Annotator.label.merged.oneBranch", new Object[] { //NOI18N
                        parents[0].getCSetShortID().substring(0, Math.min(7, parents[0].getCSetShortID().length())), 
                        parents[1].getCSetShortID().substring(0, Math.min(7, parents[1].getCSetShortID().length())), 
                        b1
                    }));
                } else {
                    label.append(NbBundle.getMessage(MercurialAnnotator.class, "LBL_Annotator.label.merged.twoBranches", new Object[] { b1, b2 })); //NOI18N
                }
            }
            if (!folderAnotation.isEmpty() && label.length() > 0) {
                label.insert(0, ", "); //NOI18N
            }
            label.insert(0, folderAnotation);
            folderAnotation = label.toString();
        }

        return uptodateFormat.format(new Object [] { nameHtml, folderAnotation.isEmpty() ? "" //NOI18N
                : getAnnotationProvider().TEXT_ANNOTATION.getFormat().format(new Object[] { " [" + folderAnotation + "]" } ) //NOI18N
        });
    }
    
    private boolean isMoreImportant(FileInformation a, FileInformation b) {
        if (b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return getComparableStatus(a.getStatus()) < getComparableStatus(b.getStatus());
    }
    
    /**
     * Gets integer status that can be used in comparators. The more important the status is for the user,
     * the lower value it has. Conflict is 0, unknown status is 100.
     *
     * @return status constant suitable for 'by importance' comparators
     */
    public static int getComparableStatus(int status) {
        if (0 != (status & FileInformation.STATUS_VERSIONED_CONFLICT)) {
            return 0;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MERGE)) {
            return 1;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_DELETEDLOCALLY)) {
            return 10;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY)) {
            return 11;
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) {
            return 12;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_ADDEDLOCALLY)) {
            return 13;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY)) {
            return 14;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_REMOVEDINREPOSITORY)) {
            return 30;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_NEWINREPOSITORY)) {
            return 31;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDINREPOSITORY)) {
            return 32;
        } else if (0 != (status & FileInformation.STATUS_VERSIONED_UPTODATE)) {
            return 50;
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_EXCLUDED)) {
            return 100;
        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NOTMANAGED)) {
            return 101;
        } else if (status == FileInformation.STATUS_UNKNOWN) {
            return 102;
        } else {
            throw new IllegalArgumentException("Uncomparable status: " + status); // NOI18N
        }
    }

    private AnnotationColorProvider getAnnotationProvider() {
        return AnnotationColorProvider.getInstance();
    }
    
    private final Map<WorkingCopyInfo, Set<VCSFileProxy>> filesWithRepositoryAnnotations = new HashMap<WorkingCopyInfo, Set<VCSFileProxy>>(3);
    
    private void addFileWithRepositoryAnnotation (WorkingCopyInfo info, VCSFileProxy file) {
        info.removePropertyChangeListener(this);
        info.addPropertyChangeListener(this);
        synchronized (filesWithRepositoryAnnotations) {
            Set<VCSFileProxy> files = filesWithRepositoryAnnotations.get(info);
            if (files == null) {
                filesWithRepositoryAnnotations.put(info, files = new HashSet<VCSFileProxy>());
            }
            files.add(file);
        }
    }

    @Override
    public void propertyChange (final PropertyChangeEvent evt) {
        if (WorkingCopyInfo.PROPERTY_WORKING_COPY_PARENT.equals(evt.getPropertyName())) {
            Utils.post(new Runnable() {
                @Override
                public void run() {
                    WorkingCopyInfo info = (WorkingCopyInfo) evt.getSource();
                    Set<VCSFileProxy> filesToRefresh;
                    synchronized (filesWithRepositoryAnnotations) {
                        filesToRefresh = filesWithRepositoryAnnotations.remove(info);
                    }
                    if (filesToRefresh != null && !filesToRefresh.isEmpty()) {
                        Mercurial.getInstance().refreshAnnotations(filesToRefresh);
                    }
                }
            }, 400);
        }
    }

    private Image addToolTip (Image icon, VCSContext context) {
        if (!VersioningSupport.getPreferences().getBoolean(VersioningSupport.PREF_BOOLEAN_TEXT_ANNOTATIONS_VISIBLE, false)) {
            return icon;
        }
        VCSFileProxy root = null;
        VCSFileProxy repository = null;
        for (VCSFileProxy f : context.getRootFiles()) {
            VCSFileProxy repo = Mercurial.getInstance().getRepositoryRoot(f);
            if (repo != null) {
                if (repository == null) {
                    repository = repo;
                    root = f;
                } else if (!repository.equals(repo)) {
                    // root files are from different repositories, do not annotate icon
                    return icon;
                }
            }
        }
        if (repository != null) {
            WorkingCopyInfo info = WorkingCopyInfo.getInstance(repository);
            addFileWithRepositoryAnnotation(info, root);
            HgLogMessage[] parents = info.getWorkingCopyParents();
            String label = null;
            if (parents.length == 1) {
                HgLogMessage parent = parents[0];
                String branchName = null;
                for (String b : parent.getBranches()) {
                    branchName = b;
                }
                if (branchName != null) {
                    label = NbBundle.getMessage(MercurialAnnotator.class, "LBL_Annotator.currentBranch.toolTip", branchName); //NOI18N
                }
            } else if (parents.length > 1) {
                String b1 = parents[0].getBranches().length == 0 ? HgBranch.DEFAULT_NAME : parents[0].getBranches()[0];
                String b2 = parents[1].getBranches().length == 0 ? HgBranch.DEFAULT_NAME : parents[1].getBranches()[0];
                if (b1.equals(b2)) {
                    label = NbBundle.getMessage(MercurialAnnotator.class, "LBL_Annotator.mergeNeeded.oneBranch.toolTip", new Object[] { //NOI18N
                        parents[0].getCSetShortID().substring(0, Math.min(7, parents[0].getCSetShortID().length())), 
                        parents[1].getCSetShortID().substring(0, Math.min(7, parents[1].getCSetShortID().length())), 
                        b1
                    });
                } else {
                    label = NbBundle.getMessage(MercurialAnnotator.class, "LBL_Annotator.mergeNeeded.twoBranches.toolTip", new Object[] { b1, b2 }); //NOI18N
                }
            }
            if (label != null) {
                icon = ImageUtilities.addToolTipToImage(icon, label.toString());
            }
        }
        return icon;
    }
}
