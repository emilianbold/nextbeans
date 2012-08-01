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
package org.netbeans.modules.web.clientproject.sites;

import java.awt.EventQueue;
import java.io.*;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.Places;
import org.openide.util.NbBundle;

/**
 *
 */
@NbBundle.Messages({"MSG_Progress2=Downloading site template",
    "MSG_Progress3=Unziping site template",
    "ERR_EmptyZip=ZIP file with site template is either empty or its download failed.",
    "ERR_StreamError=There was a network error trying to download the site template. See IDE log for more details.",
    "ERR_NotFoundError=The template file cannot be found at {0}"
})
public class SiteHelper {

    private static final Logger LOGGER = Logger.getLogger(SiteHelper.class.getName());
    private static final String JS_LIBS_DIR = "jslibs"; // NOI18N


    private SiteHelper() {
    }

    /**
     * Return <i>&lt;var/cache>/jslibs</i> directory.
     * @return <i>&lt;var/cache>/jslibs</i> directory
     */
    public static File getJsLibDirectory() {
        return Places.getCacheSubdirectory(JS_LIBS_DIR);
    }

    /**
     * Download the given URL to the target file.
     * @param url URL to be downloaded
     * @param target target file
     * @param progressHandle progress handle, can be {@code null}
     * @throws IOException if any error occurs
     */
    @NbBundle.Messages({
        "# {0} - file name",
        "SiteHelper.progress.download=Downloading file {0}"
    })
    public static void download(String url, File target, @NullAllowed ProgressHandle progressHandle) throws IOException {
        assert !EventQueue.isDispatchThread();
        if (progressHandle != null) {
            progressHandle.progress(Bundle.SiteHelper_progress_download(target.getName()));
        }
        InputStream is = new URL(url).openStream();
        try {
            copyToFile(is, target);
        } finally {
            is.close();
        }
    }

    /**
     * Unzip the given ZIP file to the given target directory. The target directory must exist.
     * @param zipFile ZIP file to be extracted
     * @param targetDirectory existing target directory
     * @param progressHandle progress handle, can be {@code null}
     * @throws IOException if any error occurs
     */
    @NbBundle.Messages({
        "# {0} - file name",
        "SiteHelper.progress.unzip=Unziping file {0}"
    })
    public static void unzip(File zipFile, File targetDirectory, @NullAllowed ProgressHandle progressHandle) throws IOException {
        assert targetDirectory.isDirectory() : "Target directory must be a directory: " + targetDirectory;
        if (progressHandle != null) {
            progressHandle.progress(Bundle.SiteHelper_progress_unzip(zipFile.getName()));
        }
        String rootFolder = getZipRootFolder(new FileInputStream(zipFile));
        unZipFile(new FileInputStream(zipFile), FileUtil.toFileObject(targetDirectory), null, rootFolder);
    }

    private static void unZipFile(InputStream source, FileObject projectRoot, ProgressHandle handle, String rootFolder) throws IOException {
        boolean firstItem = true;
        try {
            int stripLen = rootFolder != null ? rootFolder.length() : 0;
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (stripLen > 0) {
                    entryName = entryName.substring(stripLen);
                }
                if (entryName.length() == 0) {
                    continue;
                }
                firstItem = false;
                if (entry.isDirectory()) {
                    // ignore build folder from mobile boilerplate; unrelated junk IMO.
                    if (entryName.startsWith("build") || entryName.startsWith("nbproject")) {
                        continue;
                    }
                    FileUtil.createFolder(projectRoot, entryName);
                } else {
                    // ignore internal GIT files:
                    if (entryName.startsWith(".git") || entryName.contains("/.git")) {
                        continue;
                    }
                    // ignore build folder from mobile boilerplate; unrelated junk IMO.
                    if (entryName.startsWith("build/") || entryName.startsWith("nbproject/")) {
                        continue;
                    }
                    FileObject fo = FileUtil.createData(projectRoot, entryName);
                    writeFile(str, fo);
                }
            }
        } finally {
            source.close();
            if (firstItem) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(Bundle.ERR_EmptyZip()));
            }
        }
    }

    private static void writeFile(ZipInputStream str, FileObject fo) throws IOException {
        OutputStream out = fo.getOutputStream();
        try {
            FileUtil.copy(str, out);
        } finally {
            out.close();
        }
    }

    private static File copyToFile(InputStream is, File target) throws IOException {
        OutputStream os = new FileOutputStream(target);
        try {
            FileUtil.copy(is, os);
        } finally {
            os.close();
        }
        return target;
    }

    private static String getZipRootFolder(InputStream source) throws IOException {
        String folder = null;
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            boolean first = true;
            while ((entry = str.getNextEntry()) != null) {
                if (first) {
                    first = false;
                    if (entry.isDirectory()) {
                        folder = entry.getName();
                    } else {
                        return null;
                    }
                } else {
                    if (!entry.getName().startsWith(folder)) {
                        return null;
                    }
                }
            }
        } finally {
            source.close();
        }
        return folder;
    }

}
