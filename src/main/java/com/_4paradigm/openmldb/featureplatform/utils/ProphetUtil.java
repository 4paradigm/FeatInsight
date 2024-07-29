package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.featureplatform.dao.model.FesqlTable;
import com._4paradigm.openmldb.featureplatform.dao.model.FesqlTableColumn;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;

import java.util.ArrayList;
import java.util.List;

public class ProphetUtil {
    public static String prophetToDbType(String prophetType) {
        switch (prophetType.toLowerCase()) {
            case "smallint":
                return "int16";
            case "int":
                return "int32";
            case "bigint":
                return "int64";
            case "float":
                return "float";
            case "double":
                return "double";
            case "bool":
            case "boolean":
                return "bool";
            case "string":
                return "string";
            case "timestamp":
                return "timestamp";
            case "date":
                return "date";
            default:
                throw new RuntimeException("Unknown type: " + prophetType);
        }
    }

    public static String dbToProphetType(String dbType) {
        switch (dbType.toLowerCase()) {
            case "int16":
                return "SmallInt";
            case "int32":
                return "Int";
            case "int64":
                return "BigInt";
            case "float":
                return "Float";
            case "double":
                return "Double";
            case "bool":
            case "boolean":
                return "Boolean";
            case "string":
                return "String";
            case "timestamp":
                return "Timestamp";
            case "date":
                return "Date";
            default:
                throw new RuntimeException("Unknown type: " + dbType);
        }
    }

    public static String prophetSchemaToDbSchema(List<FesqlTableColumn> prophetSchema) {
        List<String> dbColumnList = new ArrayList<>();
        for (FesqlTableColumn tableColumn : prophetSchema) {
            dbColumnList.add(String.format("%s:%s", tableColumn.getName(), prophetToDbType(tableColumn.getType())));
        }
        return String.join(",", dbColumnList);
    }

    public static List<FesqlTableColumn> dbSchemaToProphetSchema(String dbSchema) {
        List<FesqlTableColumn> columnList = new ArrayList<>();
        for (String columnString : dbSchema.split(",")) {
            String[] columnArray = columnString.split(":");
            FesqlTableColumn tableColumn = new FesqlTableColumn();
            tableColumn.setName(columnArray[0].trim());
            tableColumn.setType(dbToProphetType(columnArray[1].trim()));
            columnList.add(tableColumn);
        }
        return columnList;
    }
}
