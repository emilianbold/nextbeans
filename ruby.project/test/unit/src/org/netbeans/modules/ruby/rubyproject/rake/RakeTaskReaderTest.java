/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ruby.rubyproject.rake;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.SortedSet;
import org.netbeans.modules.ruby.rubyproject.RubyProject;
import org.netbeans.modules.ruby.rubyproject.RubyProjectTestBase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class RakeTaskReaderTest extends RubyProjectTestBase {

    public RakeTaskReaderTest(String testName) {
        super(testName);
    }

    public void testGetRakeTaskTree() throws Exception {
        registerLayer();
        RubyProject project = createTestProject();
        RakeTaskReader rakeTaskReader = new RakeTaskReader(project);
        Set<RakeTask> tasks = rakeTaskReader.getRakeTaskTree();
        assertNotNull("rake file output dumped", tasks);
        assertFalse("has rake tasks", tasks.isEmpty());
    }

    public void testRawRead() throws Exception {
        registerLayer();
        RubyProject project = createTestProject();
        RakeTaskReader rakeTaskReader = new RakeTaskReader(project);
        assertNotNull("rake file output dumped", rakeTaskReader.rawRead());
    }

    public void testMultilineDescriptions() throws Exception {
        registerLayer();
        RubyProject project = createTestProject();
        String rakeContent =
                "desc 'Dummy task'\n" +
                "task :dummy\n" +
                "\n" +
                "desc 'Multiline\n" +
                "dummy'\n" +
                "task :multiline_dummy";
        dumpRakefile(rakeContent, project);
        RakeSupport.refreshTasks(project);
        RakeTaskReader rakeTaskReader = new RakeTaskReader(project);
        SortedSet<RakeTask> tasks = (SortedSet<RakeTask>) rakeTaskReader.getRakeTaskTree();
        assertEquals("two tasks", 2, tasks.size());
        RakeTask multi = RakeSupport.getRakeTask(project, "multiline_dummy");
        assertNotNull(multi);
        assertEquals("has multiline description", "Multiline\ndummy", multi.getDescription());
    }

    public void testTaskWithNamespace() throws Exception {
        registerLayer();
        RubyProject project = createTestProject();
        String rakeContent =
                "namespace 'test' do\n" +
                "  desc 'test coverage'\n" +
                "  task :coverage\n" +
                "end";
        dumpRakefile(rakeContent, project);
        RakeSupport.refreshTasks(project);
        RakeTaskReader rakeTaskReader = new RakeTaskReader(project);
        SortedSet<RakeTask> tasks = (SortedSet<RakeTask>) rakeTaskReader.getRakeTaskTree();
        assertEquals("one tasks", 1, tasks.size());
        RakeTask multi = RakeSupport.getRakeTask(project, "test:coverage");
        assertNotNull(multi);
        assertEquals("semicoloned task", "test:coverage", multi.getTask());
        assertEquals("semicoloned task", "coverage", multi.getDisplayName());
    }

    private void dumpRakefile(final String rakefileContent, final RubyProject project) throws IOException {
        FileObject script = FileUtil.createData(project.getProjectDirectory(), "Rakefile");
        PrintWriter pw = new PrintWriter(script.getOutputStream());
        try {
            pw.print(rakefileContent);
        } finally {
            pw.close();
        }
    }
}
