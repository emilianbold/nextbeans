/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.phpunit.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.api.extexecution.print.LineConvertor;
import org.netbeans.api.extexecution.print.LineConvertors;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.phpunit.PhpUnitTestingProvider;
import static org.netbeans.modules.php.phpunit.commands.SkeletonGenerator.validate;
import org.netbeans.modules.php.phpunit.options.PhpUnitOptions;
import org.netbeans.modules.php.phpunit.preferences.PhpUnitPreferences;
import org.netbeans.modules.php.phpunit.preferences.PhpUnitPreferencesValidator;
import org.netbeans.modules.php.phpunit.ui.PhpUnitTestGroupsPanel;
import org.netbeans.modules.php.phpunit.ui.options.PhpUnitOptionsPanelController;
import org.netbeans.modules.php.spi.testing.run.TestRunException;
import org.netbeans.modules.php.spi.testing.run.TestRunInfo;
import org.netbeans.modules.php.spi.testing.run.TestRunInfo.TestInfo;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;

/**
 * PHPUnit 3.4+ support.
 */
public final class PhpUnit {

    private static final Logger LOGGER = Logger.getLogger(PhpUnit.class.getName());

    static final ExecutionDescriptor.LineConvertorFactory PHPUNIT_LINE_CONVERTOR_FACTORY = new PhpUnitLineConvertorFactory();

    public static final String SCRIPT_NAME = "phpunit"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + FileUtils.getScriptExtension(true);
    // for keeping log files to able to evaluate and fix issues
    public static final boolean KEEP_LOGS = Boolean.getBoolean("nb.php.phpunit.keeplogs"); // NOI18N
    // test files suffix
    public static final String TEST_CLASS_SUFFIX = "Test"; // NOI18N
    private static final String TEST_FILE_SUFFIX = TEST_CLASS_SUFFIX + ".php"; // NOI18N
    // test method prefix
    public static final String TEST_METHOD_PREFIX = "test"; // NOI18N
    // suite files suffix
    private static final String SUITE_CLASS_SUFFIX = "Suite"; // NOI18N
    private static final String SUITE_FILE_SUFFIX = SUITE_CLASS_SUFFIX + ".php"; // NOI18N

    // cli options
    private static final String COLORS_PARAM = "--colors"; // NOI18N
    private static final String JUNIT_LOG_PARAM = "--log-junit"; // NOI18N
    private static final String FILTER_PARAM = "--filter"; // NOI18N
    private static final String COVERAGE_LOG_PARAM = "--coverage-clover"; // NOI18N
    private static final String LIST_GROUPS_PARAM = "--list-groups"; // NOI18N
    private static final String GROUP_PARAM = "--group"; // NOI18N
    // bootstrap & config
    private static final String BOOTSTRAP_PARAM = "--bootstrap"; // NOI18N
    private static final String BOOTSTRAP_FILENAME = "bootstrap%s.php"; // NOI18N
    private static final String CONFIGURATION_PARAM = "--configuration"; // NOI18N
    private static final String CONFIGURATION_FILENAME = "configuration%s.xml"; // NOI18N

    private static final List<String> DEFAULT_PARAMS = Arrays.asList(COLORS_PARAM);

    // output files
    public static final File XML_LOG;
    public static final File COVERAGE_LOG;

    // suite file
    private static final String SUITE_NAME = "NetBeansSuite"; // NOI18N
    private static final String SUITE_RUN = "--run=%s"; // NOI18N
    private static final String SUITE_REL_PATH = "phpunit/" + SUITE_NAME + ".php"; // NOI18N

    // generating files
    private static final String DIRNAME_FILE = ".dirname(__FILE__).'/"; // NOI18N

    // run info params
    private static final String PHP_UNIT_GROUPS_PARAM = "phpUnit.groups"; // NOI18N

    private static final char DIRECTORY_SEPARATOR = '/'; // NOI18N

    public static final Pattern LINE_PATTERN = Pattern.compile("(?:.+\\(\\) )?(.+):(\\d+)"); // NOI18N

    // #200489
    private static volatile File suite; // ok if it is fetched more times

    private final String phpUnitPath;


    static {
        // output files, see #200775
        String logDirName = System.getProperty("java.io.tmpdir"); // NOI18N
        String userLogDirName = System.getProperty("nb.php.phpunit.logdir"); // NOI18N
        if (userLogDirName != null) {
            LOGGER.log(Level.INFO, "Custom directory for PhpUnit logs provided: {0}", userLogDirName);
            File userLogDir = new File(userLogDirName);
            if (userLogDir.isDirectory() && FileUtils.isDirectoryWritable(userLogDir)) {
                logDirName = userLogDirName;
            } else {
                LOGGER.log(Level.WARNING, "Directory for PhpUnit logs {0} is not writable directory", userLogDirName);
            }
        }
        LOGGER.log(Level.FINE, "Directory for PhpUnit logs: {0}", logDirName);
        XML_LOG = new File(logDirName, "nb-phpunit-log.xml"); // NOI18N
        COVERAGE_LOG = new File(logDirName, "nb-phpunit-coverage.xml"); // NOI18N
    }


    private PhpUnit(String phpUnitPath) {
        assert phpUnitPath != null;
        this.phpUnitPath = phpUnitPath;
    }

    public static PhpUnit getDefault() throws InvalidPhpExecutableException {
        String script = PhpUnitOptions.getInstance().getPhpUnitPath();
        String error = validate(script);
        if (error != null) {
            throw new InvalidPhpExecutableException(error);
        }
        return new PhpUnit(script);
    }

    @CheckForNull
    public static PhpUnit getForPhpModule(PhpModule phpModule, boolean showCustomizer) {
        // first try the default phpunit, because of validation
        InvalidPhpExecutableException defaultPhpUnitExc = null;
        PhpUnit defaultPhpUnit = null;
        try {
            defaultPhpUnit = PhpUnit.getDefault();
        } catch (InvalidPhpExecutableException ex) {
            defaultPhpUnitExc = ex;
        }
        // now, php module
        try {
            PhpUnit phpUnit = getForPhpModule(phpModule);
            if (phpUnit != null) {
                return phpUnit;
            }
        } catch (InvalidPhpExecutableException ex) {
            if (showCustomizer) {
                phpModule.openCustomizer(PhpUnitTestingProvider.getInstance().getCustomizerCategoryIdent());
                defaultPhpUnitExc = null;
            }
        }
        if (defaultPhpUnitExc != null
                && showCustomizer) {
            UiUtils.invalidScriptProvided(defaultPhpUnitExc.getLocalizedMessage(), PhpUnitOptionsPanelController.OPTIONS_SUB_PATH);
        }
        return defaultPhpUnit;
    }

    @CheckForNull
    private static PhpUnit getForPhpModule(PhpModule phpModule) throws InvalidPhpExecutableException {
        assert phpModule != null;
        String error = validateForModule(phpModule);
        if (error != null) {
            throw new InvalidPhpExecutableException(error);
        }
        if (!PhpUnitPreferences.isPhpUnitEnabled(phpModule)) {
            return null;
        }
        // we have project specific phpunit
        return new PhpUnit(PhpUnitPreferences.getPhpUnitPath(phpModule));
    }

    @NbBundle.Messages("PhpUnit.script.label=PHPUnit script")
    public static String validate(String command) {
        return PhpExecutableValidator.validateCommand(command, Bundle.PhpUnit_script_label());
    }

    public static String validateForModule(PhpModule phpModule) {
        ValidationResult result = new PhpUnitPreferencesValidator()
                .validate(phpModule)
                .getResult();
        if (result.hasErrors()) {
            return result.getErrors().get(0).getMessage();
        }
        if (result.hasWarnings()) {
            return result.getWarnings().get(0).getMessage();
        }
        return null;
    }

    private static File getNbSuite() {
        if (suite == null) {
            suite = InstalledFileLocator.getDefault().locate(SUITE_REL_PATH, "org.netbeans.modules.php.phpunit", false);  // NOI18N
            assert suite != null : "Cannot find NB test suite?!";
        }
        return suite;
    }

    @CheckForNull
    public Integer runTests(PhpModule phpModule, TestRunInfo runInfo) throws TestRunException {
        PhpExecutable phpUnit = getExecutable(phpModule, getOutputTitle(runInfo, PhpUnitPreferences.isCustomSuiteEnabled(phpModule)));
        if (phpUnit == null) {
            return null;
        }

        List<String> params = createParams(true);
        params.add(JUNIT_LOG_PARAM);
        params.add(XML_LOG.getAbsolutePath());
        addBootstrap(phpModule, params);
        addConfiguration(phpModule, params);
        if (runInfo.isCoverageEnabled()) {
            params.add(COVERAGE_LOG_PARAM);
            params.add(COVERAGE_LOG.getAbsolutePath());
        }
        // test groups, not for rerun
        if (PhpUnitPreferences.getAskForTestGroups(phpModule)) {
            List<String> testGroups;
            if (runInfo.isRerun()) {
                @SuppressWarnings("unchecked")
                List<String> savedGroups = runInfo.getParameter(PHP_UNIT_GROUPS_PARAM, List.class);
                testGroups = savedGroups;
            } else {
                // ask user
                List<String> allTestGroups = getTestGroups(phpModule, runInfo);
                if (allTestGroups == null) {
                    // some error
                    return null;
                }
                testGroups = PhpUnitTestGroupsPanel.showDialog(allTestGroups, PhpUnitPreferences.getTestGroups(phpModule));
                if (testGroups != null) {
                    PhpUnitPreferences.setTestGroups(phpModule, testGroups);
                    runInfo.setParameter(PHP_UNIT_GROUPS_PARAM, testGroups);
                }
            }
            if (testGroups != null
                    && !testGroups.isEmpty()) {
                params.add(GROUP_PARAM);
                params.add(StringUtils.implode(testGroups, ",")); // NOI18N
            }
        }
        // custom tests
        List<TestInfo> customTests = runInfo.getCustomTests();
        if (!customTests.isEmpty()) {
            StringBuilder buffer = new StringBuilder(200);
            // delimiter start
            buffer.append("%"); // NOI18N
            for (TestInfo test : customTests) {
                if (buffer.length() > 1) {
                    buffer.append("|"); // NOI18N
                }
                buffer.append("\\b"); // NOI18N
                buffer.append(test.getName());
                buffer.append("\\b"); // NOI18N
            }
            // delimiter end
            buffer.append("%"); // NOI18N
            params.add(FILTER_PARAM);
            params.add(buffer.toString());
            runInfo.resetCustomTests();
        }

        if (PhpUnitPreferences.isCustomSuiteEnabled(phpModule)) {
            // custom suite
            params.add(PhpUnitPreferences.getCustomSuitePath(phpModule));
        } else {
            // standard suite
            // #218607 - hotfix
            //params.add(SUITE_NAME)
            params.add(getNbSuite().getAbsolutePath());
            params.add(String.format(SUITE_RUN, FileUtil.toFile(runInfo.getStartFile()).getAbsolutePath()));
        }

        phpUnit.workDir(getWorkingDirectory(phpModule, runInfo.getWorkingDirectory()))
                .additionalParameters(params);
        try {
            if (runInfo.getSessionType() == TestRunInfo.SessionType.TEST) {
                return phpUnit.runAndWait(getDescriptor(), "Running tests..."); // NOI18N
            }
            return phpUnit.debug(runInfo.getStartFile(), getDescriptor(), null);
        } catch (CancellationException ex) {
            // canceled
            LOGGER.log(Level.FINE, "Test creating cancelled", ex);
        } catch (ExecutionException ex) {
            LOGGER.log(Level.INFO, null, ex);
            UiUtils.processExecutionException(ex, PhpUnitOptionsPanelController.OPTIONS_SUB_PATH);
            throw new TestRunException(ex);
        }
        return null;
    }

    @NbBundle.Messages("PhpUnit.fetch.testGroups=PHPUnit (test-groups)")
    @CheckForNull
    private List<String> getTestGroups(PhpModule phpModule, TestRunInfo runInfo) throws TestRunException {
        PhpExecutable phpUnit = getExecutable(phpModule, Bundle.PhpUnit_fetch_testGroups());
        assert phpUnit != null;

        phpUnit.workDir(FileUtil.toFile(runInfo.getWorkingDirectory()));

        List<String> params = createParams(true);
        addBootstrap(phpModule, params);
        addConfiguration(phpModule, params);
        params.add(LIST_GROUPS_PARAM);
        // list test groups from the current workdir
        params.add("."); // NOI18N
        phpUnit.additionalParameters(params);

        TestGroupsOutputProcessorFactory testGroupsProcessorFactory = new TestGroupsOutputProcessorFactory();
        try {
            phpUnit.runAndWait(getDescriptor(), testGroupsProcessorFactory, "Fetching test groups..."); // NOI18N
            if (!testGroupsProcessorFactory.hasTestGroups()
                    && testGroupsProcessorFactory.hasOutput) {
                // some error
                throw new TestRunException("Test groups cannot be listed. Review Output window for details.");
            }
            return testGroupsProcessorFactory.getTestGroups();
        } catch (CancellationException ex) {
            // canceled
            LOGGER.log(Level.FINE, "Test creating cancelled", ex);
        } catch (ExecutionException ex) {
            LOGGER.log(Level.INFO, null, ex);
            UiUtils.processExecutionException(ex, PhpUnitOptionsPanelController.OPTIONS_SUB_PATH);
            throw new TestRunException(ex);
        }
        return null;
    }

    @CheckForNull
    private PhpExecutable getExecutable(PhpModule phpModule, String title) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            org.netbeans.modules.php.phpunit.ui.UiUtils.warnNoSources(phpModule.getDisplayName());
            return null;
        }

        return new PhpExecutable(phpUnitPath)
                .optionsSubcategory(PhpUnitOptionsPanelController.OPTIONS_SUB_PATH)
                .displayName(title);
    }

    private List<String> createParams(boolean withDefaults) {
        List<String> params = new ArrayList<String>();
        if (withDefaults) {
            params.addAll(DEFAULT_PARAMS);
        }
        return params;
    }

    private void addBootstrap(PhpModule phpModule, List<String> params) {
        if (PhpUnitPreferences.isBootstrapEnabled(phpModule)) {
            params.add(BOOTSTRAP_PARAM);
            params.add(PhpUnitPreferences.getBootstrapPath(phpModule));
        }
    }

    private void addConfiguration(PhpModule phpModule, List<String> params) {
        if (PhpUnitPreferences.isConfigurationEnabled(phpModule)) {
            params.add(CONFIGURATION_PARAM);
            params.add(PhpUnitPreferences.getConfigurationPath(phpModule));
        }
    }

    private ExecutionDescriptor getDescriptor() {
        return PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR
                .optionsPath(PhpUnitOptionsPanelController.OPTIONS_PATH)
                .controllable(false)
                .frontWindow(false)
                .frontWindowOnError(false)
                .inputVisible(false)
                .outConvertorFactory(PHPUNIT_LINE_CONVERTOR_FACTORY)
                .preExecution(new Runnable() {
                    @Override
                    public void run() {
                        cleanupLogFiles();
                    }
                });
    }

    // #170120
    private File getWorkingDirectory(PhpModule phpModule, FileObject defaultWorkDir) {
        if (PhpUnitPreferences.isConfigurationEnabled(phpModule)) {
            return new File(PhpUnitPreferences.getConfigurationPath(phpModule)).getParentFile();
        }
        return FileUtil.toFile(defaultWorkDir);
    }

    @NbBundle.Messages({
        "PhpUnit.run.test.single=PHPUnit (test)",
        "PhpUnit.run.test.single.custom=PHPUnit (test, custom)",
        "PhpUnit.run.test.all=PHPUnit (test all)",
        "PhpUnit.run.test.all.custom=PHPUnit (test all, custom)",
        "PhpUnit.debug.single=PHPUnit (debug)",
        "PhpUnit.debug.single.custom=PHPUnit (debug, custom)",
        "PhpUnit.debug.all=PHPUnit (debug all)",
        "PhpUnit.debug.all.custom=PHPUnit (debug all, custom)"
    })
    private String getOutputTitle(TestRunInfo runInfo, boolean customSuiteEnabled) {
        boolean allTests = runInfo.allTests();
        switch (runInfo.getSessionType()) {
            case TEST:
                if (allTests && customSuiteEnabled) {
                    return Bundle.PhpUnit_run_test_all_custom();
                } else if (allTests) {
                    return Bundle.PhpUnit_run_test_all();
                } else if (customSuiteEnabled) {
                    return Bundle.PhpUnit_run_test_single_custom();
                }
                return Bundle.PhpUnit_run_test_single();
                //break;
            case DEBUG:
                if (allTests && customSuiteEnabled) {
                    return Bundle.PhpUnit_debug_all_custom();
                } else if (allTests) {
                    return Bundle.PhpUnit_debug_all();
                } else if (customSuiteEnabled) {
                    return Bundle.PhpUnit_debug_single_custom();
                }
                return Bundle.PhpUnit_debug_single();
                //break;
            default:
                throw new IllegalStateException("Unknown session type: " + runInfo.getSessionType());
        }
    }

    void cleanupLogFiles() {
        if (PhpUnit.XML_LOG.exists()) {
            if (!PhpUnit.XML_LOG.delete()) {
                LOGGER.log(Level.INFO, "Cannot delete PHPUnit log {0}", PhpUnit.XML_LOG);
            }
        }
        if (PhpUnit.COVERAGE_LOG.exists()) {
            if (!PhpUnit.COVERAGE_LOG.delete()) {
                LOGGER.log(Level.INFO, "Cannot delete code coverage log {0}", PhpUnit.COVERAGE_LOG);
            }
        }
    }

    //~ Static helper methods

    public static boolean isTestFile(String fileName) {
        return !fileName.equals(PhpUnit.TEST_FILE_SUFFIX) && fileName.endsWith(PhpUnit.TEST_FILE_SUFFIX);
    }

    public static boolean isTestClass(String className) {
        return !className.equals(PhpUnit.TEST_CLASS_SUFFIX) && className.endsWith(PhpUnit.TEST_CLASS_SUFFIX);
    }

    public static boolean isTestMethod(String methodName) {
        return !methodName.equals(PhpUnit.TEST_METHOD_PREFIX) && methodName.startsWith(PhpUnit.TEST_METHOD_PREFIX);
    }

    public static boolean isSuiteFile(String fileName) {
        return !fileName.equals(PhpUnit.SUITE_FILE_SUFFIX) && fileName.endsWith(PhpUnit.SUITE_FILE_SUFFIX);
    }

    public static boolean isSuiteClass(String className) {
        return !className.equals(PhpUnit.SUITE_CLASS_SUFFIX) && className.endsWith(PhpUnit.SUITE_CLASS_SUFFIX);
    }

    public static boolean isTestOrSuiteFile(String fileName) {
        return isTestFile(fileName) || isSuiteFile(fileName);
    }

    public static boolean isTestOrSuiteClass(String className) {
        return isTestClass(className) || isSuiteClass(className);
    }

    public static String getTestedClass(String testOrSuiteClass) {
        assert isTestOrSuiteClass(testOrSuiteClass) : "Not Test or Suite class: " + testOrSuiteClass;
        int lastIndexOf = -1;
        if (isTestClass(testOrSuiteClass)) {
            lastIndexOf = testOrSuiteClass.lastIndexOf(PhpUnit.TEST_CLASS_SUFFIX);
        } else if (isSuiteClass(testOrSuiteClass)) {
            lastIndexOf = testOrSuiteClass.lastIndexOf(PhpUnit.SUITE_CLASS_SUFFIX);
        }
        assert lastIndexOf != -1;
        return testOrSuiteClass.substring(0, lastIndexOf);
    }

    public static String makeTestFile(String testedFileName) {
        return testedFileName + PhpUnit.TEST_FILE_SUFFIX;
    }

    public static String makeTestClass(String testedClass) {
        return testedClass + PhpUnit.TEST_CLASS_SUFFIX;
    }

    public static String makeSuiteFile(String testedFileName) {
        return testedFileName + PhpUnit.SUITE_FILE_SUFFIX;
    }

    public static String makeSuiteClass(String testedClass) {
        return testedClass + PhpUnit.SUITE_CLASS_SUFFIX;
    }

    public static File createBootstrapFile(final PhpModule phpModule) {
        FileObject testDirectory = phpModule.getTestDirectory();
        assert testDirectory != null : "Test directory must already be set";

        final FileObject configFile = FileUtil.getConfigFile("Templates/Scripting/Tests/PHPUnitBootstrap.php"); // NOI18N
        final DataFolder dataFolder = DataFolder.findFolder(testDirectory);
        final File bootstrapFile = new File(getBootstrapFilepath(testDirectory));
        final File[] files = new File[1];
        FileUtil.runAtomicAction(new Runnable() {
            @Override
            public void run() {
                try {
                    DataObject dataTemplate = DataObject.find(configFile);
                    DataObject bootstrap = dataTemplate.createFromTemplate(dataFolder, bootstrapFile.getName() + "~"); // NOI18N
                    assert bootstrap != null;
                    moveAndAdjustBootstrap(phpModule, FileUtil.toFile(bootstrap.getPrimaryFile()), bootstrapFile);
                    assert bootstrapFile.isFile();
                    files[0] = bootstrapFile;
                    informAboutGeneratedFile(bootstrapFile.getName());
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Cannot create PHPUnit bootstrap file", ex);
                }
            }
        });
        if (files[0] == null) {
            // no file generated
            warnAboutNotGeneratedFile(bootstrapFile.getName());
        }
        return files[0];
    }

    private static void moveAndAdjustBootstrap(PhpModule phpModule, File tmpBootstrap, File finalBootstrap) {
        try {
            // input
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tmpBootstrap), "UTF-8")); // NOI18N
            try {
                // output
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(finalBootstrap), "UTF-8")); // NOI18N
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.contains("%INCLUDE_PATH%")) { // NOI18N
                            if (line.startsWith("//")) { // NOI18N
                                // comment about %INCLUDE_PATH%, let's skip it
                                continue;
                            }
                            List<String> includePath = phpModule.getProperties().getIncludePath();
                            assert includePath != null : "Include path should be always present";
                            line = processIncludePath(
                                    finalBootstrap,
                                    line,
                                    includePath,
                                    FileUtil.toFile(phpModule.getProjectDirectory()));
                        }
                        out.write(line);
                        out.newLine();
                    }
                } finally {
                    out.flush();
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        if (!tmpBootstrap.delete()) {
            LOGGER.log(Level.INFO, "Cannot delete temporary file {0}", tmpBootstrap);
            tmpBootstrap.deleteOnExit();
        }

        FileUtil.refreshFor(finalBootstrap.getParentFile());
    }

    static String processIncludePath(File bootstrap, String line, List<String> includePath, File projectDir) {
        String resolvedIncludePath = ""; // NOI18N
        if (!includePath.isEmpty()) {
            StringBuilder buffer = new StringBuilder(200);
            for (String path : includePath) {
                // XXX perhaps already resolved paths should be here?
                File reference = PropertyUtils.resolveFile(projectDir, path);
                buffer.append(".PATH_SEPARATOR"); // NOI18N
                buffer.append(getDirnameFile(bootstrap, reference));
            }
            resolvedIncludePath = buffer.toString();
        } else {
            // comment out the line
            line = "//" + line; // NOI18N
        }
        line = line.replace("%INCLUDE_PATH%", resolvedIncludePath); // NOI18N
        return line;
    }

    public static File createConfigurationFile(PhpModule phpModule) {
        FileObject testDirectory = phpModule.getTestDirectory();
        assert testDirectory != null : "Test directory must already be set";

        final FileObject configFile = FileUtil.getConfigFile("Templates/Scripting/Tests/PHPUnitConfiguration.xml"); // NOI18N
        final DataFolder dataFolder = DataFolder.findFolder(testDirectory);
        final File configurationFile = new File(getConfigurationFilepath(testDirectory));
        File file = null;
        try {
            DataObject dataTemplate = DataObject.find(configFile);
            DataObject configuration = dataTemplate.createFromTemplate(dataFolder, configurationFile.getName().replace(".xml", "")); // NOI18N
            assert configuration != null;
            file = configurationFile;
            informAboutGeneratedFile(configurationFile.getName());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Cannot create PHPUnit configuration file", ex);
        }
        if (file == null) {
            // no file generated
            warnAboutNotGeneratedFile(configurationFile.getName());
            return null;
        }
        FileUtil.refreshFor(file.getParentFile());
        return file;
    }

    @NbBundle.Messages({
        "# {0} - file name",
        "PhpUnit.generating.success=Following file was generated in project''s test directory:\n\n{0}\n\nThe file is used for running tests so review and modify it if needed."
    })
    public static void informAboutGeneratedFile(String generatedFile) {
        DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(Bundle.PhpUnit_generating_success(generatedFile)));
    }

    @NbBundle.Messages({
        "# {0} - file name",
        "PhpUnit.generating.failure={0} file was not generated, review IDE log for more information."
    })
    private static void warnAboutNotGeneratedFile(String file) {
        NotifyDescriptor warning = new NotifyDescriptor.Message(
                Bundle.PhpUnit_generating_failure(file),
                NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notifyLater(warning);
    }

    private static String getDirnameFile(File testFile, File sourceFile) {
        return getRelPath(testFile, sourceFile, ".'", DIRNAME_FILE, "'"); // NOI18N
    }

    // XXX improve this and related method
    private static String getRelPath(File testFile, File sourceFile, String absolutePrefix, String relativePrefix, String suffix) {
        return getRelPath(testFile, sourceFile, absolutePrefix, relativePrefix, suffix, false);
    }

    // forceAbsolute only for unit tests
    static String getRelPath(File testFile, File sourceFile, String absolutePrefix, String relativePrefix, String suffix, boolean forceAbsolute) {
        File parentFile = testFile.getParentFile();
        String relPath = PropertyUtils.relativizeFile(parentFile, sourceFile);
        if (relPath == null || forceAbsolute) {
            // cannot be versioned...
            relPath = absolutePrefix + sourceFile.getAbsolutePath() + suffix;
        } else {
            relPath = relativePrefix + relPath + suffix;
        }
        return relPath.replace(File.separatorChar, DIRECTORY_SEPARATOR);
    }

    private static String getBootstrapFilepath(FileObject testDirectory) {
        return getFilepath(testDirectory, BOOTSTRAP_FILENAME);
    }

    private static String getConfigurationFilepath(FileObject testDirectory) {
        return getFilepath(testDirectory, CONFIGURATION_FILENAME);
    }

    private static String getFilepath(FileObject testDirectory, String filename) {
        assert testDirectory != null : "Test directory must already be set";

        File tests = FileUtil.toFile(testDirectory);
        File file = null;
        int i = 0;
        do {
            file = new File(tests, getFilename(filename, i++));
        } while (file.isFile());
        assert !file.isFile();
        return file.getAbsolutePath();
    }

    private static String getFilename(String filename, int i) {
        return String.format(filename, i == 0 ? "" : i); // NOI18N
    }

    //~ Inner classes

    static final class PhpUnitLineConvertorFactory implements ExecutionDescriptor.LineConvertorFactory {
        @Override
        public LineConvertor newLineConvertor() {
            return LineConvertors.filePattern(null, PhpUnit.LINE_PATTERN, null, 1, 2);
        }

    }

    private static final class TestGroupsOutputProcessorFactory implements ExecutionDescriptor.InputProcessorFactory {

        private final Pattern testGroupName = Pattern.compile("^\\s-\\s(.*)$"); // NOI18N
        private final List<String> testGroups = Collections.synchronizedList(new ArrayList<String>());

        private volatile boolean hasOutput = false;


        @Override
        public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
            return InputProcessors.bridge(new LineProcessor() {
                @Override
                public void processLine(String line) {
                    hasOutput = true;
                    Matcher matcher = testGroupName.matcher(line);
                    if (matcher.matches()) {
                        testGroups.add(matcher.group(1).trim());
                    }
                }
                @Override
                public void reset() {
                }
                @Override
                public void close() {
                }
            });
        }

        public List<String> getTestGroups() {
            return testGroups;
        }

        public boolean hasTestGroups() {
            return !testGroups.isEmpty();
        }

        public boolean hasOutput() {
            return hasOutput;
        }

    }

}
