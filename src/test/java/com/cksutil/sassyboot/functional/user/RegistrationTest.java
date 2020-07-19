package com.cksutil.sassyboot.functional.user;

import com.cksutil.sassyboot.functional.BaseTest;
import com.cksutil.sassyboot.functional.RequestDTO;
import com.cksutil.sassyboot.user.requestdto.RegistrationDTO;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
@DatabaseSetup(value = "classpath:testData/registrationTest.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:testData/DatabaseTearDown.xml")
public class RegistrationTest extends BaseTest {
    private  final String URL = "/api/public/register";

    @Test
    public void errorOnBlankInput() {
        final ResponseEntity<String> response = doPost(RequestDTO.createInstance(URL));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldValidateInput() throws JSONException {
        final ResponseEntity<String> response = doPost(
                RequestDTO.createInstance(URL)
                        .setPayload(new RegistrationDTO())
        );
        String expected ="{" +
                "    \"status\": \"BAD_REQUEST\"," +
                "    \"message\": \"Invalid Input detected.\"," +
                "    \"errors\": {" +
                "        \"password\": \"Please choose a password.\"," +
                "        \"name\": \"Name is required.\"," +
                "        \"confirmPassword\": \"Please confirm password\"," +
                "        \"emailId\": \"Email id is required.\"" +
                "    }" +
                "}";
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }

    @Test
    public void errorOnShortPassword() throws JSONException {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setPassword("Pas");
        registrationDTO.setConfirmPassword("Pas");

        final ResponseEntity<String> response = doPost(
                RequestDTO.createInstance(URL)
                        .setPayload(registrationDTO)
        );
        String expected ="{" +
                "    \"status\": \"BAD_REQUEST\"," +
                "    \"message\": \"Invalid Input detected.\"," +
                "    \"errors\": {" +
                "        \"password\": \"Password needs to be atleast 5 character long.\"," +
                "        \"name\": \"Name is required.\"," +
                "        \"emailId\": \"Email id is required.\"" +
                "    }" +
                "}";
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }

    @Test
    public void errorOnInvalidEmailId() throws JSONException {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setEmailId("invalid");

        final ResponseEntity<String> response = doPost(
                RequestDTO.createInstance(URL)
                        .setPayload(registrationDTO)
        );
        String expected ="{" +
                "    \"status\": \"BAD_REQUEST\"," +
                "    \"message\": \"Invalid Input detected.\"," +
                "    \"errors\": {" +
                "        \"password\": \"Please choose a password.\"," +
                "        \"name\": \"Name is required.\"," +
                "        \"confirmPassword\": \"Please confirm password\"," +
                "        \"emailId\": \"Please provide a valid email id.\"" +
                "    }" +
                "}";
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }

    @Test
    public void errorOnPasswordMismatch() throws JSONException {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("Rocky Mountain");
        registrationDTO.setEmailId("rm@gmail.com");
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("confirmPass");

        final ResponseEntity<String> response = doPost(
                RequestDTO.createInstance(URL)
                        .setPayload(registrationDTO)
        );
        String expected ="{" +
                "    \"status\": \"BAD_REQUEST\"," +
                "    \"message\": \"Invalid input.\"," +
                "    \"errors\": {" +
                "        \"confirmPassword\": \"Password not matching.\"" +
                "    }" +
                "}";
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }

    @Test
    public void errorOnUsingExistingEmailId() throws JSONException {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("Rocky Mountain");
        registrationDTO.setEmailId("rifco@covidash.com");
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");

        final ResponseEntity<String> response = doPost(
                RequestDTO.createInstance(URL)
                        .setPayload(registrationDTO)
        );
        String expected ="{" +
                "    \"status\": \"BAD_REQUEST\"," +
                "    \"message\": \"Invalid input.\"," +
                "    \"errors\": {" +
                "        \"emailId\": \"Email id rifco@covidash.com already taken.\"" +
                "    }" +
                "}";
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }

    @Test
    public void successfulUserRegistration() throws JSONException {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("Bindi Suri");
        registrationDTO.setEmailId("binsuri@covidash.com");
        registrationDTO.setPassword("password");
        registrationDTO.setConfirmPassword("password");

        final ResponseEntity<String> response = doPost(
                RequestDTO.createInstance(URL)
                        .setPayload(registrationDTO)
        );
        String expected ="{" +
                "    \"status\": \"OK\"," +
                "    \"message\": \"User registration successfull.\"" +
                "}";
        JSONAssert.assertEquals(expected, response.getBody(), true);
    }
}
