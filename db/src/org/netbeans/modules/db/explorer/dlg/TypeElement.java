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

package org.netbeans.modules.db.explorer.dlg;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.netbeans.lib.ddl.*;
import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import org.netbeans.modules.db.explorer.*;

/**
* xxx
*
* @author Slavek Psenicka
*/

class TypeElement
{
    private String tstr, tname;

    public TypeElement(String typestr, String name)
    {
        tstr = typestr;
        tname = name;
    }

    public String getType()
    {
        return tstr;
    }

    public String getName()
    {
        return tname;
    }

    public String toString()
    {
        return tname;
    }
}
