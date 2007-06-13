/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.common.method.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.j2ee.common.method.MethodModel;

/**
 *
 * @author Martin Adamek
 */
public final class MethodCustomizerPanel extends javax.swing.JPanel {
    
    public static final String NAME = "name";
    public static final String RETURN_TYPE = "returnType";
    public static final String INTERFACES = "interfaces";
    
    // immutable method prototype
    private final MethodModel methodModel;
    private final ParametersPanel parametersPanel;
    private final ExceptionsPanel exceptionsPanel;
    private final boolean hasInterfaces;
    
    private MethodCustomizerPanel(
            MethodModel methodModel,
            boolean hasLocal,
            boolean hasRemote,
            boolean selectLocal,
            boolean selectRemote,
            boolean hasReturnType,
            String  ejbql,
            boolean hasFinderCardinality,
            boolean hasExceptions,
            boolean hasInterfaces) {
        initComponents();
        
        this.methodModel = methodModel;
        this.hasInterfaces = hasInterfaces;
        
        nameTextField.setText(methodModel.getName());
        returnTypeTextField.setText(methodModel.getReturnType());
        
        localRadio.setEnabled(hasLocal);
        remoteRadio.setEnabled(hasRemote);
        bothRadio.setEnabled(hasLocal && hasRemote);
        localRadio.setSelected(selectLocal && !selectRemote);
        remoteRadio.setSelected(selectRemote && !selectLocal);
        bothRadio.setSelected(selectLocal && selectRemote);
        
        if (!hasReturnType) {
            disableReturnType();
        }
        if (ejbql == null) {
            ejbqlPanel.setVisible(false);
        } else {
            ejbqlTextArea.setText(ejbql);
        }
        cardinalityPanel.setVisible(hasFinderCardinality);
        exceptionsContainerPanel.setVisible(hasExceptions);
        interfacesPanel.setVisible(hasInterfaces);
        
        parametersPanel = new ParametersPanel(methodModel.getParameters());
        parametersContainerPanel.add(parametersPanel);
        
        exceptionsPanel = hasExceptions ? new ExceptionsPanel(methodModel.getExceptions()) : null;
        if (hasExceptions) {
            exceptionsContainerPanel.add(exceptionsPanel);
        }
        
        // listeners
        nameTextField.getDocument().addDocumentListener(new SimpleListener(NAME));
        returnTypeTextField.getDocument().addDocumentListener(new SimpleListener(RETURN_TYPE));
        SimpleListener interfacesListener = new SimpleListener(INTERFACES);
        localRadio.addActionListener(interfacesListener);
        remoteRadio.addActionListener(interfacesListener);
        bothRadio.addActionListener(interfacesListener);
    }
    
    public static MethodCustomizerPanel create(MethodModel methodModel, boolean hasLocal, boolean hasRemote, boolean selectLocal, boolean selectRemote,
            boolean hasReturnType, String  ejbql, boolean hasFinderCardinality, boolean hasExceptions, boolean hasInterfaces) {
        return new MethodCustomizerPanel(methodModel, hasLocal, hasRemote, selectLocal, selectRemote,
                hasReturnType, ejbql, hasFinderCardinality, hasExceptions, hasInterfaces);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	super.addPropertyChangeListener(listener);
         // first validation before any real event is send
        firePropertyChange(NAME, null, null);
        firePropertyChange(RETURN_TYPE, null, null);
        firePropertyChange(INTERFACES, null, null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        finderCardinalityButtonGroup = new javax.swing.ButtonGroup();
        interfaceButtonGroup = new javax.swing.ButtonGroup();
        exceptionAndParameterPane = new javax.swing.JTabbedPane();
        parametersContainerPanel = new javax.swing.JPanel();
        exceptionsContainerPanel = new javax.swing.JPanel();
        errorTextField = new javax.swing.JTextField();
        returnTypeLabel = new javax.swing.JLabel();
        returnTypeTextField = new javax.swing.JTextField();
        nameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        interfacesPanel = new javax.swing.JPanel();
        interfaceLabel = new javax.swing.JLabel();
        localRadio = new javax.swing.JRadioButton();
        remoteRadio = new javax.swing.JRadioButton();
        bothRadio = new javax.swing.JRadioButton();
        cardinalityPanel = new javax.swing.JPanel();
        cardinalityLabel = new javax.swing.JLabel();
        oneRadioButton = new javax.swing.JRadioButton();
        manyRadioButton = new javax.swing.JRadioButton();
        ejbqlPanel = new javax.swing.JPanel();
        ejbqlLabel = new javax.swing.JLabel();
        ejbqlScrollPane = new javax.swing.JScrollPane();
        ejbqlTextArea = new javax.swing.JTextArea();

        parametersContainerPanel.setLayout(new java.awt.BorderLayout());
        exceptionAndParameterPane.addTab(org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.parametersContainerPanel.TabConstraints.tabTitle"), parametersContainerPanel); // NOI18N

        exceptionsContainerPanel.setLayout(new java.awt.BorderLayout());
        exceptionAndParameterPane.addTab(org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.exceptionsPanel.TabConstraints.tabTitle"), exceptionsContainerPanel); // NOI18N

        errorTextField.setBackground(java.awt.SystemColor.control);
        errorTextField.setEditable(false);
        errorTextField.setText(org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.errorTextField.text")); // NOI18N
        errorTextField.setBorder(null);

        returnTypeLabel.setLabelFor(returnTypeTextField);
        org.openide.awt.Mnemonics.setLocalizedText(returnTypeLabel, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.returnTypeLabel.text")); // NOI18N

        returnTypeTextField.setText(org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.returnTypeTextField.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.nameTextField.text")); // NOI18N

        jLabel1.setLabelFor(nameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(interfaceLabel, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.interfaceLabel.text")); // NOI18N

        interfaceButtonGroup.add(localRadio);
        org.openide.awt.Mnemonics.setLocalizedText(localRadio, "Local");
        localRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        interfaceButtonGroup.add(remoteRadio);
        org.openide.awt.Mnemonics.setLocalizedText(remoteRadio, "Remote");
        remoteRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        interfaceButtonGroup.add(bothRadio);
        org.openide.awt.Mnemonics.setLocalizedText(bothRadio, "Both");
        bothRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout interfacesPanelLayout = new org.jdesktop.layout.GroupLayout(interfacesPanel);
        interfacesPanel.setLayout(interfacesPanelLayout);
        interfacesPanelLayout.setHorizontalGroup(
            interfacesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interfacesPanelLayout.createSequentialGroup()
                .add(interfaceLabel)
                .add(18, 18, 18)
                .add(localRadio)
                .add(18, 18, 18)
                .add(remoteRadio)
                .add(18, 18, 18)
                .add(bothRadio)
                .addContainerGap(139, Short.MAX_VALUE))
        );
        interfacesPanelLayout.setVerticalGroup(
            interfacesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interfacesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(interfaceLabel)
                .add(localRadio)
                .add(remoteRadio)
                .add(bothRadio))
        );

        org.openide.awt.Mnemonics.setLocalizedText(cardinalityLabel, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.cardinalityLabel.text")); // NOI18N

        finderCardinalityButtonGroup.add(oneRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(oneRadioButton, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.oneRadioButton.text")); // NOI18N
        oneRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        finderCardinalityButtonGroup.add(manyRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(manyRadioButton, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.manyRadioButton.text")); // NOI18N
        manyRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout cardinalityPanelLayout = new org.jdesktop.layout.GroupLayout(cardinalityPanel);
        cardinalityPanel.setLayout(cardinalityPanelLayout);
        cardinalityPanelLayout.setHorizontalGroup(
            cardinalityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardinalityPanelLayout.createSequentialGroup()
                .add(cardinalityLabel)
                .add(18, 18, 18)
                .add(oneRadioButton)
                .add(18, 18, 18)
                .add(manyRadioButton)
                .addContainerGap(216, Short.MAX_VALUE))
        );
        cardinalityPanelLayout.setVerticalGroup(
            cardinalityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardinalityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(cardinalityLabel)
                .add(oneRadioButton)
                .add(manyRadioButton))
        );

        org.openide.awt.Mnemonics.setLocalizedText(ejbqlLabel, org.openide.util.NbBundle.getMessage(MethodCustomizerPanel.class, "MethodCustomizerPanel.ejbqlLabel.text")); // NOI18N

        ejbqlScrollPane.setBorder(null);

        ejbqlTextArea.setColumns(20);
        ejbqlTextArea.setRows(5);
        ejbqlTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ejbqlScrollPane.setViewportView(ejbqlTextArea);

        org.jdesktop.layout.GroupLayout ejbqlPanelLayout = new org.jdesktop.layout.GroupLayout(ejbqlPanel);
        ejbqlPanel.setLayout(ejbqlPanelLayout);
        ejbqlPanelLayout.setHorizontalGroup(
            ejbqlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ejbqlPanelLayout.createSequentialGroup()
                .add(ejbqlLabel)
                .addContainerGap(430, Short.MAX_VALUE))
            .add(ejbqlScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        ejbqlPanelLayout.setVerticalGroup(
            ejbqlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ejbqlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ejbqlLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ejbqlScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, exceptionAndParameterPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                    .add(ejbqlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(errorTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cardinalityPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, interfacesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel1)
                            .add(returnTypeLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(returnTypeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                            .add(nameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(26, 26, 26)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(returnTypeLabel)
                    .add(returnTypeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(exceptionAndParameterPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(interfacesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cardinalityPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ejbqlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(errorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bothRadio;
    private javax.swing.JLabel cardinalityLabel;
    private javax.swing.JPanel cardinalityPanel;
    private javax.swing.JLabel ejbqlLabel;
    private javax.swing.JPanel ejbqlPanel;
    private javax.swing.JScrollPane ejbqlScrollPane;
    private javax.swing.JTextArea ejbqlTextArea;
    private javax.swing.JTextField errorTextField;
    private javax.swing.JTabbedPane exceptionAndParameterPane;
    private javax.swing.JPanel exceptionsContainerPanel;
    private javax.swing.ButtonGroup finderCardinalityButtonGroup;
    private javax.swing.ButtonGroup interfaceButtonGroup;
    private javax.swing.JLabel interfaceLabel;
    private javax.swing.JPanel interfacesPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton localRadio;
    private javax.swing.JRadioButton manyRadioButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JRadioButton oneRadioButton;
    private javax.swing.JPanel parametersContainerPanel;
    private javax.swing.JRadioButton remoteRadio;
    private javax.swing.JLabel returnTypeLabel;
    private javax.swing.JTextField returnTypeTextField;
    // End of variables declaration//GEN-END:variables
    
    public void setError(String message) {
        errorTextField.setText(message);
    }
    
    public String getMethodName() {
            return nameTextField.getText().trim();
    }
    
    public String getReturnType() {
        return returnTypeTextField.getText().trim();
    }

    public List<MethodModel.Variable> getParameters() {
        return parametersPanel.getParameters();
    }
    
    public List<String> getExceptions() {
        return exceptionsPanel.getExceptions();
    }
    
    public Set<Modifier> getModifiers() {
        // not changing?
        return methodModel.getModifiers();
    }
    
    public String getMethodBody() {
        // not changing?
        return methodModel.getBody();
    }
    
    public boolean supportsInterfacesChecking() {
        return hasInterfaces;
    }
    
    public boolean hasLocal() {
        return (localRadio.isEnabled() && localRadio.isSelected()) || hasBothInterfaces();
    }
    
    public boolean hasRemote() {
        return (remoteRadio.isEnabled() && remoteRadio.isSelected()) || hasBothInterfaces();
    }
    
    private boolean hasBothInterfaces() {
        return localRadio.isEnabled() && remoteRadio.isEnabled() && bothRadio.isSelected();
    }
    
    private void disableReturnType() {
        returnTypeLabel.setVisible(false);
        returnTypeTextField.setVisible(false);
    }
    
    /**
     * Listener on text fields. 
     * Fires change event for specified property of this JPanel, 
     * old and new value of event is null. 
     * After receiving event, client can get property value by 
     * calling {@link #getProperty(String)}
     */
    private class SimpleListener implements DocumentListener, ActionListener {
        
        private final String propertyName;
        
        public SimpleListener(String propertyName) {
            this.propertyName = propertyName;
        }
        
        public void insertUpdate(DocumentEvent documentEvent) { fire(); }
        
        public void removeUpdate(DocumentEvent documentEvent) { fire(); }
        
        public void changedUpdate(DocumentEvent documentEvent) {}
        
        public void actionPerformed(ActionEvent actionEvent) { fire(); }

        private void fire() {
            firePropertyChange(propertyName, null, null);
        }
        
    }
    
}
