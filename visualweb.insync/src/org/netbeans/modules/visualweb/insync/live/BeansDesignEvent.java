/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visualweb.insync.live;


import org.openide.util.NbBundle;
import org.netbeans.modules.visualweb.extension.openide.util.Trace;

import com.sun.rave.designtime.EventDescriptor;
import org.netbeans.modules.visualweb.insync.UndoEvent;
import org.netbeans.modules.visualweb.insync.beans.Event;
import org.netbeans.modules.visualweb.insync.beans.EventSet;
import org.netbeans.modules.visualweb.insync.java.EventMethod;

/**
 * DesignEvent implementation based on delegation to beans.Event and subclasses, using Java and/or
 * markup source.
 *
 * @author Carl Quinn
 */
public class BeansDesignEvent extends SourceDesignEvent {

    Event event;  // the persisted event state if set, i.e. not default, null if not set

    /**
     * @param descriptor
     * @param lbean
     */
    public BeansDesignEvent(EventDescriptor descriptor, BeansDesignBean lbean) {
        super(descriptor, lbean);
        EventSet es = getEventSet();
        if (es != null)
            event = es.getEvent(descriptor.getListenerMethodDescriptor().getName());
        assert Trace.trace("insync.live", "JLE event:" + event);
    }

    /**
     * @return
     */
    private EventSet getEventSet() {
        BeansDesignBean lbean = (BeansDesignBean)liveBean;
        EventDescriptor ed = getEventDescriptor();
        EventSet es = lbean.bean.getEventSet(ed.getEventSetDescriptor().getName());
        return es;
    }

    /**
     * @return
     */
    private EventSet ensureEventSet() {
        BeansDesignBean lbean = (BeansDesignBean)getDesignBean();
        EventDescriptor ed = getEventDescriptor();
        EventSet es = lbean.bean.setEventSet(ed.getEventSetDescriptor().getName());
        return es;
    }

    /**
     * @return
     */
    public Event getEvent() {
        return event;
    }

    /*
     * @see com.sun.rave.designtime.DesignEvent#getHandlerName()
     */
    public String getHandlerName() {
        return event != null ? event.getHandlerName() : null;
    }

    protected void initEvent(String handlerName) {
        EventSet es = ensureEventSet();
        event = es.setEvent(getEventDescriptor(), getEventDescriptor().getListenerMethodDescriptor(), handlerName);
    }

    /*
     * @see com.sun.rave.designtime.DesignEvent#setHandlerName(java.lang.String)
     */
    public boolean setHandlerName(String handlerName) {
        if (handlerName == null)
            handlerName = getDefaultHandlerName();
		
		if (handlerName.trim().length() == 0) {
		    return removeHandler();
		}
		
        UndoEvent undoEvent = null;
        try {
            String description = NbBundle.getMessage(BeansDesignEvent.class, "SetHandlerName", handlerName);  //NOI18N
            undoEvent = liveBean.unit.model.writeLock(description);
            if (event == null) {
                initEvent(handlerName);
            }
            else {
                event.setHandler(handlerName);
            }
        }
        finally {
            liveBean.unit.model.writeUnlock(undoEvent);
        }
        //!CQ HACK to kick our MethodBind live property shadow so the live part is up to date
        /*
        if (event instanceof MethodBindEvent) {
            PropertyDescriptor pdescr = ((MethodBindEvent)event).getBindingProperty().getDescriptor();
            SourceDesignProperty lp = (SourceDesignProperty)liveBean.getProperty(pdescr);
            lp.initLive();
        }*/
        return true;
    }

    /*
     * @see com.sun.rave.designtime.DesignEvent#isHandled()
     */
    public boolean isHandled() {
        return event == null ? false : event.getHandlerMethod() != null;
    }

    /*
     * @see com.sun.rave.designtime.DesignEvent#removeHandler()
     */
    public boolean removeHandler() {
        if (event != null) {
            UndoEvent undoEvent = null;
            try {
                String description = NbBundle.getMessage(BeansDesignEvent.class, "RemoveHandler");  //NOI18N
                undoEvent = liveBean.unit.model.writeLock(description);
                event.getEventSet().unsetEvent(event);
                event = null;
            }
            finally {
                liveBean.unit.model.writeUnlock(undoEvent);
            }
        }
        return true;
    }

    /*
     * @see com.sun.rave.designtime.DesignEvent#setHandlerMethodSource(java.lang.String)
     */
    public void setHandlerMethodSource(String methodBody) throws IllegalArgumentException {
        UndoEvent undoEvent = null;
        try {
            String description = NbBundle.getMessage(BeansDesignEvent.class, "SetHandlerMethodSource", methodBody != null ? methodBody : "");  //NOI18N
            undoEvent = liveBean.unit.model.writeLock(description);
            event.getHandlerMethod().replaceBody(methodBody);
        } finally {
            liveBean.unit.model.writeUnlock(undoEvent);
        }
    }

    /*
     * @see com.sun.rave.designtime.DesignEvent#getHandlerMethodSource()
     */
    public String getHandlerMethodSource() {
        return (event != null && event.getHandlerMethod() != null)
            ? event.getHandlerMethod().getBodyText()
            : null;
    }


    /**
     * @return the string literal if the last return statement of handler
     * is a string return statement;
     */
    public String getHandlerMethodReturn() {
        return (event != null && event.getHandlerMethod() != null)
            ? event.getHandlerMethod().getMethodReturn() : null;
    }

    
    /**
     * Update the last null return statement if the link is created for first
     * time, else all the matching string return statements are updated
     */
    public void updateReturnStrings(String oldStr, String newStr) {    
        if(event != null) {
            EventMethod method = event.getHandlerMethod();
            if(method != null) {
                //oldStr will be null when the link is created first time
                if(oldStr == null) { //NOI18N
                    method.updateLastReturnStatement(newStr);
                }else {
                    method.updateReturnStrings(oldStr, newStr);
                }
            }
        }
    }
}
