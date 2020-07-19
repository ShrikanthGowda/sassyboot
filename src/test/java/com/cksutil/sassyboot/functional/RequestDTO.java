package com.cksutil.sassyboot.functional;


import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;


public class RequestDTO {
    private String URL;
    private Map<String, String> queryParam;
    private HttpHeaders headers;
    private Object payload;


    public RequestDTO(String URL) {
        this.URL = URL;
        headers=new HttpHeaders();
        headers.add("Content-Type","application/json");
        queryParam=new HashMap<>();
    }
    public static RequestDTO createInstance(String URL){
        return new RequestDTO(URL);
    }

    public RequestDTO addHeader(String headerName,String headerValue){
        headers.add(headerName,headerValue);
        return this;
    }

    public RequestDTO setAuthToken(String token){
        headers.remove(BaseTest.AUTH_TOKEN_HEADER_NAME);
        headers.add(BaseTest.AUTH_TOKEN_HEADER_NAME,token);
        return this;
    }
    public RequestDTO setXSRFToken(String token){
        headers.remove(BaseTest.XSRF_TOKEN_HEADER_NAME);
        headers.add(BaseTest.XSRF_TOKEN_HEADER_NAME,token);
        return this;
    }

    public RequestDTO addQueryParams(String paramName,String paramValue){
        queryParam.put(paramName,paramValue);
        return this;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }

    public RequestDTO setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public String getURL() {
        return URL;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Object getPayload() {
        return payload;
    }
}
