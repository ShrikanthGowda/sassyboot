package com.cksutil.sassyboot.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationDTO {
    @NotNull(message = "Name is required.")
    private String name;

    @NotNull(message = "Email id is required.")
    @Email(message = "Please provide a valid email id.")
    private String emailId;

    private String mobileNo;

    @NotNull(message = "Please choose a password.")
    @Length(min = 5,message = "Password needs to be atleast 5 character long.")
    private String password;

    @NotNull(message = "Please confirm password")
    private String confirmPassword;
}
