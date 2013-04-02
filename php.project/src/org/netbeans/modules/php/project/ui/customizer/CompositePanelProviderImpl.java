/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.customizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.modules.editor.indent.project.api.Customizers;
import org.netbeans.modules.php.api.framework.PhpFrameworks;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.web.clientproject.api.jslibs.JavaScriptLibraryCustomizerPanel;
import org.netbeans.modules.web.clientproject.api.jslibs.JavaScriptLibrarySelectionPanel;
import org.netbeans.modules.web.common.api.CssPreprocessorsCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik, Radek Matous
 */
public class CompositePanelProviderImpl implements ProjectCustomizer.CompositeCategoryProvider {

    public static final String SOURCES = "Sources"; // NOI18N
    public static final String RUN = "Run"; // NOI18N
    public static final String BROWSER = "Browser"; // NOI18N
    public static final String JS_FILES = "JS_FILES"; // NOI18N
    public static final String PHP_INCLUDE_PATH = "PhpIncludePath"; // NOI18N
    public static final String IGNORE_PATH = "IgnorePath"; // NOI18N
    public static final String FRAMEWORKS = "Frameworks"; // NOI18N

    private final String name;
    private final Map<ProjectCustomizer.Category, PhpModuleCustomizerExtender> frameworkCategories;

    public CompositePanelProviderImpl(String name) {
        this.name = name;

        if (FRAMEWORKS.equals(name)) {
            frameworkCategories = new LinkedHashMap<ProjectCustomizer.Category, PhpModuleCustomizerExtender>();
        } else {
            frameworkCategories = null;
        }
    }

    @NbBundle.Messages("CompositePanelProviderImpl.category.browser.title=Browser")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        ProjectCustomizer.Category toReturn = null;
        final ProjectCustomizer.Category[] categories = null;
        if (SOURCES.equals(name)) {
            toReturn = ProjectCustomizer.Category.create(
                    SOURCES,
                    NbBundle.getMessage(CustomizerProviderImpl.class, "LBL_Config_Sources"),
                    null,
                    categories);
        } else if (RUN.equals(name)) {
            toReturn = ProjectCustomizer.Category.create(
                    RUN,
                    NbBundle.getMessage(CustomizerProviderImpl.class, "LBL_Config_RunConfig"),
                    null,
                    categories);
        } else if (BROWSER.equals(name)) {
            toReturn = ProjectCustomizer.Category.create(
                    BROWSER,
                    Bundle.CompositePanelProviderImpl_category_browser_title(),
                    null,
                    categories);
        } else if (JS_FILES.equals(name)) {
            toReturn = ProjectCustomizer.Category.create(
                    JS_FILES,
                    JavaScriptLibraryCustomizerPanel.getCategoryDisplayName(),
                    null);
        } else if (PHP_INCLUDE_PATH.equals(name)) {
            toReturn = ProjectCustomizer.Category.create(
                    PHP_INCLUDE_PATH,
                    NbBundle.getMessage(CustomizerProviderImpl.class, "LBL_Config_PhpIncludePath"),
                    null,
                    categories);
        } else if (IGNORE_PATH.equals(name)) {
            toReturn = ProjectCustomizer.Category.create(
                    IGNORE_PATH,
                    NbBundle.getMessage(CustomizerProviderImpl.class, "LBL_Config_IgnorePath"),
                    null,
                    categories);
        } else if (FRAMEWORKS.equals(name)) {
            fillFrameworkCategories(context.lookup(PhpProject.class));
            if (frameworkCategories.isEmpty()) {
                return null;
            }
            List<ProjectCustomizer.Category> subcategories = new ArrayList<ProjectCustomizer.Category>(frameworkCategories.keySet());
            toReturn = ProjectCustomizer.Category.create(
                    FRAMEWORKS,
                    NbBundle.getMessage(CustomizerProviderImpl.class, "LBL_Config_Frameworks"),
                    null,
                    subcategories.toArray(new ProjectCustomizer.Category[subcategories.size()]));
        }
        assert toReturn != null : "No category for name: " + name;
        return toReturn;
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        String nm = category.getName();
        final PhpProjectProperties uiProps = context.lookup(PhpProjectProperties.class);
        if (SOURCES.equals(nm)) {
            return new CustomizerSources(category, uiProps);
        } else if (RUN.equals(nm)) {
            return new CustomizerRun(uiProps, category);
        } else if (BROWSER.equals(nm)) {
            return new CustomizerBrowser(category, uiProps);
        } else if (JS_FILES.equals(nm)) {
            return new JavaScriptLibraryCustomizerPanel(category, new JavaScriptLibraryCustomizerPanel.CustomizerSupport() {
                @Override
                public File getWebRoot() {
                    return uiProps.getResolvedWebRootFolder();
                }
                @Override
                public void setLibrariesFolder(String librariesFolder) {
                    // noop
                }
                @Override
                public void setSelectedLibraries(List<JavaScriptLibrarySelectionPanel.SelectedLibrary> selectedLibraries) {
                    // noop
                }
            });
        } else if (PHP_INCLUDE_PATH.equals(nm)) {
            return new CustomizerPhpIncludePath(category, uiProps);
        } else if (IGNORE_PATH.equals(nm)) {
            return new CustomizerIgnorePath(category, uiProps);
        } else if (FRAMEWORKS.equals(nm)) {
            return new JPanel();
        }
        // possibly framework?
        if (frameworkCategories != null) {
            PhpModuleCustomizerExtender extender = frameworkCategories.get(category);
            if (extender != null) {
                return new CustomizerFramework(category, extender, uiProps);
            }
        }
        assert false : "No component found for " + category.getDisplayName();
        return new JPanel();
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 100
    )
    public static CompositePanelProviderImpl createSources() {
        return new CompositePanelProviderImpl(SOURCES);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 150
    )
    public static CompositePanelProviderImpl createRunConfig() {
        return new CompositePanelProviderImpl(RUN);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 180
    )
    public static CompositePanelProviderImpl createBrowser() {
        return new CompositePanelProviderImpl(BROWSER);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 190
    )
    public static CompositePanelProviderImpl createJsFiles() {
        return new CompositePanelProviderImpl(JS_FILES);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 195
    )
    public static ProjectCustomizer.CompositeCategoryProvider createCssPreprocessors() {
        return new CssPreprocessorsCustomizer();
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 200
    )
    public static CompositePanelProviderImpl createPhpIncludePath() {
        return new CompositePanelProviderImpl(PHP_INCLUDE_PATH);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 250
    )
    public static CompositePanelProviderImpl createIgnorePath() {
        return new CompositePanelProviderImpl(IGNORE_PATH);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 300
    )
    public static CompositePanelProviderImpl createFrameworks() {
        return new CompositePanelProviderImpl(FRAMEWORKS);
    }

//o.n.m.javascript.libraries     Projects/o-n-m-php-project/Customizer/o-n-m-javascript-libraries-ui-customizer-JSLibraryCustomizerProvider.instance @375
//o.n.m.web.client.tools.impl    Projects/o-n-m-php-project/Customizer/o-n-m-web-client-tools-impl-projects-DebugCustomizerPanelProvider-createPhpProjectDebug.instance @400

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = UiUtils.CUSTOMIZER_PATH,
        position = 1000
    )
    public static ProjectCustomizer.CompositeCategoryProvider createFormatting() {
        return Customizers.createFormattingCategoryProvider(Collections.singletonMap("allowedMimeTypes", FileUtils.PHP_MIME_TYPE)); // NOI18N
    }

    private void fillFrameworkCategories(PhpProject project) {
        frameworkCategories.clear();

        final PhpModule phpModule = project.getPhpModule();
        int i = 0;
        for (PhpFrameworkProvider frameworkProvider : PhpFrameworks.getFrameworks()) {
            PhpModuleCustomizerExtender extender = frameworkProvider.createPhpModuleCustomizerExtender(phpModule);
            if (extender != null) {
                String categoryName = extender.getDisplayName();
                if (categoryName == null) {
                    categoryName = frameworkProvider.getName();
                }
                ProjectCustomizer.Category category = ProjectCustomizer.Category.create(
                        FRAMEWORKS + i++,
                        categoryName,
                        null,
                        (ProjectCustomizer.Category[]) null);
                frameworkCategories.put(category, extender);
            }
        }
    }
}
