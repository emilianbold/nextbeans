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

package org.netbeans.modules.refactoring.java.ui.elements;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/** "Radio button" type action, base class designed for subclassing
 *
 * @author Dafe Simonek
 */
@NbBundle.Messages({"LBL_SortByName=Sort by name", "LBL_SortBySource=Sort by source"})
public abstract class SortActionSupport extends AbstractAction implements Presenter.Popup {
    
    private JRadioButtonMenuItem menuItem;
    
    /** Creates a new instance of SortByNameAction */
    public SortActionSupport () {
    }
    
    public final JMenuItem getPopupPresenter() {
        JMenuItem result = obtainMenuItem();
        updateMenuItem();
        return result;
    }
    
    protected final JRadioButtonMenuItem obtainMenuItem () {
        if (menuItem == null) {
            menuItem = new JRadioButtonMenuItem((String)getValue(Action.NAME)); 
            menuItem.setAction(this);
        }
        return menuItem;
    }
    
    protected abstract void updateMenuItem ();
    
    /** Enables sorting by names when selected
     */
    public static final class SortByNameAction extends SortActionSupport {
        private final DescriptionFilter filter;
        
        public SortByNameAction (DescriptionFilter filter) {
            super();
            putValue(Action.NAME, NbBundle.getMessage(SortByNameAction.class, "LBL_SortByName")); //NOI18N
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/java/resources/sortAlpha.png", false)); //NOI18N
            this.filter = filter;
        }
    
        public void actionPerformed (ActionEvent e) {
            filter.setNaturalSort(false);
            updateMenuItem();
        }

        protected void updateMenuItem () {
            JRadioButtonMenuItem mi = obtainMenuItem();
            mi.setSelected(!filter.isNaturalSort());
        }
    } // end of SortByNameAction

    /** Enables sorting by names when selected
     */
    public static final class SortBySourceAction extends SortActionSupport {
        private final DescriptionFilter filter;
        
        public SortBySourceAction (DescriptionFilter filter) {
            super();
            putValue(Action.NAME, NbBundle.getMessage(SortBySourceAction.class, "LBL_SortBySource")); //NOI18N
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/java/resources/sortPosition.png", false)); //NOI18N
            this.filter = filter;
        }
    
        public void actionPerformed (ActionEvent e) {
            filter.setNaturalSort(true);
            updateMenuItem();
        }

        protected void updateMenuItem () {
            JRadioButtonMenuItem mi = obtainMenuItem();
            mi.setSelected(filter.isNaturalSort());
        }
    } // end of SortBySourceAction
    
    
}
