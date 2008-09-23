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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.db.explorer.actions;

import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.modules.db.explorer.DbUtilities;

import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.modules.db.explorer.dlg.AddTableColumnDialog;
import org.netbeans.modules.db.explorer.infos.DatabaseNodeInfo;
import org.netbeans.modules.db.explorer.infos.TableNodeInfo;

public class AddColumnAction extends DatabaseAction {

    static final long serialVersionUID =5894518352294344657L;
    
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1)
            return true;
        else
            return false;
    }
    
    public void performAction (Node[] activatedNodes) {
        Node node;
        if (activatedNodes != null && activatedNodes.length == 1)
            node = activatedNodes[0];
        else
            return;

        DatabaseNodeInfo info = (DatabaseNodeInfo) node.getCookie(DatabaseNodeInfo.class);
        final TableNodeInfo nfo = (TableNodeInfo) info.getParent(nodename);
        try {
            AddTableColumnDialog dlg = new AddTableColumnDialog((Specification) nfo.getSpecification(), nfo);
            if (dlg.run()) {
                nfo.addColumn(dlg.getColumnName());
                RequestProcessor.getDefault().post(
                    new Runnable() {
                        public void run() {
                            try {
                                nfo.refreshChildren();
                            } catch ( DatabaseException de ) {
                                DbUtilities.reportError(bundle().getString("ERR_UnableToAddColumn"), de.getMessage()); // NOI18N
                            }
                        }
                    }
                );
            }
        } catch(Exception exc) {
            DbUtilities.reportError(bundle().getString("ERR_UnableToAddColumn"), exc.getMessage()); // NOI18N
        }
    }
}
