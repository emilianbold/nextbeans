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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.websvc.jaxrpc.actions;

import org.openide.util.actions.CookieAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.j2ee.dd.api.webservices.DDProvider;
import org.netbeans.modules.j2ee.dd.api.webservices.WebserviceDescription;
import org.netbeans.modules.j2ee.dd.api.webservices.Webservices;
import org.netbeans.modules.j2ee.dd.api.webservices.PortComponent;
import org.netbeans.modules.j2ee.dd.api.webservices.ServiceImplBean;
import org.netbeans.modules.websvc.api.webservices.WebServicesSupport;
import org.netbeans.modules.websvc.core.AddOperationCookie;
import org.netbeans.modules.websvc.core.WebServiceActionProvider;

public class AddOperationAction extends CookieAction {
    //private Service service;
    public String getName() {
        return NbBundle.getMessage(AddOperationAction.class, "LBL_OperationAction");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(AddOperationAction.class);
    }
    
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {AddOperationCookie.class};
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected boolean enable(Node[] activatedNodes) {
        return activatedNodes.length == 1 &&
                isWsImplBeanOrInterface(activatedNodes[0]);
    }
    
    private boolean isWsImplBeanOrInterface(Node node) {
        FileObject implClassFo = node.getLookup().lookup(FileObject.class);
        if(implClassFo==null) return false;
        WebserviceDescription wsDesc = findWSDescriptionFromClass(implClassFo);
        if (wsDesc != null) {
            WebServicesSupport wsSupport = WebServicesSupport.getWebServicesSupport(implClassFo);
            assert wsSupport != null;
            return !wsSupport.isFromWSDL(wsDesc.getWebserviceDescriptionName());
        }
        return false;
    }
    
    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return;
        }
        
        FileObject implClassFo = activatedNodes[0].getLookup().lookup(FileObject.class);
        if (implClassFo!=null) {
            AddOperationCookie addOperationCookie = WebServiceActionProvider.getAddOperationAction(implClassFo);
            if (addOperationCookie!=null) addOperationCookie.addOperation(implClassFo);
        }
    }

    private static WebserviceDescription findWSDescriptionFromClass(FileObject implClassFO) {
        WebServicesSupport wsSupport = WebServicesSupport.getWebServicesSupport(implClassFO);
        String implClassPath = implClassFO.getPath();
        int dotIndex = implClassPath.lastIndexOf('.');
        if(dotIndex>0)implClassPath = implClassPath.substring(0,dotIndex);
        implClassPath = implClassPath.replaceAll("/", ".");
        if (wsSupport != null) {
            DDProvider wsDDProvider = DDProvider.getDefault();
            Webservices webServices = null;
            try {
                webServices = wsDDProvider.getDDRoot(wsSupport.getWebservicesDD());
            } catch(java.io.IOException e) {
                throw new RuntimeException(e.getMessage());
            }
		  
            if(webServices != null) {
                WebserviceDescription[] wsDescriptions = webServices.getWebserviceDescription();
                for (int i = 0; i < wsDescriptions.length; i++) {
                    WebserviceDescription wsDescription = wsDescriptions[i];
                    PortComponent portComponent = wsDescription.getPortComponent(0);
                    
                    // first check the interface
                    String wsSEI = portComponent.getServiceEndpointInterface();
                    if ((wsSEI != null) && (implClassPath.endsWith(wsSEI))) {
                        return wsDescription;
                    }
                    
                    // then the implementation bean
                    ServiceImplBean serviceImplBean = portComponent.getServiceImplBean();
                    String link = serviceImplBean.getServletLink();
                    if (link == null) {
                        link = serviceImplBean.getEjbLink();
                    }
                    String implBean = wsSupport.getImplementationBean(link);
                    if (implBean!=null && implClassPath.endsWith(implBean)) {
                        return wsDescription;
                    }
                }
            }
        }
        return null;
    }

}
