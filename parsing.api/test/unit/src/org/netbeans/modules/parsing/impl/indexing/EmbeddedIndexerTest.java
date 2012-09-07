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
package org.netbeans.modules.parsing.impl.indexing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizer;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.test.TestFileUtils;

/**
 *
 * @author Tomas Zezula
 */
public class EmbeddedIndexerTest extends NbTestCase {

    private static final String EXT_TOP = "top";                //NOI18N
    private static final String MIME_TOP = "text/x-top";        //NOI18N
    private static final String MIME_INNER = "text/x-inner";    //NOI18N
    private static final String PATH_TOP_SOURCES = "top-src";   //NOI18N

    private static final int TIME = Integer.getInteger("RepositoryUpdaterTest.timeout", 5000);                 //NOI18N

    private FileObject srcRoot;
    private FileObject srcFile;
    private ClassPath srcCp;
    private final Map<String, Map<ClassPath,Void>> registeredClasspaths = new HashMap<String, Map<ClassPath,Void>>();

    public EmbeddedIndexerTest(@NonNull final String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.clearWorkDir();
        final File _wd = this.getWorkDir();
        final FileObject wd = FileUtil.toFileObject(_wd);
        final FileObject cache = wd.createFolder("cache");  //NOI18N
        CacheFolder.setCacheFolder(cache);
        MockMimeLookup.setInstances(
                MimePath.get(MIME_TOP),
                new TopParser.Factory(),
                new TopToInnerEmbProvider.Factory(),
                new TopIndexer.Factory());
        MockMimeLookup.setInstances(
                MimePath.get(MIME_INNER),
                new InnerParser.Factory(),
                new InnerIndexer.Factory());
        MockServices.setServices(
                TopPathRecognizer.class,
                ClassPathProviderImpl.class);
        srcRoot = wd.createFolder("src");   //NOI18N
        srcFile = FileUtil.toFileObject(
            TestFileUtils.writeFile(
                new File(FileUtil.toFile(srcRoot), "source.top"),   //NOI18N
                "   <A>   <B>   < A> < >"));                        //NOI18N
        FileUtil.setMIMEType(EXT_TOP, MIME_TOP);
        RepositoryUpdaterTest.setMimeTypes(MIME_TOP, MIME_INNER);
        ClassPathProviderImpl.root = srcRoot;
        RepositoryUpdaterTest.waitForRepositoryUpdaterInit();
    }

    @Override
    protected void tearDown() throws Exception {
        for(String id : registeredClasspaths.keySet()) {
            final Map<ClassPath,Void> classpaths = registeredClasspaths.get(id);
            GlobalPathRegistry.getDefault().unregister(id, classpaths.keySet().toArray(new ClassPath[classpaths.size()]));
        }

        super.tearDown();
    }

    public void testEmbeddingIndexer() throws Exception {
        RepositoryUpdater ru = RepositoryUpdater.getDefault();
        assertEquals(0, ru.getScannedBinaries().size());
        assertEquals(0, ru.getScannedBinaries().size());
        assertEquals(0, ru.getScannedUnknowns().size());

        final RepositoryUpdaterTest.TestHandler handler = new RepositoryUpdaterTest.TestHandler();
        final Logger logger = Logger.getLogger(RepositoryUpdater.class.getName()+".tests");
        logger.setLevel (Level.FINEST);
        logger.addHandler(handler);

        srcCp = ClassPath.getClassPath(srcRoot, PATH_TOP_SOURCES);
        assertNotNull(srcCp);
        globalPathRegistry_register(PATH_TOP_SOURCES, srcCp);
        assertTrue (handler.await());
        assertEquals(0, handler.getBinaries().size());
        assertEquals(1, handler.getSources().size());
        assertEquals(srcRoot.toURL(), handler.getSources().get(0));

        QuerySupport sup = QuerySupport.forRoots(TopIndexer.NAME, TopIndexer.VERSION, srcRoot);
        Collection<? extends IndexResult> res = sup.query("_sn", srcFile.getNameExt(), QuerySupport.Kind.EXACT, (String[]) null);
        assertEquals(1,res.size());
        assertEquals(Boolean.TRUE.toString(), res.iterator().next().getValue("valid")); //NOI18N

        sup = QuerySupport.forRoots(InnerIndexer.NAME, InnerIndexer.VERSION, srcRoot);
        res = sup.query("_sn", srcFile.getNameExt(), QuerySupport.Kind.EXACT, (String[]) null);
        assertEquals(4,res.size());
        Map<? extends Integer,? extends Integer> count = countModes(res);
        assertEquals(Integer.valueOf(1), count.get(0));
        assertEquals(Integer.valueOf(2), count.get(1));
        assertEquals(Integer.valueOf(1), count.get(2));
    }

    private static Map<? extends Integer, ? extends Integer> countModes(@NonNull final Collection<? extends IndexResult> docs)  {
        final Map<Integer,Integer> res = new HashMap<Integer, Integer>();
        for (IndexResult doc : docs) {
            final String value = doc.getValue("mode");  //NOI18N
            if (value != null) {
                try {
                    Integer count = res.get(Integer.parseInt(value));
                    count = count == null ? 1 : count + 1;
                    res.put(Integer.parseInt(value), count);
                } catch (NumberFormatException e) {}
            }
        }
        return res;
    }

    private void globalPathRegistry_register(String id, ClassPath... classpaths) {
        Map<ClassPath,Void> map = registeredClasspaths.get(id);
        if (map == null) {
            map = new IdentityHashMap<ClassPath, Void>();
            registeredClasspaths.put(id, map);
        }
        for (ClassPath cp :  classpaths) {
            map.put(cp,null);
        }
        GlobalPathRegistry.getDefault().register(id, classpaths);
    }

    private void globalPathRegistry_unregister(String id, ClassPath... classpaths) {
        GlobalPathRegistry.getDefault().unregister(id, classpaths);
        final Map<ClassPath,Void> map = registeredClasspaths.get(id);
        if (map != null) {
            map.keySet().removeAll(Arrays.asList(classpaths));
        }
    }

    public static class TopParser extends Parser {

        private TopResult resultCache;

        @Override
        public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
            final CharSequence toParse = snapshot.getText();
            boolean valid = true;
            int embStart = -1;
            for (int i=0; i< toParse.length(); i++) {
                if (toParse.charAt(i) == '<') {         //NOI18N
                    if (embStart != -1) {
                        valid = false;
                        break;
                    }
                    embStart = i+1;
                } else if (toParse.charAt(i) == '>') {  //NOI18N
                    if (embStart == -1 || embStart == i) {
                        valid = false;
                        break;
                    }
                    embStart = -1;
                }
            }
            resultCache = new TopResult(snapshot, valid);
        }

        @Override
        public Result getResult(Task task) throws ParseException {
            assert resultCache != null;
            return resultCache;
        }

        @Override
        public void addChangeListener(ChangeListener changeListener) {
        }

        @Override
        public void removeChangeListener(ChangeListener changeListener) {
        }

        static class TopResult extends Result {

            private final boolean valid;

            TopResult(
                @NonNull final Snapshot snapshot,
                final boolean valid) {
                super(snapshot);
                this.valid = valid;
            }

            boolean isValid() {
                return valid;
            }

            @Override
            protected void invalidate() {
            }
        }

        public static class Factory extends ParserFactory {

            @Override
            public Parser createParser(Collection<Snapshot> snapshots) {
                return new TopParser();
            }

        }

    }
    
    public static class InnerParser extends Parser {
        
        private InnerResult resultCache;

        @Override
        public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
            final InnerResult res = new InnerResult(snapshot);
            final CharSequence text = snapshot.getText();
            for (int i=0; i< text.length(); i++) {
                if (text.charAt(i) == 'A') {
                    res.setMode(InnerResult.A);
                    break;
                } else if (text.charAt(i) == 'B') {
                    res.setMode(InnerResult.B);
                    break;
                }
            }
            resultCache = res;
        }

        @Override
        public Result getResult(Task task) throws ParseException {
            assert resultCache != null;
            return resultCache;
        }

        @Override
        public void addChangeListener(ChangeListener changeListener) {
        }

        @Override
        public void removeChangeListener(ChangeListener changeListener) {
        }
        
        static class InnerResult extends Parser.Result {
            
            static final int UNKNOWN = 0;
            static final int A = 1;
            static final int B = 2;
            static final int C = 3;
            
            private int mode;
            
            InnerResult(@NonNull final Snapshot snapshot) {
                super(snapshot);
                mode = UNKNOWN;
            }

            @Override
            protected void invalidate() {
            }
            
            void setMode(int mode) {
                this.mode = mode;
            }
            
            int getMode() {
                return mode;
            }
        }
        
        public static class Factory extends ParserFactory {
            @Override
            public Parser createParser(Collection<Snapshot> snapshots) {
                return new InnerParser();
            }            
        }
        
    }

    public static class TopToInnerEmbProvider extends EmbeddingProvider {


        @Override
        public List<Embedding> getEmbeddings(@NonNull final Snapshot snapshot) {
            final List<Embedding> embs = new ArrayList<Embedding>();
            final CharSequence toParse = snapshot.getText();
            int embStart = -1;
            for (int i=0; i< toParse.length(); i++) {
                if (toParse.charAt(i) == '<') {         //NOI18N
                    embStart = i+1;
                } else if (toParse.charAt(i) == '>') {  //NOI18N
                    embs.add(snapshot.create(embStart, i-embStart, MIME_INNER));
                }
            }
            return embs;
        }

        @Override
        public int getPriority() {
            return 10;
        }

        @Override
        public void cancel() {
        }

        public static class Factory extends TaskFactory {
            @Override
            public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
                return Collections.<SchedulerTask>singleton(new TopToInnerEmbProvider());
            }

        }
    }

    public static class TopIndexer extends EmbeddingIndexer {

        public static final String NAME = "top";
        public static final int VERSION = 1;

        @Override
        protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
            try {
                final IndexingSupport support = IndexingSupport.getInstance(context);
                support.removeDocuments(indexable);
                final IndexDocument doc = support.createDocument(indexable);
                doc.addPair(
                    "valid",    //NOI18N
                    ((TopParser.TopResult)parserResult).isValid() ? Boolean.TRUE.toString() : Boolean.FALSE.toString(),
                    false,
                    true);
                support.addDocument(doc);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        public static class Factory extends EmbeddingIndexerFactory {

            @Override
            public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
                return new TopIndexer();
            }

            @Override
            public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            }

            @Override
            public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            }

            @Override
            public String getIndexerName() {
                return NAME;
            }

            @Override
            public int getIndexVersion() {
                return VERSION;
            }
        }
    }

    public static class InnerIndexer extends EmbeddingIndexer {

        public static final String NAME = "inner"; //NOI18N
        public static final int VERSION = 1;

        private final Factory f;

        private InnerIndexer(@NonNull final Factory f) {
            this.f = f;
        }

        @Override
        protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
            try {
                final IndexingSupport support = IndexingSupport.getInstance(context);
                final InnerParser.InnerResult ir = (InnerParser.InnerResult) parserResult;
                if (!indexable.equals(f.lastIndexable)) {
                    support.removeDocuments(indexable);
                }
                f.lastIndexable = indexable;
                final IndexDocument doc = support.createDocument(indexable);
                doc.addPair("mode", Integer.toString(ir.getMode()), true, true);    //NOI18N
                support.addDocument(doc);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        public static class Factory extends EmbeddingIndexerFactory {

            Indexable lastIndexable;

            @Override
            public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
                return new InnerIndexer(this);
            }

            @Override
            public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            }

            @Override
            public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            }

            @Override
            public String getIndexerName() {
                return NAME;
            }

            @Override
            public int getIndexVersion() {
                return VERSION;
            }

        }
    }

    public static class TopPathRecognizer extends PathRecognizer {

        @Override
        public Set<String> getSourcePathIds() {
            return Collections.<String>singleton(PATH_TOP_SOURCES);
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
            return Collections.<String>singleton(MIME_TOP);
        }
    }

    public static class ClassPathProviderImpl implements ClassPathProvider {

        static volatile FileObject root;

        private final AtomicReference<ClassPath> cpRef = new AtomicReference<ClassPath>();


        @Override
        public ClassPath findClassPath(FileObject file, String type) {
            if (PATH_TOP_SOURCES.equals(type) &&
               (FileUtil.isParentOf(root, file) || root.equals(file))) {
               ClassPath cp = cpRef.get();
               if (cp == null) {
                   cp = ClassPathSupport.createClassPath(root);
                   if (!cpRef.compareAndSet(null, cp)) {
                       cp = cpRef.get();
                   }
               }
               assert cp != null;
               return cp;
            }
            return null;
        }

    }

    private static final class AwaitWork extends RepositoryUpdater.Work {

        private final CountDownLatch latch;

        private AwaitWork(final CountDownLatch latch) {
            super(false,false,false,false,SuspendSupport.NOP,null);
            assert latch != null;
            this.latch = latch;
        }

        @Override
        protected boolean getDone() {
            latch.countDown();
            return true;
        }

    }
}
