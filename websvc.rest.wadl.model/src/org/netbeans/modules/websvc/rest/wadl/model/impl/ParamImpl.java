/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008, 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2008.11.07 at 12:36:44 PM PST
//


package org.netbeans.modules.websvc.rest.wadl.model.impl;

import java.util.Collection;
import javax.xml.namespace.QName;
import org.netbeans.modules.websvc.rest.wadl.model.*;
import org.netbeans.modules.websvc.rest.wadl.model.visitor.WadlVisitor;
import org.w3c.dom.Element;

public class ParamImpl extends NamedImpl implements Param {

    /** Creates a new instance of OperationImpl */
    public ParamImpl(WadlModel model, Element e) {
        super(model, e);
    }

    public ParamImpl(WadlModel model){
        this(model, createNewElement(WadlQNames.PARAM.getQName(), model));
    }

    public Collection<Option> getOption() {
        return getChildren(Option.class);
    }

    public void addOption(Option option) {
        addAfter(OPTION_PROPERTY, option, TypeCollection.FOR_OPTION.types());
    }

    public void removeOption(Option option) {
        removeChild(OPTION_PROPERTY, option);
    }
    
    public Collection<Link> getLink() {
        return getChildren(Link.class);
    }

    public void addLink(Link option) {
        addAfter(LINK_PROPERTY, option, TypeCollection.FOR_LINK.types());
    }

    public void removeLink(Link option) {
        removeChild(LINK_PROPERTY, option);
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return getAttribute(WadlAttribute.NAME);
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String name) {
        setAttribute(NAME_PROPERTY, WadlAttribute.NAME, name);
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link ParamStyleImpl }
     *     
     */
    public String getStyle() {
        return getAttribute(WadlAttribute.STYLE);
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParamStyleImpl }
     *     
     */
    public void setStyle(String style) {
        setAttribute(STYLE_PROPERTY, WadlAttribute.STYLE, style);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return getAttribute(WadlAttribute.ID);
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String id) {
        setAttribute(ID_PROPERTY, WadlAttribute.ID, id);
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getType() {
        return new QName(getAttribute(WadlAttribute.TYPE));
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setType(QName type) {
        String typeStr = type.getLocalPart();
        if(type.getPrefix() != null)
            typeStr = type.getPrefix() + ":" + type.getLocalPart();
        setAttribute(TYPE_PROPERTY, WadlAttribute.TYPE, typeStr);
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefault() {
        return getAttribute(WadlAttribute.DEFAULT);
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefault(String value) {
        setAttribute(DEFAULT_PROPERTY, WadlAttribute.DEFAULT, value);
    }

    /**
     * Gets the value of the required property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public String getRequired() {
        return getAttribute(WadlAttribute.REQUIRED);
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequired(String required) {
        setAttribute(REQUIRED_PROPERTY, WadlAttribute.REQUIRED, required);
    }

    /**
     * Gets the value of the repeating property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public String getRepeating() {
        return getAttribute(WadlAttribute.REPEATING);
    }

    /**
     * Sets the value of the repeating property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRepeating(String repeating) {
        setAttribute(REPEATING_PROPERTY, WadlAttribute.REPEATING, repeating);
    }

    /**
     * Gets the value of the fixed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFixed() {
        return getAttribute(WadlAttribute.FIXED);
    }

    /**
     * Sets the value of the fixed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFixed(String fixed) {
        setAttribute(FIXED_PROPERTY, WadlAttribute.FIXED, fixed);
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return getAttribute(WadlAttribute.PATH);
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String path) {
        setAttribute(PATH_PROPERTY, WadlAttribute.PATH, path);
    }

    public void accept(WadlVisitor visitor) {
        visitor.visit(this);
    }

}
