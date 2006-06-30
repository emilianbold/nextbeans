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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.tests.j2eeserver.plugin.jsr88;

import javax.enterprise.deploy.spi.*;
import java.util.*;

/**
 *
 * @author  gfink
 */
public class Targ implements Target {

    String name;
    public Targ(String name) {
        this.name = name;
    }

    public String getDescription() {
        return "Description for " + name;
    }

    public String getName() {
        return name;
    }

    Map modules = new HashMap();
    public void add(TargetModuleID tmid) {
        modules.put(tmid.toString(), tmid);
    }
    public TargetModuleID getTargetModuleID(String id) {
        return (TargetModuleID) modules.get(id);
    }
    public TargetModuleID[] getTargetModuleIDs() {
        return (TargetModuleID[]) modules.values().toArray(new TargetModuleID[0]);
    }
}
