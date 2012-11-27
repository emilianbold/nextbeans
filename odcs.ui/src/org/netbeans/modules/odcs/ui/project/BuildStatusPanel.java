/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.odcs.ui.project;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.odcs.api.ODCSProject;
import org.netbeans.modules.odcs.ui.api.ODCSUiServer;
import org.netbeans.modules.team.ui.spi.BuilderAccessor;
import org.netbeans.modules.team.ui.spi.JobHandle;
import org.netbeans.modules.team.ui.spi.ProjectHandle;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;

/**
 *
 * @author jpeska
 */
public class BuildStatusPanel extends javax.swing.JPanel {

    private static final RequestProcessor RP = new RequestProcessor(BuildStatusPanel.class);
    private final ProjectHandle<ODCSProject> projectHandle;
    private final JobPropertyListener buildPropertyListener;
    private List<JobHandle> builds;

    /**
     * Creates new form BuildStatusPanel
     */
    public BuildStatusPanel(ProjectHandle<ODCSProject> projectHandle) {
        this.projectHandle = projectHandle;
        this.buildPropertyListener = new JobPropertyListener();
        initComponents();
        loadBuildStatuses();
    }

    void removeBuildListeners() {
        if (builds != null) { // can be uninitialized yet, see bug 217353
            for (JobHandle jobHandle : builds) {
                jobHandle.removePropertyChangeListener(buildPropertyListener);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblError = new javax.swing.JLabel();
        lblEmptyContent = new javax.swing.JLabel();
        pnlTitle = new TitlePanel();
        lblTitle = new javax.swing.JLabel();
        pnlStatuses = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        lblError.setForeground(new java.awt.Color(255, 0, 0));
        lblError.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/odcs/ui/resources/error.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lblError, org.openide.util.NbBundle.getMessage(BuildStatusPanel.class, "LBL_ErrorLoading")); // NOI18N

        lblEmptyContent.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblEmptyContent.setForeground(new java.awt.Color(102, 102, 102));
        org.openide.awt.Mnemonics.setLocalizedText(lblEmptyContent, org.openide.util.NbBundle.getMessage(BuildStatusPanel.class, "BuildStatusPanel.lblEmptyContent.text")); // NOI18N

        setBackground(new java.awt.Color(255, 255, 255));

        lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblTitle, org.openide.util.NbBundle.getMessage(BuildStatusPanel.class, "BuildStatusPanel.lblTitle.text")); // NOI18N

        javax.swing.GroupLayout pnlTitleLayout = new javax.swing.GroupLayout(pnlTitle);
        pnlTitle.setLayout(pnlTitleLayout);
        pnlTitleLayout.setHorizontalGroup(
            pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTitleLayout.setVerticalGroup(
            pnlTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(lblTitle)
                .addGap(3, 3, 3))
        );

        pnlStatuses.setOpaque(false);
        pnlStatuses.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BuildStatusPanel.class, "BuildStatusPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(100, 0, 0, 0);
        pnlStatuses.add(jLabel1, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlStatuses, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlStatuses, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblEmptyContent;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlStatuses;
    private javax.swing.JPanel pnlTitle;
    // End of variables declaration//GEN-END:variables

    private void loadBuildStatuses() {
        RP.post(new Runnable() {
            @Override
            public void run() {
                BuilderAccessor<ODCSProject> buildAccessor = ODCSUiServer.forServer(projectHandle.getTeamProject().getServer()).getDashboard().getDashboardProvider().getBuildAccessor(ODCSProject.class);
                final List<JobHandle> jobs = buildAccessor.getJobs(projectHandle);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (jobs != null && !jobs.isEmpty()) {
                            showBuildStatuses(jobs);
                        } else if (jobs == null) {
                            showError();
                        } else {
                            showEmptyContent();
                        }
                    }
                });
            }
        });
    }

    private void showBuildStatuses(List<JobHandle> jobs) {
        this.builds = jobs;
        pnlStatuses.removeAll();
        for (JobHandle jobHandle : jobs) {
            jobHandle.addPropertyChangeListener(buildPropertyListener);
            JPanel panel = createPanel(jobHandle);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 5, 0);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            pnlStatuses.add(panel, gbc);
        }
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        pnlStatuses.add(new JLabel(), gbc);
        pnlStatuses.revalidate();
        this.repaint();
    }

    private JPanel createPanel(final JobHandle jobHandle) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        LinkLabel lblName = new LinkLabel(jobHandle.getDisplayName()) {
            @Override
            public void mouseClicked(MouseEvent e) {
                jobHandle.getDefaultAction().actionPerformed(new ActionEvent(this, 0, "")); // NOI18N
            }
        };
        lblName.setIcon(getJobIcon(jobHandle.getStatus().name().toLowerCase()));
        panel.add(lblName, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 3), 0, 0));

        return panel;
    }

    private Icon getJobIcon(String colorString) {
        ImageIcon icon = ImageUtilities.loadImageIcon("org/netbeans/modules/odcs/ui/resources/build_" + colorString + ".png", true); //NOI18N
        if (icon == null) {
            icon = ImageUtilities.loadImageIcon("org/netbeans/modules/odcs/ui/resources/build_unknown.png", true); //NOI18N
        }
        return icon;
    }

    private void showEmptyContent() {
        pnlStatuses.removeAll();
        pnlStatuses.add(lblEmptyContent, new GridBagConstraints());
        this.repaint();
    }

    private void showError() {
        pnlStatuses.removeAll();
        pnlStatuses.add(lblError, new GridBagConstraints());
        this.repaint();
    }

    private class JobPropertyListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(JobHandle.PROP_STATUS)) {
                removeBuildListeners();
                loadBuildStatuses();
            }
        }
    }
}
