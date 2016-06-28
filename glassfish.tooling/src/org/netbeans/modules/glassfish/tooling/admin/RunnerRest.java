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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import static org.netbeans.modules.glassfish.tooling.admin.Runner.PARAM_SEPARATOR;
import org.netbeans.modules.glassfish.tooling.admin.response.ResponseContentType;
import org.netbeans.modules.glassfish.tooling.admin.response.ResponseParserFactory;
import org.netbeans.modules.glassfish.tooling.admin.response.RestActionReport;
import org.netbeans.modules.glassfish.tooling.admin.response.RestResponseParser;
import org.netbeans.modules.glassfish.tooling.data.GlassFishServer;
import org.netbeans.modules.glassfish.tooling.utils.ServerUtils;

/**
 * GlassFish server administration command execution using REST interface.
 * <p/>
 * Class implements GlassFish server administration functionality trough REST
 * interface.
 * <p/>
 * @author Tomas Kraus, Peter Benedikovic
 */
public class RunnerRest extends Runner {

    ////////////////////////////////////////////////////////////////////////////
    // Instance attributes                                                    //
    ////////////////////////////////////////////////////////////////////////////

    /** Parser for returned response. Default response is XML. */
    RestResponseParser parser = ResponseParserFactory.getRestParser(getResponseType());

    /** Object representation of returned response.*/
    RestActionReport report;

    /**
     * GlassFish administration command result.
     * <p/>
     * Result instance life cycle is started with submitting task into
     * <code>ExecutorService</code>'s queue. method <code>call()</code>
     * is responsible for correct <code>TaskState</code> and receiveResult value
     * handling.
     */
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    protected ResultString result;

    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an instance of administration command executor using
     * REST interface.
     * <p/>
     * @param server  GlassFish server entity object.
     * @param command GlassFish server administration command entity.
     */
    public RunnerRest(final GlassFishServer server, final Command command) {
        this(server, command, null);
    }


    /**
     * Constructs an instance of administration command executor using
     * REST interface.
     * <p/>
     * @param server  GlassFish server entity object.
     * @param command GlassFish server administration command entity.
     * @param query   Query string for this command.
     */
    public RunnerRest(final GlassFishServer server, final Command command,
            final String query) {
        this(server, command, "/command/", query);
    }
    
    /**
     * Constructs an instance of administration command executor using
     * REST interface.
     * <p/>
     * @param server  GlassFish server entity object.
     * @param command GlassFish server administration command entity.
     * @param path    path which builds URL we speak to.
     * @param query   Query string for this command.
     */
    public RunnerRest(final GlassFishServer server, final Command command,
            final String path, final String query) {
        super(server, command, path, query);
        silentFailureAllowed = false;
    }

    /**
     * Helper methods that appends java.util.Properties into
     * POST request body.
     * @param sb
     * @param properties
     * @param paramName
     * @param separator 
     */
    protected void appendProperties(StringBuilder sb,
            final Map<String,String> properties, final String paramName,
            final boolean separator) {
        if (null != properties && properties.size() > 0) {
            Set<Map.Entry<String,String>> entrySet = properties.entrySet();
            boolean first = true;
            if (separator) {
                sb.append(PARAM_SEPARATOR);
            }
            sb.append(paramName).append(PARAM_ASSIGN_VALUE);
            for (Map.Entry<String,String> entry : entrySet) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (first) {
                    first = false;
                }
                else {
                    sb.append(ITEM_SEPARATOR);
                }
                sb.append(key);
                sb.append(PARAM_ASSIGN_VALUE);
                if (val != null) {
                    sb.append(val);
                }
            }
        }
    }
    
    protected void appendIfNotEmpty(StringBuilder sb, String paramName, String paramValue) {
        if ((paramValue != null) && (!paramValue.isEmpty())) {
            sb.append(paramName);
            sb.append(PARAM_ASSIGN_VALUE);
            sb.append(paramValue);
        }
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
     * Do not send information to the server via HTTP POST by default.
     * <p/>
     * @return <code>true</code> if using HTTP POST to send to server
     *         or <code>false</code> otherwise
     */
    @Override
    public boolean getDoOutput() {
        return true;
    }

    /**
     * Inform whether this runner implementation accepts gzip format.
     * <p/>
     * @return <code>true</code> when gzip format is accepted,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean acceptsGzip() {
        return false;
    }

    /**
     * Build Glassfish REST command URL.
     * <p/>
     * @return <code>String</code> value containing command URL.
     * @throws <code>CommandException</code> if there is a problem with building
     *         command URL.
     */
    @Override
    protected String constructCommandUrl() throws CommandException {
        String protocol = "http";
        URI uri;
        try {
            uri = new URI(protocol, null, server.getHost(),
                    server.getAdminPort(), path + command.getCommand(),
                    query, null);
        } catch (URISyntaxException use) {
            throw new CommandException(CommandException.RUNNER_HTTP_URL, use);
        }
        return uri.toASCIIString();
    }

    /**
     * Override to change the type of HTTP method used for this command.
     * Default is GET.
     * <p/>
     * @return HTTP method (GET, POST, etc.)
     */
    @Override
    protected String getRequestMethod() {
        return "POST"; // NOI18N
    }
    
   /*
     * Handle sending data to server using REST command interface.
     */
    @Override
    protected void handleSend(HttpURLConnection hconn) throws IOException {
        // Do nothing.
    }

    /**
     * Override this method to read response from provided input stream. <p/>
     * Override to read the response data sent by the server. Do not close the
     * stream parameter when finished. Caller will take care of that. <p/>
     * <p/>
     * @param in Stream to read data from.
     * @return <code>true</code> if response was read correctly.
     * @throws java.io.IOException in case of stream error.
     */
    @Override
    protected boolean readResponse(InputStream in, HttpURLConnection hconn) {
        report = parser.parse(in);
        return isSuccess();
    }

    /**
     * Override to parse, validate, and/or format any data read from the server
     * in readResponse() / readManifest().
     * <p/>
     * @return <code>true</code> if data was processed correctly.
     */
    @Override
    protected boolean processResponse() {
        result.value = report.getMessage();
        return isSuccess();
    }

    /**
     * Added to give descendants possibility to decide what report state has
     * successful meaning.
     * <p/>
     * Default is to delegate decision to report itself.
     * <p/>
     * @return Does report state have successful meaning?
     */
    protected boolean isSuccess() {
        return report.isSuccess();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Methods                                                                //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Prepare headers for HTTP connection.
     * <p/>
     * @param conn Target HTTP connection.
     * @throws <code>CommandException</code> if there is a problem with setting
     *         the headers.
     */
    @Override
    protected void prepareHttpConnection(HttpURLConnection conn)
            throws CommandException {
        super.prepareHttpConnection(conn);
        // from gf4 every command has to be authenticated
        String adminUser = server.getAdminUser();
        String adminPassword = server.getAdminPassword();
        if (adminUser != null && adminUser.length() > 0) {
            adminPassword = (adminPassword != null
                    && adminPassword.length() > 0) ? adminPassword : "";
            String authCredentials = ServerUtils.basicAuthCredentials(
                    adminUser, adminPassword);
            conn.setRequestProperty(
                    "Authorization", "Basic " + authCredentials);
        }
        conn.setRequestProperty("Accept", getResponseType().toString());
        conn.addRequestProperty("X-Requested-By",
                "GlassFish REST HTML interface");
    }

    /**
     * Method returns content type in which the server is asked to return
     * the response.
     * <p/>
     * @return <code>ContentType</code> that runner wants to get from server.
     */
    protected ResponseContentType getResponseType() {
        return ResponseContentType.APPLICATION_JSON;
    }

}
