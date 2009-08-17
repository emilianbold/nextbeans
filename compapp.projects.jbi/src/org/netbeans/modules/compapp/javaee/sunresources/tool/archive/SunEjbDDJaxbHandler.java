/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.compapp.javaee.sunresources.tool.archive;

import java.util.Iterator;

import org.netbeans.modules.compapp.javaee.sunresources.generated.sunejb30.*;
import org.netbeans.modules.compapp.javaee.sunresources.tool.cmap.CMapNode;
import org.openide.util.NbBundle;

/**
 * @author echou
 *
 */
public class SunEjbDDJaxbHandler {

    private SunEjbJar root;
    
    public SunEjbDDJaxbHandler(Object root) throws Exception {
        if (root instanceof SunEjbJar) {
            this.root = (SunEjbJar) root;
        } else {
            throw new Exception(
                    NbBundle.getMessage(SunEjbDDJaxbHandler.class, "EXC_bad_jaxbroot", root.getClass()));
        }
    }
    
    public String findJndiByEjbName(String ejbName) {
        for (Iterator<Ejb> ejbIter = root.getEnterpriseBeans().getEjb().iterator();
            ejbIter.hasNext(); ) {
            Ejb ejb = ejbIter.next();
            if (ejb.getEjbName().equals(ejbName)) {
                return ejb.getJndiName();
            }
        }
        return null;
    }
    
    public String resolveResRef(String ejbName, String resRefName) {
        for (Iterator<Ejb> ejbIter = root.getEnterpriseBeans().getEjb().iterator();
            ejbIter.hasNext(); ) {
            Ejb ejb = ejbIter.next();
            for (Iterator<ResourceRef> resRefIter = ejb.getResourceRef().iterator();
                resRefIter.hasNext(); ) {
                ResourceRef resRef = resRefIter.next();
                if (resRef.getResRefName().equals(resRefName)) {
                    return resRef.getJndiName();
                }
            }
        }
        return null;
    }

    public String resolveMsgDestRef(String ejbName, String msgDestRefLink) {
        for (Iterator<MessageDestination> msgDestIter = root.getEnterpriseBeans().getMessageDestination().iterator(); 
            msgDestIter.hasNext(); ) {
            MessageDestination msgDest = msgDestIter.next();
            if (msgDest.getMessageDestinationName().equals(msgDestRefLink)) {
                return msgDest.getJndiName();
            }
        }
        return null;
    }
    
    public void resolveWebservice(CMapNode node, String ejbName, String serviceEndpoint, 
            WebservicesDDJaxbHandler webservicesDD) {
        for (Iterator<Ejb> ejbIter = root.getEnterpriseBeans().getEjb().iterator();
            ejbIter.hasNext(); ) {
            Ejb ejb = ejbIter.next();
            for (Iterator<WebserviceEndpoint> wsEndpointIter = ejb.getWebserviceEndpoint().iterator();
                wsEndpointIter.hasNext(); ) {
                WebserviceEndpoint wsEndpoint = wsEndpointIter.next();
                String portCompName = wsEndpoint.getPortComponentName();
                // resolve using webservice.xml
                if (webservicesDD != null) {
                    webservicesDD.resolvePortCompName(node, portCompName);
                }
            }
        }
    }
}
