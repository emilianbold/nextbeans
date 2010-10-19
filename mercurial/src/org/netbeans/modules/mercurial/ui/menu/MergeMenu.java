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

package org.netbeans.modules.mercurial.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.modules.mercurial.MercurialAnnotator;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.netbeans.modules.mercurial.ui.merge.MergeAction;
import org.netbeans.modules.mercurial.ui.update.ConflictResolvedAction;
import org.netbeans.modules.mercurial.ui.update.ResolveConflictsAction;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.SystemActionBridge;

/**
 * Container menu for branch actions.
 *
 * @author Maros Sandor
 */
public class MergeMenu extends DynamicMenu {
    private boolean bShowMarkAsResolved;

    public MergeMenu(boolean bShowMarkAsResolved) {
        super(NbBundle.getMessage(MergeMenu.class, "CTL_MenuItem_MergeMenu"));
        this.bShowMarkAsResolved = bShowMarkAsResolved;
    }

    @Override
    protected JMenu createMenu() {
        JMenu menu = new JMenu(this);
        org.openide.awt.Mnemonics.setLocalizedText(menu, NbBundle.getMessage(MergeMenu.class, "CTL_MenuItem_MergeMenu"));
        
        JMenuItem item = menu.add(new SystemActionBridge(SystemAction.get(MergeAction.class), NbBundle.getMessage(MercurialAnnotator.class, "CTL_PopupMenuItem_Merge"))); //NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(item, item.getText());

        item = menu.add(new SystemActionBridge(SystemAction.get(ResolveConflictsAction.class), NbBundle.getMessage(MercurialAnnotator.class, "CTL_PopupMenuItem_Resolve"))); //NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(item, item.getText());
        if(bShowMarkAsResolved){
            item = menu.add(new SystemActionBridge(SystemAction.get(ConflictResolvedAction.class), NbBundle.getMessage(MercurialAnnotator.class, "CTL_PopupMenuItem_MarkResolved"))); //NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(item, item.getText());
        }
        return menu;
    }
}
