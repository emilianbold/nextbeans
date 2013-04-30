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

package org.netbeans.modules.cordova.project;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cordova.platforms.MobileProjectExtender;
import org.netbeans.modules.web.browser.api.WebBrowser;
import org.netbeans.modules.web.clientproject.spi.platform.ClientProjectEnhancedBrowserImplementation;
import org.netbeans.modules.web.clientproject.spi.platform.ProjectConfigurationCustomizer;
import org.netbeans.modules.web.clientproject.spi.platform.RefreshOnSaveListener;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.openide.util.Exceptions;

/**
 * PhoneGap pseudo browser
 * @author Jan Becicka
 */
public class EnhancedBrowserImpl implements ClientProjectEnhancedBrowserImplementation {

    final private Project project;
    private MobileConfigurationsProvider configsProvider;

    EnhancedBrowserImpl(Project project, WebBrowser browser) {
        this.project = project;
        configsProvider = new MobileConfigurationsProvider(project);
    }

    @Override
    public void save() {
        // this should save changes in UI for particular configuration
    }

    @Override
    public RefreshOnSaveListener getRefreshOnSaveListener() {
        return new RefreshListener(/*???*/);
    }

    @Override
    public ActionProvider getActionProvider() {
        final MobileConfigurationImpl activeConfiguration = configsProvider.getActiveConfiguration();
        if (activeConfiguration == null) {
            return null;
        }
        return activeConfiguration.getDevice().getActionProvider(project);
    }

    @Override
    public ProjectConfigurationCustomizer getProjectConfigurationCustomizer() {
        return null;
    }

    @Override
    public void deactivate() {
    }

    @Override
    public boolean isHighlightSelectionEnabled() {
        return true;
    }

    @Override
    public ProjectConfigurationProvider getProjectConfigurationProvider() {
        try {
            MobileProjectExtender.createMobileConfigs(project.getProjectDirectory());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return configsProvider;
    }

}
