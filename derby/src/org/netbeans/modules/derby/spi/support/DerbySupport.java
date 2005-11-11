/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.derby.spi.support;

import java.io.File;
import org.netbeans.modules.derby.DerbyOptions;

/**
 *
 * @author Andrei Badea
 */
public final class DerbySupport {
    
    private DerbySupport() {
    }
    
    /**
     * Sets the location of the Derby jars.
     *
     * @param location the jars locations. This must be an existing directory.
     */
    public static void setDerbyLocation(String location) {
        DerbyOptions.getDefault().setDerbyLocation(location);
    }
    
    public static String getDerbyLocation() {
        return DerbyOptions.getDefault().getDerbyLocation();
    }
}
