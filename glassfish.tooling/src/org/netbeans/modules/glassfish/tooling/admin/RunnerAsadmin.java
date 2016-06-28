/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015, 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.glassfish.tooling.admin;

import org.netbeans.modules.glassfish.tooling.TaskState;
import org.netbeans.modules.glassfish.tooling.TaskEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import static org.netbeans.modules.glassfish.tooling.admin.RunnerJava.getServerConfig;
import static org.netbeans.modules.glassfish.tooling.admin.RunnerJava.setJavaEnvironment;
import org.netbeans.modules.glassfish.tooling.data.GlassFishServer;
import org.netbeans.modules.glassfish.tooling.data.ToolConfig;
import org.netbeans.modules.glassfish.tooling.data.ToolsConfig;
import org.netbeans.modules.glassfish.tooling.logging.Logger;
import org.netbeans.modules.glassfish.tooling.utils.OsUtils;
import org.netbeans.modules.glassfish.tooling.utils.ServerUtils;

/**
 * GlassFish server administration command execution using local
 * asadmin interface.
 * <p/>
 * Class implements GlassFish server administration functionality command line
 * asadmin interface.
 * <p/>
 * @author Tomas Kraus, Peter Benedikovic
 */
public abstract class RunnerAsadmin extends RunnerJava {

    ////////////////////////////////////////////////////////////////////////////
    // Class attributes                                                       //
    ////////////////////////////////////////////////////////////////////////////

    /** Logger instance for this class. */
    private static final Logger LOGGER = new Logger(RunnerAsadmin.class);

    /** Specifies the administrator username. */
    private static final String USER_PARAM = "--user";

    /** Specifies the name, including the full path, of a file that contains 
     *  password entries. */
    private static final String PASSWORD_FILE_PARAM = "--passwordfile";

    ////////////////////////////////////////////////////////////////////////////
    // Static methods                                                         //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Extracts asadmin JAR path from GlassFish server entity object.
     * <p/>
     * @param server GlassFish server entity object.
     * @return Asadmin JAR path to execute.
     */
    private static String extractPath(final GlassFishServer server) {
        final String METHOD = "extractPath";
        ToolsConfig toolsConfig = getServerConfig(server).getTools();
        if (toolsConfig == null) {
            throw new CommandException(LOGGER.excMsg(METHOD, "noToolsConfig"),
                    server.getVersion());
        }
        ToolConfig asadmin = toolsConfig.getAsadmin();
        if (asadmin == null) {
            throw new CommandException(LOGGER.excMsg(METHOD, "noAsadminConfig"),
                    server.getVersion());
        }
        String serverHome = server.getServerHome();
        String asadminJar = asadmin.getJar();
        StringBuilder sb = new StringBuilder(serverHome.length()
                + OsUtils.FILE_SEPARATOR_LENGTH + asadminJar.length());
        sb.append(serverHome);
        ServerUtils.addPathElement(sb, asadminJar);
        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Instance attributes                                                    //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * GlassFish administration command result.
     * <p/>
     * Result instance life cycle is started with submitting task into
     * <code>ExecutorService</code>'s queue. Method <code>call()</code>
     * is responsible for correct <code>TaskState</code> and receiveResult value
     * handling.
     */
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    protected ResultString result;

    /** Asadmin JAR path  (relative under GlassFish home). */
    private final String asadminJar;

    /** Process standard input. */
    protected Writer stdIn;

    /** Process standard output. */
    protected Reader stdOut;

    /** Process standard error output. */
    protected Reader stdErr;

    /** Process IO processing. */
    protected ProcessIOContent processIO;

    /** Support for <code>asadmin</code> <code>--passwordfile</code>
     *  file format. */
    protected PasswordFile passwordFile;

    ////////////////////////////////////////////////////////////////////////////
    // Abstract methods                                                       //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Create internal <code>ProcessIOContent</code> object corresponding
     * to command execution IO.
     */
    protected abstract ProcessIOContent createProcessIOContent();

    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an instance of administration command executor using
     * command line asadmin interface.
     * <p/>
     * @param server  GlassFish server entity object.
     * @param command GlassFish server administration command entity.
     * @param query   Query string for this command.
     */
    public RunnerAsadmin(final GlassFishServer server, final Command command,
            final String query) {
        super(server, command, query);
        asadminJar = extractPath(server);
        stdIn = null;
        stdOut = null;
        stdErr = null;
        passwordFile = new PasswordFile(server);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implemented Abstract Methods                                           //
    ////////////////////////////////////////////////////////////////////////////

   /**
     * Create <code>ResultString</code> object corresponding
     * to <code>String</code>command execution value to be returned.
     * <p/>
     * @return <code>String</code>command execution value to be returned.
     */
    @Override
    protected Result createResult() {
        return result = new ResultString();
    }

    /**
     * Reads response from server and stores internally.
     * <p/>
     * @param in Stream to read data from.
     * @return Always returns <code>false</code>.
     * @throws CommandException in case of stream error.
     */
    @Override
    protected boolean readResponse(final InputStream in,
            final HttpURLConnection hconn) {
        return false;
    }

    /**
     * Extracts result value from internal storage.
     * <p/>
     * @return Always returns <code>false</code>.
     */
    @Override
    protected boolean processResponse() {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ExecutorService call() method helpers                                  //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Build process execution arguments.
     * <p/>
     * @return Process execution arguments.
     */
    String buildProcessArgs() {
        final String METHOD = "buildProcessArgs";
        String user = server.getAdminUser();
        String passwordFilePath = passwordFile.getFilePath();
        StringBuilder sb = new StringBuilder(JAR_PARAM.length() + 1
                + asadminJar.length() + 1 + USER_PARAM.length() + 1
                + user.length() + 1 + PASSWORD_FILE_PARAM.length() + 1
                + passwordFilePath.length() + 1
                + command.command.length() + 1
                + query.length());
        sb.append(JAR_PARAM);
        sb.append(PARAM_ASSIGN_VALUE);
        sb.append(asadminJar);
        sb.append(PARAM_SEPARATOR);
        sb.append(USER_PARAM);
        sb.append(PARAM_ASSIGN_VALUE);
        sb.append(user);
        sb.append(PARAM_SEPARATOR);
        sb.append(PASSWORD_FILE_PARAM);
        sb.append(PARAM_ASSIGN_VALUE);
        sb.append(passwordFilePath);
        sb.append(PARAM_SEPARATOR);
        sb.append(command.command);
        sb.append(QUERY_SEPARATOR);
        sb.append(query);
        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Methods                                                                //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Execute an arbitrary server command.
     * <p/>
     * @return <code>Future</code> object to retrieve receiveResult of
     * asynchronous execution.
     */
    @Override
    Future<? extends Result> execute() {
        processIO = createProcessIOContent();
        return super.execute();
    }

    /**
     * Execute an arbitrary server command using provided executor.
     * <p/>
     * @return <code>Future</code> object to retrieve receiveResult of
     * asynchronous execution.
     */
    @Override
    Future<? extends Result> execute(final ExecutorService executor) {
        processIO = createProcessIOContent();
        return super.execute(executor);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ExecutorService call() method                                          //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * GlassFish server administration command execution call.
     * This is an entry point from <code>executor<code>'s serialization queue.
     * <p/>
     * Attempts to start local GalssFish DAS directly using <code>java</code>
     * executable.
     * <p/>
     * @return Task execution state.
     */
    @Override
    public Result call() {
        if (!verifyJavaVM()) {
            return handleStateChange(TaskState.FAILED,
                    TaskEvent.NO_JAVA_VM, command.getCommand(),
                    server.getName());
        }
        passwordFile.write();
        Process process;
        String[] args = OsUtils.parseParameters(javaVMExe, buildProcessArgs());
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        setProcessCurrentDir(pb);
        setJavaEnvironment(pb.environment(), command);
        try {
            process = pb.start();
        } catch (IOException ex) {
            return handleStateChange(TaskState.FAILED,
                    TaskEvent.JAVA_VM_EXEC_FAILED, command.getCommand(),
                    server.getName());
        }
        ProcessIOParser parser = new ProcessIOParser(
                new OutputStreamWriter(process.getOutputStream()),
                new InputStreamReader(process.getInputStream()),
                processIO);
        ProcessIOResult verifyResult;
        try {
            verifyResult = parser.verify();
            result.value = parser.getOutput();
        } catch (IOException ex) {
            return handleStateChange(TaskState.FAILED,
                    TaskEvent.EXCEPTION, command.getCommand(),
                    server.getName());
        }
        switch (verifyResult) {
            case SUCCESS:
                return handleStateChange(TaskState.COMPLETED,
                        TaskEvent.CMD_COMPLETED, command.getCommand(),
                        server.getName());
            default:
                return handleStateChange(TaskState.FAILED,
                        TaskEvent.CMD_FAILED, command.getCommand(),
                        server.getName());
        }
    }

}
