package com._4paradigm.openmldb.featureplatform.interceptor;

import com._4paradigm.openmldb.featureplatform.dao.model.SqlExecutorWrapper;
import com._4paradigm.openmldb.featureplatform.utils.SqlExecutorPoolManager;
import com._4paradigm.openmldb.sdk.SqlExecutor;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SqlExecutorInterceptor implements HandlerInterceptor {

    @Autowired
    private SqlExecutorWrapper sqlExecutorWrapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String URI = request.getRequestURI();
        if(URI.equals("/api/login")) {
            return true;
        }

        String uuid = request.getHeader("UUID");
        if(uuid != null) {
            sqlExecutorWrapper.setUuid(uuid);
            SqlClusterExecutor sqlExecutor = SqlExecutorPoolManager.getInstance().getSqlExecutor(uuid);
            if(sqlExecutor != null) {
                sqlExecutorWrapper.setSqlExecutor(sqlExecutor);
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

}
