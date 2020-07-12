package com.cksutil.sassyboot.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class JSONBodyUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {
    private static final String REQUEST_BODY_ATTRIBUTE = "_JAUTH_RBODY_";
    private final ObjectMapper objectMapper;

    public JSONBodyUsernamePasswordAuthFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (requiresAuthentication(request, response)) {
            UsernamePasswordRequest usernamePasswordRequest = objectMapper.readValue(request.getInputStream(), UsernamePasswordRequest.class);
            request.setAttribute(REQUEST_BODY_ATTRIBUTE, usernamePasswordRequest);
        }

        super.doFilter(req, res, chain);
    }

    protected String obtainUsername(HttpServletRequest request) {
        UsernamePasswordRequest usernamePasswordRequest = (UsernamePasswordRequest) request.getAttribute(REQUEST_BODY_ATTRIBUTE);
        return usernamePasswordRequest.get(getUsernameParameter());
    }

    protected String obtainPassword(HttpServletRequest request) {
        UsernamePasswordRequest usernamePasswordRequest = (UsernamePasswordRequest) request.getAttribute(REQUEST_BODY_ATTRIBUTE);
        return usernamePasswordRequest.get(getPasswordParameter());
    }

    private static class UsernamePasswordRequest extends HashMap<String, String> {
    }
}
