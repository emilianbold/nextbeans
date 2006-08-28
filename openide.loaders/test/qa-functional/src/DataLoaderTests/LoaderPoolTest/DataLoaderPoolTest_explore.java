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

/*
 * DataLoaderPoolTest_explore.java
 *
 * Tests basic asking operations over LoaderPool
 *
 * Created on June 22, 2001, 12:32 PM
 */

package DataLoaderTests.LoaderPoolTest;

import junit.framework.*;
import org.netbeans.junit.*;

public class DataLoaderPoolTest_explore extends NbTestCase {

    /** Creates new DataLoaderPoolTest_explore */
    public DataLoaderPoolTest_explore(java.lang.String testName) {
        super(testName);
    }

    /**Allows this test to be executed inside ide*/
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**This suite*/
    public static Test suite() {
        NbTestSuite suite = new NbTestSuite(DataLoaderPoolTest_explore.class);
        return suite;
    }
    
    static boolean successful = true;
    LoaderPoolTest LPT = null;
        
    /**
     *Performs initializing before own tests starts
     */
    void prepare() {
        try{
            //when not in XTest harness -> woring directory will be under actual userdir
            if (Manager.getWorkDirPath()==null) System.setProperty("nbjunit.workdir",System.getProperty("netbeans.user"));
            //clearWorkDir();
            LPT = new LoaderPoolTest(getName());
            //now setting workdir - this class will write nothing into the logs, only the utility class should,
            //however into location for this class
            LPT.work = ""+
                Manager.getWorkDirPath()+
                java.io.File.separator + this.getClass().getName().replace('.',java.io.File.separatorChar)+
                java.io.File.separator + getName();
            LPT.prepare();
        }catch(Exception e){
            e.printStackTrace();
            e.printStackTrace(getRef());
            assertTrue("Initialization of test failed! ->" + e,false);
        }
    }
    
    /**
     *Performs clean up
     */
    void clean() {
        //getRef().flush();
        //getRef().close();
        LPT.clean();
    }
    
    /**
     *Performs waiting of current thread for time in millis
     *@param millist integer number - time in millis to wait
     */
    void dummyWait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            LPT.printException(ex);
        }
    }
    
    /** Own test:
     *Asks for all Loaders, Preferred Loader etc.
     */
    public void testDLPExplore() {
        
        try{
            prepare();
            LPT.writeRef(new LoaderPoolTest("x").getClass().getName());
            LPT.exploreDataLoaderPool();
            LPT.compareTest();
            LPT.printExtensions( (org.openide.loaders.UniFileLoader) LPT.textdl );
            clean();
            //do not forget to merge the results!
            successful = successful && LPT.successful;
            System.out.println("\n" + successful );
        }catch(Throwable ee){
            ee.printStackTrace(getRef());
            ee.printStackTrace();
            LPT.writeRef(FAILED);
        }
        
        assertTrue("Exploration of DataLoaderPool failed!",successful);
    }
    
    //if you want print exceptions into log file, put here true.
    public static final boolean PRINT_EXCEPTIONS = true;
    
    public static final String PASSED = "passed.\n";
    public static final String FAILED = "failed.\n";
}
