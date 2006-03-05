/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.util;

import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.junit.NbTestCase;
import org.openide.ErrorManager;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

public class WeakListenersTest extends NbTestCase {
    static {
        System.setProperty("org.openide.util.Lookup", "org.openide.util.WeakListenersTest$Lkp");
    }
    private static Thread activeQueueThread;

    private ErrorManager log;
    
    public WeakListenersTest(String testName) {
        super(testName);
    }

    protected int timeOut() {
        return 5000;
    }
    
    protected void setUp () throws Exception {
        log = ErrorManager.getDefault().getInstance("TEST-" + getName());
        
        if (activeQueueThread == null) {
            class WR extends WeakReference implements Runnable {
                public WR (Object o) {
                    super (o, Utilities.activeReferenceQueue ());
                }
                public synchronized void run () {
                    activeQueueThread = Thread.currentThread();
                    notifyAll ();
                }
            }
            
            Object obj = new Object ();
            WR wr = new WR (obj);
            synchronized (wr) {
                obj = null;
                assertGC ("Has to be cleared", wr);
                // and has to execute run method
                while (activeQueueThread != null) {
                    wr.wait ();
                }
            }
        }
    }
    
    protected void runTest() throws Throwable {
        assertNotNull ("ErrManager has to be in lookup", org.openide.util.Lookup.getDefault ().lookup (ErrManager.class));
        ErrManager.messages.setLength(0);
        
        try {
            super.runTest();
        } catch (Throwable ex) {
            throw new junit.framework.AssertionFailedError (
                ex.getMessage() + "\n" + ErrManager.messages.toString()
            ).initCause(ex);
        }
    }
    
    
    public void testOneCanCallHashCodeOrOnWeakListener () {
        Listener l = new Listener ();
        Object weak = WeakListeners.create (PropertyChangeListener.class, l, null);
        weak.hashCode ();
    }
    
    /** Useful for next test */
    interface X extends java.util.EventListener {
        public void invoke ();
    }
    /** Useful for next test */
    class XImpl implements X {
        public int cnt;
        public void invoke () {
            cnt++;
        }
    }
    public void testCallingMethodsWithNoArgumentWorks() {
        XImpl l = new XImpl ();
        log.log("XImpl created: " + l);
        X weak = (X)WeakListeners.create (X.class, l, null);
        log.log("weak created: " + weak);
        weak.invoke ();
        log.log("invoked");
        assertEquals ("One invocation", 1, l.cnt);
    }

    public void testReleaseOfListenerWithNullSource () throws Exception {
        doTestReleaseOfListener (false);
    }
    
    public void testReleaseOfListenerWithSource () throws Exception {
        doTestReleaseOfListener (true);
    }
    
    private void doTestReleaseOfListener (final boolean source) throws Exception {   
        Listener l = new Listener ();
        
        class MyButton extends javax.swing.JButton {
            private Thread removedBy;
            private int cnt;
            
            public synchronized void removePropertyChangeListener (PropertyChangeListener l) {
                // notify prior
                log.log("removePropertyChangeListener: " + source + " cnt: " + cnt);
                if (source && cnt == 0) {
                    notifyAll ();
                    try {
                        // wait for 1
                        log.log("wait for 1");
                        wait ();
                        log.log("wait for 1 over");
                    } catch (InterruptedException ex) {
                        fail ("Not happen");
                    }
                }
                log.log("Super removePropertyChangeListener");
                super.removePropertyChangeListener (l);
                log.log("Super over removePropertyChangeListener");
                removedBy = Thread.currentThread();
                cnt++;
                notifyAll ();
            }
            
            public synchronized void waitListener () throws Exception {
                int cnt = 0;
                while (removedBy == null) {
                    log.log("waitListener, wait 500");
                    wait (500);
                    log.log("waitListener 500 Over");
                    if (cnt++ == 5) {
                        fail ("Time out: removePropertyChangeListener was not called at all");
                    } else {
                        log.log("Forced gc");
                        System.gc ();
                        System.runFinalization();
                        log.log("after force runFinalization");
                    }
                }
            }
        }
        
        MyButton button = new MyButton ();
        log.log("Button is here");
        java.beans.PropertyChangeListener weakL = WeakListeners.propertyChange (l, source ? button : null);
        log.log("WeakListeners created: " + weakL);
        button.addPropertyChangeListener(weakL);
        log.log("WeakListeners attached");
        assertTrue ("Weak listener is there", Arrays.asList (button.getPropertyChangeListeners()).indexOf (weakL) >= 0);
        
        button.setText("Ahoj");
        log.log("setText changed to ahoj");
        assertEquals ("Listener called once", 1, l.cnt);
        
        WeakReference ref = new WeakReference (l);
        log.log("Clearing listener");
        l = null;
        

        synchronized (button) {
            log.log("Before assertGC");
            assertGC ("Can disappear", ref);
            log.log("assertGC ok");
            
            if (source) {
                log.log("before wait");
                button.wait ();
                log.log("after wait");
                // this should not remove the listener twice
                button.setText ("Hoj");
                log.log("after setText - > hoj");
                // go on (wait 1)
                button.notify ();
                log.log("before wait listener");
                
                button.waitListener ();
                log.log("after waitListener");
            } else {
                // trigger the even firing so weak listener knows from
                // where to unregister
                log.log("before setText -> Hoj");
                button.setText ("Hoj");
                log.log("after setText -> Hoj");
            }
            
            log.log("before 2 waitListener");
            button.waitListener ();
            log.log("after 2 waitListener");
            Thread.sleep (500);
            log.log("Thread.sleep over");
        }

        assertEquals ("Weak listener has been removed", -1, Arrays.asList (button.getPropertyChangeListeners()).indexOf (weakL));
        assertEquals ("Button released from a thread", activeQueueThread, button.removedBy);
        assertEquals ("Unregister called just once", 1, button.cnt);
        
        // and because it is not here, it can be GCed
        WeakReference weakRef = new WeakReference (weakL);
        weakL = null;
        log.log("Doing assertGC at the end");
        assertGC ("Weak listener can go away as well", weakRef);
    }
    
    
    public void testSourceCanBeGarbageCollected () {
        javax.swing.JButton b = new javax.swing.JButton ();
        Listener l = new Listener ();
        
        b.addPropertyChangeListener (WeakListeners.propertyChange (l, b));
        
        WeakReference ref = new WeakReference (b);
        b = null;
        
        assertGC ("Source can be GC", ref);
    }
    
    public void testNamingListenerBehaviour () throws Exception {
        Listener l = new Listener ();
        ImplEventContext c = new ImplEventContext ();
        javax.naming.event.NamingListener weakL = (javax.naming.event.NamingListener)WeakListeners.create (
            javax.naming.event.ObjectChangeListener.class,
            javax.naming.event.NamingListener.class,
            l,
            c
        );
        
        c.addNamingListener("", javax.naming.event.EventContext.OBJECT_SCOPE, weakL);
        assertEquals ("Weak listener is there", weakL, c.listener);
        
        WeakReference ref = new WeakReference (l);
        l = null;

        synchronized (c) {
            assertGC ("Can disappear", ref);
            c.waitListener ();
        }
        assertNull ("Listener removed", c.listener);
    }
    
    public void testExceptionIllegalState () {
        Listener l = new Listener ();
        try {
            WeakListeners.create (PropertyChangeListener.class, javax.naming.event.NamingListener.class, l, null);
            fail ("This shall not be allowed as NamingListener is not superclass of PropertyChangeListener");
        } catch (IllegalArgumentException ex) {
            // ok
        }
        
        try {
            WeakListeners.create (Object.class, l, null);
            fail ("Not interface, it should fail");
        } catch (IllegalArgumentException ex) {
            // ok
        }
        
        try {
            WeakListeners.create (Object.class, Object.class, l, null);
            fail ("Not interface, it should fail");
        } catch (IllegalArgumentException ex) {
            // ok
        }
        
        try {
            WeakListeners.create (PropertyChangeListener.class, Object.class, l, null);
            fail ("Not interface, it should fail");
        } catch (IllegalArgumentException ex) {
            // ok
        }
    }
    
    public void testHowBigIsWeakListener () throws Exception {
        Listener l = new Listener ();
        javax.swing.JButton button = new javax.swing.JButton ();
        ImplEventContext c = new ImplEventContext ();
        
        Object[] ignore = new Object[] {
            l, 
            button,
            c,
            Utilities.activeReferenceQueue()
        };
        
        
        PropertyChangeListener pcl = WeakListeners.propertyChange(l, button);
        assertSize ("Not too big (plus 32 from ReferenceQueue)", java.util.Collections.singleton (pcl), 112, ignore);
        
        Object ocl = WeakListeners.create (javax.naming.event.ObjectChangeListener.class, javax.naming.event.NamingListener.class, l, c);
        assertSize ("A bit bigger (plus 32 from ReferenceQueue)", java.util.Collections.singleton (ocl), 128, ignore);
        
        Object nl = WeakListeners.create (javax.naming.event.NamingListener.class, l, c);
        assertSize ("The same (plus 32 from ReferenceQueue)", java.util.Collections.singleton (nl), 128, ignore);
        
    }

    public void testPrivateRemoveMethod() throws Exception {
        PropChBean bean = new PropChBean();
        Listener listener = new Listener();
        PCL weakL = (PCL) WeakListeners.create(PCL.class, listener, bean);
        WeakReference ref = new WeakReference(listener);
        
        bean.addPCL(weakL);
        
        bean.listeners.firePropertyChange (null, null, null);
        assertEquals ("One call to the listener", 1, listener.cnt);
        listener.cnt = 0;
        
        listener = null;
        assertGC("Listener wasn't GCed", ref);
        
        ref = new WeakReference(weakL);
        weakL = null;
        assertGC("WeakListener wasn't GCed", ref);
        
        // this shall enforce the removal of the listener
        bean.listeners.firePropertyChange (null, null, null);
        
        assertEquals ("No listeners", 0, bean.listeners.getPropertyChangeListeners ().length);
    }

    public void testStaticRemoveMethod() throws Exception {
        ChangeListener l = new ChangeListener() {public void stateChanged(ChangeEvent e) {}};
        Singleton.addChangeListener(WeakListeners.change(l, Singleton.class));
        assertEquals(1, Singleton.listeners.size());
        Reference r = new WeakReference(l);
        l = null;
        assertGC("could collect listener", r);
        assertEquals("called remove method", 0, Singleton.listeners.size());
    }
    public static class Singleton {
        public static List listeners = new ArrayList();
        public static void addChangeListener(ChangeListener l) {
            listeners.add(l);
        }
        public static void removeChangeListener(ChangeListener l) {
            listeners.remove(l);
        }
    }
    
    private static final class Listener 
    implements java.beans.PropertyChangeListener, javax.naming.event.ObjectChangeListener {
        public int cnt;
        
        public void propertyChange (java.beans.PropertyChangeEvent ev) {
            cnt++;
        }
        
        public void namingExceptionThrown(javax.naming.event.NamingExceptionEvent evt) {
            cnt++;
        }
        
        public void objectChanged(javax.naming.event.NamingEvent evt) {
            cnt++;
        }
    } // end of Listener
    
    private static final class ImplEventContext extends javax.naming.InitialContext 
    implements javax.naming.event.EventContext {
        public javax.naming.event.NamingListener listener;
        
        public ImplEventContext () throws Exception {
        }
        
        public void addNamingListener(javax.naming.Name target, int scope, javax.naming.event.NamingListener l) throws javax.naming.NamingException {
            assertNull (listener);
            listener = l;
        }
        
        public void addNamingListener(String target, int scope, javax.naming.event.NamingListener l) throws javax.naming.NamingException {
            assertNull (listener);
            listener = l;
        }
        
        public synchronized void removeNamingListener(javax.naming.event.NamingListener l) throws javax.naming.NamingException {
            assertEquals ("Removing the same listener", listener, l);
            listener = null;
            notifyAll ();
        }
        
        public boolean targetMustExist() throws javax.naming.NamingException {
            return false;
        }
        
        public synchronized void waitListener () throws Exception {
            int cnt = 0;
            while (listener != null) {
                wait (500);
                if (cnt++ == 5) {
                    fail ("Time out: removeNamingListener was not called at all");
                } else {
                    System.gc ();
                    System.runFinalization();
                }
            }
        }
        
    }
    
    private static class PropChBean {
        private java.beans.PropertyChangeSupport listeners = new java.beans.PropertyChangeSupport (this);
        private void addPCL(PCL l) { listeners.addPropertyChangeListener (l); }
        private void removePCL(PCL l) { listeners.removePropertyChangeListener (l); }
    } // End of PropChBean class

    // just a marker, its name will be used to construct the name of add/remove methods, e.g. addPCL, removePCL
    private static interface PCL extends PropertyChangeListener {
    } // End of PrivatePropL class
    
    
    
    
    
    
    
    
    
    //
    // Our fake lookup
    //
    public static final class Lkp extends org.openide.util.lookup.AbstractLookup {
        private ErrManager err = new ErrManager ();
        private org.openide.util.lookup.InstanceContent ic;
        
        public Lkp () {
            this (new org.openide.util.lookup.InstanceContent ());
        }
        
        private Lkp (org.openide.util.lookup.InstanceContent ic) {
            super (ic);
            ic.add (err);
            this.ic = ic;
        }
        
        public static void turn (boolean on) {
            Lkp lkp = (Lkp)org.openide.util.Lookup.getDefault ();
            if (on) {
                lkp.ic.add (lkp.err);
            } else {
                lkp.ic.remove (lkp.err);
            }
        }
    }
    
    //
    // Manager to delegate to
    //
    public static final class ErrManager extends org.openide.ErrorManager {
        public static final StringBuffer messages = new StringBuffer ();
        
        private String prefix;
        
        public ErrManager () {
            this (null);
        }
        public ErrManager (String prefix) {
            this.prefix = prefix;
        }
        
        public static ErrManager get () {
            return (ErrManager)org.openide.util.Lookup.getDefault ().lookup (ErrManager.class);
        }
        
        public Throwable annotate (Throwable t, int severity, String message, String localizedMessage, Throwable stackTrace, java.util.Date date) {
            return t;
        }
        
        public Throwable attachAnnotations (Throwable t, org.openide.ErrorManager.Annotation[] arr) {
            return t;
        }
        
        public org.openide.ErrorManager.Annotation[] findAnnotations (Throwable t) {
            return null;
        }
        
        public org.openide.ErrorManager getInstance (String name) {
            if (
                name.startsWith ("org.openide.util.RequestProcessor") ||
                name.startsWith("TEST")
            ) {
                return new ErrManager ('[' + name + ']');
            } else {
                // either new non-logging or myself if I am non-logging
                return new ErrManager ();
            }
        }
        
        public void log (int severity, String s) {
            lastSeverity = severity;
            lastText = s;
            if (this != get()) {
                messages.append(prefix);
                messages.append(s);
                messages.append('\n');
            }
        }
        
        public void notify (int severity, Throwable t) {
            lastThrowable = t;
            lastSeverity = severity;
        }
        private static int lastSeverity;
        private static Throwable lastThrowable;
        private static String lastText;

        public static void assertNotify (int sev, Throwable t) {
            assertEquals ("Severity is same", sev, lastSeverity);
            assertSame ("Throwable is the same", t, lastThrowable);
            lastThrowable = null;
            lastSeverity = -1;
        }
        
        public static void assertLog (int sev, String t) {
            assertEquals ("Severity is same", sev, lastSeverity);
            assertEquals ("Text is the same", t, lastText);
            lastText = null;
            lastSeverity = -1;
        }
        
    } // end of ErrManager
    
}
