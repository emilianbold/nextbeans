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
package org.netbeans.modules.refactoring.java.ui;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.Set;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.DialogBinding;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.ui.TypeElementFinder;
import org.netbeans.modules.refactoring.java.RetoucheUtils;
import org.netbeans.modules.refactoring.java.api.JavaRefactoringUtils;
import org.netbeans.modules.refactoring.java.plugins.LocalVarScanner;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Panel contains components for signature change. There is table with
 * parameters, you can add parameters, reorder parameters, rename parameters
 * or remove parameters. You can also change the methods access modifier.
 *
 * @author  Pavel Flaska, Jan Becicka, Ralph Ruijs
 */
public class ChangeParametersPanel extends JPanel implements CustomRefactoringPanel {

    TreePathHandle refactoredObj;
    private int[] parameterSpan;
    ParamTableModel model;
    private ChangeListener parent;
    private TableTabAction tableTabAction;
    private TableTabAction tableShiftTabAction;
    
    private static Action editAction = null;
    private String returnType;
    private String enclosingClassName;
    
    private static final String[] modifierNames = {
        "public", // NOI18N
        "protected", // NOI18N
        "<default>", // NOI18N
        "private" // NOI18N
    };
    
    public Component getComponent() {
        return this;
    }
    
    private static final String[] columnNames = {
        getString("LBL_ChangeParsColName"), // NOI18N
        getString("LBL_ChangeParsColType"), // NOI18N
        getString("LBL_ChangeParsColDefVal"), // NOI18N
        getString("LBL_ChangeParsColOrigIdx"), // NOI18N
        getString("LBL_ChangeParsParUsed") // NOI18N
    };

    // modifier items in combo - indexes
    private static final int MOD_PUBLIC_INDEX = 0;
    private static final int MOD_PROTECTED_INDEX = 1;
    private static final int MOD_DEFAULT_INDEX = 2;
    private static final int MOD_PRIVATE_INDEX = 3;

    private static final String ACTION_INLINE_EDITOR = "invokeInlineEditor";  //NOI18N

    /** Creates new form ChangeMethodSignature */
    public ChangeParametersPanel(TreePathHandle refactoredObj, ChangeListener parent) {
        this.refactoredObj = refactoredObj;
        this.parent = parent;
        model = new ParamTableModel(columnNames, 0);
        initComponents();

        InputMap im = paramTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap ptActionMap = paramTable.getActionMap();

        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        Action oldTabAction = ptActionMap.get(im.get(tab));
        tableTabAction = new TableTabAction(oldTabAction);
        ptActionMap.put(im.get(tab), tableTabAction);
        
        KeyStroke shiftTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK);
        Action oldShiftTabAction = ptActionMap.get(im.get(shiftTab));
        tableShiftTabAction = new TableTabAction(oldShiftTabAction);
        ptActionMap.put(im.get(shiftTab), tableShiftTabAction);
    }
    private boolean initialized = false;
    public void initialize() {
        try {
            if (initialized) {
                return;
            }
            JavaSource source = JavaSource.forFileObject(refactoredObj.getFileObject());
            source.runUserActionTask(new CancellableTask<CompilationController>() {
                public void run(org.netbeans.api.java.source.CompilationController info) {
                    try {
                        info.toPhase(org.netbeans.api.java.source.JavaSource.Phase.RESOLVED);
                        ExecutableElement e = (ExecutableElement) refactoredObj.resolveElement(info);
                        returnType = e.getReturnType().toString();
                        TreePath enclosingClass = JavaRefactoringUtils.findEnclosingClass(info, refactoredObj.resolve(info), true, true, true, true, true);
                        TreePathHandle tph = TreePathHandle.create(enclosingClass, info);
                        Element enclosingElement = tph.resolveElement(info);
                        enclosingClassName = enclosingElement.getSimpleName().toString();
                        if (enclosingElement.getKind().isInterface() || inheritedFromInterface(e, info.getElementUtilities())) {
                            modifiersCombo.setEnabled(false);
                        }
                        initTableData(info);
                        setModifier(e.getModifiers());
                        previewChange.setText(genDeclarationString());
                        previewChange.setToolTipText(genDeclarationString());
                    }
                    catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                public void cancel() {
                }
            }, true);
            initialized = true;
        }
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static boolean inheritedFromInterface(ExecutableElement e, ElementUtilities utils) {
        while (e != null) {
            if (utils.implementsMethod(e)) {
                return true;
            }
            e = utils.getOverriddenMethod(e);
        }
        return false;
    }
    
    protected DefaultTableModel getTableModel() {
        return model;
    }
    
    protected Set<Modifier> getModifier() {
        modifiers.remove(Modifier.PRIVATE);
        modifiers.remove(Modifier.PUBLIC);
        modifiers.remove(Modifier.PROTECTED);
        
        switch (modifiersCombo.getSelectedIndex()) {
        case MOD_PRIVATE_INDEX: modifiers.add(Modifier.PRIVATE);break;
        case MOD_DEFAULT_INDEX: break; /* no modifier */
        case MOD_PROTECTED_INDEX: modifiers.add(Modifier.PROTECTED); break;
        case MOD_PUBLIC_INDEX: modifiers.add(Modifier.PUBLIC); break;
        }
        return modifiers;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        modifiersPanel = new javax.swing.JPanel();
        modifiersLabel = new javax.swing.JLabel();
        modifiersCombo = new javax.swing.JComboBox();
        eastPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        fillPanel = new javax.swing.JPanel();
        westPanel = new javax.swing.JScrollPane();
        paramTable = new javax.swing.JTable();
        paramTitle = new javax.swing.JLabel();
        previewChange = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setAutoscrolls(true);
        setName(getString("LBL_TitleChangeParameters"));
        setLayout(new java.awt.GridBagLayout());

        modifiersPanel.setLayout(new java.awt.GridBagLayout());

        modifiersLabel.setLabelFor(modifiersCombo);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/refactoring/java/ui/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(modifiersLabel, bundle.getString("LBL_ChangeParsMods")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        modifiersPanel.add(modifiersLabel, gridBagConstraints);

        modifiersCombo.setModel(new DefaultComboBoxModel(modifierNames));
        modifiersCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifiersComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        modifiersPanel.add(modifiersCombo, gridBagConstraints);
        modifiersCombo.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_modifiersCombo")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(modifiersPanel, gridBagConstraints);

        eastPanel.setLayout(new java.awt.GridBagLayout());

        buttonsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 11, 1, 1));
        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(addButton, bundle.getString("LBL_ChangeParsAdd")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        buttonsPanel.add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsAdd")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, bundle.getString("LBL_ChangeParsRemove")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        buttonsPanel.add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsRemove")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(moveUpButton, bundle.getString("LBL_ChangeParsMoveUp")); // NOI18N
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        buttonsPanel.add(moveUpButton, gridBagConstraints);
        moveUpButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsMoveUp")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, bundle.getString("LBL_ChangeParsMoveDown")); // NOI18N
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(moveDownButton, gridBagConstraints);
        moveDownButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsMoveDown")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        eastPanel.add(buttonsPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        eastPanel.add(fillPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(eastPanel, gridBagConstraints);

        westPanel.setPreferredSize(new java.awt.Dimension(453, 100));

        paramTable.setModel(model);
        initRenderer();
        paramTable.getSelectionModel().addListSelectionListener(getListener1());
        paramTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        model.addTableModelListener(getListener2());
        paramTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), ACTION_INLINE_EDITOR); //NOI18N
        paramTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), ACTION_INLINE_EDITOR); //NOI18N
        paramTable.getActionMap().put(ACTION_INLINE_EDITOR, getEditAction()); //NOI18N
        paramTable.setSurrendersFocusOnKeystroke(true);
        paramTable.setCellSelectionEnabled(false);
        paramTable.setRowSelectionAllowed(true);
        // paramTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE); //NOI18N
        paramTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //NOI18N
        westPanel.setViewportView(paramTable);
        paramTable.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_paramTable")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(westPanel, gridBagConstraints);

        paramTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        paramTitle.setLabelFor(paramTable);
        org.openide.awt.Mnemonics.setLocalizedText(paramTitle, bundle.getString("LBL_ChangeParsParameters")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(paramTitle, gridBagConstraints);

        previewChange.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getBundle(ChangeParametersPanel.class).getString("LBL_ChangeParsPreview"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(previewChange, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void modifiersComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifiersComboActionPerformed
        previewChange.setText(genDeclarationString());
        previewChange.setToolTipText(genDeclarationString());
    }//GEN-LAST:event_modifiersComboActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        acceptEditedValue(); 
        int[] selectedRows = paramTable.getSelectedRows();
        ListSelectionModel selectionModel = paramTable.getSelectionModel();
        for (int i = 0; i < selectedRows.length; ++i) {
            boolean b = ((Boolean) ((Vector) model.getDataVector().get(selectedRows[i] - i)).get(4)).booleanValue();
            if (!b) {
                String title = getString("LBL_ChangeParsCannotDeleteTitle");
                String mes = MessageFormat.format(getString("LBL_ChangeParsCannotDelete"),((Vector) model.getDataVector().get(selectedRows[i] - i)).get(0));
                int a = new JOptionPane().showConfirmDialog(this, mes, title, JOptionPane.YES_NO_OPTION);
                if (a==JOptionPane.YES_OPTION) {
                    model.removeRow(selectedRows[i] - i);
                    selectionModel.clearSelection();
                }
            } else {
                model.removeRow(selectedRows[i] - i);
                selectionModel.clearSelection();
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
        doMove(1);
    }//GEN-LAST:event_moveDownButtonActionPerformed

    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
        doMove(-1);
    }//GEN-LAST:event_moveUpButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        acceptEditedValue(); 
        int rowCount = model.getRowCount();
        model.addRow(new Object[] { "par" + rowCount, "Object", "null", new Integer(-1), Boolean.TRUE }); // NOI18N
        paramTable.scrollRectToVisible(paramTable.getCellRect(rowCount, 0, false));
        paramTable.changeSelection(rowCount, 0, false, false);
        autoEdit(paramTable);
    }//GEN-LAST:event_addButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel eastPanel;
    private javax.swing.JPanel fillPanel;
    private javax.swing.JComboBox modifiersCombo;
    private javax.swing.JLabel modifiersLabel;
    private javax.swing.JPanel modifiersPanel;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JTable paramTable;
    private javax.swing.JLabel paramTitle;
    private javax.swing.JLabel previewChange;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane westPanel;
    // End of variables declaration//GEN-END:variables

    private ListSelectionListener getListener1() {
        return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                if (!lsm.isSelectionEmpty()) {
                    // Find out which indexes are selected.
                    int minIndex = lsm.getMinSelectionIndex();
                    int maxIndex = lsm.getMaxSelectionIndex();
                    setButtons(minIndex, maxIndex);
                    
                    boolean enableRemoveBtn = true;
                    for (int i = minIndex; i <= maxIndex; i++) {
                        enableRemoveBtn = model.isRemovable(i);
                        if (!enableRemoveBtn)
                            break;
                    }
                    removeButton.setEnabled(enableRemoveBtn);
                }
                else {
                    moveDownButton.setEnabled(false);
                    moveUpButton.setEnabled(false);
                    removeButton.setEnabled(false);
                }
            }
        };
    }
    
    private TableModelListener getListener2() {
        return new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                // update buttons availability
                int[] selectedRows = paramTable.getSelectedRows();
                if (selectedRows.length == 0) {
                    removeButton.setEnabled(false);
                }
                else {
                    boolean enableRemoveBtn = true;
                    for (int i = 0; i < selectedRows.length; i++) {
                        if (selectedRows[i] < model.getRowCount()) {
                            enableRemoveBtn = model.isCellEditable(selectedRows[i], 0);
                            if (!enableRemoveBtn)
                                break;
                        }
                    }
                    removeButton.setEnabled(enableRemoveBtn);
                    int min = selectedRows[0];
                    int max = selectedRows[selectedRows.length - 1];
                    setButtons(min, max);
                }
                
                // update preview
                previewChange.setText(genDeclarationString());
                previewChange.setToolTipText(genDeclarationString());
                
                parent.stateChanged(null);
            }
        };
    }

    private void initTableData(CompilationController info) {
        ExecutableElement method = (ExecutableElement) refactoredObj.resolveElement(info);
        MethodTree tree = info.getTrees().getTree(method);
        
        parameterSpan = info.getTreeUtilities().findMethodParameterSpan(tree);
        
        List<? extends VariableElement> pars = method.getParameters();

//        List typeList = new ArrayList();
//        for (Iterator parIt = pars.iterator(); parIt.hasNext(); ) {
//            Parameter par = (Parameter) parIt.next();
//            typeList.add(par.getType());
//        }

        Collection<ExecutableElement> allMethods = new ArrayList();
        allMethods.addAll(RetoucheUtils.getOverridenMethods(method, info));
        allMethods.addAll(RetoucheUtils.getOverridingMethods(method, info));
        allMethods.add(method);
        
        for (ExecutableElement currentMethod: allMethods) {
            int originalIndex = 0;
            for (VariableElement par:currentMethod.getParameters()) {
                TypeMirror desc = par.asType();
                String typeRepresentation;
                if (method.isVarArgs() && originalIndex == pars.size()-1) {
                    typeRepresentation = getTypeStringRepresentation(((ArrayType)desc).getComponentType()) + " ..."; // NOI18N
                } else {
                    typeRepresentation = getTypeStringRepresentation(desc);
                }
                LocalVarScanner scan = new LocalVarScanner(info, null);
                scan.scan(info.getTrees().getPath(method), par);
                Boolean removable = !scan.hasRefernces();
                if (model.getRowCount()<=originalIndex) {
                    Object[] parRep = new Object[] { par.toString(), typeRepresentation, "", new Integer(originalIndex), removable };
                    model.addRow(parRep);
                } else {
                    removable = Boolean.valueOf(model.isRemovable(originalIndex) && removable.booleanValue());
                    ((Vector) model.getDataVector().get(originalIndex)).set(4, removable);
                }
                originalIndex++;
            }
        }
    }
    
    private static String getTypeStringRepresentation(TypeMirror desc) {
        return desc.toString();
    }

    private boolean acceptEditedValue() {
        TableCellEditor tce = paramTable.getCellEditor();
        if (tce != null)
            return paramTable.getCellEditor().stopCellEditing();
        return false;
    }
    
    private void doMove(int step) {
        acceptEditedValue(); 
        
        ListSelectionModel selectionModel = paramTable.getSelectionModel();
        int min = selectionModel.getMinSelectionIndex();
        int max = selectionModel.getMaxSelectionIndex();
        
        selectionModel.clearSelection();
        model.moveRow(min, max, min + step);
        selectionModel.addSelectionInterval(min + step, max + step);
    }
    
    private void setButtons(int min, int max) {
        int r = model.getRowCount() - 1;
        moveUpButton.setEnabled(min > 0 ? true : false);
        moveDownButton.setEnabled(max < r ? true : false);
    }
    
    private void initRenderer() {
        TableColumnModel tcm = paramTable.getColumnModel();
        paramTable.removeColumn(tcm.getColumn(3));
        paramTable.removeColumn(tcm.getColumn(3));
        Enumeration columns = paramTable.getColumnModel().getColumns();
        TableColumn tc = null;
        while (columns.hasMoreElements()) {
            tc = (TableColumn) columns.nextElement();
            tc.setCellRenderer(new ParamRenderer(paramTable.getDefaultRenderer(String.class)));
            tc.setCellEditor(new ParamEditor(paramTable.getDefaultEditor(String.class)));
        }
    }

    private Set<Modifier> modifiers = new HashSet<Modifier>();
    private void setModifier(Set<Modifier> mods) {
        this.modifiers.clear();
        // #170543: set only access modifiers
        if (mods.contains(Modifier.PRIVATE)) {
            this.modifiers.add(Modifier.PRIVATE);
            modifiersCombo.setSelectedIndex(MOD_PRIVATE_INDEX);
        } else if (mods.contains(Modifier.PROTECTED)) {
            this.modifiers.add(Modifier.PROTECTED);
            modifiersCombo.setSelectedIndex(MOD_PROTECTED_INDEX);
        } else if (mods.contains(Modifier.PUBLIC)) {
            this.modifiers.add(Modifier.PUBLIC);
            modifiersCombo.setSelectedIndex(MOD_PUBLIC_INDEX);
        } else
            modifiersCombo.setSelectedIndex(MOD_DEFAULT_INDEX);
    }

    public String genDeclarationString() {
        // generate preview for modifiers
        // access modifiers
        String mod = modifiersCombo.getSelectedIndex() != MOD_DEFAULT_INDEX /*default modifier?*/ ?
            (String) modifiersCombo.getSelectedItem() + ' ' : ""; // NOI18N
        
        StringBuffer buf = new StringBuffer(mod);
        // other than access modifiers - using data provided by the element
        // first of all, reset access modifier, because it is generated from combo value
//        String otherMod = Modifier.toString(((CallableFeature) refactoredObj).getModifiers() & 0xFFFFFFF8);
//        if (otherMod.length() != 0) {
//            buf.append(otherMod);
//            buf.append(' ');
//        }
        // generate the return type for the method and name
        // for the both - method and constructor
        String name;
        if (RetoucheUtils.getElementKind(refactoredObj) == ElementKind.METHOD) {
            buf.append(returnType);
            buf.append(' ');
            name = RetoucheUtils.getSimpleName(refactoredObj);
        } else {
            // for constructor, get name from the declaring class            
            // name = RetoucheUtils.getSimpleName(refactoredObj);
            name = enclosingClassName;
        }
        buf.append(name);
        buf.append('(');
        // generate parameters to the preview string
        List[] parameters = (List[]) model.getDataVector().toArray(new List[0]);
        if (parameters.length > 0) {
            int i;
            for (i = 0; i < parameters.length - 1; i++) {
                buf.append((String) parameters[i].get(1));
                buf.append(' ');
                buf.append((String) parameters[i].get(0));
                buf.append(',').append(' ');
            }
            buf.append((String) parameters[i].get(1));
            buf.append(' ');
            buf.append((String) parameters[i].get(0));
        }
        buf.append(')'); //NOI18N
        
        return buf.toString();
    }

    private static String getString(String key) {
        return NbBundle.getMessage(ChangeParametersPanel.class, key);
    }

    private static Action getEditAction() {
        if (editAction == null) {
            editAction = new EditAction();
        }
        return editAction;
    }

    private static void autoEdit(JTable tab) {
        if (tab.editCellAt(tab.getSelectedRow(), tab.getSelectedColumn(), null)
                && tab.getEditorComponent() != null) {
            JTextComponent field;
            if (tab.getEditorComponent() instanceof ChangeParametersButtonPanel) {
                field = (JTextComponent) ((ChangeParametersButtonPanel) tab.getEditorComponent()).getComp();
            } else {
                field = (JTextComponent) tab.getEditorComponent();
            }
            field.requestFocusInWindow();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    ////////////////////////////////////////////////////////////////////////////
    // this class is used for marking rows as read-only. If the user uses
    // standard DefaultTableModel, rows added through its methods is added
    // as a read-write. -- Use methods with Boolean paramater to add
    // rows marked as read-only.
    static class ParamTableModel extends DefaultTableModel {
        
        public ParamTableModel(Object[] data, int rowCount) {
            super(data, rowCount);
        }

        public boolean isCellEditable(int row, int column) {
            if (column > 2) {
                // check box indicating usage of parameter is not editable
                return false;
            }
            // otherwise, check that user can change only the values provided
            // for the new parameter. name change of old paramter is allowed.
            if(column > 0) {
                Integer origIdx = (Integer) ((Vector) getDataVector().get(row)).get(3);
                return origIdx.intValue() == -1 ? true : false;
            }
            return true;
        }
        
        public boolean isRemovable(int row) {
            return true;//((Boolean) ((Vector) getDataVector().get(row)).get(4)).booleanValue();
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    } // end ParamTableModel

    private static class EditAction extends AbstractAction {
        public void actionPerformed(ActionEvent ae) {
            autoEdit((JTable) ae.getSource());
        }
    }

    private class TypeAction extends AbstractAction {
        private final JTable table;
        private final Object value;
        private final int row;
        private final int col;

        private TypeAction(JTable table, Object value, int row, int col) {
            this.table = table;
            this.value = value;
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FileObject file = RetoucheUtils.getFileObject(refactoredObj);
            ElementHandle<TypeElement> type = TypeElementFinder.find(ClasspathInfo.create(file), null);
            if (type != null) {
                String fqn = type.getQualifiedName().toString();
                acceptEditedValue();
                table.setValueAt(fqn, row, col);
            }
        }
    }

    private class TableTabAction extends AbstractAction {
        private final Action originalTabAction;
        
        public TableTabAction(Action originalTabAction) {
            this.originalTabAction = originalTabAction;
        }

        public void actionPerformed(ActionEvent e) {
            boolean acceptEditedValue = acceptEditedValue();
            originalTabAction.actionPerformed(e);
            if(acceptEditedValue) {
                JTable table = (JTable) e.getSource();
                autoEdit(table);
            }
        }
    }

    class ParamRenderer implements TableCellRenderer {
        Color origBackground;
        ChangeParametersButtonPanel buttonpanel;
        private final TableCellRenderer original;

        public ParamRenderer(TableCellRenderer original) {
            setOpaque(true);
            origBackground = getBackground();
            buttonpanel = new ChangeParametersButtonPanel();
            this.original = original;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            boolean isRemovable = model.isRemovable(row);
            JComponent comp = (JComponent) original.getTableCellRendererComponent(table,  value, isSelected, hasFocus, row, column);
            if(column == 1 && table.isCellEditable(row, column)) {
                buttonpanel.setComp(comp);
                comp = buttonpanel;
            }
            
            if (!isSelected) {
                if (!isRemovable) {
                    comp.setBackground(UIManager.getColor("Panel.background")); // NOI18N
                } else {
                    comp.setBackground(origBackground);
                }
            }
            return comp;
        }
    }

    class ParamEditor implements TableCellEditor {

        private final TableCellEditor original;
        private JTextComponent editorPane;
        private int startOffset;

        public ParamEditor(TableCellEditor original) {
            this.original = original;
            ((DefaultCellEditor) original).setClickCountToStart(1);
            startOffset = 0;
        }

        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int col) {
            JTextComponent tableCellEditorComponent = (JTextComponent) original.getTableCellEditorComponent(table, value, isSelected, row, col);
            Component returnValue = tableCellEditorComponent;
            tableCellEditorComponent.setCaretPosition(tableCellEditorComponent.getText().length());
            tableCellEditorComponent.selectAll();
            if (col < 2) {
                try {
                    editorPane = new JEditorPane() {

                        @Override
                        public boolean isFocusCycleRoot() {
                            return false; // EditorPane should not be focusRoot when it is inside a table.
                        }

                        @Override
                        protected void processKeyEvent(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                                if (e.getID() == KeyEvent.KEY_PRESSED) {
                                    if (e.getModifiers() == KeyEvent.SHIFT_DOWN_MASK
                                            || e.getModifiers() == KeyEvent.SHIFT_MASK) {
                                        tableShiftTabAction.actionPerformed(new ActionEvent(paramTable,
                                                ActionEvent.ACTION_PERFORMED,
                                                null,
                                                e.getWhen(), ActionEvent.SHIFT_MASK));
                                    } else {
                                        tableTabAction.actionPerformed(new ActionEvent(paramTable,
                                                ActionEvent.ACTION_PERFORMED,
                                                null,
                                                e.getWhen(), e.getModifiers()));
                                    }
                                }
                            } else {
                                super.processKeyEvent(e);
                            }
                        }
                    };
                    
                    FileObject fileObject = refactoredObj.getFileObject();
                    DataObject dob = DataObject.find(fileObject);
                    editorPane.getDocument().putProperty(
                            Document.StreamDescriptionProperty,
                            dob);

                    DialogBinding.bindComponentToFile(fileObject, parameterSpan[0] + 1, parameterSpan[1] - parameterSpan[0], editorPane);                 
                    
                    returnValue = editorPane;
                    
                    if(col == 0) {
                        editorPane.setText(model.getValueAt(row, col+1) + " " + value.toString()); //NOI18N
                        startOffset = ((String)model.getValueAt(row, col + 1)).length() + 1;
                        int endOffset = value.toString().length() + startOffset;
                        editorPane.select(startOffset, endOffset);
                        try {
                            Position startPos = editorPane.getDocument().createPosition(startOffset);
                            Position endPos = editorPane.getDocument().createPosition(endOffset);
                            editorPane.putClientProperty("document-view-start-position", startPos); //NOI18N
                            editorPane.putClientProperty("document-view-end-position", endPos); //NOI18N
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }

                    if (col == 1) {
                        editorPane.setText(value.toString());
                        editorPane.selectAll();
                        ChangeParametersButtonPanel buttonPanel = new ChangeParametersButtonPanel();
                        buttonPanel.setButtonAction(new TypeAction(table, value, row, col));
                        buttonPanel.setComp(editorPane);
                        returnValue = buttonPanel;
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return returnValue;
        }

        public Object getCellEditorValue() {
            if(editorPane != null) {
                return editorPane.getText().substring(startOffset).replace(System.getProperty("line.separator"), "").trim(); //NOI18N
            }
            return original.getCellEditorValue();
        }

        public boolean isCellEditable(EventObject anEvent) {
            return original.isCellEditable(anEvent);
        }

        public boolean stopCellEditing() {
            return original.stopCellEditing();
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return original.shouldSelectCell(anEvent);
        }

        public void removeCellEditorListener(CellEditorListener l) {
            original.removeCellEditorListener(l);
        }

        public void cancelCellEditing() {
            original.cancelCellEditing();
        }

        public void addCellEditorListener(CellEditorListener l) {
            original.addCellEditorListener(l);
        }
    }
    
    class ChangeParametersButtonPanel extends JPanel {
        public static final String ELLIPSIS = "\u2026"; //NOI18N
        private JComponent comp;
        private JButton button;

        public ChangeParametersButtonPanel() {
            button = new JButton(ELLIPSIS) {

                @Override
                public boolean isFocusable() {
                    return (ChangeParametersButtonPanel.this.getParent() != null);
                }
            };
            setLayout(new BorderLayout(0, 0));
            add(button, BorderLayout.EAST);
        }

        public void setComp(JComponent comp) {
            this.comp = comp;
            add(comp, BorderLayout.CENTER);
        }

        public JComponent getComp() {
            return comp;
        }

        private void setButtonAction(Action action) {
            button.setAction(action);
            button.setText(ELLIPSIS);
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
        }
     }
    // end INNERCLASSES
}
