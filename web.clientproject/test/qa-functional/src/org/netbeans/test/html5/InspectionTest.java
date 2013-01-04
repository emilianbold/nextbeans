/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.test.html5;

import java.util.logging.Logger;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.junit.NbModuleSuite;

/**
 *
 * @author Vladimir Riha
 */
public class InspectionTest extends GeneralHTMLProject {

    private static final Logger LOGGER = Logger.getLogger(InspectionTest.class.getName());

    public InspectionTest(String args) {
        super(args);
    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(InspectionTest.class).addTest(
                "testOpenProject",
                "testMultipleSelect").enableModules(".*").clusters(".*").honorAutoloadEager(true));
    }

    public void testOpenProject() throws Exception {
        startTest();
        InspectionTest.current_project = "simpleProject";
        openProject("simpleProject");
        setRunConfiguration("Embedded WebKit Browser", true, true);
        endTest();
    }

    /**
     * Case: Run file, turn inspection on, select one element, add another
     * element to selection, check that 2 elements are selected
     *
     * @throws Exception
     */
    public void testMultipleSelect() throws Exception {
        startTest();
        runFile("simpleProject", "index.html");
        EmbeddedBrowserOperator eb = new EmbeddedBrowserOperator("Web Browser");
        eb.checkInspectModeButton(true);
        EditorOperator eo = new EditorOperator("index.html");
        eo.setCaretPositionToLine(18);
        type(eo, "window.setTimeout(function() {document.getElementById(\"el1\").setAttribute(\":netbeans_selected\", \"set\")}, 3000);\n"
                + "window.setTimeout(function() {document.getElementById(\"el2\").setAttribute(\":netbeans_selected\", \"add\")}, 5000);");
        eo.save();
        waitElementsSelected(2);
        HTMLElement[] el = getSelectedElements();
        assertEquals("Unexpected number of selected elements: was " + el.length + " should be 2", 2, el.length);
        endTest();
    }
}
