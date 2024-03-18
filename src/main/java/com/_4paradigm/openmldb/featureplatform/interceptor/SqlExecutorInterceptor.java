package com._4paradigm.openmldb.featureplatform.interceptor;

import com._4paradigm.openmldb.featureplatform.dao.model.SqlExecutorWrapper;
import com._4paradigm.openmldb.featureplatform.utils.DatabaseConnectionUtil;
import com._4paradigm.openmldb.sdk.SqlExecutor;
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
        String uuid = request.getHeader("UUID");
        if(uuid != null) {
            SqlExecutor sqlExecutor = DatabaseConnectionUtil.getSqlExecutor(uuid);
            if(sqlExecutor != null) {
                sqlExecutorWrapper.setSqlExecutor(sqlExecutor);
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

}
