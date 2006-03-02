/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * $Id$
 */

/*
 * IncludeImpl.java
 *
 * Created on October 3, 2005, 3:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.xml.schema.model.impl;

import org.netbeans.modules.xml.schema.model.Include;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.visitor.SchemaVisitor;
import org.w3c.dom.Element;

/**
 *
 * @author rico
 */
public class IncludeImpl extends SchemaComponentImpl implements Include{
    
    protected IncludeImpl(SchemaModelImpl model){
        this(model, createNewComponent(SchemaElements.INCLUDE, model));
    }
    
    public IncludeImpl(SchemaModelImpl model, Element el){
        super(model,el);
    }

	/**
	 *
	 *
	 */
	public Class<? extends SchemaComponent> getComponentType() {
		return Include.class;
	}
    
    /**
     * Visitor
     */
    public void accept(SchemaVisitor visitor) {
        visitor.visit(this);
    }
    
    /**
     *
     */
    public void setSchemaLocation(String uri) {
        setAttribute(SCHEMA_LOCATION_PROPERTY, SchemaAttributes.SCHEMA_LOCATION, uri);
    }
    
    /**
     *
     */
    public String getSchemaLocation() {
        return getAttribute(SchemaAttributes.SCHEMA_LOCATION);
    }
}
