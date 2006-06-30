/*
 * $Id$
 *
 * ---------------------------------------------------------------------------
 *
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
 * "Portions Copyrighted [year] [name of copyright owner]".
 *
 * The Original Software is the Jemmy library. The Initial Developer of the
 * Original Software is Alexandre Iline. All Rights Reserved.
 *
 * ---------------------------------------------------------------------------
 *
 * Contributor(s): Manfred Riem (mriem@netbeans.org).
 */
package org.netbeans.jemmy.operators;

import java.awt.Frame;
import java.awt.Label;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.netbeans.jemmy.util.NameComponentChooser;

/**
 * A JUnit test for LabelOperator.
 *
 * @author Manfred Riem (mriem@netbeans.org)
 * @version $Revision$
 */
public class LabelOperatorTest extends TestCase {
    /**
     * Stores the frame.
     */
    private Frame frame;
    
    /**
     * Stores the label.
     */
    private Label label;
    
    /**
     * Constructor.
     *
     * @param testName the name of the test.
     */
    public LabelOperatorTest(String testName) {
        super(testName);
    }

    /**
     * Setup before testing.
     *
     * @throws Exception when a major problem occurs.
     */
    protected void setUp() throws Exception {
        frame = new Frame();
        label = new Label("LabelOperatorTest");
        label.setName("LabelOperatorTest");
        frame.add(label);
    }

    /**
     * Cleanup after testing.
     *
     * @throws Exception when a major problem occurs.
     */
    protected void tearDown() throws Exception {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    /**
     * Suite method.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LabelOperatorTest.class);
        
        return suite;
    }
    
    /**
     * Test constructor.
     */
    public void testConstructor() {
        frame.setVisible(true);
        
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        
        LabelOperator operator1 = new LabelOperator(operator);
        assertNotNull(operator1);
        
        LabelOperator operator2 = new LabelOperator(operator, new NameComponentChooser("LabelOperatorTest"));
        assertNotNull(operator2);
        
        LabelOperator operator3 = new LabelOperator(operator, "LabelOperatorTest");
        assertNotNull(operator3);
    }

    /**
     * Test findLabel method.
     */
    public void testFindLabel() {
        frame.setVisible(true);
        
        Label label = LabelOperator.findLabel(frame, "LabelOperatorTest", false, false);
        assertNotNull(label);
        
        Label label2 = LabelOperator.findLabel(frame, new NameComponentChooser("LabelOperatorTest"));
        assertNotNull(label2);
    }

    /**
     * Test waitLabel method.
     */
    public void testWaitLabel() {
        frame.setVisible(true);
        
        Label label = LabelOperator.waitLabel(frame, "LabelOperatorTest", false, false);
        assertNotNull(label);
        
        Label label2 = LabelOperator.waitLabel(frame, new NameComponentChooser("LabelOperatorTest"));
        assertNotNull(label2);
    }

    /**
     * Test getDump method.
     */
    public void testGetDump() {
        frame.setVisible(true);
        
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        
        LabelOperator operator1 = new LabelOperator(operator);
        assertNotNull(operator1);
        
        operator1.getDump();
    }

    /**
     * Test getAlignment method.
     */
    public void testGetAlignment() {
        frame.setVisible(true);
        
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        
        LabelOperator operator1 = new LabelOperator(operator);
        assertNotNull(operator1);
        
        operator1.setAlignment(operator1.getAlignment());
    }

    /**
     * Test getText method.
     */
    public void testGetText() {
        frame.setVisible(true);
        
        FrameOperator operator = new FrameOperator();
        assertNotNull(operator);
        
        LabelOperator operator1 = new LabelOperator(operator);
        assertNotNull(operator1);
        
        operator1.setText(operator1.getText());
    }
}
