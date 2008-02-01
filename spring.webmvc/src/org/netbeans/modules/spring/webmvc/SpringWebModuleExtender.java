/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 * Portions Copyrighted 2008 Craig MacKay.
 */

package org.netbeans.modules.spring.webmvc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.j2ee.dd.api.common.CommonDDBean;
import org.netbeans.modules.j2ee.dd.api.common.CreateCapability;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Listener;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.ServletMapping;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.WelcomeFileList;
import org.netbeans.modules.spring.api.beans.ConfigFileGroup;
import org.netbeans.modules.spring.api.beans.ConfigFileManager;
import org.netbeans.modules.spring.api.beans.SpringScope;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * The WebModuleExtender implementation for Spring Web MVC.
 *
 * @author Craig MacKay
 */
public class SpringWebModuleExtender extends WebModuleExtender implements ChangeListener {  
    
    private static final Logger LOGGER = Logger.getLogger(SpringWebModuleExtender.class.getName());
    
    private SpringConfigPanelVisual frameworkPanelVisual;
    private final SpringWebFrameworkProvider framework;
    private final ExtenderController controller;
    private boolean customizer;

    private String dispatcherName; 
    private String dispatcherMapping; 
    private boolean includeJstl = true;


    /**
     * Creates a new instance of SpringWebModuleExtender 
     * @param framework
     * @param controller an instance of org.netbeans.modules.web.api.webmodule.ExtenderController 
     * @param customizer
     * @param dispatcherName
     * @param dispatcherMapping
     */
    public SpringWebModuleExtender(SpringWebFrameworkProvider framework, ExtenderController controller, boolean customizer, String dispatcherName, String dispatcherMapping) {
        this.framework = framework;
        this.controller = controller;
        this.customizer = customizer;
        this.dispatcherName = dispatcherName;
        this.dispatcherMapping = dispatcherMapping; 
    }
    
    public ExtenderController getController() {
        return controller;
    }

    public String getDispatcherName() {
        return dispatcherName;
    }

    public String getDispatcherMapping() {
        return dispatcherMapping;
    }

    public boolean getIncludeJstl() {
        return includeJstl;
    }

    public SpringConfigPanelVisual getComponent() {
        if (frameworkPanelVisual == null) {
            frameworkPanelVisual = new SpringConfigPanelVisual(this);
        }
        return frameworkPanelVisual;
    }

    public boolean isValid() {
        if (dispatcherName == null || dispatcherName.trim().length() == 0){
            controller.setErrorMessage(NbBundle.getMessage(SpringConfigPanelVisual.class, "MSG_NamePatternIsEmpty")); // NOI18N
            return false;
        }
        if (!isNamePatternValid(dispatcherName)){
            controller.setErrorMessage(NbBundle.getMessage(SpringConfigPanelVisual.class, "MSG_NamePatternIsNotValid")); // NOI18N
            return false;
        }
        if (dispatcherMapping == null || dispatcherMapping.trim().length() == 0) {
            controller.setErrorMessage(NbBundle.getMessage(SpringConfigPanelVisual.class, "MSG_MappingPatternIsEmpty")); // NOI18N
            return false;
        }
        if (!isMappingPatternValid(dispatcherMapping)){
            controller.setErrorMessage(NbBundle.getMessage(SpringConfigPanelVisual.class, "MSG_MappingPatternIsNotValid")); // NOI18N
            return false;
        }        
        controller.setErrorMessage(null);
        return true;    
    }
    
    private boolean isNamePatternValid(String pattern) {        
        return Pattern.matches("\\w+", pattern);
    }
    
    private boolean isMappingPatternValid(String pattern){
        // mapping validation based on the Servlet 2.4 specification,section SRV.11.2
        if (pattern.startsWith("*.")){ // NOI18N
            String p = pattern.substring(2);
            if (p.indexOf('.') == -1 && p.indexOf('*') == -1  
                    && p.indexOf('/') == -1 && !p.trim().equals("") && !p.contains(" ")) { // NOI18N
                return true;
            }
        }
        
        if ((pattern.length() > 3) && pattern.endsWith("/*") && pattern.startsWith("/") && !pattern.contains(" ")) // NOI18N
            return true;
        
        if (pattern.matches("/")){ // NOI18N
            return true;
        }
               
        return false;
    }

    public HelpCtx getHelp() {
        return new HelpCtx(SpringWebModuleExtender.class);
    }

    public void readSettings(Object settings) {
    }

    public void storeSettings(Object settings) {
    }

    private final List<ChangeListener> listeners = new ArrayList<ChangeListener>(1);

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    private void fireStateChanged() {
        // Fire change event to check for valid dispatcher name and mapping.        
        ChangeEvent e = new ChangeEvent(this);
        Object[] changeListeners = listeners.toArray();
        for (int i = 0; i < changeListeners.length; i++) {
            ChangeListener changeListener = (ChangeListener)changeListeners[i];
            changeListener.stateChanged(e);
        }        
    }
    
    public void stateChanged(ChangeEvent e) {
        SpringConfigPanelVisual panel = ((SpringConfigPanelVisual)e.getSource());        
        dispatcherName = panel.getDispatcherName();
        dispatcherMapping = panel.getDispatcherMapping();
        includeJstl = panel.getIncludeJstl();
        fireStateChanged();
    }

    @Override
    public void update() {
    // not used yet
    }

    @Override
    public Set<FileObject> extend(WebModule webModule) {
        CreateSpringConfig createSpringConfig = new CreateSpringConfig(webModule);
        FileObject webInf = webModule.getWebInf();
        if (webInf != null) {
            try {
                FileSystem fs = webInf.getFileSystem();
                fs.runAtomicAction(createSpringConfig);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
                return null;
            }
        }
        return createSpringConfig.getFilesToOpen();
    }

    private class CreateSpringConfig implements FileSystem.AtomicAction {

        public static final String SPRING_CLASS_NAME = "org.springframework.core.SpringVersion"; // NOI18N
        public static final String JSTL_CLASS_NAME = "javax.servlet.jsp.jstl.core.Config"; // NOI18N
        public static final String CONTEXT_LOADER = "org.springframework.web.context.ContextLoaderListener"; // NOI18N
        public static final String DISPATCHER_SERVLET = "org.springframework.web.servlet.DispatcherServlet"; // NOI18N
        public static final String ENCODING = "UTF-8"; // NOI18N
        private Set<FileObject> filesToOpen = new LinkedHashSet<FileObject>();
        private WebModule webModule;

        public CreateSpringConfig(WebModule webModule) {
            this.webModule = webModule;
        }

        public void run() throws IOException {
            // MODIFY WEB.XML
            FileObject dd = webModule.getDeploymentDescriptor();
            WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
            addContextParam(ddRoot, "contextConfigLocation", "/WEB-INF/applicationContext.xml"); // NOI18N
            addListener(ddRoot, CONTEXT_LOADER);
            addServlet(ddRoot, getComponent().getDispatcherName(), DISPATCHER_SERVLET, getComponent().getDispatcherMapping(), "2"); // NOI18N
            WelcomeFileList welcomeFiles = ddRoot.getSingleWelcomeFileList();
            if (welcomeFiles == null) {
                try {
                    welcomeFiles = (WelcomeFileList) ddRoot.createBean("WelcomeFileList"); // NOI18N
                    ddRoot.setWelcomeFileList(welcomeFiles);
                } catch (ClassNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (welcomeFiles.sizeWelcomeFile() == 0) {
                welcomeFiles.addWelcomeFile("index.jsp"); // NOI18N
            }
            ddRoot.write(dd);

            // ADD JSTL IF ENABLED
            if (includeJstl) {
                Library jstlLibrary = getLibrary(JSTL_CLASS_NAME);            
                assert jstlLibrary != null;
                addLibraryToWebModule(jstlLibrary, webModule);                
            }

            // ADD SPRING LIBRARY
            Library springLibrary = getLibrary(SPRING_CLASS_NAME);
            assert springLibrary != null;
            addLibraryToWebModule(springLibrary, webModule);

            // CREATE WEB-INF/JSP FOLDER
            FileObject webInf = webModule.getWebInf();
            FileObject jsp = webInf.createFolder("jsp");

            // COPY TEMPLATE SPRING RESOURCES (JSP, XML, PROPERTIES)
            copyResource("index.jsp", FileUtil.createData(jsp, "index.jsp")); // NOI18N
            copyResource("jdbc.properties", FileUtil.createData(webInf, "jdbc.properties")); // NOI18N
            final List<File> configFiles = new ArrayList<File>(2);
            FileObject configFile;
            configFile = copyResource("applicationContext.xml", FileUtil.createData(webInf, "applicationContext.xml")); // NOI18N
            addFileToOpen(configFile);
            configFiles.add(FileUtil.toFile(configFile));
            configFile = copyResource("dispatcher-servlet.xml", FileUtil.createData(webInf, getComponent().getDispatcherName() + "-servlet.xml")); // NOI18N
            addFileToOpen(configFile);
            configFiles.add(FileUtil.toFile(configFile));

            SpringScope scope = SpringScope.getSpringScope(configFile);
            if (scope != null) {
                final ConfigFileManager manager = scope.getConfigFileManager();
                manager.mutex().writeAccess(new Runnable() {
                    public void run() {
                        List<ConfigFileGroup> groups = manager.getConfigFileGroups();
                        String groupName = NbBundle.getMessage(SpringWebModuleExtender.class, "LBL_DefaultGroup");
                        ConfigFileGroup newGroup = ConfigFileGroup.create(groupName, configFiles);
                        groups.add(newGroup);
                        manager.putConfigFileGroups(groups);
                        try {
                            manager.save();
                        } catch (IOException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                });
            } else {
                LOGGER.log(Level.WARNING, "Could not find a SpringScope for file {0}", configFile);
            }

            // MODIFY EXISTING INDEX.JSP
            FileObject documentBase = webModule.getDocumentBase();
            FileObject indexJsp = documentBase.getFileObject("index.jsp"); // NOI18N
            if (indexJsp == null) {
                indexJsp = FileUtil.createData(documentBase, "index.jsp"); // NOI18N
            }
            addFileToOpen(copyResource("redirect.jsp", indexJsp)); // NOI18N
        }

        public void addFileToOpen(FileObject file) {
            filesToOpen.add(file);
        }

        public Set<FileObject> getFilesToOpen() {
            return filesToOpen;
        }

        protected FileObject copyResource(String resourceName, FileObject target) throws UnsupportedEncodingException, IOException {
            InputStream in = getClass().getResourceAsStream("resources/templates/" + resourceName); // NOI18N
            String lineSeparator = System.getProperty("line.separator"); // NOI18N
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, ENCODING));
            try {
                String line = reader.readLine();
                while (line != null) {
                    buffer.append(line);
                    buffer.append(lineSeparator);
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
            FileLock lock = target.lock();
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), ENCODING));
                try {
                    writer.write(buffer.toString());
                } finally {
                    writer.close();
                }
            } finally {
                lock.releaseLock();
            }
            return target;
        }

        @SuppressWarnings(value = "deprecation")
        protected void addLibraryToWebModule(Library library, WebModule webModule) throws IOException {
            FileOwnerQuery.getOwner(webModule.getDocumentBase()).getLookup().lookup(org.netbeans.spi.java.project.classpath.ProjectClassPathExtender.class).addLibrary(library);
        }

        protected Listener addListener(WebApp webApp, String classname) throws IOException {
            Listener listener = (Listener) createBean(webApp, "Listener"); // NOI18N
            listener.setListenerClass(classname);
            webApp.addListener(listener);
            return listener;
        }

        protected Servlet addServlet(WebApp webApp, String name, String classname, String pattern, String loadOnStartup) throws IOException {
            Servlet servlet = (Servlet) createBean(webApp, "Servlet"); // NOI18N
            servlet.setServletName(name);
            servlet.setServletClass(classname);
            if (loadOnStartup != null) {
                servlet.setLoadOnStartup(new BigInteger(loadOnStartup));
            }
            webApp.addServlet(servlet);
            if (pattern != null) {
                addServletMapping(webApp, name, pattern);
            }
            return servlet;
        }

        protected ServletMapping addServletMapping(WebApp webApp, String name, String pattern) throws IOException {
            ServletMapping mapping = (ServletMapping) createBean(webApp, "ServletMapping"); // NOI18N
            mapping.setServletName(name);
            mapping.setUrlPattern(pattern);
            webApp.addServletMapping(mapping);
            return mapping;
        }

        protected InitParam addContextParam(WebApp webApp, String name, String value) throws IOException {
            InitParam initParam = (InitParam) createBean(webApp, "InitParam"); // NOI18N
            initParam.setParamName(name);
            initParam.setParamValue(value);
            webApp.addContextParam(initParam);
            return initParam;
        }

        protected CommonDDBean createBean(CreateCapability creator, String beanName) throws IOException {
            CommonDDBean bean = null;
            try {
                bean = creator.createBean(beanName);
            } catch (ClassNotFoundException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
                throw new IOException("Error creating bean with name:" + beanName); // NOI18N
            }
            return bean;
        }
        
        private boolean containsPath(List<URL> roots, String relativePath) {
             // workaround for #126307
            List<URL> validRoots = new ArrayList<URL>();            
            URL url = null;
            Iterator it = roots.iterator();
            while (it.hasNext()) {
                url = (URL)it.next();
                if ((url.getPath().startsWith("nbinst://"))) { // NOI18N
                    validRoots.add(url);
                }
            }                        
            ClassPath cp = ClassPathSupport.createClassPath((validRoots.toArray(new URL[validRoots.size()])));
            return cp.findResource(relativePath) != null;
        }
                        
        private boolean containsClass(List<URL> libraryContent, String className) {   
            String classRelativePath = className.replace('.', '/') + ".class"; //NOI18N
            return containsPath(libraryContent, classRelativePath); //NOI18N
        }            
        
        protected Library getLibrary(String className) {
            for (Library eachLibrary : LibraryManager.getDefault().getLibraries()) {              
                if (containsClass(eachLibrary.getContent("classpath"), className)) { // NOI18N
                    return eachLibrary;
                }
            }

            //Library wasn't found
            return null;
        }     
    }
}
