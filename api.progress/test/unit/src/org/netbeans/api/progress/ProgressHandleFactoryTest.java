/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.api.progress;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import junit.framework.TestCase;
import org.netbeans.progress.module.Controller;
import org.netbeans.progress.spi.InternalHandle;
import org.netbeans.progress.spi.ProgressEvent;
import org.netbeans.progress.spi.ProgressUIWorker;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;

/**
 *
 * @author Milos Kleint (mkleint@netbeans.org)
 */
public class ProgressHandleFactoryTest extends TestCase {

    public ProgressHandleFactoryTest(String testName) {
        super(testName);
    }

    /**
     * Test of createHandle method, of class org.netbeans.progress.api.ProgressHandleFactory.
     */
    public void testCreateHandle() {
        
        ProgressHandle handle = ProgressHandleFactory.createHandle("task 1");
        InternalHandle internal = handle.getInternalHandle();
        assertEquals("task 1", internal.getDisplayName());
        assertFalse(internal.isAllowCancel());
        assertFalse(internal.isCustomPlaced());
        assertEquals(InternalHandle.STATE_INITIALIZED, internal.getState());
        
        handle = ProgressHandleFactory.createHandle("task 2", new TestCancel());
        internal = handle.getInternalHandle();
        assertEquals("task 2", internal.getDisplayName());
        assertTrue(internal.isAllowCancel());
        assertFalse(internal.isCustomPlaced());
        assertEquals(InternalHandle.STATE_INITIALIZED, internal.getState());
        
    }

    
    public void testCustomComponentIsInitialized() {
        Controller.defaultInstance = new TestController();
        
        ProgressHandle handle = ProgressHandleFactory.createHandle("task 1");
        JComponent component = ProgressHandleFactory.createProgressComponent(handle);
        
        handle.start(15);
        handle.progress(2);
        waitForTimerFinish();
        
        assertEquals(15, ((JProgressBar) component).getMaximum());
        assertEquals(2, ((JProgressBar) component).getValue());
        
        handle = ProgressHandleFactory.createHandle("task 2");
        component = ProgressHandleFactory.createProgressComponent(handle);
        
        handle.start(20);
        waitForTimerFinish();
        
        assertEquals(20, ((JProgressBar) component).getMaximum());
        assertEquals(0, ((JProgressBar) component).getValue());
        
    }
    
    private static class AwtBlocker implements Runnable {

        public AwtBlocker(int blockingTime) {
            this.blockingTime = blockingTime;
        }

        public void run() {
            UIDefaults uidef = UIManager.getDefaults();
            synchronized (uidef) {
                blocking = true;
                sleep();
            }
        }
        synchronized void sleep() {
            try {
                wait(blockingTime);
            } catch (InterruptedException ex) {
            }
        }
        synchronized void wakeup() {
            notify();
        }
        volatile public boolean blocking = false;
        private int blockingTime;
    }

    /**
     * Tests if ProgressUIWorkerProvider is created inside awt thread (if not deadlock is possible)
     */
    public void testProgressCanBeCreatedOutOfSyncAwt() {
        Controller.defaultInstance = null;
        final int blockingTime = 10000;
        AwtBlocker blocker = new AwtBlocker(blockingTime);
        long start = System.currentTimeMillis();
        SwingUtilities.invokeLater(blocker);
        while (!blocker.blocking) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }

        ProgressHandle pHandle = ProgressHandleFactory.createHandle("Peforming operation...");
        pHandle.start();
        long elapsed = System.currentTimeMillis() - start;
        assertTrue("Possible deadlock detected, ProgressUIWorkerProvider is creating UI outside AWT thread.", elapsed < blockingTime);
        pHandle.finish();
        blocker.wakeup();
    }

     private static class TestCancel implements Cancellable {
         public boolean cancel() {
             return true;
         }
         
   }
   
     
    private class TestController extends Controller {
        public TestController() {
            super(new ProgressUIWorker() {
                public void processProgressEvent(ProgressEvent event) { }
                public void processSelectedProgressEvent(ProgressEvent event) { }
            });
        }
        
        public Timer getTestTimer() {
            return timer;
        }
    }
    
    private void waitForTimerFinish() {
        TestController tc = (TestController)Controller.defaultInstance;
        int count = 0;
        do {
            if (count > 10) {
                fail("Takes too much time");
            }
            try {
                count = count + 1;
                Thread.sleep(300);
            } catch (InterruptedException exc) {
                System.out.println("interrupted");
            }        
        } while (tc.getTestTimer().isRunning());

    }
     
    
}
