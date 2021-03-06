/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 */
package org.netbeans.jellytools.modules.form.properties.editors;

import java.io.IOException;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.modules.form.FormDesignerOperator;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jemmy.EventTool;
import org.netbeans.junit.NbTestSuite;

/**
 * Test of
 * org.netbeans.jellytools.modules.form.properties.editors.PropertyPickerOperator.
 * * @author Jiri Skrivanek
 */
public class PropertyPickerOperatorTest extends FormPropertiesEditorsTestCase {

    public static final String[] tests = new String[]{
        "testLblComponent",
        "testCboComponent",
        "testLblProperties",
        "testLstProperties",
        "testSetComponent",
        "testSetProperty",
        "testClose"
    };

    /** Method used for explicit testsuite definition
     * @return  created suite
     */
    public static NbTestSuite suite() {
        return (NbTestSuite) createModuleTest(PropertyPickerOperatorTest.class, tests);
    }

    /** Redirect output to log files, wait before each test case. */
    @Override
    protected void setUp() throws IOException {
        super.setUp();
        if (ppo == null) {
            // need to wait because combo box is not refreshed in time
            new EventTool().waitNoEvent(1000);
            // set "Value from existing component"
            fceo.setMode(Bundle.getString("org.netbeans.modules.form.Bundle", "CTL_FormConnection_DisplayName"));
            ParametersPickerOperator paramPicker = new ParametersPickerOperator(PROPERTY_NAME);
            paramPicker.property();
            paramPicker.selectProperty();
            ppo = new PropertyPickerOperator();
        }
    }
    private static PropertyPickerOperator ppo;

    /** Constructor required by JUnit.
     * @param testName method name to be used as testcase
     */
    public PropertyPickerOperatorTest(java.lang.String testName) {
        super(testName);
    }

    /** Test of lblComponent method. */
    public void testLblComponent() {
        String expected = Bundle.getStringTrimmed("org.netbeans.modules.form.Bundle", "CTL_CW_Component");
        String label = ppo.lblComponent().getText();
        assertEquals("Wrong label found.", expected, label);
    }

    /** Test of cboComponent method. */
    public void testCboComponent() {
        ppo.cboComponent();
    }

    /** Test of lblProperties method. */
    public void testLblProperties() {
        String expected = Bundle.getStringTrimmed("org.netbeans.modules.form.Bundle", "CTL_CW_PropertyList");
        String label = ppo.lblProperties().getText();
        assertEquals("Wrong label found.", expected, label);
    }

    /** Test of lstProperties method. */
    public void testLstProperties() {
        ppo.lstProperties();
    }

    /** Test of setComponent method. */
    public void testSetComponent() {
        String expected = Bundle.getString("org.netbeans.modules.form.Bundle", "CTL_FormTopContainerName");
        ppo.setComponent(expected);
        assertEquals("Select component failed.", expected, ppo.cboComponent().getSelectedItem());
    }

    /** Test of setProperty method. Also closes opened windows. */
    public void testSetProperty() {
        String expected = "title";
        ppo.setProperty(expected);
        assertEquals("Select property failed.", expected, ppo.lstProperties().getSelectedValue());
    }

    /** Clean-up after tests. Close opened dialog and property sheet. */
    public void testClose() {
        ppo.close();
        fceo = null;
        new PropertySheetOperator("[JFrame]").close();
        new FormDesignerOperator(SAMPLE_FRAME_NAME).closeDiscard();
    }
}
