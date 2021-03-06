/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.php.editor.model.MethodScope;
import org.netbeans.modules.php.editor.model.ModelElement;
import org.netbeans.modules.php.editor.model.Scope;
import org.netbeans.modules.php.editor.model.TypeScope;
import org.netbeans.modules.php.editor.model.nodes.ASTNodeInfo;
import org.netbeans.modules.php.editor.model.nodes.FunctionDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.MethodDeclarationInfo;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Identifier;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.ReturnStatement;

/**
 * @author Radek Matous
 */
class CodeMarkerBuilder {
    private ASTNodeInfo currentNodeInfo;
    private Scope  currentScope;
    private Map<ASTNodeInfo<ReturnStatement>, Scope> returnStatements;
    private HashMap<MethodDeclarationInfo, Scope> methodDeclarations;
    private HashMap<FunctionDeclarationInfo, Scope> fncDeclarations;

    CodeMarkerBuilder() {
        this.returnStatements = new HashMap<>();
        this.methodDeclarations = new HashMap<>();
        this.fncDeclarations = new HashMap<>();
    }

    void prepare(FunctionDeclaration node, Scope scope) {
        FunctionDeclarationInfo nodeInfo = FunctionDeclarationInfo.create(node);
        if (canBePrepared(node, scope)) {
            fncDeclarations.put(nodeInfo, scope);
        }
    }

    void prepare(MethodDeclaration node, Scope scope) {
        if (scope instanceof MethodScope && scope.getInScope() instanceof TypeScope) {
            MethodDeclarationInfo nodeInfo = MethodDeclarationInfo.create(node, (TypeScope) scope.getInScope());
            if (canBePrepared(node, scope)) {
                methodDeclarations.put(nodeInfo, scope);
            }
        }
    }

    void prepare(ReturnStatement returnStatement, Scope scope) {
        ASTNodeInfo<ReturnStatement> nodeInfo = ASTNodeInfo.create(returnStatement);
        if (canBePrepared(returnStatement, scope)) {
            returnStatements.put(nodeInfo, scope);
        }
    }

    void setCurrentContextInfo(final int offset) {
        final Collection<LazyBuild> scopesToScan = new ArrayList<>();
        for (Entry<MethodDeclarationInfo, Scope> entry : methodDeclarations.entrySet()) {
            if (entry.getValue() instanceof LazyBuild) {
                LazyBuild scope = (LazyBuild) entry.getValue();
                scopesToScan.add(scope);
            }
            setOccurenceAsCurrent(entry.getKey(), entry.getValue(), offset);
        }
        for (LazyBuild lazyBuild : scopesToScan) {
            if (!lazyBuild.isScanned()) {
                lazyBuild.scan();
            }
        }
        for (Entry<FunctionDeclarationInfo, Scope> entry : fncDeclarations.entrySet()) {
            setOccurenceAsCurrent(entry.getKey(), entry.getValue(), offset);
        }
        for (Entry<ASTNodeInfo<ReturnStatement>, Scope> entry : returnStatements.entrySet()) {
            setOccurenceAsCurrent(entry.getKey(), entry.getValue(), offset);
        }
    }

    private void buildFunctionDeclarations(FileScopeImpl fileScope) {
        String scopeName = currentScope.getName();
        for (Entry<FunctionDeclarationInfo, Scope> entry : fncDeclarations.entrySet()) {
            Scope scope = entry.getValue();
            FunctionDeclarationInfo nodInfo = entry.getKey();
            if (scopeName.equalsIgnoreCase(scope.getName())) {
                FunctionDeclaration function = nodInfo.getOriginalNode();
                Identifier functionName = function.getFunctionName();
                OffsetRange range = new OffsetRange(function.getStartOffset(), functionName.getStartOffset());
                fileScope.addCodeMarker(new CodeMarkerImpl.InvisibleCodeMarker(range, fileScope));
            }
        }
    }

    private void buildMethodDeclarations(FileScopeImpl fileScope) {
        String scopeName = currentScope.getName();
        for (Entry<MethodDeclarationInfo, Scope> entry : methodDeclarations.entrySet()) {
            Scope scope = entry.getValue();
            Scope parentScope = scope.getInScope();
            Scope parentCurrentScope = currentScope.getInScope();

            MethodDeclarationInfo nodInfo = entry.getKey();
            if (scopeName.equalsIgnoreCase(scope.getName())) {
                if (parentCurrentScope != null && parentScope != null && parentCurrentScope.getName().equalsIgnoreCase(parentScope.getName())) {
                    FunctionDeclaration function = nodInfo.getOriginalNode().getFunction();
                    Identifier functionName = function.getFunctionName();
                    OffsetRange range = new OffsetRange(function.getStartOffset(), functionName.getStartOffset());
                    fileScope.addCodeMarker(new CodeMarkerImpl.InvisibleCodeMarker(range, fileScope));
                }
            }
        }
    }

    private void buildReturnStatement(FileScopeImpl fileScope) {
        String scopeName = currentScope.getName();
        for (Entry<ASTNodeInfo<ReturnStatement>, Scope> entry : returnStatements.entrySet()) {
            Scope scope = entry.getValue();
            Scope parentScope = scope.getInScope();
            Scope parentCurrentScope = currentScope.getInScope();

            ASTNodeInfo<ReturnStatement> nodInfo = entry.getKey();
            if (scopeName.equalsIgnoreCase(scope.getName())) {
                if (parentCurrentScope != null && parentScope != null && parentCurrentScope.getName().equalsIgnoreCase(parentScope.getName())) {
                    fileScope.addCodeMarker(new CodeMarkerImpl(nodInfo, fileScope));
                }
            }
        }
    }

    void build(FileScopeImpl fileScope, final int offset) {
        if (currentNodeInfo == null && offset >= 0) {
            setCurrentContextInfo(offset);
        }
        if (currentNodeInfo != null && currentScope != null) {
            ASTNodeInfo.Kind kind = currentNodeInfo.getKind();
            currentNodeInfo = null;
            switch (kind) {
                case FUNCTION:
                    buildFunctionDeclarations(fileScope);
                    buildReturnStatement(fileScope);
                    break;
                case STATIC_METHOD:
                case METHOD:
                    buildMethodDeclarations(fileScope);
                    buildReturnStatement(fileScope);
                    break;
                case RETURN_MARKER:
                    buildMethodDeclarations(fileScope);
                    buildFunctionDeclarations(fileScope);
                    buildReturnStatement(fileScope);
                    break;
                default:
                    throw new IllegalStateException(kind.toString());
            }
        }
    }

    private boolean canBePrepared(ASTNode node, ModelElement scope) {
        return scope != null && node != null;
    }

    private void setOccurenceAsCurrent(ASTNodeInfo nodeInfo, Scope scope, final int offset) {
        OffsetRange range = nodeInfo.getRange();
        ASTNode originalNode = nodeInfo.getOriginalNode();
        if (originalNode instanceof ReturnStatement) {
            ReturnStatement returnStatement = (ReturnStatement) originalNode;
            Expression expression = returnStatement.getExpression();
            if (expression != null) {
                range = new OffsetRange(returnStatement.getStartOffset(), expression.getStartOffset());
            }
        } else if (originalNode instanceof MethodDeclaration) {
            FunctionDeclaration function = ((MethodDeclaration) originalNode).getFunction();
            Identifier functionName = function.getFunctionName();
            range = new OffsetRange(function.getStartOffset(), functionName.getStartOffset());
        } else if (originalNode instanceof FunctionDeclaration) {
            FunctionDeclaration function = (FunctionDeclaration) originalNode;
            Identifier functionName = function.getFunctionName();
            range = new OffsetRange(function.getStartOffset(), functionName.getStartOffset());
        }
        if (range.containsInclusive(offset)) {
            currentNodeInfo = nodeInfo;
            currentScope = scope;
        }
    }

}
