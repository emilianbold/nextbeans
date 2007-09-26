/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.masterfs;

import java.net.URISyntaxException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.URLMapper;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Implements URLMapper for MasterFileSystem.
 * @author  rm111737
 */
public final class MasterURLMapper extends URLMapper {
    /** Creates a new instance of MasterURLMapper */
    public MasterURLMapper() {
    }

    public FileObject[] getFileObjects(final URL url) {                
        final FileSystem hfs = MasterFileSystem.getDefault();
        if (!url.getProtocol().equals("file")) return null;  //NOI18N
        //TODO: review and simplify         
        FileObject retVal = null;
        String filePath = null;
        try {
            filePath = FileUtil.normalizeFile(new File(new URI(url.toExternalForm()))).getAbsolutePath();
        } catch (URISyntaxException e) {
            StringBuilder sb = new StringBuilder();            
            sb.append(e.getLocalizedMessage()).append(" [").append(url.toExternalForm()).append(']');//NOI18N
            IllegalArgumentException iax = new IllegalArgumentException(sb.toString());
            if (Utilities.isWindows() && url.getAuthority() != null) {
                Exceptions.attachLocalizedMessage(iax,NbBundle.getMessage(MasterURLMapper.class, "MSG_UNC_PATH"));//NOI18N
            }            
            Exceptions.printStackTrace(iax);
            return null;
        }

        retVal = hfs.findResource(filePath);
        if (!(retVal instanceof MasterFileObject)) return null;
        if (!retVal.isValid()) return null;
        ProvidedExtensionsProxy.checkReentrancy();
        return new FileObject[]{retVal};
    }

    public URL getURL(final FileObject fo, final int type) {
        if (type == URLMapper.NETWORK || !(fo instanceof MasterFileObject)) return null;        
        MasterFileObject hfo = (MasterFileObject) fo;
        File f = (hfo != null) ? hfo.getResource().getFile() : null;

        try {
            return (f != null) ? fileToURL(f, fo) : null;
        } catch (MalformedURLException mfx) {
            return null;
        }
    }
    
    private static boolean isWindowsDriveRoot(File file) {
        return (Utilities.isWindows () || (Utilities.getOperatingSystem () == Utilities.OS_OS2)) && file.getParent() == null;
    }
    
    static URL fileToURL(File file, FileObject fo) throws MalformedURLException {        
        URL retVal = null;
        if (isWindowsDriveRoot(file)) {
            retVal = new URL ("file:/"+file.getAbsolutePath ());//NOI18N            
        } else {
            if (fo.isFolder() && (!fo.isValid() || fo.isVirtual())) {                
                String urlDef = file.toURI().toURL().toExternalForm();
                String pathSeparator = "/";//NOI18N
                if (!urlDef.endsWith(pathSeparator)) {
                    retVal = new URL (urlDef + pathSeparator);     
                }                  
            }
            retVal = (retVal == null) ? file.toURI().toURL() : retVal;                        
        }        
        return retVal;
    }

}
