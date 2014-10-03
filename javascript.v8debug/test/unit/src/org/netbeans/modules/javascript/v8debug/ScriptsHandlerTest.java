/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javascript.v8debug;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.lib.v8debug.V8Script;

/**
 *
 * @author Martin Entlicher
 */
public class ScriptsHandlerTest {
    
    public ScriptsHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class ScriptsHandler.
     */
    @Test
    public void testAdd() {
        /* TODO
        System.out.println("add");
        V8Script script = null;
        ScriptsHandler instance = null;
        instance.add(script);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        */
    }

    /**
     * Test of getLocalPath method, of class ScriptsHandler.
     */
    @Test
    public void testGetLocalPath() throws Exception {
        System.out.println("getLocalPath");
        ScriptsHandler instance = new ScriptsHandler(null, null);
        assertEquals("", instance.getLocalPath(""));
        assertEquals("/a/b/c", instance.getLocalPath("/a/b/c"));
        
        instance = new ScriptsHandler("/home/test", "/var/server/path/");
        try {
            assertEquals("", instance.getLocalPath(""));
            fail("Did not throw OutOfScope exception.");
        } catch (ScriptsHandler.OutOfScope oos) {
        }
        assertEquals("/home/test", instance.getLocalPath("/var/server/path"));
        assertEquals("/home/test/file", instance.getLocalPath("/var/server/path/file"));
        assertEquals("/home/test/test/My\\File.js", instance.getLocalPath("/var/server/path/test/My\\File.js"));
        try {
            instance.getLocalPath("/var/server/path2");
            fail("Did not throw OutOfScope exception.");
        } catch (ScriptsHandler.OutOfScope oos) {
        }
        try {
            instance.getLocalPath("/var/server");
            fail("Did not throw OutOfScope exception.");
        } catch (ScriptsHandler.OutOfScope oos) {
        }
        
        instance = new ScriptsHandler("C:\\\\Users\\Test", "/var");
        assertEquals("C:\\\\Users\\Test", instance.getLocalPath("/var"));
        assertEquals("C:\\\\Users\\Test\\folder\\MyFile.js", instance.getLocalPath("/var/folder/MyFile.js"));
        
        instance = new ScriptsHandler("C:\\\\", "/var");
        assertEquals("C:\\\\", instance.getLocalPath("/var"));
        assertEquals("C:\\\\File.js", instance.getLocalPath("/var/File.js"));
        
        instance = new ScriptsHandler("C:\\\\", "/");
        assertEquals("C:\\\\", instance.getLocalPath("/"));
        assertEquals("C:\\\\File.js", instance.getLocalPath("/File.js"));
    }

    /**
     * Test of getServerPath method, of class ScriptsHandler.
     */
    @Test
    public void testGetServerPath() throws Exception {
        System.out.println("getServerPath");
        ScriptsHandler instance = new ScriptsHandler(null, null);
        assertEquals("/a/b/c", instance.getServerPath("/a/b/c"));
        
        instance = new ScriptsHandler("/home/test", "/var/server/path/");
        try {
            assertEquals("", instance.getServerPath(""));
            fail("Did not throw OutOfScope exception.");
        } catch (ScriptsHandler.OutOfScope oos) {
        }
        assertEquals("/var/server/path", instance.getServerPath("/home/test"));
        assertEquals("/var/server/path/file", instance.getServerPath("/home/test/file"));
        assertEquals("/var/server/path/test/My\\File.js", instance.getServerPath("/home/test/test/My\\File.js"));
        try {
            instance.getServerPath("/home/test2");
            fail("Did not throw OutOfScope exception.");
        } catch (ScriptsHandler.OutOfScope oos) {
        }
        try {
            instance.getServerPath("/home");
            fail("Did not throw OutOfScope exception.");
        } catch (ScriptsHandler.OutOfScope oos) {
        }
        
        instance = new ScriptsHandler("C:\\\\Users\\Test", "/var");
        assertEquals("/var", instance.getServerPath("C:\\\\Users\\Test"));
        assertEquals("/var/folder/MyFile.js", instance.getServerPath("C:\\\\Users\\Test\\folder\\MyFile.js"));
        
        instance = new ScriptsHandler("C:\\\\", "/var");
        assertEquals("/var", instance.getServerPath("C:\\\\"));
        assertEquals("/var/File.js", instance.getServerPath("C:\\\\File.js"));
        
        instance = new ScriptsHandler("C:\\\\", "/");
        assertEquals("/", instance.getServerPath("C:\\\\"));
        assertEquals("/File.js", instance.getServerPath("C:\\\\File.js"));
    }
    
}
