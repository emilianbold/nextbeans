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

package org.netbeans.modules.profiler.ppoints.ui;

import org.netbeans.lib.profiler.common.ProfilingSettings;
import org.netbeans.modules.profiler.ppoints.ProfilingPoint;
import org.netbeans.modules.profiler.ppoints.ProfilingPointsManager;
import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;


/**
 *
 * @author Jiri Sedlacek
 */
@NbBundle.Messages({
    "ProfilingPointsDisplayer_PpActiveMsg=Profiling Points Active in {0}",
    "ProfilingPointsDisplayer_NoActivePpsString=<No profiling points active for current configuration>",
    "ProfilingPointsDisplayer_ListAccessName=Active profiling points for the configuration"
})
public class ProfilingPointsDisplayer extends JPanel implements HelpCtx.Provider {
    //~ Static fields/initializers -----------------------------------------------------------------------------------------------

    private static final String HELP_CTX_KEY = "ProfilingPointsDisplayer.HelpCtx"; // NOI18N
    private static final HelpCtx HELP_CTX = new HelpCtx(HELP_CTX_KEY);
    
    private static ProfilingPointsDisplayer defaultInstance;

    //~ Instance fields ----------------------------------------------------------------------------------------------------------

    private DefaultListModel listModel;
    private JList list;

    //~ Constructors -------------------------------------------------------------------------------------------------------------

    private ProfilingPointsDisplayer() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------
    
    public HelpCtx getHelpCtx() {
        return HELP_CTX;
    }

    public static void displayProfilingPoints(Lookup.Provider project, ProfilingSettings settings) {
        ProfilingPointsDisplayer ppd = getDefault();
        ppd.setupDisplay(project, settings);

        final DialogDescriptor dd = new DialogDescriptor(ppd,
                                                         Bundle.ProfilingPointsDisplayer_PpActiveMsg(settings.getSettingsName()), 
                                                         true,
                                                         new Object[] { DialogDescriptor.OK_OPTION }, DialogDescriptor.OK_OPTION,
                                                         0, null, null);
        final Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.pack();
        d.setVisible(true);

        ppd.cleanup();
    }

    private static ProfilingPointsDisplayer getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new ProfilingPointsDisplayer();
        }

        return defaultInstance;
    }

    private void cleanup() {
        listModel.removeAllElements();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.getAccessibleContext().setAccessibleName(Bundle.ProfilingPointsDisplayer_ListAccessName());
        list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(6);
        list.setCellRenderer(org.netbeans.modules.profiler.ppoints.Utils.getPresenterListRenderer());

        JScrollPane listScroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroll.setPreferredSize(new Dimension(405, listScroll.getPreferredSize().height));

        add(listScroll, BorderLayout.CENTER);
    }

    private void setupDisplay(Lookup.Provider project, ProfilingSettings settings) {
        List<ProfilingPoint> compatibleProfilingPoints = ProfilingPointsManager.getDefault()
                                                                               .getCompatibleProfilingPoints(project, settings,
                                                                                                             true);
        listModel.removeAllElements();

        if (compatibleProfilingPoints.size() == 0) {
            listModel.addElement(Bundle.ProfilingPointsDisplayer_NoActivePpsString());
        } else {
            for (ProfilingPoint profilingPoint : compatibleProfilingPoints) {
                listModel.addElement(profilingPoint);
            }
        }
    }
}
