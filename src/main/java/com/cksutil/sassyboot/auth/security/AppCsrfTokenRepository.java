package com.cksutil.sassyboot.auth.security;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppCsrfTokenRepository implements CsrfTokenRepository {
    private final HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository;
    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;

    public AppCsrfTokenRepository(String csrfTokenCookieName,String csrfTokenHeaderName) {
        httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();;
        final CookieCsrfTokenRepository cookieCsrfTR = new CookieCsrfTokenRepository();
        cookieCsrfTR.setCookieHttpOnly(false);
        cookieCsrfTR.setCookiePath("/");
        cookieCsrfTR.setCookieName(csrfTokenCookieName);
        httpSessionCsrfTokenRepository.setHeaderName(csrfTokenHeaderName);
        cookieCsrfTokenRepository = cookieCsrfTR;
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return httpSessionCsrfTokenRepository.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        httpSessionCsrfTokenRepository.saveToken(token,request,response);
        cookieCsrfTokenRepository.saveToken(token,request,response);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return httpSessionCsrfTokenRepository.loadToken(request);
    }
}
