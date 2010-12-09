package org.netbeans.libs.svnclientadapter.svnkit;

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

import java.util.Collection;
import org.netbeans.junit.NbTestCase;
import org.netbeans.libs.svnclientadapter.SvnClientAdapterFactory;
import org.openide.util.Lookup;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;

/**
 *
 * @author tomas
 */
public class SvnKitClientAdapterFactoryTest extends NbTestCase {

    public SvnKitClientAdapterFactoryTest(String name) {
        super(name);
    }

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    public void testIsAvailable() {
        SvnKitClientAdapterFactory f = getFactory();
        assertNotNull(f);
        assertTrue(f.isAvailable());
        ISVNClientAdapter c = f.createClient();
        assertNotNull(c);
    }

    public void testProvides() {
        SvnKitClientAdapterFactory f = getFactory();
        assertNotNull(f);
        assertEquals(SvnClientAdapterFactory.Client.SVNKIT, f.provides());
    }
    
    public void testGetFactory() {
        Collection<SvnClientAdapterFactory> cl = (Collection<SvnClientAdapterFactory>) Lookup.getDefault().lookupAll(SvnClientAdapterFactory.class);
        for (SvnClientAdapterFactory f : cl) {
            if(f.getClass() == SvnKitClientAdapterFactory.class) {
                return;
            }
        }
        fail("couldn't lookup SvnKitClientAdapterFactory");
    }

    private SvnKitClientAdapterFactory getFactory() {
        return new TestFactory();
    }

    private class TestFactory extends SvnKitClientAdapterFactory {
        @Override
        public Client provides() {
            return super.provides();
        }
        @Override
        public boolean isAvailable() {
            return super.isAvailable();
        }
    }
}