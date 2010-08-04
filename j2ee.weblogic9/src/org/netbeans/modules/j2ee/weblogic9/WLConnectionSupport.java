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

package org.netbeans.modules.j2ee.weblogic9;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.weblogic9.deploy.WLDeploymentManager;
import org.openide.util.Exceptions;

/**
 *
 * @author Petr Hejl
 */
public final class WLConnectionSupport {

    private final WLDeploymentManager deploymentManager;

    public WLConnectionSupport(WLDeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
    }

    public <T> T executeAction(Callable<T> action) throws Exception {
        synchronized (deploymentManager) {
            ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(
                    WLDeploymentManagerAccessor.getDefault().getWLClassLoader(deploymentManager));
            try {
                return action.call();
            } finally {
                Thread.currentThread().setContextClassLoader(originalLoader);
            }
        }
    }

    public <T> T executeAction(JMXAction<T> action) throws Exception {
        synchronized (deploymentManager) {
            ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(
                    WLDeploymentManagerAccessor.getDefault().getWLClassLoader(deploymentManager));
            try {
                InstanceProperties instanceProperties = deploymentManager.getInstanceProperties();
                String host = instanceProperties.getProperty(WLPluginProperties.HOST_ATTR);
                String port = instanceProperties.getProperty(WLPluginProperties.PORT_ATTR);
                if ((host == null || host.trim().length() == 0
                        && (port == null || port.trim().length() == 0))) {
                    Properties domainProperties = WLPluginProperties.getDomainProperties(
                            instanceProperties.getProperty( WLPluginProperties.DOMAIN_ROOT_ATTR));
                    host = domainProperties.getProperty(WLPluginProperties.HOST_ATTR);
                    port = domainProperties.getProperty(WLPluginProperties.PORT_ATTR);
                }
                JMXServiceURL url = new JMXServiceURL("iiop", host.trim(), // NOI18N
                        Integer.parseInt(port.trim()), action.getPath());

                Map<String, String> env = new HashMap<String, String>();
                env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
                             "weblogic.management.remote"); // NOI18N
                env.put(javax.naming.Context.SECURITY_PRINCIPAL, deploymentManager.
                        getInstanceProperties().getProperty(
                                InstanceProperties.USERNAME_ATTR).toString());
                env.put(javax.naming.Context.SECURITY_CREDENTIALS, deploymentManager.
                        getInstanceProperties().getProperty(
                                InstanceProperties.PASSWORD_ATTR).toString());

                JMXConnector jmxConnector = JMXConnectorFactory.newJMXConnector(url, env);
                jmxConnector.connect();
                try {
                    return action.call(jmxConnector.getMBeanServerConnection());
                } finally {
                    jmxConnector.close();
                }
            } finally {
                Thread.currentThread().setContextClassLoader(originalLoader);
            }
        }
    }

    public static interface JMXAction<T> {

        T call(MBeanServerConnection connection) throws Exception;

        String getPath();

    }
    
    public static abstract class JMXDomainRuntimeServiceAction<T> implements JMXAction<T>{

        public String getPath() {
            return "/jndi/weblogic.management.mbeanservers.domainruntime";// NOI18N
        }
        
        public ObjectName getRootService() throws MalformedObjectNameException{
            return new ObjectName(
                    "com.bea:Name=DomainRuntimeService,"
                            + "Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");// NOI18N
        }
    }
    
    public static abstract class JMXEditAction<T> implements JMXAction<T>{

        public String getPath() {
            return "/jndi/weblogic.management.mbeanservers.edit";         // NOI18N
        }
        
        public ObjectName getRootService() throws MalformedObjectNameException{
            return new ObjectName(
                    "com.bea:Name=EditService,"
                    + "Type=weblogic.management.mbeanservers.edit.EditServiceMBean");// NOI18N
        }
    }

    public static abstract class WLDeploymentManagerAccessor {

        private static volatile WLDeploymentManagerAccessor accessor;

        public static void setDefault(WLDeploymentManagerAccessor accessor) {
            if (WLDeploymentManagerAccessor.accessor != null) {
                throw new IllegalStateException("Already initialized accessor"); // NOI18N
            }
            WLDeploymentManagerAccessor.accessor = accessor;
        }

        public static WLDeploymentManagerAccessor getDefault() {
            if (accessor != null) {
                return accessor;
            }

            Class c = WLConnectionSupport.class;
            try {
                Class.forName(c.getName(), true, WLDeploymentManagerAccessor.class.getClassLoader());
            } catch (ClassNotFoundException cnf) {
                Exceptions.printStackTrace(cnf);
            }

            return accessor;
        }

        public abstract ClassLoader getWLClassLoader(WLDeploymentManager manager);
    }
}
