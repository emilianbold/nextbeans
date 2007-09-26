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

package org.openide.nodes;

import java.awt.event.ActionEvent;
import java.beans.*;
import java.util.*;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;

import junit.textui.TestRunner;
import org.netbeans.junit.Log;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;

/** Checking some of the behaviour of Node (and AbstractNode).
 * @author Jaroslav Tulach, Jesse Glick
 */
public class NodeTest extends NbTestCase {

    public NodeTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(NodeTest.class));
    }

    public void testGetActions () throws Exception {
        final SystemAction[] arr1 = {
            SystemAction.get (PropertiesAction.class)
        };
        final SystemAction[] arr2 = {
        };
        
        AbstractNode an = new AbstractNode (Children.LEAF) {
            public SystemAction[] getActions () {
                return arr1;
            }
            
            public SystemAction[] getContextActions () {
                return arr2;
            }
        };
        
        
        assertEquals ("getActions(false) properly delegates to getActions()", arr1, an.getActions (false));
        assertEquals ("getActions(true) properly delegates to getContextActions()", arr2, an.getActions (true));
        
    }
    
    public void testCanCallNodeSetChildrenFromReadAccess() throws Exception {
        CharSequence log = Log.enable("global`", Level.WARNING);
        class Mn extends AbstractNode implements Runnable {
            public Mn() {
                super(Children.LEAF);
            }

            public void run() {
                setChildren(new Children.Array());
            }
        }
        Mn mn = new Mn();
        
        Children.MUTEX.readAccess(mn);
        
        assertEquals("Log is empty", "", log.toString());
    }
    
    public void testPreferredAction() throws Exception {
        final SystemAction a1 = SystemAction.get(PropertiesAction.class);
        final Action a2 = new AbstractAction() {
            public void actionPerformed(ActionEvent ev) {}
        };
        final SystemAction a3 = SystemAction.get(OpenAction.class);
        final Action a4 = new AbstractAction() {
            public void actionPerformed(ActionEvent ev) {}
        };
        // Old code:
        Node n1 = new AbstractNode(Children.LEAF) {
            {
                setDefaultAction(a1);
            }
        };
        Node n2 = new AbstractNode(Children.LEAF) {
            public SystemAction getDefaultAction() {
                return a1;
            }
        };
        // New code:
        Node n4 = new AbstractNode(Children.LEAF) {
            public Action getPreferredAction() {
                return a1;
            }
        };
        // emulation of DataNode
        Node n5 = new AbstractNode(Children.LEAF) {
            {
                setDefaultAction (a1);
            }
            
            public SystemAction getDefaultAction () {
                return super.getDefaultAction ();
            }
        };
        Node n6 = new AbstractNode(Children.LEAF) {
            public Action getPreferredAction() {
                return a2;
            }
        };
        // Wacko code:
        Node n7 = new AbstractNode(Children.LEAF) {
            {
                setDefaultAction(a1);
            }
            public SystemAction getDefaultAction() {
                return a3;
            }
        };
        assertEquals(a1, n1.getDefaultAction());
        assertEquals(a1, n1.getPreferredAction());
        assertEquals(a1, n2.getDefaultAction());
        assertEquals(a1, n2.getPreferredAction());
        assertEquals(a1, n4.getDefaultAction());
        assertEquals(a1, n4.getPreferredAction());
        assertEquals(a1, n5.getPreferredAction());
        assertEquals(a1, n5.getDefaultAction());
        assertEquals(null, n6.getDefaultAction());
        assertEquals(a2, n6.getPreferredAction());
        assertEquals(a3, n7.getDefaultAction());
        assertEquals(a3, n7.getPreferredAction());
    }

    public void testShortDescriptionCanBeSetToNull () {
        class PCL extends NodeAdapter {
            public int cnt;
            
            public void propertyChange (PropertyChangeEvent ev) {
                if (Node.PROP_SHORT_DESCRIPTION.equals (ev.getPropertyName ())) {
                    cnt++;
                }
            }
        }
        
        AbstractNode an = new AbstractNode (Children.LEAF);
        an.setDisplayName ("My name");
        
        PCL pcl = new PCL ();
        an.addNodeListener (pcl);
        assertEquals ("My name", an.getShortDescription ());
        
        an.setShortDescription ("Ahoj");
        assertEquals ("Ahoj", an.getShortDescription ());
        assertEquals ("One change", 1, pcl.cnt);
        
        an.setShortDescription (null);
        assertEquals ("My name", an.getShortDescription ());
        assertEquals ("Second change", 2, pcl.cnt);
    }
    
    /** Another sample action */
    public static final class PropertiesAction extends OpenAction {
        
    }
}
