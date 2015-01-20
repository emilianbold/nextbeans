/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.remote.impl.fileoperations.spi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.netbeans.modules.dlight.libs.common.PathUtilities;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.util.FileInfoProvider;
import org.netbeans.modules.nativeexecution.api.util.ProcessUtils;
import org.netbeans.modules.nativeexecution.api.util.ProcessUtils.ExitStatus;
import org.netbeans.modules.remote.impl.RemoteLogger;
import org.netbeans.modules.remote.impl.fs.RemoteFileObject;
import org.netbeans.modules.remote.impl.fs.RemoteFileSystem;
import org.netbeans.modules.remote.impl.fs.RemoteFileSystemTransport;
import org.netbeans.modules.remote.impl.fs.RemoteFileSystemUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Static methods that are need for RemoteVcsSupportImpl
 * @author vkvashin
 */
public class RemoteVcsSupportUtil {

    private RemoteVcsSupportUtil() {        
    }
    
    private static final boolean USE_CACHE;
    static {
        String text = System.getProperty("rfs.vcs.cache");
        USE_CACHE = (text == null) ? true : Boolean.parseBoolean(text);
    }
    
    public static boolean isSymbolicLink(FileSystem fileSystem, String path) {
        if (fileSystem instanceof RemoteFileSystem) {
            RemoteFileSystem rfs = (RemoteFileSystem) fileSystem;
            if (USE_CACHE) {
                Boolean res = rfs.vcsSafeIsSymbolicLink(path);
                if (res != null) {
                    return res.booleanValue();
                }
            }            
            final ExecutionEnvironment env = rfs.getExecutionEnvironment();
            if (!ConnectionManager.getInstance().isConnectedTo(env)) {
                return false;
            }
            try {
                FileInfoProvider.StatInfo statInfo = RemoteFileSystemTransport.lstat(env, path);
                return statInfo.isLink();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
                if (RemoteFileSystemUtils.isFileNotFoundException(ex)) {
                    return false;
                }
                ex.printStackTrace(System.err);
            }
            return false;
            
        } else {
            return false;
        }
    }

    /** returns fully resolved canonical path or NULL if this is not a symbolic link */
    public static String getCanonicalPath(FileSystem fileSystem, String path) throws IOException {
        if (fileSystem instanceof RemoteFileSystem) {
            return getCanonicalPathImpl((RemoteFileSystem) fileSystem, path);
        } else {
            return null;
        }
    }
    
    /** returns fully resolved canonical path or NULL if this is not a symbolic link */
    private static String getCanonicalPathImpl(RemoteFileSystem fs, String path) throws IOException {
        Boolean isLink = fs.vcsSafeCanonicalPathDiffers(path);
        if (isLink != null && !isLink.booleanValue()) {
            return null;
        }
        ExecutionEnvironment env = fs.getExecutionEnvironment();
        if (!ConnectionManager.getInstance().isConnectedTo(env)) {
            throw new ConnectException(env.getDisplayName() + " not connected"); // NOI18N
        }
        try {
            FileInfoProvider.StatInfo statInfo = RemoteFileSystemTransport.lstat(env, path);
            if (statInfo.isLink()) {
                String target = statInfo.getLinkTarget();
                if (!target.startsWith("/")) { //NOI18N
                    target = PathUtilities.normalizeUnixPath(path + "/" + target); // NOI18N
                }
                String nextTarget = getCanonicalPathImpl(fs, target);
                return (nextTarget == null) ? target : nextTarget;
            } else {
                return null;
            }
        } catch (InterruptedException ex) {
            throw new InterruptedIOException();
        } catch (ExecutionException ex) {
            if (RemoteFileSystemUtils.isFileNotFoundException(ex)) {
                final FileNotFoundException fnfe = new FileNotFoundException();
                fnfe.initCause(ex);
                throw fnfe; // TODO: think over whether this is correct
            }
            throw new IOException(ex);
        }
    }
    
    public static boolean canReadImpl(RemoteFileSystem fileSystem, String path) {        
        try {
            ExecutionEnvironment env = fileSystem.getExecutionEnvironment();
            FileInfoProvider.StatInfo statInfo = RemoteFileSystemTransport.stat(env, path);
            return statInfo.canRead(env);
        } catch (InterruptedException ex) {
            return false; // TODO: is this correct?
        } catch (ExecutionException ex) {
            return false; // TODO: is this correct?
        }    
    }
    
    public static boolean canRead(FileSystem fileSystem, String path) {
        if (fileSystem instanceof RemoteFileSystem) {
            return canReadImpl((RemoteFileSystem) fileSystem, path);
        } else {
            return false;
        }        
    }
    
    public static long getSizeImpl(RemoteFileSystem fileSystem, String path) {
        try {
            ExecutionEnvironment env = fileSystem.getExecutionEnvironment();
            FileInfoProvider.StatInfo statInfo = RemoteFileSystemTransport.stat(env, path);
            return statInfo.getSize();
        } catch (InterruptedException ex) {
            return 0; // TODO: is this correct?
        } catch (ExecutionException ex) {
            return 0; // TODO: is this correct?
        }
    }

    public static long getSize(FileSystem fileSystem, String path) {
        if (fileSystem instanceof RemoteFileSystem) {
            return getSizeImpl((RemoteFileSystem) fileSystem, path);
        } else {
            return 0; // TODO: should it be -1?
        }
    }

    public static OutputStream getOutputStream(FileSystem fileSystem, String path) throws IOException {            
        FileObject fo = getFileObject(fileSystem, path);
        return fo.getOutputStream();
    }

    private static FileObject getFileObject(FileSystem fileSystem, String path) throws IOException {
        FileObject fo = fileSystem.findResource(path);
        if (fo == null)  {
            String parentPath = PathUtilities.getDirName(path);
            FileObject parentFO = fileSystem.findResource(parentPath);
            while (parentFO == null) {
                parentPath = PathUtilities.getDirName(parentPath);
                parentFO = fileSystem.findResource(parentPath);
            }
            if (parentFO == null) {
                throw new IOException(new NullPointerException());
            }
            parentFO.refresh();
        }
        fo = fileSystem.findResource(path);
        if (fo == null) {
            fo = FileUtil.createData(fileSystem.getRoot(), path);
        }
        return fo;
    }

    private static void deleteExternally(ExecutionEnvironment env, String path) {
        final ExitStatus res = ProcessUtils.execute(env, "rm", "-rf", path);
        if (!res.isOK()) {
            RemoteLogger.info("Error deleting {0}:{1} rc={2} {3}", env, path, res.exitCode, res.error); //NOI18N
        }
    }

    public static void delete(FileSystem fs, String path) {
        RemoteLogger.assertTrue(fs instanceof RemoteFileSystem, "" + fs + " not an instance of RemoteFileSystem"); //NOI18N
        if (fs instanceof RemoteFileSystem) {
            final RemoteFileSystem rfs = (RemoteFileSystem) fs;
            final ExecutionEnvironment env = rfs.getExecutionEnvironment();
            if (rfs.isInsideVCS()) {
                deleteExternally(env, path);
            } else {
                try {
                    FileObject fo = getFileObject(fs, path);
                    if (fo != null) {
                        fo.delete();
                    } else {
                        RemoteLogger.info("Can not delete inexistent file {0}:{1}", env, path);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public static void setLastModified(FileSystem fs, String path, String referenceFile) {
        RemoteLogger.assertTrue(fs instanceof RemoteFileSystem, "" + fs + " not an instance of RemoteFileSystem"); //NOI18N
        if (fs instanceof RemoteFileSystem) {
            final RemoteFileSystem rfs = (RemoteFileSystem) fs;
            final ExecutionEnvironment env = rfs.getExecutionEnvironment();
            final ExitStatus res = ProcessUtils.execute(env, "touch", "-r", path, referenceFile);
            if (res.isOK()) {
                try {
                    String base1 = PathUtilities.getDirName(path);
                    String base2 = PathUtilities.getDirName(referenceFile);
                    FileObject baseFO1 = (base1 == null) ? fs.getRoot() : getFileObject(fs, base1);
                    FileObject baseFO2 = (base2 == null) ? fs.getRoot() : getFileObject(fs, base2);
                    if (baseFO1 instanceof RemoteFileObject) {
                        ((RemoteFileObject) baseFO1).nonRecursiveRefresh();
                    }
                    if (baseFO2 instanceof RemoteFileObject && ! baseFO2.equals(baseFO1)) {
                        ((RemoteFileObject) baseFO2).nonRecursiveRefresh();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            } else {
                RemoteLogger.info("Error setting timestamp for {0}:{1} from {2} rc={3} {4}", //NOI18N
                        env, path, env, referenceFile, res.exitCode, res.error);
            }
        }
    }
}
