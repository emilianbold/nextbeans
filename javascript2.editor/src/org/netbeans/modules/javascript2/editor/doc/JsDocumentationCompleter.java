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
package org.netbeans.modules.javascript2.editor.doc;

import com.oracle.nashorn.ir.AccessNode;
import com.oracle.nashorn.ir.BinaryNode;
import com.oracle.nashorn.ir.FunctionNode;
import com.oracle.nashorn.ir.IdentNode;
import com.oracle.nashorn.ir.Node;
import com.oracle.nashorn.ir.PropertyNode;
import com.oracle.nashorn.ir.VarNode;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.modules.javascript2.editor.doc.api.JsDocumentationSupport;
import org.netbeans.modules.javascript2.editor.doc.spi.SyntaxProvider;
import org.netbeans.modules.javascript2.editor.model.Identifier;
import org.netbeans.modules.javascript2.editor.model.JsElement;
import org.netbeans.modules.javascript2.editor.model.JsElement.Kind;
import org.netbeans.modules.javascript2.editor.model.JsFunction;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.model.impl.JsObjectImpl;
import org.netbeans.modules.javascript2.editor.model.impl.ModelUtils;
import org.netbeans.modules.javascript2.editor.model.impl.PathNodeVisitor;
import org.netbeans.modules.javascript2.editor.parser.JsParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class JsDocumentationCompleter {

    protected static final RequestProcessor RP = new RequestProcessor("JavaScript Documentation Completer", 1); //NOI18N

    public static void generateCompleteComment(BaseDocument doc, int caretOffset, int indent) {
        Runnable documentationGenerator = new DocumentationGenerator(doc, caretOffset, indent);
        RP.post(documentationGenerator);
    }

    private static class DocumentationGenerator implements Runnable {

        private final BaseDocument doc;
        private final int offset;
        private final int indent;

        public DocumentationGenerator(BaseDocument doc, int offset, int indent) {
            this.doc = doc;
            this.offset = offset;
            this.indent = indent;
        }

        @Override
        public void run() {
            try {
                ParserManager.parse(Collections.singleton(Source.create(doc)), new UserTask() {
                    @Override
                    public void run(ResultIterator resultIterator) throws Exception {
                        ParserResult parserResult = (ParserResult) resultIterator.getParserResult();
                        if (parserResult != null && parserResult instanceof JsParserResult) {
                            final JsParserResult jsParserResult = (JsParserResult) parserResult;
                            if (jsParserResult.getRoot() == null) {
                                // broken source
                                return;
                            }
                            Node nearestNode = getNearestNode(jsParserResult, offset);
                            if (nearestNode == null) {
                                // no non-doc node found in the file
                                return;
                            }
                            int examinedOffset = nearestNode instanceof VarNode ? nearestNode.getStart() : nearestNode.getFinish();
                            JsObject jsObject = findJsObjectFunctionVariable(jsParserResult.getModel().getGlobalObject(), examinedOffset);
                            assert jsObject != null;
                            boolean wrapperObject = isWrapperObject(jsParserResult, jsObject, nearestNode);
                            if (jsObject.getJSKind() == Kind.FILE || wrapperObject) {
                                String fqn = getFqnName(jsParserResult, nearestNode);
                                jsObject = ModelUtils.findJsObjectByName(jsParserResult.getModel(), fqn);
                            }
                            if (isField(jsObject)) {
                                generateFieldComment(doc, offset, indent, jsParserResult, jsObject);
                            } else if (isFunction(jsObject)) {
                                generateFunctionComment(doc, offset, indent, jsParserResult, jsObject);
                            } else {
                                // object - generate field for now, could be cleverer
                                generateFieldComment(doc, offset, indent, jsParserResult, jsObject);
                            }
                        }
                    }
                });
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private static boolean isWrapperObject(JsParserResult jsParserResult, JsObject jsObject, Node nearestNode) {
        List<Identifier> nodeName = jsParserResult.getModel().getNodeName(nearestNode);
        if (nodeName.isEmpty()) {
            return false;
        }
        return jsObject.getProperties().containsKey(nodeName.get(nodeName.size() - 1).getName());
    }

    /**
     * Tries to get fully qualified name for given node.
     *
     * @param parserResult JavaScript parser results
     * @param node examined node for its FQN
     * @return fully qualified name of the node
     */
    public static String getFqnName(JsParserResult parserResult, Node node) {
        PathToNodeVisitor ptnv = new PathToNodeVisitor(node);
        FunctionNode root = parserResult.getRoot();
        root.accept(ptnv);
        StringBuilder fqn = new StringBuilder();
        for (Node currentNode : ptnv.getFinalPath()) {
            List<Identifier> name = parserResult.getModel().getNodeName(currentNode);
            for (Identifier identifier : name) {
                fqn.append(".").append(identifier.getName()); //NOI18N
            }
        }
        return fqn.toString().substring(1);
    }

    private static void generateFieldComment(BaseDocument doc, int offset, int indent, JsParserResult jsParserResult, JsObject jsObject) throws BadLocationException {
        StringBuilder toAdd = new StringBuilder();
        SyntaxProvider syntaxProvider = JsDocumentationSupport.getSyntaxProvider(jsParserResult);

        Collection<? extends TypeUsage> assignments = jsObject.getAssignments();
        StringBuilder types = new StringBuilder();
        for (TypeUsage typeUsage : assignments) {
            // name and type are equivalent in the case of assigning parrametrs like "this.name = name"
            if (!typeUsage.getType().equals(jsObject.getName())) {
                types.append("|").append(typeUsage.getType());
            }
        }
        String type = types.length() == 0 ? null : types.toString().substring(1);
        generateDocEntry(doc, toAdd, syntaxProvider.typeTagTemplate(), indent, null, type); //NOI18N

        doc.insertString(offset, toAdd.toString(), null);
    }

    private static void generateFunctionComment(BaseDocument doc, int offset, int indent, JsParserResult jsParserResult, JsObject jsObject) throws BadLocationException {
        StringBuilder toAdd = new StringBuilder();
        SyntaxProvider syntaxProvider = JsDocumentationSupport.getSyntaxProvider(jsParserResult);
        // TODO - could know constructors
        JsFunction function = ((JsFunction) jsObject);
        addParameters(doc, toAdd, syntaxProvider.paramTagTemplate(), indent, jsParserResult, function.getParameters()); //NOI18N
        if (!function.getReturnTypes().isEmpty()) {
            addReturns(doc, toAdd, syntaxProvider.returnTagTemplate(), indent, jsParserResult, function.getReturnTypes()); //NOI18N
        }

        doc.insertString(offset, toAdd.toString(), null);
    }

    private static void addParameters(BaseDocument doc, StringBuilder toAdd, String template, int indent, JsParserResult jsParserResult, Collection<? extends JsObject> params) {
        for (JsObject jsObject : params) {
            generateDocEntry(doc, toAdd, template, indent, jsObject.getName(), null);
        }
    }

    private static void addReturns(BaseDocument doc, StringBuilder toAdd, String template, int indent, JsParserResult jsParserResult, Collection<? extends TypeUsage> returns) {
        StringBuilder sb = new StringBuilder();
        SyntaxProvider syntaxProvider = JsDocumentationSupport.getSyntaxProvider(jsParserResult);

        for (TypeUsage typeUsage : returns) {
            if (syntaxProvider.typesSeparator() == null) {
                // any first char which will be removed below
                sb.append(" ").append(typeUsage.getType()); //NOI18N
                break;
            } else {
                sb.append(syntaxProvider.typesSeparator()).append(typeUsage.getType());
            }
        }

        int separatorLength = syntaxProvider.typesSeparator() == null ? 1 : syntaxProvider.typesSeparator().length();
        String returnString = returns.isEmpty() ? "" : sb.toString().substring(separatorLength);
        generateDocEntry(doc, toAdd, template, indent, null, returnString);
    }

    private static void generateDocEntry(BaseDocument doc, StringBuilder toAdd, String template, int indent, String name, String type) {
        toAdd.append("\n"); //NOI18N
        toAdd.append(IndentUtils.createIndentString(doc, indent));

        toAdd.append("* "); //NOI18N
        toAdd.append(getProcessedTemplate(template, name, type));
    }

    private static String getProcessedTemplate(String template, String name, String type) {
        String finalTag = template;
        if (name != null) {
            finalTag = finalTag.replace(SyntaxProvider.NAME_PLACEHOLDER, name);
        }
        if (type != null) {
            finalTag = finalTag.replace(SyntaxProvider.TYPE_PLACEHOLDER, type);
        } else {
            finalTag = finalTag.replace(SyntaxProvider.TYPE_PLACEHOLDER, "type"); //NOI18N
        }
        return finalTag;
    }

    private static boolean isField(JsObject jsObject) {
        Kind kind = jsObject.getJSKind();
        return kind == Kind.FIELD || kind == Kind.VARIABLE || kind == Kind.PROPERTY;
    }

    private static boolean isFunction(JsObject jsObject) {
        return jsObject.getJSKind().isFunction();
    }

    /**
     * Gets the nearest next node for given offset.
     *
     * @param parserResult parser result of the JS file
     * @param offset offset where to start searching
     * @return {@code Node} which is the closest one
     */
    private static Node getNearestNode(JsParserResult parserResult, int offset) {
        FunctionNode root = parserResult.getRoot();
        NearestNodeVisitor offsetVisitor = new NearestNodeVisitor(offset);
        root.accept(offsetVisitor);
        return offsetVisitor.getNearestNode();
    }

    private static JsObject findJsObjectFunctionVariable(JsObject object, int offset) {
        JsObjectImpl jsObject = (JsObjectImpl) object;
        JsObject result = null;
        JsObject tmpObject = null;
        if (jsObject.getOffsetRange().containsInclusive(offset)) {
            result = jsObject;
            for (JsObject property : jsObject.getProperties().values()) {
                JsElement.Kind kind = property.getJSKind();
                if (kind == JsElement.Kind.OBJECT
                        || kind == JsElement.Kind.FUNCTION || kind == JsElement.Kind.METHOD || kind == JsElement.Kind.CONSTRUCTOR
                        || kind == JsElement.Kind.VARIABLE) {
                    tmpObject = findJsObjectFunctionVariable(property, offset);
                }
                if (tmpObject != null) {
                    result = tmpObject;
                    break;
                }
            }
        }
        return result;
    }

    private static class NearestNodeVisitor extends PathNodeVisitor {

        private final int offset;
        private Node nearestNode = null;

        public NearestNodeVisitor(int offset) {
            this.offset = offset;
        }

        private void processNode(Node node, boolean onset) {
            if (onset) {
                if (offset < node.getStart() && (nearestNode == null || node.getStart() < nearestNode.getStart())) {
                    nearestNode = node;
                }
            }
        }

        public Node getNearestNode() {
            if (nearestNode instanceof AccessNode) {
                FarestIdentNodeVisitor farestNV = new FarestIdentNodeVisitor();
                nearestNode.accept(farestNV);
                return farestNV.getFarestNode();
            }
            return nearestNode;
        }

        @Override
        public Node visit(AccessNode accessNode, boolean onset) {
            processNode(accessNode, onset);
            return super.visit(accessNode, onset);
        }

        @Override
        public Node visit(FunctionNode functionNode, boolean onset) {
            if (functionNode.getKind() != FunctionNode.Kind.SCRIPT) {
                processNode(functionNode, onset);
            }
            return super.visit(functionNode, onset);
        }

        @Override
        public Node visit(PropertyNode propertyNode, boolean onset) {
            processNode(propertyNode, onset);
            return super.visit(propertyNode, onset);
        }

        @Override
        public Node visit(VarNode varNode, boolean onset) {
            processNode(varNode, onset);
            return super.visit(varNode, onset);
        }

        @Override
        public Node visit(BinaryNode binaryNode, boolean onset) {
            processNode(binaryNode, onset);
            return super.visit(binaryNode, onset);
        }
    }

    private static class FarestIdentNodeVisitor extends PathNodeVisitor {

        private Node farestNode;
        private final StringBuilder farestPath = new StringBuilder();

        @Override
        public Node visit(IdentNode identNode, boolean onset) {
            farestNode = identNode;
            if (onset) {
                farestPath.append(".").append(identNode.getName()); //NOI18N
            }
            return super.visit(identNode, onset);
        }

        public Node getFarestNode() {
            return farestNode;
        }

        public String getFarestFqn() {
            return farestPath.toString().substring(1);
        }
    }

    private static class PathToNodeVisitor extends PathNodeVisitor {

        private final Node finalNode;
        private List<? extends Node> finalPath;

        public PathToNodeVisitor(Node finalNode) {
            this.finalNode = finalNode;
        }

        @Override
        public void addToPath(Node node) {
            super.addToPath(node);
            if (node.equals(finalNode)) {
                finalPath = new LinkedList<Node>(getPath());
            }
        }

        public List<? extends Node> getFinalPath() {
            return finalPath;
        }
    }
}
