/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * CategoryPanelFormatters.java
 *
 * Created on Jan 20, 2009, 3:30:49 PM
 */

package org.netbeans.modules.debugger.jpda.ui.options;

/**
 *
 * @author martin
 */
public class CategoryPanelFormatters extends javax.swing.JPanel {

    /** Creates new form CategoryPanelFormatters */
    public CategoryPanelFormatters() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        formattersScrollPane = new javax.swing.JScrollPane();
        formattersList = new javax.swing.JList();
        formattersAddButton = new javax.swing.JButton();
        formattersRemoveButton = new javax.swing.JButton();
        formattersMoveUpButton = new javax.swing.JButton();
        formattersMoveDownButton = new javax.swing.JButton();
        formatterNameLabel = new javax.swing.JLabel();
        formatterNameTextField = new javax.swing.JTextField();
        formatterClassTypesLabel = new javax.swing.JLabel();
        formatterClassTypesTextField = new javax.swing.JTextField();
        formatterClassTypesSubtypesCheckBox = new javax.swing.JCheckBox();
        formatValueLabel = new javax.swing.JLabel();
        formatValueScrollPane = new javax.swing.JScrollPane();
        formatValueEditorPane = new javax.swing.JEditorPane();
        formatChildrenLabel = new javax.swing.JLabel();
        formatChildrenAsCodeRadioButton = new javax.swing.JRadioButton();
        formatChildrenCodeScrollPane = new javax.swing.JScrollPane();
        formatChildrenCodeEditorPane = new javax.swing.JEditorPane();
        formatChildrenAsListRadioButton = new javax.swing.JRadioButton();
        formatChildrenListScrollPane = new javax.swing.JScrollPane();
        formatChildrenListTable = new javax.swing.JTable();
        variableAddButton = new javax.swing.JButton();
        variableRemoveButton = new javax.swing.JButton();
        variableMoveUpButton = new javax.swing.JButton();
        variableMoveDownButton = new javax.swing.JButton();
        childrenExpandExpressionLabel = new javax.swing.JLabel();
        childrenExpandExpressionTextField = new javax.swing.JTextField();

        formattersList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        formattersScrollPane.setViewportView(formattersList);

        formattersAddButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersAddButton.text")); // NOI18N

        formattersRemoveButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersRemoveButton.text")); // NOI18N

        formattersMoveUpButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersMoveUpButton.text")); // NOI18N

        formattersMoveDownButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersMoveDownButton.text")); // NOI18N

        formatterNameLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatterNameLabel.text")); // NOI18N

        formatterClassTypesLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatterClassTypesLabel.text")); // NOI18N

        formatterClassTypesSubtypesCheckBox.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatterClassTypesSubtypesCheckBox.text")); // NOI18N

        formatValueLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatValueLabel.text")); // NOI18N

        formatValueScrollPane.setViewportView(formatValueEditorPane);

        formatChildrenLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenLabel.text")); // NOI18N

        buttonGroup1.add(formatChildrenAsCodeRadioButton);
        formatChildrenAsCodeRadioButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenAsCodeRadioButton.text")); // NOI18N

        formatChildrenCodeScrollPane.setViewportView(formatChildrenCodeEditorPane);

        buttonGroup1.add(formatChildrenAsListRadioButton);
        formatChildrenAsListRadioButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenAsListRadioButton.text")); // NOI18N

        formatChildrenListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        formatChildrenListScrollPane.setViewportView(formatChildrenListTable);

        variableAddButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableAddButton.text")); // NOI18N

        variableRemoveButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableRemoveButton.text")); // NOI18N

        variableMoveUpButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableMoveUpButton.text")); // NOI18N

        variableMoveDownButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableMoveDownButton.text")); // NOI18N

        childrenExpandExpressionLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.childrenExpandExpressionLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(formatterClassTypesLabel)
                    .add(formatterNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(formatterNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(formatterClassTypesTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(formatterClassTypesSubtypesCheckBox)
                        .add(12, 12, 12))))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(formatValueScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                    .add(formatValueLabel))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(childrenExpandExpressionLabel)
                .addContainerGap(24, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(formatChildrenListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(variableRemoveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(variableAddButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(variableMoveUpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(variableMoveDownButton)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(formattersScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(formattersAddButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(formattersRemoveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(formattersMoveUpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(formattersMoveDownButton)))
                    .add(formatChildrenAsListRadioButton)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(formatChildrenCodeScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
                    .add(formatChildrenAsCodeRadioButton)
                    .add(formatChildrenLabel))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(24, 24, 24)
                .add(childrenExpandExpressionTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(formattersAddButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(formattersRemoveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(formattersMoveUpButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(formattersMoveDownButton))
                    .add(formattersScrollPane, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(formatterNameLabel)
                    .add(formatterNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(formatterClassTypesLabel)
                    .add(formatterClassTypesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(formatterClassTypesSubtypesCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatValueLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatValueScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenAsCodeRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenCodeScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenAsListRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(variableAddButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(variableRemoveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(variableMoveUpButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(variableMoveDownButton))
                    .add(formatChildrenListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(childrenExpandExpressionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(childrenExpandExpressionTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel childrenExpandExpressionLabel;
    private javax.swing.JTextField childrenExpandExpressionTextField;
    private javax.swing.JRadioButton formatChildrenAsCodeRadioButton;
    private javax.swing.JRadioButton formatChildrenAsListRadioButton;
    private javax.swing.JEditorPane formatChildrenCodeEditorPane;
    private javax.swing.JScrollPane formatChildrenCodeScrollPane;
    private javax.swing.JLabel formatChildrenLabel;
    private javax.swing.JScrollPane formatChildrenListScrollPane;
    private javax.swing.JTable formatChildrenListTable;
    private javax.swing.JEditorPane formatValueEditorPane;
    private javax.swing.JLabel formatValueLabel;
    private javax.swing.JScrollPane formatValueScrollPane;
    private javax.swing.JLabel formatterClassTypesLabel;
    private javax.swing.JCheckBox formatterClassTypesSubtypesCheckBox;
    private javax.swing.JTextField formatterClassTypesTextField;
    private javax.swing.JLabel formatterNameLabel;
    private javax.swing.JTextField formatterNameTextField;
    private javax.swing.JButton formattersAddButton;
    private javax.swing.JList formattersList;
    private javax.swing.JButton formattersMoveDownButton;
    private javax.swing.JButton formattersMoveUpButton;
    private javax.swing.JButton formattersRemoveButton;
    private javax.swing.JScrollPane formattersScrollPane;
    private javax.swing.JButton variableAddButton;
    private javax.swing.JButton variableMoveDownButton;
    private javax.swing.JButton variableMoveUpButton;
    private javax.swing.JButton variableRemoveButton;
    // End of variables declaration//GEN-END:variables

}
