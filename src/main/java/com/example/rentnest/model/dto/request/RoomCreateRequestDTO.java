package com.example.rentnest.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class RoomCreateRequestDTO {
    private Long hostelId;
    private String roomName;
    private BigDecimal basePrice;
    private Double area; //dien tich
    private Integer floor; // so tang
    private Integer bathCount; // so nha ve sinh
    private String bedType;
    private String status;
}
