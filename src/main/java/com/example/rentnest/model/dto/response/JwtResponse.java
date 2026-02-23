package com.example.rentnest.model.dto.response;

import lombok.Builder;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String roles;

    public JwtResponse(String token, Long id, String username, String email, String roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
