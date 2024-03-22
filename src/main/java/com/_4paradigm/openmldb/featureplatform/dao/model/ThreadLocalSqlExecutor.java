package com._4paradigm.openmldb.featureplatform.dao.model;

import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;

public class ThreadLocalSqlExecutor {
    private static final ThreadLocal<String> uuid = new ThreadLocal<>();
    private static final ThreadLocal<SqlClusterExecutor> sqlExecutor = new ThreadLocal<>();

    public static void setUuid(String id) {
        uuid.set(id);
    }

    public static void setSqlExecutor(SqlClusterExecutor executor) {
        sqlExecutor.set(executor);
    }

    public static String getUuid() {
        return uuid.get();
    }

    public static SqlClusterExecutor getSqlExecutor() {
        return sqlExecutor.get();
    }

    public static void cleanThreadLocal() {
        uuid.remove();
        sqlExecutor.remove();
    }

}
