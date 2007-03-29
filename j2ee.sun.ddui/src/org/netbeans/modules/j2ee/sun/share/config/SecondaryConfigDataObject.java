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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.sun.share.config;

import java.util.Collections;
import java.util.Set;
import javax.enterprise.deploy.spi.exceptions.ConfigurationException;
//import org.netbeans.modules.j2ee.deployment.plugins.api.ConfigurationSupport;

import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.MultiFileLoader;

import org.netbeans.modules.j2ee.sun.share.config.ui.ConfigBeanTopComponent;
import org.netbeans.modules.j2ee.sun.share.configbean.SunONEDeploymentConfiguration;
import org.openide.filesystems.FileUtil;


/**
 * @author nn136682
 */
public class SecondaryConfigDataObject extends ConfigDataObject {
    
    private ConfigDataObject primary;
    
    /** Creates a new instance of SecondaryConfigDataObject */
    public SecondaryConfigDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
    }
    
    public boolean isSecondaryOf(ConfigDataObject primary) {
        return getPrimaryDataObject() == primary;
    }
    
    public SunONEDeploymentConfiguration getDeploymentConfiguration() throws ConfigurationException {
        // Request deployment configuration for SJSAS from j2eeserver module
        ConfigDataObject p = getPrimaryDataObject();
        FileObject fo = p.getPrimaryFile();
        String serverId = getProvider().getServerID();
        // DDBean Removal
        //ConfigurationSupport.requestCreateConfiguration(fo, serverId);
        return SunONEDeploymentConfiguration.getConfiguration(FileUtil.toFile(fo));
    }
    
    private ConfigDataObject getPrimaryDataObject() {
        if (primary == null || !primary.isValid()) {
            // The only JSR-88 secondary configuration file supported by SJSAS 8.x 
            // or 9.x is sun-cmp-mappings.xml which is secondary for sun-ejb-jar.xml
            // AND that they will always reside in the same directory.  So we can find
            // the primary data object by doing a find("sun-ejb-jar.xml") here.
            FileObject folder = getPrimaryFile().getParent();
            FileObject sejFO = folder.getFileObject("sun-ejb-jar", "xml");
            if(sejFO != null) {
                try {
                    DataObject dObj = DataObject.find(sejFO);
                    primary = (ConfigDataObject) dObj.getCookie(ConfigDataObject.class);
                    if(primary != null) {
                        primary.addSecondary(this);
                    }
                } catch(DataObjectNotFoundException ex) {
                }
            }
        }
        
        return primary;
    }
    
    private EditCookie _getEditCookie() {
        ConfigDataObject cdo = getPrimaryDataObject();
        EditCookie primaryEdit = cdo == null ? null : cdo.getEditCookie();
        EditCookie myEdit = super.getEditCookie();
        if (primaryEdit != null) {
            return myEdit;
        } else {
            return null;
        }
    }

    private OpenCookie _getOpenCookie() {
        ConfigDataObject cdo = getPrimaryDataObject();
        return cdo == null ? null : (OpenCookie) cdo.getCookie(OpenCookie.class);
    }

    public org.openide.nodes.Node.Cookie getCookie(Class c) {
        if (OpenCookie.class.isAssignableFrom(c)) {
            return _getOpenCookie();
        } else if (EditCookie.class.isAssignableFrom(c)) {
            return _getEditCookie();
        }
        return super.getCookie(c);
    }
    
    protected Set getSecondaries() {
        return Collections.EMPTY_SET;
    }
    
    protected ConfigurationStorage getStorage() {
        ConfigDataObject cdo = getPrimaryDataObject();
        return cdo == null ? null : cdo.getStorage();
    }
    
    protected void openConfigEditor() {
        ConfigDataObject cdo = getPrimaryDataObject();
        if (cdo != null) {
            getPrimaryDataObject().openConfigEditor();
            firePropertyChange(PROP_COOKIE, null, null);
        }
    }
    
    protected ConfigBeanTopComponent findOpenedConfigEditor() {
        ConfigDataObject cdo = getPrimaryDataObject();
        return cdo == null ? null : cdo.findOpenedConfigEditor();
    }
    
    public boolean closeConfigEditors() {
        ConfigDataObject cdo = getPrimaryDataObject();
        return cdo == null ? false : cdo.closeConfigEditors();
    }
    
    public void fileDeleted(org.openide.filesystems.FileEvent fe) {
        if (fe.getFile().equals(this.getPrimaryFile()) && getPrimaryDataObject() != null) {
            primary.removeSecondary(this);
        }
    }

    protected void fireCookieChange() {
        fireLimitedCookieChange();
        ConfigDataObject cdo = getPrimaryDataObject();
        if (cdo != null) {
            cdo.fireLimitedCookieChange();
        }
    }

    //warn: is called from primary ConfigDataObject, don't delegate back
    public void setChanged() {
        addSaveCookie(new S0());
    }

   //No actual save until we have SPI to notify individual config descriptor change.
    private class S0 implements SaveCookie {
        public void save() throws java.io.IOException {
            ConfigDataObject cdo = getPrimaryDataObject();
            if (cdo != null) {
                cdo.resetAllChanged();
            }
        }
    }
}
