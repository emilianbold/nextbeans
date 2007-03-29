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

package org.netbeans.modules.tomcat5.ide;

import java.io.File;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.api.AppChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.tomcat5.TomcatManager;
import org.netbeans.modules.tomcat5.TomcatManagerImpl;
import org.netbeans.modules.tomcat5.TomcatModule;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.tomcat5.progress.ProgressEventSupport;
import org.netbeans.modules.tomcat5.progress.Status;

/**
 *
 * @author  Pavel Buzek
 */
public class TomcatIncrementalDeployment extends IncrementalDeployment {
    
    private TomcatManager tm;
    
    /** Creates a new instance of TomcatIncrementaDeployment */
    public TomcatIncrementalDeployment (DeploymentManager dm) {
        this.tm = (TomcatManager) dm;
    }
    
    public boolean canFileDeploy (Target target, J2eeModule j2eeModule) {
        return j2eeModule.getModuleType().equals (javax.enterprise.deploy.shared.ModuleType.WAR);
        
    }
    
    public File getDirectoryForModule (TargetModuleID module) {
        return null;
        /*TomcatModule tModule = (TomcatModule) module;
        String moduleFolder = tm.getCatalinaBaseDir ().getAbsolutePath ()
        + System.getProperty("file.separator") + "webapps"   //NOI18N
        + System.getProperty("file.separator") + tModule.getPath ().substring (1); //NOI18N
        File f = new File (moduleFolder);
        return f;*/
    }
    
    public File getDirectoryForNewApplication (Target target, J2eeModule module, ModuleConfiguration configuration) {
        if (module.getModuleType().equals (ModuleType.WAR)) {
            return null;
            /*if (configuration instanceof WebappConfiguration) {
                String moduleFolder = tm.getCatalinaBaseDir ().getAbsolutePath ()
                + System.getProperty("file.separator") + "webapps"   //NOI18N
                + System.getProperty("file.separator") + ((WebappConfiguration)configuration).getPath ().substring (1);  //NOI18N
                File f = new File (moduleFolder);
                return f;
            }*/
        }
        throw new IllegalArgumentException ("ModuleType:" + module == null ? null : module.getModuleType() + " Configuration:"+configuration); //NOI18N
    }
    
    public java.io.File getDirectoryForNewModule (java.io.File appDir, String uri, J2eeModule module, ModuleConfiguration configuration) {
        throw new UnsupportedOperationException ();
    }
    
    public ProgressObject incrementalDeploy (final TargetModuleID module, AppChangeDescriptor changes) {
        if (changes.descriptorChanged () || changes.serverDescriptorChanged () || changes.classesChanged ()) {
            TomcatManagerImpl tmi = new TomcatManagerImpl (tm);
            if (changes.serverDescriptorChanged ()) {
                new TomcatManagerImpl (tm).remove ((TomcatModule) module);
                tmi.incrementalRedeploy ((TomcatModule) module);
            } else if (changes.descriptorChanged()) {
                new TomcatManagerImpl (tm).stop((TomcatModule) module);
                tmi.start ((TomcatModule) module);
            } else {
                tmi.reload ((TomcatModule)module);
            }
            return tmi;
        } else {
            final P p = new P (module);
            p.supp.fireHandleProgressEvent (module, new Status (ActionType.EXECUTE, CommandType.DISTRIBUTE, "", StateType.COMPLETED));
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        p.supp.fireHandleProgressEvent (module, new Status (ActionType.EXECUTE, CommandType.DISTRIBUTE, "", StateType.COMPLETED));
                        
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
            return p;
        }
    }
    
    public ProgressObject initialDeploy (Target target, J2eeModule app, ModuleConfiguration configuration, File dir) {
        TomcatManagerImpl tmi = new TomcatManagerImpl (tm);
        File contextXml = new File (dir.getAbsolutePath () + "/META-INF/context.xml"); //NOI18N
        tmi.initialDeploy (target, contextXml, dir);
        return tmi;
    }
    
    public void notifyDeployment(TargetModuleID module) {
        if (tm.isTomcat50() && tm.getTomcatProperties().getOpenContextLogOnRun()) {
            tm.openLog(module);
        }
    }
    
    private static class P implements ProgressObject {
        
        ProgressEventSupport supp = new ProgressEventSupport (this);
        TargetModuleID tmid;
        
        P (TargetModuleID tmid) {
            this.tmid = tmid;
        }
        
        public void addProgressListener (javax.enterprise.deploy.spi.status.ProgressListener progressListener) {
            supp.addProgressListener (progressListener);
        }
        
        public void removeProgressListener (javax.enterprise.deploy.spi.status.ProgressListener progressListener) {
            supp.removeProgressListener (progressListener);
        }
        
        public javax.enterprise.deploy.spi.status.ClientConfiguration getClientConfiguration (javax.enterprise.deploy.spi.TargetModuleID targetModuleID) {
            return null;
        }
        
        public javax.enterprise.deploy.spi.status.DeploymentStatus getDeploymentStatus () {
            return supp.getDeploymentStatus ();
        }
        
        public javax.enterprise.deploy.spi.TargetModuleID[] getResultTargetModuleIDs () {
            return new TargetModuleID [] {tmid};
        }
        
        public boolean isCancelSupported () {
            return false;
        }
        
        public boolean isStopSupported () {
            return false;
        }
        
        public void cancel () throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException {
            throw new OperationUnsupportedException ("");
        }
        
        public void stop () throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException {
            throw new OperationUnsupportedException ("");
        }
        
    }
}
