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
package org.netbeans.modules.j2ee.weblogic9.j2ee;

import java.util.Map;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.j2ee.deployment.common.api.Version;
import org.netbeans.spi.project.libraries.LibraryImplementation3;

/**
 *
 * @author Petr Hejl
 */
public class JerseyLibraryHelperTest extends NbTestCase {

    public JerseyLibraryHelperTest(String name) {
        super(name);
    }

    public void testMavenDepsWL1211() {
        LibraryImplementation3 impl = JerseyLibraryHelper.getJerseyInMemoryLibrary(
                Version.fromDottedNotationWithFallback("12.1.1"), null);
        assertNotNull(impl);
        Map<String, String> props = impl.getProperties();
        String deps = props.get("maven-dependencies");
        assertMaven12Deps(deps);
    }

    public void testMavenDepsWL120() {
        LibraryImplementation3 impl = JerseyLibraryHelper.getJerseyInMemoryLibrary(
                Version.fromDottedNotationWithFallback("12.0"), null);
        assertNotNull(impl);
        Map<String, String> props = impl.getProperties();
        String deps = props.get("maven-dependencies");
        assertMaven12Deps(deps);
    }

    public void testMavenDepsWL12() {
        LibraryImplementation3 impl = JerseyLibraryHelper.getJerseyInMemoryLibrary(
                Version.fromDottedNotationWithFallback("12"), null);
        assertNotNull(impl);
        Map<String, String> props = impl.getProperties();
        String deps = props.get("maven-dependencies");
        assertMaven12Deps(deps);
    }

    public void testMavenDepsWL10() {
        LibraryImplementation3 impl = JerseyLibraryHelper.getJerseyInMemoryLibrary(
                Version.fromDottedNotationWithFallback("10"), null);
        assertNull(impl);
    }

    private static void assertMaven12Deps(String deps) {
        assertTrue(deps.contains("com.sun.jersey:jersey-client:1.9:jar"));
        assertTrue(deps.contains("com.sun.jersey:jersey-json:1.9:jar"));
        assertTrue(deps.contains("com.sun.jersey.contribs:jersey-multipart:1.9:jar"));
        assertTrue(deps.contains("com.sun.jersey:jersey-server:1.9:jar"));
        assertTrue(deps.contains("org.codehaus.jackson:jackson-core-asl:1.8.3:jar"));
        assertTrue(deps.contains("org.codehaus.jackson:jackson-jaxrs:1.8.3:jar"));
        assertTrue(deps.contains("org.codehaus.jackson:jackson-mapper-asl:1.8.3:jar"));
        assertTrue(deps.contains("org.codehaus.jackson:jackson-xc:1.8.3:jar"));
        assertTrue(deps.contains("org.codehaus.jettison:jettison:1.1:jar"));
    }
}