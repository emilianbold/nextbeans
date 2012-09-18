/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.websvc.rest.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.Modifier;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.javaee.specs.support.api.JaxRsStackSupport;
import org.netbeans.modules.websvc.api.support.SourceGroups;
import org.netbeans.modules.websvc.rest.RestUtils;
import org.netbeans.modules.websvc.rest.spi.WebRestSupport;
import org.netbeans.modules.websvc.rest.support.JavaSourceHelper;
import org.netbeans.modules.websvc.rest.wizard.HttpMethodsPanel.HttpMethods;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.WizardDescriptor.ProgressInstantiatingIterator;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;


/**
 * @author ads
 *
 */
public class OriginResourceIterator implements
        ProgressInstantiatingIterator<WizardDescriptor>
{
    private static final String CLASS = ".class";                       //NOI18N
    private static final String CONTAINER_CONTAINER_RESPONSE = 
            "com.sun.jersey.spi.container.ContainerResponse";           //NOI18N
    private static final String CONTAINER_CONTAINER_REQUEST = 
            "com.sun.jersey.spi.container.ContainerRequest";            //NOI18N
    private static final String CONTAINER_RESPONSE_FILTER = 
            "com.sun.jersey.spi.container.ContainerResponseFilter";     // NOI18N
    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.AsynchronousInstantiatingIterator#instantiate()
     */
    @Override
    public Set instantiate() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.InstantiatingIterator#initialize(org.openide.WizardDescriptor)
     */
    @Override
    public void initialize( WizardDescriptor wizard ) {
        myWizard = wizard;
        Project project = Templates.getProject(wizard);
        SourceGroup[] sourceGroups = SourceGroups.getJavaSourceGroups(project);    
        
        Panel panel ;
        myRestFilterPanel = new RestFilterPanel( wizard );
        if (sourceGroups.length == 0) {
            SourceGroup[] genericSourceGroups = ProjectUtils.
                    getSources(project).getSourceGroups(Sources.TYPE_GENERIC);
            panel = Templates.buildSimpleTargetChooser(project,  genericSourceGroups).
                        bottomPanel( myRestFilterPanel).create();
        } else {
            panel = JavaTemplates.createPackageChooser(project, sourceGroups, 
                            myRestFilterPanel, true);
        }
        myPanels = new Panel[]{ panel };
        setSteps();
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.InstantiatingIterator#uninitialize(org.openide.WizardDescriptor)
     */
    @Override
    public void uninitialize( WizardDescriptor arg0 ) {
        myPanels = null;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#addChangeListener(javax.swing.event.ChangeListener)
     */
    @Override
    public void addChangeListener( ChangeListener arg0 ) {
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#current()
     */
    @Override
    public Panel<WizardDescriptor> current() {
        return myPanels[myIndex];
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return myIndex<myPanels.length-1;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#hasPrevious()
     */
    @Override
    public boolean hasPrevious() {
        return myIndex >0 ;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#name()
     */
    @Override
    public String name() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#nextPanel()
     */
    @Override
    public void nextPanel() {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        myIndex++;        
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#previousPanel()
     */
    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        myIndex--;    
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#removeChangeListener(javax.swing.event.ChangeListener)
     */
    @Override
    public void removeChangeListener( ChangeListener arg0 ) {
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.ProgressInstantiatingIterator#instantiate(org.netbeans.api.progress.ProgressHandle)
     */
    @Override
    public Set instantiate( ProgressHandle handle ) throws IOException {
        handle.start();
        
        FileObject dir = Templates.getTargetFolder(myWizard);
        String filterName = Templates.getTargetName(myWizard );
        
        FileObject filterClass = GenerationUtils.createClass(dir,filterName, null );
        
        Project project = Templates.getProject(myWizard);
        WebRestSupport support = project.getLookup().lookup(WebRestSupport.class);
        if ( support!= null ){
            boolean hasRequest = RestUtils.hasClass(project, 
                    CONTAINER_CONTAINER_REQUEST.replace('.', '/')+CLASS);
            boolean hasFilter = RestUtils.hasClass(project, 
                    CONTAINER_RESPONSE_FILTER.replace('.', '/')+CLASS);
            boolean hasResponse = RestUtils.hasClass(project, 
                    CONTAINER_CONTAINER_RESPONSE.replace('.', '/')+CLASS);
            if ( !hasRequest || !hasFilter || !hasResponse ){
                handle.progress(NbBundle.getMessage(OriginResourceIterator.class, 
                        "MSG_ExtendsClasspath"));                                // NOI18N 
                JaxRsStackSupport jaxRsSupport = support.getJaxRsStackSupport();
                if ( jaxRsSupport == null ) {
                    jaxRsSupport = JaxRsStackSupport.getDefault();
                }
                jaxRsSupport.extendsJerseyProjectClasspath(project);
            }
        }
        
        handle.progress(NbBundle.getMessage(OriginResourceIterator.class, 
                "MSG_GenerateClassFilter"));                                // NOI18N
        JavaSource javaSource = JavaSource.forFileObject(filterClass);
        final String fqn[] = new String[1];
        javaSource.runModificationTask( new Task<WorkingCopy>() {
            
            @Override
            public void run( WorkingCopy  copy ) throws Exception {
                copy.toPhase(Phase.ELEMENTS_RESOLVED);
                ClassTree classTree = JavaSourceHelper.getTopLevelClassTree(copy);
                fqn[0] = JavaSourceHelper.getTopLevelClassElement(copy).
                        getQualifiedName().toString();
                TreeMaker maker = copy.getTreeMaker();
                ClassTree newTree = maker.addClassImplementsClause(classTree, 
                        maker.QualIdent(CONTAINER_RESPONSE_FILTER));
                
                
                List<VariableTree> params = new ArrayList<VariableTree>(2);
                ModifiersTree paramModifiers = maker.Modifiers(
                        Collections.<Modifier>emptySet());
                params.add(maker.Variable(paramModifiers, "request", 
                        maker.QualIdent(
                        CONTAINER_CONTAINER_REQUEST), null)); 
                params.add(maker.Variable(paramModifiers, "response", 
                        maker.QualIdent(
                        CONTAINER_CONTAINER_RESPONSE), null)); 
                MethodTree method = maker.Method(maker.Modifiers( 
                        EnumSet.of(Modifier.PUBLIC), 
                            Collections.singletonList(maker.Annotation(
                                    maker.QualIdent(Override.class.getName()), 
                                        Collections.<ExpressionTree>emptyList()))), 
                        "filter", 
                        maker.QualIdent(CONTAINER_CONTAINER_RESPONSE),
                        Collections.<TypeParameterTree>emptyList(), params, 
                        Collections.<ExpressionTree>emptyList(), getFilterBody(), null);
                newTree = maker.addClassMember( newTree, method);
                copy.rewrite( classTree, newTree);
            }
        }).commit();
        
        handle.progress(NbBundle.getMessage(OriginResourceIterator.class, 
                "MSG_UpdateDescriptor"));               // NOI18N
        if ( support != null ){
            support.addInitParam(WebRestSupport.CONTAINER_RESPONSE_FILTER, fqn[0]);
        }
        
        return Collections.singleton(filterClass);
    }
    
    private String getFilterBody(){
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("response.getHttpHeaders().putSingle(\"Access-Control-Allow-Origin\",\"");//NOI18N
        builder.append(myWizard.getProperty(RestFilterPanel.ORIGIN));
        builder.append("\");");                                                                                                                      //NOI18N
        
        builder.append("response.getHttpHeaders().putSingle(\"Access-Control-Allow-Methods\",\"");//NOI18N
        List<HttpMethods> methods = (List<HttpMethods>)myWizard.getProperty(
                RestFilterPanel.HTTP_METHODS);
        for (HttpMethods httpMethod : methods) {
            builder.append(httpMethod.toString().toUpperCase(Locale.ENGLISH));
            builder.append(", ");                                                                                                                   //NOI18N
        }
        if ( !methods.isEmpty()){
            builder.delete(builder.length()-2, builder.length());
        }
        builder.append("\");");                                                                                                                     //NOI18N
        
        builder.append("response.getHttpHeaders().putSingle(\"Access-Control-Allow-Headers\",\"");//NOI18N
        builder.append(myWizard.getProperty(RestFilterPanel.HEADERS));
        builder.append("\");");                                                     //NOI18N                                                              //NOI18N
        builder.append("return response;}");                                        //NOI18N
        return builder.toString();
    }
    
    private void setSteps() {
        Object contentData = myWizard.getProperty(WizardDescriptor.PROP_CONTENT_DATA);  
        if ( contentData instanceof String[] ){
            String steps[] = (String[])contentData;
            steps[steps.length-1]=NbBundle.getMessage(OriginResourceIterator.class, 
                    "TXT_ConfigureFilter");        // NOI18N
            for( int i=0; i<myPanels.length; i++ ){
                Panel panel = myPanels[i];
                JComponent component = (JComponent)panel.getComponent();
                component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i));
            }
        }
    }
    
    private WizardDescriptor myWizard;
    private WizardDescriptor.Panel[] myPanels;
    private Panel myRestFilterPanel;
    private int myIndex;

}