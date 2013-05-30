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
package org.netbeans.modules.css.prep.ui.customizer;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.Pair;

public class MappingsPanel extends JPanel {

    private static final long serialVersionUID = 16987546576769L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    // @GuardedBy("EDT")
    private final MappingsTableModel mappingsTableModel;
    // we must be thread safe
    private final List<Pair<String, String>> mappings = new CopyOnWriteArrayList<>();


    public MappingsPanel() {
        assert EventQueue.isDispatchThread();

        mappingsTableModel = new MappingsTableModel(mappings);

        initComponents();
        init();
    }

    private void init() {
        assert EventQueue.isDispatchThread();
        mappingsTable.setModel(mappingsTableModel);
        enableRemoveButton();
        // listeners
        mappingsTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                fireChange();
            }
        });
        mappingsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                enableRemoveButton();
            }
        });
    }

    public List<Pair<String, String>> getMappings() {
        return mappings;
    }

    public void setMappings(List<Pair<String, String>> mappings) {
        assert EventQueue.isDispatchThread();
        this.mappings.clear();
        this.mappings.addAll(mappings);
        mappingsTableModel.fireMappingsChange();
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    void enablePanel(boolean enabled) {
        watchLabel.setEnabled(enabled);
        mappingsTable.setEnabled(enabled);
        addButton.setEnabled(enabled);
        if (enabled) {
            enableRemoveButton();
        } else {
            removeButton.setEnabled(false);
        }
    }

    void enableRemoveButton() {
        removeButton.setEnabled(mappingsTable.getSelectedRowCount() > 0);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        watchLabel = new JLabel();
        mappingsScrollPane = new JScrollPane();
        mappingsTable = new JTable();
        addButton = new JButton();
        removeButton = new JButton();

        Mnemonics.setLocalizedText(watchLabel, NbBundle.getMessage(MappingsPanel.class, "MappingsPanel.watchLabel.text")); // NOI18N

        mappingsScrollPane.setViewportView(mappingsTable);

        Mnemonics.setLocalizedText(addButton, NbBundle.getMessage(MappingsPanel.class, "MappingsPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(removeButton, NbBundle.getMessage(MappingsPanel.class, "MappingsPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(watchLabel)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(mappingsScrollPane, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(addButton, GroupLayout.Alignment.TRAILING)
                    .addComponent(removeButton, GroupLayout.Alignment.TRAILING)))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {addButton, removeButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(watchLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton))
                    .addComponent(mappingsScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        mappings.add(Pair.of("", "")); // NOI18N
        mappingsTableModel.fireMappingsChange();
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int[] selectedRows = mappingsTable.getSelectedRows();
        assert selectedRows.length > 0;
        for (int i = selectedRows.length - 1; i >= 0; --i) {
            mappings.remove(selectedRows[i]);
        }
        mappingsTableModel.fireMappingsChange();
    }//GEN-LAST:event_removeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addButton;
    private JScrollPane mappingsScrollPane;
    private JTable mappingsTable;
    private JButton removeButton;
    private JLabel watchLabel;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private static final class MappingsTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -65325657686411L;

        private final List<Pair<String, String>> mappings;


        public MappingsTableModel(List<Pair<String, String>> mappings) {
            assert mappings != null;
            this.mappings = mappings;
        }

        @Override
        public int getRowCount() {
            return mappings.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Pair<String, String> pair = mappings.get(rowIndex);
            if (columnIndex == 0) {
                return pair.first();
            }
            if (columnIndex == 1) {
                return pair.second();
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            String path = (String) aValue;
            Pair<String, String> pair = mappings.get(rowIndex);
            if (columnIndex == 0) {
                mappings.set(rowIndex, Pair.of(path, pair.second()));
            } else if (columnIndex == 1) {
                mappings.set(rowIndex, Pair.of(pair.first(), path));
            } else {
                throw new IllegalStateException("Unknown column index: " + columnIndex);
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @NbBundle.Messages({
            "MappingsTableModel.column.input.title=Input",
            "MappingsTableModel.column.output.title=Output",
        })
        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return Bundle.MappingsTableModel_column_input_title();
            }
            if (columnIndex == 1) {
                return Bundle.MappingsTableModel_column_output_title();
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0
                    || columnIndex == 1) {
                return String.class;
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0
                    || columnIndex == 1) {
                return true;
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        public void fireMappingsChange() {
            assert EventQueue.isDispatchThread();
            fireTableDataChanged();
        }

    }

}
