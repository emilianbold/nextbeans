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

package org.netbeans.modules.xml.wsdlextui.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingFault;
import org.netbeans.modules.xml.wsdl.model.BindingInput;
import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.BindingOutput;
import org.netbeans.modules.xml.wsdl.model.ExtensibilityElement;
import org.netbeans.modules.xml.wsdl.model.Fault;
import org.netbeans.modules.xml.wsdl.model.Input;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.wsdl.model.Output;
import org.netbeans.modules.xml.wsdl.model.Part;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPBinding;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPBody;
import org.netbeans.modules.xml.wsdl.ui.spi.ValidationInfo;
import org.netbeans.modules.xml.xam.Reference;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.openide.util.NbBundle;

/**
 *
 * @author radval
 */
public class SoapBindingValidator {
    
    private List<ValidationInfo> mVList = new ArrayList<ValidationInfo>();
    
    /** Creates a new instance of SoapBindingValidator */
    public SoapBindingValidator() {
    }
    
    /**
     * validate Binding and its child elements based on
     * the information available in corresponding PortType. This will be called
     * when binding user goes from portType configuration wizard to binding
     * configuration or when user changes subtype of binding in binding configuration.
     * Note this binding is not yet added to definition.
     * @param binding Binding for portType
     */
    public List<ValidationInfo> validate(Binding binding) {
        SOAPBinding.Style style = null;
        
        List<ExtensibilityElement> ee = binding.getExtensibilityElements();
        Iterator<ExtensibilityElement> it = ee.iterator();
        while(it.hasNext()) {
            ExtensibilityElement e = it.next();
            if(e instanceof SOAPBinding) {
                SOAPBinding sBinding = (SOAPBinding) e;
                style = sBinding.getStyle();
                break;
            }
        }
        
        if(style != null) {
            
            Collection<BindingOperation> bOps = binding.getBindingOperations();
            Iterator<BindingOperation> itBops = bOps.iterator();
            while(itBops.hasNext()) {
                BindingOperation op = itBops.next();
                processBindingOperation(style, op);
            }
        }
        
        return this.mVList;
        
    }
    
    private void processBindingOperation(SOAPBinding.Style style, BindingOperation bindingOperation) {
        BindingInput bIn = bindingOperation.getBindingInput();
        if (bIn != null) {
            processBindingOperationInput(style, bIn);
        }
        
        BindingOutput bOut = bindingOperation.getBindingOutput();
        if (bOut != null) { 
            processBindingOperationOutput(style, bOut);
        }
        
        Collection<BindingFault> bFaults = bindingOperation.getBindingFaults();
        if (bFaults != null && !bFaults.isEmpty()) {
            Iterator<BindingFault> it = bFaults.iterator();
            while(it.hasNext()) {
                BindingFault bFault = it.next();
                processBindingOperationFault(style, bFault);
            }
        }
    }
    
    
    private void processBindingOperationInput(SOAPBinding.Style style, BindingInput bIn) {
            List<ExtensibilityElement> eeList = bIn.getExtensibilityElements();
            Iterator<ExtensibilityElement> it =  eeList.iterator();
            while(it.hasNext()) {
                ExtensibilityElement ee = it.next();
                if(ee instanceof SOAPBody) {
                    SOAPBody sBody = (SOAPBody) ee;
                    SOAPBody.Use use = sBody.getUse();
                    if(use != null) {
                        Reference<Input> inputRef = bIn.getInput();
                        if(inputRef != null && inputRef.get() != null) {
                            Input input = inputRef.get();
                            NamedComponentReference<Message> messageRef =  input.getMessage();
                            if(use.equals(SOAPBody.Use.LITERAL)) {
                                if(style.equals(SOAPBinding.Style.DOCUMENT)) {
                                    processSoapBodyForDocumentLiteral(messageRef);
                                } else if (style.equals(SOAPBinding.Style.RPC)) {
                                    processSoapBodyForRPCLiteral(messageRef);
                                }
                            }

                        }
                        
                    }
                    
                }
            }
        
    }
    
    private void processBindingOperationOutput(SOAPBinding.Style style, BindingOutput bOut ) {
        if(style.equals(SOAPBinding.Style.DOCUMENT)) {
            List<ExtensibilityElement> eeList = bOut.getExtensibilityElements();
            Iterator<ExtensibilityElement> it =  eeList.iterator();
            while(it.hasNext()) {
                ExtensibilityElement ee = it.next();
                if(ee instanceof SOAPBody) {
//                    SOAPBody sBody = (SOAPBody) ee;
//                    Reference<Output> outputRef = bOut.getOutput();
//                    if(outputRef != null && outputRef.get() != null) {
//                        Output output = outputRef.get();
//                        NamedComponentReference<Message> messageRef =  output.getMessage();
//                        processSoapBodyForDocumentLiteral(messageRef);
//                        
//                    }
                    SOAPBody sBody = (SOAPBody) ee;
                    SOAPBody.Use use = sBody.getUse();
                    if(use != null) {
                        Reference<Output> outputRef = bOut.getOutput();
                        if(outputRef != null && outputRef.get() != null) {
                            Output output = outputRef.get();
                            NamedComponentReference<Message> messageRef =  output.getMessage();
                            if(use.equals(SOAPBody.Use.LITERAL)) {
                                if(style.equals(SOAPBinding.Style.DOCUMENT)) {
                                    processSoapBodyForDocumentLiteral(messageRef);
                                } else if (style.equals(SOAPBinding.Style.RPC)) {
                                    processSoapBodyForRPCLiteral(messageRef);
                                }
                            }

                        }
                        
                    }
                }
            }
        }
    }
    
    private void processBindingOperationFault(SOAPBinding.Style style, BindingFault bFault) {
        if(style.equals(SOAPBinding.Style.DOCUMENT)) {
            List<ExtensibilityElement> eeList = bFault.getExtensibilityElements();
            Iterator<ExtensibilityElement> it =  eeList.iterator();
            while(it.hasNext()) {
                ExtensibilityElement ee = it.next();
                if(ee instanceof SOAPBody) {
                    SOAPBody sBody = (SOAPBody) ee;
                    SOAPBody.Use use = sBody.getUse();
                    if(use != null) {
                        Reference<Fault> faultRef = bFault.getFault();
                        if(faultRef != null && faultRef.get() != null) {
                            Fault fault = faultRef.get();
                            NamedComponentReference<Message> messageRef =  fault.getMessage();
                            if(use.equals(SOAPBody.Use.LITERAL)) {
                                if(style.equals(SOAPBinding.Style.DOCUMENT)) {
                                    processSoapBodyForDocumentLiteral(messageRef);
                                } else if (style.equals(SOAPBinding.Style.RPC)) {
                                    processSoapBodyForRPCLiteral(messageRef);
                                }
                            }

                        }
                        
                    }
                }
            }
        }
    }
    
    private void processSoapBodyForDocumentLiteral(NamedComponentReference<Message> messageRef) {
        if(messageRef != null && messageRef.get() != null) {
            Message message = messageRef.get();
            if(message.getParts().size() > 1) {
                String desc = NbBundle.getMessage(SoapBindingValidator.class, "LBL_doc_literal_R2210");
                ValidationInfo vInfo = 
                        new ValidationInfo(ValidationInfo.ValidationType.ERROR, desc);
                this.mVList.add(vInfo);        
            } else {
                Collection<Part> parts = message.getParts();
                Iterator<Part> it = parts.iterator();
                if(it.hasNext()) {
                    Part part = it.next();
                    String typeValue = part.getAttribute(new StringAttribute("type"));
                    
                    if(typeValue != null && !typeValue.trim().equals("")) {
                    //Not to add schema model dependency
                    //if(part.getType() != null && part.getType().get() != null) {
                        String desc = NbBundle.getMessage(SoapBindingValidator.class, "LBL_doc_literal_R2204");
                        ValidationInfo vInfo = 
                            new ValidationInfo(ValidationInfo.ValidationType.ERROR, desc);
                        this.mVList.add(vInfo);        
                    }
                }
            }
        }
    }
    
    private void processSoapBodyForRPCLiteral(NamedComponentReference<Message> messageRef) {
        if(messageRef != null && messageRef.get() != null) {
            Message message = messageRef.get();
                Collection<Part> parts = message.getParts();
                Iterator<Part> it = parts.iterator();
                if(it.hasNext()) {
                    Part part = it.next();
                    String elementValue = part.getAttribute(new StringAttribute("element"));
                    
                    if(elementValue != null && !elementValue.trim().equals("")) {
                    //Not to add schema model dependency
                    //if(part.getElement() != null && part.Element().get() != null) {
                        String desc = NbBundle.getMessage(SoapBindingValidator.class, "LBL_rpc_literal_R2203");
                        ValidationInfo vInfo = 
                            new ValidationInfo(ValidationInfo.ValidationType.ERROR, desc);
                        this.mVList.add(vInfo);        
                    }
                }
            
        }
    }
}
