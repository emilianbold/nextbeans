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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.bpel.model.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.netbeans.modules.bpel.model.api.BpelEntity;
import org.netbeans.modules.bpel.model.api.Documentation;
import org.netbeans.modules.bpel.model.api.ExtensibleElements;
import org.netbeans.modules.bpel.model.api.ExtensionEntity;
import org.netbeans.modules.bpel.model.api.events.EntityInsertEvent;
import org.netbeans.modules.bpel.model.api.events.VetoException;
import org.netbeans.modules.bpel.model.xam.BpelElements;
import org.netbeans.modules.bpel.model.xam.BpelTypes;
import org.netbeans.modules.bpel.model.xam.BpelTypesEnum;
import org.netbeans.modules.xml.xam.dom.Attribute;
import org.w3c.dom.Element;

/**
 * @author ads
 */
public abstract class ExtensibleElementsImpl extends BpelContainerImpl implements ExtensibleElements {

    ExtensibleElementsImpl( BpelModelImpl model, Element e ) {
        super(model, e);
    }

    ExtensibleElementsImpl( BpelBuilderImpl builder, String tagName ) {
        super(builder, tagName);
    }

    public String getDocumentation() {
      Documentation [] documentations = getDocumentations();

      if (documentations == null || documentations.length == 0) {
        return null;
      }
      Documentation documentation = documentations[0];
      
      if (documentation == null) {
        return null;
      }
      String content = documentation.getContent();

      if (content == null) {
        return null;
      }
      content = content.trim();

      if (content.length() == 0) {
        return null;
      }
      return content;
    }

    public void setDocumentation(String content) throws VetoException {
      Documentation documentation = getBpelModel().getBuilder().createDocumentation();
      documentation.setContent(content);

      if (sizeOfDocumentations() > 0) {
        if (content == null || content.trim().length() == 0) {
          removeDocumentation(0);
        }
        else {
          setDocumentation(documentation, 0);
        }
      }
      else {
        insertDocumentation(documentation, 0);
      }
    }

    public void removeDocumentation() throws VetoException {
      setDocumentation(null);
    }

    public void addDocumentation(Documentation documentation) {
        addChildAfter(documentation, Documentation.class, BpelTypesEnum.DOCUMENTATION);
    }

    public Documentation getDocumentation( int i ) {
        return getChild( Documentation.class , i );
    }

    public Documentation[] getDocumentations() {
        readLock();
        try {
            List<Documentation> list = getChildren( Documentation.class );
            return list.toArray( new Documentation[list.size()] );
        }
        finally {
            readUnlock();
        }
    }

    public void insertDocumentation( Documentation documentation, int i ) {
        insertAtIndexAfter( documentation , Documentation.class , i , 
                BpelTypesEnum.DOCUMENTATION );
    }

    public void removeDocumentation( int i ) {
        removeChild( Documentation.class , i );
    }

    public void setDocumentation( Documentation documentation, int i ) {
        setChildAtIndex( documentation , Documentation.class , i );
    }

    public void setDocumentations( Documentation[] documentations ) {
        setArrayAfter( documentations , Documentation.class , 
                BpelTypesEnum.DOCUMENTATION);
    }

    public int sizeOfDocumentations() {
        readLock();
        try {
            return getChildren( Documentation.class ).size();
        }
        finally {
            readUnlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.bpel.model.api.ExtensibleElements#addExtensionEntity(Class<T>, T)
     */
    public <T extends ExtensionEntity> void addExtensionEntity(Class<T> clazz, T entity) {
        assert entity.canExtend(this);
        addChildAfter(entity, clazz, BpelTypesEnum.DOCUMENTATION);
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.bpel.model.api.BpelContainer#getExtensionChildren()
     */
    public List<ExtensionEntity> getExtensionChildren() {
        return getChildren( ExtensionEntity.class );
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.soa.model.bpel20.impl.BpelContainerImpl#create(org.w3c.dom.Element)
     */
    @Override
    protected BpelEntity create( Element element )
    {
        if ( BpelElements.DOCUMENTATION.getName().equals( element.getLocalName()) ){
            return new DocumentationImpl( getModel() , element );
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.netbeans.modules.soa.model.bpel20.impl.BpelEntityImpl#getDomainAttributes()
     */
    protected Attribute[] getDomainAttributes() {
        // TODO : common framework for accesing to extension attributes ? 
        if ( myAttributes.get() == null ){
            Attribute[] ret = new Attribute[0];
            myAttributes.compareAndSet(null, ret);
        }
        return myAttributes.get();
    }
    
    private static AtomicReference<Attribute[]> myAttributes = 
        new AtomicReference<Attribute[]>();
}
