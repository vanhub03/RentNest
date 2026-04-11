package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RoomDetailResponse {
    private Long id;
    private String roomName;
    private Double area;
    private BigDecimal basePrice;
    private String status;
    private String bedType;
    private int bathCount;

    private String hostelName;
    private String fullAddress;
    private String description;

    private List<String> images;
    private List<ServiceDto> services;

    private String landlordName;
    private String landlordPhone;

    @Data
    @Builder
    public static class ServiceDto{
        private String serviceName;
        private BigDecimal price;
        private String unit;
    }
}
