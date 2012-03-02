/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.core.ui.sampler;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.netbeans.modules.sampler.Sampler;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author Jaroslav Bachorik, Tomas Hurka
 */
@ActionID(id = "org.netbeans.modules.profiler.actions.SelfSamplerAction", category = "Profile")
@ActionRegistration(iconInMenu = true, displayName = "#SelfSamplerAction_ActionNameStart", iconBase = "org/netbeans/core/ui/sampler/selfSampler.png")
@ActionReferences({
    @ActionReference(path = "Toolbars/Memory", position = 2000),
    @ActionReference(path = "Shortcuts", name = "AS-Y")
})
public class SelfSamplerAction extends AbstractAction implements AWTEventListener {
    // -----
    // I18N String constants
    private static final String ACTION_NAME_START = NbBundle.getMessage(SelfSamplerAction.class, "SelfSamplerAction_ActionNameStart");
    private static final String ACTION_NAME_STOP = NbBundle.getMessage(SelfSamplerAction.class, "SelfSamplerAction_ActionNameStop");
//    private static final String ACTION_DESCR = NbBundle.getMessage(SelfSamplerAction.class, "SelfSamplerAction_ActionDescription");
    private static final String NOT_SUPPORTED = NbBundle.getMessage(SelfSamplerAction.class, "SelfSamplerAction_NotSupported");
    private final AtomicReference<Sampler> RUNNING = new AtomicReference<Sampler>();

    //~ Constructors -------------------------------------------------------------------------------------------------------------
    public SelfSamplerAction() {
        putValue(Action.NAME, ACTION_NAME_START);
        putValue(Action.SHORT_DESCRIPTION, ACTION_NAME_START);
        putValue ("iconBase", "org/netbeans/core/ui/sampler/selfSampler.png"); // NOI18N
        if (System.getProperty(SelfSamplerAction.class.getName() + ".sniff") != null) { //NOI18N
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        Sampler c = Sampler.createManualSampler("Self Sampler");  // NOI18N
        if (c != null) {
            if (RUNNING.compareAndSet(null, c)) {
                putValue(Action.NAME, ACTION_NAME_STOP);
                putValue(Action.SHORT_DESCRIPTION, ACTION_NAME_STOP);
                putValue ("iconBase", "org/netbeans/core/ui/sampler/selfSamplerRunning.png"); // NOI18N
                c.start();
            } else if ((c = RUNNING.getAndSet(null)) != null) {
                final Sampler controller = c;

                setEnabled(false);
                SwingWorker worker = new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        controller.stop();
                        return null;
                    }

                    @Override
                    protected void done() {
                        putValue(Action.NAME, ACTION_NAME_START);
                        putValue(Action.SHORT_DESCRIPTION, ACTION_NAME_START);
                        putValue ("iconBase", "org/netbeans/core/ui/sampler/selfSampler.png"); // NOI18N
                        SelfSamplerAction.this.setEnabled(true);
                    }
                };
                worker.execute();
            }
        } else {
            NotifyDescriptor d = new NotifyDescriptor.Message(NOT_SUPPORTED, NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        KeyEvent kevent = (KeyEvent) event;
        if (kevent.getID() == KeyEvent.KEY_RELEASED && kevent.getKeyCode() == KeyEvent.VK_ALT_GRAPH) { // AltGr
            actionPerformed(new ActionEvent(this, event.getID(), "shortcut")); //NOI18N
            kevent.consume();
        }
    }

    final boolean isProfileMe(Sampler c) {
        return c == RUNNING.get();
    }
}
