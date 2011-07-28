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
package org.netbeans.modules.profiler.j2ee.selector.nodes.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.profiler.api.icons.Icons;
import org.netbeans.modules.profiler.j2ee.impl.icons.JavaEEIcons;
import org.netbeans.modules.profiler.j2ee.selector.nodes.web.servlet.ServletNode;
import org.netbeans.modules.profiler.nbimpl.javac.ClasspathInfoFactory;
import org.netbeans.modules.profiler.selector.spi.nodes.ContainerNode;
import org.netbeans.modules.profiler.selector.spi.nodes.GreedySelectorChildren;
import org.netbeans.modules.profiler.selector.spi.nodes.SelectorChildren;
import org.netbeans.modules.profiler.selector.spi.nodes.SelectorNode;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jaroslav Bachorik
 */
abstract public class AbstractWebContainerNode extends ContainerNode {
    private class Children extends GreedySelectorChildren<ContainerNode> {
        @Override
        protected List<? extends SelectorNode> prepareChildren(final ContainerNode parent) {
            final Set<SelectorNode> nodes = new HashSet<SelectorNode>();

            try {
                Project project = parent.getLookup().lookup(Project.class);
                
                WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
                if (wm != null) {
                    final ClasspathInfo cpInfo = ClasspathInfoFactory.infoFor(project);
                    final MetadataModel<WebAppMetadata> mm = wm.getMetadataModel();
                    nodes.addAll(mm.runReadAction(new MetadataModelAction<WebAppMetadata, Collection<ServletNode>>() {

                        @Override
                        public Collection<ServletNode> run(final WebAppMetadata metadata) throws Exception {
                            final Collection<ServletNode> sNodes = new ArrayList<ServletNode>();
                            JavaSource js = JavaSource.create(cpInfo, new FileObject[0]);
                            js.runUserActionTask(new CancellableTask<CompilationController>() {
                                    public void cancel() {
                                    }

                                    public void run(CompilationController cc) throws Exception {
                                        nodes.addAll(collectChildren(cpInfo, cc, metadata));
                                    }
                            }, false);
                            return sNodes;
                        }
                    }));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return new ArrayList<SelectorNode>(nodes);
        }
    }
    
    public AbstractWebContainerNode(String name, ContainerNode parent) {
        super(name, Icons.getIcon(JavaEEIcons.PACKAGE), parent);
    }
    
    @Override
    final protected SelectorChildren getChildren() {
        return new Children();
    }
    
    abstract protected Collection<SelectorNode> collectChildren(ClasspathInfo cpInfo, CompilationController cc, WebAppMetadata md);
}
