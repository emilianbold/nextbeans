/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.kenai.ui.spi;

import java.beans.PropertyChangeListener;

/**
 * Abstraction of messaging info associated with a kenai project.
 * 
 * @author S. Aubrecht
 */
public abstract class MessagingHandle {

    /**
     * The name of Integer property which is fired when the count of online members
     * has changed for this project. The property value is the new count of online members.
     */
    public static final String PROP_ONLINE_COUNT = "onlineCount";
    /**
     * The name of Integer property which is fired when the count of messages
     * has changed for this project. The property value is the new count of messages.
     */
    public static final String PROP_MESSAGE_COUNT = "messageCount";

    /**
     *
     * @return Number of online project members.
     */
    public abstract int getOnlineCount();

    /**
     *
     * @return Number of available messages.
     */
    public abstract int getMessageCount();

    public abstract void addPropertyChangeListener( PropertyChangeListener l );

    public abstract void removePropertyChangeListener( PropertyChangeListener l );
}
