package com.cksutil.sassyboot.auth;

import com.cksutil.sassyboot.auth.resource.SessionUserResource;
import com.cksutil.sassyboot.auth.security.SessionUser;
import com.cksutil.sassyboot.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @GetMapping("/api/session")
    public ResponseEntity<ApiResponse> getSession(Authentication authentication){
        final SessionUser sessionUser = (SessionUser)authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success("Success", SessionUserResource.build(sessionUser)));
    }
}
