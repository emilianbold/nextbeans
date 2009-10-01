/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.products.nb.ergonomics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.NbClusterConfigurationLogic;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.applications.NetBeansUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.helper.Dependency;
import org.netbeans.installer.utils.helper.ErrorLevel;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.panels.netbeans.NbWelcomePanel;
import org.netbeans.installer.wizard.components.panels.netbeans.NbWelcomePanel.BundleType;

/**
 *
 * @author Dmitry Lipin
 */
public class ConfigurationLogic extends NbClusterConfigurationLogic {
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String ERGONOMICS_CLUSTER = 
            "{ergonomics-cluster}"; // NOI18N
    private static final String JAVA_CLUSTER =
            "{java-cluster}"; // NOI18N
    
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    public ConfigurationLogic() throws InitializationException {
        super(new String[]{
            ERGONOMICS_CLUSTER}, null);
    }

    @Override
    public void install(Progress progress) throws InstallationException {
        super.install(progress);
        String type = System.getProperty(NbWelcomePanel.WELCOME_PAGE_TYPE_PROPERTY);
        if(type!=null && BundleType.getType(type).equals(BundleType.JAVA)) {
            // Issue 157484. JavaSE should be enabled in "Java" distribution
            // http://www.netbeans.org/issues/show_bug.cgi?id=157484
            List<Dependency> dependencies =
                getProduct().getDependencyByUid(BASE_IDE_UID);
            final Product nbProduct =
                Registry.getInstance().getProducts(dependencies.get(0)).get(0);
            final File nbLocation = nbProduct.getInstallationLocation();
            try {
                NetBeansUtils.addCluster(nbLocation, ERGONOMICS_CLUSTER, JAVA_CLUSTER);
            } catch (IOException e) {
                LogManager.log(ErrorLevel.WARNING, e);
            }
        }
    }
}
