/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.hint;

import org.netbeans.modules.csl.api.Rule;
import org.netbeans.modules.css.editor.properties.Semitones;
import org.netbeans.modules.javascript2.editor.hints.AssignmentInCondition;
import org.netbeans.modules.javascript2.editor.hints.BetterConditionHint;
import org.netbeans.modules.javascript2.editor.hints.DuplicatePropertyName;
import org.netbeans.modules.javascript2.editor.hints.JsConventionRule;
import org.netbeans.modules.javascript2.editor.hints.MissingSemicolonHint;
import org.netbeans.modules.javascript2.editor.hints.UnexpectedCommaInObjectLiteral;

/**
 *
 * @author Petr Pisl
 */
public class JsConventionHintTest extends HintTestBase {

    public JsConventionHintTest(String testName) {
        super(testName);
    }
    
    
    private Rule createRule() {
        return new JsConventionRule();
    }
    
    private Rule createSemicolonHint() {
        return new MissingSemicolonHint();
    }
    
    private Rule createBetterConditionHint() {
        return new BetterConditionHint();
    }
    
    private Rule createDuplicatePropertyHint() {
        return new DuplicatePropertyName();
    }
    
    public void testSemicolon1() throws Exception {
        checkHints(this, createSemicolonHint(), "testfiles/coloring/assignments01.js", null);
    }
    
    public void testSemicolon01() throws Exception {
        checkHints(this, createSemicolonHint(), "testfiles/hints/variableDeclaration.js", null);
    }
    
    public void testSemicolon02() throws Exception {
        checkHints(this, createSemicolonHint(), "testfiles/hints/missingSemicolon01.js", null);
    }
    
    public void testSemicolon03() throws Exception {
        checkHints(this, createSemicolonHint(), "testfiles/hints/varInForNode.js", null);
    }
    
    public void testUnexpectedComma01() throws Exception {
        checkHints(this, new UnexpectedCommaInObjectLiteral(), "testfiles/hints/unexpectedComma.js", null);
    }
    
    public void testAccidentalAssignment01() throws Exception {
        checkHints(this, new AssignmentInCondition(), "testfiles/hints/accidentalAssignment.js", null);
    }
    
    public void testBetterCondition01() throws Exception {
        checkHints(this, createBetterConditionHint(), "testfiles/hints/betterCondition.js", null);
    }
    
    public void testDuplicateName01() throws Exception {
        checkHints(this, createDuplicatePropertyHint(), "testfiles/hints/duplicateName.js", null);
    }
    
    public void testDuplicateName02() throws Exception {
        checkHints(this, createDuplicatePropertyHint(), "testfiles/hints/duplicateName02.js", null);
    }
    
    public void testDuplicateName03() throws Exception {
        checkHints(this, createDuplicatePropertyHint(), "testfiles/hints/duplicateName03.js", null);
    }
}