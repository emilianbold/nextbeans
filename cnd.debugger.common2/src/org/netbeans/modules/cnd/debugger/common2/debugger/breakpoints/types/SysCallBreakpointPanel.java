/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.types;

import java.awt.Component;
import java.util.Vector;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.BreakpointPanel;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.NativeBreakpoint;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.SystemInfo;
import org.netbeans.modules.cnd.debugger.common2.utils.IpeUtils;
import org.netbeans.modules.cnd.debugger.common2.values.SysCallEE;

class SysCallBreakpointPanel extends BreakpointPanel {

    private SysCallBreakpoint fb;
    private SystemInfo si_syscalls;
    
    @Override
    protected final void seed(NativeBreakpoint breakpoint) {
	seedCommonComponents(breakpoint);
	fb = (SysCallBreakpoint) breakpoint;

	// LATER
	// The following doesn't work as explained in SignalBreakpointType

	if (fb.getSysCall() == null)
	    sysCallCombo.setSelectedItem(si_syscalls.all());
	else
	    sysCallCombo.setSelectedItem(fb.getSysCall());

	if (fb.getEntryExit() == SysCallEE.ENTRY) 
	    entranceToggle.setSelected(true);
	else
	    exitToggle.setSelected(true);
    }

    /*
     * Constructors
     */
    public SysCallBreakpointPanel() {
	this (new SysCallBreakpoint(NativeBreakpoint.TOPLEVEL), false);
    }

    public SysCallBreakpointPanel(NativeBreakpoint b) {
	this ((SysCallBreakpoint)b, true);
    }

    /** Creates new form SysCallBreakpointPanel */
    public SysCallBreakpointPanel(SysCallBreakpoint breakpoint,
				  boolean customizing) {
	super(breakpoint, customizing);
	fb = breakpoint;

	initComponents();
	addCommonComponents(3);
	entranceToggle.setSelected(true);

	sysCallCombo.setEditable(true);
	Component c = sysCallCombo.getEditor().getEditorComponent();
	if (c instanceof javax.swing.text.JTextComponent) {
	    javax.swing.text.Document d =
		((javax.swing.text.JTextComponent)c).getDocument();
	    d.addDocumentListener(this);
	}

	// Fill in actual syscalls asynchronously like this:
	/** Items in the combo boxes */
	Vector<String> comboValues = new Vector<String>(250);
	si_syscalls = new SystemInfo.Syscalls(comboValues);
	si_syscalls.stuffInto(sysCallCombo);

	seed(breakpoint);

	// Arrange to revalidate on changes
	sysCallCombo.addItemListener(this);
    }
    
    @Override
    public void setDescriptionEnabled(boolean enabled) {
	// sysCallLabel.setEnabled(false);
	sysCallCombo.setEnabled(false);
	entranceToggle.setEnabled(false);
	exitToggle.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
	buttonGroup1 = new javax.swing.ButtonGroup();
	sysCallLabel = new javax.swing.JLabel();
	sysCallCombo = new javax.swing.JComboBox();
	entranceToggle = new javax.swing.JRadioButton();
	exitToggle = new javax.swing.JRadioButton();

	panel_settings.setLayout(new java.awt.GridBagLayout());
	java.awt.GridBagConstraints gridBagConstraints1;

	sysCallLabel.setText(Catalog.get("SystemCall"));// NOI18N
	sysCallLabel.setDisplayedMnemonic(
	    Catalog.getMnemonic("MNEM_SystemCall"));	// NOI18N
	sysCallLabel.setLabelFor(sysCallCombo);
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.ipadx = 5;
	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
	panel_settings.add(sysCallLabel, gridBagConstraints1);

	sysCallCombo.setEditable(true);
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.gridwidth = 3;
	gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
	gridBagConstraints1.weightx = 1.0;
	panel_settings.add(sysCallCombo, gridBagConstraints1);

	entranceToggle.setText(SysCallEE.ENTRY.toString());	// NOI18N
	entranceToggle.setMnemonic(
	    Catalog.getMnemonic("MNEM_OnEntrance"));		// NOI18N
	buttonGroup1.add(entranceToggle);
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.gridx = 0;
	gridBagConstraints1.gridwidth = 4;
	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
	panel_settings.add(entranceToggle, gridBagConstraints1);

	exitToggle.setText(SysCallEE.EXIT.toString());	// NOI18N
	exitToggle.setMnemonic(
	    Catalog.getMnemonic("MNEM_OnExit"));	// NOI18N
	buttonGroup1.add(exitToggle);
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.gridx = 0;
	gridBagConstraints1.gridwidth = 4;
	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
	panel_settings.add(exitToggle, gridBagConstraints1);

	// a11y
	sysCallCombo.getAccessibleContext().setAccessibleDescription(
	    Catalog.get("ACSD_SystemCall") // NOI18N
	);
	entranceToggle.getAccessibleContext().setAccessibleDescription(
	    entranceToggle.getText()
	);
	exitToggle.getAccessibleContext().setAccessibleDescription(
	    exitToggle.getText()
	);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel sysCallLabel;
    private javax.swing.JComboBox sysCallCombo;
    private javax.swing.JRadioButton entranceToggle;
    private javax.swing.JRadioButton exitToggle;
    // End of variables declaration//GEN-END:variables

    @Override
    protected void assignProperties() {
	String syscall = sysCallCombo.getSelectedItem().toString();
	if (syscall.equals(si_syscalls.all()))
	    fb.setSysCall(null);
	else
	    fb.setSysCall(syscall);

	if (exitToggle.isSelected())
	    fb.setEntryExit(SysCallEE.EXIT);
	else
	    fb.setEntryExit(SysCallEE.ENTRY);
    }
    
    @Override
    protected boolean propertiesAreValid() {
	Component c = sysCallCombo.getEditor().getEditorComponent();
	if (c instanceof javax.swing.text.JTextComponent) {
	    String text = ((javax.swing.text.JTextComponent)c).getText();
	    if (IpeUtils.isEmpty(text)) {
		return false;
	    } else {
		return true;
	    }
	} else if (IpeUtils.isEmpty(sysCallCombo.getSelectedItem().toString())) {
	    return false;
	}
	return true;
    }
}
