/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.versioning.core;

import org.netbeans.modules.versioning.core.util.VCSSystemProvider;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Level;
import javax.swing.Action;
import org.netbeans.modules.versioning.core.spi.VCSAnnotator;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.versioning.core.spi.VCSInterceptor;
import org.netbeans.modules.versioning.core.spi.VCSVisibilityQuery;
import org.netbeans.modules.versioning.core.spi.VersioningSystem;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.api.VersioningSupport;
import org.netbeans.modules.versioning.core.spi.*;
import org.netbeans.spi.queries.CollocationQueryImplementation2;
import org.openide.util.ContextAwareAction;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Stupka
 */
public class DelegatingVCS extends VersioningSystem implements VCSSystemProvider.VersioningSystem<VersioningSystem> {

    private final Map<?, ?> map;
    private VersioningSystem delegate;
    private Set<String> metadataFolderNames;
    private final Object DELEGATE_LOCK = new Object();
    
    private final String displayName;
    private final String menuLabel;
    private final boolean isLocalHistory;    
    
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Caches folders known as having no metadata according to {@link #getMetadataFolderNames()}. 
     * Will be flushed at delegate instantiation.
     */
    private final Set<VCSFileProxy> unversionedParents = Collections.synchronizedSet(new HashSet<VCSFileProxy>(20));
    
    public static DelegatingVCS create(Map<?, ?> map) {
        return new DelegatingVCS(map);
    }

    private DelegatingVCS(Map<?, ?> map) {
        this.map = map;
        this.displayName = (String) map.get("displayName");
        this.menuLabel = (String) map.get("menuLabel");
        Object ilh = map.get("isLocalHistory");
        this.isLocalHistory = ilh != null ? (Boolean) map.get("isLocalHistory") : false;
        VersioningManager.LOG.log(Level.FINE, "Created DelegatingVCS for : {0}", map.get("displayName")); // NOI18N
    }

    @Override
    public VersioningSystem getDelegate() {
        VersioningManager manager = VersioningManager.getInstance();
        synchronized(DELEGATE_LOCK) {
            if(delegate == null) {
                manager.flushNullOwners();   
                delegate = (VersioningSystem) map.get("delegate");                  // NOI18N
                if(delegate != null) {
                    synchronized(support) {
                        PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
                        for (PropertyChangeListener l : listeners) {
                            delegate.addPropertyChangeListener(l);
                            support.removePropertyChangeListener(l);
                        }
                    }
                } else {
                    VersioningManager.LOG.log(Level.WARNING, "Couldn't create delegate for : {0}", map.get("displayName")); // NOI18N
                }
            }
            return delegate;
        }
    }
    
    @Override
    public void getOriginalFile(VCSFileProxy workingCopy, VCSFileProxy originalFile) {
        getDelegate().getOriginalFile(workingCopy, originalFile);
    }

    @Override
    public CollocationQueryImplementation2 getCollocationQueryImplementation() {
        return getDelegate().getCollocationQueryImplementation();
    }

    @Override
    public VCSFileProxy getTopmostManagedAncestor(VCSFileProxy file) {
        if(!isAlive()) {
            if(getMetadataFolderNames().contains(file.getName()) && file.isDirectory()) {
                VersioningManager.LOG.log(
                        Level.FINE, 
                        "will awake VCS {0} because of metadata folder {1}",// NOI18N 
                        new Object[]{displayName, file}); 

                return getDelegate().getTopmostManagedAncestor(file);
            } 
            if(hasMetadata(file)) {
                VersioningManager.LOG.log(
                        Level.FINE, 
                        "will awake VCS {0} because {1} contains matadata",     // NOI18N
                        new Object[]{displayName, file});
                
                
                return getDelegate().getTopmostManagedAncestor(file);
            }
        } else {
            return getDelegate().getTopmostManagedAncestor(file);
        }
        return null;
    }

    @Override
    public boolean isLocalHistory() {
        return isLocalHistory;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String getMenuLabel() {
        return menuLabel;
    }

    @Override
    public final void addPropertyCL(PropertyChangeListener listener) {
        if(isAlive()) {
            getDelegate().addPropertyChangeListener(listener);
        } else {
            synchronized(support) {
                support.addPropertyChangeListener(listener);
            }
        }
    }
    
    @Override
    public final void removePropertyCL(PropertyChangeListener listener) {
        if(isAlive()) {
            getDelegate().removePropertyChangeListener(listener);
        } else {        
            synchronized(support) {
                support.removePropertyChangeListener(listener);
            }
        }
    }
    
    @Override
    public boolean isExcluded(VCSFileProxy file) {
        return VersioningSupport.isExcluded(file);
    }

    @Override
    public VCSAnnotator getVCSAnnotator() {
        return getDelegate().getVCSAnnotator();
    }
    
    @Override
    public VCSVisibilityQuery getVisibilityQuery() { 
        return getDelegate().getVisibilityQuery();
    }
    
    @Override
    public VCSInterceptor getVCSInterceptor() {
        return getDelegate().getVCSInterceptor();
    }

    @Override
    public VCSHistoryProvider getVCSHistoryProvider() {
        return getDelegate().getVCSHistoryProvider();
    }
    
    @Override
    public boolean accept(VCSContext ctx) {
        return true;
    }
    
    public boolean isMetadataFile(VCSFileProxy file) {
        return getMetadataFolderNames().contains(file.getName());
    }

    private Collection<String> getMetadataFolderNames() {
        if(metadataFolderNames == null) {
            metadataFolderNames = new HashSet<String>();
            int i = 0;
            while(true) {
                String name = (String) map.get("metadataFolderName" + i++);
                if(name == null) {
                    break;
                }
                name = parseName(name);
                if(name == null) {
                    continue;
                }
                metadataFolderNames.add(name);
            }
        }
        return metadataFolderNames;
    }
    
    Action[] getActions(VCSContext ctx, VCSAnnotator.ActionDestination actionDestination) {
        if(map == null || isAlive()) {
            VCSAnnotator annotator = getVCSAnnotator();
            return annotator != null ? annotator.getActions(ctx, actionDestination) : new Action[0];
        } else {
            Action[] ia = getInitActions(ctx);
            Action[] ga = getGlobalActions(ctx);
            
            List<Action> l = new ArrayList<Action>(ia.length + ga.length + 1); // +1 if separator needed
            
            // init actions
            l.addAll(Arrays.asList(ia));
            // add separator if necessary 
            if(ga.length > 0 && ia.length > 0 && l.get(l.size() - 1) != null) {
                l.add(null); 
            }
            // global actions
            l.addAll(Arrays.asList(ga));
            
            return  l.toArray(new Action[l.size()]);
        }        
    }
    
    Action[] getGlobalActions(VCSContext ctx) {
        assert !isAlive();
        String category = (String) map.get("actionsCategory");              // NOI18N
        List<? extends Action> l = Utilities.actionsForPath("Versioning/" + category + "/Actions/Global"); // NOI18N
        List<Action> ret = new ArrayList<Action>(l.size());
        for (Action action : l) {
            if(action instanceof ContextAwareAction) {
                ret.add(((ContextAwareAction)action).createContextAwareInstance(Lookups.singleton(ctx)));
            } else {
                ret.add(action);
            }
        }        
        return ret != null ? ret.toArray(new Action[ret.size()]) : new Action[0];
    }
    
    Action[] getInitActions(VCSContext ctx) {
        String category = (String) map.get("actionsCategory");              // NOI18N
        List<? extends Action> l = Utilities.actionsForPath("Versioning/" + category + "/Actions/Unversioned"); // NOI18N
        List<Action> ret = new ArrayList<Action>(l.size());
        for (Action action : l) {
            if(action instanceof ContextAwareAction) {
                ret.add(((ContextAwareAction)action).createContextAwareInstance(Lookups.singleton(ctx)));
            } else {
                ret.add(action);
            }
        }
        return ret.toArray(new Action[ret.size()]);
    }

    boolean isAlive() {
        synchronized(DELEGATE_LOCK) {
            return delegate != null;
        }
    }
    
    private boolean hasMetadata(VCSFileProxy file) {
        if(file == null) {
            return false;
        }
        
        VersioningManager.LOG.log(Level.FINE, "looking up metadata for {0}", new Object[] { file });
        if(unversionedParents.contains(file)) {
            VersioningManager.LOG.fine(" cached as unversioned");
            return false;
        }
        
        boolean ret = false;
        Set<VCSFileProxy> done = new HashSet<VCSFileProxy>();
        for(String folderName : getMetadataFolderNames()) {
            VCSFileProxy parent;
            if(file.isDirectory()) {
                parent = file;
            } else {
                parent = file.getParentFile();
            }
            
            if(unversionedParents.contains(parent)) {
                VersioningManager.LOG.log(Level.FINE, " already known as unversioned {0}", new Object[] { file });
                break;
            }
            
            while(parent != null) {
                // is the folder a special one where metadata should not be looked for?
                boolean forbiddenFolder = org.netbeans.modules.versioning.core.util.Utils.isForbiddenFolder(parent);
                final boolean metadataFolder = !forbiddenFolder && VCSFileProxy.createFileProxy(parent, folderName).exists();
                if(metadataFolder) {
                    VersioningManager.LOG.log(
                            Level.FINER, 
                            "found metadata folder {0} for file {1}",           // NOI18N
                            new Object[]{metadataFolder, file});
                    
                    ret = true;
                } else {
                    done.add(parent);
                }
                parent = parent.getParentFile();
            }
        }
        if(!ret) {
            VersioningManager.LOG.log(Level.FINE, " storing unversioned");
            unversionedParents.addAll(done);
        }
        return ret;
    }
    
    /**
     * Testing purposes only!
     */
    void reset() {
        synchronized(DELEGATE_LOCK) {
            delegate = null;
        }
    }

    private String parseName(String name) {
        if(name == null) {
            return null;
        }
        int idx = name.indexOf(":");
        
        if(idx < 0) {
            return name;
        }
        
        String cmd[] = name.split(":");
        
        // "_svn:getenv:SVN_ASP_DOT_NET_HACK:notnull"
        if(cmd.length != 4 || !cmd[1].contains("getenv")) {
            return name;
        } else {
            assert cmd[3].equals("notnull") || cmd[3].equals("null");
            
            boolean notnull = cmd[3].trim().equals("notnull");
            if(notnull) {
                return System.getenv(cmd[2]) != null ? cmd[0] : null;
            } else {
                return System.getenv(cmd[2]) == null ? cmd[0] : null;
            }
        }
    }

}
