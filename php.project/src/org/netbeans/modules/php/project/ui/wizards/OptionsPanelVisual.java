/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.wizards;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.UIResource;

class OptionsPanelVisual extends JPanel {

    private static final long serialVersionUID = -38388194985L;

    public static final String LBL_INDEX_FILE_PATH = "LBL_PathToIndexFile"; // NOI18N
    //private static final String MSG_ILLEGAL_INDEX_FILE_NAME
    //                                     = "MSG_IllegalIndexFileName";     // NOI18N

    OptionsPanelVisual(String originalEncoding) {
        initComponents();
        init(originalEncoding);
    }

    private void init(String originalEncoding) {
        createIndexCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                indexNameTextField.setEnabled(createIndexCheckBox.isSelected());
            }
        });
        encodingComboBox.setModel(new EncodingModel(originalEncoding));
        encodingComboBox.setRenderer(new EncodingRenderer());
    }

    void addCreateIndexListener(ActionListener listener) {
        createIndexCheckBox.addActionListener(listener);
    }

    void addIndexNameListener(DocumentListener listener) {
        indexNameTextField.getDocument().addDocumentListener(listener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setAsMainCheckBox = new javax.swing.JCheckBox();
        indexNameTextField = new javax.swing.JTextField();
        createIndexCheckBox = new javax.swing.JCheckBox();
        encodingLabel = new javax.swing.JLabel();
        encodingComboBox = new javax.swing.JComboBox();

        setAsMainCheckBox.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/php/project/ui/wizards/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, bundle.getString("LBL_SetAsMain")); // NOI18N
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        indexNameTextField.setText("index.php"); // NOI18N

        createIndexCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createIndexCheckBox, org.openide.util.NbBundle.getBundle(OptionsPanelVisual.class).getString("LBL_CreateIndexFile")); // NOI18N
        createIndexCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        encodingLabel.setLabelFor(encodingComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(encodingLabel, org.openide.util.NbBundle.getMessage(OptionsPanelVisual.class, "LBL_Encoding")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(createIndexCheckBox))
                            .add(encodingLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(encodingComboBox, 0, 288, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, indexNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)))
                    .add(setAsMainCheckBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createIndexCheckBox)
                    .add(indexNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(encodingLabel)
                    .add(encodingComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(setAsMainCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setAsMainCheckBox.getAccessibleContext().setAccessibleName("Set as Main Project");
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(OptionsPanelVisual.class, "ACS_LBL_SetAsMain_A11YDesc")); // NOI18N
        indexNameTextField.getAccessibleContext().setAccessibleName("Index File Name "); // NOI18N
        indexNameTextField.getAccessibleContext().setAccessibleDescription("Specify the name of index file"); // NOI18N
        createIndexCheckBox.getAccessibleContext().setAccessibleName("Create Index File"); // NOI18N
        createIndexCheckBox.getAccessibleContext().setAccessibleDescription("Select Checkbox to create index file"); // NOI18N
        encodingComboBox.getAccessibleContext().setAccessibleName("Default Encoding");
        encodingComboBox.getAccessibleContext().setAccessibleDescription("Default Encoding");
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox createIndexCheckBox;
    private javax.swing.JComboBox encodingComboBox;
    private javax.swing.JLabel encodingLabel;
    private javax.swing.JTextField indexNameTextField;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables

    boolean isCreateIndex() {
        return createIndexCheckBox.isSelected();
    }

    void setCreateIndex(boolean isCreateIndex) {
        createIndexCheckBox.setSelected(isCreateIndex);
    }

    String getIndexName() {
        return indexNameTextField.getText();
    }

    void setIndexName(String indexName) {
        indexNameTextField.setText(indexName);
    }

    Charset getEncoding() {
        return (Charset) encodingComboBox.getSelectedItem();
    }

    boolean isSetAsMain() {
        return setAsMainCheckBox.isSelected();
    }

    void setSetAsMain(boolean isSetAsMain) {
        setAsMainCheckBox.setSelected(isSetAsMain);
    }

    private static class EncodingModel extends DefaultComboBoxModel {
        private static final long serialVersionUID = -3139920099217726436L;

        public EncodingModel(String originalEncoding) {
            Charset defEnc = null;
            for (Charset c : Charset.availableCharsets().values()) {
                if (c.name().equals(originalEncoding)) {
                    defEnc = c;
                }
                addElement(c);
            }
            if (defEnc == null) {
                defEnc = Charset.defaultCharset();
            }
            setSelectedItem(defEnc);
        }
    }

    private static class EncodingRenderer extends JLabel implements ListCellRenderer, UIResource {
        private static final long serialVersionUID = 3196531352192214602L;

        public EncodingRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            assert value instanceof Charset;
            setName("ComboBox.listRenderer"); // NOI18N
            setText(((Charset) value).displayName());
            setIcon(null);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }

        @Override
        public String getName() {
            String name = super.getName();
            return name == null ? "ComboBox.renderer" : name; // NOI18N
        }
    }
}
