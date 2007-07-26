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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.websvc.rest.model.impl;

import javax.lang.model.element.Element;
import org.netbeans.modules.websvc.rest.model.api.RestMethodDescription;

/**
 *
 * @author Peter Liu
 */
class RestMethodDescriptionFactory {

    public static RestMethodDescriptionImpl create(Element element) {
            
        if (Utils.hasHttpMethod(element)) {
            return new HttpMethodImpl(element);
        }
        
        if (Utils.hasUriTemplate(element)) {
            return new SubResourceLocatorImpl(element);
        }
        
        return null;
    }
}
