/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.text.syntax.dom;

import org.w3c.dom.*;
import org.netbeans.modules.xml.text.syntax.*;
import org.netbeans.editor.*;

/**
 * Read-only DOM Comment node.
 *
 * @author Petr Kuzel
 */
public class CommentImpl extends SyntaxNode implements Comment {
    
    public CommentImpl(XMLSyntaxSupport support, TokenItem from, int to) {
        super( support, from, to );
    }
    
    public String toString() {
        return "Comment" + super.toString() + "<!--" + getData() + "-->";
    }
    
    public String getNodeValue() throws org.w3c.dom.DOMException {
        return getData();  //??? strip delimiters
    }
    
    public String getNodeName() {
        return "#comment";  //NOI18N
    }
    
    public short getNodeType() {
        return Node.COMMENT_NODE;
    }
    
    public Text splitText(int offset) {
        throw new ROException();
    }
 
    public String getData() {
        String data = first.getImage();
        return data.substring(("<!--".length() - 1) , (data.length() - "-->".length() -1 ));  //NOI18N        
    }

    public void setData(String data) {
        throw new ROException();
    }
    
    public int getLength() {
        return getData().length();
    }
    
    public String substringData(int offset, int count) {
        return getData().substring(offset, offset + count + 1);
    }

    public void appendData(String arg) {
        throw new ROException();
    }
    
    public void insertData(int offset, String arg) {
        throw new ROException();
    }


    public void deleteData(int offset, int count) {
        throw new ROException();
    }                           

    public void replaceData(int offset, int count, String arg) {
        throw new ROException();
    }

    
}

