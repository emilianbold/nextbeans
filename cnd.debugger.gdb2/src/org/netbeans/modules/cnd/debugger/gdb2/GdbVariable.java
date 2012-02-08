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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.debugger.gdb2;

import javax.swing.SwingUtilities;

import javax.swing.Action;

import org.netbeans.modules.cnd.debugger.common2.debugger.NativeDebugger;
import org.netbeans.modules.cnd.debugger.common2.debugger.Variable;

import org.netbeans.modules.cnd.debugger.common2.debugger.ModelChangeDelegator;

import org.netbeans.modules.cnd.debugger.common2.debugger.VariableModel;
import org.netbeans.modules.cnd.debugger.common2.debugger.WatchModel;
import org.openide.util.Exceptions;

class GdbVariable extends Variable {
    protected final GdbDebuggerImpl debugger;
    private final boolean isWatch;

    private String mi_name;
    //private String value; // should use the one in parent (VARIABLE) class
    private String mi_format = "natural"; // NOI18N
    private int numchild;
    private boolean editable;
    private boolean changed;
    private boolean inScope = true;
    private boolean dynamic = false;

    public GdbVariable(GdbDebuggerImpl debugger, ModelChangeDelegator updater,
		       Variable parent,
		       String name, String type, String value,
		       boolean watch) {
	super(updater, parent, name, type, value);
	this.debugger = debugger;
	isWatch = watch;
	//this.value = value;
    }

    @Override
    public NativeDebugger getDebugger() {
	return this.debugger;
    }

    protected void setChanged(boolean changed) {
	this.changed = changed;
    }

    protected boolean isChanged() {
	return changed;
    }

    public boolean isWatch() {
	return isWatch;
    }

    public void setInScope(boolean inScope) {
	this.inScope = inScope;
    }

    public boolean isInScope() {
	return inScope;
    }

    // override Variable
    @Override
    public String getAsText() {
	String prefix = org.netbeans.modules.cnd.debugger.common2.debugger.Log.Watch.varprefix? mi_name + ": ": ""; // NOI18N
	if (inScope) {
            String res = super.getAsText();
            if (res != null) {
                res = res.replace("\\\"", "\""); //NOI18N
            }
	    return prefix + res;
        } else {
	    return prefix + "<OUT_OF_SCOPE>"; // NOI18N
        }
    }

    protected void setEditable(String attr) {
	editable = attr.equals("editable"); // NOI18N
	if (editable && (numchild > 0)) {
		setPtr(true);
	}
    }

    @Override
    public boolean isEditable() {
	return editable;
    }

    protected void setMIName(String mi_name) {
	this.mi_name = mi_name;
    }

    public void setValue(String v) {
        //value = v;
	setAsText(v);
    }

    public String getMIName() {
	return mi_name;
    }

    void setFormat(String format) {
	mi_format = format;
    }

    protected void setNumChild(String child) {
	this.numchild = Integer.parseInt(child);
	if (this.numchild > 0) {
	    setLeaf(false);
	} else {
	    setLeaf(true);
	}
    }

    // override Variable
    @Override
    public int getNumChild() {
	return this.numchild;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    // override Variable
    @Override
    public Variable[] getChildren() {
        if (isLeaf())
            return new Variable[0];

        if (children != null) {
            return children;   // cached children
        }

        if (waitingForDebugger)
            return new Variable[0];
                
        waitingForDebugger = true;           // reset in setChildren()
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    setChildren();
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        };
        SwingUtilities.invokeLater(r);
        
	// return a dummy place-holder for now.
        return new Variable[0];
    }

    public void setChildren() {
        debugger.getMIChildren(this, getMIName(), 0);
    }

    // interface Variable
    @Override
    public void noteExpanded(boolean isWatch) {
        if (isExpanded())
            return;
        setExpanded(true);
    }

    // interface Variable
    @Override
    public void noteCollapsed(boolean isWatch) {
        setExpanded(false);
    }


    // for assign new value from view nodes
    @Override
    public void setVariableValue(String assigned_v) {
        // no need to update to the same value
        if (!assigned_v.equals(value)) {
            // always assign char* in non-mi form, IZ 193500
            boolean miName = !"char *".equals(type); //NOI18N
            debugger.assignVar(this, assigned_v, miName);
        }
    }

    @Override
    public void removeAllDescendantFromOpenList(boolean isLocal) {
    }

    // interface Variable
    @Override
    public String getDebugInfo() {
	return "";
    }

    // implement Variable
    @Override
    public boolean getDelta() {
        return false;
    }

    // interface Variable
    @Override
    public Action[] getActions(boolean isWatch) {
	if (isWatch) {
	    return new Action[] {
                WatchModel.NEW_WATCH_ACTION,
		null,
		new WatchModel.DeleteAllAction(),
		null,
		// LATER VariableModel.Action_INHERITED_MEMBERS,
		// LATER VariableModel.Action_DYNAMIC_TYPE,
		VariableModel.getOutputFormatAction(this),
		// LATER SystemAction.get(MaxObjectAction.class),
		null
	    };
	} else {
	    // local
            return new Action[] {
		// LATER VariableModel.Action_INHERITED_MEMBERS,
                // LATER VariableModel.Action_DYNAMIC_TYPE,
                VariableModel.getOutputFormatAction(this),
                null,
            };

	}
    }

    // interface Variable
    @Override
    public boolean isArrayBrowsable() {
	// No array browser for gdb
	return false;
    }

    // interface Variable
    @Override
    public void postFormat(String format) {
	debugger.postVarFormat(this, format);
    }

    // interface Variable
    @Override
    public String getFormat() {
	return mi_format;
    }
}
