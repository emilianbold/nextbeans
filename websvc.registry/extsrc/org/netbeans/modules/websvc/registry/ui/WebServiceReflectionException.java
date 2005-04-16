/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.registry.ui;

/**
 * This Exception class wraps Reflection Exceptions so the Web Service code can catch a single exception.
 * @author  David Botterill
 */
public class WebServiceReflectionException extends Exception {
    
    /** Creates a new instance of WebServiceReflectionException */
    public WebServiceReflectionException() {
    }
    
    public WebServiceReflectionException(String inMessage,Throwable inThrowable) {
        super(inMessage,inThrowable);
    }
    
}
