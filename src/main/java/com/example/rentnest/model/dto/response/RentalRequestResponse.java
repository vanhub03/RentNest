package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RentalRequestResponse {
    private Long id;
    private String tenantName;
    private String tenantPhone;
    private String roomName;
    private String hostelAddress;
    private LocalDate expectedMoveInDate;
    private LocalDateTime createdAt;
    private String status;
}
