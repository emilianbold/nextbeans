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
package org.netbeans.modules.php.composer.ui.actions;

import java.awt.Dialog;
import java.awt.EventQueue;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 * UI for Composer search command.
 */
public final class SearchPanel extends JPanel {

    private static final long serialVersionUID = -4572187014657456L;

    private final PhpModule phpModule;


    private SearchPanel(PhpModule phpModule) {
        assert phpModule != null;

        this.phpModule = phpModule;

        initComponents();
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "SearchPanel.panel.title=Composer Search ({0})",
        "SearchPanel.panel.require.label=Require",
        "SearchPanel.panel.requireDev.label=Require (dev)"
    })
    public static void open(PhpModule phpModule) {
        assert EventQueue.isDispatchThread();

        SearchPanel searchPanel = new SearchPanel(phpModule);
        Object[] options = new Object[] {
            searchPanel.requireButton,
            searchPanel.requireDevButton,
            DialogDescriptor.CANCEL_OPTION,
        };

        final DialogDescriptor descriptor = new DialogDescriptor(
                searchPanel,
                Bundle.SearchPanel_panel_title(phpModule.getDisplayName()),
                false,
                options,
                searchPanel.requireButton,
                DialogDescriptor.DEFAULT_ALIGN, null, null);
        descriptor.setClosingOptions(new Object[] {DialogDescriptor.CANCEL_OPTION});
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        requireDevButton = new JButton();
        requireButton = new JButton();
        tokenLabel = new JLabel();
        tokenTextField = new JTextField();
        onlyInNameCheckBox = new JCheckBox();
        searchButton = new JButton();
        resultsLabel = new JLabel();
        outputSplitPane = new JSplitPane();
        resultsScrollPane = new JScrollPane();
        resultsList = new JList();
        detailsScrollPane = new JScrollPane();
        detailsTextPane = new JTextPane();

        Mnemonics.setLocalizedText(requireDevButton, NbBundle.getMessage(SearchPanel.class, "SearchPanel.requireDevButton.text")); // NOI18N

        Mnemonics.setLocalizedText(requireButton, NbBundle.getMessage(SearchPanel.class, "SearchPanel.requireButton.text")); // NOI18N

        Mnemonics.setLocalizedText(tokenLabel, NbBundle.getMessage(SearchPanel.class, "SearchPanel.tokenLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(onlyInNameCheckBox, NbBundle.getMessage(SearchPanel.class, "SearchPanel.onlyInNameCheckBox.text")); // NOI18N

        Mnemonics.setLocalizedText(searchButton, NbBundle.getMessage(SearchPanel.class, "SearchPanel.searchButton.text")); // NOI18N

        Mnemonics.setLocalizedText(resultsLabel, NbBundle.getMessage(SearchPanel.class, "SearchPanel.resultsLabel.text")); // NOI18N

        outputSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        resultsScrollPane.setViewportView(resultsList);

        outputSplitPane.setLeftComponent(resultsScrollPane);

        detailsTextPane.setEditable(false);
        detailsScrollPane.setViewportView(detailsTextPane);

        outputSplitPane.setBottomComponent(detailsScrollPane);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(outputSplitPane, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tokenLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(onlyInNameCheckBox)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tokenTextField)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(resultsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(tokenLabel)
                    .addComponent(tokenTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(onlyInNameCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputSplitPane, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane detailsScrollPane;
    private JTextPane detailsTextPane;
    private JCheckBox onlyInNameCheckBox;
    private JSplitPane outputSplitPane;
    private JButton requireButton;
    private JButton requireDevButton;
    private JLabel resultsLabel;
    private JList resultsList;
    private JScrollPane resultsScrollPane;
    private JButton searchButton;
    private JLabel tokenLabel;
    private JTextField tokenTextField;
    // End of variables declaration//GEN-END:variables
}
