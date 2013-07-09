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
package org.netbeans.modules.j2ee.ejbverification.rules;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.j2ee.common.ProjectUtil;
import org.netbeans.modules.j2ee.dd.api.ejb.Session;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.ejbverification.EJBAPIAnnotations;
import org.netbeans.modules.j2ee.ejbverification.EJBProblemContext;
import org.netbeans.modules.j2ee.ejbverification.HintsUtils;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.TriggerTreeKind;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Checks whether @Schedule annotation of the class (if any) has non-persistent Timer in cases of EJB3.2 Lite
 * and also that no @Schedule annotation is allowed in case of EJB3.1 Lite.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
@Hint(displayName = "#PersistentTimerInEjbLite.display.name",
        description = "#PersistentTimerInEjbLite.desc",
        id = "o.n.m.j2ee.ejbverification.PersistentTimerInEjbLite",
        category = "JavaEE",
        enabled = true,
        suppressWarnings = "PersistentTimerInEjbLite")
@NbBundle.Messages({
    "PersistentTimerInEjbLite.display.name=Persistent timer within EJB Lite",
    "PersistentTimerInEjbLite.desc=Persistent timer (@Schedule annotation) can't be used in case of EJB 3.2 Lite and timer can't be used at all within EJB 3.1 Lite targeting project.",
    "PersistentTimerInEjbLite.err.timer.in.ee6lite=@Schedule is not allowed in project which targets JavaEE 6 Web profile server.",
    "PersistentTimerInEjbLite.err.nonpersistent.timer.in.ee7lite=Persistent timer is not allowed in project which targets JavaEE 7 Web profile server."
})
public final class PersistentTimerInEjbLite {

    @TriggerTreeKind(Tree.Kind.CLASS)
    public static Collection<ErrorDescription> run(HintContext hintContext) {
        final List<ErrorDescription> problems = new ArrayList<>();
        final EJBProblemContext ctx = HintsUtils.getOrCacheContext(hintContext);
        if (ctx != null && ctx.getEjb() instanceof Session) {
            boolean ee7lite = ctx.getEjbModule().getJ2eeProfile() == Profile.JAVA_EE_7_WEB;
            boolean ee6lite = ctx.getEjbModule().getJ2eeProfile() == Profile.JAVA_EE_6_WEB;
            J2eePlatform platform = ProjectUtil.getPlatform(ctx.getProject());
            if ((ee6lite || ee7lite) && nonEeFullServer(platform)) {
                for (Element element : ctx.getClazz().getEnclosedElements()) {
                    for (AnnotationMirror annm : element.getAnnotationMirrors()) {
                        if (EJBAPIAnnotations.SCHEDULE.equals(annm.getAnnotationType().toString())) {
                            if (ee6lite) {
                                problems.add(HintsUtils.createProblem(element, ctx.getComplilationInfo(),
                                        Bundle.PersistentTimerInEjbLite_err_timer_in_ee6lite(), Severity.ERROR));
                            }
                            if (ee7lite && isTimerPersistent(annm.getElementValues())) {
                                Fix fix = new PersistentTimerInEjbLiteFix(ctx.getFileObject(), element);
                                problems.add(HintsUtils.createProblem(element, ctx.getComplilationInfo(),
                                        Bundle.PersistentTimerInEjbLite_err_nonpersistent_timer_in_ee7lite(), Severity.ERROR, fix));
                            }
                        }
                    }
                }
            }
        }

        return problems;
    }

    private static boolean nonEeFullServer(J2eePlatform platform) {
        if (platform == null) {
            return true;
        }

        return !platform.getSupportedProfiles().contains(Profile.JAVA_EE_6_FULL);
    }

    private static boolean isTimerPersistent(Map<? extends ExecutableElement, ? extends AnnotationValue> values) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(EJBAPIAnnotations.PERSISTENT)) {
                Object elementValue = entry.getValue().getValue();
                if (elementValue instanceof Boolean) {
                    return (Boolean) elementValue;
                }
            }
        }
        return true;
    }

    private static class PersistentTimerInEjbLiteFix implements Fix {

        private final ElementHandle methodElement;
        private final FileObject fileObject;

        private PersistentTimerInEjbLiteFix(FileObject fileObject, Element methodElement) {
            this.fileObject = fileObject;
            this.methodElement = ElementHandle.create(methodElement);
        }

        @Messages({
            "PersistentTimerInEjbLiteFix.lbl.make.timer.nonpersistent=Make the timer non-persistent"
        })
        @Override
        public String getText() {
            return Bundle.PersistentTimerInEjbLiteFix_lbl_make_timer_nonpersistent();
        }

        @Override
        public ChangeInfo implement() throws Exception {
            Task<WorkingCopy> task = new Task<WorkingCopy>() {
                @Override
                public void run(WorkingCopy copy) throws Exception {
                    copy.toPhase(JavaSource.Phase.RESOLVED);
                    fixTimerAnnotation(copy);
                }
            };
            JavaSource js = JavaSource.forFileObject(fileObject);
            if (js != null) {
                js.runModificationTask(task).commit();
            }
            return null;
        }

        public void fixTimerAnnotation(WorkingCopy copy) {
            TypeElement scheduleAnnotation = copy.getElements().getTypeElement(EJBAPIAnnotations.SCHEDULE);
            ModifiersTree modifiers = ((MethodTree) copy.getTrees().getPath(methodElement.resolve(copy)).getLeaf()).getModifiers();
            TreeMaker tm = copy.getTreeMaker();
            for (AnnotationTree at : modifiers.getAnnotations()) {
                TreePath tp = new TreePath(new TreePath(copy.getCompilationUnit()), at.getAnnotationType());
                Element e = copy.getTrees().getElement(tp);
                if (scheduleAnnotation.equals(e)) {
                    List<? extends ExpressionTree> arguments = at.getArguments();
                    for (ExpressionTree et : arguments) {
                        if (et.getKind() == Tree.Kind.ASSIGNMENT) {
                            AssignmentTree assignment = (AssignmentTree) et;
                            AssignmentTree newAssignment = tm.Assignment(assignment.getVariable(), tm.Literal(false));
                            if (EJBAPIAnnotations.PERSISTENT.equals(assignment.getVariable().toString())) {
                                copy.rewrite(
                                        modifiers,
                                        copy.getTreeUtilities().translate(modifiers, Collections.singletonMap(et, newAssignment)));
                                return;
                            }
                        }
                    }
                    List<ExpressionTree> newArguments = new ArrayList<>(arguments);
                    ExpressionTree persistenQualIdent = tm.QualIdent(EJBAPIAnnotations.PERSISTENT);
                    newArguments.add(tm.Assignment(persistenQualIdent, tm.Literal(false)));
                    AnnotationTree newAnnotation = tm.Annotation(tp.getLeaf(), newArguments);
                    copy.rewrite(at, newAnnotation);
                    return;
                }
            }
        }
    }
}
