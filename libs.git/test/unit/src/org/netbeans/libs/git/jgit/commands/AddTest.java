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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.libs.git.jgit.commands;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.util.RawParseUtils;
import org.netbeans.libs.git.GitClient;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitStatus;
import org.netbeans.libs.git.GitStatus.Status;
import org.netbeans.libs.git.jgit.AbstractGitTestCase;
import org.netbeans.libs.git.jgit.Utils;

/**
 *
 * @author ondra
 */
public class AddTest extends AbstractGitTestCase {
    private Repository repository;
    private File workDir;

    public AddTest (String testName) throws IOException {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workDir = getWorkingDirectory();
        repository = getRepository(getLocalGitRepository());
    }

    public void testAddNoRoots () throws Exception {
        File file = new File(workDir, "file");
        file.createNewFile();
        assertNullDirCacheEntry(Collections.singleton(file));
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[0], m);
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        assertDirCacheSize(1);
        assertDirCacheEntry(Collections.singleton(file));
    }
    
    public void testAddFileToEmptyIndex () throws Exception {
        File file = new File(workDir, "file");
        file.createNewFile();

        assertNullDirCacheEntry(Collections.singleton(file));
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { file }, m);
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        assertDirCacheSize(1);
        assertDirCacheEntry(Collections.singleton(file));

        // no error while adding the same file twice
        m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { file }, m);
        assertEquals(Collections.<File>emptySet(), m.notifiedFiles);
        assertDirCacheEntry(Collections.singleton(file));

        write(file, "hello, i've changed");
        assertDirCacheEntryModified(Collections.singleton(file));
        m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { file }, m);
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        assertDirCacheSize(1);
        assertDirCacheEntry(Collections.singleton(file));
    }

    public void testAddFileToNonEmptyIndex () throws Exception {
        File file = new File(workDir, "file");
        file.createNewFile();
        File file2 = new File(workDir, "file2");
        file2.createNewFile();
        File file3 = new File(workDir, "file3");
        file3.createNewFile();

        assertNullDirCacheEntry(Arrays.asList(file, file2, file3));
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { file }, m);
        assertEquals(Collections.singleton(file), m.notifiedFiles);
        assertDirCacheSize(1);
        assertDirCacheEntry(Arrays.asList(file));
        assertNullDirCacheEntry(Arrays.asList(file2, file3));

        m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { file2 }, m);
        assertEquals(Collections.singleton(file2), m.notifiedFiles);
        assertDirCacheSize(2);
        assertDirCacheEntry(Arrays.asList(file, file2));
        assertNullDirCacheEntry(Arrays.asList(file3));

        m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { file, file2 }, m);
        assertEquals(Collections.<File>emptySet(), m.notifiedFiles);
        assertDirCacheSize(2);
        assertDirCacheEntry(Arrays.asList(file, file2));
        assertNullDirCacheEntry(Arrays.asList(file3));
    }

    public void testAddFolder () throws Exception {
        File file = new File(workDir, "file");
        write(file, "file");
        File folder1 = new File(workDir, "folder1");
        folder1.mkdirs();
        File file1_1 = new File(folder1, "file1");
        write(file1_1, "file1_1");
        File file1_2 = new File(folder1, "file2");
        write(file1_2, "file1_2");
        File subfolder1 = new File(folder1, "subfolder");
        subfolder1.mkdirs();
        File file1_1_1 = new File(subfolder1, "file1");
        write(file1_1_1, "file1_1_1");
        File file1_1_2 = new File(subfolder1, "file2");
        write(file1_1_2, "file1_1_2");

        File folder2 = new File(workDir, "folder2");
        folder2.mkdirs();
        File file2_1 = new File(folder2, "file1");
        write(file2_1, "file2_1");
        File file2_2 = new File(folder2, "file2");
        write(file2_2, "file2_2");
        File subfolder2 = new File(folder2, "subfolder");
        subfolder2.mkdirs();
        File file2_1_1 = new File(subfolder2, "file1");
        write(file2_1_1, "file2_1_1");
        File file2_1_2 = new File(subfolder2, "file2");
        write(file2_1_2, "file2_1_2");

        assertNullDirCacheEntry(Arrays.asList(file, file1_1, file1_2, file1_1_1, file1_1_2, file2_1, file2_2, file2_1_1, file2_1_2));
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { subfolder1 }, m);
        assertEquals(new HashSet<File>(Arrays.asList(file1_1_1, file1_1_2)), m.notifiedFiles);
        assertDirCacheSize(2);
        assertDirCacheEntry(Arrays.asList(file1_1_1, file1_1_2));
        assertNullDirCacheEntry(Arrays.asList(file, file1_1, file1_2, file2_1, file2_2, file2_1_1, file2_1_2));

        m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { folder1 }, m);
        assertEquals(new HashSet<File>(Arrays.asList(file1_1, file1_2)), m.notifiedFiles);
        assertDirCacheSize(4);
        assertDirCacheEntry(Arrays.asList(file1_1, file1_2, file1_1_1, file1_1_2));
        assertNullDirCacheEntry(Arrays.asList(file, file2_1, file2_1_1, file2_1_2));

        m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { folder2 }, m);
        assertEquals(new HashSet<File>(Arrays.asList(file2_1, file2_2, file2_1_1, file2_1_2)), m.notifiedFiles);
        assertDirCacheSize(8);
        assertDirCacheEntry(Arrays.asList(file1_1, file1_2, file1_1_1, file1_1_2, file2_1, file2_2, file2_1_1, file2_1_2));
    }

    public void testAddIgnored () throws Exception {
        File folder1 = new File(workDir, "folder1");
        folder1.mkdirs();
        File file1_1 = new File(folder1, "file1_1");
        write(file1_1, "file1_1");
        File file1_2 = new File(folder1, "file1_2");
        write(file1_2, "file1_2");

        File folder2 = new File(workDir, "folder2");
        folder2.mkdirs();
        File file2_1 = new File(folder2, "file2_1");
        write(file2_1, "file2_1");
        File file2_2 = new File(folder2, "file2_2");
        write(file2_2, "file2_2");

        write(new File(workDir, ".gitignore"), "file1_1\nfolder2");

        assertNullDirCacheEntry(Arrays.asList(file1_1, file2_1, file1_2, file2_2));
        GitClient client = getClient(workDir);
        Monitor m = new Monitor();
        client.addNotificationListener(m);
        client.add(new File[] { folder1, folder2 }, m);
        assertEquals(new HashSet<File>(Arrays.asList(file1_2)), m.notifiedFiles);
        assertDirCacheSize(1);
        assertDirCacheEntry(Arrays.asList(file1_2));
        assertNullDirCacheEntry(Arrays.asList(file1_1, file2_1, file2_2));
    }
    
    public void testAddIgnoreExecutable () throws Exception {
        if (isWindows()) {
            // no reason to test on windows
            return;
        }
        File f = new File(workDir, "f");
        write(f, "hi, i am executable");
        f.setExecutable(true);
        File[] roots = { f };
        GitClient client = getClient(workDir);
        StoredConfig config = repository.getConfig();
        config.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_FILEMODE, false);
        config.save();
        // add should not set executable bit in index
        add(roots);
        Map<File, GitStatus> statuses = client.getStatus(roots, NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, f, true, Status.STATUS_ADDED, Status.STATUS_NORMAL, Status.STATUS_ADDED, false);
        
        // index should differ from wt
        config.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_FILEMODE, true);
        config.save();
        statuses = client.getStatus(roots, NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, f, true, Status.STATUS_ADDED, Status.STATUS_MODIFIED, Status.STATUS_ADDED, false);
    }
    
    public void testUpdateIndexIgnoreExecutable () throws Exception {
        if (isWindows()) {
            // no reason to test on windows
            return;
        }
        File f = new File(workDir, "f");
        write(f, "hi, i am not executable");
        File[] roots = { f };
        add(roots);
        commit(roots);
        f.setExecutable(true);
        GitClient client = getClient(workDir);
        StoredConfig config = repository.getConfig();
        config.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_FILEMODE, false);
        config.save();
        write(f, "hi, i am executable");
        // add should not set executable bit in index
        add(roots);
        Map<File, GitStatus> statuses = client.getStatus(roots, NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, f, true, Status.STATUS_MODIFIED, Status.STATUS_NORMAL, Status.STATUS_MODIFIED, false);
        
        // index should differ from wt
        config.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_FILEMODE, true);
        config.save();
        statuses = client.getStatus(roots, NULL_PROGRESS_MONITOR);
        assertStatus(statuses, workDir, f, true, Status.STATUS_MODIFIED, Status.STATUS_MODIFIED, Status.STATUS_MODIFIED, false);
    }

    public void testCancel () throws Exception {
        final File file = new File(workDir, "file");
        file.createNewFile();
        final File file2 = new File(workDir, "file2");
        file2.createNewFile();

        final Monitor m = new Monitor();
        final GitClient client = getClient(workDir);
        final Exception[] exs = new Exception[1];
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.addNotificationListener(m);
                    client.add(new File[] { file, file2 },m);
                } catch (GitException ex) {
                    exs[0] = ex;
                }
            }
        });
        m.cont = false;
        t1.start();
        m.waitAtBarrier();
        m.cancel();
        m.cont = true;
        t1.join();
        assertTrue(m.isCanceled());
        assertEquals(1, m.count);
        assertEquals(null, exs[0]);
    }
    
    public void testAddNested () throws Exception {
        File f = new File(workDir, "f");
        write(f, "file");
        
        GitClient client = getClient(workDir);
        client.add(new File[] { f }, NULL_PROGRESS_MONITOR);
        client.commit(new File[] { f }, "init commit", null, null, NULL_PROGRESS_MONITOR);
        
        Thread.sleep(1100);
        File nested = new File(workDir, "nested");
        nested.mkdirs();
        File f2 = new File(nested, "f");
        write(f2, "file");
        GitClient clientNested = getClient(nested);
        clientNested.init(NULL_PROGRESS_MONITOR);
        clientNested.add(new File[] { f2 }, NULL_PROGRESS_MONITOR);
        clientNested.commit(new File[] { f2 }, "aaa", null, null, NULL_PROGRESS_MONITOR);
        write(f2, "change");
        
        Thread.sleep(1000);
        client.add(new File[] { workDir }, NULL_PROGRESS_MONITOR);
        Map<File, GitStatus> statuses = client.getStatus(new File[] { workDir }, NULL_PROGRESS_MONITOR);
        assertEquals(2, statuses.size());
        assertStatus(statuses, workDir, f, true, Status.STATUS_NORMAL, Status.STATUS_NORMAL, Status.STATUS_NORMAL, false);
        // nested should be added as gitlink
        assertStatus(statuses, workDir, nested, true, Status.STATUS_ADDED, Status.STATUS_NORMAL, Status.STATUS_ADDED, false);
        DirCacheEntry e = repository.readDirCache().getEntry("nested");
        assertEquals(FileMode.GITLINK, e.getFileMode());
        assertEquals(nested.length(), e.getLength());
        assertNotSame(ObjectId.zeroId().name(), e.getObjectId().getName());
        
        statuses = clientNested.getStatus(new File[] { nested }, NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, nested, f2, true, Status.STATUS_NORMAL, Status.STATUS_MODIFIED, Status.STATUS_MODIFIED, false);
    }
    
    public void testAddMixedLineEndings () throws Exception {
        File f = new File(workDir, "f");
        String content = "";
        for (int i = 0; i < 10000; ++i) {
            content += i + "\r\n";
        }
        write(f, content);
        File[] files = new File[] { f };
        GitClient client = getClient(workDir);
        client.add(files, NULL_PROGRESS_MONITOR);
        client.commit(files, "commit", null, null, NULL_PROGRESS_MONITOR);
        
        Map<File, GitStatus> statuses = client.getStatus(files, NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, f, true, Status.STATUS_NORMAL, Status.STATUS_NORMAL, Status.STATUS_NORMAL, false);
        
        // lets turn autocrlf on
        StoredConfig cfg = repository.getConfig();
        cfg.setString(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, "true");
        cfg.save();
        
        // when this starts failing, remove the work around
        ObjectInserter inserter = repository.newObjectInserter();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.setFilter(PathFilterGroup.createFromStrings("f"));
        treeWalk.setRecursive(true);
        treeWalk.reset();
        treeWalk.addTree(new FileTreeIterator(repository));
        while (treeWalk.next()) {
            String path = treeWalk.getPathString();
            assertEquals("f", path);
            WorkingTreeIterator fit = treeWalk.getTree(0, WorkingTreeIterator.class);
            InputStream in = fit.openEntryStream();
            try {
                inserter.insert(Constants.OBJ_BLOB, fit.getEntryLength(), in);
                fail("this should fail, remove the work around");
            } catch (EOFException ex) {
                assertEquals("Input did not match supplied length. 10000 bytes are missing.", ex.getMessage());
            } finally {
                in.close();
                inserter.release();
            }
            break;
        }
        
        // no err should occur
        write(f, content + "hello");
        statuses = client.getStatus(files, NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, f, true, Status.STATUS_NORMAL, Status.STATUS_MODIFIED, Status.STATUS_MODIFIED, false);
        client.add(files, NULL_PROGRESS_MONITOR);
        statuses = client.getStatus(files, NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, f, true, Status.STATUS_MODIFIED, Status.STATUS_NORMAL, Status.STATUS_MODIFIED, false);
        client.commit(files, "message", null, null, NULL_PROGRESS_MONITOR);
        statuses = client.getStatus(files, NULL_PROGRESS_MONITOR);
        assertEquals(1, statuses.size());
        assertStatus(statuses, workDir, f, true, Status.STATUS_NORMAL, Status.STATUS_NORMAL, Status.STATUS_NORMAL, false);
    }
    
    public void testLineEndingsWindows () throws Exception {
        if (!isWindows()) {
            return;
        }
        // lets turn autocrlf on
        StoredConfig cfg = repository.getConfig();
        cfg.setString(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, "true");
        cfg.save();
        
        File f = new File(workDir, "f");
        write(f, "a\r\nb\r\n");
        File[] roots = new File[] { f };
        
        GitClient client = getClient(workDir);
        runExternally(workDir, Arrays.asList("git.cmd", "add", "f"));
        DirCacheEntry e1 = repository.readDirCache().getEntry("f");
        client.add(roots, NULL_PROGRESS_MONITOR);
        DirCacheEntry e2 = repository.readDirCache().getEntry("f");
        assertStatus(client.getStatus(roots, NULL_PROGRESS_MONITOR),
                workDir, f, true, Status.STATUS_ADDED, Status.STATUS_NORMAL, Status.STATUS_ADDED, false);
        List<String> res = runExternally(workDir, Arrays.asList("git.cmd", "status", "-s"));
        assertEquals(Arrays.asList("A  f"), res);
        assertEquals(e1.getFileMode(), e2.getFileMode());
        assertEquals(e1.getPathString(), e2.getPathString());
        assertEquals(e1.getRawMode(), e2.getRawMode());
        assertEquals(e1.getStage(), e2.getStage());
        assertEquals(e1.getLength(), e2.getLength());
        assertEquals(e1.getObjectId(), e2.getObjectId());

        write(f, "a\nb\n");
        res = runExternally(workDir, Arrays.asList("git.cmd", "status", "-s"));
        assertEquals(Arrays.asList("AM f"), res);
        assertStatus(client.getStatus(roots, NULL_PROGRESS_MONITOR),
                workDir, f, true, Status.STATUS_ADDED, Status.STATUS_MODIFIED, Status.STATUS_ADDED, false);
        
        res = runExternally(workDir, Arrays.asList("git.cmd", "commit", "-m", "gugu"));
        res = runExternally(workDir, Arrays.asList("git.cmd", "checkout", "--", "f"));
        
        RevCommit commit = Utils.findCommit(repository, "HEAD");
        TreeWalk walk = new TreeWalk(repository);
        walk.reset();
        walk.addTree(commit.getTree());
        walk.setFilter(PathFilter.create("f"));
        walk.setRecursive(true);
        walk.next();
        assertEquals("f", walk.getPathString());
        ObjectLoader loader = repository.getObjectDatabase().open(walk.getObjectId(0));
        assertEquals(4, loader.getSize());
        assertEquals("a\nb\n", new String(loader.getBytes()));
        assertEquals(e1.getObjectId(), walk.getObjectId(0));
        
        res = runExternally(workDir, Arrays.asList("git.cmd", "status", "-s"));
        assertEquals(0, res.size());
        assertStatus(client.getStatus(roots, NULL_PROGRESS_MONITOR),
                workDir, f, true, Status.STATUS_NORMAL, Status.STATUS_NORMAL, Status.STATUS_NORMAL, false);
    }
    
    public void testAddSymlink () throws Exception {
        if (isWindows()) {
            return;
        }
        String path = "folder/file";
        File f = new File(workDir, path);
        f.getParentFile().mkdir();
        write(f, "file");
        add(f);
        commit(f);
        
        Thread.sleep(1100);
        
        // try with commandline client
        File link = new File(workDir, "link");
        runExternally(workDir, Arrays.asList("ln", "-s", path, link.getName()));
        long ts = Files.readAttributes(Paths.get(link.getAbsolutePath()), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).lastModifiedTime().toMillis();
        runExternally(workDir, Arrays.asList("git", "add", link.getName()));
        DirCacheEntry e = repository.readDirCache().getEntry(link.getName());
        assertEquals(FileMode.SYMLINK, e.getFileMode());
        ObjectId id = e.getObjectId();
        assertEquals(ts, e.getLastModified() / 1000 * 1000);
        ObjectReader reader = repository.getObjectDatabase().newReader();
        assertTrue(reader.has(e.getObjectId()));
        byte[] bytes = reader.open(e.getObjectId()).getBytes();
        assertEquals(path, RawParseUtils.decode(bytes));
        
        // now with internal
        File link2 = new File(workDir, "link2");
        Files.createSymbolicLink(Paths.get(link2.getAbsolutePath()), Paths.get(path));
        ts = Files.readAttributes(Paths.get(link2.getAbsolutePath()), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).lastModifiedTime().toMillis();
        getClient(workDir).add(new File[] { link2 }, NULL_PROGRESS_MONITOR);
        
        DirCacheEntry e2 = repository.readDirCache().getEntry(link2.getName());
        assertEquals(FileMode.SYMLINK, e2.getFileMode());
        assertEquals(id, e2.getObjectId());
        assertEquals(0, e2.getLength());
        assertEquals(ts, e2.getLastModified() / 1000 * 1000);
        assertTrue(reader.has(e2.getObjectId()));
        bytes = reader.open(e2.getObjectId()).getBytes();
        assertEquals(path, RawParseUtils.decode(bytes));
        reader.release();
    }
    
    public void testAddMissingSymlink () throws Exception {
        if (isWindows()) {
            return;
        }
        String path = "folder/file";
        File f = new File(workDir, path);
        
        // try with commandline client
        File link = new File(workDir, "link");
        Files.createSymbolicLink(Paths.get(link.getAbsolutePath()), Paths.get(path));
        getClient(workDir).add(new File[] { link }, NULL_PROGRESS_MONITOR);
        DirCacheEntry e = repository.readDirCache().getEntry(link.getName());
        assertEquals(FileMode.SYMLINK, e.getFileMode());
        assertEquals(0, e.getLength());
        ObjectReader reader = repository.getObjectDatabase().newReader();
        assertTrue(reader.has(e.getObjectId()));
        byte[] bytes = reader.open(e.getObjectId()).getBytes();
        assertEquals(path, RawParseUtils.decode(bytes));
        reader.release();
    }

    private void assertDirCacheEntry (Collection<File> files) throws IOException {
        DirCache cache = repository.lockDirCache();
        for (File f : files) {
            String relativePath = Utils.getRelativePath(workDir, f);
            DirCacheEntry e = cache.getEntry(relativePath);
            assertNotNull(e);
            assertEquals(relativePath, e.getPathString());
            assertEquals(f.lastModified(), e.getLastModified());
            InputStream in = new FileInputStream(f);
            try {
                assertEquals(e.getObjectId(), repository.newObjectInserter().idFor(Constants.OBJ_BLOB, f.length(), in));
            } finally {
                in.close();
            }
            if (e.getLength() == 0 && f.length() != 0) {
                assertTrue(e.isSmudged());
            } else {
                assertEquals(f.length(), e.getLength());
            }
        }
        cache.unlock();
    }

    private void assertDirCacheEntryModified (Collection<File> files) throws IOException {
        DirCache cache = repository.lockDirCache();
        for (File f : files) {
            String relativePath = Utils.getRelativePath(workDir, f);
            DirCacheEntry e = cache.getEntry(relativePath);
            assertNotNull(e);
            assertEquals(relativePath, e.getPathString());
            InputStream in = new FileInputStream(f);
            try {
                assertNotSame(e.getObjectId(), repository.newObjectInserter().idFor(Constants.OBJ_BLOB, f.length(), in));
            } finally {
                in.close();
            }
        }
        cache.unlock();
    }

    private void assertNullDirCacheEntry (Collection<File> files) throws Exception {
        DirCache cache = repository.lockDirCache();
        for (File f : files) {
            DirCacheEntry e = cache.getEntry(Utils.getRelativePath(workDir, f));
            assertNull(e);
        }
        cache.unlock();
    }

    private void assertDirCacheSize (int expectedSize) throws IOException {
        DirCache cache = repository.lockDirCache();
        try {
            assertEquals(expectedSize, cache.getEntryCount());
        } finally {
            cache.unlock();
        }
    }
}
