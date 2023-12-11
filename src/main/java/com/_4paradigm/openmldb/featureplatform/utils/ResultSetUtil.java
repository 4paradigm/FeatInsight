package com._4paradigm.openmldb.featureplatform.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import com._4paradigm.openmldb.DataType;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.Schema;

public class ResultSetUtil {
    public static String resultSetToString(SQLResultSet resultSet) throws SQLException {
        com._4paradigm.openmldb.sdk.Schema schema = resultSet.GetInternalSchema();
        int columnCount = schema.getColumnList().size();
        //int columnCount = schema.GetColumnCnt();

        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        StringBuilder schemaString = new StringBuilder("Schema: ");
        // Append column names
        for (int i = 0; i < columnCount; i++) {
            if (i != 0) {
                schemaString.append(", ");
            }
            //schemaString.append(schema.GetColumnName(i) + "(" + schema.GetColumnType(i) + ")");
            schemaString.append(schema.getColumnName(i) + "(" + schema.getColumnType(i) + ")");
        }
        joiner.add(schemaString);

        // Append rows
        while (resultSet.next()) {
            StringJoiner rowJoiner = new StringJoiner(", ");
            for (int i = 0; i < columnCount; i++) {
                //DataType type = schema.GetColumnType(i);
                int type = schema.getColumnType(i);
                String columnValue = TypeUtil.getResultSetStringColumn(resultSet, i + 1, type);
                rowJoiner.add(columnValue);
            }
            joiner.add(rowJoiner.toString());
        }

        return joiner.toString();
    }

    public static List<List<String>> resultSetToStringArray(SQLResultSet resultSet) throws SQLException {
        List<List<String>> returnList = new ArrayList<>();

        //Schema schema = resultSet.GetInternalSchema();
        Schema schema = resultSet.GetInternalSchema();
        //int columnCount = schema.GetColumnCnt();
        int columnCount = schema.getColumnList().size();

        // Add schema
        List<String> schemaList = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            //schemaList.add(schema.GetColumnName(i));
            schemaList.add(schema.getColumnName(i));
        }
        returnList.add(schemaList);

        // Add rows
        while (resultSet.next()) {
            List<String> rowList = new ArrayList<>();

            for (int i = 0; i < columnCount; i++) {
                //DataType type = schema.GetColumnType(i);
                int type = schema.getColumnType(i);
                String columnValue = TypeUtil.getResultSetStringColumn(resultSet, i + 1, type);
                rowList.add(columnValue);
            }

            returnList.add(rowList);
        }

        return returnList;
    }

    public static void assertSizeIsOne(ResultSet result) throws SQLException {
        if (result.getFetchSize() == 0) {
            throw new SQLException("The size of result set is 0, can not find the resource");
        } else if (result.getFetchSize() > 1) {
            throw new SQLException(String.format("The size of result set is %d, get more than one resource", result.getFetchSize()));
        }
    }

}
