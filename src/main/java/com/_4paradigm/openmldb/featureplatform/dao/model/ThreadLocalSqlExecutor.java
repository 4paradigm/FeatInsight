package com._4paradigm.openmldb.featureplatform.dao.model;

import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalSqlExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalSqlExecutor.class);
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
        try {
            return sqlExecutor.get();
        }
        catch (Exception e) {
            logger.error(String.format("failed to get sql executor from ThreadLocal: %s", e.getMessage()));
            return null;
        }
    }

    public static void cleanThreadLocal() {
        uuid.remove();
        sqlExecutor.remove();
    }

}
