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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.glassfish.common.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.glassfish.spi.GlassfishModule;
import org.netbeans.modules.glassfish.spi.GlassfishModule.ServerState;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Peter Williams
 */
public class ViewAdminConsoleAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        GlassfishModule commonSupport = activatedNodes[0].getLookup().lookup(GlassfishModule.class);
        if(commonSupport != null) {
            if(commonSupport.getServerState() == ServerState.RUNNING) {
                try {
                    Map<String, String> ip = commonSupport.getInstanceProperties();
                    String host = ip.get(GlassfishModule.HOSTNAME_ATTR);
                    String httpPort = ip.get(GlassfishModule.HTTPPORT_ATTR);
                    URL url = new URL("http://" + host + ":" + httpPort + "/admin");
                    URLDisplayer.getDefault().showURL(url);
                } catch (MalformedURLException ex) {
                    Logger.getLogger("glassfish").log(Level.WARNING, ex.getLocalizedMessage(), ex);
                }
            } else {
                String message = NbBundle.getMessage(ViewAdminConsoleAction.class, 
                        "MSG_ServerMustBeRunning");
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(message,
                        NotifyDescriptor.DEFAULT_OPTION);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return activatedNodes != null && activatedNodes.length == 1;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ViewAdminConsoleAction.class, "CTL_ViewAdminConsoleAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
