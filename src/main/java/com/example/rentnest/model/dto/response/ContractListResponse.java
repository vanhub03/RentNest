package com.example.rentnest.model.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractListResponse {
    private Long id;
    private Long rentalRequestId;
    private String contractCode;
    private String tenantName;
    private String tenantPhone;
    private Long roomId;
    private String roomName;
    private String hostelName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal depositAmount;
    private String status;
    private boolean expiringSoon;
    private String contractFileUrl;
}
