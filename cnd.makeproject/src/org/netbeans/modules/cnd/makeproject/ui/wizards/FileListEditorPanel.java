/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.makeproject.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.cnd.api.remote.ui.RemoteFileChooserUtil;
import org.netbeans.modules.cnd.utils.FSPath;
import org.netbeans.modules.cnd.utils.ui.FileFilterFactory;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
public class FileListEditorPanel extends javax.swing.JPanel {
    
    private final List<String> data = new ArrayList<>();
    private final ExecutionEnvironment env;
    private final FileSystem fileSystem;

    /**
     * Creates new form FileListEditorPanel
     */
    public FileListEditorPanel(String files, ExecutionEnvironment env, FileSystem fileSystem) {
        initComponents();
        for(String s : files.split(";")) { //NOI18N
            if (!s.isEmpty()) {
                data.add(s);
            }
        }
        this.env = env;
        this.fileSystem = fileSystem;
        fileList.setModel(new MyModel(data));
        fileList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                checkSelection();
            }
        });
        if (data.size() > 0) {
            fileList.setSelectedIndex(0);
        }
    }

    public List<String> getFileList() {
        return new ArrayList<>(data);
    }
    
    private void checkSelection() {
        checkSelection(getSelectedIndex());
    }
    
    private synchronized int getSelectedIndex() {
        int index = fileList.getSelectedIndex();
        if (index >= 0 && index < data.size()) {
            return index;
        } else {
            return 0;
        }
    }

    protected synchronized void checkSelection(int i) {
        if (i >= 0 && data.size() > 0) {
            removeButton.setEnabled(true);
            editButton.setEnabled(true);
        } else {
            removeButton.setEnabled(false);
            editButton.setEnabled(false);
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

        fileListLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        addFolderButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(fileListLabel, org.openide.util.NbBundle.getMessage(FileListEditorPanel.class, "FileListEditorPanel.fileListLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 6, 0);
        add(fileListLabel, gridBagConstraints);

        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(fileList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 6);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(FileListEditorPanel.class, "FileListEditorPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(addButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(addFolderButton, org.openide.util.NbBundle.getMessage(FileListEditorPanel.class, "FileListEditorPanel.addFolderButton.text")); // NOI18N
        addFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFolderButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 10);
        add(addFolderButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(FileListEditorPanel.class, "FileListEditorPanel.editButton.text")); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 10);
        add(editButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(FileListEditorPanel.class, "FileListEditorPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 10);
        add(removeButton, gridBagConstraints);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        FileFilter[] filters = FileFilterFactory.getBinaryFilters(fileSystem);
        String path = ""; // NOI18N
        int index = fileList.getSelectedIndex();
        if (index >= 0 && index < data.size()) {
            path = data.get(index);
        }
        if (path.isEmpty()) { 
            path = SelectModePanel.getDefaultDirectory(env);
        }
        JFileChooser fileChooser = RemoteFileChooserUtil.createFileChooser(env,
                NbBundle.getMessage(FileListEditorPanel.class, "SelectBinaryPanelVisual.Browse.Multi.Title"), // NOI18N
                NbBundle.getMessage(FileListEditorPanel.class, "SelectBinaryPanelVisual.Browse.Multi.Select"), // NOI18N
                JFileChooser.FILES_ONLY, filters, path, false);
        fileChooser.setMultiSelectionEnabled(true);
        int ret = fileChooser.showOpenDialog(this);
        try {
            if (ret == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File[] selected = fileChooser.getSelectedFiles();
            if (selected == null || selected.length == 0) {
                return;
            }
            List<String> toAdd = new ArrayList<>();
            if (selected.length == 1) {
                if (!data.contains(selected[0].getPath())) {
                    toAdd.add(selected[0].getPath());
                }
            } else {
                for(File f : selected) {
                    if (!data.contains(f.getPath())) {
                        toAdd.add(f.getPath());
                    }
                }
            }
            if (!toAdd.isEmpty()) {
                int addAtIndex = data.size();
                data.addAll(toAdd);
                fileList.setModel(new MyModel(data));
                fileList.setSelectedIndex(addAtIndex);
                fileList.ensureIndexIsVisible(addAtIndex);
                checkSelection();
            }
        } finally {
            addButton.requestFocus();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        FileFilter[] filters = FileFilterFactory.getBinaryFilters(fileSystem);
        String path = ""; // NOI18N
        int index = fileList.getSelectedIndex();
        if (index >= 0 && index < data.size()) {
            path = data.get(index);
        } else {
            return;
        }
        if (path.isEmpty()) { 
            path = SelectModePanel.getDefaultDirectory(env);
        }
        JFileChooser fileChooser = RemoteFileChooserUtil.createFileChooser(env,
                NbBundle.getMessage(FileListEditorPanel.class, "SelectBinaryPanelVisual.Browse.Title"), // NOI18N
                NbBundle.getMessage(FileListEditorPanel.class, "SelectBinaryPanelVisual.Browse.Select"), // NOI18N
                JFileChooser.FILES_ONLY, filters, path, false);
        int ret = fileChooser.showOpenDialog(this);
        try {
            if (ret == JFileChooser.CANCEL_OPTION) {
                return;
            }
            path = fileChooser.getSelectedFile().getPath();
            if (data.contains(path)) {
                return;
            }
            data.set(index, path);
            fileList.setModel(new MyModel(data));
            fileList.setSelectedIndex(index);
            fileList.ensureIndexIsVisible(index);
            checkSelection();
        } finally {
            editButton.requestFocus();
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        final int selectedIndex = getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        if (selectedIndex >= (data.size())) {
            return;
        }
        data.remove(selectedIndex);
        fileList.setModel(new MyModel(data));
        int newSelectedIndex = (selectedIndex >= data.size()) ? selectedIndex - 1 : selectedIndex;
        if (newSelectedIndex >= 0) {
            fileList.ensureIndexIsVisible(newSelectedIndex);
            checkSelection(newSelectedIndex);
            fileList.setSelectedIndex(newSelectedIndex);
        } else {
            checkSelection();
        }
        if (removeButton.isEnabled()) {
            removeButton.requestFocus();
        } else {
            addButton.requestFocus();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFolderButtonActionPerformed
        String path = ""; // NOI18N
        int index = fileList.getSelectedIndex();
        if (index >= 0 && index < data.size()) {
            path = data.get(index);
        }
        if (path.isEmpty()) { 
            path = SelectModePanel.getDefaultDirectory(env);
        }
        JFileChooser fileChooser = RemoteFileChooserUtil.createFileChooser(env,
                NbBundle.getMessage(FileListEditorPanel.class, "SelectBinaryPanelVisual.Browse.Folder.Title"), // NOI18N
                NbBundle.getMessage(FileListEditorPanel.class, "SelectBinaryPanelVisual.Browse.Folder.Select"), // NOI18N
                JFileChooser.DIRECTORIES_ONLY, null, path, true);
        int ret = fileChooser.showOpenDialog(this);
        try {
            if (ret == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File selected = fileChooser.getSelectedFile();
            if (selected == null) {
                return;
            }
            FSPath fsPath = new FSPath(fileSystem, selected.getPath());
            FileObject fo = fsPath.getFileObject();
            if (fo != null && fo.isValid() && fo.isFolder()) {
                List<String> toAdd = new ArrayList<>();
                for(FileObject f : fo.getChildren()) {
                    if (!data.contains(f.getPath())) {
                        if (MIMENames.isBinary(f.getMIMEType())) {
                            toAdd.add(f.getPath());
                        }
                    }
                }
                if (!toAdd.isEmpty()) {
                    int addAtIndex = data.size();
                    data.addAll(toAdd);
                    fileList.setModel(new MyModel(data));
                    fileList.setSelectedIndex(addAtIndex);
                    fileList.ensureIndexIsVisible(addAtIndex);
                    checkSelection();
                }
            }
        } finally {
            addFolderButton.requestFocus();
        }
    }//GEN-LAST:event_addFolderButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addFolderButton;
    private javax.swing.JButton editButton;
    private javax.swing.JList fileList;
    private javax.swing.JLabel fileListLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    private static final class MyModel<String> extends AbstractListModel {

        private final List<String> listData;

        private MyModel(List<String> listData) {
            this.listData = listData;
        }

        @Override
        public int getSize() {
            return listData.size();
        }

        @Override
        public String getElementAt(int i) {
            return listData.get(i);
        }
    }
}
