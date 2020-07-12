package com.cksutil.sassyboot.user;

import com.cksutil.sassyboot.exception.ApiException;
import com.cksutil.sassyboot.user.dto.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = encoder;
    }

    @Transactional
    public void createUser(RegistrationDTO registrationDTO){
        validateRegistrationData(registrationDTO);
        userRepository.save(UserEntity.builder()
                .name(registrationDTO.getName())
                .emailId(registrationDTO.getEmailId())
                .mobileNo(registrationDTO.getMobileNo())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .build());
    }

    private void validateRegistrationData(RegistrationDTO registrationDTO){
        final Map<String, String> fieldErrors = new HashMap<>();
        if (userRepository.countByEmailId(registrationDTO.getEmailId()) != 0) {
            fieldErrors.put("emailId", "Email id " + registrationDTO.getEmailId() + " already taken.");
        }

        if (!registrationDTO.getConfirmPassword().equals(registrationDTO.getPassword())) {
            fieldErrors.put("confirmPassword", "Password not matching.");
        }

        if (fieldErrors.size() > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid input.",fieldErrors);
        }
    }
}
