/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.impl.indexing;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.Project;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizer;
import org.netbeans.modules.project.uiapi.OpenProjectsTrampoline;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tomas Zezula
 */
public class IndexerVersionsTest extends NbTestCase {

    private static final String MIME = "text/test";
    private static final String INDEXER_NAME = "Test";
    private static final String SOURCES = "src";
    private static final long TIMEOUT = 5000L;

    private MockIndexerFactory indexerFactory;
    private FileObject srcRoot1;
    private FileObject srcRoot2;
    private FileObject f1;
    private FileObject f2;
    private final Map<String, Set<ClassPath>> registeredClasspaths = new HashMap<String, Set<ClassPath>>();

    public IndexerVersionsTest(final String name) {
        super (name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.clearWorkDir();
        final File _wd = this.getWorkDir();
        final FileObject wd = FileUtil.toFileObject(_wd);
        final FileObject cache = wd.createFolder("cache");
        CacheFolder.setCacheFolder(cache);

        MockServices.setServices(MockPathRecognizer.class, MockOpenProject.class);
        indexerFactory = new MockIndexerFactory(1);
        MockMimeLookup.setInstances(MimePath.get(MIME), indexerFactory);
        Set<String> mt = new HashSet<String>();
        mt.add(MIME);
        Util.allMimeTypes = mt;
        assertNotNull("No masterfs",wd);
        srcRoot1 = wd.createFolder("src1");
        assertNotNull(srcRoot1);
        srcRoot2 = wd.createFolder("src2");
        assertNotNull(srcRoot2);
        
        FileUtil.setMIMEType("tst", MIME);
        f1 = FileUtil.createData(srcRoot1,"a.tst");
        assertNotNull(f1);
        assertEquals(MIME, f1.getMIMEType());
        f2 = FileUtil.createData(srcRoot2,"b.tst");
        assertNotNull(f2);
        assertEquals(MIME, f2.getMIMEType());
        RepositoryUpdaterTest.waitForRepositoryUpdaterInit();
    }

    @Override
    protected void tearDown() throws Exception {
        for(String id : registeredClasspaths.keySet()) {
            Set<ClassPath> classpaths = registeredClasspaths.get(id);
            GlobalPathRegistry.getDefault().unregister(id, classpaths.toArray(new ClassPath[classpaths.size()]));
        }

        super.tearDown();
    }


    public void testIndexerVersioning () throws Exception {
        RepositoryUpdater ru = RepositoryUpdater.getDefault();
        assertEquals(0, ru.getScannedBinaries().size());
        assertEquals(0, ru.getScannedBinaries().size());
        assertEquals(0, ru.getScannedUnknowns().size());
        assertNull(CacheFolder.getDataFolder(srcRoot1.getURL()).getFileObject(indexerFactory.getIndexerName()));
        assertNull(CacheFolder.getDataFolder(srcRoot2.getURL()).getFileObject(indexerFactory.getIndexerName()));

        int firstVersion = indexerFactory.getIndexVersion();

        final MockHandler handler = new MockHandler();
        final Logger logger = Logger.getLogger(RepositoryUpdater.class.getName()+".tests");
        logger.setLevel (Level.FINEST);
        logger.addHandler(handler);

        ClassPath cp1 = ClassPathSupport.createClassPath(srcRoot1);
        globalPathRegistry_register(SOURCES,new ClassPath[]{cp1});
        assertTrue (handler.await(TIMEOUT));
        assertEquals(1, handler.getSources().size());
        assertEquals(this.srcRoot1.getURL(), handler.getSources().get(0));
        assertNotNull(CacheFolder.getDataFolder(srcRoot1.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), firstVersion)));

        handler.reset();
        ClassPath cp2 = ClassPathSupport.createClassPath(srcRoot2);
        globalPathRegistry_register(SOURCES,new ClassPath[]{cp2});
        assertTrue (handler.await(TIMEOUT));
        assertEquals(1, handler.getSources().size());
        assertEquals(this.srcRoot2.getURL(), handler.getSources().get(0));
        assertNotNull(CacheFolder.getDataFolder(srcRoot2.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), firstVersion)));

        handler.reset();
        globalPathRegistry_unregister(SOURCES, new ClassPath[]{cp1,cp2});
        assertTrue (handler.await(TIMEOUT));
        assertTrue(handler.getSources().isEmpty());


        indexerFactory = new MockIndexerFactory(2);
        int secondVersion = indexerFactory.getIndexVersion();
        assertNull(CacheFolder.getDataFolder(srcRoot1.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), secondVersion)));
        assertNull(CacheFolder.getDataFolder(srcRoot2.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), secondVersion)));        
        MockMimeLookup.setInstances(MimePath.get(MIME), indexerFactory);

        handler.reset();
        globalPathRegistry_register(SOURCES,new ClassPath[]{cp1});
        assertTrue (handler.await(TIMEOUT));
        assertEquals(1, handler.getSources().size());
        assertEquals(this.srcRoot1.getURL(), handler.getSources().get(0));
        assertNotNull(CacheFolder.getDataFolder(srcRoot1.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), firstVersion)));
        assertNotNull(CacheFolder.getDataFolder(srcRoot1.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), secondVersion)));


        handler.reset();
        globalPathRegistry_register(SOURCES,new ClassPath[]{cp2});
        assertTrue (handler.await(TIMEOUT));
        assertEquals(1, handler.getSources().size());
        assertEquals(this.srcRoot2.getURL(), handler.getSources().get(0));
        assertNotNull(CacheFolder.getDataFolder(srcRoot2.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), firstVersion)));
        assertNotNull(CacheFolder.getDataFolder(srcRoot2.getURL()).getFileObject(String.format("%s/%d", indexerFactory.getIndexerName(), secondVersion)));

    }



    // <editor-fold defaultstate="collapsed" desc="Helper methods">
    protected final void globalPathRegistry_register(String id, ClassPath [] classpaths) {
        Set<ClassPath> set = registeredClasspaths.get(id);
        if (set == null) {
            set = new HashSet<ClassPath>();
            registeredClasspaths.put(id, set);
        }
        set.addAll(Arrays.asList(classpaths));
        GlobalPathRegistry.getDefault().register(id, classpaths);
    }

    protected final void globalPathRegistry_unregister(String id, ClassPath [] classpaths) {
        GlobalPathRegistry.getDefault().unregister(id, classpaths);
        Set<ClassPath> set = registeredClasspaths.get(id);
        if (set != null) {
            set.removeAll(Arrays.asList(classpaths));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mock Services">
    public static class MockOpenProject implements  OpenProjectsTrampoline {

        public @Override Project[] getOpenProjectsAPI() {
            return new Project[0];
        }

        public @Override void openAPI(Project[] projects, boolean openRequiredProjects, boolean showProgress) {

        }

        public @Override void closeAPI(Project[] projects) {

        }

        @Override
        public void addPropertyChangeListenerAPI(PropertyChangeListener listener, Object source) {

        }

        @Override
        public Future<Project[]> openProjectsAPI() {
            return new Future<Project[]>() {

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return true;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public Project[] get() throws InterruptedException, ExecutionException {
                    return new Project[0];
                }

                @Override
                public Project[] get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return new Project[0];
                }
            };
        }

        @Override
        public void removePropertyChangeListenerAPI(PropertyChangeListener listener) {

        }

        public @Override Project getMainProject() {
            return null;
        }

        public @Override void setMainProject(Project project) {

        }
    }

    public static class MockPathRecognizer extends PathRecognizer {

        @Override
        public Set<String> getSourcePathIds() {
            return Collections.<String>singleton(SOURCES);    //NOI18N
        }

        @Override
        public Set<String> getLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getBinaryLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getMimeTypes() {
            return Collections.<String>singleton(MIME);
        }
    }

    public static class MockIndexerFactory extends CustomIndexerFactory {

        private int version;

        public MockIndexerFactory(int version) {
            this.version = version;
        }

        @Override
        public CustomIndexer createIndexer() {
            return new CustomIndexer() {
                @Override
                protected void index(Iterable<? extends Indexable> files, Context context) {
                    System.out.println("Scanning " + files + " into " + context.getIndexFolder().getPath());
                }
            };
        }

        @Override
        public boolean supportsEmbeddedIndexers() {
            return false;
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
        }

        @Override
        public String getIndexerName() {
            return INDEXER_NAME;
        }

        @Override
        public int getIndexVersion() {
            return version;
        }
    }

    private static final class MockHandler extends Handler {

        private volatile CountDownLatch latch;
        private volatile List<URL> sources;

        private MockHandler() {
            reset();
        }

        public void reset() {
            latch = new CountDownLatch(1);
            sources = null;
        }

        public boolean await(long milis) throws InterruptedException {
            return latch.await(milis, TimeUnit.MILLISECONDS);
        }

        public List<? extends URL> getSources() {
            return sources;
        }

        @Override
        public void publish(LogRecord record) {
            String msg = record.getMessage();            
            if ("RootsWork-finished".equals(msg)) {
                latch.countDown();
            } else if ("scanSources".equals(msg)) {
                @SuppressWarnings("unchecked")
                List<URL> s =(List<URL>) record.getParameters()[0];
                sources = s;
            }
        }

        @Override
        public void flush() {            
        }

        @Override
        public void close() throws SecurityException {            
        }
    }
    //</editor-fold>

}
