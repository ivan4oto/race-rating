package com.ivangochev.raceratingapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank
    private String password;
    @Email
    private String email;
}
