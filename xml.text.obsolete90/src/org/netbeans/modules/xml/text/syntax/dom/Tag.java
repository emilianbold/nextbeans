/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.xml.text.syntax.dom;

import org.netbeans.modules.xml.text.syntax.XMLSyntaxSupport;
import org.netbeans.modules.xml.text.syntax.XMLTokenIDs;
import java.util.*;
import javax.swing.text.BadLocationException;

import org.w3c.dom.*;
import org.netbeans.modules.xml.text.syntax.*;
import org.netbeans.modules.xml.spi.dom.*;
import org.netbeans.editor.*;

/**
 * Represents tag syntax element. It also represent DOM <code>Element</code>.
 * This duality means that one document element is represented by
 * two DOM <code>Element</code> instances - one for start tag and one for
 * end tag. This is hidden during document traversal but never relay on
 * <code>equals</code>. The <code>equals</code> is used for syntax element
 * purposes.
 */
public abstract class Tag extends SyntaxNode implements Element, XMLTokenIDs {
    
    protected NamedNodeMap domAttributes;
    
    protected String name;
    
    public Tag(XMLSyntaxSupport support, TokenItem from, int to, String name, Collection attribs) {
        super( support, from,to );
        this.name = name;
    }
    
    public final short getNodeType() {
        return Node.ELEMENT_NODE;
    }
    
    public final String getNodeName() {
        return getTagName();
    }
    
    public final String getTagName() {
        return name;
    }
    
    /**
     * Create properly bound attributes and cache results.
     * Parse attributes from first token.
     */
    public synchronized NamedNodeMap getAttributes() {
        
        HashMap map = new LinkedHashMap(3);
        
        SCAN_LOOP:
            for (TokenItem next = first().getNext(); next != null; next = next.getNext()) {
                TokenID id = next.getTokenID();
                String name;
                String value;
                if (id == ARGUMENT) {
                    TokenItem attributeStart = next;
                    name = next.getImage();
                    while (next.getTokenID() != VALUE) {
                        next = next.getNext();
                        if (next == null || next.getTokenID() == ERROR) break SCAN_LOOP;
                    }
                    
                    // fuzziness to relax minor tokenization changes
                    String image = next.getImage();
                    char test = image.charAt(0);
                    if (image.length() == 1) {
                        if (test == '"' || test == '\'') {
                            next = next.getNext();
                        }
                    }
                    
                    if (next == null) break SCAN_LOOP;
                    value = next.getImage();
                    
                    Object key = NamedNodeMapImpl.createKey(name);
                    map.put(key, new AttrImpl(support, attributeStart, this));
                    
                    next = Util.skipAttributeValue(next, test);
                    if (next == null) break SCAN_LOOP;
                } else if (id == WS) {
                    // just skip
                } else {
                    break; // end of element markup
                }
            }
            
            // domAttributes = new NamedNodeMapImpl(map);
            return new NamedNodeMapImpl(map);
    }
    
    public String getAttribute(String name) {
        Attr attribute = getAttributeNode(name);
        return attribute != null ? attribute.getValue() : "";
    }
    
    public final void setAttribute(String name, String value) {
        NamedNodeMap attributes = getAttributes();
        Node attr = attributes.getNamedItem(name);
        if (attr != null) {
            attr.setNodeValue(value);
        } else {
            String stringToInsert = " " + name + "=" + '"' + value + '"';
            
            // Get the document and lock it
            BaseDocument doc = (BaseDocument)support.getDocument();
            doc.atomicLock();
            try {            
                // An attribute with the name was not found for the element
                // Let's add it to the end
                int insertStart = offset + length - 1;

                SCAN_LOOP:
                    for (TokenItem next = first().getNext(); next != null; next = next.getNext()) {
                        TokenID id = next.getTokenID();
                        if (id == ARGUMENT) {
                            while (next.getTokenID() != VALUE) {
                                next = next.getNext();
                                if (next == null) break SCAN_LOOP;
                            }

                            if (next == null) break SCAN_LOOP;

                            String image = next.getImage();
                            char test = image.charAt(0);

                            while (next.getTokenID() == VALUE || next.getTokenID() == CHARACTER) {
                                String actualValue = Util.actualAttributeValue(image);
                                if (!actualValue.equals(image)) {
                                    insertStart = next.getOffset() + actualValue.length();
                                    break SCAN_LOOP;
                                }
                                next = next.getNext();
                                if (next == null) break SCAN_LOOP;

                                // Check if this is the last token in the element and set the
                                // insertStart if it is
                                image = next.getImage();
                                insertStart = next.getOffset();
                                if (image.length() > 0 && image.charAt(image.length() - 1) == '>') {
                                    // The element is closing
                                    insertStart += image.length() - 1;
                                    if (image.length() > 1 && image.charAt(image.length() - 2) == '/') {
                                        // We have a closed element at the form <blu/>
                                        insertStart--;
                                    }
                                }
                            }

                            if (next == null) break SCAN_LOOP;
                        } else if (id == WS) {
                            // just skip
                        } else {
                            break; // end of element markup
                        }
                    }
                
                    doc.insertString(insertStart, stringToInsert, null);
                    doc.invalidateSyntaxMarks();
                } catch( BadLocationException e ) {
                    throw new DOMException(DOMException.INVALID_STATE_ERR , e.getMessage());
                } finally {
                    doc.atomicUnlock();
                }
        }
        
        // Update this object's member variables
//        retokenizeObject();
    }
    
    public final void removeAttribute(String name) {
        throw new ROException();
    }
    
    public Attr getAttributeNode(String name) {
        NamedNodeMap map = getAttributes();
        Node node = map.getNamedItem(name);
        return (Attr) node;
    }
    
    public final Attr setAttributeNode(Attr attribute) {
        throw new ROException();
    }
    
    public final Attr removeAttributeNode(Attr attribute) {
        throw new ROException();
    }
    
    public NodeList getElementsByTagName(String name) {
        throw new ROException();
    }
    
    /**
     * Returns previous sibling by locating pairing start tag
     * and asking it for previous non-start tag SyntaxNode.
     */
    public Node getPreviousSibling() {
        SyntaxNode prev = getStartTag();
        if (prev == null) return null;
        prev = findPrevious(prev);
        if (prev instanceof StartTag) {
            return null;
        } else {
            return prev;
        }
    }
    
    /**
     * Returns next sibling by locating pairing end tag
     * and asking it for next non-end tag SyntaxNode.
     */
    public Node getNextSibling() {
        SyntaxNode next = getEndTag();
        if (next == null) return null;
        next = findNext(next);
        if (next instanceof EndTag) {
            return null;
        } else {
            return next;
        }
    }
    
    public Node getFirstChild() {
        NodeList list = getChildNodes();
        if (list.getLength() == 0) return null;
        return getChildNodes().item(0);
    }
    
    public Node getLastChild() {
        NodeList list = getChildNodes();
        if (list.getLength() == 0) return null;
        return list.item(list.getLength());
    }
    
    protected abstract Tag getStartTag();
    
    protected abstract Tag getEndTag();
    
    //    public boolean equals(Object obj) {
    //        if ((obj instanceof Tag) == false) return false;
    //        return false;
    //    }
    
    
    // unsupported DOM level 2 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    public String getAttributeNS(String namespaceURI, String localName) {
        throw new UOException();
    }
    
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
        throw new UOException();
    }
    
    public void removeAttributeNS(String namespaceURI, String localName) {
        throw new UOException();
    }
    
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        throw new UOException();
    }
    
    public Attr setAttributeNodeNS(Attr newAttr) {
        throw new UOException();
    }
    
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        throw new UOException();
    }
    
    public boolean hasAttribute(String name) {
        throw new UOException();
    }
    
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        throw new UOException();
    }
    
//    public void retokenizeObject() {
//        // Update this object's member variables
//        try {
//            first = support.getTokenChain(offset, support.getDocument().getLength());
//        } catch (BadLocationException e) {
//            throw new DOMException(DOMException.INVALID_STATE_ERR , e.getMessage());
//        }
//    }

    /**
     * We guarantee DOM Node equality by using Java Object's equals.
     * It's potentionally dangerous as it mixes StartTags and EndTags.
     * Never put this object into vector of so until you assumes DOM Node
     * equality.
     * <p>
     * I would appreciate a methos at DOM that would define
     * Node equals not Objevt's equals.
     */
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Tag) {
            Tag tag = (Tag) obj;
            Tag t1 = tag.getStartTag();
            Tag t2 = getStartTag();
            if (t1 == null || t2 == null) return false;
            return t1.superEquals(t2);
        }
        return false;
    }

    private boolean superEquals(Tag tag) {
        return super.equals(tag);
    }

    /**
     * The same as for equals it's DOM node hashcode.
     */
    public final int hashCode() {
        Tag tag = getStartTag();
        if (tag == null || tag == this) {
            return super.hashCode();
        } else {
            return tag.hashCode();
        }
    }
}

