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

package org.netbeans.modules.j2ee.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;

/**
 * An utility class for working with data sources.
 *
 * @author Andrei Badea
 *
 * @since 1.7
 */
public class DatasourceHelper {

    private DatasourceHelper() {
    }

    /**
     * Finds the database connections whose database URL and user name equal
     * the database URL and the user name of the passed data source.
     *
     * @param  datasource the data source.
     *
     * @return the list of database connections; never null.
     *
     * @throws NullPointerException if the datasource parameter was null.
     */
    public static List<DatabaseConnection> findDatabaseConnections(Datasource datasource) {
        if (datasource == null) {
            throw new NullPointerException("The datasource parameter cannot be null."); // NOI18N
        }
        String databaseUrl = datasource.getUrl();
        String user = datasource.getUsername();
        if (databaseUrl == null || user == null) {
            return Collections.emptyList();
        }
        List<DatabaseConnection> result = new ArrayList<DatabaseConnection>();
        for (DatabaseConnection dbconn : ConnectionManager.getDefault().getConnections()) {
            if (databaseUrl.equals(dbconn.getDatabaseURL()) && user.equals(dbconn.getUser())) {
                result.add(dbconn);
            }
        }
        if (result.size() > 0) {
            return Collections.unmodifiableList(result);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Finds the data source with the given JNDI name in the module and
     * project data sources of the given provider.
     *
     * @param  provider the {@link J2eeModuleProvider provider} whose data sources 
     *         are to be searched; cannot be null.
     * @param  jndiName the JNDI name to search for; cannot be null.
     *
     * @return the found data source or null if no data source was found.
     *
     * @throws NullPointerException if either the <code>provider</code>
     *         or the <code>jndiName</code> parameter was null.
     *
     * @since 1.11
     */
    public static Datasource findDatasource(J2eeModuleProvider provider, String jndiName) throws ConfigurationException {
        if (provider == null) {
            throw new NullPointerException("The provider parameter cannot be null."); // NOI18N
        }
        if (jndiName == null) {
            throw new NullPointerException("The jndiName parameter cannot be null."); // NOI18N
        }
        for (Datasource datasource : provider.getServerDatasources()) {
            if (jndiName.equals(datasource.getJndiName())) {
                return datasource;
            }
        }
        for (Datasource datasource : provider.getModuleDatasources()) {
            if (jndiName.equals(datasource.getJndiName())) {
                return datasource;
            }
        }
        return null;
    }
}
