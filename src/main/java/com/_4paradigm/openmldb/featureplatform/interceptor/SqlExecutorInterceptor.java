package com._4paradigm.openmldb.featureplatform.interceptor;

import com._4paradigm.openmldb.featureplatform.dao.model.ThreadLocalSqlExecutor;
import com._4paradigm.openmldb.featureplatform.utils.SqlExecutorPoolManager;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SqlExecutorInterceptor implements HandlerInterceptor {

    @Autowired
    private SqlExecutorPoolManager sqlExecutorPoolManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // do not need to check OPTIONS request
        if(request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }

        String URI = request.getRequestURI();
        if(URI.equals("/api/login")) {
            return true;
        }

        String uuid = request.getHeader("UUID");
        if(uuid != null) {
            ThreadLocalSqlExecutor.setUuid(uuid);
            SqlClusterExecutor sqlExecutor = sqlExecutorPoolManager.getSqlExecutor(uuid);
            if(sqlExecutor != null) {
                ThreadLocalSqlExecutor.setSqlExecutor(sqlExecutor);
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        ThreadLocalSqlExecutor.cleanThreadLocal();
    }

}
