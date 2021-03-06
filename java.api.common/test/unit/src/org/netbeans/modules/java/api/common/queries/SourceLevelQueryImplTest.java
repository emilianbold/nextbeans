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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.api.common.queries;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.Permission;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.platform.implspi.JavaPlatformProvider;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation2;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.netbeans.api.project.TestUtil;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.netbeans.spi.project.support.ant.AntBasedTestUtil;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Mutex;
import org.openide.util.test.MockLookup;

/**
 * Tests for {@link SourceLevelQueryImpl}.
 *
 * @author Tomas Mysik
 */
public class SourceLevelQueryImplTest extends NbTestCase {

    static {
        System.setSecurityManager(new SecurityManager() {
            public @Override void checkPermission(Permission perm) {
                if (perm instanceof SocketPermission) {
                    throw new SecurityException();
                }
            }
            public @Override void checkPermission(Permission perm, Object context) {
                if (perm instanceof SocketPermission) {
                    throw new SecurityException();
                }
            }
        });
    }

    private static final String JDK_8 = "8";    //NOI18N
    private static final String JDK_8_ALIAS = "1.8";    //NOI18N
    private static final String JDK_7_ALIAS = "1.7";    //NOI18N
    private static final String JAVAC_SOURCE = "1.2";
    private static final String DEFAULT_JAVAC_SOURCE = "17.2";

    private static final String TEST_PLATFORM = "TestPlatform";
    private static final String BROKEN_PLATFORM = "BrokenPlatform";

    private FileObject scratch;
    private FileObject projdir;
    private AntProjectHelper helper;
    private PropertyEvaluator eval;
    private Project prj;

    public SourceLevelQueryImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        MockLookup.setInstances(
                AntBasedTestUtil.testAntBasedProjectType(),
                new TestPlatformProvider());
        this.clearWorkDir();
        Properties p = System.getProperties();
        if (p.getProperty("netbeans.user") == null) {
            p.put("netbeans.user", FileUtil.toFile(TestUtil.makeScratchDir(this)).getAbsolutePath());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        scratch = null;
        projdir = null;
        super.tearDown();
    }


    private void prepareProject(
            @NonNull final String platformName) throws IOException {
        prepareProject(platformName, null, null, null);
    }
    private void prepareProject(
            @NonNull final String platformName,
            @NullAllowed final String sourceLevel,
            @NullAllowed final String targetLevel,
            @NullAllowed final String profile) throws IOException {
        scratch = TestUtil.makeScratchDir(this);
        projdir = scratch.createFolder("proj");
        helper = ProjectGenerator.createProject(projdir, "test");
        assertNotNull(helper);
        prj = ProjectManager.getDefault().findProject(projdir);
        assertNotNull(prj);
        EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        props.setProperty("javac.source", "${def}");
        props.setProperty("javac.target",
            targetLevel == null ?
            "${def}" :
            targetLevel);
        props.setProperty("platform.active", platformName);
        props.setProperty("def",
                sourceLevel != null ?
                sourceLevel :
                JAVAC_SOURCE);
        if (profile != null) {
            props.setProperty("javac.profile", profile);    //NOI18N
        }
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
        props = PropertyUtils.getGlobalProperties();
        props.put("default.javac.source", DEFAULT_JAVAC_SOURCE);
        PropertyUtils.putGlobalProperties(props);
        eval = helper.getStandardPropertyEvaluator();
        assertNotNull(eval);
    }

    public void testGetSourceLevelWithValidPlatform() throws Exception {
        this.prepareProject(TEST_PLATFORM);

        FileObject dummy = projdir.createData("Dummy.java");
        SourceLevelQueryImplementation sourceLevelQuery = QuerySupport.createSourceLevelQuery(eval);

        String sl = sourceLevelQuery.getSourceLevel(dummy);
        assertEquals(JAVAC_SOURCE, sl);
    }

    public void testGetSourceLevelWithBrokenPlatform() throws Exception {
        this.prepareProject(BROKEN_PLATFORM);

        FileObject dummy = projdir.createData("Dummy.java");
        SourceLevelQueryImplementation sourceLevelQuery = QuerySupport.createSourceLevelQuery(eval);

        String sl = sourceLevelQuery.getSourceLevel(dummy);
        assertEquals(DEFAULT_JAVAC_SOURCE, sl);
    }

    public void testSourceLevelQuery2() throws Exception {
        this.prepareProject(TEST_PLATFORM);
        final FileObject dummy = projdir.createData("Dummy.java");
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertNotNull(result);
        assertEquals(JAVAC_SOURCE, result.getSourceLevel().toString());
    }

    public void testFiring() throws Exception {
        this.prepareProject(TEST_PLATFORM);
        final FileObject dummy = projdir.createData("Dummy.java");
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertNotNull(result);
        assertEquals(JAVAC_SOURCE, result.getSourceLevel().toString());
        class TestChangeListener implements ChangeListener {
            final AtomicInteger ec = new AtomicInteger();
            @Override
            public void stateChanged(final ChangeEvent event) {
                ec.incrementAndGet();
            }
        }
        final TestChangeListener tl = new TestChangeListener();
        result.addChangeListener(tl);
        final EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        props.setProperty("javac.source", "1.7");
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
        assertEquals(1, tl.ec.intValue());
        assertEquals("1.7", result.getSourceLevel().toString());
    }

    public void testProfilesJDK8ProfileGiven() throws IOException {
        this.prepareProject(TEST_PLATFORM, JDK_8, null, SourceLevelQuery.Profile.COMPACT2.getName());
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.COMPACT2, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
    }

    public void testProfilesJDK8AliasProfileGiven() throws IOException {
        this.prepareProject(TEST_PLATFORM, JDK_8_ALIAS, null, SourceLevelQuery.Profile.COMPACT2.getName());
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.COMPACT2, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
    }

    public void testProfilesJDK8AliasProfileNotGiven() throws IOException {
        this.prepareProject(TEST_PLATFORM, JDK_8, null, null);
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.DEFAULT, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
    }

    public void testProfilesOldJDKAliasProfileGiven() throws IOException {
        this.prepareProject(TEST_PLATFORM, JAVAC_SOURCE, null, SourceLevelQuery.Profile.COMPACT2.getName());
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.DEFAULT, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
    }

    public void testProfilesSourceJDK7AliasTargetJDK8ProfileGiven() throws IOException {
        this.prepareProject(TEST_PLATFORM, JDK_7_ALIAS, JDK_8_ALIAS, SourceLevelQuery.Profile.COMPACT2.getName());
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.COMPACT2, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
    }

    public void testProfileChanges() throws Exception {
        prepareProject(TEST_PLATFORM, JDK_8, null, SourceLevelQuery.Profile.COMPACT1.getName());
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.COMPACT1, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
        ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                final EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                props.setProperty("javac.profile", SourceLevelQuery.Profile.COMPACT2.getName());   //NOI18N
                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                ProjectManager.getDefault().saveProject(prj);
                return null;
            }
        });
        result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.COMPACT2, ((SourceLevelQueryImplementation2.Result2)result).getProfile());

    }

    public void testProfileListening() throws Exception {
        prepareProject(TEST_PLATFORM, JDK_8, null, SourceLevelQuery.Profile.COMPACT1.getName());
        final FileObject dummy = projdir.createData("Dummy.java");  //NOI18N
        final SourceLevelQueryImplementation2 sourceLevelQuery = QuerySupport.createSourceLevelQuery2(eval);
        final SourceLevelQueryImplementation2.Result result = sourceLevelQuery.getSourceLevel(dummy);
        assertTrue(result instanceof SourceLevelQueryImplementation2.Result2);
        assertEquals(SourceLevelQuery.Profile.COMPACT1, ((SourceLevelQueryImplementation2.Result2)result).getProfile());
        final AtomicInteger eventCount = new AtomicInteger();
        final ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                eventCount.getAndIncrement();
            }
        };
        result.addChangeListener(listener);
        ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                final EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                props.setProperty("javac.profile", SourceLevelQuery.Profile.COMPACT2.getName());   //NOI18N
                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                ProjectManager.getDefault().saveProject(prj);
                return null;
            }
        });
        assertEquals(1, eventCount.get());
        assertEquals(SourceLevelQuery.Profile.COMPACT2, ((SourceLevelQueryImplementation2.Result2)result).getProfile());

    }

    private static class TestPlatformProvider implements JavaPlatformProvider {

        private JavaPlatform platform;

        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public JavaPlatform[] getInstalledPlatforms()  {
            return new JavaPlatform[] {
                getDefaultPlatform()
            };
        }

        public JavaPlatform getDefaultPlatform()  {
            if (this.platform == null) {
                this.platform = new TestPlatform();
            }
            return this.platform;
        }
    }

    private static class TestPlatform extends JavaPlatform {

        public FileObject findTool(String toolName) {
            return null;
        }

        public String getVendor() {
            return "me";
        }

        public ClassPath getStandardLibraries() {
            return ClassPathSupport.createClassPath(new URL[0]);
        }

        public Specification getSpecification() {
            return new Specification("j2se", new SpecificationVersion("1.5"));
        }

        public ClassPath getSourceFolders() {
            return null;
        }

        public Map<String, String> getProperties() {
            return Collections.singletonMap("platform.ant.name", TEST_PLATFORM);
        }

        public List<URL> getJavadocFolders() {
            return null;
        }

        public Collection<FileObject> getInstallFolders() {
            return null;
        }

        public String getDisplayName() {
            return "TestPlatform";
        }

        public ClassPath getBootstrapLibraries() {
            return ClassPathSupport.createClassPath(new URL[0]);
        }
    }
}
