/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.preprocessorbridge.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.event.ChangeListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.preprocessorbridge.spi.CompileOnSaveAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.BaseUtilities;
import org.openide.util.ChangeSupport;
import org.openide.util.Parameters;
import org.openide.util.test.MockChangeListener;
import org.openide.util.test.MockLookup;

/**
 *
 * @author Tomas Zezula
 */
public class CompileOnSaveActionQueryTest extends NbTestCase {
    
    private URL nonSrcUrl;
    private URL srcUrl1, srcUrl2;
    private ActionImpl impl1, impl2, impl3;
    
    public CompileOnSaveActionQueryTest(@NonNull final String name) {
        super(name);
    }
    
    @Before
    @Override
    public void setUp() throws IOException {
        clearWorkDir();
        final File wd = getWorkDir();
        final FileObject src1 = FileUtil.createFolder(FileUtil.normalizeFile(
                new File(wd,"src"))); //NOI18N
        final FileObject src2 = FileUtil.createFolder(FileUtil.normalizeFile(
                new File(wd,"src2"))); //NOI18N
        nonSrcUrl = BaseUtilities.toURI(wd).toURL();
        srcUrl1 = src1.toURL();
        srcUrl2 = src2.toURL();
        impl1 = new ActionImpl();
        impl2 = new ActionImpl();
        impl3 = new ActionImpl();
        MockLookup.setInstances(
            new ProviderImpl(srcUrl1, impl1),
            new ProviderImpl(srcUrl2, impl2),
            new ProviderImpl(srcUrl1, impl3));
        //Enable all
        impl1.setEnabled(true);
        impl1.setUpdateClasses(true);
        impl1.setUpdateResources(true);
        impl2.setEnabled(true);
        impl2.setUpdateClasses(true);
        impl2.setUpdateResources(true);
        impl3.setEnabled(true);
        impl3.setUpdateClasses(true);
        impl3.setUpdateResources(true);
    }
    
    @After
    @Override
    public void tearDown() {
    }

    public void testQuery() throws IOException {
        CompileOnSaveAction a = CompileOnSaveActionQuery.getAction(nonSrcUrl);
        assertNull(a);
        assertEquals(0, impl1.getInvocationCountAndReset());
        assertEquals(0, impl2.getInvocationCountAndReset());
        assertEquals(0, impl3.getInvocationCountAndReset());
        a = CompileOnSaveActionQuery.getAction(srcUrl1);
        assertNotNull(a);
        CompileOnSaveAction.Context ctx = CompileOnSaveAction.Context.clean(srcUrl1);
        a.performAction(ctx);
        assertEquals(1, impl1.getInvocationCountAndReset());
        assertEquals(0, impl2.getInvocationCountAndReset());
        assertEquals(0, impl3.getInvocationCountAndReset());
        a = CompileOnSaveActionQuery.getAction(srcUrl2);
        assertNotNull(a);
        ctx = CompileOnSaveAction.Context.clean(srcUrl2);
        a.performAction(ctx);
        assertEquals(0, impl1.getInvocationCountAndReset());
        assertEquals(1, impl2.getInvocationCountAndReset());
        assertEquals(0, impl3.getInvocationCountAndReset());        
    }
    
    
    public void testQueryChanges() throws IOException {        
        CompileOnSaveAction a = CompileOnSaveActionQuery.getAction(srcUrl1);
        assertNotNull(a);
        CompileOnSaveAction.Context ctx = CompileOnSaveAction.Context.clean(srcUrl1);
        a.performAction(ctx);
        assertEquals(1, impl1.getInvocationCountAndReset());
        assertEquals(0, impl2.getInvocationCountAndReset());
        assertEquals(0, impl3.getInvocationCountAndReset());
        impl1.setEnabled(false);
        a.performAction(ctx);
        assertEquals(0, impl1.getInvocationCountAndReset());
        assertEquals(0, impl2.getInvocationCountAndReset());
        assertEquals(1, impl3.getInvocationCountAndReset());        
    }
    
    
    public void testQueryEvents() throws IOException {        
        CompileOnSaveAction a = CompileOnSaveActionQuery.getAction(srcUrl1);
        assertNotNull(a);
        CompileOnSaveAction.Context ctx = CompileOnSaveAction.Context.clean(srcUrl1);
        a.performAction(ctx);
        assertEquals(1, impl1.getInvocationCountAndReset());
        assertEquals(0, impl2.getInvocationCountAndReset());
        assertEquals(0, impl3.getInvocationCountAndReset());
        final MockChangeListener l = new MockChangeListener();
        a.addChangeListener(l);
        impl1.setEnabled(false);
        l.assertEvent();
        a.performAction(ctx);
        assertEquals(0, impl1.getInvocationCountAndReset());
        assertEquals(0, impl2.getInvocationCountAndReset());
        assertEquals(1, impl3.getInvocationCountAndReset());        
    }
    
    
    
    private static final class ActionImpl implements CompileOnSaveAction {
        
        private final ChangeSupport listeners = new ChangeSupport(this);
        private boolean enabled;
        private boolean resEnabled;
        private boolean clzEnabled;
        private int invocationCount;

        @Override
        public Boolean performAction(Context ctx) throws IOException {
            invocationCount++;
            return null;
        }
        
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public boolean isUpdateResources() {
            return resEnabled;
        }

        @Override
        public boolean isUpdateClasses() {
            return clzEnabled;
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            listeners.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {            
            listeners.removeChangeListener(l);
        }
        
        void setUpdateResources(final boolean v) {
            this.resEnabled = v;
            listeners.fireChange();
        }
        
        void setUpdateClasses(final boolean v) {
            this.clzEnabled = v;
            listeners.fireChange();
        }
        
        void setEnabled(final boolean v) {
            this.enabled = v;
            listeners.fireChange();
        }
        
        int getInvocationCountAndReset() {
            int res = invocationCount;
            invocationCount = 0;
            return res;
        }
    }
    
    private static final class ProviderImpl implements CompileOnSaveAction.Provider {
        private final URL root;
        private final CompileOnSaveAction action;
        
        ProviderImpl(
                @NonNull final URL root,
                @NonNull final CompileOnSaveAction action) {
            Parameters.notNull("root", root);   //NOI18N
            Parameters.notNull("action", action);   //NOI18N
            this.root = root;
            this.action = action;
        }       
        
        @Override
        @CheckForNull
        public CompileOnSaveAction forRoot(@NonNull final URL root) {
            if (this.root.equals(root)) {
                return this.action;
            }
            return null;
        }        
    }
        
    
}
