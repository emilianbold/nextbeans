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

import com.sun.javadoc.Doc;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.Set;
import javax.lang.model.element.*;
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
import org.netbeans.editor.Utilities;
import org.netbeans.modules.refactoring.java.RefactoringModule;
import org.netbeans.modules.refactoring.java.RetoucheUtils;
import org.netbeans.modules.refactoring.java.api.ChangeParametersRefactoring.ParameterInfo;
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
 * or remove parameters. You can also change the methods access modifier, name
 * and return type.
 *
 * @author  Pavel Flaska, Jan Becicka, Ralph Ruijs
 */
public class ChangeParametersPanel extends JPanel implements CustomRefactoringPanel {
    private static final String MIME_JAVA = "text/x-java"; // NOI18N
    private static final String UPDATEJAVADOC = "updateJavadoc.changeParameters"; // NOI18N
    private static final String GENJAVADOC = "generateJavadoc.changeParameters"; // NOI18N
    private static final String COMPATIBLE = "compatible.changeParameters"; // NOI18N

    TreePathHandle refactoredObj;
    private int[] parameterSpan;
    ParamTableModel model;
    private ChangeListener parent;
    private TableTabAction tableTabAction;
    private TableTabAction tableShiftTabAction;
    
    private static Action editAction = null;
    private Action returnTypeAction;
    private Doc javadocDoc;
    
    private static final String[] modifierNames = {
        "public", // NOI18N
        "protected", // NOI18N
        "<default>", // NOI18N
        "private" // NOI18N
    };
    private ParameterInfo[] preConfiguration;
    private final ReturnTypeDocListener returnTypeDocListener;
    private final MethodNameDocListener methodNameDocListener;
    private final JComponent[] singleLineEditor;
    private boolean methodNameChanged;
    private boolean returnTypeChanged;
    private boolean isConstructor;
    
    
    public Component getComponent() {
        return this;
    }
    
    private static final String[] columnNames = {
        getString("LBL_ChangeParsColType"), // NOI18N
        getString("LBL_ChangeParsColName"), // NOI18N
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
    public ChangeParametersPanel(TreePathHandle refactoredObj, ChangeListener parent, ParameterInfo[] preConfiguration) {
        returnTypeDocListener = new ReturnTypeDocListener();
        methodNameDocListener = new MethodNameDocListener();
        this.refactoredObj = refactoredObj;
        this.parent = parent;
        this.preConfiguration = preConfiguration;
        model = new ParamTableModel(columnNames, 0);
        this.returnTypeAction = new ReturnTypeAction();
        singleLineEditor = Utilities.createSingleLineEditor(MIME_JAVA);
        
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
        
        methodNameText.getDocument().addDocumentListener(methodNameDocListener);
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
                        methodNameText.setText(e.getSimpleName().toString());
                        final String returnType = e.getReturnType().toString();
                        
                        MethodTree methodTree = (MethodTree) refactoredObj.resolve(info).getLeaf();
                        Tree tree = methodTree.getReturnType();
                        final long start = info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), tree);
                        final long end = info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), tree);
                        
                        final FileObject fileObject = refactoredObj.getFileObject();
                        DataObject dob = DataObject.find(fileObject);
                        ((JEditorPane)singleLineEditor[1]).getDocument().putProperty(
                                Document.StreamDescriptionProperty,
                                dob);
                       
                        javadocDoc = info.getElementUtilities().javaDocFor(e);
                        if(javadocDoc.commentText() == null || javadocDoc.getRawCommentText().equals("")) {
                            chkGenJavadoc.setEnabled(true);
                            chkGenJavadoc.setVisible(true);
                            chkUpdateJavadoc.setVisible(false);
                        } else {
                            chkUpdateJavadoc.setEnabled(true);
                            chkUpdateJavadoc.setVisible(true);
                            chkGenJavadoc.setVisible(false);
                        }
                        TreePath enclosingClass = JavaRefactoringUtils.findEnclosingClass(info, refactoredObj.resolve(info), true, true, true, true, true);
                        TreePathHandle tph = TreePathHandle.create(enclosingClass, info);
                        Element enclosingElement = tph.resolveElement(info);
                        if (enclosingElement.getKind().isInterface() || inheritedFromInterface(e, info.getElementUtilities())) {
                            modifiersCombo.setEnabled(false);
                        }
                        initTableData(info);
                        setModifier(e.getModifiers());
                        for (TypeParameterElement typeParameterElement : e.getTypeParameters()) {
                            Tree typeParameterTree = info.getTrees().getTree(typeParameterElement);
                            typeParameters.add(typeParameterTree.toString());
                        }
                        
                        isConstructor = e.getKind() == ElementKind.CONSTRUCTOR;
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if(isConstructor) {
                                    methodNameText.setEnabled(false);
                                    singleLineEditor[1].setEnabled(false);
                                } else {
                                    DialogBinding.bindComponentToFile(fileObject, (int) start, (int) (end - start), ((JEditorPane)singleLineEditor[1]));
                                }
                                ((JEditorPane)singleLineEditor[1]).setText(returnType);
                                ((JEditorPane)singleLineEditor[1]).getDocument().addDocumentListener(returnTypeDocListener);
                                ((JEditorPane)singleLineEditor[1]).putClientProperty(
                                    "HighlightsLayerExcludes", //NOI18N
                                    "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" //NOI18N
                                );
                                initialized = true;
                                methodNameChanged = false;
                                returnTypeChanged = false;
                                updatePreview();
                            }});
                    }
                    catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                public void cancel() {
                }
            }, true);
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
        Set<Modifier> modifiers = new HashSet<Modifier>(1);
//        modifiers.remove(Modifier.PRIVATE);
//        modifiers.remove(Modifier.PUBLIC);
//        modifiers.remove(Modifier.PROTECTED);
        
        switch (modifiersCombo.getSelectedIndex()) {
        case MOD_PRIVATE_INDEX: modifiers.add(Modifier.PRIVATE);break;
        case MOD_DEFAULT_INDEX: break; /* no modifier */
        case MOD_PROTECTED_INDEX: modifiers.add(Modifier.PROTECTED); break;
        case MOD_PUBLIC_INDEX: modifiers.add(Modifier.PUBLIC); break;
        }
        return modifiers;
    }
    
    protected Javadoc getJavadoc() {
        if(chkUpdateJavadoc.isVisible() && chkUpdateJavadoc.isSelected()) {
            return Javadoc.UPDATE;
        } else if(chkGenJavadoc.isVisible() && chkGenJavadoc.isSelected()) {
            return Javadoc.GENERATE;
        } else {
            return Javadoc.NONE;
        }
    }
    
    public enum Javadoc {
        NONE,
        UPDATE,
        GENERATE
    }
    
    protected String getMethodName() {
        return methodNameChanged? methodNameText.getText() : null;
    }
    
    protected String getReturnType() {
        return returnTypeChanged? ((JEditorPane)singleLineEditor[1]).getText() : null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modifiersPanel = new javax.swing.JPanel();
        modifiersLabel = new javax.swing.JLabel();
        modifiersCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        methodNameText = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = (JScrollPane)singleLineEditor[0];
        westPanel = new javax.swing.JScrollPane();
        paramTable = new javax.swing.JTable();
        paramTitle = new javax.swing.JLabel();
        chkUpdateJavadoc = new javax.swing.JCheckBox();
        chkGenJavadoc = new javax.swing.JCheckBox();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        previewChange = new javax.swing.JLabel();
        chkCompatible = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setAutoscrolls(true);
        setName(getString("LBL_TitleChangeParameters"));

        modifiersLabel.setLabelFor(modifiersCombo);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/refactoring/java/ui/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(modifiersLabel, bundle.getString("LBL_ChangeParsMods")); // NOI18N

        modifiersCombo.setModel(new DefaultComboBoxModel(modifierNames));
        modifiersCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifiersComboActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ChangeParametersPanel.class, "ChangeParametersPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ChangeParametersPanel.class, "ChangeParametersPanel.jLabel2.text")); // NOI18N

        methodNameText.setPreferredSize(new java.awt.Dimension(112, 27));

        jButton1.setAction(getReturnTypeAction());
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "…"); // NOI18N

        javax.swing.GroupLayout modifiersPanelLayout = new javax.swing.GroupLayout(modifiersPanel);
        modifiersPanel.setLayout(modifiersPanelLayout);
        modifiersPanelLayout.setHorizontalGroup(
            modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifiersPanelLayout.createSequentialGroup()
                .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(modifiersPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(modifiersLabel))
                    .addComponent(modifiersCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(modifiersPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(modifiersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap())
                    .addComponent(methodNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))
        );
        modifiersPanelLayout.setVerticalGroup(
            modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, modifiersPanelLayout.createSequentialGroup()
                .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modifiersLabel)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(modifiersCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(modifiersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(methodNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        modifiersCombo.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_modifiersCombo")); // NOI18N

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

        paramTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        paramTitle.setLabelFor(paramTable);
        org.openide.awt.Mnemonics.setLocalizedText(paramTitle, bundle.getString("LBL_ChangeParsParameters")); // NOI18N

        chkUpdateJavadoc.setSelected(((Boolean) RefactoringModule.getOption(UPDATEJAVADOC, Boolean.FALSE)).booleanValue());
        org.openide.awt.Mnemonics.setLocalizedText(chkUpdateJavadoc, org.openide.util.NbBundle.getMessage(ChangeParametersPanel.class, "LBL_UpdateJavadoc")); // NOI18N
        chkUpdateJavadoc.setEnabled(false);
        chkUpdateJavadoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkUpdateJavadocItemStateChanged(evt);
            }
        });

        chkGenJavadoc.setSelected(((Boolean) RefactoringModule.getOption(GENJAVADOC, Boolean.FALSE)).booleanValue());
        org.openide.awt.Mnemonics.setLocalizedText(chkGenJavadoc, org.openide.util.NbBundle.getMessage(ChangeParametersPanel.class, "LBL_GenJavadoc")); // NOI18N
        chkGenJavadoc.setEnabled(false);
        chkGenJavadoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkGenJavadocItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addButton, bundle.getString("LBL_ChangeParsAdd")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, bundle.getString("LBL_ChangeParsRemove")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(moveUpButton, bundle.getString("LBL_ChangeParsMoveUp")); // NOI18N
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, bundle.getString("LBL_ChangeParsMoveDown")); // NOI18N
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 0));
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        previewChange.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChangeParametersPanel.class, "LBL_ChangeParsPreview"))); // NOI18N
        previewChange.setOpaque(true);
        jScrollPane1.setViewportView(previewChange);

        org.openide.awt.Mnemonics.setLocalizedText(chkCompatible, org.openide.util.NbBundle.getMessage(ChangeParametersPanel.class, "IntroduceParameterPanel.chkIsCompatible.text")); // NOI18N
        chkCompatible.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCompatibleItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paramTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(westPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(moveDownButton)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(moveUpButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(modifiersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(chkCompatible)
                .addContainerGap(392, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUpdateJavadoc)
                    .addComponent(chkGenJavadoc))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, moveDownButton, moveUpButton, removeButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(paramTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moveUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moveDownButton))
                    .addComponent(westPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modifiersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUpdateJavadoc)
                    .addComponent(chkGenJavadoc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCompatible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, Short.MAX_VALUE)
                .addContainerGap())
        );

        addButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsAdd")); // NOI18N
        removeButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsRemove")); // NOI18N
        moveUpButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsMoveUp")); // NOI18N
        moveDownButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ChangeParsMoveDown")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void modifiersComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifiersComboActionPerformed
        updatePreview();
    }//GEN-LAST:event_modifiersComboActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        acceptEditedValue(); 
        int[] selectedRows = paramTable.getSelectedRows();
        ListSelectionModel selectionModel = paramTable.getSelectionModel();
        for (int i = 0; i < selectedRows.length; ++i) {
            boolean b = ((Boolean) ((Vector) model.getDataVector().get(selectedRows[i] - i)).get(4)).booleanValue();
            if (!b) {
                String title = getString("LBL_ChangeParsCannotDeleteTitle");
                String mes = MessageFormat.format(getString("LBL_ChangeParsCannotDelete"),((Vector) model.getDataVector().get(selectedRows[i] - i)).get(1));
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
        model.addRow(new Object[] { "Object", "par" + rowCount, "null", new Integer(-1), Boolean.TRUE }); // NOI18N
        paramTable.scrollRectToVisible(paramTable.getCellRect(rowCount, 0, false));
        paramTable.changeSelection(rowCount, 0, false, false);
        autoEdit(paramTable);
    }//GEN-LAST:event_addButtonActionPerformed

    private void chkUpdateJavadocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkUpdateJavadocItemStateChanged
        Boolean b = evt.getStateChange() == ItemEvent.SELECTED ? Boolean.TRUE : Boolean.FALSE;
        RefactoringModule.setOption(UPDATEJAVADOC, b); // NOI18N
        updatePreview();
    }//GEN-LAST:event_chkUpdateJavadocItemStateChanged

    private void chkGenJavadocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkGenJavadocItemStateChanged
        Boolean b = evt.getStateChange() == ItemEvent.SELECTED ? Boolean.TRUE : Boolean.FALSE;
        RefactoringModule.setOption(GENJAVADOC, b); // NOI18N
        updatePreview();
    }//GEN-LAST:event_chkGenJavadocItemStateChanged

    private void chkCompatibleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCompatibleItemStateChanged
        Boolean b = evt.getStateChange() == ItemEvent.SELECTED ? Boolean.TRUE : Boolean.FALSE;
        RefactoringModule.setOption(COMPATIBLE, b);
        updatePreview();
    }//GEN-LAST:event_chkCompatibleItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JCheckBox chkCompatible;
    private javax.swing.JCheckBox chkGenJavadoc;
    private javax.swing.JCheckBox chkUpdateJavadoc;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField methodNameText;
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

    
    protected boolean isCompatible() {
        return chkCompatible.isSelected();
    }
    
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
                
                updatePreview();
            }
        };
    }
    
    private void updatePreview() {
        if(initialized) {
            // update preview
            previewChange.setText(genDeclarationString());
            previewChange.setToolTipText(genDeclarationString());

            parent.stateChanged(null);
        }
    }

    private void initTableData(CompilationController info) {
        ExecutableElement method = (ExecutableElement) refactoredObj.resolveElement(info);
        MethodTree tree = info.getTrees().getTree(method);
        
        parameterSpan = info.getTreeUtilities().findMethodParameterSpan(tree);
        
        List<? extends VariableElement> pars = method.getParameters();

        Collection<ExecutableElement> allMethods = new ArrayList();
        allMethods.addAll(RetoucheUtils.getOverridenMethods(method, info));
        allMethods.addAll(RetoucheUtils.getOverridingMethods(method, info));
        allMethods.add(method);
        
        for (ExecutableElement currentMethod: allMethods) {
            int originalIndex = 0;
            for (VariableElement par:currentMethod.getParameters()) {
                VariableTree parTree = (VariableTree) info.getTrees().getTree(par);
                String typeRepresentation;
                if (method.isVarArgs() && originalIndex == pars.size()-1) {
                    typeRepresentation = getTypeStringRepresentation(parTree) + " ..."; // NOI18N
                } else {
                    typeRepresentation = getTypeStringRepresentation(parTree);
                }
                LocalVarScanner scan = new LocalVarScanner(info, null);
                scan.scan(info.getTrees().getPath(method), par);
                Boolean removable = !scan.hasRefernces();
                if (model.getRowCount()<=originalIndex) {
                    Object[] parRep = new Object[] { typeRepresentation, par.toString(), "", new Integer(originalIndex), removable };
                    model.addRow(parRep);
                } else {
                    removable = Boolean.valueOf(model.isRemovable(originalIndex) && removable.booleanValue());
                    ((Vector) model.getDataVector().get(originalIndex)).set(4, removable);
                }
                originalIndex++;
            }
        }
        if(preConfiguration != null) {
            List<Object[]> newModel = new LinkedList<Object[]>();
            for (int i = 0; i < preConfiguration.length; i++) {
                ParameterInfo parameterInfo = preConfiguration[i];
                newModel.add(new Object[] {parameterInfo.getName(),
                    parameterInfo.getType(),
                    parameterInfo.getDefaultValue() == null? "" : parameterInfo.getDefaultValue(),
                    parameterInfo.getOriginalIndex(),
                    model.isRemovable(parameterInfo.getOriginalIndex())});
            }
            while(model.getRowCount() > 0) {
                model.removeRow(0);
            }
            for (Object[] row : newModel) {
                model.addRow(row);
            }
        }
    }
    
    private static String getTypeStringRepresentation(VariableTree desc) {
        return desc.getType().toString();
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
    private List<String> typeParameters = new LinkedList<String>();
    private void setModifier(Set<Modifier> mods) {
        modifiers.clear();
        modifiers.addAll(mods);
        modifiers.remove(Modifier.PRIVATE);
        modifiers.remove(Modifier.PUBLIC);
        modifiers.remove(Modifier.PROTECTED);
        // #170543: set only access modifiers
        if (mods.contains(Modifier.PRIVATE)) {
//            this.modifiers.add(Modifier.PRIVATE);
            modifiersCombo.setSelectedIndex(MOD_PRIVATE_INDEX);
        } else if (mods.contains(Modifier.PROTECTED)) {
//            this.modifiers.add(Modifier.PROTECTED);
            modifiersCombo.setSelectedIndex(MOD_PROTECTED_INDEX);
        } else if (mods.contains(Modifier.PUBLIC)) {
//            this.modifiers.add(Modifier.PUBLIC);
            modifiersCombo.setSelectedIndex(MOD_PUBLIC_INDEX);
        } else
            modifiersCombo.setSelectedIndex(MOD_DEFAULT_INDEX);
    }

    public String genDeclarationString() {
        StringBuilder buf = new StringBuilder("<html>");
        
        // generate preview for modifiers
        // access modifiers
        String mod = modifiersCombo.getSelectedIndex() != MOD_DEFAULT_INDEX /*default modifier?*/ ?
            (String) modifiersCombo.getSelectedItem() + ' ' : ""; // NOI18N
        buf.append(mod);
        
        // other than access modifiers - using data provided by the element
        for (Modifier modifier : modifiers) {
            buf.append(modifier.toString());
            buf.append(' '); //NOI18N
        }
        // Type parameters opt
        for (String typeParameterElement : typeParameters) {
            buf.append("&lt;").append(typeParameterElement).append("&gt;"); //NOI18N
            buf.append(' '); //NOI18N
        }
        
        // generate the return type for the method and name
        // for the both - method and constructor
        if (RetoucheUtils.getElementKind(refactoredObj) == ElementKind.METHOD) {
            buf.append(((JEditorPane)singleLineEditor[1]).getText());
            buf.append(' '); //NOI18N
        }
        buf.append(methodNameText.getText());
        buf.append('('); //NOI18N

        // generate parameters to the preview string
        List[] parameters = (List[]) model.getDataVector().toArray(new List[0]);
        if (parameters.length > 0) {
            int i;
            for (i = 0; i < parameters.length - 1; i++) {
                buf.append((String) parameters[i].get(0));
                buf.append(' '); //NOI18N
                buf.append((String) parameters[i].get(1));
                buf.append(',').append(' '); //NOI18N
            }
            buf.append((String) parameters[i].get(0));
            buf.append(' '); //NOI18N
            buf.append((String) parameters[i].get(1));
        }
        buf.append(")</html>"); //NOI18N
        
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
            // otherwise, check that user can only change the default value
            // for new parameters.
            if(column > 1) {
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
    
    private class ReturnTypeAction extends AbstractAction {

        public ReturnTypeAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FileObject file = RetoucheUtils.getFileObject(refactoredObj);
            ElementHandle<TypeElement> type = TypeElementFinder.find(ClasspathInfo.create(file), null);
            if (type != null) {
                String fqn = type.getQualifiedName().toString();
                ((JEditorPane)singleLineEditor[1]).setText(fqn);
                ((JEditorPane)singleLineEditor[1]).selectAll();
            }
        }
    }

    private Action getReturnTypeAction() {
        return returnTypeAction;
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
            boolean isEditable = model.isCellEditable(row, column);
            JComponent comp = (JComponent) original.getTableCellRendererComponent(table,  value, isSelected, hasFocus, row, column);
            if(column == 0 && table.isCellEditable(row, column)) {
                buttonpanel.setComp(comp);
                comp = buttonpanel;
            }
            
            if (!isSelected) {
                if (!isEditable) {
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
                    
                    if(col == 1) {
                        editorPane.setText(model.getValueAt(row, col-1) + " " + value.toString()); //NOI18N
                        startOffset = ((String)model.getValueAt(row, col - 1)).length() + 1;
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

                    if (col == 0) {
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
    
    private class ReturnTypeDocListener implements DocumentListener {
        public ReturnTypeDocListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            returnTypeChanged = true;
            updatePreview();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            returnTypeChanged = true;
            updatePreview();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            returnTypeChanged = true;
            updatePreview();
        }
    }
    
    private class MethodNameDocListener implements DocumentListener {

        public MethodNameDocListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            methodNameChanged = true;
            updatePreview();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            methodNameChanged = true;
            updatePreview();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            methodNameChanged = true;
            updatePreview();
        }
    }
    // end INNERCLASSES
}
