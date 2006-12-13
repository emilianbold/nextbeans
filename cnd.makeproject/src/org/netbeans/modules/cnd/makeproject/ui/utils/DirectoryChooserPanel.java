/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.cnd.makeproject.ui.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.util.Vector;
import javax.swing.JFileChooser;
import org.netbeans.modules.cnd.makeproject.api.configurations.BooleanConfiguration;
import org.netbeans.modules.cnd.makeproject.api.remote.FilePathAdaptor;
import org.netbeans.modules.cnd.api.utils.FileChooser;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.HelpCtx;

public class DirectoryChooserPanel extends javax.swing.JPanel implements HelpCtx.Provider, PropertyChangeListener { 
    private MyListEditorPanel myListEditorPanel;
    private String baseDir;
    private boolean addPathPanel;
    private BooleanConfiguration inheritValues;
    private PropertyEditorSupport editor;
    
    public DirectoryChooserPanel(String baseDir, Object[] data, boolean addPathPanel, BooleanConfiguration inheritValues, String inheritText, PropertyEditorSupport editor, PropertyEnv env) {
	this.baseDir = baseDir;
	this.addPathPanel = addPathPanel;
	this.inheritValues = inheritValues;
        this.editor = editor;
        initComponents();
	myListEditorPanel = new MyListEditorPanel(data);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        listPanel.add(myListEditorPanel, gridBagConstraints);
	if (inheritValues != null) {
	    inheritTextArea.setBackground(inheritPanel.getBackground());
	    inheritTextArea.setText(inheritText);
	    setPreferredSize(new java.awt.Dimension(450, 330));
	    inheritCheckBox.setSelected(inheritValues.getValue());
	}
	else {
	    remove(inheritPanel);
	    //setPreferredSize(new java.awt.Dimension(450, 350));
	    setPreferredSize(new java.awt.Dimension(450, 220));
	}
        
        env.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
        env.addPropertyChangeListener(this);
    }

    public void setInstructionsText(String txt) {
	//instructionsTextArea.setText(txt);
    }

    public void setListData(Object[] data){
	myListEditorPanel.setListData(data);
    }

    public Vector getListData() {
	return myListEditorPanel.getListData();
    }

    private Object getPropertyValue() throws IllegalStateException {
	return getListData();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName()) && evt.getNewValue() == PropertyEnv.STATE_VALID) {
            editor.setValue(getPropertyValue());
        }
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx("RuntimeSearchDirectories"); // NOI18N
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        listPanel = new javax.swing.JPanel();
        inheritPanel = new javax.swing.JPanel();
        scrollPanel = new javax.swing.JScrollPane();
        panel = new javax.swing.JPanel();
        inheritLabel = new javax.swing.JLabel();
        inheritTextArea = new javax.swing.JTextArea();
        inheritCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(323, 223));
        listPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(listPanel, gridBagConstraints);

        inheritPanel.setLayout(new java.awt.GridBagLayout());

        scrollPanel.setBorder(null);
        panel.setLayout(new java.awt.GridBagLayout());

        inheritLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/ui/utils/Bundle").getString("INHERITED_VALUES_LBL"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        panel.add(inheritLabel, gridBagConstraints);

        inheritTextArea.setEditable(false);
        inheritTextArea.setLineWrap(true);
        inheritTextArea.setWrapStyleWord(true);
        inheritTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panel.add(inheritTextArea, gridBagConstraints);

        inheritCheckBox.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/ui/utils/Bundle").getString("INHERIT_CHECKBOX_MN").charAt(0));
        inheritCheckBox.setSelected(true);
        inheritCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/ui/utils/Bundle").getString("INHERIT_CHECKBOX_LBL"));
        inheritCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inheritCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel.add(inheritCheckBox, gridBagConstraints);

        scrollPanel.setViewportView(panel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        inheritPanel.add(scrollPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(inheritPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void inheritCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inheritCheckBoxActionPerformed
	inheritValues.setValue(inheritCheckBox.isSelected());
    }//GEN-LAST:event_inheritCheckBoxActionPerformed
    
    private class MyListEditorPanel extends ListEditorPanel {
	public MyListEditorPanel(Object[] objects) {
	    super(objects);
	    getDefaultButton().setVisible(false);
	}

	public Object addAction() {
	    String seed = null;
	    if (FileChooser.getCurrectChooserFile() != null)
		seed = FileChooser.getCurrectChooserFile().getPath();
	    if (seed == null)
		seed = baseDir;
	    FileChooser fileChooser = new FileChooser("Select Directory", "Select", JFileChooser.DIRECTORIES_ONLY, null, seed, true);
	    PathPanel pathPanel = null;
	    if (addPathPanel)
		pathPanel = new PathPanel();
	    fileChooser.setAccessory(pathPanel);
	    int ret = fileChooser.showOpenDialog(this);
	    if (ret == JFileChooser.CANCEL_OPTION)
		return null;
	    String itemPath = fileChooser.getSelectedFile().getPath();
	    itemPath = FilePathAdaptor.mapToRemote(itemPath);
	    itemPath = FilePathAdaptor.naturalize(itemPath);
	    String bd = baseDir;
	    bd = FilePathAdaptor.mapToRemote(bd);
	    bd = FilePathAdaptor.naturalize(bd);
	    if (pathPanel != null && pathPanel.getMode() == PathPanel.REL_OR_ABS)
		itemPath = IpeUtils.toAbsoluteOrRelativePath(bd, itemPath);
	    else if (pathPanel != null && pathPanel.getMode() == PathPanel.REL)
		itemPath = IpeUtils.toRelativePath(bd, itemPath);
	    else
		itemPath = itemPath;
	    itemPath = FilePathAdaptor.normalize(itemPath);
	    return itemPath;
	}

	public String getListLabelText() {
	    return "Directories:";
	}
	public char getListLabelMnemonic() {
	    return 'I';
	}
    
	public String getAddButtonText() {
	    return "Add Directory";
	}
	public char getAddButtonMnemonics() {
	    return 'A';
	}
    
	public String getRenameButtonText() {
	    return "Edit";
	}
	public char getRenameButtonMnemonics() {
	    return 'E';
	}

	public Object copyAction(Object o) {
	    return new String((String)o);
	}

	public void editAction(Object o) {
	    String s = (String)o;

	    NotifyDescriptor.InputLine notifyDescriptor = new NotifyDescriptor.InputLine("", "Edit");
	    notifyDescriptor.setInputText(s);
	    DialogDisplayer.getDefault().notify(notifyDescriptor);
	    if (notifyDescriptor.getValue() != NotifyDescriptor.OK_OPTION)
		return;
	    String newS = notifyDescriptor.getInputText();
	    Vector vector = getListData();
	    Object[] arr = getListData().toArray();
	    for (int i = 0; i < arr.length; i++) {
		if (arr[i] == o) {
		    vector.remove(i);
		    vector.add(i, newS);
		    break;
		}
	    }
	}
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox inheritCheckBox;
    private javax.swing.JLabel inheritLabel;
    private javax.swing.JPanel inheritPanel;
    private javax.swing.JTextArea inheritTextArea;
    private javax.swing.JPanel listPanel;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane scrollPanel;
    // End of variables declaration//GEN-END:variables
    
}
