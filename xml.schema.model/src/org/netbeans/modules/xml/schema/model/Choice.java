/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * $Id$
 */


package org.netbeans.modules.xml.schema.model;

/**
 * This interface represents a choice outside a definition of a group. 
 * @author Chris Webster
 */
public interface Choice extends CommonChoice, ComplexExtensionDefinition, 
ComplexTypeDefinition, SequenceDefinition, SchemaComponent {
        public static final String MAX_OCCURS_PROPERTY  = "maxOccurs";
	public static final String MIN_OCCURS_PROPERTY  = "minOccurs";
    
        /**
         * @return mmaxOccurs attribute value if set, otherwise null.
         */
	String getMaxOccurs();
        /**
         * Set maxOccurs attribute value.
         * @param mmx minOccurs value; null value means reset to default.
         */
	void setMaxOccurs(String max);
	
        /**
         * @return default values for attribute minOccurs.
         */
        String getMaxOccursDefault();
        
        /**
         * @return the actual value set by user or default value if not set.
         */
        String getMaxOccursEffective();
        
        /**
         * @return minOccurs attribute value if set, otherwise null.
         */
	Integer getMinOccurs();
        
        /**
         * Set minOccurs attribute value.
         * @param min minOccurs value; null value means reset to default.
         */
	void setMinOccurs(Integer min);

        /**
         * @return default value for attribute minOccurs.
         */
        int getMinOccursDefault();
        
        /**
         * @return the actual value set by user or default value if not set.
         */
        int getMinOccursEffective();
}
