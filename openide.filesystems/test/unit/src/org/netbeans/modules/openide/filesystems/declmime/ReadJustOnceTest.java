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
package org.netbeans.modules.openide.filesystems.declmime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MIMEResolver;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.test.MockLookup;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class ReadJustOnceTest extends NbTestCase {
    public ReadJustOnceTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        URL u = this.getClass().getResource ("code-fs.xml");        
        FileSystem fs = new XMLFileSystem(u);
        FileObject resolversRoot = fs.getRoot().getFileObject("root");
        resolversRoot.refresh();
        
        FileObject fos[] = resolversRoot.getChildren();
        List<MIMEResolver> resolvers = new ArrayList<MIMEResolver>();
        for (int i = 0; i<fos.length; i++) {
            resolvers.add(createResolver(fos[i]));
        }
        
        MockLookup.setInstances(resolvers.toArray());
    }

    protected MIMEResolver createResolver(FileObject fo) throws Exception {
        if (fo == null) {
            throw new NullPointerException();
        }
        return MIMEResolverImpl.forDescriptor(fo);
    }
    
    public void testHowManyReads() {
        final AtomicInteger cnt = new AtomicInteger();
        LocalFileSystem lfs = new LocalFileSystem() {
            @Override
            protected String[] children(String name) {
                return new String[] { "data.txt" };
            }

            @Override
            protected boolean folder(String name) {
                return !name.equals("data.txt");
            }
            
            @Override
            protected InputStream inputStream(String name) throws FileNotFoundException {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        cnt.incrementAndGet();
                        throw new IOException();
                    }
                };
            }
        };
        FileObject fo = lfs.findResource("data.txt");
        assertNotNull("File object found", fo);
        assertEquals("No mime type", "content/unknown", fo.getMIMEType());
        assertEquals("One query for input stream", 1, cnt.intValue());
    }
}
