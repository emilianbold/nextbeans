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
package org.netbeans.modules.cordova.template;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyledDocument;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.cordova.CordovaPlatform;
import org.netbeans.modules.cordova.android.AndroidPlatform;
import org.netbeans.modules.cordova.ios.IOSPlatform;
import org.netbeans.modules.cordova.project.ConfigUtils;
import org.netbeans.modules.web.clientproject.spi.SiteTemplateImplementation;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@NbBundle.Messages({"LBL_Name=Cordova Project"})
@ServiceProvider(service = SiteTemplateImplementation.class, position = 1000)
public class CordovaTemplate implements SiteTemplateImplementation {

    @Override
    public String getName() {
        return Bundle.LBL_Name();
    }

    @Override
    public void apply(AntProjectHelper helper, ProgressHandle handle) {
        try {
            FileObject p = helper.getProjectDirectory();
            File examplesFolder = new File(CordovaPlatform.getDefault().getSdkLocation() + "/lib/android/example/assets/www");
            FileObject examples = FileUtil.toFileObject(examplesFolder);
            FileObject index = FileUtil.copyFile(examples.getFileObject("index.html"), p, "index");
            FileUtil.copyFile(examples.getFileObject("main.js"), p, "main");
            FileUtil.copyFile(examples.getFileObject("master.css"), p, "master");
            DataObject find = DataObject.find(index);
            EditorCookie c = find.getLookup().lookup(EditorCookie.class);
            StyledDocument openDocument = c.openDocument();
            String version = CordovaPlatform.getDefault().getVersion();
            final String cordova = "cordova-" + version + ".js";
            int start = openDocument.getText(0, openDocument.getLength()).indexOf(cordova);
            openDocument.remove(start, cordova.length());
            openDocument.insertString(start, "js/libs/Cordova-" + version + "/" + cordova, null);
            find.getCookie(SaveCookie.class).save();

            EditableProperties ios = new EditableProperties(true);
            ios.put("display.name", "iPhone Simulator");
            ios.put("type", IOSPlatform.TYPE);
            ios.put("device", "emulator");
            ConfigUtils.createConfigFile(p, "ios", ios);

            EditableProperties androide = new EditableProperties(true);
            androide.put("display.name", "Android Emulator");
            androide.put("type", AndroidPlatform.TYPE);
            androide.put("device", "emulator");
            ConfigUtils.createConfigFile(p, "android", androide);

            EditableProperties androidd = new EditableProperties(true);
            androidd.put("display.name", "Android Device");
            androidd.put("type", AndroidPlatform.TYPE);
            androidd.put("device", "device");
            ConfigUtils.createConfigFile(p, "android", androidd);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Throwable ex) {
            
        }
    }


    @Override
    public String getDescription() {
        return "Cordova Template";
    }

    @Override
    public boolean isPrepared() {
        return true;
    }

    @Override
    public void prepare() throws IOException {
    }

    @Override
    public Collection<String> supportedLibraries() {
        return Collections.singletonList("Cordova");
    }

    private class SiteTemplateCustomizerImpl implements /*SiteTemplateCustomizer,*/ PropertyChangeListener {

        public SiteTemplateCustomizerImpl() {
            CordovaPlatform.getDefault().addPropertyChangeListener(this);
        }
        private CordovaTemplatePanel panel;
        private transient final ChangeSupport changeSupport = new ChangeSupport(this);

        //@Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        //@Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        //@Override
        public JComponent getComponent() {
            if (panel == null) {
                panel = new CordovaTemplatePanel();
            }
            return panel;
        }

        //@Override
        public boolean isValid() {
            return CordovaPlatform.getDefault().getSdkLocation() != null;
        }

        //@Override
        public String getErrorMessage() {
            return "Mobile Platforms not Configured";
        }

        //@Override
        public String getWarningMessage() {
            return null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            changeSupport.fireChange();
            panel.update();
        }
    }
}