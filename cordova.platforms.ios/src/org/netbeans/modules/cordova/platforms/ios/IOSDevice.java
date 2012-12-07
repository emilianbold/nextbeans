/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cordova.platforms.ios;

import java.io.IOException;
import java.util.Properties;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cordova.platforms.Device;
import org.netbeans.modules.cordova.platforms.MobilePlatform;
import org.netbeans.modules.cordova.platforms.PlatformManager;
import org.netbeans.modules.cordova.platforms.ProcessUtils;
import org.netbeans.modules.cordova.platforms.PropertyProvider;
import org.netbeans.modules.web.clientproject.spi.platform.ProjectConfigurationCustomizer;
import org.netbeans.spi.project.ActionProvider;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Becicka
 */
public enum IOSDevice implements Device {
    
    IPHONE("iPhone", "--family iphone"), //NOI18N
    IPHONE_RETINA("iPhone (Retina)", "--family iphone --retina"), //NOI18N
    IPAD("iPad", "--family ipad"), //NOI18N
    IPAD_RETINA("iPad (Retina)", "--family ipad --retina"); //NOI18N
    
    String displayName;
    String args;

    IOSDevice(String name, String args) {
        this.displayName = name;
        this.args = args;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArgs() {
        return args;
    }

    @Override
    public boolean isEmulator() {
        return true;
    }

    @Override
    public MobilePlatform getPlatform() {
        return PlatformManager.getPlatform(PlatformManager.IOS_TYPE);
    }

    @Override
    public void addProperties(Properties props) {
        props.put("ios.sim.exec", getPlatform().getSimulatorPath());//NOI18N
        props.put("ios.device.args", getArgs());//NOI18N
    }

    @Override
    public ActionProvider getActionProvider(Project p) {
        return new IOSActionProvider(p);
    }

    @Override
    public ProjectConfigurationCustomizer getProjectConfigurationCustomizer(Project project, PropertyProvider aThis) {
        return new IOSConfigurationPanel.IOSConfigurationCustomizer(project, aThis);
    }
    
    @Override
    public void openUrl(String url) {
        try {
            String sim = InstalledFileLocator.getDefault().locate("bin/ios-sim", "org.netbeans.modules.cordova.platforms.ios", false).getPath();
            String a = ProcessUtils.callProcess(sim, false, "launch", "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/" + getIPhoneSimName() +".sdk/Applications/MobileSafari.app", "--args", "-u", url); //NOI18N
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String getIPhoneSimName() {
        return getPlatform().getPrefferedTarget().getIdentifier().replace("p","P").replace("s", "S");
    }
}