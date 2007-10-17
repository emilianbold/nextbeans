/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.cnd.repository.access;

import java.io.File;
import java.util.Collection;
import java.util.List;
import org.netbeans.modules.cnd.api.model.CsmProject;
import org.netbeans.modules.cnd.modelimpl.csm.core.ModelImpl;
import org.netbeans.modules.cnd.modelimpl.trace.TraceModelBase;

/**
 * Tries to close project as soon as it is parsed -
 * while its libraries are still being parsed
 * @author Vladimir Kvashin
 */
public class CloseProjectWhenParsingLib extends RepositoryAccessTestBase  {

    private final static boolean verbose;
    static {
        verbose = true; // Boolean.getBoolean("test.library.access.verbose");
        if( verbose ) {
            System.setProperty("cnd.modelimpl.timing", "true");
            System.setProperty("cnd.modelimpl.timing.per.file.flat", "true");
            System.setProperty("cnd.repository.listener.trace", "true");
            System.setProperty("cnd.trace.close.project", "true");
	    System.setProperty("cnd.repository.workaround.nulldata", "true");
        }
    }    
    
    public CloseProjectWhenParsingLib(String testName) {
	super(testName);
    }
    
    public void testRun() throws Exception {
	
	File projectRoot = getDataFile("quote_syshdr");
	
	int count = Integer.getInteger("test.close.project.when.parsing.libs.laps", 2);
	
	final TraceModelBase traceModel = new  TraceModelBase();
	traceModel.setUseSysPredefined(true);
	traceModel.processArguments(projectRoot.getAbsolutePath());
	ModelImpl model = traceModel.getModel();
	
	for (int i = 0; i < count; i++) {
	    System.err.printf("%s: processing project %s. Pass %d \n", getBriefClassName(), projectRoot.getAbsolutePath(), i);
	    final CsmProject project = traceModel.getProject();
	    assertNoExceptions();
	    project.waitParse();
	    Collection<CsmProject> libs = project.getLibraries();
	    // here is the key point:
	    // if we wait until libs are parsed prior than resetting the project,
	    // the assertion won't appear;
	    // if we close project without persistence cleanup, 
	    // the assertion won't appear either
	    // waitLibsParsed(project);
	    traceModel.resetProject(true);
	    waitCloseAndClear(libs, traceModel);
	    assertNoExceptions();
	}
	assertNoExceptions();
    }
    
    private void waitCloseAndClear(Collection<CsmProject> libs, TraceModelBase traceModel) {
        for( CsmProject lib : libs ) {
	    lib.waitParse();
	}
        for( CsmProject lib : libs ) {
	    traceModel.closeProject(lib, true);
	}
    }
    
}
