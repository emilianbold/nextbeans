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

package org.netbeans.debugger.registry;

import java.util.Map;

import org.netbeans.api.debugger.DebuggerEngine.Destructor;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.DebuggerEngineProvider;

/**
 *
 * @author Martin Entlicher
 */
public class DebuggerEngineProviderContextAware extends DebuggerEngineProvider implements ContextAwareService<DebuggerEngineProvider> {

    private String serviceName;
    private ContextProvider context;
    private DebuggerEngineProvider delegate;

    private DebuggerEngineProviderContextAware(String serviceName) {
        this.serviceName = serviceName;
    }

    private DebuggerEngineProviderContextAware(String serviceName, ContextProvider context) {
        this.serviceName = serviceName;
        this.context = context;
    }

    private synchronized DebuggerEngineProvider getDelegate() {
        if (delegate == null) {
            delegate = (DebuggerEngineProvider) ContextAwareSupport.createInstance(serviceName, context);
        }
        return delegate;
    }

    public DebuggerEngineProvider forContext(ContextProvider context) {
        if (context == this.context) {
            return this;
        } else {
            return new DebuggerEngineProviderContextAware(serviceName, context);
        }
    }

    @Override
    public String[] getLanguages() {
        return getDelegate().getLanguages();
    }

    @Override
    public String getEngineTypeID() {
        return getDelegate().getEngineTypeID();
    }

    @Override
    public Object[] getServices() {
        return getDelegate().getServices();
    }

    @Override
    public void setDestructor(Destructor desctuctor) {
        getDelegate().setDestructor(desctuctor);
    }

    /**
     * Creates instance of <code>ContextAwareService</code> based on layer.xml
     * attribute values
     *
     * @param attrs attributes loaded from layer.xml
     * @return new <code>ContextAwareService</code> instance
     */
    static ContextAwareService createService(Map attrs) throws ClassNotFoundException {
        String serviceName = (String) attrs.get(ContextAwareServiceHandler.SERVICE_NAME);
        return new DebuggerEngineProviderContextAware(serviceName);
    }

}
