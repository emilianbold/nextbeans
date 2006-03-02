/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * $Id$
 */

package org.netbeans.modules.xml.schema.model.impl;

import org.netbeans.modules.xml.schema.model.Notation;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.visitor.SchemaVisitor;
import org.w3c.dom.Element;

/**
 *
 * @author Nam Nguyen
 */
public class NotationImpl extends NamedImpl implements Notation {
    
    /** Creates a new instance of NotationImpl */
    public NotationImpl(SchemaModelImpl model) {
        this(model,createNewComponent(SchemaElements.DOCUMENTATION,model));
    }
    
    /**
     * Creates a new instance of DocumentationImpl
     */
    public NotationImpl(SchemaModelImpl model, Element el) {
        super(model, el);
    }

    public void setSystemIdentifier(String systemID) {
        setAttribute(SYSTEM_PROPERTY, SchemaAttributes.SYSTEM, systemID);
    }

    public String getSystemIdentifier() {
        return getAttribute(SchemaAttributes.SYSTEM);
    }

    public void setPublicIdentifier(String publicID) {
        setAttribute(SYSTEM_PROPERTY, SchemaAttributes.PUBLIC, publicID);
    }

    public String getPublicIdentifier() {
        return getAttribute(SchemaAttributes.PUBLIC);
    }

    public void accept(SchemaVisitor v) {
        v.visit(this);
    }

    public Class<? extends SchemaComponent> getComponentType() {
        return Notation.class;
    }
    
}
