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

package org.netbeans.modules.form.wizard;

import javax.swing.event.*;
import org.netbeans.modules.form.*;

/**
 * The first panel of connection wizard - for selecting the activation event
 * on the source component and handler for the event (where the connection code
 * will be generated).
 *
 * @author Tomas Pavek
 */

class ConnectionWizardPanel1 implements org.openide.WizardDescriptor.Panel {

    private Event selectedEvent;

    private RADComponent sourceComponent;

    private EventListenerList listenerList;

    private ConnectionPanel1 uiPanel;

    // -------

    ConnectionWizardPanel1(RADComponent source) {
        sourceComponent = source;
    }

    RADComponent getSourceComponent() {
        return sourceComponent;
    }

    Event getSelectedEvent() {
        return selectedEvent;
    }
    String getEventName() {
        return uiPanel != null ? uiPanel.getEventName() : null;
    }

    void setSelectedEvent(Event event) {
        selectedEvent = event;
        fireStateChanged();
    }

    boolean handlerAlreadyExists() {
        if (uiPanel == null)
            return false;

        return selectedEvent != null
               && selectedEvent.hasEventHandler(uiPanel.getEventName());
    }

    // ----------
    // WizardDescriptor.Panel implementation

    public java.awt.Component getComponent() {
        if (uiPanel == null)
            uiPanel = new ConnectionPanel1(this);
        return uiPanel;
    }

    public org.openide.util.HelpCtx getHelp() {
        return new org.openide.util.HelpCtx("gui.connecting.source"); // NOI18N
    }

    public boolean isValid() {
        String eventName = uiPanel != null ? uiPanel.getEventName() : null;
        return selectedEvent != null
               && eventName != null && !"".equals(eventName) 
               && org.openide.util.Utilities.isJavaIdentifier(eventName);
    }

    public void readSettings(java.lang.Object settings) {
    }

    public void storeSettings(java.lang.Object settings) {
    }

    public void addChangeListener(ChangeListener listener) {
        if (listenerList == null)
            listenerList = new EventListenerList();
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        if (listenerList != null)
            listenerList.remove(ChangeListener.class, listener);
    }

    // -----

    void fireStateChanged() {
        if (listenerList == null)
            return;

        ChangeEvent e = null;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ChangeListener.class) {
                if (e == null)
                    e = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(e);
            }
        }
    }
}
