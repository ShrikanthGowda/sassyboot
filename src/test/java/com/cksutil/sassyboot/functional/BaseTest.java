package com.cksutil.sassyboot.functional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.HttpCookie;
import java.util.Collection;
import java.util.Objects;

public class BaseTest {
    public static final String XSRF_TOKEN_COOKIE_NAME="XSRF-TOKEN";
    public static final String XSRF_TOKEN_HEADER_NAME="X-CSRF-TOKEN";

    public static final String AUTH_TOKEN_COOKIE_NAME="SESSION";
    public static final String AUTH_TOKEN_HEADER_NAME="X-Auth-Token";

    private TestRestTemplate testRestTemplate;

    protected ResponseEntity<String> doGet(RequestDTO requestDTO) {
        final UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromPath(requestDTO.getURL());
        requestDTO.getQueryParam().forEach(urlBuilder::queryParam);
        return testRestTemplate
                .exchange(urlBuilder.toUriString(), HttpMethod.GET,
                        new HttpEntity<>(requestDTO.getHeaders()), String.class);
    }

    protected ResponseEntity<String> doPost(RequestDTO requestDTO) {
        return testRestTemplate
                .exchange(requestDTO.getURL(), HttpMethod.POST,
                        new HttpEntity<>(requestDTO.getPayload(), requestDTO.getHeaders()), String.class);
    }

    protected ResponseEntity<String> doDelete(RequestDTO requestDTO) {
        return testRestTemplate
                .exchange(requestDTO.getURL(), HttpMethod.DELETE,
                        new HttpEntity<>(requestDTO.getHeaders()), String.class);
    }

    protected static ResponseEntity<String> doDelete(TestRestTemplate testRestTemplate,RequestDTO requestDTO) {
        return testRestTemplate
                .exchange(requestDTO.getURL(), HttpMethod.DELETE,
                        new HttpEntity<>(requestDTO.getHeaders()), String.class);
    }

    protected  static <T> String getAuthToken(ResponseEntity<T> response) {
        return  Objects.requireNonNull(response.getHeaders().get(HttpHeaders.SET_COOKIE))
                .stream()
                .map(HttpCookie::parse)
                .flatMap(Collection::stream)
                .filter(httpCookie -> httpCookie.getName().equals(BaseTest.AUTH_TOKEN_COOKIE_NAME)
                        && !StringUtils.isEmpty(httpCookie.getValue()))
                .findAny().map(HttpCookie::getValue)
                .orElse(null);
    }

    protected  static <T> String getXSRFToken(ResponseEntity<T> response) {
        return  Objects.requireNonNull(response.getHeaders().get(HttpHeaders.SET_COOKIE))
                .stream()
                .map(HttpCookie::parse)
                .flatMap(Collection::stream)
                .filter(httpCookie -> httpCookie.getName().equals(BaseTest.XSRF_TOKEN_COOKIE_NAME)
                        && !StringUtils.isEmpty(httpCookie.getValue()))
                .findFirst().map(HttpCookie::getValue)
                .orElse(null);
    }

    protected void printJSON(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        System.out.println(jsonObject.toString(4));
    }

    @Autowired
    public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }
}
