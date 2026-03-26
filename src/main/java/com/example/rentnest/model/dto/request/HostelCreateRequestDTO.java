package com.example.rentnest.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelCreateRequestDTO {
    private String name;
    private String addressDetail;
    private String wardCode;
    private String ward; //ten phuong
    private String districtCode; //ma quan huyen
    private String district;
    private String cityCode; //ma thanh pho, tinh
    private String city;
    private String description;
}
