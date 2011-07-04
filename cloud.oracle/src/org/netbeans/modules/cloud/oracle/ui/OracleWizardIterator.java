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
package org.netbeans.modules.cloud.oracle.ui;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.cloud.common.spi.support.ui.CloudResourcesWizardPanel;
import org.netbeans.modules.cloud.oracle.OracleInstance;
import org.netbeans.modules.cloud.oracle.OracleInstanceManager;
import org.netbeans.modules.j2ee.weblogic9.tools.DomainGenerator;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 */
public class OracleWizardIterator implements WizardDescriptor.AsynchronousInstantiatingIterator<WizardDescriptor>{

    private static final String LOCAL_DOMAIN_DIR = "JavaEE/Cloud9";

    private final ChangeSupport listeners;
    private WizardDescriptor wizard;
    private OracleWizardPanel panel;
    private LocalInstancePanel panel2;
    private CloudResourcesWizardPanel panel3;
    private int count = 0;

    public OracleWizardIterator() {
        listeners = new ChangeSupport(this);
    }
    
    public static final String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N

    @Override
    public Set instantiate() throws IOException {
        String username = (String)wizard.getProperty(OracleWizardPanel.TENANT_USERNAME);
        assert username != null;
        String pwd = (String)wizard.getProperty(OracleWizardPanel.TENANT_PASSWORD);
        assert pwd != null;
        String url = (String)wizard.getProperty(OracleWizardPanel.URL_ENDPOINT);
        assert url != null;
        String name = (String)wizard.getProperty(PROP_DISPLAY_NAME);
        assert name != null;
        String tenant = (String)wizard.getProperty(OracleWizardPanel.TENANT_ID);
        assert tenant != null;
        String service = (String)wizard.getProperty(OracleWizardPanel.SERVICE_NAME);
        assert service != null;
        
        String serverDir = (String)wizard.getProperty(LocalInstancePanel.LOCAL_SERVER);

        if (serverDir != null && serverDir.trim().length() > 0) {
            File jarFo = InstalledFileLocator.getDefault().locate(
                    "modules/ext/cloud_10.3.6.0.jar", "org.netbeans.modules.libs.cloud9", false); // NOI18N
            if (jarFo == null) {
                throw new IOException("Could not find domain template");
            }
            FileObject configRoot = FileUtil.getConfigRoot();
            FileObject domainDirParent = FileUtil.createFolder(
                    configRoot, LOCAL_DOMAIN_DIR);

            FileObject domainDir = null;
            int i = 0;
            while (domainDir == null) {
                i++;
                String domainName = FileUtil.findFreeFolderName(domainDirParent,
                        "Cloud9Domain"); // NOI18N
                try {
                    domainDir = domainDirParent.createFolder(domainName);
                } catch (IOException ex) {
                    if (i > 10) {
                        throw ex;
                    }
                }
            }
            try {
                DomainGenerator.generateDomain(new File(serverDir),
                        jarFo, FileUtil.toFile(domainDir));
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                throw new IOException(ex.getCause());
            }
        }
        
        OracleInstanceManager.getDefault().add(new OracleInstance(name, username, pwd, url, tenant, service));
        
        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panel = null;
        panel2 = null;
        panel3 = null;
    }

    @Override
    public Panel current() {
        switch (count) {
            case 0:
                if (panel == null) {
                    panel = new OracleWizardPanel();
                }
                return panel;
            case 1:
                if (panel2 == null) {
                    panel2 = new LocalInstancePanel();
                }
                return panel2;
            default:
                if (panel3 == null) {
                    panel3 = new CloudResourcesWizardPanel(getPanelContentData(), 2);
                }
                return panel3;
        }
    }

    static String[] getPanelContentData() {
        return new String[] {
                NbBundle.getMessage(OracleWizardPanel.class, "LBL_ACIW_Oracle"),
                NbBundle.getMessage(OracleWizardPanel.class, "LBL_ACIW_Local"),
                NbBundle.getMessage(OracleWizardPanel.class, "LBL_ACIW_Resources")
            };
    }
    
    @Override
    public String name() {
        return "Oracle Cloud 9";
    }

    @Override
    public boolean hasNext() {
        return count < 2;
    }

    @Override
    public boolean hasPrevious() {
        return count > 0;
    }

    @Override
    public void nextPanel() {
        count++;
    }

    @Override
    public void previousPanel() {
        count--;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.removeChangeListener(l);
    }
    
}
