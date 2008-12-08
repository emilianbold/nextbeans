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
package org.netbeans.modules.db.dataview.output;

import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import javax.swing.table.TableModel;
import org.netbeans.modules.db.dataview.meta.DBColumn;
import org.netbeans.modules.db.dataview.meta.DBConnectionFactory;
import org.netbeans.modules.db.dataview.meta.DBException;
import org.netbeans.modules.db.dataview.meta.DBMetaDataFactory;
import org.netbeans.modules.db.dataview.meta.DBPrimaryKey;
import org.netbeans.modules.db.dataview.meta.DBTable;
import org.netbeans.modules.db.dataview.util.DataViewUtils;
import org.openide.util.NbBundle;

/**
 * Generates DML for editable resultset
 *
 * @author Ahimanikya Satapathy
 */
class SQLStatementGenerator {

    private DataViewDBTable tblMeta;
    private DataView dataView;

    public SQLStatementGenerator(DataView dataView) {
        this.dataView = dataView;
        this.tblMeta = dataView.getDataViewDBTable();
    }

    // TODO: Generated by default, can be overwitten by user, allow that
    String[] generateInsertStatement(Object[] insertedRow) throws DBException {
        StringBuilder insertSql = new StringBuilder();
        StringBuilder rawInsertSql = new StringBuilder();
        insertSql.append("INSERT INTO "); // NOI18N
        rawInsertSql.append(insertSql.toString());

        String colNames = " ("; // NOI18N
        String values = "";     // NOI18N
        String rawvalues = "";  // NOI18N
        String commaStr = ", "; // NOI18N
        boolean comma = false;
        for (int i = 0; i < insertedRow.length; i++) {
            DBColumn dbcol = tblMeta.getColumn(i);
            if (dbcol.isGenerated() || (insertedRow[i] != null && insertedRow[i].equals("<DEFAULT>"))) { // NOI18N
                continue;
            }

            if (insertedRow[i] == null && !dbcol.isNullable()) {
                throw new DBException(NbBundle.getMessage(SQLStatementGenerator.class, "MSG_nullable_check"));
            }

            if (comma) {
                values += commaStr;
                rawvalues += commaStr;
                colNames += commaStr;
            } else {
                comma = true;
            }

            values += insertedRow[i] == null ? " NULL " : "?"; // NOI18N
            rawvalues += getQualifiedValue(dbcol.getJdbcType(), insertedRow[i]);
            colNames += dbcol.getQualifiedName();
        }

        colNames += ")"; // NOI18N
        String tableName = tblMeta.getFullyQualifiedName(0);
        insertSql.append(tableName + colNames + " Values(" + values + ")"); // NOI18N
        rawInsertSql.append(tableName.trim() + "\n\t" + colNames + " \nVALUES \n\t(" + rawvalues + ")"); // NOI18N

        return new String[]{insertSql.toString(), rawInsertSql.toString()};
    }

    String[] generateUpdateStatement(int row, int col, Object value, List<Object> values, List<Integer> types, TableModel tblModel) throws DBException {
        DBColumn dbcol = tblMeta.getColumn(col);
        int type = dbcol.getJdbcType();

        if (!dbcol.isNullable() && value == null) {
            throw new DBException(NbBundle.getMessage(SQLStatementGenerator.class, "MSG_nullable_check"));
        }

        StringBuilder updateStmt = new StringBuilder();
        StringBuilder rawUpdateStmt = new StringBuilder();

        updateStmt.append("UPDATE ").append(tblMeta.getFullyQualifiedName(0)).append(" SET "); // NOI18N

        rawUpdateStmt.append(updateStmt.toString()).append(tblMeta.getQualifiedName(col)).append(" = ");
        rawUpdateStmt.append(getQualifiedValue(type, value).toString()).append(" WHERE "); // NOI18N

        updateStmt.append(tblMeta.getQualifiedName(col)).append(" = ? WHERE "); // NOI18N
        values.add(value);
        types.add(type);

        generateWhereCondition(updateStmt, rawUpdateStmt, types, values, row, tblModel);

        return new String[]{updateStmt.toString(), rawUpdateStmt.toString()};
    }

    String[] generateDeleteStatement(List<Integer> types, List<Object> values, int rowNum, TableModel tblModel) {
        StringBuilder deleteStmt = new StringBuilder();
        StringBuilder rawDeleteStmt = new StringBuilder();

        deleteStmt.append("DELETE FROM ").append(tblMeta.getFullyQualifiedName(0)).append(" WHERE "); // NOI18N
        rawDeleteStmt.append(deleteStmt.toString());

        generateWhereCondition(deleteStmt, rawDeleteStmt, types, values, rowNum, tblModel);
        return new String[]{deleteStmt.toString(), rawDeleteStmt.toString()};
    }

    // TODO: Support for FK, and other constraint and Index recreation.
    String generateCreateStatement(DBTable table) throws DBException, Exception {

        Connection conn = DBConnectionFactory.getInstance().getConnection(dataView.getDatabaseConnection());
        String msg = "";
        if (conn == null) {
            Throwable ex = DBConnectionFactory.getInstance().getLastException();
            if (ex != null) {
                msg = ex.getMessage();
            } else {
                msg = NbBundle.getMessage(SQLExecutionHelper.class, "MSG_connection_failure", dataView.getDatabaseConnection());
            }
            dataView.setErrorStatusText(new DBException(msg));
            throw new DBException(msg);
        }

        boolean isdb2 = table.getParentObject().getDBType() == DBMetaDataFactory.DB2 ? true : false;

        StringBuffer sql = new StringBuffer();
        List<DBColumn> columns = table.getColumnList();
        sql.append("CREATE TABLE ").append(table.getQualifiedName()).append(" ("); // NOI18N
        int count = 0;
        for (DBColumn col : columns) {
            if (count++ > 0) {
                sql.append(", "); // NOI18N
            }

            String typeName = col.getTypeName();
            sql.append(col.getQualifiedName()).append(" ");

            int scale = col.getScale();
            int precision = col.getPrecision();
            if (precision > 0 && DataViewUtils.isPrecisionRequired(col.getJdbcType(), isdb2)) {
                if (typeName.contains("(")) { // Handle MySQL Binary Type // NOI18N
                    sql.append(typeName.replace("(", "(" + precision)); // NOI18N
                } else {
                    sql.append(typeName).append("(").append(precision); // NOI18N
                    if (scale > 0 && DataViewUtils.isScaleRequired(col.getJdbcType())) {
                        sql.append(", ").append(scale).append(")"); // NOI18N
                    } else {
                        sql.append(")"); // NOI18N
                    }
                }
            } else {
                sql.append(typeName);
            }

            if (DataViewUtils.isBinary(col.getJdbcType()) && isdb2) {
                sql.append("  FOR BIT DATA "); // NOI18N
            }

            if (col.hasDefault()) {
                sql.append(" DEFAULT ").append(col.getDefaultValue()).append(" "); // NOI18N
            }

            if (!col.isNullable()) {
                sql.append(" NOT NULL"); // NOI18N
            }

            if (col.isGenerated()) {
                sql.append(" ").append(getAutoIncrementText(table.getParentObject().getDBType()));
            }
        }

        DBPrimaryKey pk = table.getPrimaryKey();
        if (pk != null) {
            count = 0;
            sql.append(", PRIMARY KEY ("); // NOI18N
            for (String col : pk.getColumnNames()) {
                if (count++ > 0) {
                    sql.append(", "); // NOI18N
                }
                sql.append(table.getQuoter().quoteIfNeeded(col));
            }
            sql.append(")"); // NOI18N
        }
        sql.append(")"); // NOI18N

        return sql.toString();
    }

    static String getCountSQLQuery(String queryString) {
        // User may type "FROM" in either lower, upper or mixed case
        String[] splitByFrom = queryString.toUpperCase().split("FROM"); // NOI18N
        queryString = queryString.substring(splitByFrom[0].length());

        String[] splitByOrderBy = queryString.toUpperCase().split("ORDER BY"); // NOI18N
        queryString = queryString.substring(0, splitByOrderBy[0].length());
        return "SELECT COUNT(*) " + queryString; // NOI18N
    }

    static String getCountAsSubQuery(String queryString) {
        String[] splitByOrderBy = queryString.toUpperCase().split("ORDER BY"); // NOI18N
        queryString = queryString.substring(0, splitByOrderBy[0].length());
        return "SELECT COUNT(*) FROM (" + queryString + ") C2668"; // NOI18N
    }

    private boolean addSeparator(boolean and, StringBuilder result, StringBuilder raw, String sep) {
        if (and) {
            result.append(sep);
            raw.append(sep);
            return true;
        } else {
            return true;
        }
    }

    private void generateNameValue(int i, StringBuilder result, StringBuilder raw, Object value, List<Object> values, List<Integer> types) {
        String columnName = tblMeta.getQualifiedName(i);
        int type = tblMeta.getColumnType(i);

        if (value != null) {
            values.add(value);
            types.add(type);
            result.append(columnName + " = ? "); // NOI18N
            raw.append(columnName).append(" = ").append(getQualifiedValue(type, value)); // NOI18N
        } else { // Handle NULL value in where condition
            result.append(columnName + " IS NULL "); // NOI18N
            raw.append(columnName).append(" IS ").append(getQualifiedValue(type, value)); // NOI18N
        }
    }

    private void generateWhereCondition(StringBuilder result, StringBuilder raw, List<Integer> types, List<Object> values, int rowNum, TableModel model) {
        DBPrimaryKey key = tblMeta.geTable(0).getPrimaryKey();
        boolean keySelected = false;
        boolean and = false;

        if (key != null) {
            for (String keyName : key.getColumnNames()) {
                for (int i = 0; i < model.getColumnCount(); i++) {
                    String columnName = tblMeta.getColumnName(i);
                    if (columnName.equals(keyName)) {
                        //Object val = model.getValueAt(rowNum, i);
                        Object val = dataView.getDataViewPageContext().getColumnData(rowNum, i);
                        if (val != null) {
                            keySelected = true;
                            and = addSeparator(and, result, raw, " AND "); // NOI18N
                            generateNameValue(i, result, raw, val, values, types);
                            break;
                        }
                    }
                }
            }
        }

        if (key == null || !keySelected) {
            for (int i = 0; i < model.getColumnCount(); i++) {
                //Object val = model.getValueAt(rowNum, i);
                Object val = dataView.getDataViewPageContext().getColumnData(rowNum, i);
                and = addSeparator(and, result, raw, " AND "); // NOI18N
                generateNameValue(i, result, raw, val, values, types);
            }
        }
    }

    private Object getQualifiedValue(int type, Object val) {
        if (val == null) {
            return "NULL"; // NOI18N
        }
        if (type == Types.BIT && !(val instanceof Boolean)) {
            return "b'" + val + "'"; // NOI18N
        } else if (DataViewUtils.isNumeric(type)) {
            return val;
        } else {
            return "'" + val + "'"; // NOI18N
        }
    }

    private String getAutoIncrementText(int dbType) throws Exception {
        switch (dbType) {
            case DBMetaDataFactory.MYSQL:
                return "AUTO_INCREMENT"; // NOI18N

            case DBMetaDataFactory.PostgreSQL:
                return "SERIAL"; // NOI18N

            case DBMetaDataFactory.SQLSERVER:
                return "IDENTITY"; // NOI18N
            default:
                return "GENERATED ALWAYS AS IDENTITY"; // NOI18N
        }
    }
}
