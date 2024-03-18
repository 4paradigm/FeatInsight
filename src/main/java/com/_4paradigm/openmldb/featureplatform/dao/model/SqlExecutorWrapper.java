package com._4paradigm.openmldb.featureplatform.dao.model;

import com._4paradigm.openmldb.sdk.SqlExecutor;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
public class SqlExecutorWrapper {
    private String uuid;
    private SqlExecutor sqlExecutor;

}
