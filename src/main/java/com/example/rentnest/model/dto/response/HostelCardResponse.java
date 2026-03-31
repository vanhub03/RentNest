package com.example.rentnest.model.dto.response;

import com.example.rentnest.model.RoomImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HostelCardResponse {
    private Long id;
    private String name;
    private String addressDetail;
    private String ward;
    private String wardCode;
    private String district;
    private String districtCode;
    private String city;
    private String cityCode;
    private String description;
    private Integer roomCount;
    private Integer serviceCount;
    private Integer imageCount;
    private List<String> images;
}
