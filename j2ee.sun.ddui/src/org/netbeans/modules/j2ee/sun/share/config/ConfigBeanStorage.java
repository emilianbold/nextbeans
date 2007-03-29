/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.sun.share.config;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import javax.enterprise.deploy.model.DDBean;
import javax.enterprise.deploy.model.XpathEvent;
import javax.enterprise.deploy.spi.DConfigBean;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.exceptions.BeanNotFoundException;
import javax.enterprise.deploy.spi.exceptions.ConfigurationException;
import org.netbeans.modules.j2ee.sun.share.config.ui.ConfigBeanNode;
import org.netbeans.modules.j2ee.sun.share.configbean.Base;
import org.netbeans.modules.j2ee.sun.share.configbean.DConfigBeanProperties;
import org.netbeans.modules.j2ee.sun.share.configbean.SunONEDeploymentConfiguration;
import org.openide.ErrorManager;
import org.openide.nodes.Node;

/**
 */
public class ConfigBeanStorage implements PropertyChangeListener, Comparable {
    
    private ConfigurationStorage storage;
    DConfigBean bean;
    private ConfigBeanStorage parent;
    private ConfigBeanNode node = null;
    private Map childMap = new HashMap();
    
    public ConfigBeanStorage(DConfigBean bean, ConfigBeanStorage parent, ConfigurationStorage storage) throws ConfigurationException{
        this.bean = bean;
        this.parent = parent;
        this.storage = storage;
        // need to ensure that the basebean events are caught?
        // synchronize new CBS and event handling
        initChildren();
        // store itself in its BaseBean.
        StandardDDImpl dd = (StandardDDImpl) bean.getDDBean();
        dd.proxy.addConfigBean(this);
        // PENDING set up listener on dd to implement notify()
        
        bean.addPropertyChangeListener(this);
    }
    
    public Map getChildMap() {
        return childMap;
    }
    
    public ConfigurationStorage getStorage() {
        return storage;
    }
    
    public synchronized Node getNode() {
        //if (node == null)
        node = new ConfigBeanNode(this);
        return node;
    }
    
    public void propertyChange(PropertyChangeEvent pce) {
        if (storage != null) {
            // Only set changed on true change events (as opposed to DISPLAY_NAME, which can
            // be changed by a validation event and is not associated with a change of persistable data.)
            if(!DConfigBeanProperties.PROP_DISPLAY_NAME.equalsIgnoreCase(pce.getPropertyName())) {
                storage.setChanged();
            }
        }
        if (DConfigBeanProperties.PROP_DISPLAY_NAME.equalsIgnoreCase(pce.getPropertyName())) {
            if(node != null) {
                node.setDisplayName((String) pce.getNewValue());
            }
        }
    }
    
    public void remove() {
        DDCommon dd = (DDCommon) ((StandardDDImpl)bean.getDDBean()).proxy;
        dd.removeConfigBean(this);
        if(parent != null) {
            try {
                parent.bean.removeDConfigBean(bean);
                storage.setChanged();
            } catch (BeanNotFoundException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            } catch (RuntimeException re) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, re);
            }
        }
    }
    
    private void initChildren() throws ConfigurationException {
        String[] xpaths = bean.getXpaths();
        if(xpaths != null) {
            for(int i = 0; i < xpaths.length; i++) {
                DDBean[] beans = bean.getDDBean().getChildBean(xpaths[i]);
                for(int j = 0; j < beans.length; j++) {
                    addChild((StandardDDImpl) beans[j]);
                }
            }
        }
    }
    
    void fireEvent(String relPath, XpathEvent xe) throws ConfigurationException {
        String[] xpaths = bean.getXpaths();
        if(xpaths != null) {
            StandardDDImpl eventDD = (StandardDDImpl) xe.getBean();
            StandardDDImpl[] targetDDs = calculateTargetDDs(eventDD, relPath, xe, xpaths);
            if (targetDDs != null) {
                for (int i=0; i<targetDDs.length; i++) {
                    if (xe.isAddEvent()) {
                        addChild(targetDDs[i]);
                    } else {
                        removeChild(targetDDs[i]);
                    }
                }
            }
        }
    }

    private StandardDDImpl[] calculateTargetDDs(final StandardDDImpl eventDD, final String relPath, final XpathEvent xe, final String[] xpaths) {
        StandardDDImpl targetDDs[] = null;
        for(int i = 0 ; i < xpaths.length; i++) {
            if(xpaths[i].equals(relPath) || xpaths[i].equals(xe.getBean().getXpath())) {
                targetDDs = new StandardDDImpl[] { eventDD };
                break;
            }
        }
        HashSet targetSet = new HashSet();
        for (int i=0; targetDDs == null && i < xpaths.length; i++) {
            // IZ 73849 - Make sure that relative path is a true xpath prefix which means
            // that if xpath[i] starts with, but is longer than the relative path, then the
            // character immediately following the prefix must be a slash.  i.e.
            //
            //   xpath == relpath   OR
            //   xpath == relpath + "/" + remainder of xpath
            //
            if(xpaths[i].startsWith(relPath) &&
                    (xpaths[i].length() == relPath.length() || xpaths[i].charAt(relPath.length()) == '/')) {
                String targetPath = DDCommon.getRelativePath(xpaths[i], relPath);
                DDBean[] dds = eventDD.getChildBean(targetPath);
                if (dds == null) {
                    continue;
                }
                for (int j=0; j<dds.length; j++) {
                    if (!(dds[j] instanceof StandardDDImpl)) {
                        continue;
                    }
                    targetSet.add(dds[j]);
                }
                if (targetSet.size() > 0) {
                    targetDDs = (StandardDDImpl[]) targetSet.toArray(new StandardDDImpl[targetSet.size()]);
                }
            }
        }
        return targetDDs;
    }
    
    public static final String RESOURCE_REF = "resource-ref"; //NOI18N
    
    private void addChild(StandardDDImpl dd) throws ConfigurationException {
        DConfigBean cb = bean.getDConfigBean(dd);
        if(cb == null) {
            return;
        }
        if (RESOURCE_REF.equals(dd.proxy.dtdname)) {
            DeploymentConfiguration dc = storage.getDeploymentConfiguration();
            if(dc instanceof SunONEDeploymentConfiguration) {
                SunONEDeploymentConfiguration config = (SunONEDeploymentConfiguration) dc;
                config.ensureResourceDefined(dd);
            }
        }
        ConfigBeanStorage cbs = new ConfigBeanStorage(cb, this, storage);
        Collection c = (Collection) childMap.get(dd.getXpath());
        if(c == null) {
            c = new TreeSet();
            childMap.put(dd.getXpath(), c);
        }
        c.add(cbs);
        fireChildBeanAddedEvent(cbs);
    }
    
    private void removeChild(DDBean remBean) {
        Collection c = (Collection) childMap.get(remBean.getXpath());
        if(c == null) {
            return;
        }
        for(Iterator i = c.iterator(); i.hasNext(); ) {
            ConfigBeanStorage cbs = (ConfigBeanStorage) i.next();
            if (cbs.bean.getDDBean().equals(remBean)) {
                cbs.remove();
                i.remove();
                fireChildBeanRemovedEvent(cbs);
            }
        }
    }
    
    /** Ordering for ConfigBeanStorage intances.  This is so we can use TreeSet or
     *  some other ordered set to store these objects and offer a reasonable ordering
     *  in the Configuration Editor UI.
     */
    public int compareTo(Object o) {
        if(o == null) {
            throw new NullPointerException("Null argument passed to ConfigBeanStorage.compareTo()"); // NOI18N
        }
        
        ConfigBeanStorage target = (ConfigBeanStorage) o;
        int result = -1;
        if(this == o) {
            result = 0;
        } else if(bean instanceof Base && target.bean instanceof Base) {
            Base sourceBean = (Base) bean;
            Base targetBean = (Base) target.bean;
            
            result = sourceBean.getDisplayName().compareTo(targetBean.getDisplayName());
            
            if(result == 0) {
                result = sourceBean.getIdentity().compareTo(targetBean.getIdentity());
            }
        } else {
            throw new IllegalStateException("Source or target bean is null or not derived from Base."); // NOI18N
        }
        
        return result;
    }
    
    public DConfigBean getConfigBean() {
        return bean;
    }
    
    public static interface ChildrenChangeListener {
        public void childBeanAdded(ConfigBeanStorage childBeanStorage);
        public void childBeanRemoved(ConfigBeanStorage childBeanStorage);
    }
    
    private List childrenChangeListeners = new Vector();
    public void addChildrenChangeListener(ChildrenChangeListener l) {
        childrenChangeListeners.add(l);
    }
    public void removeChildrenChangeListener(ChildrenChangeListener l) {
        childrenChangeListeners.remove(l);
    }
    private void fireChildBeanAddedEvent(ConfigBeanStorage childBeanStorage) {
        for (Iterator i=childrenChangeListeners.iterator(); i.hasNext();) {
            ChildrenChangeListener l = (ChildrenChangeListener) i.next();
            l.childBeanAdded(childBeanStorage);
        }
    }
    private void fireChildBeanRemovedEvent(ConfigBeanStorage childBeanStorage) {
        for (Iterator i=childrenChangeListeners.iterator(); i.hasNext();) {
            ChildrenChangeListener l = (ChildrenChangeListener) i.next();
            l.childBeanRemoved(childBeanStorage);
        }
    }
    
}
