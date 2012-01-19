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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.versioning;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem.AtomicAction;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author tomas
 */
public class DeleteCreateTest extends NbTestCase {
    private File dataRootDir;

    public DeleteCreateTest(String arg0) {
        super(arg0);
    }

    @Override
    protected Level logLevel() {
        return Level.INFO;
    }   
    
    @Override
    protected void setUp() throws Exception {    
        dataRootDir = getWorkDir();
        dataRootDir.mkdirs();
        File userdir = new File(dataRootDir + "userdir");
        userdir.mkdirs();
        System.setProperty("netbeans.user", userdir.getAbsolutePath());
        
        FileObject fo = FileUtil.toFileObject(getWorkDir());
        MockServices.setServices(DeleteCreateTestAnnotationProvider.class);
        // interceptor init
        DeleteCreateTestAnnotationProvider.instance.init();
    }

    @Override
    protected void tearDown() throws Exception {        
        DeleteCreateTestAnnotationProvider.instance.reset();
    }

    public void testDeleteCreateFile() throws IOException {
        
        // non atomic delete and create
        File file1 = new File(dataRootDir, "file1");
        file1 = FileUtil.normalizeFile(file1);
        file1.createNewFile();
                
        final FileObject fo1 = FileUtil.toFileObject(file1);
        fo1.delete();
        fo1.getParent().createData(fo1.getName());             
        
        // get intercepted events 
        String[] nonAtomic = DeleteCreateTestAnnotationProvider.instance.events.toArray(new String[DeleteCreateTestAnnotationProvider.instance.events.size()]);
        DeleteCreateTestAnnotationProvider.instance.events.clear();
        
        // atomic delete and create
        File file2 = new File(dataRootDir, "file2");
        file2 = FileUtil.normalizeFile(file2);
        file2.createNewFile();
        
        final FileObject fo2 = FileUtil.toFileObject(file2);
        AtomicAction a = new AtomicAction() {
            public void run() throws IOException {             
                fo2.delete();
                fo2.getParent().createData(fo2.getName());
            }
        };
        fo2.getFileSystem().runAtomicAction(a);        
        // get intercepted events 
        String[] atomic = DeleteCreateTestAnnotationProvider.instance.events.toArray(new String[DeleteCreateTestAnnotationProvider.instance.events.size()]);
        
        Logger l = Logger.getLogger(DeleteCreateTest.class.getName());
        l.info("- atomic events ----------------------------------");
        for (String s : atomic) l.info(s);        
        l.info("- non atomic events ------------------------------");
        for (String s : nonAtomic) l.info(s);
        l.info("-------------------------------");
        
        // test
        assertEquals(atomic.length, nonAtomic.length);
        for (int i = 0; i < atomic.length; i++) {
            assertEquals(atomic[i], nonAtomic[i]);            
        }        
    }  

    public void testDeleteCreateFolder() throws IOException {

        // non atomic delete and create
        File file1 = new File(dataRootDir, "folder1");
        file1 = FileUtil.normalizeFile(file1);
        file1.mkdirs();

        final FileObject fo1 = FileUtil.toFileObject(file1);
        fo1.delete();
        fo1.getParent().createFolder(fo1.getName());

        // get intercepted events
        String[] nonAtomic = DeleteCreateTestAnnotationProvider.instance.events.toArray(new String[DeleteCreateTestAnnotationProvider.instance.events.size()]);
        DeleteCreateTestAnnotationProvider.instance.events.clear();

        // atomic delete and create
        File file2 = new File(dataRootDir, "folder2");
        file2 = FileUtil.normalizeFile(file2);
        file2.mkdirs();

        final FileObject fo2 = FileUtil.toFileObject(file2);
        AtomicAction a = new AtomicAction() {
            public void run() throws IOException {
                fo2.delete();
                fo2.getParent().createFolder(fo2.getName());
            }
        };
        fo2.getFileSystem().runAtomicAction(a);
        // get intercepted events
        String[] atomic = DeleteCreateTestAnnotationProvider.instance.events.toArray(new String[DeleteCreateTestAnnotationProvider.instance.events.size()]);

        Logger l = Logger.getLogger(DeleteCreateTest.class.getName());
        l.info("- atomic events ----------------------------------");
        for (String s : atomic) l.info(s);
        l.info("- non atomic events ------------------------------");
        for (String s : nonAtomic) l.info(s);
        l.info("-------------------------------");

        // test
        assertEquals(atomic.length, nonAtomic.length);
        for (int i = 0; i < atomic.length; i++) {
            assertEquals(atomic[i], nonAtomic[i]);
        }
    }

}
