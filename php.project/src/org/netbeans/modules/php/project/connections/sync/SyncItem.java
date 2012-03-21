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
package org.netbeans.modules.php.project.connections.sync;

import java.util.LinkedList;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.php.project.connections.TmpLocalFile;
import org.netbeans.modules.php.project.connections.transfer.TransferFile;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Sync item holding remote and local files and providing
 * operations with them.
 */
public final class SyncItem {

    @StaticResource
    static final String NOOP_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/noop.png"; // NOI18N
    @StaticResource
    static final String DOWNLOAD_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/download.png"; // NOI18N
    @StaticResource
    static final String DOWNLOAD_REVIEW_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/download.png"; // NOI18N
    @StaticResource
    static final String UPLOAD_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/upload.png"; // NOI18N
    @StaticResource
    static final String UPLOAD_REVIEW_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/upload.png"; // NOI18N
    @StaticResource
    static final String DELETE_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/delete.png"; // NOI18N
    @StaticResource
    static final String SYMLINK_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/symlink.png"; // NOI18N
    @StaticResource
    static final String FILE_DIR_COLLISION_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/file-dir-collision.png"; // NOI18N
    @StaticResource
    static final String FILE_CONFLICT_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/file-conflict.png"; // NOI18N


    @NbBundle.Messages({
        "Operation.noop.titleWithMnemonic=No o&peration",
        "Operation.download.titleWithMnemonic=&Download",
        "Operation.downloadReview.titleWithMnemonic=Download &with review",
        "Operation.upload.titleWithMnemonic=&Upload",
        "Operation.uploadReview.titleWithMnemonic=Upload w&ith review",
        "Operation.delete.titleWithMnemonic=D&elete",
        "Operation.symlink.titleWithMnemonic=S&ymbolic link",
        "Operation.fileDirCollision.titleWithMnemonic=File &vs. directory collision",
        "Operation.fileConflict.titleWithMnemonic=File c&onflict"
    })
    public static enum Operation {

        NOOP(Bundle.Operation_noop_titleWithMnemonic(), NOOP_ICON_PATH, false),
        DOWNLOAD(Bundle.Operation_download_titleWithMnemonic(), DOWNLOAD_ICON_PATH, true),
        DOWNLOAD_REVIEW(Bundle.Operation_downloadReview_titleWithMnemonic(), DOWNLOAD_REVIEW_ICON_PATH, true),
        UPLOAD(Bundle.Operation_upload_titleWithMnemonic(), UPLOAD_ICON_PATH, true),
        UPLOAD_REVIEW(Bundle.Operation_uploadReview_titleWithMnemonic(), UPLOAD_REVIEW_ICON_PATH, true),
        DELETE(Bundle.Operation_delete_titleWithMnemonic(), DELETE_ICON_PATH, false),
        SYMLINK(Bundle.Operation_symlink_titleWithMnemonic(), SYMLINK_ICON_PATH, false),
        FILE_DIR_COLLISION(Bundle.Operation_fileDirCollision_titleWithMnemonic(), FILE_DIR_COLLISION_ICON_PATH, false),
        FILE_CONFLICT(Bundle.Operation_fileConflict_titleWithMnemonic(), FILE_CONFLICT_ICON_PATH, false);


        private final String titleWithMnemonic;
        private final String iconPath;
        private final boolean progress;


        private Operation(String titleWithMnemonic, String iconPath, boolean progress) {
            this.titleWithMnemonic = titleWithMnemonic;
            this.iconPath = iconPath;
            this.progress = progress;
        }

        public String getTitle() {
            return titleWithMnemonic.replace("&", ""); // NOI18N
        }

        public String getTitleWithMnemonic() {
            return titleWithMnemonic;
        }

        public Icon getIcon() {
            return ImageUtilities.loadImageIcon(iconPath, false);
        }

        public boolean hasProgress() {
            return progress;
        }

    }


    private final SyncItems syncItems;
    private final TransferFile remoteTransferFile;
    private final TransferFile localTransferFile;
    private final Operation defaultOperation;

    private volatile Operation operation;
    private volatile boolean valid = false;
    private volatile String message = null;
    private volatile boolean validated = false;
    // for merging
    private volatile TmpLocalFile tmpLocalFile = null;


    SyncItem(SyncItems syncItems, TransferFile remoteTransferFile, TransferFile localTransferFile, long lastTimestamp) {
        assert syncItems != null;
        assert remoteTransferFile != null || localTransferFile != null;
        this.syncItems = syncItems;
        this.remoteTransferFile = remoteTransferFile;
        this.localTransferFile = localTransferFile;
        defaultOperation = calculateDefaultOperation(lastTimestamp);
    }

    public String getName() {
        if (remoteTransferFile != null) {
            return remoteTransferFile.getName();
        }
        return localTransferFile.getName();
    }

    public String getPath() {
        if (remoteTransferFile != null) {
            return remoteTransferFile.getRemotePath();
        }
        return localTransferFile.getRemotePath();
    }

    public long getSize() {
        if (remoteTransferFile != null) {
            return remoteTransferFile.getSize();
        }
        return localTransferFile.getSize();
    }

    public String getRemotePath() {
        if (remoteTransferFile == null) {
            return null;
        }
        return remoteTransferFile.getRemotePath();
    }

    public String getLocalPath() {
        if (localTransferFile == null) {
            return null;
        }
        return localTransferFile.getLocalPath();
    }


    public TransferFile getRemoteTransferFile() {
        return remoteTransferFile;
    }

    public TransferFile getLocalTransferFile() {
        return localTransferFile;
    }

    public Operation getOperation() {
        if (operation != null) {
            return operation;
        }
        return defaultOperation;
    }

    public void setOperation(Operation operation) {
        assert operation != null;
        validated = false;
        this.operation = operation;
    }

    public void resetOperation() {
        cleanupTmpLocalFile();
        tmpLocalFile = null;
        operation = null;
        validated = false;
    }

    public void cleanupTmpLocalFile() {
        if (tmpLocalFile != null) {
            tmpLocalFile.cleanup();
        }
    }

    @NbBundle.Messages({
        "SyncItem.error.fileConflict=File must be merged before synchronization.",
        "SyncItem.error.fileDirCollision=Cannot synchronize file with directory.",
        "SyncItem.error.childNotDeleted=Not all children marked for deleting.",
        "SyncItem.error.cannotDownload=Non-existing file cannot be downloaded.",
        "SyncItem.error.cannotUpload=Non-existing file cannot be uploaded.",
        "SyncItem.warn.downloadReview=File should be reviewed before first download.",
        "SyncItem.warn.uploadReview=File should be reviewed before first upload.",
        "SyncItem.warn.symlink=Symbolic links are not transfered (to avoid future overriding)."
    })
    public void validate() {
        if (validated) {
            return;
        }
        validated = true;
        message = null;
        valid = true;
        Operation op = getOperation();
        switch (op) {
            case NOOP:
                // noop
                break;
            case FILE_CONFLICT:
                valid = false;
                message = Bundle.SyncItem_error_fileConflict();
                return;
            case FILE_DIR_COLLISION:
                valid = false;
                message = Bundle.SyncItem_error_fileDirCollision();
                break;
            case SYMLINK:
                valid = true;
                message = Bundle.SyncItem_warn_symlink();
                break;
            case DELETE:
                if (localTransferFile != null
                        && !verifyChildrenOperation(localTransferFile, Operation.DELETE)) {
                    valid = false;
                    message = Bundle.SyncItem_error_childNotDeleted();
                    return;
                }
                if (remoteTransferFile != null
                        && !verifyChildrenOperation(remoteTransferFile, Operation.DELETE)) {
                    valid = false;
                    message = Bundle.SyncItem_error_childNotDeleted();
                    return;
                }
                break;
            case DOWNLOAD:
            case DOWNLOAD_REVIEW:
                if (remoteTransferFile == null) {
                    valid = false;
                    message = Bundle.SyncItem_error_cannotDownload();
                    return;
                }
                if (op == Operation.DOWNLOAD_REVIEW) {
                    message = Bundle.SyncItem_warn_downloadReview();
                }
                break;
            case UPLOAD:
            case UPLOAD_REVIEW:
                if (localTransferFile == null) {
                    valid = false;
                    message = Bundle.SyncItem_error_cannotUpload();
                    return;
                }
                if (op == Operation.UPLOAD_REVIEW) {
                    message = Bundle.SyncItem_warn_uploadReview();
                }
                break;
            default:
                throw new IllegalStateException("Unhandled operation: " + op);
        }
    }

    public boolean hasError() {
        validate();
        return !valid;
    }

    public boolean hasWarning() {
        validate();
        return valid && message != null;
    }

    public String getMessage() {
        validate();
        return message;
    }

    public boolean isDiffPossible() {
        if (remoteTransferFile != null
                && localTransferFile != null) {
            return remoteTransferFile.isFile() && localTransferFile.isFile();
        } else if (remoteTransferFile != null) {
            return remoteTransferFile.isFile();
        }
        return localTransferFile.isFile();
    }

    public boolean isOperationChangePossible() {
        if (getOperation() == Operation.SYMLINK) {
            return false;
        }
        return true;
    }

    public TmpLocalFile getTmpLocalFile() {
        return tmpLocalFile;
    }

    public void setTmpLocalFile(TmpLocalFile tmpLocalFile) {
        assert tmpLocalFile != null;
        this.tmpLocalFile = tmpLocalFile;
    }

    private Operation calculateDefaultOperation(long lastTimestamp) {
        if (remoteTransferFile != null && remoteTransferFile.isLink()) {
            return Operation.SYMLINK;
        }
        if (localTransferFile != null && remoteTransferFile != null) {
            if (localTransferFile.isFile() && !remoteTransferFile.isFile()) {
                return Operation.FILE_DIR_COLLISION;
            }
            if (localTransferFile.isDirectory() && !remoteTransferFile.isDirectory()) {
                return Operation.FILE_DIR_COLLISION;
            }
            if (localTransferFile.isDirectory() && remoteTransferFile.isDirectory()) {
                return Operation.NOOP;
            }
        }
        if (lastTimestamp == -1) {
            // running for the first time
            return calculateFirstDefaultOperation();
        }
        return calculateNewDefaultOperation(lastTimestamp);
    }

    private Operation calculateFirstDefaultOperation() {
        if (localTransferFile == null
                || remoteTransferFile == null) {
            if (localTransferFile == null) {
                return Operation.DOWNLOAD;
            }
            return Operation.UPLOAD;
        }
        long localTimestamp = localTransferFile.getTimestamp();
        RemoteTimestamp remoteTimestamp = new RemoteTimestamp(remoteTransferFile.getTimestamp());
        long localSize = localTransferFile.getSize();
        long remoteSize = remoteTransferFile.getSize();
        if (remoteTimestamp.equalsTo(localTimestamp)
                && localSize == remoteSize) {
            // simply equal files
            return Operation.NOOP;
        }
        if (remoteTimestamp.newerThan(localTimestamp)) {
            return Operation.DOWNLOAD_REVIEW;
        }
        return Operation.UPLOAD_REVIEW;
    }

    private Operation calculateNewDefaultOperation(long lastTimestamp) {
        if (localTransferFile == null
                || remoteTransferFile == null) {
            if (localTransferFile == null) {
                return new RemoteTimestamp(remoteTransferFile.getTimestamp()).newerThan(lastTimestamp) ? Operation.DOWNLOAD : Operation.DELETE;
            }
            return localTransferFile.getTimestamp() > lastTimestamp ? Operation.UPLOAD : Operation.DELETE;
        }
        long localTimestamp = localTransferFile.getTimestamp();
        RemoteTimestamp remoteTimestamp = new RemoteTimestamp(remoteTransferFile.getTimestamp());
        long localSize = localTransferFile.getSize();
        long remoteSize = remoteTransferFile.getSize();
        if (remoteTimestamp.equalsTo(localTimestamp)
                && localSize == remoteSize) {
            // simply equal files
            return Operation.NOOP;
        }
        if (localTimestamp <= lastTimestamp
                && remoteTimestamp.equalsOrOlderThan(lastTimestamp)
                && localSize == remoteSize) {
            // already synchronized
            return Operation.NOOP;
        }
        if (localTimestamp > lastTimestamp
                && remoteTimestamp.newerThan(lastTimestamp)) {
            // both files are newer
            return Operation.FILE_CONFLICT;
        }
        // only one file is newer
        if (remoteTimestamp.newerThan(localTimestamp)) {
            return Operation.DOWNLOAD;
        }
        return Operation.UPLOAD;
    }

    private boolean verifyChildrenOperation(TransferFile transferFile, Operation operation) {
        LinkedList<TransferFile> children = new LinkedList<TransferFile>();
        children.addAll(transferFile.getChildren());
        while (!children.isEmpty()) {
            TransferFile child = children.pop();
            SyncItem syncItem = syncItems.getByRemotePath(child.getRemotePath());
            if (syncItem.getOperation() != operation) {
                return false;
            }
            children.addAll(child.getChildren());
        }
        return true;
    }

    @Override
    public String toString() {
        return "SyncItem{" // NOI18N
                + "path: " + (localTransferFile != null ? localTransferFile.getRemotePath() : remoteTransferFile.getRemotePath()) // NOI18N
                + ", localFile: " + (localTransferFile != null) // NOI18N
                + ", remoteFile: " + (remoteTransferFile != null) // NOI18N
                + ", operation: " + getOperation() // NOI18N
                + ", valid: " + valid // NOI18N
                + ", tmpLocalFile: " + (tmpLocalFile != null) // NOI18N
                + "}"; // NOI18N
    }

    //~ Inner classes

    private static final class RemoteTimestamp {

        private static final long TIMEDIFF_TOLERANCE = 30L; // in seconds

        private final long remoteTimestamp;


        public RemoteTimestamp(long remoteTimestamp) {
            this.remoteTimestamp = remoteTimestamp;
        }

        public boolean equalsTo(long timestamp) {
            // similarly as ant
            return Math.abs(timestamp - remoteTimestamp) < TIMEDIFF_TOLERANCE;
        }

        public boolean equalsOrOlderThan(long timestamp) {
            if (equalsTo(timestamp)) {
                return true;
            }
            return remoteTimestamp <= timestamp;
        }

        public boolean newerThan(long timestamp) {
            if (equalsTo(timestamp)) {
                return false;
            }
            return remoteTimestamp > timestamp;
        }

        @Override
        public String toString() {
            return String.valueOf(remoteTimestamp);
        }

    }

}
