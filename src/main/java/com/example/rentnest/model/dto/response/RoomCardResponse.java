package com.example.rentnest.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomCardResponse {
    private Long id;
    private String title;
    private Double area;
    private BigDecimal price;
    private String status;
    private String location;
    private String thumbnail;
    private String bedType;
    private int bathCount;
}
