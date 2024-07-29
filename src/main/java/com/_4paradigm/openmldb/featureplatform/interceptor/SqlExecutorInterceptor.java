package com._4paradigm.openmldb.featureplatform.interceptor;

import com._4paradigm.openmldb.featureplatform.dao.model.FesqlParseDagRequest;
import com._4paradigm.openmldb.featureplatform.dao.model.ThreadLocalSqlExecutor;
import com._4paradigm.openmldb.featureplatform.utils.SqlExecutorPoolManager;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SqlExecutorInterceptor implements HandlerInterceptor {

    @Autowired
    private SqlExecutorPoolManager sqlExecutorPoolManager;
    @Value("${prophet.token_auth_enabled}")
    private String prophetTokenAuthEnabled;
    @Value("${openmldb.username}")
    private String openmldbUsername;
    @Value("${openmldb.password}")
    private String openmldbPassword;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // do not need to check OPTIONS request
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }

        String URI = request.getRequestURI();
        if (URI.endsWith("/api/login")) {
            return true;
        }
        if ("true".equalsIgnoreCase(prophetTokenAuthEnabled)) {
            String userTokenOrAccessKey = null;
            if (null != request.getCookies()) {
                for (Cookie cookie : request.getCookies()) {
                    if ("User-Token".equals(cookie.getName())) {
                        userTokenOrAccessKey = cookie.getValue();
                        break;
                    }
                }
            }
            if (null == userTokenOrAccessKey) {
                userTokenOrAccessKey = request.getHeader("User-Token");
            }
            if (null == userTokenOrAccessKey) {
                userTokenOrAccessKey = request.getHeader("Access-Key");
            }
            if (null != userTokenOrAccessKey) {
                SqlClusterExecutor sqlExecutor = sqlExecutorPoolManager.getSqlExecutor(userTokenOrAccessKey);
                if (sqlExecutor == null) {
                    try {
                        sqlExecutor = sqlExecutorPoolManager.createSqlExecutor(openmldbUsername, openmldbPassword, userTokenOrAccessKey);
                    } catch (SqlException e) {
                        throw new RuntimeException(e);
                    }
                }
                ThreadLocalSqlExecutor.setSqlExecutor(sqlExecutor);
                return true;
            }
        }
        String uuid = request.getHeader("UUID");
        if (uuid != null) {
            ThreadLocalSqlExecutor.setUuid(uuid);
            SqlClusterExecutor sqlExecutor = sqlExecutorPoolManager.getSqlExecutor(uuid);
            if (sqlExecutor != null) {
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
