package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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