package com.example.rentnest.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    private String floor;
    private int bathCount;
    private Long hostelId;
    private List<String> images;
}
