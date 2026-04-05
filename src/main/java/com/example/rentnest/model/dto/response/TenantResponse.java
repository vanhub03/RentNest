package com.example.rentnest.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String identityCard;
    private String roomName;
    private String hostelName;
    private boolean isRepresentative;
}
