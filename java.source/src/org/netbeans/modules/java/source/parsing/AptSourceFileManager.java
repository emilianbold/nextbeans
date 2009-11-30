/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.source.parsing;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.java.source.classpath.AptCacheForSourceQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Zezula
 */
public class AptSourceFileManager extends SourceFileManager {

    private final ClassPath userRoots;

    public AptSourceFileManager (final @NonNull ClassPath userRoots,
                              final @NonNull ClassPath aptRoots,
                              final boolean ignoreExcludes) {
        super(aptRoots,ignoreExcludes);
        assert userRoots != null;
        this.userRoots = userRoots;
    }

    @Override
    public javax.tools.FileObject getFileForOutput(Location l, String pkgName, String relativeName, javax.tools.FileObject sibling)
        throws IOException, UnsupportedOperationException, IllegalArgumentException {
        if (StandardLocation.SOURCE_OUTPUT != l) {
            throw new UnsupportedOperationException("Only apt output is supported."); // NOI18N
        }
        final FileObject aptRoot = getAptRoot(sibling);
        if (aptRoot == null) {
            throw new UnsupportedOperationException("No apt root for source root: " + getOwnerRoot(sibling)); // NOI18N
        }
        final String nameStr = pkgName.length() == 0 ?
            relativeName :
            pkgName.replace('.', '/') + '/' + relativeName;    //NOI18N
        final FileObject file = FileUtil.createData(aptRoot, nameStr);
        assert file != null;
        return FileObjects.nbFileObject(file, aptRoot);
    }


    @Override
    public JavaFileObject getJavaFileForOutput (Location l, String className, JavaFileObject.Kind kind, javax.tools.FileObject sibling)
        throws IOException, UnsupportedOperationException, IllegalArgumentException {
        if (StandardLocation.SOURCE_OUTPUT != l) {
            throw new UnsupportedOperationException("Only apt output is supported."); // NOI18N
        }
        final FileObject aptRoot = getAptRoot(sibling);
        if (aptRoot == null) {
            throw new UnsupportedOperationException("No apt root for source root: " + getOwnerRoot(sibling)); // NOI18N
        }
        final String nameStr = className.replace('.', '/') + kind.extension;    //NOI18N
        final FileObject file = FileUtil.createData(aptRoot, nameStr);
        assert file != null;
        return FileObjects.nbFileObject(file, aptRoot);
    }

    private FileObject getAptRoot (final javax.tools.FileObject sibling) {
        final URL ownerRoot = getOwnerRoot (sibling);
        if (ownerRoot == null) {
            return null;
        }
        final URL aptRoot = AptCacheForSourceQuery.getAptFolder(ownerRoot);
        return aptRoot == null ? null : URLMapper.findFileObject(aptRoot);
    }

    private URL getOwnerRoot (final javax.tools.FileObject sibling) {
        try {
            return sibling == null ? getOwnerRootNoSib() : getOwnerRootSib(sibling);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    private URL getOwnerRootSib (final javax.tools.FileObject sibling) throws MalformedURLException {
        assert sibling != null;
        final URL sibURL = sibling.toUri().toURL();
        for (ClassPath.Entry entry : userRoots.entries()) {
            final URL rootURL = entry.getURL();
            if (FileObjects.isParentOf(rootURL, sibURL)) {
                return rootURL;
            }
        }
        return null;
    }

    private URL getOwnerRootNoSib () {
        //todo: fix me, now supports just 1 src root
        final List<ClassPath.Entry> entries = userRoots.entries();
        return entries.size() == 1 ? entries.get(0).getURL() : null;
    }

}
