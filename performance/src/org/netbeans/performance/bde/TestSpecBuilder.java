/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.performance.bde;

import java.util.List;
import java.util.ArrayList;

import org.netbeans.performance.bde.generated.*;

/** Visitor for an AST. Builds up benchmarks definitions.
 */
public final class TestSpecBuilder implements BDEParserVisitor {

    /** Starting node */
    private ASTStart start;

    /** new TestSpecBuilder */
    public TestSpecBuilder(ASTStart start) {
        this.start = start;
    }

    /** Creates a List of TestDefinitions from the given String */
    public static List parse(String spec) throws Exception {
        TestSpecBuilder builder = BDEParser.parseTestSpec(spec);
        return builder.build();
    }
    
    /** @return List of TestDefinitions. Starts building */
    private List build() throws Exception {
        return (List) start.jjtAccept(this, null);
    }
    
    //-------------------- interface methods --------------------
    
    public Object visit(SimpleNode node, Object data) throws Exception {
        data = node.childrenAccept(this, data);
        return data;
    }

    /** @return a String related to this node */
    public Object visit(ASTString node, Object data) throws Exception { 
        return node.getFirstToken().image.toString();
    }
    
    /** @return a String related to this node */
    public Object visit(ASTIdentifier node, Object data) throws Exception {
        return node.getFirstToken().image.toString();
    }
    
    public Object visit(ASTStart node, Object data) throws Exception {
        List list = new ArrayList(5);
        node.childrenAccept(this, list);
        return list;
    }

    public Object visit(ASTLoadDefinition node, Object data) throws Exception {
        int count = node.jjtGetNumChildren();
        String file = null;
        if (count == 1) {
            Node n = node.jjtGetChild(0);
            file = (String) n.jjtAccept(this, null);
        }
        LoadDefinition ld = new LoadDefinition(file);
        List list = (List) data;
        list.add(ld);
        return null;
    }
    
    public Object visit(ASTStoreDefinition node, Object data) throws Exception {
        int count = node.jjtGetNumChildren();
        String file = null;
        if (count == 1) {
            Node n = node.jjtGetChild(0);
            file = (String) n.jjtAccept(this, null);
        }
        StoreDefinition sd = new StoreDefinition(file);
        List list = (List) data;
        list.add(sd);
        return null;
    }
  
    /** Creates a List of TestDefinitions */
    public Object visit(ASTTestDefinitionList node, Object data) throws Exception {
        return node.childrenAccept(this, data);
    }

    /** Constructs one TestDefinition */
    public Object visit(ASTTestDefinition node, Object data) throws Exception {
        List list = (List) data;
        int count = node.jjtGetNumChildren();
        Node n = node.jjtGetChild(0);
        String className = (String) n.jjtAccept(this, null);
        
        if (count > 1) {
            n = node.jjtGetChild(1);
            List aList = (List) n.jjtAccept(this, null);
            if (count > 2) {
                n = node.jjtGetChild(2);
                List args = (List) n.jjtAccept(this, null);
                list.add(new TestDefinition(className, aList, args));
            } else {
                if (n instanceof ASTMethodFilterList) {
                    list.add(new TestDefinition(className, aList, null));
                } else {
                    list.add(new TestDefinition(className, null, aList));
                }
            }
        } else {
            list.add(new TestDefinition(className, null, null));
        }
        
        return list;
    }

    /** @return String - a class name */
    public Object visit(ASTClassName node, Object data) throws Exception {
        return node.getFirstToken().image.toString();
    }

    /** Constructs a List of Strings (method filters) */
    public Object visit(ASTMethodFilterList node, Object data) throws Exception {
        List list = new ArrayList();
        node.childrenAccept(this, list);
        return list;
    }

    /** @return String that represents one method filter */
    public Object visit(ASTMethodFilter node, Object data) throws Exception {
        List list = (List) data;
        list.add(node.getFirstToken().image.toString());
        return list;
    }

    /** Constructs a List of ArgumentSeries */
    public Object visit(ASTArgDataList node, Object data) throws Exception {
        List list = new ArrayList();
        node.childrenAccept(this, list);
        return list;
    }

    /** Constructs new ArgumentSeries that represents one "[...]" */
    public Object visit(ASTArgDataSeries node, Object data) throws Exception { 
        ArgumentSeries as = new ArgumentSeries();
        node.childrenAccept(this, as);
        ((List) data).add(as);
        return null;
    }

    /** Constructs representation for "Id = Val" */
    public Object visit(ASTArgData node, Object data) throws Exception { 
        Node n = node.jjtGetChild(0);
        String key = (String) n.jjtAccept(this, null);
        n = node.jjtGetChild(1);
        List values = (List) n.jjtAccept(this, null);
        ((ArgumentSeries) data).add(key, values);
        return null;
    }

    /** Constructs a String that represents this Id */
    public Object visit(ASTId node, Object data) throws Exception { 
        Node n = node.jjtGetChild(0);
        return (String) n.jjtAccept(this, null);
    }

    /** Constructs a list of values */
    public Object visit(ASTValSpecList node, Object data) throws Exception {
        List list = new ArrayList(7);
        node.childrenAccept(this, list);
        return list;
    }

    /** Adds this value to given list (data) */
    public Object visit(ASTValSpec node, Object data) throws Exception {
        List list = (List) data;
        list.add(node.jjtGetChild(0).jjtAccept(this, null));
        return list;
    }

    /** Constructs either an Integer or an Interval */
    public Object visit(ASTIntegerDef node, Object data) throws Exception { 
        int count = node.jjtGetNumChildren();
        if (count == 1) { // single int
            Node n = node.jjtGetChild(0);
            return n.jjtAccept(this, null); // Integer
        } else if (count >= 2) { // interval
            Node n = node.jjtGetChild(0);
            int i = ((Integer) n.jjtAccept(this, null)).intValue();
            n = node.jjtGetChild(1);
            int j = ((Integer) n.jjtAccept(this, null)).intValue();
            
            if (count == 3) {
                n = node.jjtGetChild(2);
                int k = ((Integer) n.jjtAccept(this, null)).intValue();
                return new Interval(i, j, k);
            } else {
                return new Interval(i, j);
            }
        }
        return data;
    }

    /** @return an Integer related to this node */
    public Object visit(ASTInteger node, Object data) throws Exception { 
        return Integer.valueOf(node.getFirstToken().image.toString());
    }
}
