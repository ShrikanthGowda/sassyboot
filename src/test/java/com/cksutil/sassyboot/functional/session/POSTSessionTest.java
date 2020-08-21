package com.cksutil.sassyboot.functional.session;

import com.cksutil.sassyboot.functional.BaseTest;
import com.cksutil.sassyboot.functional.RequestDTO;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class POSTSessionTest extends BaseTest {
    private  final String URL = "/api/session";
    private  String AUTH_TOKEN=null;
    private  String XSRF_TOKEN=null;

    @BeforeAll
    public void setup(){
        if(AUTH_TOKEN==null || XSRF_TOKEN==null){
            ResponseEntity<String> response = doGet(RequestDTO.createInstance(URL));
            AUTH_TOKEN=BaseTest.getAuthToken(response);
            XSRF_TOKEN=BaseTest.getXSRFToken(response);
        }
    }

    @AfterAll
    public  void tearDown(){
        if(AUTH_TOKEN!=null || XSRF_TOKEN!=null){
            doDelete(RequestDTO.createInstance(URL)
                    .setAuthToken(AUTH_TOKEN)
                    .setXSRFToken(XSRF_TOKEN));
        }
    }

    @Test
    public void errorOnBlankXSRFToken() {
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL).setAuthToken(AUTH_TOKEN));
        assertThat(response.getStatusCode(), not(equalTo(HttpStatus.OK)));
    }

    @Test
    public void errorOnInvalidXSRFToken() {
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL)
                .setAuthToken(AUTH_TOKEN)
                .setXSRFToken("INVALID_XSRF_TOKEN"));
        assertThat(response.getStatusCode(), not(equalTo(HttpStatus.OK)));
    }

    @Test
    public void errorOnBlankInput() {
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL)
                .setAuthToken(AUTH_TOKEN)
                .setXSRFToken(XSRF_TOKEN));
        assertThat(response.getStatusCode(), not(equalTo(HttpStatus.OK)));
    }

    @Test
    public void errorOnInvalidUsername() {
        final SessionDTO sessionDTO = new SessionDTO("6789786756","dummy_password");
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL)
                .setAuthToken(AUTH_TOKEN)
                .setXSRFToken(XSRF_TOKEN)
                .setPayload(sessionDTO));
        assertThat(response.getStatusCode(),equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void errorOnInvalidPassword() {
        Map<String,String[][]> abdf = new HashMap<>();
        final SessionDTO sessionDTO = new SessionDTO("admin@sassyboot.com","invalid_password");
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL)
                .setAuthToken(AUTH_TOKEN)
                .setXSRFToken(XSRF_TOKEN)
                .setPayload(sessionDTO));
        assertThat(response.getStatusCode(),equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void successOnCorrectEmailIdPassword() throws JSONException {
            final SessionDTO sessionDTO = new SessionDTO("admin@sassyboot.com","password");
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL)
                .setAuthToken(AUTH_TOKEN)
                .setXSRFToken(XSRF_TOKEN)
                .setPayload(sessionDTO));
        assertThat(response.getStatusCode(),equalTo(HttpStatus.OK));
        final String expectedOutput = "{" +
                "    \"payload\": {" +
                "        \"accountLocked\": false," +
                "        \"name\": \"SassyBoot Admin\"," +
                "        \"accountExpired\": false," +
                "        \"userId\": \"b516e3aa-a3af-11e7-bbd2-63ad024569ce\"," +
                "        \"credentialsExpired\": false," +
                "        \"authorities\": {" +
                "            \"ROLE_SYSTEM_ADMIN\": true" +
                "        }," +
                "        \"enabled\": true" +
                "    }," +
                "    \"message\": \"Successfully loggedin\"," +
                "    \"status\": \"OK\"" +
                "}";
        JSONAssert.assertEquals(expectedOutput, response.getBody(), true);
        AUTH_TOKEN=getAuthToken(response);
        XSRF_TOKEN=getXSRFToken(response);
    }
}
