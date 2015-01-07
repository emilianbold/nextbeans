/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium2.webclient.protractor;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.print.ConvertedLine;
import org.netbeans.api.extexecution.print.LineConvertor;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.javascript.v8debug.api.Connector;
import org.netbeans.modules.selenium2.api.Utils;
import org.netbeans.modules.selenium2.webclient.api.RunInfo;
import org.netbeans.modules.selenium2.webclient.api.SeleniumRerunHandler;
import org.netbeans.modules.selenium2.webclient.api.SeleniumTestingProviders;
import org.netbeans.modules.selenium2.webclient.api.TestRunnerReporter;
import org.netbeans.modules.selenium2.webclient.api.Utilities;
import org.netbeans.modules.selenium2.webclient.protractor.preferences.ProtractorPreferences;
import org.netbeans.modules.selenium2.webclient.protractor.preferences.ProtractorPreferencesValidator;
import org.netbeans.modules.web.clientproject.api.WebClientProjectConstants;
import org.netbeans.modules.web.common.api.ExternalExecutable;
import org.netbeans.modules.web.common.api.ValidationResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 *
 * @author Theofanis Oikonomou
 */
class ProtractorRunner {
    private static final Logger LOG = Logger.getLogger(ProtractorRunner.class.getName());
    private static final String DEBUG_HOST = "localhost"; // NOI18N
    private static final int DEBUG_PORT = 5858;
    
    public static void runTests(FileObject[] activatedFOs) {
        internalProtractorRunner(activatedFOs, false);
    }
    
    public static void debugTests(FileObject[] activatedFOs) {
        internalProtractorRunner(activatedFOs, true);
    }
    
    private static void internalProtractorRunner(FileObject[] activatedFOs, boolean debug) {
        assert !EventQueue.isDispatchThread();
        Project p = FileOwnerQuery.getOwner(activatedFOs[0]);
        if (p == null) {
            return;
        }
        
        File protractorJasmineNBReporter = InstalledFileLocator.getDefault().locate(
                    "protractor/jasmine-netbeans-reporter.js", "org.netbeans.modules.selenium2.webclient.protractor", false); // NOI18N
        if(protractorJasmineNBReporter == null) {
            return;
        }
        
        File protractorNBConfig = InstalledFileLocator.getDefault().locate(
                    "protractor/netbeans-configuration.js", "org.netbeans.modules.selenium2.webclient.protractor", false); // NOI18N
        if(protractorNBConfig == null) {
            return;
        }
        
        String node = Utilities.getNode(p);
        if(node == null) {
            Utilities.openNodeSettings(p);
            return;
        }
        
        FileObject testsFolder = Utilities.getTestsSeleniumFolder(p, true);
        if(testsFolder == null) {
            Utilities.openCustomizer(p, WebClientProjectConstants.CUSTOMIZER_SOURCES_IDENT);
            return;
        }
        
        String protractor = ProtractorPreferences.getProtractor(p);
        String userConfigurationFile = ProtractorPreferences.getUserConfigurationFile(p);
        ValidationResult validationResult = new ProtractorPreferencesValidator()
                .validateProtractor(protractor)
                .validateUserConfigurationFile(p, userConfigurationFile)
                .getResult();
        if(protractor == null || protractor.isEmpty() || !validationResult.isFaultless()) {
            Utilities.openCustomizer(p, SeleniumTestingProviders.CUSTOMIZER_SELENIUM_TESTING_IDENT);
            return;
        }
        
        boolean testProject = activatedFOs.length == 1 && activatedFOs[0].equals(p.getProjectDirectory());
        String specs;
        if(testProject) {
            specs = FileUtil.toFile(testsFolder).getAbsolutePath() + "/**/*.js";
        } else {
            ArrayList<String> files2test = new ArrayList<>();
            for(FileObject fo : activatedFOs) {
                if(fo.isFolder()) { // recursively add all specs under this folder
                    Enumeration<? extends FileObject> children = fo.getChildren(true);
                    while (children.hasMoreElements()) {
                        add2specs(children.nextElement(), files2test);
                    }
                } else {
                    add2specs(fo, files2test);
                }
            }
            String specs2test = files2test.toString();
            specs = specs2test.substring(1, specs2test.length() - 1);
        }
        RunInfo runInfo = getRunInfo(p, activatedFOs, testsFolder, specs, protractorJasmineNBReporter.getAbsolutePath(), userConfigurationFile, testProject);

        String displayname = ProjectUtils.getInformation(runInfo.getProject()).getDisplayName() + " Selenium Tests"; // NOI18N
        final ExternalExecutable externalexecutable = new ExternalExecutable(node)
                .workDir(FileUtil.toFile(p.getProjectDirectory()))
                .displayName(displayname)
                .additionalParameters(getAdditionalArguments(protractor, protractorNBConfig.getAbsolutePath(), debug))
                .environmentVariables(runInfo.getEnvVars());
        TestRunnerReporter testRunnerReporter = new TestRunnerReporter(runInfo, "jasmine-netbeans-reporter "); // NOI18N

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ExecutionDescriptor.LineConvertorFactory outputLineConvertorFactory = getOutputLineConvertorFactory(testRunnerReporter, runInfo, countDownLatch, debug);

        final ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true)
                .showProgress(true)
                .showSuspended(true)
                .outLineBased(true)
                .errLineBased(true)
                .outConvertorFactory(outputLineConvertorFactory);

        final Future<Integer> task = externalexecutable.run(descriptor);
        
        if (debug) {
            try {
                countDownLatch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            Connector.Properties props = createConnectorProperties(DEBUG_HOST, DEBUG_PORT, p);
            try {
                Connector.connect(props, new Runnable() {
                    @Override
                    public void run() {
                        task.cancel(true);
                    }
                });
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private static void add2specs(FileObject fo, ArrayList<String> files2test) {
        String file2test = FileUtil.toFile(fo).getAbsolutePath();
        if (file2test != null) {
            if (!files2test.contains(file2test)) {
                files2test.add(file2test);
            }
        }
    }
    
    private static ExecutionDescriptor.LineConvertorFactory getOutputLineConvertorFactory(final TestRunnerReporter testRunnerReporter, final RunInfo runInfo, final CountDownLatch countDownLatch, boolean debug) {
        if (!debug) {
            return new ExecutionDescriptor.LineConvertorFactory() {
                @Override
                public LineConvertor newLineConvertor() {
                    return new OutputLineConvertor(testRunnerReporter, runInfo, null);
                }
            };
        }
        final Runnable countDownTask = new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
            }
        };
        return new ExecutionDescriptor.LineConvertorFactory() {
            @Override
            public LineConvertor newLineConvertor() {
                return new OutputLineConvertor(testRunnerReporter, runInfo, countDownTask);
            }
        };
    }
    
    private static ArrayList<String> getAdditionalArguments(String protractor, String config, boolean debug) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(protractor);
        if(debug) {
            arguments.add("--debug-brk");
        }
        arguments.add(config);
        
        return arguments;
    }
    
    private static RunInfo getRunInfo(Project p, FileObject[] activatedFOs, FileObject testsFolder, String specs, String jasmineNBReporter, String userConfigurationFile, boolean testProject) {
        RunInfo.Builder builder = new RunInfo.Builder(activatedFOs).setTestingProject(testProject)
                .addEnvVar("SPECS", specs)
                .addEnvVar("JASMINE_NB_REPORTER", jasmineNBReporter)
                .addEnvVar("USER_CONFIGURATION_FILE", userConfigurationFile)
                .setRerunHandler(new SeleniumRerunHandler(p, activatedFOs, CustomizerProtractorPanel.IDENTIFIER, true))
                .setIsSelenium(true)
                .setShowOutput(false);
        if(activatedFOs.length == 1 && !activatedFOs[0].equals(p.getProjectDirectory())) {
            String testFile = FileUtil.getRelativePath(testsFolder, activatedFOs[0]);
            builder = builder.setTestFile(testFile);
        }
        return builder.build();
    }
    
    private static Connector.Properties createConnectorProperties(String host, int port, Project project) {
        List<File> sourceRoots = getRoots(project, WebClientProjectConstants.SOURCES_TYPE_HTML5);
        List<File> siteRoots = getRoots(project, WebClientProjectConstants.SOURCES_TYPE_HTML5_SITE_ROOT);
        List<File> testRoots = getRoots(project, WebClientProjectConstants.SOURCES_TYPE_HTML5_TEST);
        List<String> localPaths = new ArrayList<>(sourceRoots.size());
        List<String> localPathsExclusionFilter = Collections.EMPTY_LIST;
        for (File src : sourceRoots) {
            localPaths.add(src.getAbsolutePath());
            for (File site : siteRoots) {
                if (isSubdirectoryOf(src, site)) {
                    if (localPathsExclusionFilter.isEmpty()) {
                        localPathsExclusionFilter = new ArrayList<>();
                    }
                    localPathsExclusionFilter.add(site.getAbsolutePath());
                }
            }
            for (File site : testRoots) {
                if (isSubdirectoryOf(src, site)) {
                    if (localPathsExclusionFilter.isEmpty()) {
                        localPathsExclusionFilter = new ArrayList<>();
                    }
                    localPathsExclusionFilter.add(site.getAbsolutePath());
                }
            }
        }
        return new Connector.Properties(host, port, localPaths, Collections.EMPTY_LIST, localPathsExclusionFilter);
    }
    
    private static List<File> getRoots(Project project, String type) {
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(type);
        List<File> roots = new ArrayList<>(sourceGroups.length);
        for (SourceGroup sourceGroup : sourceGroups) {
            FileObject rootFolder = sourceGroup.getRootFolder();
            File root = FileUtil.toFile(rootFolder);
            assert root != null : rootFolder;
            roots.add(root);
        }
        return roots;
    }
    
    private static boolean isSubdirectoryOf(File folder, File child) {
        if (!folder.isDirectory()) {
            return false;
        }
        String fp;
        try {
            fp = folder.getCanonicalPath();
        } catch (IOException ioex) {
            fp = folder.getAbsolutePath();
        }
        String chp;
        try {
            chp = child.getCanonicalPath();
        } catch (IOException ioex) {
            chp = child.getAbsolutePath();
        }
        if (!chp.startsWith(fp)) {
            return false;
        }
        int fl = fp.length();
        if (chp.length() == fl) {
            return true;
        }
        char separ = chp.charAt(fl);
        if (File.separatorChar == separ) {
            return true;
        } else {
            return false;
        }
    }
    
    private static class OutputLineConvertor implements LineConvertor {
        private final TestRunnerReporter testRunnerReporter;
        private final RunInfo runInfo;
        private Runnable debuggerStartTask;

        public OutputLineConvertor(TestRunnerReporter testRunnerReporter, RunInfo runInfo, Runnable debuggerStartTask) {
            this.testRunnerReporter = testRunnerReporter;
            this.runInfo = runInfo;
            this.debuggerStartTask = debuggerStartTask;
        }

        @Override
        public List<ConvertedLine> convert(String line) {
            // debugger?
            if (debuggerStartTask != null
                    && line.startsWith("debugger listening on port")) { // NOI18N
                debuggerStartTask.run();
                debuggerStartTask = null;
            }
            String output2display = testRunnerReporter.processLine(line);
            if(output2display == null) {
                return Collections.EMPTY_LIST;
            }
            TestRunnerReporter.CallStackCallback callStackCallback = new TestRunnerReporter.CallStackCallback(runInfo.getProject());
            Pair<File, int[]> parsedLocation = callStackCallback.parseLocation(line, false);
            FileOutputListener fileOutputListener = parsedLocation == null ? null : new FileOutputListener(parsedLocation.first(), parsedLocation.second()[0], parsedLocation.second()[1]);
            return Collections.singletonList(ConvertedLine.forText(output2display, fileOutputListener));
        }
    }
    
    private static final class FileOutputListener implements OutputListener {

        final File file;
        final int line;
        final int column;

        public FileOutputListener(File file, int line, int column) {
            assert file != null;
            this.file = file;
            this.line = line;
            this.column =column;
        }

        @Override
        public void outputLineSelected(OutputEvent ev) {
            // noop
        }

        @Override
        public void outputLineAction(OutputEvent ev) {
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    Utils.openFile(file, line, column);
                }
            });
        }

        @Override
        public void outputLineCleared(OutputEvent ev) {
            // noop
        }
    }
    
}
