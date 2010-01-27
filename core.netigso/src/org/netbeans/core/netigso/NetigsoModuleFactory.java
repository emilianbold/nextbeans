/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.core.netigso;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import org.netbeans.Events;
import org.netbeans.InvalidException;
import org.netbeans.Module;
import org.netbeans.ModuleFactory;
import org.netbeans.ModuleManager;
import org.netbeans.Stamps;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
@ServiceProvider(service=ModuleFactory.class)
public class NetigsoModuleFactory extends ModuleFactory
implements Stamps.Updater {
    private static NetigsoModuleFactory instance;
    private static NetigsoActivator activator;
    private static Framework framework;
    private static Set<String> registered;
    private static List<NetigsoModule> toInit = new ArrayList<NetigsoModule>();
    private static List<Module> toRegister = new ArrayList<Module>();

    public NetigsoModuleFactory() {
        instance = this;
        readBundles();
    }

    /** Starts all the bundles with enabled modules.*/
    static void start() {
        List<NetigsoModule> turnOn;
        List<Module> turnRegister;
        synchronized (NetigsoModuleFactory.class) {
            turnOn = toInit;
            turnRegister = toRegister;
            toRegister = null;
            toInit = null;
        }
        if (turnOn == null || turnOn.isEmpty()) {
            return;
        }

        for (Module m :turnRegister) {
            try {
                fakeOneModule(m);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        startContainer();
        for (NetigsoModule nm : turnOn) {
            try {
                nm.start();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    static synchronized void classLoaderUp(NetigsoModule nm) throws IOException {
        if (toInit != null) {
            toInit.add(nm);
            return;
        }
        startContainer();
        nm.start();
    }

    static synchronized void classLoaderDown(NetigsoModule nm) {
        if (toInit != null) {
            toInit.remove(nm);
            return;
        }
    }

    /** Used on shutdown */
    static void shutdown() throws Exception {
        activator = null;
        if (framework != null) {
            framework.stop();
            framework.waitForStop(10000L);
        }
        framework = null;
    }
    
    /** used from tests.
     */
    static void clear() throws Exception {
        shutdown();
        readBundles();
    }

    private static File getNetigsoCache() throws IllegalStateException {
        // Explicitly specify the directory to use for caching bundles.
        String ud = System.getProperty("netbeans.user");
        if (ud == null) {
            throw new IllegalStateException();
        }
        File udf = new File(ud);
        return new File(new File(new File(udf, "var"), "cache"), "netigso");
    }

    private static void deleteRec(File dir) {
        File[] arr = dir.listFiles();
        if (arr != null) {
            for (File f : arr) {
                deleteRec(f);
            }
        }
        dir.delete();
    }

    private static void readBundles() {
        registered = new HashSet<String>();
        try {
            InputStream is = Stamps.getModulesJARs().asStream("netigso-bundles");
            if (is == null) {
                File f;
                try {
                    f = getNetigsoCache();
                } catch (IllegalStateException ex) {
                    return;
                }
                deleteRec(f);
                return;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8")); // NOI18N
            for (;;) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                registered.add(line);
            }
        } catch (IOException ex) {
            NetigsoModule.LOG.log(Level.WARNING, "Cannot read cache", ex);
        }
    }

    @Override
    public Module createFixed(Manifest mani, Object history, ClassLoader loader, boolean autoload, boolean eager, ModuleManager mgr, Events ev) throws InvalidException {
        Module m = super.createFixed(mani, history, loader, autoload, eager, mgr, ev);
        try {
            registerBundle(m);
        } catch (IOException ex) {
            throw (InvalidException)new InvalidException(m, ex.getMessage()).initCause(ex);
        }
        return m;
    }

    @Override
    public Module create(
        File jar, Object history,
        boolean reloadable, boolean autoload, boolean eager,
        ModuleManager mgr, Events ev
    ) throws IOException {
        try {
            Module m = super.create(jar, history, reloadable, autoload, eager, mgr, ev);
            registerBundle(m);
            return m;
        } catch (InvalidException ex) {
            Manifest mani = ex.getManifest();
            if (mani != null) {
                String name = mani.getMainAttributes().getValue("Bundle-SymbolicName"); // NOI18N
                if (name == null) {
                    throw ex;
                }
                return new NetigsoModule(mani, jar, mgr, ev, history, reloadable, autoload, eager);
            }
            throw ex;
        }
    }

    synchronized static Framework getContainer() throws BundleException {
        if (framework == null) {
            assert toInit == null : "OSGi container shall be initialized only after restored of core.netigso was called";
            Map<String,Object> configMap = new HashMap<String,Object>();
            final String cache = getNetigsoCache().getPath();
            configMap.put(Constants.FRAMEWORK_STORAGE, cache);
            activator = new NetigsoActivator();
            configMap.put("felix.bootdelegation.classloaders", activator);
            FrameworkFactory frameworkFactory = Lookup.getDefault().lookup(FrameworkFactory.class);
            if (frameworkFactory == null) {
                throw new IllegalStateException(
                    "Cannot find OSGi framework implementation." + // NOI18N
                    " Is org.netbeans.libs.felix module or similar enabled?" // NOI18N
                );
            }
            framework = frameworkFactory.newFramework(configMap);
            framework.init();
            new NetigsoServices(framework);
            NetigsoModule.LOG.finer("OSGi Container initialized"); // NOI18N
        }
        return framework;
    }

    private static void startContainer() {
        try {
            if (getContainer().getState() == Bundle.STARTING) {
                NetigsoModule.LOG.finer("OSGi start:"); // NOI18N
                getContainer().start();
                NetigsoModule.LOG.finer("OSGi started"); // NOI18N
            }
        } catch (BundleException ex) {
            Exceptions.printStackTrace(ex);
        }
    }


    /** Creates a fake bundle definition that represents one NetBeans module
     *
     * @param m the module
     * @return the stream to read the definition from or null, if it does not
     *   make sense to represent this module as bundle
     */
    private static final InputStream fakeBundle(Module m) throws IOException {
        String exp = (String) m.getAttribute("OpenIDE-Module-Public-Packages"); // NOI18N
        if ("-".equals(exp)) { // NOI18N
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Manifest man = new Manifest();
        man.getMainAttributes().putValue("Manifest-Version", "1.0"); // workaround for JDK bug
        man.getMainAttributes().putValue("Bundle-ManifestVersion", "2"); // NOI18N
        man.getMainAttributes().putValue("Bundle-SymbolicName", m.getCodeNameBase()); // NOI18N

        if (m.getSpecificationVersion() != null) {
            String spec = threeDotsWithMajor(m.getSpecificationVersion().toString(), m.getCodeName());
            man.getMainAttributes().putValue("Bundle-Version", spec.toString()); // NOI18N
        }
        if (exp != null) {
            man.getMainAttributes().putValue("Export-Package", exp.replaceAll("\\.\\*", "")); // NOI18N
        } else {
            man.getMainAttributes().putValue("Export-Package", m.getCodeNameBase()); // NOI18N
        }
        JarOutputStream jos = new JarOutputStream(os, man);
        jos.close();
        return new ByteArrayInputStream(os.toByteArray());
    }

    private static String threeDotsWithMajor(String version, String withMajor) {
        int indx = withMajor.indexOf('/');
        int major = 0;
        if (indx > 0) {
            major = Integer.parseInt(withMajor.substring(indx + 1));
        }
        String[] segments = (version + ".0.0.0").split("\\.");
        assert segments.length >= 3 && segments[0].length() > 0;

        return (Integer.parseInt(segments[0]) + major * 100) + "."  + segments[1] + "." + segments[2];
    }

    private static void registerBundle(Module m) throws IOException {
        if (!registered.add(m.getCodeName())) {
            return;
        }

        synchronized (NetigsoModuleFactory.class) {
            if (toRegister != null) {
                toRegister.add(m);
                return;
            }
        }
        fakeOneModule(m);
    }

    private static void fakeOneModule(Module m) throws IOException {
        InputStream is = fakeBundle(m);
        if (is != null) {
            try {
                NetigsoActivator.register(m);
                getContainer().getBundleContext().installBundle("netigso://" + m.getCodeNameBase(), is);
                is.close();
            } catch (BundleException ex) {
                throw new IOException(ex.getMessage());
            }
            Stamps.getModulesJARs().scheduleSave(instance, "netigso-bundles", false); // NOI18N
        }
    }

    public void flushCaches(DataOutputStream os) throws IOException {
        Writer w = new OutputStreamWriter(os);
        for (String s : registered) {
            w.write(s);
            w.write('\n');
        }
        w.close();
    }

    public void cacheReady() {
    }
}
