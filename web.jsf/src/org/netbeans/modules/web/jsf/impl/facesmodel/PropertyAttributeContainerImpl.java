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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.web.jsf.impl.facesmodel;

import java.util.List;

import org.netbeans.modules.web.jsf.api.facesmodel.AttributeContainer;
import org.netbeans.modules.web.jsf.api.facesmodel.ConfigAttribute;
import org.netbeans.modules.web.jsf.api.facesmodel.Property;
import org.netbeans.modules.web.jsf.api.facesmodel.PropertyContainer;
import org.w3c.dom.Element;



/**
 * @author ads
 *
 */
abstract class PropertyAttributeContainerImpl extends JSFConfigComponentImpl 
    implements PropertyContainer , AttributeContainer 
{

    PropertyAttributeContainerImpl( JSFConfigModelImpl model,
            Element element )
    {
        super(model, element);
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.AttributeContainer#addAttribute(org.netbeans.modules.web.jsf.api.facesmodel.ConfigAttribute)
     */
    public void addAttribute( ConfigAttribute attribute ) {
        appendChild( ATTRIBUTE, attribute);
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.AttributeContainer#addAttribute(int, org.netbeans.modules.web.jsf.api.facesmodel.ConfigAttribute)
     */
    public void addAttribute( int index, ConfigAttribute attribute ) {
        insertAtIndex( ATTRIBUTE, attribute, index , ConfigAttribute.class);
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.AttributeContainer#getAttributes()
     */
    public List<ConfigAttribute> getAttributes() {
        return getChildren( ConfigAttribute.class );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.AttributeContainer#removeAttribute(org.netbeans.modules.web.jsf.api.facesmodel.ConfigAttribute)
     */
    public void removeAttribute( ConfigAttribute attribute ) {
        removeChild( ATTRIBUTE, attribute);
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.PropertyContainer#addProperty(int, org.netbeans.modules.web.jsf.api.facesmodel.Property)
     */
    public void addProperty( int index, Property property ) {
        insertAtIndex( PROPERTY , property, index , Property.class );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.PropertyContainer#addProperty(org.netbeans.modules.web.jsf.api.facesmodel.Property)
     */
    public void addProperty( Property property ) {
        appendChild( PROPERTY,  property );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.PropertyContainer#getProperties()
     */
    public List<Property> getProperties() {
        return getChildren( Property.class );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.jsf.api.facesmodel.PropertyContainer#removePropety(org.netbeans.modules.web.jsf.api.facesmodel.Property)
     */
    public void removePropety( Property property ) {
        removeChild( PROPERTY, property );
    }


}
