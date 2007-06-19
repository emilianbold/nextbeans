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
package org.netbeans.qa.form.visualDevelopment;

import java.util.Vector;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.actions.EditAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.ProjectRootNode;

import org.netbeans.junit.NbTestSuite;

import org.netbeans.jellytools.*;
import org.netbeans.jellytools.modules.form.*;
import org.netbeans.jellytools.nodes.*;
import org.netbeans.jellytools.actions.*;

import org.netbeans.jemmy.operators.*;
import org.netbeans.junit.ide.ProjectSupport;
import org.netbeans.qa.form.*;
import java.io.*;


/**
 *<P>
 *<B><BR> Test create frame.</B>
 *
 *<BR><BR><B>What it tests:</B><BR>
 *  Frame containing all components from Component Palette AWT category try compile.
 *<BR><BR><B>How it works:</B><BR>
 *  Find tested form file, add all components from AWT category and compile created frame (check compile resolution).
 *
 *<BR><BR><B>Settings:</B><BR>
 *  Jemmy/Jelly classes, VisualDevelopmentSupport class in the classpath.
 *
 *<BR><BR><B>Resources:</B><BR>
 *  File (Resources.) clear_Frame(java/form) generated by NBr32(37).
 *
 *<BR><B>Possible reasons of failure</B>
 * <BR><U>jelly didn't find menu or popup menu</U>
 * <BR><U>is impossible add component or components in AWT category is another as in NB r3.2 (37)</U>
 * <BR><U>component was't add correctly or generated source code is wrong</U>
 *
 * @author  Marian.Mirilovic@czech.sun.com
 * @version
 */
public class AddComponents_AWT extends JellyTestCase {
    
    public String FILE_NAME = "clear_Frame";
    public String PACKAGE_NAME = "data";
    public String DATA_PROJECT_NAME = "SampleProject";
    public String FRAME_ROOT = "[Frame]";
    
    public MainWindowOperator mainWindow;
    public ProjectsTabOperator pto;
    public Node formnode;
    
    public AddComponents_AWT(String testName) {
        super(testName);
    }
    
    /** Run test.
     */
    
    public void testAddAndCompile() {
        String categoryName = "AWT";
        
        System.out.println(">>>" + this.getWorkDirPath() + "<<<");
        
        mainWindow = MainWindowOperator.getDefault();
        pto = new ProjectsTabOperator();
        sleep(300);
        ProjectRootNode prn = pto.getProjectRootNode(DATA_PROJECT_NAME);
        sleep(300);
        prn.select();
        formnode = new Node(prn, "Source Packages|" + PACKAGE_NAME + "|" + FILE_NAME);
        formnode.select();
        log("Form node selected.");
        
        EditAction editAction = new EditAction();
        editAction.perform(formnode);
        log("Source Editor window opened.");
        
        OpenAction openAction = new OpenAction();
        openAction.perform(formnode);
        log("Form Editor window opened.");
        
        // store all component names from the category in the Vector
        Vector componentNames = new Vector();
        ComponentPaletteOperator palette = new ComponentPaletteOperator();
        palette.collapseBeans();
        //        palette.collapseLayouts();
        palette.collapseSwingControls();
        palette.collapseSwingControls();
        palette.collapseSwingMenus();
        palette.collapseSwingWindows();
        palette.expandAWT();
        String[] componentList = {"Label", "Button", "Text Field", "Text Area", "Checkbox", "Choice", "List", "Scrollbar", "Scroll Pane", "Panel", "Canvas", "Menu Bar", "Popup Menu"};
        for (int i=0;i < componentList.length; i++) {
            log("Adding " + componentList[i]);
            componentNames.addElement(componentList[i]);
        }
        ComponentInspectorOperator cio = new ComponentInspectorOperator();
        Node inspectorRootNode = new Node(cio.treeComponents(), FRAME_ROOT);
        inspectorRootNode.select();
        inspectorRootNode.expand();
        // add all beans from Palette Category to form
        Action popupAddFromPaletteAction;
        for(int i = 0; i < componentNames.size(); i++){
            String itemPath = "Add From Palette|AWT|" + componentNames.elementAt(i).toString();
            log("Running " + itemPath);
            popupAddFromPaletteAction = new Action(null, itemPath);
            popupAddFromPaletteAction.perform(inspectorRootNode);
        }
        
        log("All components from Component Palette : " + categoryName + " - were added to " + FILE_NAME);
        log("Try to save the form.");
        new org.netbeans.jemmy.EventTool().waitNoEvent(1000);
        editAction.perform(formnode);
        Action saveAction;
        saveAction = new Action("File|Save", null);
        saveAction.perform();
    }
    
    /** Run test.
     */
    public void testFormFile() {
        compareFileByExt("form");
    }
    
    /** Run test.
     */
    public void testJavaFile() {
        compareFileByExt("java");
    }
    
    private void compareFileByExt(String fileExt) {
        String refSourceFilePath = getDataDir().getAbsolutePath() + File.separatorChar
                + DATA_PROJECT_NAME +  File.separatorChar + "src" + File.separatorChar
                + PACKAGE_NAME + File.separatorChar + FILE_NAME + "." + fileExt;
        log("refSourceFilePath:" + refSourceFilePath);
        
        try {
            getRef().print( VisualDevelopmentUtil.readFromFile(refSourceFilePath) );
        } catch (Exception e) {
            fail("Fail during creating ref file: " + e.getMessage());
        }
        
        String javaVersionPrefix = VisualDevelopmentUtil.JAVA_VERSION.substring(0,3);
        String passFileName = this.getName() + "_" + javaVersionPrefix + ".pass";
        log("passFileName: " + passFileName);
        
        compareReferenceFiles(this.getName()+".ref", passFileName, this.getName()+".diff");
    }

    /** Run test.
     */
    public void testCloseDataProject(){
        closeDataProject();
        //        EditorWindowOperator ewo = new EditorWindowOperator();
        //        ewo.closeDiscard();
    }
    
    public void closeDataProject(){
        ProjectSupport.closeProject(DATA_PROJECT_NAME);
        log("SampleProject closed.");
        
    }
    
    
    
    static void sleep(int ms) {
        try {Thread.sleep(ms);} catch (Exception e) {}
    }
    
    /** Suite
     * @param args arguments from command line
     */
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new AddComponents_AWT("testAddAndCompile"));
        suite.addTest(new AddComponents_AWT("testFormFile"));
        suite.addTest(new AddComponents_AWT("testJavaFile"));
        //suite.addTest(new AddComponents_AWT("testCloseDataProject"));
        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
