/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.jsf.editor.facelets;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.web.jsfapi.api.DefaultLibraryInfo;
import org.netbeans.modules.web.jsfapi.api.Library;
import org.netbeans.modules.web.jsfapi.api.LibraryComponent;
import org.netbeans.modules.web.jsfapi.api.LibraryInfo;
import org.netbeans.modules.web.jsfapi.api.LibraryType;
import org.netbeans.modules.web.jsfapi.api.NamespaceUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 * Access to facelet library descriptors in bundled web.jsf20 library's javax.faces.jar
 * Also provides some useful methods for getting default library's displayname or
 * default prefix.
 *
 * @author marekfukala
 */
public class DefaultFaceletLibraries {

    private static DefaultFaceletLibraries INSTANCE;
    private Collection<FileObject> libraryDescriptorsFiles;
    private Map<String, FaceletsLibraryDescriptor> librariesDescriptors;
    private static Map<String, Library> jsf22FaceletPseudoLibraries;

    public static synchronized DefaultFaceletLibraries getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultFaceletLibraries();
        }
        return INSTANCE;
    }

    public DefaultFaceletLibraries() {
        init();
    }

    private void init() {
        File jsfImplJar = InstalledFileLocator.getDefault().locate(
                "modules/ext/jsf-2_2/javax.faces.jar", //NOI18N
                "org.netbeans.modules.web.jsf20", false); //NOI18N
        assert jsfImplJar != null;

        FileObject jsfImplJarFo = FileUtil.getArchiveRoot(FileUtil.toFileObject(jsfImplJar));
        libraryDescriptorsFiles = findLibraryDescriptors(jsfImplJarFo, ".taglib.xml"); //NOI18N

    }

    public Collection<FileObject> getLibrariesDescriptorsFiles() {
        return this.libraryDescriptorsFiles;
    }

    public synchronized Map<String, FaceletsLibraryDescriptor> getLibrariesDescriptors() {
        if(librariesDescriptors == null) {
            librariesDescriptors = new HashMap<>();
            parseLibraries();
        }
        return librariesDescriptors;
    }

    private void parseLibraries() {
        for(FileObject lfo : getLibrariesDescriptorsFiles()) {
            FaceletsLibraryDescriptor descritor;
            try {
                descritor = FaceletsLibraryDescriptor.create(lfo);
                librariesDescriptors.put(descritor.getNamespace(), descritor);
            } catch (LibraryDescriptorException ex) {
                Logger.global.log(Level.WARNING, "Error parsing facelets library " +
                        FileUtil.getFileDisplayName(lfo) + " in javax.faces.jar from bundled web.jsf20 library", ex);
            }
        }

    }

    public static String getLibraryDisplayName(String uri) {
        LibraryInfo li = DefaultLibraryInfo.forNamespace(uri);
        return li != null ? li.getDisplayName() : null;
    }

    public static String getLibraryDefaultPrefix(String uri) {
        LibraryInfo li = DefaultLibraryInfo.forNamespace(uri);
        return li != null ? li.getDefaultPrefix() : null;
    }

     private static Collection<FileObject> findLibraryDescriptors(FileObject classpathRoot, String suffix) {
        Collection<FileObject> files = new ArrayList<>();
        Enumeration<? extends FileObject> fos = classpathRoot.getChildren(true); //scan all files in the jar
        while (fos.hasMoreElements()) {
            FileObject file = fos.nextElement();
            if(!file.isValid() || !file.isData()) {
                continue;
}
            if (file.getNameExt().toLowerCase(Locale.US).endsWith(suffix)) { //NOI18N
                //found library, create a new instance and cache it
                files.add(file);
            }
        }
        return files;
    }

    protected synchronized static Map<String, Library> getJsf22FaceletPseudoLibraries(FaceletsLibrarySupport support) {
        if (jsf22FaceletPseudoLibraries == null) {
            jsf22FaceletPseudoLibraries = new HashMap<>(2);
            jsf22FaceletPseudoLibraries.put(DefaultLibraryInfo.JSF.getLegacyNamespace(), new JsfFaceletPseudoLibrary(support, DefaultLibraryInfo.JSF));
            jsf22FaceletPseudoLibraries.put(DefaultLibraryInfo.PASSTHROUGH.getLegacyNamespace(), new JsfFaceletPseudoLibrary(support, DefaultLibraryInfo.PASSTHROUGH));
        }
        return jsf22FaceletPseudoLibraries;
    }

    private static class JsfFaceletPseudoLibrary implements Library {

        private final String namespace;
        private final String prefix;
        private final String displayName;

        public JsfFaceletPseudoLibrary(FaceletsLibrarySupport support, DefaultLibraryInfo defaultLibraryInfo) {
            this.namespace = defaultLibraryInfo.getNamespace();
            this.prefix = defaultLibraryInfo.getDefaultPrefix();
            this.displayName = defaultLibraryInfo.getDisplayName();
        }

        @Override
        public String getDefaultPrefix() {
            return prefix;
        }

        @Override
        public String getDefaultNamespace() {
            return null;
        }

        @Override
        public LibraryType getType() {
            return LibraryType.CLASS;
        }

        @Override
        public String getNamespace() {
            return namespace;
        }

        @Override
        public Collection<? extends LibraryComponent> getComponents() {
            return Collections.emptyList();
        }

        @Override
        public LibraryComponent getComponent(String componentName) {
            return null;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String getLegacyNamespace() {
            return NamespaceUtils.NS_MAPPING.get(namespace);
        }

    }
}
