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
package org.netbeans.modules.tasks.ui;

import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import org.netbeans.modules.bugtracking.api.Issue;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.ui.search.QuickSearchComboBar;
import org.netbeans.modules.tasks.ui.dashboard.DashboardViewer;
import org.netbeans.modules.tasks.ui.model.Category;
import org.openide.util.NbBundle;

/**
 *
 * @author jpeska
 */
public class QuickSearchPanel extends javax.swing.JPanel {

    private List<Category> categories;
    private final QuickSearchComboBar quickSearchComboBar;
    private final Repository repository;

    /**
     * Creates new form QuickSearchPanel
     */
    public QuickSearchPanel(Repository repository) {
        this.repository = repository;
        initComponents();
        quickSearchComboBar = new QuickSearchComboBar(this);
        GroupLayout layout = (GroupLayout) this.getLayout();
        quickSearchComboBar.setRepository(repository);
        layout.replace(placeholderTask, quickSearchComboBar);
    }

    private DefaultComboBoxModel getCategoryModel() {
        categories = DashboardViewer.getInstance().getCategories(true);
        String[] catNames = new String[categories.size() + 1];
        catNames[0] = "<" + NbBundle.getMessage(QuickSearchPanel.class, "LBL_NoCategory") + ">"; //NOI18N
        for (int i = 0; i < categories.size(); i++) {
            catNames[i + 1] = categories.get(i).getName();
        }
        return new DefaultComboBoxModel(catNames);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboxCategory = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        placeholderTask = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        cboxCategory.setModel(getCategoryModel());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, NbBundle.getMessage(QuickSearchPanel.class, "QuickSearchPanel.jLabel3.text")); // NOI18N

        placeholderTask.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item1", "Item 2" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, NbBundle.getMessage(QuickSearchPanel.class, "QuickSearchPanel.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(QuickSearchPanel.class, "QuickSearchPanel.jLabel1.text")); // NOI18N
        jLabel1.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(QuickSearchPanel.class, "QuickSearchPanel.jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboxCategory, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(placeholderTask, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(placeholderTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(68, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboxCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox placeholderTask;
    // End of variables declaration//GEN-END:variables

    public void addQuickSearchListener(PropertyChangeListener listener) {
        quickSearchComboBar.addPropertyChangeListener(listener);
    }

    public void removeQuickSearchListener(PropertyChangeListener listener) {
        quickSearchComboBar.removePropertyChangeListener(listener);
    }

    public Category getSelectedCategory() {
        int selectedIndex = cboxCategory.getSelectedIndex();
        if (selectedIndex == 0) {
            return null;
        } else {
            return categories.get(selectedIndex - 1);
        }
    }

    public Issue getSelectedTask() {
        return quickSearchComboBar.getIssue();
    }

    public static String getTaskEvent() {
        return QuickSearchComboBar.EVT_ISSUE_CHANGED;
    }
}