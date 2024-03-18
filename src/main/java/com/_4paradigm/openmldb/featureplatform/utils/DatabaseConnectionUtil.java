package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.featureplatform.dao.model.ExpiringSqlExecutor;
import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.SqlExecutor;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * once an executor is being used, the expiry time will be set as one hour after the current time
 * but if an executor has not been used within one hour, it will be cleaned up
 */
@Component
public class DatabaseConnectionUtil {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionUtil.class);
    private static Environment env;
    private static final long lifetimeInSeconds = 3600L;
    private static final ConcurrentHashMap<String, ExpiringSqlExecutor> executorPool = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Autowired
    public DatabaseConnectionUtil (Environment environment) {
        env = environment;
        scheduledExecutorService.scheduleAtFixedRate(DatabaseConnectionUtil::cleanUp, 1, 30, TimeUnit.MINUTES);
    }

    public static String createSqlExecutor(String username, String password) {
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        SdkOption option = new SdkOption();
        option.setZkCluster(zkHost);
        option.setZkPath(zkPath);
        option.setUser(username);
        option.setPassword(password);

        SqlClusterExecutor sqlExecutor = null;
        try {
            sqlExecutor = new SqlClusterExecutor(option);
        } catch (SqlException e) {
            logger.error(String.format("Try to create sql executor for %s but fail with exception: %s",
                    username,
                    e.getMessage()));
            return null;
        }

        String uuid = UUID.randomUUID().toString();
        executorPool.put(uuid, new ExpiringSqlExecutor(sqlExecutor, lifetimeInSeconds));
        return uuid;

    }

    public static SqlExecutor getSqlExecutor(String uuid) {
        ExpiringSqlExecutor expiringSqlExecutor = executorPool.get(uuid);
        if(expiringSqlExecutor == null || expiringSqlExecutor.isExpired()) {
            return null;
        }
        else {
            expiringSqlExecutor.setExpiryTime(Instant.now().plusSeconds(lifetimeInSeconds));
            return expiringSqlExecutor.getSqlExecutor();
        }
    }

    public static boolean closeSqlExecutor(String uuid) {
        ExpiringSqlExecutor expiringSqlExecutor = executorPool.get(uuid);
        expiringSqlExecutor.closeSqlExecutor();
        executorPool.remove(uuid);
        return true;
    }

    private static void cleanUp() {
        for(String uuid : executorPool.keySet()) {
            ExpiringSqlExecutor  expiringSqlExecutor = executorPool.get(uuid);
            if(expiringSqlExecutor != null && expiringSqlExecutor.isExpired()) {
                expiringSqlExecutor.closeSqlExecutor();
                executorPool.remove(uuid);
            }
        }
    }

}
