package com.cksutil.sassyboot.auth.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class AppSessionIdResolver implements HttpSessionIdResolver {
    private static final Log logger = LogFactory.getLog(AppSessionIdResolver.class);

    private HeaderHttpSessionIdResolver headerHttpSessionIdResolver;
    private CookieHttpSessionIdResolver cookieHttpSessionIdResolver;

    public AppSessionIdResolver(String authTokenHeaderName) {
        headerHttpSessionIdResolver = new HeaderHttpSessionIdResolver(authTokenHeaderName);
        cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        List<String> sessionIds = cookieHttpSessionIdResolver.resolveSessionIds(request);
        if (sessionIds.size() == 0) {
            sessionIds = headerHttpSessionIdResolver.resolveSessionIds(request);
            if (sessionIds.size() != 0) {
                String sessionId = base64Decode(sessionIds.get(0));
                sessionIds= Collections.singletonList(sessionId);
            }
        }
        return sessionIds;
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        cookieHttpSessionIdResolver.setSessionId(request, response, sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        cookieHttpSessionIdResolver.expireSession(request, response);
    }

    private String base64Decode(String base64Value) {
        try {
            byte[] decodedCookieBytes = Base64.getDecoder().decode(base64Value);
            return new String(decodedCookieBytes);
        } catch (Exception ex) {
            logger.debug("Unable to Base64 decode value: " + base64Value);
            return null;
        }
    }
}
