package com._4paradigm.openmldb.featureplatform.utils;

public class OpenmldbSqlUtil {

    public static String removeDeployFromSql(String inputSql) {
        String[] parts = inputSql.trim().split(" ");

        // Using StringBuilder to concatenate strings efficiently
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            sb.append(parts[i]);
            // Add space back except for the last element
            if (i < parts.length - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

}