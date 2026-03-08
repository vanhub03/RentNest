package com.example.rentnest.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HostelCardResponse {
    private Long id;
    private String name;
    private String addressDetail;
    private String ward;
    private String district;
    private String city;
    private String description;
    private Integer roomCount;
    private Integer serviceCount;
    private Integer imageCount;
}
