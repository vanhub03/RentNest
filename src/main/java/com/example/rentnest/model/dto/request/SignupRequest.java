package com.example.rentnest.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private String role;
}
