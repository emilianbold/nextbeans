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
package org.netbeans.modules.javascript2.editor.sdoc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.javascript2.editor.doc.JsDocumentationTestBase;
import org.netbeans.modules.javascript2.editor.doc.spi.DocIdentifierImpl;
import org.netbeans.modules.javascript2.editor.doc.spi.JsModifier;
import org.netbeans.modules.javascript2.editor.doc.spi.DocIdentifier;
import org.netbeans.modules.javascript2.editor.doc.spi.DocParameter;
import org.netbeans.modules.javascript2.editor.doc.spi.JsDocumentationHolder;
import org.netbeans.modules.javascript2.editor.model.Type;
import org.netbeans.modules.javascript2.editor.model.impl.TypeImpl;
import org.netbeans.modules.javascript2.editor.parser.JsParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;

/**
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class SDocDocumentationProviderTest extends JsDocumentationTestBase {

    private static final String TEST_FILE_PATH = "testfiles/sdoc/";
    private static final String FILE_NAME_GENERAL = TEST_FILE_PATH + "classWithSDoc.js";
    private static final String FILE_NAME_PARAMETERS = TEST_FILE_PATH + "parameterTypes.js";

    private JsDocumentationHolder documentationHolder;
    private JsParserResult parserResult;

    public SDocDocumentationProviderTest(String testName) {
        super(testName);
    }

    private void initializeDocumentationHolder(Source source) throws ParseException {
        ParserManager.parse(Collections.singleton(source), new UserTask() {
            public @Override void run(ResultIterator resultIterator) throws Exception {
                Parser.Result result = resultIterator.getParserResult();
                assertTrue(result instanceof JsParserResult);
                
                parserResult = (JsParserResult) result;
                documentationHolder = getDocumentationHolder(parserResult, new SDocDocumentationProvider());
            }
        });
    }

    private void checkReturnType(Source source, final int offset, final List<? extends Type> expected) throws Exception {
        initializeDocumentationHolder(source);
        if (expected == null) {
            assertNull(documentationHolder.getReturnType(getNodeForOffset(parserResult, offset)));
        } else {
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), documentationHolder.getReturnType(getNodeForOffset(parserResult, offset)).get(i));
            }
        }
    }

    private void checkParameter(Source source, final int offset, final FakeDocParameter expectedParam) throws Exception {
        initializeDocumentationHolder(source);
        if (expectedParam == null) {
            assertNull(documentationHolder.getParameters(getNodeForOffset(parserResult, offset)));
        } else {
            List<DocParameter> parameters = documentationHolder.getParameters(getNodeForOffset(parserResult, offset));
            assertEquals(expectedParam.getDefaultValue(), parameters.get(0).getDefaultValue());
            assertEquals(expectedParam.getParamDescription(), parameters.get(0).getParamDescription());
            assertEquals(expectedParam.getParamName(), parameters.get(0).getParamName());
            assertEquals(expectedParam.isOptional(), parameters.get(0).isOptional());
            for (int i = 0; i < expectedParam.getParamTypes().size(); i++) {
                assertEquals(expectedParam.getParamTypes().get(i), parameters.get(0).getParamTypes().get(i));
            }
        }
    }

    private void checkDocumentation(Source source, final int offset, final String expected) throws Exception {
        initializeDocumentationHolder(source);
        assertEquals(expected, documentationHolder.getDocumentation(getNodeForOffset(parserResult, offset)));
    }

    private void checkDeprecated(Source source, final int offset, final boolean expected) throws Exception {
        initializeDocumentationHolder(source);
        assertEquals(expected, documentationHolder.isDeprecated(getNodeForOffset(parserResult, offset)));
    }

    private void checkModifiers(Source source, final int offset, final String expectedModifiers) throws Exception {
        initializeDocumentationHolder(source);
        Set<JsModifier> realModifiers = documentationHolder.getModifiers(getNodeForOffset(parserResult, offset));
        if (expectedModifiers == null) {
            assertEquals(0, realModifiers.size());
        } else {
            String[] expModifiers = expectedModifiers.split("[|]");
            assertEquals(expModifiers.length, realModifiers.size());
            for (int i = 0; i < expModifiers.length; i++) {
                assertTrue(realModifiers.contains(JsModifier.fromString(expModifiers[i])));
            }
        }
    }

    private void checkFirstSummary(Source source, int offset, String summary) throws ParseException {
        initializeDocumentationHolder(source);
        assertEquals(summary, documentationHolder.getCommentForOffset(offset, documentationHolder.getCommentBlocks()).getSummary().get(0));
    }

    public void testGetSummaryOfClassFromContextDescription() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function Rectangle2(width, height) {^");
        checkFirstSummary(testSource, caretOffset, "Create a new Rectangle instance.");
    }

    public void testGetSummaryOfClassFromClassDescription() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function ShapeFactory(){^");
        checkFirstSummary(testSource, caretOffset, "This class exists to demonstrate the assignment of a class prototype\n  as an anonymous block.");
    }

    public void testGetReturnTypeForReturn() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Shape.prototype.clone = function(){^");
        checkReturnType(testSource, caretOffset, Arrays.asList(new TypeImpl("Shape", 3499)));
    }

    public void testGetNullReturnTypeAtNoReturnTypeComment() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Shape.prototype.clone3 = function(){^");
        checkReturnType(testSource, caretOffset, Collections.<Type>emptyList());
    }

    public void testGetNullReturnTypeAtMissingComment() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Shape.prototype.clone4 = function(){^");
        checkReturnType(testSource, caretOffset, Collections.<Type>emptyList());
    }

    public void testGetReturnTypeAtFunction() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function martion () {^");
        checkReturnType(testSource, caretOffset, Arrays.asList(new TypeImpl("Number", 10625)));
    }

    public void testGetReturnTypeAtObjectFunction() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "getVersion: function() {^");
        checkReturnType(testSource, caretOffset, Arrays.asList(new TypeImpl("Number", 10919)));
    }

    public void testGetReturnTypeAtType() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Shape.prototype.getColor = function(){^");
        checkReturnType(testSource, caretOffset, Arrays.asList(new TypeImpl("Color", 2821)));
    }

    public void testGetReturnTypeAtProperty() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Shape.prototype.border = function(){^return border;};");
        checkReturnType(testSource, caretOffset, Arrays.asList(new TypeImpl("int", 2277)));
    }

    public void testGetParametersForNameAndTypeParam() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_PARAMETERS));
        final int caretOffset = getCaretOffset(testSource, "function line1(userName){^}");
        FakeDocParameter fakeDocParameter = new FakeDocParameter(new DocIdentifierImpl("userName", 23), null, "", false,
                Arrays.<Type>asList(new TypeImpl("String", 15)));
        checkParameter(testSource, caretOffset, fakeDocParameter);
    }

    public void testGetParametersForNameAndMoreTypesParam() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_PARAMETERS));
        final int caretOffset = getCaretOffset(testSource, "function line2(product){^}");
        FakeDocParameter fakeDocParameter = new FakeDocParameter(new DocIdentifierImpl("product", 95), null, "", false,
                Arrays.<Type>asList(new TypeImpl("String", 79), new TypeImpl("Number", 87)));
        checkParameter(testSource, caretOffset, fakeDocParameter);
    }

    public void testGetParametersForFullDocOptionalParam() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_PARAMETERS));
        final int caretOffset = getCaretOffset(testSource, "function line3(accessLevel){^}");
        FakeDocParameter fakeDocParameter = new FakeDocParameter(new DocIdentifierImpl("accessLevel", 158), null, "accessLevel is optional", true,
                Arrays.<Type>asList(new TypeImpl("String", 149)));
        checkParameter(testSource, caretOffset, fakeDocParameter);
    }

    public void testGetParametersForFullDocParam() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_PARAMETERS));
        final int caretOffset = getCaretOffset(testSource, "function line5(accessLevel){^}");
        FakeDocParameter fakeDocParameter = new FakeDocParameter(new DocIdentifierImpl("accessLevel", 334), null, "accessLevel is optional", false,
                Arrays.<Type>asList(new TypeImpl("String", 326)));
        checkParameter(testSource, caretOffset, fakeDocParameter);
    }

    public void testDocumentationDescriptionReturns() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function Shape(){^");
        checkDocumentation(testSource, caretOffset, "<p style=\"margin: 5 5 5 5\">Construct a new Shape object.This is the basic Shape class.\n  It can be considered an abstract class, even though no such thing\n  really existing in JavaScript</p><h3>Returns:</h3><table style=\"margin-left:10px;\"><tr><td valign=\"top\" style=\"margin-right:5px;\"><b>Type:</b></td><td valign=\"top\">Shape | Coordinate</td></tr><tr><td valign=\"top\" style=\"margin-right:5px;\"><b>Description:</b></td><td valign=\"top\">A new shape.</td></tr></table>");
    }

    public void testDocumentationDescription() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function addReference(){^");
        checkDocumentation(testSource, caretOffset, "<p style=\"margin: 5 5 5 5\">This is an inner method, just used here as an example</p>");
    }

    public void testDocumentationDescriptionExamples() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function Hexagon(sideLength) {^");
        checkDocumentation(testSource, caretOffset, "<p style=\"margin: 5 5 5 5\">Create a new Hexagon instance.Hexagon is a class that is a <i>logical</i> sublcass of\n  Shape (thanks to the <code>&#64;extends</code> tag), but in\n  reality it is completely unrelated to Shape.</p><h3>Parameters:</h3><table style=\"margin-left:10px;\"><tr><td valign=\"top\" style=\"margin-right:5px;\">int</td><td valign=\"top\" style=\"margin-right:5px;\"><b>sideLength</b></td><td>The length of one side for the new Hexagon</td></tr></table>");
    }

    public void testDeprecated01() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "function Add(One, Two){^");
        checkDeprecated(testSource, caretOffset, true);
    }

    public void testDeprecated02() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Circle.^PI = 3.14;");
        checkDeprecated(testSource, caretOffset, true);
    }

    public void testDeprecated03() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Rectangle.prototype.^width = 0;");
        checkDeprecated(testSource, caretOffset, false);
    }

    public void testDeprecated04() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Coordinate.prototype.getX = function(){^");
        checkDeprecated(testSource, caretOffset, false);
    }

    public void testModifiers01() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Rectangle.prototype.^width = 0;");
        checkModifiers(testSource, caretOffset, "private");
    }

    public void testModifiers02() throws Exception {
        Source testSource = getTestSource(getTestFile(FILE_NAME_GENERAL));
        final int caretOffset = getCaretOffset(testSource, "Rectangle.prototype.getWidth = function(){^");
        checkModifiers(testSource, caretOffset, null);
    }

    private static class FakeDocParameter implements DocParameter {

        DocIdentifier paramName;
        String defaultValue, paramDesc;
        boolean optional;
        List<Type> paramTypes;

        public FakeDocParameter(DocIdentifier paramName, String defaultValue, String paramDesc, boolean optional, List<Type> paramTypes) {
            this.paramName = paramName;
            this.defaultValue = defaultValue;
            this.paramDesc = paramDesc;
            this.optional = optional;
            this.paramTypes = paramTypes;
        }
        @Override
        public DocIdentifier getParamName() {
            return paramName;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public boolean isOptional() {
            return optional;
        }

        @Override
        public String getParamDescription() {
            return paramDesc;
        }

        @Override
        public List<Type> getParamTypes() {
            return paramTypes;
        }

    }
}