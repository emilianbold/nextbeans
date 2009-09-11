/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.web.project.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.common.J2eeProjectCapabilities;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJarMetadata;
import org.netbeans.modules.j2ee.dd.api.ejb.EnterpriseBeans;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.spi.ejbjar.support.J2eeProjectView;
import org.netbeans.modules.web.project.WebProject;
import org.netbeans.modules.web.project.ui.customizer.WebProjectProperties;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;

/**
 * NodeFactory to create EJB nodes.
 *
 * @author kaktus
 */
@NodeFactory.Registration(projectType="org-netbeans-modules-web-project", position=350)
public class EjbsNodeFactory implements NodeFactory {

    public EjbsNodeFactory() {
    }

    public NodeList createNodes(Project p) {
        WebProject project = p.getLookup().lookup(WebProject.class);
        assert project != null;
        return new EjbNodeList(project);
    }

    private static class EjbNodeList implements NodeList<String>, PropertyChangeListener{
        private static final String KEY_EJBS = "ejbKey"; //NOI18N
        private final WebProject project;
        private final ChangeSupport changeSupport = new ChangeSupport(this);
        private Node view = null;
        private boolean isViewEmpty = true;

        EjbNodeList(WebProject proj) {
            this.project = proj;
        }

        public List<String> keys() {
            J2eeProjectCapabilities projectCap = J2eeProjectCapabilities.forProject(project);
            checkView();
            if (projectCap.isEjb31LiteSupported() && !isViewEmpty){
                return Collections.singletonList(KEY_EJBS);
            }else{
                return Collections.EMPTY_LIST;
            }
        }

        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            changeSupport.removeChangeListener(l);
        }

        public Node node(String key) {
            if (KEY_EJBS.equals(key)) {
                return view;
            }
            return null;
        }

        public void addNotify() {
            EjbJar ejbModule = project.getAPIEjbJar();
            view = J2eeProjectView.createEjbsView(ejbModule, project);
            project.evaluator().addPropertyChangeListener(this);
        }

        public void removeNotify() {
            project.evaluator().removePropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String p = evt.getPropertyName();
            if (p != null && (p.equals(WebProjectProperties.J2EE_PLATFORM) || p.startsWith("/EnterpriseBeans/"))){ //NOI18N
                fireChange();
            }
        }

        private void fireChange(){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    changeSupport.fireChange();
                }
            });
        }

        private void checkView(){
            new Thread(new Runnable(){
                public void run() {
                    Boolean isEmpty = Boolean.TRUE;
                    try {
                        isEmpty = project.getAPIEjbJar().getMetadataModel().runReadAction(new MetadataModelAction<EjbJarMetadata, Boolean>() {
                            public Boolean run(EjbJarMetadata metadata) {
                                org.netbeans.modules.j2ee.dd.api.ejb.EjbJar ejbJar = metadata.getRoot();
                                if (ejbJar != null) {
                                    EnterpriseBeans enterpriseBeans = ejbJar.getEnterpriseBeans();
                                    if (enterpriseBeans != null) {
                                        enterpriseBeans.removePropertyChangeListener(EjbNodeList.this);
                                        enterpriseBeans.addPropertyChangeListener(EjbNodeList.this);
                                        if (enterpriseBeans.getEjbs().length > 0){
                                            return Boolean.FALSE;
                                        }
                                    }
                                }
                                return Boolean.TRUE;
                            }
                        });
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    }
                            
                    if (isEmpty^isViewEmpty){
                        isViewEmpty = isEmpty;
                        fireChange();
                    }
                }
            }).start();
/*
            Boolean isEmpty = Boolean.TRUE;
            try {
                isEmpty = project.getAPIEjbJar().getMetadataModel().runReadAction(new MetadataModelAction<EjbJarMetadata, Boolean>() {
                    public Boolean run(EjbJarMetadata metadata) {
                        org.netbeans.modules.j2ee.dd.api.ejb.EjbJar ejbJar = metadata.getRoot();
                        if (ejbJar != null) {
                            EnterpriseBeans enterpriseBeans = ejbJar.getEnterpriseBeans();
                            if (enterpriseBeans != null) {
                                enterpriseBeans.removePropertyChangeListener(EjbNodeList.this);
                                enterpriseBeans.addPropertyChangeListener(EjbNodeList.this);
                                if (enterpriseBeans.getEjbs().length > 0){
                                    return Boolean.FALSE;
                                }
                            }
                        }
                        return Boolean.TRUE;
                    }
                });
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
            return isEmpty;
 */
        }
    }
}
