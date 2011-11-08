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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.options;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.netbeans.spi.options.OptionsPanelController.ContainerRegistration;
import org.netbeans.spi.options.OptionsPanelController.SubRegistration;
import org.netbeans.spi.options.OptionsPanelController.TopLevelRegistration;
import org.openide.filesystems.annotations.LayerBuilder;
import org.openide.filesystems.annotations.LayerBuilder.File;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class OptionsPanelControllerProcessor extends LayerGeneratingProcessor {

    public @Override Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(
            TopLevelRegistration.class.getCanonicalName(),
            ContainerRegistration.class.getCanonicalName(),
            SubRegistration.class.getCanonicalName()
        ));
    }

    protected boolean handleProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws LayerGenerationException {
        if (roundEnv.processingOver()) {
            return false;
        }
        for (Element e : roundEnv.getElementsAnnotatedWith(TopLevelRegistration.class)) {
            TopLevelRegistration r = e.getAnnotation(TopLevelRegistration.class);
            LayerBuilder builder = layer(e);
            File file = builder.instanceFile("OptionsDialog", r.id().length() > 0 ? r.id() : null, r, null).
                    methodvalue("instanceCreate", OptionsCategory.class.getName(), "createCategory").
                    instanceAttribute("controller", OptionsPanelController.class).
                    bundlevalue("categoryName", r.categoryName()).
                    position(r.position());
            iconBase(e, r.iconBase(), r, file, builder);
            keywords(e, r.keywords(), r.keywordsCategory(), r, file);
            file.write();
        }
        for (Element e : roundEnv.getElementsAnnotatedWith(SubRegistration.class)) {
            SubRegistration r = e.getAnnotation(SubRegistration.class);
            if (r.position() != Integer.MAX_VALUE && r.location().equals("Advanced")) {
                throw new LayerGenerationException("position ignored for Advanced subpanels", e, processingEnv, r, "position");
            }
            File file = layer(e).instanceFile("OptionsDialog/" + r.location(), r.id().length() > 0 ? r.id() : null, r, null).
                    methodvalue("instanceCreate", AdvancedOption.class.getName(), "createSubCategory").
                    instanceAttribute("controller", OptionsPanelController.class).
                    bundlevalue("displayName", r.displayName()).
                    position(r.position());
            keywords(e, r.keywords(), r.keywordsCategory(), r, file);
            file.write();
        }
        for (Element e : roundEnv.getElementsAnnotatedWith(ContainerRegistration.class)) {
            ContainerRegistration r = e.getAnnotation(ContainerRegistration.class);
            LayerBuilder builder = layer(e);
            File file = builder.file("OptionsDialog/" + r.id() + ".instance").
                    methodvalue("instanceCreate", OptionsCategory.class.getName(), "createCategory").
                    stringvalue("advancedOptionsFolder", "OptionsDialog/" + r.id()).
                    bundlevalue("categoryName", r.categoryName()).
                    position(r.position());
            iconBase(e, r.iconBase(), r, file, builder);
            keywords(e, r.keywords(), r.keywordsCategory(), r, file);
            file.write();
            layer(e).folder("OptionsDialog/" + r.id()).position(0).write();
        }
        return true;
    }

    private void iconBase(Element e, String iconBase, Annotation r, File file, LayerBuilder builder) throws LayerGenerationException {
        builder.validateResource(iconBase, e, r, "iconBase", true);
        file.stringvalue("iconBase", iconBase);
    }

    private void keywords(Element e, String keywords, String keywordsCategory, Annotation r, File file) throws LayerGenerationException {
        if (keywords.length() > 0) {
            if (keywordsCategory.length() == 0) {
                throw new LayerGenerationException("Must specify both keywords and keywordsCategory", e, processingEnv, r, "keywordsCategory");
            }
            file.bundlevalue("keywords", keywords, r, "keywords").bundlevalue("keywordsCategory", keywordsCategory, r, "keywordsCategory");
        } else {
            if (keywordsCategory.length() > 0) {
                throw new LayerGenerationException("Must specify both keywords and keywordsCategory", e, processingEnv, r, "keywords");
            }
        }
    }

}
