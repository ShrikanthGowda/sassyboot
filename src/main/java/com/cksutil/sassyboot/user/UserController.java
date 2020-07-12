package com.cksutil.sassyboot.user;

import com.cksutil.sassyboot.common.ApiResponse;
import com.cksutil.sassyboot.user.dto.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/api/public/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Validated RegistrationDTO registrationDTO){
        userService.createUser(registrationDTO);
        return ResponseEntity.ok().body(ApiResponse.success("User registration successfull."));
    }
}
