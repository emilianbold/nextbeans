/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.php.editor.indent;

import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.Block;
import org.netbeans.modules.php.editor.parser.astnodes.DoStatement;
import org.netbeans.modules.php.editor.parser.astnodes.ExpressionStatement;
import org.netbeans.modules.php.editor.parser.astnodes.ForEachStatement;
import org.netbeans.modules.php.editor.parser.astnodes.ForStatement;
import org.netbeans.modules.php.editor.parser.astnodes.IfStatement;
import org.netbeans.modules.php.editor.parser.astnodes.WhileStatement;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultTreePathVisitor;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 */
public class IndentLevelCalculator extends DefaultTreePathVisitor {

    private Map<Integer, Integer> indentLevels;
    private int indentSize;
    private int continuationIndentSize;
    private BaseDocument doc;

    public IndentLevelCalculator(Document doc, Map<Integer, Integer> indentLevels) {
        this.indentLevels = indentLevels;
        this.doc = (BaseDocument) doc;
        CodeStyle codeStyle = CodeStyle.get(doc);
        indentSize = codeStyle.getIndentSize();
        continuationIndentSize = codeStyle.getContinuationIndentSize();
    }

    @Override
    public void visit(Block node) {
        if (node.isCurly()) {
            int start = node.getStartOffset();
            try {
                int firstNonWS = Utilities.getFirstNonWhiteFwd(doc, start + 1);
                if (firstNonWS > -1){
                    start = firstNonWS;
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            indentLevels.put(start, indentSize);
            indentLevels.put(node.getEndOffset(), -1 * indentSize);
        }

        super.visit(node);
    }

    @Override
    public void visit(IfStatement node) {
        indentNonBlockStatement(node.getFalseStatement());
        indentNonBlockStatement(node.getTrueStatement());

        super.visit(node);
    }

    @Override
    public void visit(DoStatement node) {
        indentNonBlockStatement(node.getBody());
        super.visit(node);
    }

    @Override
    public void visit(ForEachStatement node) {
        indentNonBlockStatement(node.getStatement());
        super.visit(node);
    }

    @Override
    public void visit(ForStatement node) {
        indentNonBlockStatement(node.getBody());
        super.visit(node);
    }

    @Override
    public void visit(WhileStatement node) {
        indentNonBlockStatement(node.getBody());
        super.visit(node);
    }

    @Override
    public void visit(ExpressionStatement node) {
        indentContinuationWithinStatement(node);
        super.visit(node);
    }

    private void indentContinuationWithinStatement(ASTNode node){

    }

    private void indentNonBlockStatement(ASTNode node){
        if (!(node instanceof Block)){
            indentLevels.put(node.getStartOffset(), indentSize);
            indentLevels.put(node.getEndOffset(), -1 * indentSize);
        }
    }
}