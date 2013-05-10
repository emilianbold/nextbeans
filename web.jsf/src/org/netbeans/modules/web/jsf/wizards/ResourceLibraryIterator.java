/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.jsf.wizards;

import java.awt.Component;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.jsf.api.facesmodel.JSFVersion;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * A template wizard for the Resource Library Contract.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class ResourceLibraryIterator implements TemplateWizard.Iterator {

    private static final long serialVersionUID = 1L;

    protected static final String META_INF = "META-INF";  //NOI18N
    protected static final String CONTRACTS = "contracts"; //NOI18N

    private WizardDescriptor.Panel<WizardDescriptor>[] panels;
    private WizardDescriptor descriptor;
    private ProjectType projectType;
    private int index;

    @Override
    public Set<DataObject> instantiate(TemplateWizard wiz) throws IOException {
        Project project = Templates.getProject(wiz);
        FileObject contractsParent = getNearestContractsParent(project);
        Set<DataObject> result = new HashSet<DataObject>();

        // create contracts folder if doesn't exist yet
        if (projectType == ProjectType.J2SE) {
            if (!META_INF.equals(contractsParent.getName())) {
                // the nearest parent is the META-INF now
                contractsParent = FileUtil.createFolder(contractsParent, META_INF);
            }
        }

        // check or create "contracts" folder if necessary
        FileObject targetDir = contractsParent.getFileObject(CONTRACTS);
        if (targetDir == null) {
            targetDir = FileUtil.createFolder(contractsParent, CONTRACTS);
        }

        // create the contract
        FileObject contractFolder = FileUtil.createFolder(targetDir, (String) wiz.getProperty(ResourceLibraryIteratorPanel.PROP_CONTRACT_NAME));

        // create template if requested
        if ((Boolean) wiz.getProperty(ResourceLibraryIteratorPanel.PROP_CREATE_TEMPLATE)) {
            FileObject cssFolder = contractFolder.getFileObject(TemplateIterator.CSS_FOLDER);
            if (cssFolder == null) {
                cssFolder = FileUtil.createFolder(contractFolder, TemplateIterator.CSS_FOLDER);
            }

            TemplateIterator.CreateTemplateAction templateAction = new TemplateIterator.CreateTemplateAction(
                    (TemplatePanelVisual) wiz.getProperty(ResourceLibraryIteratorPanel.PROP_TEMPLATE_PANEL),
                    (String) wiz.getProperty(ResourceLibraryIteratorPanel.PROP_TEMPLATE_NAME),
                    contractFolder,
                    cssFolder,
                    JSFVersion.JSF_2_2);
            contractsParent.getFileSystem().runAtomicAction(templateAction);
            result.add(DataObject.find(templateAction.getResult()));
        }

        return result;
    }

    @Override
    public void initialize(TemplateWizard wiz) {
        descriptor = wiz;
        Project project = Templates.getProject(wiz);
        FileObject contractsParent = getNearestContractsParent(project);
        loadPanelsAndSteps(contractsParent);
    }

    @Override
    public void uninitialize(TemplateWizard wiz) {
        panels = null;
        descriptor = null;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return panels[index];
    }

    @Override
    public String name() {
        return NbBundle.getMessage(TemplateIterator.class, "TITLE_x_of_y", new Integer(index + 1), new Integer(panels.length));
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    private FileObject getNearestContractsParent(Project project) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null) {
            // web application
            projectType = ProjectType.WEB;
            if (wm.getDocumentBase() != null) {
                return wm.getDocumentBase();
            }
        } else {
            // j2se library
            projectType = ProjectType.J2SE;
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (SourceGroup sourceGroup : sourceGroups) {
                FileObject metaInf = sourceGroup.getRootFolder().getFileObject(META_INF);
                if (metaInf != null) {
                    return metaInf;
                }
            }
            if (sourceGroups.length > 0) {
                return sourceGroups[0].getRootFolder();
            }
        }

        // fallback
        return project.getProjectDirectory();
    }

    private void loadPanelsAndSteps(FileObject contractsParent) {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{new ResourceLibraryIteratorPanel(descriptor, contractsParent, projectType)};

            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i)); // NOI18N
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps); // NOI18N
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE); // NOI18N
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE); // NOI18N
                }
            }
        }
    }

    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = descriptor.getProperty(WizardDescriptor.PROP_CONTENT_DATA); // NOI18N
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }

    protected enum ProjectType {
        WEB, //web project
        J2SE, //j2se library
    }
}
