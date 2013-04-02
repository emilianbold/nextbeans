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
package org.netbeans.modules.subversion.ui.diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.netbeans.api.diff.Difference;
import org.netbeans.modules.subversion.FileInformation;
import org.netbeans.modules.subversion.FileStatusCache;
import org.netbeans.modules.subversion.RepositoryFile;
import org.netbeans.modules.subversion.Subversion;
import org.netbeans.modules.subversion.SvnFileNode;
import org.netbeans.modules.subversion.client.SvnClient;
import org.netbeans.modules.subversion.client.SvnClientExceptionHandler;
import org.netbeans.modules.subversion.client.SvnProgressSupport;
import org.netbeans.modules.subversion.util.Context;
import org.netbeans.modules.subversion.util.SvnUtils;
import org.netbeans.modules.versioning.spi.VersioningSupport;
import org.netbeans.spi.diff.DiffProvider;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNDiffSummary;
import org.tigris.subversion.svnclientadapter.SVNDiffSummary.SVNDiffKind;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.utils.Depth;
import org.tigris.subversion.svnclientadapter.utils.SVNUrlUtils;

/**
 *
 * @author Ondrej Vrabec
 */
class RevisionSetupsSupport {
    private final RepositoryFile repositoryTreeLeft;
    private final RepositoryFile repositoryTreeRight;
    private final SVNUrl repositoryUrl;
    private final Context context;
    private final FileStatusCache cache;
    private final Map<File, Setup> wcSetups;
    private final boolean workingCopy;
    private final boolean base;
    private final Map<String, SVNDiffSummary[]> diffSummaryCache;
    private final Set<String> missingURLs;

    public RevisionSetupsSupport (RepositoryFile repositoryTreeLeft, RepositoryFile repositoryTreeRight,
            SVNUrl repositoryUrl, Context context) {
        this.repositoryTreeLeft = repositoryTreeLeft;
        this.repositoryTreeRight = repositoryTreeRight;
        this.repositoryUrl = repositoryUrl;
        this.context = context;
        this.cache = Subversion.getInstance().getStatusCache();
        this.wcSetups = new LinkedHashMap<File, Setup>();
        this.diffSummaryCache = new LinkedHashMap<String, SVNDiffSummary[]>();
        this.missingURLs = new LinkedHashSet<String>();
        workingCopy = SVNRevision.WORKING.equals(this.repositoryTreeRight.getRevision());
        base = SVNRevision.BASE.equals(this.repositoryTreeRight.getRevision())
                || SVNRevision.BASE.equals(this.repositoryTreeLeft.getRevision());
    }
        
    Setup[] computeSetupsBetweenRevisions (SvnProgressSupport supp) {
        RepositoryFile left = repositoryTreeLeft;
        RepositoryFile right = repositoryTreeRight;
        assert left != null && right != null;
        if (left.toString().equals(right.toString())) {
            return new Setup[0];
        }
        try {
            SvnClient client = Subversion.getInstance().getClient(repositoryUrl);
            List<Setup> setups = new ArrayList<Setup>();
            for (File root : SvnUtils.getActionRoots(context, false)) {
                boolean flatFile = VersioningSupport.isFlat(root);
                final SVNUrl leftUrl = left.replaceLastSegment(root.getName(), 0).getFileUrl();
                final SVNUrl rightUrl = right.replaceLastSegment(root.getName(), 0).getFileUrl();
                if (base || workingCopy) {
                    ISVNStatus[] statuses = client.getStatus(root, !flatFile, true, false, true);
                    Map<File, ISVNStatus> statusMap = new HashMap<File, ISVNStatus>(statuses.length);
                    for (ISVNStatus s : statuses) {
                        statusMap.put(s.getFile(), s);
                    }                        
                    for (ISVNStatus s : statuses) {
                        if (supp.isCanceled()) {
                            return null;
                        }
                        final File f = s.getFile();
                        if ((cache.getStatus(f).getStatus() & FileInformation.STATUS_NOTVERSIONED_EXCLUDED) != 0) {
                            continue;
                        }
                        if (flatFile && !f.equals(root) && s.getNodeKind() == SVNNodeKind.DIR) {
                            // not interested in nested folders when a package is selected
                            continue;
                        }
                        SVNRevision revision = s.getRevision();
                        SVNUrl url = s.getUrl();
                        if (s.isCopied() || revision == null) {
                            // not yet in the repository
                            url = null;
                            revision = null;
                            if (!workingCopy) {
                                // and not diffing WC
                                continue;
                            }
                        }
                        final SVNUrl leftFileUrl, rightFileUrl;
                        final SVNRevision leftRevision, rightRevision;
                        String relativePath = getRelativePath(root, f);
                        if (SVNRevision.BASE.equals(right.getRevision()) || workingCopy) {
                            leftFileUrl = leftUrl.appendPath(relativePath);
                            leftRevision = left.getRevision();
                            rightFileUrl = url;
                            rightRevision = revision;
                        } else {
                            leftFileUrl = url;
                            leftRevision = revision;
                            rightFileUrl = rightUrl.appendPath(relativePath);
                            rightRevision = right.getRevision();
                        }
                        List<Setup> partialSetups = buildSetups(client, leftFileUrl, leftRevision, 
                            rightFileUrl, rightRevision, 
                            supp, flatFile, flatFile ? Depth.files : Depth.infinity,
                            f, false, statusMap);
                        if (partialSetups == null) {
                            // canceled
                            return null;
                        } else {
                            setups.addAll(partialSetups);
                        }
                    }
                } else {
                    List<Setup> partialSetups = buildSetups(client, leftUrl, left.getRevision(), 
                            rightUrl, right.getRevision(), 
                            supp, flatFile, flatFile ? Depth.files : Depth.infinity,
                            root, true, Collections.<File, ISVNStatus>emptyMap());
                    if (partialSetups == null) {
                        // canceled
                        return null;
                    } else {
                        setups.addAll(partialSetups);
                    }
                }
            }
            if (workingCopy) {
                // and finally add just local modifications
                for (Map.Entry<File, Setup> e : wcSetups.entrySet()) {
                    setups.add(e.getValue());
                }
            }
            return setups.toArray(new Setup[setups.size()]);
        } catch (SVNClientException ex) {
            SvnClientExceptionHandler.notifyException(ex, true, false);
            return new Setup[0];
        }
    }

    void setWCSetups (Setup[] wcSetups) {
        for (Setup s : wcSetups) {
            File f = s.getBaseFile();
            if (s.getPropertyName() == null) {
                // for now add only file's content setup, no property
                this.wcSetups.put(f, s);
            }
        }
    }

    private boolean checkUrlExistance (SvnClient client, SVNUrl url, SVNRevision revision) throws SVNClientException {
        if (url == null) {
            // local file, not yet in the repository
            return false;
        }
        if (parentMissing(url, revision)) {
            return false;
        }
        try {
            client.getInfo(url, revision, revision);
        } catch (SVNClientException ex) {
            if (SvnClientExceptionHandler.isWrongURLInRevision(ex.getMessage())){
                cacheParentMissing(url, revision);
                return false;
            } else {
                throw ex;
            }
        }
        return true;
    }

    private List<Setup> addPropertySetups (SvnClient client, SVNUrl leftFileUrl, SVNRevision leftRevision,
            SVNUrl rightFileUrl, SVNRevision rightRevision) throws SVNClientException {
        List<Setup> propSetups = new ArrayList<Setup>();
        DiffProvider diffAlgorithm = (DiffProvider) Lookup.getDefault().lookup(DiffProvider.class);
        try {
            Map<String, byte[]> leftProps = leftFileUrl == null
                    ? Collections.<String, byte[]>emptyMap()
                    : toMap(client.getProperties(leftFileUrl, leftRevision, leftRevision));
            Map<String, byte[]> rightProps = rightFileUrl == null
                    ? Collections.<String, byte[]>emptyMap()
                    : toMap(client.getProperties(rightFileUrl, rightRevision, rightRevision));

            Set<String> allProps = new TreeSet<String>(leftProps.keySet());
            allProps.addAll(rightProps.keySet());
            for (String key : allProps) {
                boolean isLeft = leftProps.containsKey(key);
                boolean isRight = rightProps.containsKey(key);
                boolean propertiesDiffer = true;
                if (isLeft && isRight) {
                    MultiDiffPanel.Property p1 = new MultiDiffPanel.Property(leftProps.get(key));
                    MultiDiffPanel.Property p2 = new MultiDiffPanel.Property(rightProps.get(key));
                    Difference[] diffs = diffAlgorithm.computeDiff(p1.toReader(), p2.toReader());
                    propertiesDiffer = (diffs.length != 0);
                }
                if (propertiesDiffer) {
                    // TODO finish property setups init
                }
            }
        } catch (IOException e) {
            Subversion.LOG.log(Level.INFO, null, e);
        }
        return propSetups;
    }

    private Map<String, byte[]> toMap (ISVNProperty[] properties) {
        Map<String, byte[]> map = new LinkedHashMap<String, byte[]>(properties.length);
        for (ISVNProperty prop : properties) {
            map.put(prop.getName(), prop.getData());
        }
        return map;
    }

    private boolean isSkippedInParent (List<String> skippedPaths, String filePath) {
        for (String p : skippedPaths) {
            if (filePath.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    private String getRelativePath (File root, File f) {
        String path = "";
        while (f != null && !f.equals(root)) {
            path = f.getName() + "/" + path;
            f = f.getParentFile();
        }
        if (f == null) {
            return null;
        } else {
            return path.isEmpty() ? path : path.substring(0, path.length() - 1);
        }
    }

    private Setup createSetup (SVNDiffSummary summary, File file, SVNUrl leftUrl, SVNRevision leftRevision,
            SVNUrl rightUrl, String rightRevision) {
        FileInformation fi = null;
        Setup localSetup = wcSetups.get(file);
        boolean deleted = summary.getDiffKind() == SVNDiffKind.DELETED;
        boolean added = summary.getDiffKind() == SVNDiffKind.ADDED;
        if (localSetup != null) {
            // local file, diffing WC
            fi = cache.getStatus(file);
            if (added && (fi.getStatus() & FileInformation.STATUS_IN_REPOSITORY) == 0) {
                // don't override added status with modified
                fi = null;
            } else {
                deleted = (fi.getStatus() & (FileInformation.STATUS_VERSIONED_DELETEDLOCALLY
                        | FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY)) != 0;
                added = (fi.getStatus() & (FileInformation.STATUS_VERSIONED_ADDEDLOCALLY
                        | FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) != 0;
            }
        }
        if (fi == null) {
            fi = new RevisionsFileInformation(summary);
        }
        wcSetups.remove(file);
        Setup setup = new Setup(file, repositoryUrl,
                leftUrl, added ? null : leftRevision.toString(),
                SVNUrlUtils.getRelativePath(repositoryUrl, leftUrl) + "@" + leftRevision,
                rightUrl, deleted ? null : rightRevision,
                Setup.REVISION_CURRENT.equals(rightRevision)
                ? file.getName() + "@" + rightRevision
                : SVNUrlUtils.getRelativePath(repositoryUrl, rightUrl) + "@" + rightRevision,
                fi);
        setup.setNode(new DiffNode(setup, new SvnFileNode(file), FileInformation.STATUS_ALL));
        return setup;
    }

    private List<Setup> buildSetups (SvnClient client, SVNUrl leftFileUrl, SVNRevision leftRevision,
            SVNUrl rightFileUrl, SVNRevision rightRevision, SvnProgressSupport supp, 
            boolean flatFile, int depth, File root, boolean addAll, 
            Map<File, ISVNStatus> statusMap) throws SVNClientException {
        boolean sameURLs = leftFileUrl != null && leftFileUrl.equals(rightFileUrl)
                && leftRevision != null && leftRevision.equals(rightRevision);
        List<Setup> setups = new ArrayList<Setup>();
        if (!sameURLs) {
            SVNDiffSummary[] diffSummaries = getCachedSummaries(leftFileUrl, leftRevision, rightRevision);
            boolean leftExists = diffSummaries != null || checkUrlExistance(client, leftFileUrl, leftRevision);
            boolean rightExists = diffSummaries != null || checkUrlExistance(client, rightFileUrl, rightRevision);
            if (supp.isCanceled()) {
                return null;
            }
            if (leftExists && rightExists) {
                if (diffSummaries == null) {
                    diffSummaries = client.diffSummarize(
                            leftFileUrl,
                            leftRevision,
                            rightFileUrl,
                            rightRevision, depth, true);
                    cacheSummaries(diffSummaries, leftFileUrl, leftRevision, rightRevision);
                }
                List<String> skippedPaths = new ArrayList<String>();
                for (SVNDiffSummary summary : diffSummaries) {
                    if (supp.isCanceled()) {
                        return null;
                    }
                    String filePath = summary.getPath();
                    File file = filePath.isEmpty() ? root : new File(root, filePath);
                    boolean skipItem = !filePath.isEmpty();
                    if (addAll || summary.getDiffKind() == SVNDiffSummary.SVNDiffKind.DELETED 
                            // diff for package but a subpackage was deleted or a file somewhere lower in the subtree
                            && !(flatFile && (summary.getNodeKind() == SVNNodeKind.DIR.toInt() || filePath.contains("/")))
                            // if skipped path contains its ancestor, it means the ancestor exists locally
                            // and the file deletion will be handled there (and correct BASE revision will be set)
                            && !isSkippedInParent(skippedPaths, filePath))
                    {
                        // interested in deleted files/folders
                        skipItem = false;
                        // file or folder is deleted in this revision
                        // and it is not locally present (e.g. when updated to a different revision locally)
                        // example: remove folder/subfolder in revision 5; update folder/subfolder to revision 4
                        // and recreate it. Then diff summary for folder@5 will yeild subfolder as deleted
                        // but since it is present locally in its BASE~4, we cannot display it as deleted
                        ISVNStatus fileStatus = statusMap.get(file);
                        if (fileStatus != null && !fileStatus.isCopied() && fileStatus.getRevision() != null) {
                            // does exist
                            skipItem = true;
                            skippedPaths.add(filePath);
                        }
                    }
                    if (summary.getDiffKind() != SVNDiffSummary.SVNDiffKind.NORMAL && !skipItem) {
                        Setup setup = createSetup(summary, file,
                                leftFileUrl.appendPath(filePath), leftRevision,
                                workingCopy ? null : rightFileUrl.appendPath(filePath),
                                workingCopy ? Setup.REVISION_CURRENT : rightRevision.toString());
                        setups.add(setup);
                        // TODO when property diffs demanded, uncomment this and finish the method's implementation
        //                                        newSetups.addAll(addPropertySetups(client, leftFileUrl, left.getRevision(),
        //                                                rightFileUrl, right.getRevision()));
                    }
                }
            } else {
                SVNDiffSummary summary = new SVNDiffSummary("", leftExists
                        ? SVNDiffSummary.SVNDiffKind.DELETED
                        : SVNDiffSummary.SVNDiffKind.ADDED, false, 0);
                Setup setup = createSetup(summary, root, leftFileUrl, leftRevision,
                        workingCopy ? null : rightFileUrl,
                        workingCopy ? Setup.REVISION_CURRENT : rightRevision.toString());
                setups.add(setup);
                // TODO when property diffs demanded, uncomment this and finish the method's implementation
    //                                newSetups.addAll(addPropertySetups(client, leftExists ? leftUrl : null, left.getRevision(),
    //                                        rightExists ? rightUrl : null, right.getRevision()));
            }
        }
        if (workingCopy) {
            // and local modifications for this root
            if (wcSetups.containsKey(root)) {
                SVNDiffSummary summary = new SVNDiffSummary("", SVNDiffSummary.SVNDiffKind.NORMAL, false, 0);
                Setup setup = createSetup(summary, root, leftFileUrl, leftRevision, null, Setup.REVISION_CURRENT);
                setups.add(setup);
            }
        }
        return setups;
    }

    private void cacheSummaries (SVNDiffSummary[] diffSummaries, SVNUrl leftUrl,
            SVNRevision leftRevision, SVNRevision rightRevision) {
        String revisionString = "@" + leftRevision + ":" + rightRevision;
        Map<String, List<SVNDiffSummary>> sums = new LinkedHashMap<String, List<SVNDiffSummary>>();
        sums.put("", new ArrayList<SVNDiffSummary>(diffSummaries.length));
        for (SVNDiffSummary s : diffSummaries) {
            String path = s.getPath();
            do {
                List<SVNDiffSummary> list = sums.get(path);
                if (list == null) {
                    list = new ArrayList<SVNDiffSummary>();
                    sums.put(path, list);
                }
                String suffix = s.getPath().substring(path.length());
                if (suffix.startsWith("/")) {
                    suffix = suffix.substring(1);
                }
                list.add(new SVNDiffSummary(suffix, s.getDiffKind(), s.propsChanged(), s.getNodeKind()));
                int index = path.lastIndexOf("/");
                if (index > -1) {
                    path = path.substring(0, index);
                } else if (!path.isEmpty()) {
                    path = "";
                } else {
                    path = null;
                }
            } while (path != null);
        }
        for (Map.Entry<String, List<SVNDiffSummary>> e : sums.entrySet()) {
            SVNDiffSummary[] summaryArray = e.getValue().toArray(new SVNDiffSummary[e.getValue().size()]);
            String key;
            if (e.getKey().isEmpty()) {
                key = leftUrl.toString();
            } else {
                key = leftUrl.toString() + "/" + e.getKey();
            }
            key += revisionString;
            diffSummaryCache.put(key, summaryArray);
        }
    }

    private SVNDiffSummary[] getCachedSummaries (SVNUrl url, SVNRevision leftRevision, SVNRevision rightRevision) {
        String revisionString = "@" + leftRevision + ":" + rightRevision;
        boolean direct = true;
        while (url != null) {
            SVNDiffSummary[] sums = diffSummaryCache.get(url.toString() + revisionString);
            if (sums != null) {
                return direct ? sums : new SVNDiffSummary[0];
            }
            direct = false;
            url = url.getParent();
        }
        return null;
    }

    private void cacheParentMissing (SVNUrl url, SVNRevision revision) {
        missingURLs.add(url.toString() + "@" + revision);
    }

    private boolean parentMissing (SVNUrl url, SVNRevision revision) {
        while (url != null) {
            if (missingURLs.contains(url.toString() + "@" + revision)) {
                return true;
            }
            url = url.getParent();
        }
        return false;
    }

    private static class RevisionsFileInformation extends FileInformation {

        private final String name;
        
        public RevisionsFileInformation (SVNDiffSummary item) {
            super(toStatus(item.getDiffKind()), 0, item.getNodeKind() == SVNNodeKind.DIR.toInt());
            this.name = toStatusText(item.getDiffKind());
        }

        @Override
        public String getStatusText (int displayStatuses) {
            return name;
        }
        
        @NbBundle.Messages({
            "LBL_DiffRevisions.status.added=Added",
            "LBL_DiffRevisions.status.removed=Removed",
            "LBL_DiffRevisions.status.modified=Modified",
            "LBL_DiffRevisions.status.uptodate=Normal"
        })
        private static String toStatusText (SVNDiffSummary.SVNDiffKind diffKind) {
            if (diffKind == SVNDiffSummary.SVNDiffKind.DELETED) {
                return Bundle.LBL_DiffRevisions_status_removed();
            } else if (diffKind == SVNDiffSummary.SVNDiffKind.ADDED) {
                return Bundle.LBL_DiffRevisions_status_added();
            } else if (diffKind == SVNDiffSummary.SVNDiffKind.MODIFIED) {
                return Bundle.LBL_DiffRevisions_status_modified();
            } else {
                return Bundle.LBL_DiffRevisions_status_uptodate();
            }
        }

        private static int toStatus (SVNDiffSummary.SVNDiffKind diffKind) {
            if (diffKind == SVNDiffSummary.SVNDiffKind.ADDED) {
                return FileInformation.STATUS_VERSIONED_ADDEDLOCALLY;
            } else if (diffKind == SVNDiffSummary.SVNDiffKind.DELETED) {
                return FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY;
            } else if (diffKind == SVNDiffSummary.SVNDiffKind.MODIFIED) {
                return FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY;
            } else {
                return FileInformation.STATUS_VERSIONED_UPTODATE;
            }
        }
    }
}
