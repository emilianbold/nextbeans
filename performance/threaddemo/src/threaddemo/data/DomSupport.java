/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package threaddemo.data;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.ref.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.text.NbDocument;
import org.openide.util.*;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.*;
import threaddemo.locking.Lock;
import threaddemo.locking.LockAction;
import threaddemo.locking.LockExceptionAction;
import threaddemo.model.*;
import threaddemo.util.*;

// XXX should maybe show stale value during delays

/**
 * Support class for DOM provider interface.
 * <p>The derived model is a {@link Document}. The deltas to the derived model
 * are the same {@link Document}s - this class does not model structural diffs
 * using the {@link TwoWaySupport} semantics.
 * @author Jesse Glick
 */
public final class DomSupport extends DocumentParseSupport implements DomProvider, ErrorHandler, TwoWayListener, EntityResolver, EventListener {
    
    static {
        // Need Xerces DOM - Crimson's DOM has no event support.
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
    }
    
    private final Phadhail ph;
    private final Set listeners = new HashSet();
    private boolean inIsolatingChange = false;
    
    public DomSupport(Phadhail ph, EditorCookie.Observable edit, Lock lock) {
        super(edit, lock);
        this.ph = ph;
        addTwoWayListener(this);
    }
    
    public Document getDocument() throws IOException {
        try {
            return (Document)getLock().read(new LockExceptionAction() {
                public Object run() throws IOException {
                    assert !inIsolatingChange;
                    try {
                        Object v = getValueBlocking();
                        System.err.println("getDocument: " + v);//XXX
                        return (Document)v;
                    } catch (InvocationTargetException e) {
                        throw (IOException)e.getCause();
                    }
                }
            });
        } catch (InvocationTargetException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public void setDocument(final Document d) throws IOException {
        if (d == null) throw new NullPointerException();
        try {
            getLock().write(new LockExceptionAction() {
                public Object run() throws IOException {
                    assert !inIsolatingChange;
                    Document old = (Document)getStaleValueNonBlocking();
                    if (old != null && old != d) {
                        ((EventTarget)old).removeEventListener("DOMSubtreeModified", DomSupport.this, false);
                        ((EventTarget)d).addEventListener("DOMSubtreeModified", DomSupport.this, false);
                    }
                    try {
                        mutate(d);
                        return null;
                    } catch (InvocationTargetException e) {
                        throw (IOException)e.getCause();
                    } catch (ClobberException e) {
                        throw (IOException)new IOException(e.toString()).initCause(e);
                    }
                }
            });
        } catch (InvocationTargetException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public boolean isReady() {
        return ((Boolean)getLock().read(new LockAction() {
            public Object run() {
                assert !inIsolatingChange;
                return getValueNonBlocking() != null ? Boolean.TRUE : Boolean.FALSE;
            }
        })).booleanValue();
    }
    
    public void start() {
        initiate();
    }
    
    public Lock lock() {
        return getLock();
    }
    
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    private void fireChange() {
        final ChangeListener[] ls;
        synchronized (listeners) {
            if (listeners.isEmpty()) {
                return;
            }
            ls = (ChangeListener[])listeners.toArray(new ChangeListener[listeners.size()]);
        }
        final ChangeEvent ev = new ChangeEvent(this);
        getLock().read(new Runnable() {
            public void run() {
                assert !inIsolatingChange;
                System.err.println("DS.fireChange");
                for (int i = 0; i < ls.length; i++) {
                    ls[i].stateChanged(ev);
                }
            }
        });
    }
    
    protected boolean requiresUnmodifiedDocument() {
        return false;
    }
    
    protected final DerivationResult doDerive(StyledDocument document, List documentEvents, Object oldValue) throws IOException {
        assert !inIsolatingChange;
        // ignoring documentEvents
        System.err.println("DS.doDerive");//XXX
        if (oldValue != null) {
            ((EventTarget)oldValue).removeEventListener("DOMSubtreeModified", this, false);
        }
        InputSource source;
        if (document != null) {
            String text;
            try {
                text = document.getText(0, document.getLength());
            } catch (BadLocationException e) {
                assert false : e;
                text = "";
            }
            source = new InputSource(new StringReader(text));
        } else {
            // From disk.
            source = new InputSource(ph.getInputStream());
        }
        Document newValue;
        try {
            newValue = XMLUtil.parse(source, false, false, this, this);
        } catch (SAXException e) {
            throw (IOException)new IOException(e.toString()).initCause(e);
        }
        ((EventTarget)newValue).addEventListener("DOMSubtreeModified", this, false);
        // This impl does not compute structural diffs, so newValue == derivedDelta when modified:
        return new DerivationResult(newValue, oldValue != null ? newValue : null);
    }
    
    protected final Object doRecreate(StyledDocument document, Object oldValue, Object derivedDelta) throws IOException {
        assert !inIsolatingChange;
        System.err.println("doRecreate");//XXX
        Document newDom = (Document)derivedDelta;
        // ignoring oldValue, returning same newDom
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CharArrayWriter wr = new CharArrayWriter();
        try {
            XMLUtil.write(newDom, baos, "UTF-8");
        } catch (IOException ioe) {
            assert false : ioe;
            throw ioe;
        }
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException e) {
            assert false : e;
        }
        try {
            document.insertString(0, baos.toString("UTF-8"), null);
        } catch (UnsupportedEncodingException e) {
            assert false : e;
            throw e;
        } catch (BadLocationException e) {
            assert false : e;
        }
        return newDom;
    }
    
    protected long delay() {
        return 5000L;
    }
    
    public String toString() {
        return "DomSupport[" + ph + "]";
    }
    
    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    public void warning(SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        // Ignore external entities.
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }
    
    public void broken(TwoWayEvent.Broken evt) {
        System.err.println("Received: " + evt);//XXX
        fireChange();
    }
    
    public void clobbered(TwoWayEvent.Clobbered evt) {
        System.err.println("Received: " + evt);//XXX
        assert false;
    }
    
    public void derived(TwoWayEvent.Derived evt) {
        System.err.println("Received: " + evt);//XXX
        fireChange();
    }
    
    public void forgotten(TwoWayEvent.Forgotten evt) {
        System.err.println("Received: " + evt);//XXX
        assert false;
    }
    
    public void invalidated(TwoWayEvent.Invalidated evt) {
        System.err.println("Received: " + evt);//XXX
        // just wait...
        initiate();
    }
    
    public void recreated(TwoWayEvent.Recreated evt) {
        System.err.println("Received: " + evt);//XXX
        fireChange();
    }
    
    public void handleEvent(final Event evt) {
        try {
            getLock().write(new Runnable() {
                public void run() {
                    Document d = (Document)evt.getCurrentTarget();
                    Document old = (Document)getValueNonBlocking();
                    assert old == null || old == d;
                    if (!inIsolatingChange) {
                        try {
                            setDocument(d);
                        } catch (IOException e) {
                            assert false : e;
                        }
                    }
                }
            });
        } catch (RuntimeException e) {
            // Xerces ignores them.
            e.printStackTrace();
        }
    }
    
    public void isolatingChange(Runnable r) {
        assert getLock().canWrite();
        assert !inIsolatingChange;
        inIsolatingChange = true;
        try {
            r.run();
        } finally {
            inIsolatingChange = false;
            Document d = (Document)getValueNonBlocking();
            if (d != null) {
                try {
                    setDocument(d);
                } catch (IOException e) {
                    assert false : e;
                }
            }
        }
    }
    
}
