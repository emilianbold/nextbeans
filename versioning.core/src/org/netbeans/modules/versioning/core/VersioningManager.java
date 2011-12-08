/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
 */
package org.netbeans.modules.versioning.core;

import org.netbeans.modules.versioning.core.spi.VCSInterceptor;
import org.netbeans.modules.versioning.core.spi.VCSVisibilityQuery;
import org.netbeans.modules.versioning.core.spi.VCSAnnotator;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import org.netbeans.modules.versioning.core.api.VersioningSupport;
import org.netbeans.modules.masterfs.providers.InterceptionListener;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.LookupEvent;
import org.netbeans.modules.versioning.core.util.VCSSystemProvider.VersioningSystem;
import java.io.File;
import java.util.*;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.util.VCSSystemProvider;
import org.netbeans.spi.queries.CollocationQueryImplementation;
import org.openide.util.*;

/**
 * Top level versioning manager that mediates communitation between IDE and registered versioning systems.
 * 
 * @author Maros Sandor
 */
public class VersioningManager implements PropertyChangeListener, LookupListener, PreferenceChangeListener {
    
    /**
     * Indicates to the Versioning manager that the layout of versioned files may have changed. Previously unversioned 
     * files became versioned, versioned files became unversioned or the versioning system for some files changed.
     * The manager will flush any caches that may be holding such information.  
     * A versioning system usually needs to fire this after an Import action. 
     */
    public static final String EVENT_VERSIONED_ROOTS = "null VCS.VersionedFilesChanged";

    /**
     * The NEW value is a Set of Files whose versioning status changed. This event is used to re-annotate files, re-fetch
     * original content of files and generally refresh all components that are connected to these files.
     */
    public static final String EVENT_STATUS_CHANGED = "Set<File> VCS.StatusChanged";

    /**
     * Used to signal the Versioning manager that some annotations changed. Note that this event is NOT required in case
     * the status of the file changes in which case annotations are updated automatically. Use this event to force annotations
     * refresh in special cases, for example when the format of annotations changes.
     * Use null as new value to force refresh of all annotations.
     */
    public static final String EVENT_ANNOTATIONS_CHANGED = "Set<File> VCS.AnnotationsChanged";


    /**
     * Priority defining the order of versioning systems used when determining the owner of a file. I.e. what versioning system should handle the file.
     * @see #getProperty(String)
     * @see #putProperty(String, Object)
     */
    static final String PROP_PRIORITY = "Integer VCS.Priority"; //NOI18N
    
    private static VersioningManager instance;
    private static boolean initialized = false;
    private static boolean initializing = false;
    private static final Object INIT_LOCK = new Object();

    public static synchronized VersioningManager getInstance() {
        if (instance == null) {
            instance = new VersioningManager();
            instance.init();
        }
        return instance;
    }

    public static boolean isInitialized() {
        if(initialized && OpenProjects.getDefault().openProjects().isDone()) {
            return true;
        }
        synchronized(INIT_LOCK) {
            if(!initializing) {
                initializing = true;
                new RequestProcessor("Initialize VCS").post(new Runnable() {        // NOI18N
                    @Override
                    public void run() {                    
                        getInstance(); // init manager                                                    
                    }                    
                });
            }
        }
        return false;
    }

    // ======================================================================================================

    private final FilesystemInterceptor filesystemInterceptor;

    /**
     * Result of Lookup.getDefault().lookup(new Lookup.Template<VersioningSystem>(VersioningSystem.class));
     */
//    private final Lookup.Result<VersioningSystem> systemsLookupResult;
    
    /**
     * Holds all registered versioning systems.
     */
    private final List<VCSSystemProvider.VersioningSystem> versioningSystems = new ArrayList<VCSSystemProvider.VersioningSystem>(5);

    /**
     * What folder is versioned by what versioning system. 
     * TODO: use SoftHashMap if there is one available in APIs
     */
    private final Map<VCSFileProxy, VCSSystemProvider.VersioningSystem> folderOwners = new HashMap<VCSFileProxy, VCSSystemProvider.VersioningSystem>(200);
    
    /**
     * What file is versioned by what versioning system - keep it small
     * We will hold only recently questioned files for cases when a file is subsequently 
     * queried too often in a short time.
     * 
     */
    private final Map<VCSFileProxy, VCSSystemProvider.VersioningSystem> fileOwners = new LinkedHashMap<VCSFileProxy, VCSSystemProvider.VersioningSystem>(50) {
        private int MAX_SIZE = 50;
        @Override
        protected boolean removeEldestEntry(Entry<VCSFileProxy, VCSSystemProvider.VersioningSystem> eldest) {
            return size() > MAX_SIZE;
        }        
    };

    /**
     * Holds registered local history system.
     */
    private VCSSystemProvider.VersioningSystem localHistory;

    public static final Logger LOG = Logger.getLogger("org.netbeans.modules.versioning");

    /**
     * What files or folders are managed by local history.
     * TODO: use SoftHashMap if there is one available in APIs
     */
    private final Map<VCSFileProxy, Boolean> localHistoryFiles = new LinkedHashMap<VCSFileProxy, Boolean>(200);

    /**
     * Holds methods intercepted by a specific vcs. See {@link #needsLocalHistory(methodName)}
     */
    private Map<String, Set<String>> interceptedMethods = new HashMap<String, Set<String>>();

    private final VersioningSystem NULL_OWNER = new VersioningSystem() {
        @Override public boolean isLocalHistory() {  throw new IllegalStateException(); }
        @Override public VCSFileProxy getTopmostManagedAncestor(VCSFileProxy file) { throw new IllegalStateException(); }
        @Override public VCSInterceptor getInterceptor() { throw new IllegalStateException(); }
        @Override public void getOriginalFile(VCSFileProxy workingCopy, VCSFileProxy originalFile) { throw new IllegalStateException(); }
        @Override public CollocationQueryImplementation getCollocationQueryImplementation() { throw new IllegalStateException(); }
        @Override public void addPropertyCL(PropertyChangeListener listener) { throw new IllegalStateException(); }
        @Override public void removePropertyCL(PropertyChangeListener listener) { throw new IllegalStateException(); }
        @Override public boolean isExcluded(VCSFileProxy file) { throw new IllegalStateException(); }
        @Override public VCSAnnotator getAnnotator() { throw new IllegalStateException(); }
        @Override public VCSVisibilityQuery getVisibility() { throw new IllegalStateException(); }
        @Override public Object getDelegate() { throw new IllegalStateException(); }
        @Override public String getDisplayName() { throw new IllegalStateException(); }
        @Override public String getMenuLabel() { throw new IllegalStateException(); }
    };
    
    private VersioningManager() {
//        systemsLookupResult = Lookup.getDefault().lookup(new Lookup.Template<VersioningSystem>(VersioningSystem.class));
        filesystemInterceptor = new FilesystemInterceptor(true);
    }
    
    private void init() {
        try {
//            systemsLookupResult.addLookupListener(this);
            refreshVersioningSystems();
            filesystemInterceptor.init(this);
            VersioningSupport.getPreferences().addPreferenceChangeListener(this);
        } finally {
            initialized = true;                                    
        }
    }

    private int refreshSerial;
    
    /**
     * List of versioning systems changed.
     */
    private void refreshVersioningSystems() {
        Collection<? extends VCSSystemProvider> providers = Lookup.getDefault().lookupAll(VCSSystemProvider.class);
        synchronized(versioningSystems) {
            // inline unloadVersioningSystems();
            for (VCSSystemProvider.VersioningSystem system : versioningSystems) {
                system.removePropertyCL(this);
            }
            versioningSystems.clear();
            for (VCSSystemProvider p : providers) {
                Collection<VCSSystemProvider.VersioningSystem> systems = p.getVersioningSystems();
                localHistory = null;
                // inline unloadVersioningSystems();

                // inline loadVersioningSystems(systems);
                versioningSystems.addAll(systems);
                for (VCSSystemProvider.VersioningSystem system : versioningSystems) {
                    if (localHistory == null && system.isLocalHistory()) {
                        localHistory = system;
                    }
                    system.addPropertyCL(this);
                }
                // inline loadVersioningSystems(systems);
            }
        }

        flushFileOwnerCache();
        fireFileStatusChanged(null);
        VersioningAnnotationProvider.refreshAllAnnotations();
    }

    InterceptionListener getInterceptionListener() {
        return filesystemInterceptor;
    }

    private void fireFileStatusChanged(Set<File> files) {
        // pushing the change ... DiffSidebarManager may as well listen for changes
        propertyChangeSupport.firePropertyChange(EVENT_STATUS_CHANGED, files, null);
    }
    
    void flushFileOwnerCache() {
        synchronized(folderOwners) {
            folderOwners.clear();
        }
        synchronized(fileOwners) {
            fileOwners.clear();
        }
        
    }

    public void flushNullOwners() {
        synchronized(folderOwners) {
            flushNullOwners(folderOwners);
        }
        synchronized(fileOwners) {
            flushNullOwners(fileOwners);
        }
    }
    
    private void flushNullOwners(Map<VCSFileProxy, VersioningSystem> map) {
        Iterator<VCSFileProxy> it = map.keySet().iterator();
        while(it.hasNext()) {
            if(map.get(it.next()).equals(NULL_OWNER)) {
                it.remove();
            }
        }
    }

    VersioningSystem[] getVersioningSystems() {
        synchronized(versioningSystems) {
            return versioningSystems.toArray(new VersioningSystem[versioningSystems.size()]);
        }
    }

    /**
     * Determines versioning systems that manage files in given context.
     * 
     * @param ctx VCSContext to examine
     * @return VersioningSystem systems that manage this context or an empty array if the context is not versioned
     */
    VersioningSystem[] getOwners(VCSContext ctx) {
        Set<VCSFileProxy> files = ctx.getRootFiles();
        Set<VersioningSystem> owners = new HashSet<VersioningSystem>();
        for (VCSFileProxy file : files) {
            VersioningSystem vs = getOwner(file);
            if (vs != null) {
                owners.add(vs);
            }
        }
        return (VersioningSystem[]) owners.toArray(new VersioningSystem[owners.size()]);
    }

    /**
     * Determines the versioning system that manages given file.
     * Owner of a file:
     * - annotates its label in explorers, editor tab, etc.
     * - provides menu actions for it
     * - supplies "original" content of the file
     * 
     * Owner of a file may change over time (one common example is the Import command). In such case, the appropriate 
     * Versioning System is expected to fire the PROP_VERSIONED_ROOTS property change. 
     * 
     * @param file a file
     * @return VersioningSystem owner of the file or null if the file is not under version control
     */
    public VersioningSystem getOwner(VCSFileProxy file) {
        return getOwner(file, null);
    }
    
    /**
     * Determines the versioning system that manages given file.
     * Owner of a file:
     * - annotates its label in explorers, editor tab, etc.
     * - provides menu actions for it
     * - supplies "original" content of the file
     * 
     * Owner of a file may change over time (one common example is the Import command). In such case, the appropriate 
     * Versioning System is expected to fire the PROP_VERSIONED_ROOTS property change. 
     * 
     * @param file a file
     * @param isFile flag to avoid unnecessary disk access if information already available. Determines
     *        whether the given file is a file or directory.
     * @return VersioningSystem owner of the file or null if the file is not under version control
     */
    public VersioningSystem getOwner(VCSFileProxy file, Boolean isFile) {
        LOG.log(Level.FINE, "looking for owner of {0}", file);
                
        /**
         * minor speed optimization, file.isFile may last a while, so try to acquire
         * the owner from fileOwner or folderOwners directly before file.isFile call
         * otherwise the owner will be acquired after a file.isFile call
         */
        VersioningSystem owner = null;
        synchronized(fileOwners) {
            owner = fileOwners.get(file);
        }
        if(owner == null) {
            synchronized(folderOwners) {
                owner = folderOwners.get(file);
            }
        }
        if (owner != null) {
            if (owner == NULL_OWNER) {
                LOG.log(Level.FINE, " cached NULL_OWNER of {0}", new Object[] { file });
                return null;
            }
            LOG.log(Level.FINE, " cached owner {0} of {1}", new Object[] { owner.getClass().getName(), file });
            return owner;
        }

        VCSFileProxy folder = file;
        
        if(isFile == null) {
            isFile = file.isFile();
        }
        
        if (isFile) {
            folder = file.getParentFile();
            if (folder == null) {
                LOG.log(Level.FINE, " null parent");
                return null;
            }
            synchronized(folderOwners) {
                owner = folderOwners.get(folder);
            }
        }

        if (owner == null && VersioningSupport.isExcluded(folder)) {
            // the owner is not known yet and the folder is excluded/unversioned
            LOG.log(Level.FINE, " caching NULL_OWNER of excluded {0}", new Object[] { file }); //NOI18N
            if (isFile) {
                synchronized(fileOwners) {
                    fileOwners.put(folder, NULL_OWNER);
                }
            }
            synchronized(folderOwners) {
                folderOwners.put(folder, NULL_OWNER);
            }
            return null;
        } else if (owner != null) {
            synchronized(fileOwners) {
                LOG.log(Level.FINE, " caching owner {0} of {1}", new Object[] { owner != null ? owner.getClass().getName() : null, file }) ;
                fileOwners.put(file, owner != null ? owner : NULL_OWNER);            
            }           
            if (owner == NULL_OWNER) {
                LOG.log(Level.FINE, " cached NULL_OWNER of {0}", new Object[] { folder });
                return null;
            }            
            LOG.log(Level.FINE, " cached owner {0} of {1}", new Object[] { owner.getClass().getName(), folder });                         
            return owner;
        }        
        
        // no owner known yet - lets ask all registered VersioningSystem-s
        VCSFileProxy closestParent = null;

        VersioningSystem[] vs = getVersioningSystems();
        for (VersioningSystem system : vs) {
            if (system != localHistory) {    // currently, local history is never an owner of a file
                VCSFileProxy topmost = system.getTopmostManagedAncestor(folder);
                LOG.log(Level.FINE, " {0} returns {1} ", new Object[] { system.getClass().getName(), topmost }) ;
                if (topmost != null && (closestParent == null || Utils.isAncestorOrEqual(closestParent, topmost))) {
                    if (VersioningConfig.getDefault().isDisconnected(system, topmost)) {
                        // repository root is disconnected from this vcs
                        LOG.log(Level.FINE, " skipping disconnected owner = {0} for {1}", new Object[] { 
                            system.getClass().getName(), topmost }) ;
                    } else {
                        LOG.log(Level.FINE, " owner = {0}", new Object[] { system.getClass().getName() }) ;
                        owner = system;
                        closestParent = topmost;
                    }
                }
            }
        }
                
        synchronized(folderOwners) {
            if (owner != null) {
                LOG.log(Level.FINE, " caching owner {0} of {1}", new Object[] { owner != null ? owner.getClass().getName() : null, folder }) ;
                folderOwners.put(folder, owner);
            } else {
                // nobody owns the folder => all parents aren't owned
                while(folder != null) {
                    LOG.log(Level.FINE, " caching unversioned folder {0}", new Object[] { folder }) ;
                    folderOwners.put(folder, NULL_OWNER);
                    folder = folder.getParentFile();
                }
            }
        }
        synchronized(fileOwners) {
            LOG.log(Level.FINE, " caching owner {0} of {1}", new Object[] { owner != null ? owner.getClass().getName() : null, file }) ;
            fileOwners.put(file, owner != null ? owner : NULL_OWNER);            
        }
        LOG.log(Level.FINE, "owner = {0}", new Object[] { owner != null ? owner.getClass().getName() : null }) ;
        return owner;
    }
    
    /**
     * Returns local history module that handles the given file.
     * 
     * @param file the file to examine
     * @return VersioningSystem local history versioning system or null if there is no local history for the file
     */
    VersioningSystem getLocalHistory(VCSFileProxy file) {
        return getLocalHistory(file, null);
    }
    
    /**
     * Returns local history module that handles the given file.
     * 
     * @param file the file to examine
     * @param isFile flag to avoid unnecessary disk access if information already available. Determines
     *        whether the given file is a file or directory.
     * @return VersioningSystem local history versioning system or null if there is no local history for the file
     */
    VersioningSystem getLocalHistory(VCSFileProxy file, Boolean isFile) {
                
        VersioningSystem lh = localHistory;
        if (lh == null) return null;

        String nbUserdir = System.getProperty("netbeans.user", ""); //NOI18N
        if (!nbUserdir.isEmpty() && !Utils.isVersionUserdir() && Utils.isAncestorOrEqual(VCSFileProxy.createFileProxy(new File(nbUserdir)), file)) { 
            return null;
        }
        
        synchronized(localHistoryFiles) {
            Boolean isManagedByLocalHistory = localHistoryFiles.get(file);
            if (isManagedByLocalHistory != null && isManagedByLocalHistory) {
                return lh;
            }
        }
        VCSFileProxy folder = file;
        if(isFile == null) {
            isFile = file.isFile();
        }
        if (isFile) {
            folder = file.getParentFile();
            if (folder == null) return null;
        }

        synchronized(localHistoryFiles) {
            Boolean isManagedByLocalHistory = localHistoryFiles.get(folder);
            if (isManagedByLocalHistory != null) {
                return isManagedByLocalHistory ? lh : null;
            }
        }

        boolean isManaged = lh.getTopmostManagedAncestor(folder) != null;
        if (isManaged) {
            putLocalHistoryFile(Boolean.TRUE, folder);
            return lh;
        } else {
            isManaged = lh.getTopmostManagedAncestor(file) != null;
            putLocalHistoryFile(isManaged, file);
            return isManaged ? lh : null;
        }        
    }

    private void putLocalHistoryFile(Boolean b, VCSFileProxy... files) {
        synchronized(localHistoryFiles) {
            if(localHistoryFiles.size() > 1500) {
                Iterator<VCSFileProxy> it = localHistoryFiles.keySet().iterator();
                for (int i = 0; i < 150; i++) {
                    it.next();
                    it.remove();
                }
            }
            for (VCSFileProxy file : files) {
                localHistoryFiles.put(file, b);
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        refreshVersioningSystems();
    }

    /**
     * Versioning status or other parameter changed. 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (EVENT_STATUS_CHANGED.equals(evt.getPropertyName())) {
            Set<File> files = (Set<File>) evt.getNewValue();
            VersioningAnnotationProvider.instance.refreshAnnotations(files);
            fireFileStatusChanged(files);
        } else if (EVENT_ANNOTATIONS_CHANGED.equals(evt.getPropertyName())) {
            Set<File> files = (Set<File>) evt.getNewValue();
            VersioningAnnotationProvider.instance.refreshAnnotations(files);
        } else if (EVENT_VERSIONED_ROOTS.equals(evt.getPropertyName())) {
            if(evt.getSource() instanceof VersioningSystem) {
                versionedRootsChanged((VersioningSystem) evt.getSource());
            } else {
                versionedRootsChanged(null);
            }
        }
    }

    public void versionedRootsChanged() {
        versionedRootsChanged(null);
    }
    
    public void versionedRootsChanged(VersioningSystem owner) {
        if(owner != null && owner == localHistory) {
            synchronized(localHistoryFiles) {
                localHistoryFiles.clear();
            }
        } else {
            flushFileOwnerCache();
            fireFileStatusChanged(null);
        }
    }
    
    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        VersioningAnnotationProvider.instance.refreshAnnotations(null);
    }

    /**
     * Determines if the given methodName is implemented by local histories {@link VCSInterceptor}
     *
     * @param methodName
     * @return <code>true</code> if the given methodName is implemented by local histories {@link VCSInterceptor}
     * otherwise <code>false</code>
     */
    boolean needsLocalHistory(String methodName) {
        boolean ret = false;
        try {
            synchronized(versioningSystems) {
                if(localHistory == null) {
                    return ret;
                }
                Set<String> s = interceptedMethods.get(localHistory.getClass().getName());
                if(s == null) {
                    s = new HashSet<String>();
                    Method[] m = localHistory.getInterceptor().getClass().getDeclaredMethods();
                    for (Method method : m) {
                        if((method.getModifiers() & Modifier.PUBLIC) != 0) {
                            s.add(method.getName());
                        }
                    }
                    interceptedMethods.put(localHistory.getClass().getName(), s);
                }
                ret = s.contains(methodName);
                return ret;
            }
        } finally {
            LOG.log(Level.FINE, "needsLocalHistory method [{0}] returns {1}", new Object[] {methodName, ret});
        }
    }

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
}
