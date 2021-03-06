/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.analysis.ui.analyzer;

import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.modules.php.analysis.options.AnalysisOptions;
import org.netbeans.modules.php.analysis.options.AnalysisOptionsValidator;
import org.netbeans.modules.php.analysis.ui.MessDetectorRuleSetsListModel;
import org.netbeans.modules.php.analysis.util.AnalysisUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

public class MessDetectorCustomizerPanel extends JPanel {

    private static final long serialVersionUID = -4687321324676897L;

    public static final String RULE_SETS = "messDetector.ruleSets"; // NOI18N

    private final MessDetectorRuleSetsListModel ruleSetsListModel = new MessDetectorRuleSetsListModel();
    final Analyzer.CustomizerContext<Void, MessDetectorCustomizerPanel> context;
    final Preferences settings;


    public MessDetectorCustomizerPanel(Analyzer.CustomizerContext<Void, MessDetectorCustomizerPanel> context) {
        assert context != null;

        this.context = context;
        this.settings = context.getSettings();

        initComponents();
        init();
    }

    @CheckForNull
    public static List<String> getRuleSets(Preferences settings) {
        if (settings == null) {
            return null;
        }
        String ruleSets = settings.get(MessDetectorCustomizerPanel.RULE_SETS, null);
        if (ruleSets == null) {
            return null;
        }
        return AnalysisUtils.deserialize(ruleSets);
    }

    private void init() {
        ruleSetsList.setModel(ruleSetsListModel);

        // rule sets
        List<String> ruleSets = getRuleSets(settings);
        if (ruleSets == null) {
            ruleSets = AnalysisOptions.getInstance().getMessDetectorRuleSets();
        }
        selectRuleSets(ruleSets);

        // listeners
        ruleSetsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                validateAndSetData();
            }
        });
    }

    List<String> getSelectedRuleSets() {
        return ruleSetsList.getSelectedValuesList();
    }

    void selectRuleSets(List<String> ruleSets) {
        ruleSetsList.clearSelection();
        for (String ruleSet : ruleSets) {
            int indexOf = MessDetectorRuleSetsListModel.getAllRuleSets().indexOf(ruleSet);
            assert indexOf != -1 : "Rule set not found: " + ruleSet;
            ruleSetsList.addSelectionInterval(indexOf, indexOf);
        }
    }

    void validateAndSetData() {
        if (validateData()) {
            setData();
        }
    }

    private boolean validateData() {
        ValidationResult result = new AnalysisOptionsValidator()
                .validateMessDetectorRuleSets(getSelectedRuleSets())
                .getResult();
        if (result.hasErrors()) {
            context.setError(result.getErrors().get(0).getMessage());
            return false;
        }
        if (result.hasWarnings()) {
            context.setError(result.getWarnings().get(0).getMessage());
            return false;
        }
        context.setError(null);
        return true;
    }

    private void setData() {
        settings.put(RULE_SETS, AnalysisUtils.serialize(getSelectedRuleSets()));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ruleSetsLabel = new JLabel();
        ruleSetsScrollPane = new JScrollPane();
        ruleSetsList = new JList<String>();

        ruleSetsLabel.setLabelFor(ruleSetsList);
        Mnemonics.setLocalizedText(ruleSetsLabel, NbBundle.getMessage(MessDetectorCustomizerPanel.class, "MessDetectorCustomizerPanel.ruleSetsLabel.text")); // NOI18N

        ruleSetsScrollPane.setViewportView(ruleSetsList);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ruleSetsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ruleSetsScrollPane, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(ruleSetsLabel)
            .addComponent(ruleSetsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel ruleSetsLabel;
    private JList<String> ruleSetsList;
    private JScrollPane ruleSetsScrollPane;
    // End of variables declaration//GEN-END:variables
}
