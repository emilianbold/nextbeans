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
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.wsitmodelext.addressing.impl;

import org.netbeans.modules.websvc.wsitmodelext.addressing.AddressingQName;
import org.netbeans.modules.websvc.wsitmodelext.addressing.ReferenceParameters;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.visitor.WSDLVisitor;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Grebac
 */
public class ReferenceParametersImpl extends AddressingComponentImpl implements ReferenceParameters {
    
    /**
     * Creates a new instance of ReferenceParametersImpl
     */
    public ReferenceParametersImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public ReferenceParametersImpl(WSDLModel model){
        this(model, createPrefixedElement(AddressingQName.REFERENCEPARAMETERS.getQName(), model));
    }

    public void setReferenceParameters(String referenceParameters) {
        setText(REFERENCEPARAMETERS_CONTENT_VALUE_PROPERTY, referenceParameters);
    }

    public String getReferenceParameters() {
        return getText();
    }

    @Override
    public void accept(WSDLVisitor visitor) {
        visitor.visit(this);
    }
    
}
