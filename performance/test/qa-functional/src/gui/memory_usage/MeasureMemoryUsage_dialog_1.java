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


package gui.memory_usage;

import org.netbeans.junit.NbTestSuite;
import gui.menu.*;
import gui.window.*;
import gui.action.*;

/**
 * Measure UI-RESPONSIVENES and WINDOW_OPENING.
 *
 * @author  mmirilovic@netbeans.org
 */
public class MeasureMemoryUsage_dialog_1 {

    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();

        suite.addTest(new EmptyTestCase("measureMemoryUsage", "Empty test case"));

        suite.addTest(new ModuleManager("measureTime", "Module Manager dialog open"));
        suite.addTest(new ServerManager("measureTime", "Server Manager dialog open"));
        suite.addTest(new TemplateManager("measureTime", "Template Manager dialog open"));
        
        suite.addTest(new Options("measureTime", "Options dialog open"));
                
        return suite;
    }
    
}
