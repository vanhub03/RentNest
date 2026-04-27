package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RentalRequestResponse {
    private Long id;
    private Long roomId;
    private String tenantName;
    private String tenantPhone;
    private String tenantEmail;
    private String cccd;
    private String landlordName;
    private String landlordPhone;
    private String roomName;
    private String roomThumbnail;
    private String hostelName;
    private String hostelAddress;
    private Double roomArea;
    private Integer roomFloor;
    private Integer bathCount;
    private BigDecimal depositAmount;
    private String note;
    private String rejectReason;
    private LocalDate expectedMoveInDate;
    private LocalDateTime createdAt;
    private String status;
    private String roomStatus;
}
