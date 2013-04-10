/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.deadlock.detector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.State;
import java.lang.management.LockInfo;
import static java.lang.management.ManagementFactory.*;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;


/**
 * Detects deadlocks using ThreadMXBean.
 * @see java.lang.management.ThreadMXBean
 * @author Mandy Chung, David Strupl
 */
class Detector implements Runnable {

    private static final Logger LOG = Logger.getLogger(Detector.class.getName());
    
    /**
     * This variable is used from different threads and is protected by 
     * synchronized(this).
     */
    private boolean running = true;
    /**
     * How long  to wait (in milliseconds) between the deadlock checks.
     */
    private static long PAUSE = 2000;
    /**
     * How long to wait (in milliseconds) before the deadlock detection starts.
     */
    private static long INITIAL_PAUSE = 10000;
    /**
     * Indents for printing the thread dumps.
     */
    private static final String INDENT = "    "; // NOI18N
    /**
     * The thread bean used for the deadlock detection.
     */
    private ThreadMXBean threadMXBean;
    
    Detector() {
        threadMXBean = getThreadMXBean();
        Integer pauseFromSysProp = Integer.getInteger("org.netbeans.modules.deadlock.detector.Detector.PAUSE"); // NOI18N
        if (pauseFromSysProp != null) {
            PAUSE = pauseFromSysProp.longValue();
        }
        Integer initialPauseFromSysProp = Integer.getInteger("org.netbeans.modules.deadlock.detector.Detector.INITIAL_PAUSE"); // NOI18N
        if (initialPauseFromSysProp != null) {
            INITIAL_PAUSE = initialPauseFromSysProp.longValue();
        }
    }
    
    /**
     * Starts a new thread that periodically checks for deadlocks.
     */
    void start() {
        if (threadMXBean == null) {
            return;
        }
        Thread t = new Thread(this, "Deadlock Detector"); // NOI18N
        t.start();
    }
    
    /**
     * Stops the detector thread.
     */
    synchronized void stop() {
        running = false;
    }
    
    /**
     * Accessing the variable running under the synchronized (this).
     * @return whether we are still running the detector thread
     */
    private synchronized boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(INITIAL_PAUSE);
            while (isRunning()) {
                long time = System.currentTimeMillis();
                detectDeadlock();
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Deadlock detection took: {0} ms.", System.currentTimeMillis() - time); // NOI18N
                }
                Thread.sleep(PAUSE);
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    /**
     * The main method called periodically by the deadlock detector thread.
     */
    private void detectDeadlock() {
        if (threadMXBean == null) {
            return;
        }
        long[] tids;
        tids = threadMXBean.findDeadlockedThreads();
        if (tids == null) {
            return;
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Deadlock detected");
        }
        PrintStream out = null;
        File file = null;
        try {
            file = File.createTempFile("deadlock", ".txt"); // NOI18N
            out = new PrintStream(new FileOutputStream(file));
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Temporrary file created: {0}" , file);
            }            
        } catch (IOException iOException) {
            out = System.out;            
        }        
        out.println("Deadlocked threads :"); // NOI18N
        ThreadInfo[] deadlocked = threadMXBean.getThreadInfo(tids, true, true);
        for (ThreadInfo ti : deadlocked) {
            printThreadInfo(ti, out);
            printMonitorInfo(ti, out);
            printLockInfo(ti.getLockedSynchronizers(), out);
            out.println();
        }
        out.println("All threads :"); // NOI18N
        tids = threadMXBean.getAllThreadIds();
        ThreadInfo[] infos = threadMXBean.getThreadInfo(tids, true, true);
        for (ThreadInfo ti : infos) {
            printThreadInfo(ti, out);
            printMonitorInfo(ti, out);
            printLockInfo(ti.getLockedSynchronizers(), out);
            out.println();
        }
        if (out != System.out) {
            out.close();
        }
        stop();
        
        reportStackTrace(deadlocked, file);
    }

    private void printThreadInfo(ThreadInfo ti, PrintStream out) {
       printThread(ti, out);

       // print stack trace with locks
       StackTraceElement[] stacktrace = ti.getStackTrace();
       MonitorInfo[] monitors = ti.getLockedMonitors();
       for (int i = 0; i < stacktrace.length; i++) {
           StackTraceElement ste = stacktrace[i];
           out.println(INDENT + "at " + ste.toString()); // NOI18N
           for (MonitorInfo mi : monitors) {
               if (mi.getLockedStackDepth() == i) {
                   out.println(INDENT + "  - locked " + mi); // NOI18N
               }
           }
       }
       out.println();
    }

    private void printThread(ThreadInfo ti, PrintStream out) {
       StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"" + // NOI18N
                                            " Id=" + ti.getThreadId() +       // NOI18N
                                            " in " + ti.getThreadState());    // NOI18N
       if (ti.getLockName() != null) {
           sb.append(" on lock=").append(ti.getLockName()); // NOI18N
       }
       if (ti.isSuspended()) {
           sb.append(" (suspended)"); // NOI18N
       }
       if (ti.isInNative()) {
           sb.append(" (running in native)"); // NOI18N
       }
       out.println(sb.toString());
       if (ti.getLockOwnerName() != null) {
            out.println(INDENT + " owned by " + ti.getLockOwnerName() + // NOI18N
                               " Id=" + ti.getLockOwnerId());           // NOI18N
       }
    }

    private void printMonitorInfo(ThreadInfo ti, PrintStream out) {
       MonitorInfo[] monitors = ti.getLockedMonitors();
       out.println(INDENT + "Locked monitors: count = " + monitors.length); // NOI18N
       for (MonitorInfo mi : monitors) {
           out.println(INDENT + "  - " + mi + " locked at "); // NOI18N
           out.println(INDENT + "      " + mi.getLockedStackDepth() + // NOI18N
                              " " + mi.getLockedStackFrame());       // NOI18N
       }
    }

    private void printLockInfo(LockInfo[] locks, PrintStream out) {
       out.println(INDENT + "Locked synchronizers: count = " + locks.length); // NOI18N
       for (LockInfo li : locks) {
           out.println(INDENT + "  - " + li); // NOI18N
       }
       out.println();
    }    

    /**
     * Use exception reporter to report the stack trace of the deadlocked thrads.
     * @param deadlocked 
     */
    private void reportStackTrace(ThreadInfo[] deadlocked, File report) {
        for (ThreadInfo toBeReported : deadlocked) {
            DeadlockDetectedException dde = new DeadlockDetectedException();
            dde.setStackTrace(toBeReported.getStackTrace());
            LOG.log(Level.SEVERE, report.getAbsolutePath(), dde);
        }
    }
    
    private static class DeadlockDetectedException extends RuntimeException {
    }
}

// o.n.core
