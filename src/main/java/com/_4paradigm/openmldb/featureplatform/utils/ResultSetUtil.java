package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.DataType;
import com._4paradigm.openmldb.Schema;
import com._4paradigm.openmldb.jdbc.SQLResultSet;

import java.sql.SQLException;
import java.util.StringJoiner;
public class ResultSetUtil {
    public static String resultSetToString(SQLResultSet resultSet) throws SQLException {
        Schema schema = resultSet.GetInternalSchema();
        int columnCount = schema.GetColumnCnt();

        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        StringBuilder schemaString = new StringBuilder("Schema: ");
        // Append column names
        for (int i = 0; i < columnCount; i++) {
            if (i != 0) {
                schemaString.append(", ");
            }
            schemaString.append(schema.GetColumnName(i) + "(" + schema.GetColumnType(i) + ")");
        }
        joiner.add(schemaString);

        // Append rows
        while (resultSet.next()) {
            StringJoiner rowJoiner = new StringJoiner(", ");
            for (int i = 0; i < columnCount; i++) {
                DataType type = schema.GetColumnType(i);
                String columnValue = TypeUtil.getResultSetStringColumn(resultSet, i+1, type);
                rowJoiner.add(columnValue);
            }
            joiner.add(rowJoiner.toString());
        }

        return joiner.toString();
    }

}
