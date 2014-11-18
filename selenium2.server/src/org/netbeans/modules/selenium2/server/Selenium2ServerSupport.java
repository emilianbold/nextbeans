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
package org.netbeans.modules.selenium2.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.Utilities;

/**
 *
 * @author Theofanis Oikonomou
 */
public class Selenium2ServerSupport implements Runnable, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(Selenium2ServerSupport.class.getName());

    private static final Selenium2ServerSupport INSTANCE = new Selenium2ServerSupport();
    private static Object server = null;
    private static boolean isRunning = false;
    private static boolean isStarting = false;
    private static Action action = null;
    private static Task latestTask = null;
    public static final int PORT_DEFAULT = 4444;
    public static final String PORT = "port"; //NOI18N
    public static final String USER_EXTENSION_FILE = "user.extension.file"; //NOI18N
    public static final String SELENIUM_SERVER_JAR = "selenium.server.jar"; //NOI18N
    public static final boolean SINGLE_WINDOW_DEFAULT = false;
    public static final String SINGLE_WINDOW = "single.window"; //NOI18N
    public static final String FIREFOX_PROFILE_TEMPLATE_DIR = "firefox.profile.template.dir"; //NOI18N

    private Selenium2ServerSupport() {
    }

    public static Selenium2ServerSupport getInstance() {
        return INSTANCE;
    }

    public Task startServer() {
        if (isRunning()) {
            return Task.EMPTY;
        }
        
        if (!Selenium2Customizer.isConfiguredProperly()) {
            if (!configureServer()) {
                return Task.EMPTY;
            }
        }
        
        action = Action.START;
        return postTask();
    }

    public Task stopServer() {
        if (!isRunning()) {
            return Task.EMPTY;
        }
        action = Action.STOP;
        return postTask();
    }

    public Task restartServer() {
        if (!isRunning()) {
            return startServer();
        } else {
            action = Action.RESTART;
            return postTask();
        }
    }
    
    public boolean configureServer() {
        final boolean[] res = new boolean[1];
        Runnable r = new Runnable() {
            @Override
            public void run() {
                boolean b = Selenium2Customizer.showCustomizer();
                Selenium2ServicesNode.getInstance().refresh();
                res[0] = b;
            }
        };
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                // it should be safe to call invokeAndWait here
                // as this action will be either called from AWT thread or from
                // RequestProcessor as a result of "start server" in
                // case when configuration is missing the first time:
                SwingUtilities.invokeAndWait(r);
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        return res[0];
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isStarting() {
        return isStarting;
    }

    private static Task postTask(){
        Task t = RequestProcessor.getDefault().post(INSTANCE);
        latestTask = t;
        return t;
    }

    @Override
    public void run() {
        try {
            isStarting = true;
            Selenium2ServicesNode.getInstance().refresh();
            if (server == null) {
                initializeServer();
            }
            switch (action) {
                case START:
                    callSeleniumServerMethod("boot");
                    callSeleniumServerMethod("start");
                    break;
                case STOP:
                    callSeleniumServerMethod("stop");
                    break;
                case RESTART:
                    callSeleniumServerMethod("stop");
                    callSeleniumServerMethod("boot");
                    callSeleniumServerMethod("start");
                    break;
                case RELOAD:
                    callSeleniumServerMethod("stop");
                    server = null;
                    initializeServer();
                    callSeleniumServerMethod("boot");
                    callSeleniumServerMethod("start");
                    break;
                default:
                    assert false : "Invalid option";
            }
            isStarting = false;
            Selenium2ServicesNode.getInstance().refresh();
            if (action == null) {
                return;
            }
            isRunning = (!action.equals(Action.STOP));
            action = null;
        } catch (BindException bi) {
            LOGGER.log(Level.INFO, "Port already in use - the server is probably already running.", bi); //NOI18N
        } catch (Exception exc) {
            LOGGER.log(Level.INFO, null, exc);
        }
    }

    protected static URLClassLoader getSeleniumServerClassLoader() {
        URL url = null;
        try {
            url = Utilities.toURI(new File(getPrefs().get(SELENIUM_SERVER_JAR, null))).toURL();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return URLClassLoader.newInstance(new URL[] {url});
    }

    private void callSeleniumServerMethod(String method) {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader curr = server.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(curr);
            server.getClass().getMethod(method).invoke(server);
        } catch (IllegalAccessException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (NoSuchMethodException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (SecurityException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (InvocationTargetException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    private static void initializeServer() throws Exception {
        URLClassLoader urlClassLoader = getSeleniumServerClassLoader();
        Class seleniumServer = urlClassLoader.loadClass("org.openqa.selenium.server.SeleniumServer"); //NOI18N
        Class remoteControlConfiguration = urlClassLoader.loadClass(
                "org.openqa.selenium.server.RemoteControlConfiguration"); //NOI18N

        Object remoteControlConfigurationInstance = remoteControlConfiguration.newInstance();
        int port = getPrefs().getInt(PORT, PORT_DEFAULT);
        remoteControlConfiguration.getMethod("setPort", int.class).invoke(
            remoteControlConfigurationInstance, port); //NOI18N
        boolean runInSingleWindow = getPrefs().getBoolean(SINGLE_WINDOW, SINGLE_WINDOW_DEFAULT);
        remoteControlConfiguration.getMethod("setSingleWindow", Boolean.TYPE).invoke( //NOI18N
                remoteControlConfigurationInstance, runInSingleWindow);
		String firefoxProfileDir = getPrefs().get(FIREFOX_PROFILE_TEMPLATE_DIR, ""); //NOI18N
		if (!firefoxProfileDir.isEmpty()) {
				File ffProfileDir = new File(firefoxProfileDir);
				if (ffProfileDir.exists()) {
					remoteControlConfiguration.getMethod("setFirefoxProfileTemplate", File.class).invoke( //NOI18N
						remoteControlConfigurationInstance, ffProfileDir);
				}
		}
        String userExtensionsString = getPrefs().get(USER_EXTENSION_FILE, ""); //NOI18N
        if (!userExtensionsString.isEmpty()) {
            File userExtensionFile = new File(userExtensionsString);
            if (userExtensionFile.exists()) {
                remoteControlConfiguration.getMethod("setUserExtensions", File.class).invoke( //NOI18N
                        remoteControlConfigurationInstance, userExtensionFile);
            }
        }
        
        server = seleniumServer.getConstructor(remoteControlConfiguration).
                newInstance(remoteControlConfigurationInstance);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PORT.equals(evt.getPropertyName())){
            action = Action.RELOAD;
            RequestProcessor.getDefault().post(INSTANCE);
        }
    }

    public static Preferences getPrefs() {
        return NbPreferences.forModule(Selenium2Customizer.class);
    }

    // listen on SeleniumProperties
    static PropertyChangeListener getPropertyChangeListener() {
        return INSTANCE;
    }

    private static enum Action {

        START, STOP, RESTART, RELOAD
    }

    static void waitAllTasksFinished(){
        if (latestTask == null){
            return;
        }
        while (!latestTask.isFinished()){
            latestTask.waitFinished();
        }
    }
}
