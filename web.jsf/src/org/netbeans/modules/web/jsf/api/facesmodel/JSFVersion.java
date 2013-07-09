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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.web.jsf.api.facesmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.common.ClasspathUtil;
import org.netbeans.modules.j2ee.common.ProjectUtil;
import org.netbeans.modules.j2ee.deployment.common.api.Version;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerLibrary;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.jsf.JSFUtils;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Petr Pisl, ads, Martin Fousek
 */
public enum JSFVersion {
    JSF_1_0("JSF 1.0"),
    JSF_1_1("JSF 1.1"),
    JSF_1_2("JSF 1.2"),
    JSF_2_0("JSF 2.0"),
    JSF_2_1("JSF 2.1"),
    JSF_2_2("JSF 2.2");

    private static final LinkedHashMap<JSFVersion, String> SPECIFIC_CLASS_NAMES = new LinkedHashMap<JSFVersion, String>();

    static {
        SPECIFIC_CLASS_NAMES.put(JSFVersion.JSF_2_2, JSFUtils.JSF_2_2__API_SPECIFIC_CLASS);
        SPECIFIC_CLASS_NAMES.put(JSFVersion.JSF_2_1, JSFUtils.JSF_2_1__API_SPECIFIC_CLASS);
        SPECIFIC_CLASS_NAMES.put(JSFVersion.JSF_2_0, JSFUtils.JSF_2_0__API_SPECIFIC_CLASS);
        SPECIFIC_CLASS_NAMES.put(JSFVersion.JSF_1_2, JSFUtils.JSF_1_2__API_SPECIFIC_CLASS);
        SPECIFIC_CLASS_NAMES.put(JSFVersion.JSF_1_1, JSFUtils.FACES_EXCEPTION);
    }

    private final String shortName;


    private JSFVersion(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
    
    private static final RequestProcessor RP = new RequestProcessor(JSFVersion.class);
    private static final Logger LOG = Logger.getLogger(JSFVersion.class.getName());

    // caches for holding JSF version and the project CP listeners
    private static final Map<WebModule, JSFVersion> projectVersionCache = new WeakHashMap<WebModule, JSFVersion>();
    private static final Map<WebModule, PropertyChangeListener> projectListenerCache = new WeakHashMap<WebModule, PropertyChangeListener>();

    /**
     * Gets the JSF version supported by the WebModule. It seeks for the JSF only on the classpath including the
     * platform classpath.
     *
     * @param webModule WebModule to seek for JSF version
     * @return JSF version if any found on the WebModule compile classpath, {@code null} otherwise
     */
    @CheckForNull
    public synchronized static JSFVersion forWebModule(@NonNull final WebModule webModule) {
        Parameters.notNull("webModule", webModule); //NOI18N
        JSFVersion version = projectVersionCache.get(webModule);
        if (version == null) {
            version = get(webModule, true);
            ClassPath compileCP = getCompileClasspath(webModule);
            if (compileCP == null) {
                return version;
            }
            PropertyChangeListener listener = WeakListeners.propertyChange(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (ClassPath.PROP_ROOTS.equals(evt.getPropertyName())) {
                        projectVersionCache.put(webModule, get(webModule, true));
                    }
                }
            }, compileCP);
            compileCP.addPropertyChangeListener(listener);
            projectListenerCache.put(webModule, listener);
            projectVersionCache.put(webModule, get(webModule, true));
        }
        return version;
    }

    /**
     * Gets the highest JSF version found on the classpath. This method can
     * be slow and sholdn't be called within AWT EDT.
     *
     * @param classpath consists of jar files and folders containing classes
     * @return JSF version if any found on the classpath, {@code null} otherwise
     * @since 1.46
     */
    @CheckForNull
    public static synchronized JSFVersion forClasspath(@NonNull Collection<File> classpath) {
        Parameters.notNull("classpath", classpath); //NOI18N
        try {
            return ClasspathUtil.containsClass(classpath, SPECIFIC_CLASS_NAMES);
        } catch (IOException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return null;
    }

    /**
     * Gets the highest JSF version found on the classpath. This method can
     * be slow and sholdn't be called within AWT EDT.
     *
     * @param classpath consists of jar files and folders containing classes
     * @return JSF version if any found on the classpath, {@code null} otherwise
     * @since 1.46
     */
    @CheckForNull
    public static JSFVersion forClasspath(@NonNull List<URL> classpath) {
        Parameters.notNull("classpath", classpath); //NOI18N
        try {
            return ClasspathUtil.containsClass(classpath, SPECIFIC_CLASS_NAMES);
        } catch (IOException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return null;
    }

    /**
     * Gets the JSF version of the server library if any.
     *
     * @param lib server library to detect
     * @return JSF version if valid JSF server library, {@code null} otherwise
     * @since 1.46
     */
    @CheckForNull
    public static JSFVersion forServerLibrary(@NonNull ServerLibrary lib) {
        Parameters.notNull("serverLibrary", lib); //NOI18N
        if ("JavaServer Faces".equals(lib.getSpecificationTitle())) { // NOI18N
            if (Version.fromJsr277NotationWithFallback("2.2").equals(lib.getSpecificationVersion())) { //NOI18N
                return JSFVersion.JSF_2_2;
            } else if (Version.fromJsr277NotationWithFallback("2.1").equals(lib.getSpecificationVersion())) { //NOI18N
                return JSFVersion.JSF_2_1;
            } else if (Version.fromJsr277NotationWithFallback("2.0").equals(lib.getSpecificationVersion())) { // NOI18N
                return JSFVersion.JSF_2_0;
            } else if (Version.fromJsr277NotationWithFallback("1.2").equals(lib.getSpecificationVersion())) { // NOI18N
                return JSFVersion.JSF_1_2;
            } else if (Version.fromJsr277NotationWithFallback("1.1").equals(lib.getSpecificationVersion())) { // NOI18N
                return JSFVersion.JSF_1_1;
            } else {
                LOG.log(Level.INFO, "Unknown JSF version {0}", lib.getSpecificationVersion());
            }
        }
        return null;
    }

    /**
     * Says whether the current instance is at least of the same JSF version.
     *
     * @param version version to compare
     * @return {@code true} if the current instance is at least of the given version, {@code false} otherwise
     */
    public boolean isAtLeast(@NonNull JSFVersion version) {
        Parameters.notNull("version", version); //NOI18N
        int thisMajorVersion = Integer.parseInt(this.name().substring(4, 5));
        int thisMinorVersion = Integer.parseInt(this.name().substring(6, 7));
        int compMajorVersion = Integer.parseInt(version.name().substring(4, 5));
        int compMinorVersion = Integer.parseInt(version.name().substring(6, 7));
        return thisMajorVersion > compMajorVersion
                || thisMajorVersion == compMajorVersion && thisMinorVersion >= compMinorVersion;
    }

    /**
     * Gets version of the JSF on the project classpath. You can specify whether the classpath should include
     * platform's classpath too. If you don't need to exclude platform classpath use the
     * {@link #forWebModule(org.netbeans.modules.web.api.webmodule.WebModule)} which caches its results per project.
     *
     * @param webModule webModule
     * @param includingPlatformCP whether to include platform into the JSF version investigation of not
     * @return JSF version
     */
    @CheckForNull
    public static JSFVersion get(@NonNull WebModule webModule, boolean includingPlatformCP) {
        Parameters.notNull("webModule", webModule); //NOI18N
        if (webModule.getDocumentBase() == null) {
            return null;
        }

        ClassPath compileCP = ClassPath.getClassPath(webModule.getDocumentBase(), ClassPath.COMPILE);
        if (compileCP == null) {
            return null;
        }

        if (includingPlatformCP) {
            for (Map.Entry<JSFVersion, String> entry : SPECIFIC_CLASS_NAMES.entrySet()) {
                String className = entry.getValue();
                if (compileCP.findResource(className.replace('.', '/') + ".class") != null) { //NOI18N
                    return entry.getKey();
                }
            }
            return null;
        } else {
            Project project = FileOwnerQuery.getOwner(JSFUtils.getFileObject(webModule));
            if (project == null) {
                return null;
            }
            List<File> platformClasspath = Arrays.asList(ClasspathUtil.getJ2eePlatformClasspathEntries(project, ProjectUtil.getPlatform(project)));
            List<URL> projectDeps = new ArrayList<URL>();
            for (ClassPath.Entry entry : compileCP.entries()) {
                File archiveOrDir = FileUtil.archiveOrDirForURL(entry.getURL());
                if (archiveOrDir == null || !platformClasspath.contains(archiveOrDir)) {
                    projectDeps.add(entry.getURL());
                }
            }
            try {
                return ClasspathUtil.containsClass(projectDeps, SPECIFIC_CLASS_NAMES);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    private static ClassPath getCompileClasspath(WebModule webModule) {
        FileObject projectFile = JSFUtils.getFileObject(webModule);
        if (projectFile == null) {
            return null;
        }

        Project project = FileOwnerQuery.getOwner(projectFile);
        if (project == null) {
            return null;
        }

        ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
        if (webModule.getDocumentBase() != null) {
            return cpp.findClassPath(webModule.getDocumentBase(), ClassPath.COMPILE);
        } else {
            Sources sources = ProjectUtils.getSources(project);
            if (sources == null) {
                return null;
            }

            SourceGroup[] sourceGroups = sources.getSourceGroups("java"); //NOII18N
            if (sourceGroups.length > 0) {
                return cpp.findClassPath(sourceGroups[0].getRootFolder(), ClassPath.COMPILE);
            }
        }
        return null;
    }
}
