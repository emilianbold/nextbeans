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
package org.netbeans.modules.web.el.completion;

import org.netbeans.modules.web.el.ELTestBaseForTestProject;

/**
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class ELCompletionTest extends ELTestBaseForTestProject {

    public ELCompletionTest(String name) {
        super(name);
    }

    public void testCompletionForBean() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion04.xhtml", "#{bean.^}", false);
    }

    public void testCompletionForArray() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion01.xhtml", "#{bean.myArray.^}", false);
    }

    public void testCompletionForList() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion02.xhtml", "#{bean.myList.^}", false);
    }

    public void testCompletionForString() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion03.xhtml", "#{bean.myString.^}", false);
    }

    public void testCompletionForStaticIterableElement() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion05.xhtml", "#{[\"one\", 2].^}", false);
    }

    public void testCompletionForStream() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion06.xhtml", "#{[\"one\", 2].stream().^}", false);
    }

    public void testCompletionForStreamMax() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion07.xhtml", "#{[1, 2].stream().max().^}", false);
    }

    public void testCompletionForContinuosStream() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion08.xhtml", "#{[1, 2, 3, 4, 5].stream().substream(5).distinct().^}", false);
    }

    public void testCompletionForOptional() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion09.xhtml", "#{[1,2,3].stream().average().^}", false);
    }

    public void testCompletionAfterSimicolon() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/completion10.xhtml", "#{a = 5; bean.^}", false);
    }

    public void testJavaCompletion01() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/java/java_completion01.xhtml", "#{^}", false);
    }

    public void testJavaCompletion02() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/java/java_completion02.xhtml", "#{T(^)}", false);
    }

    public void testJavaCompletion03() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/java/java_completion03.xhtml", "#{T(Boolean).^}", false);
    }

    public void testJavaCompletion04() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/java/java_completion04.xhtml", "#{T(java.lang.Boolean).^}", false);
    }

    public void testJavaCompletion05() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/java/java_completion05.xhtml", "#{java.math.^}", false);
    }

    public void testJavaCompletion06() throws Exception {
        checkCompletion("projects/testWebProject/web/completion/java/java_completion06.xhtml", "#{T(Bo^)}", false);
    }

}
