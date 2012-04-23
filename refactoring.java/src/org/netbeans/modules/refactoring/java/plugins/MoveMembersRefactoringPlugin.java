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
package org.netbeans.modules.refactoring.java.plugins;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.*;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.MoveRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.ProgressEvent;
import org.netbeans.modules.refactoring.java.RefactoringUtils;
import org.netbeans.modules.refactoring.java.api.JavaMoveMembersProperties;
import org.netbeans.modules.refactoring.java.api.JavaRefactoringUtils;
import org.netbeans.modules.refactoring.java.spi.JavaRefactoringPlugin;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Implemented abilities: <ul> <li>Move field(s)</li> <li>Move method(s)</li>
 * </ul>
 *
 * @author Ralph Ruijs
 */
@NbBundle.Messages({"ERR_NothingSelected=Nothing selected to move",
    "ERR_MoveToLibrary=Cannot move to a library",
    "ERR_MoveFromLibrary=Cannot move from a library",
    "ERR_MoveFromClass=Can only move members of a class",
    "ERR_MoveToSameClass=Target can not be the same as the source class",
    "ERR_MoveToSuperClass=Cannot move to a superclass, maybe you need the Pull Up Refactoring?",
    "ERR_MoveToSubClass=Cannot move to a subclass, maybe you need the Push Down Refactoring?",
    "ERR_MoveGenericField=Cannot move a generic field",
    "# {0} - Method name",
    "ERR_MoveAbstractMember=Cannot move abstract method \"{0}\"",
    "# {0} - Method name",
    "ERR_MoveMethodPolymorphic=Cannot move polymorphic method \"{0}\"",
    "WRN_InitNoAccess=Field initializer uses local accessors which will not be accessible",
    "# {0} - File displayname : line number",
    "WRN_NoAccessor=No accessor found to invoke the method from: {0}",
    "TXT_DelegatingMethod=Delegating method"})
public class MoveMembersRefactoringPlugin extends JavaRefactoringPlugin {

    private final MoveRefactoring refactoring;
    private final JavaMoveMembersProperties properties;

    public MoveMembersRefactoringPlugin(MoveRefactoring moveRefactoring) {
        this.refactoring = moveRefactoring;
        this.properties = moveRefactoring.getContext().lookup(JavaMoveMembersProperties.class);
    }

    @Override
    protected JavaSource getJavaSource(Phase p) {
        TreePathHandle source;
        source = properties.getPreSelectedMembers()[0];
        if(source != null && source.getFileObject() != null) {
            switch(p) {
                case CHECKPARAMETERS:
                case FASTCHECKPARAMETERS:
                case PRECHECK:
                case PREPARE:
                    ClasspathInfo cpInfo = getClasspathInfo(refactoring);
                    return JavaSource.create(cpInfo, source.getFileObject());
            }
        }
        return null;
    }

    @Override
    protected ClasspathInfo getClasspathInfo(AbstractRefactoring refactoring) {
        List<TreePathHandle> handles = new ArrayList<TreePathHandle>(refactoring.getRefactoringSource().lookupAll(TreePathHandle.class));
        Lookup targetLookup = this.refactoring.getTarget();
        if(targetLookup != null) {
            TreePathHandle target = targetLookup.lookup(TreePathHandle.class);
            if(target != null) {
                handles.add(target);
            }
        }
        ClasspathInfo cpInfo;
        if (!handles.isEmpty()) {
            cpInfo = RefactoringUtils.getClasspathInfoFor(handles.toArray(new TreePathHandle[handles.size()]));
        } else {
            cpInfo = JavaRefactoringUtils.getClasspathInfoFor((FileObject)properties.getPreSelectedMembers()[0].getFileObject());
        }
        refactoring.getContext().add(cpInfo);
        return cpInfo;
    }

    @Override
    protected Problem preCheck(CompilationController info) throws IOException {
        info.toPhase(JavaSource.Phase.RESOLVED);
        Problem preCheckProblem = isElementAvail(properties.getPreSelectedMembers()[0], info);
        if (preCheckProblem != null) {
            return preCheckProblem;
        }

        Element element = properties.getPreSelectedMembers()[0].resolveElement(info);
        TreePath path = info.getTrees().getPath(element);
        if (path != null) {
            TreePath enclosingClassPath = JavaRefactoringUtils.findEnclosingClass(info, path, true, true, true, true, false);
            if (enclosingClassPath != null) {
                Element typeElement = info.getTrees().getElement(enclosingClassPath);
                if (typeElement == null || !typeElement.getKind().isClass() || enclosingClassPath.getLeaf().getKind() == Tree.Kind.INTERFACE) {
                    return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveFromClass"));
                }
            } else {
                return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveFromClass"));
            }
        } else {
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveFromClass"));
        }
        return preCheckProblem;
    }

    @Override
    protected Problem checkParameters(CompilationController javac) throws IOException {
        javac.toPhase(JavaSource.Phase.RESOLVED);
        // TODO source method is using something not available at target
        // TODO source using generics not available at target
        // TODO Check if member is static but target is non static inner
        // TODO Check if target is in <default> package but source is not
        return null;
    }

    @Override
    protected Problem fastCheckParameters(CompilationController javac) throws IOException {
        javac.toPhase(JavaSource.Phase.RESOLVED);
        Collection<? extends TreePathHandle> source = refactoring.getRefactoringSource().lookupAll(TreePathHandle.class);

        if (source.isEmpty()) { // [f] nothing is selected
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_NothingSelected")); //NOI18N
        }

        Lookup targetLookup = refactoring.getTarget();
        TreePathHandle target;
        if(targetLookup == null || (target = targetLookup.lookup(TreePathHandle.class)) == null) {
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_NoTarget")); //NOI18N
        }

        if (target.getFileObject() == null || !JavaRefactoringUtils.isOnSourceClasspath(target.getFileObject())) { // [f] target is not on source classpath
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveToLibrary")); //NOI18N
        }
        TreePathHandle sourceTph = source.iterator().next();
        if (sourceTph.getFileObject() == null || !JavaRefactoringUtils.isOnSourceClasspath(sourceTph.getFileObject())) { // [f] source is not on source classpath
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveFromLibrary")); //NOI18N
        }
        
        for (TreePathHandle treePathHandle : source) {
            Element element = treePathHandle.resolveElement(javac);
            if(element.getKind() == ElementKind.FIELD) {
                VariableElement var = (VariableElement) element;
                if(var.asType().getKind() == TypeKind.TYPEVAR) {
                    return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveGenericField"));
                }
            }
            if(element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;
                if(method.getModifiers().contains(Modifier.ABSTRACT)) {
                    return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveAbstractMember", element.getSimpleName()));
                }
                
                // Method can not be polymorphic
                Collection<ExecutableElement> overridenMethods = JavaRefactoringUtils.getOverriddenMethods(method, javac);
                Collection<ExecutableElement> overridingMethods = JavaRefactoringUtils.getOverridingMethods(method, javac, cancelRequested);
                if (overridenMethods.size() > 0 || overridingMethods.size() > 0) {
                    return new Problem(true, NbBundle.getMessage(InlineRefactoringPlugin.class, "ERR_MoveMethodPolymorphic", method.getSimpleName())); //NOI18N
                }
            }
        }

        TreePath targetPath = target.resolve(javac);
        if(targetPath == null) {
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_TargetNotResolved"));
        }
        TreePath targetClass = JavaRefactoringUtils.findEnclosingClass(javac, targetPath, true, true, true, true, true);
        TypeMirror targetType = javac.getTrees().getTypeMirror(targetClass);
        if(targetType == null) {
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_TargetNotResolved"));
        }
        Problem p = checkProjectDeps(sourceTph.getFileObject(), target.getFileObject());
        if(p != null) {
            return p;
        }

        TreePath sourceClass = JavaRefactoringUtils.findEnclosingClass(javac, sourceTph.resolve(javac), true, true, true, true, true);
        TypeMirror sourceType = javac.getTrees().getTypeMirror(sourceClass);
        if (sourceType.equals(targetType)) { // [f] target is the same as source
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveToSameClass")); //NOI18N
        }
        if (javac.getTypes().isSubtype(sourceType, targetType)) { // [f] target is a superclass of source
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveToSuperClass")); //NOI18N
        }
        if (javac.getTypes().isSubtype(targetType, sourceType)) { // [f] target is a subclass of source
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MoveToSubClass")); //NOI18N
        }
        
        Element targetElement = target.resolveElement(javac);
        PackageElement targetPackage = (PackageElement) javac.getElementUtilities().outermostTypeElement(targetElement).getEnclosingElement();
        Element sourceElement = sourceTph.resolveElement(javac);
        PackageElement sourcePackage = (PackageElement) javac.getElementUtilities().outermostTypeElement(sourceElement).getEnclosingElement();
        if(targetPackage.isUnnamed() && !sourcePackage.isUnnamed()) {
            return new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MovingMemberToDefaultPackage")); //NOI18N
        }
        for (TreePathHandle treePathHandle : source) {
            Element element = treePathHandle.resolveElement(javac);
            List<? extends Element> enclosedElements = targetElement.getEnclosedElements();
            switch(element.getKind()) {
                case FIELD:
                    enclosedElements = ElementFilter.fieldsIn(enclosedElements);
                    break;
                case METHOD:
                    enclosedElements = ElementFilter.methodsIn(enclosedElements);
                    break;
                case CONSTRUCTOR:
                    enclosedElements = ElementFilter.constructorsIn(enclosedElements);
                    break;
                default:
                    enclosedElements = ElementFilter.typesIn(enclosedElements);
                    break;
            }
            for (Element member : enclosedElements) {
                if(element.getSimpleName().contentEquals(member.getSimpleName())) {
                    p = JavaPluginUtils.chainProblems(p, new Problem(true, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_PullUp_MemberAlreadyExists", element.getSimpleName())));
                }
            }
        }
        return p;
    }

    private Set<FileObject> getRelevantFiles() {
        final Set<FileObject> set = new LinkedHashSet<FileObject>();

        ClasspathInfo cpInfo = getClasspathInfo(refactoring);
        final ClassIndex idx = cpInfo.getClassIndex();
        final Collection<? extends TreePathHandle> tphs = refactoring.getRefactoringSource().lookupAll(TreePathHandle.class);
        TreePathHandle target = refactoring.getTarget().lookup(TreePathHandle.class);
        FileObject file = target.getFileObject();
        JavaSource source = JavaPluginUtils.createSource(file, cpInfo, target);
        CancellableTask<CompilationController> task = new CancellableTask<CompilationController>() {

            public void cancel() {
            }

            public void run(CompilationController info) throws Exception {
                info.toPhase(JavaSource.Phase.RESOLVED);
                Set<ClassIndex.SearchScopeType> searchScopeType = new HashSet<ClassIndex.SearchScopeType>(1);
                searchScopeType.add(ClassIndex.SearchScope.SOURCE);

                for (TreePathHandle tph : tphs) {
                    set.add(tph.getFileObject());
                    final Element el = tph.resolveElement(info);
                    if (el.getKind() == ElementKind.METHOD) {
                        // get method references from index
                        set.addAll(idx.getResources(ElementHandle.create((TypeElement) el.getEnclosingElement()), EnumSet.of(ClassIndex.SearchKind.METHOD_REFERENCES), searchScopeType)); //?????
                    }
                    if (el.getKind().isField()) {
                        // get field references from index
                        set.addAll(idx.getResources(ElementHandle.create((TypeElement) el.getEnclosingElement()), EnumSet.of(ClassIndex.SearchKind.FIELD_REFERENCES), searchScopeType));
                    }
                }
            }
        };
        try {
            source.runUserActionTask(task, true);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Make sure the target is added last. Needed for escalating visibility.
        set.remove(file);
        set.add(file);
        return set;
    }

    @Override
    public Problem prepare(RefactoringElementsBag refactoringElements) {
        fireProgressListenerStart(ProgressEvent.START, -1);

        Set<FileObject> relevantFiles = getRelevantFiles();
        Problem p = null;
        TreePathHandle targetHandle = refactoring.getTarget().lookup(TreePathHandle.class);
        fireProgressListenerStep(relevantFiles.size());
        MoveMembersTransformer transformer = new MoveMembersTransformer(refactoring);
        TransformTask task = new TransformTask(transformer, targetHandle);
        Problem prob = createAndAddElements(relevantFiles, task, refactoringElements, refactoring, getClasspathInfo(refactoring));
        prob = JavaPluginUtils.chainProblems(prob, transformer.getProblem());
        fireProgressListenerStop();
        return prob != null ? prob : JavaPluginUtils.chainProblems(transformer.getProblem(), p);
    }

    @SuppressWarnings("CollectionContainsUrl")
    private Problem checkProjectDeps(FileObject sourceFile, FileObject targetFile) {
        Set<FileObject> sourceRoots = new HashSet<FileObject>();
        ClassPath cp = ClassPath.getClassPath(sourceFile, ClassPath.SOURCE);
        if (cp != null) {
            FileObject root = cp.findOwnerRoot(sourceFile);
            sourceRoots.add(root);
        }

        FileObject targetRoot = null;
        ClassPath targetCp = ClassPath.getClassPath(targetFile, ClassPath.SOURCE);
        if(targetCp != null) {
            targetRoot = targetCp.findOwnerRoot(targetFile);
        }
        
        if(!sourceRoots.isEmpty() && targetRoot != null) {
            URL targetUrl = URLMapper.findURL(targetRoot, URLMapper.EXTERNAL);
            Project targetProject = FileOwnerQuery.getOwner(targetRoot);
            Set<URL> deps = SourceUtils.getDependentRoots(targetUrl);

            for (FileObject sourceRoot : sourceRoots) {
                URL sourceUrl = URLMapper.findURL(sourceRoot, URLMapper.INTERNAL);
                if (!deps.contains(sourceUrl)) {
                    Project sourceProject = FileOwnerQuery.getOwner(sourceRoot);
                    for (FileObject affected : getRelevantFiles()) {
                        if (FileOwnerQuery.getOwner(affected).equals(sourceProject) && !sourceProject.equals(targetProject)) {
                            assert sourceProject != null;
                            assert targetProject != null;
                            String sourceName = ProjectUtils.getInformation(sourceProject).getDisplayName();
                            String targetName = ProjectUtils.getInformation(targetProject).getDisplayName();
                            return new Problem(false, NbBundle.getMessage(MoveMembersRefactoringPlugin.class, "ERR_MemberMissingProjectDeps", sourceName, targetName));
                        }
                    }
                }
            }
        }
        return null;
    }
}
