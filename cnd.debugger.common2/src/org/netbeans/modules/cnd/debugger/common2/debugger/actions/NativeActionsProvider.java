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

package org.netbeans.modules.cnd.debugger.common2.debugger.actions;

import org.netbeans.spi.debugger.ActionsProviderSupport;
import org.netbeans.spi.debugger.ContextProvider;

import org.netbeans.modules.cnd.debugger.common2.debugger.NativeDebugger;
import org.netbeans.modules.cnd.debugger.common2.debugger.DebuggerManager;
import org.netbeans.modules.cnd.debugger.common2.debugger.State;
import org.netbeans.modules.cnd.debugger.common2.debugger.StateListener;

/**
 * Common code for all ActionProvider's in this package.
 */

abstract class NativeActionsProvider
    extends ActionsProviderSupport implements StateListener {

    private ContextProvider ctx;

    public NativeActionsProvider(ContextProvider ctx) {
	this.ctx = ctx;
	if (getDebugger() != null)
	    getDebugger().addStateListener(this);
    }

    // interface ActionsProvider
    // override ActionsProvider
    @Override
    public void postAction (final Object action,
			    final Runnable actionPerformedNotifier) {
	// Comes on the eventQ
	// The default implementation of postAction calls doAction on an RP.
	// We call it in the eventQ.
	try {
	    doAction(action);
	} finally {
	    actionPerformedNotifier.run();
	}
    }
    protected NativeDebugger getDebugger() {
	// TODO: how do we know this returns the debugger for 
	// the current session?
	if (ctx != null)
	    return ctx.lookupFirst(null, NativeDebugger.class);
	else {
	    NativeDebugger nd = DebuggerManager.get().currentDebugger();
	    return (nd != null) ? nd : null;
	}
    }

    protected DebuggerManager manager() {
	return DebuggerManager.get();
    }
}
