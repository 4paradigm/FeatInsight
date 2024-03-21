package com._4paradigm.openmldb.featureplatform.dao.model;

import com._4paradigm.openmldb.sdk.SqlExecutor;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import lombok.Data;

import java.time.Instant;

@Data
public class ExpiringSqlExecutor {
    private SqlClusterExecutor sqlExecutor;
    private volatile Instant expiryTime;

    public ExpiringSqlExecutor(SqlClusterExecutor sqlExecutor, long lifetimeInSeconds) {
        this.sqlExecutor = sqlExecutor;
        this.expiryTime = Instant.now().plusSeconds(lifetimeInSeconds);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryTime);
    }

    public synchronized void closeSqlExecutor() {
        this.sqlExecutor.close();
    }

}
