/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.kenai.api;

import java.net.MalformedURLException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jan Becicka
 */
public class KenaiProjectTest {

    static String UNITTESTUNIQUENAME = "unittestuniquename01";

    public KenaiProjectTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            System.setProperty("kenai.com.url","http://testkenai.com");
//            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.home"), ".test-kenai")));
//            String username = br.readLine();
//            String password = br.readLine();
//            br.close();
//            Kenai.getDefault().login(username, password.toCharArray());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Test of forRepository method, of class KenaiProject.
     */
    @Test
    public void testForRepository1() throws Exception {
        System.out.println("forRepository1");
        String uri = "https://testkenai.com/svn/unittestuniquename01~source-code-repository";
        KenaiProject result = KenaiProject.forRepository(uri);
        assert result.getName().equals(UNITTESTUNIQUENAME);
    }
    /**
     * Test of forRepository method, of class KenaiProject.
     */
    @Test
    public void testForRepository2() throws Exception {
        System.out.println("forRepository2");
        String uri = "https://testkenai.com/hg/unittestuniquename01~source-code-repository2";
        KenaiProject result = KenaiProject.forRepository(uri);
        assert result.getName().equals(UNITTESTUNIQUENAME);
    }

    @Test
    public void testCheckName() throws KenaiException, MalformedURLException {
        assert KenaiProject.checkName("uniquenamebond007") == null;
        assert KenaiProject.checkName(UNITTESTUNIQUENAME).equals("foo");
    }
}