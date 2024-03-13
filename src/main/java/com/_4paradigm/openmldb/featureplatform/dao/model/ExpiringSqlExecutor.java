package com._4paradigm.openmldb.featureplatform.dao.model;

import com._4paradigm.openmldb.sdk.SqlExecutor;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;

@Getter
@Data
public class ExpiringSqlExecutor {
    private SqlExecutor sqlExecutor = null;
    private Instant expiryTime = null;

    public ExpiringSqlExecutor(SqlExecutor sqlExecutor, long lifetimeInSeconds) {
        this.sqlExecutor = sqlExecutor;
        this.expiryTime = Instant.now().plusSeconds(lifetimeInSeconds);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryTime);
    }

    public void closeSqlExecutor() {
        this.sqlExecutor.close();
    }


}
