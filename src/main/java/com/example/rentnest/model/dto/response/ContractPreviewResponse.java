package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ContractPreviewResponse {
    private Long id;
    private Long rentalRequestId;
    private String contractCode;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private Integer durationMonths;
    private BigDecimal depositAmount;
    private BigDecimal monthlyRent;
    private Integer paymentDay;
    private List<ServiceChargeDto> services;

    private String landlordName;
    private String landlordPhone;
    private String landlordAddress;

    private String tenantName;
    private String tenantPhone;
    private String tenantEmail;
    private String tenantIdentityCard;

    private Long roomId;
    private String roomName;
    private String hostelName;
    private String hostelAddress;
    private Double roomArea;
    private Integer roomFloor;
    private Integer bathCount;

    @Getter
    @Setter
    @Builder
    public static class ServiceChargeDto{
        private Long id;
        private String serviceName;
        private BigDecimal unitPrice;
        private String unitName;
        private boolean metered;
    }
}
