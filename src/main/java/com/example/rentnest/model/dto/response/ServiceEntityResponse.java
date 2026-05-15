package com.example.rentnest.model.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceEntityResponse {
    private Long id; // id cua service entity de FE goi update/delete dung record
    private Long hostelId; // id hostel de giup cho FE mo modal update
    private String hostelName; // ten co so de hien thi tren dropdown khi update
    private String serviceName; //ten service hien thi tren grid
    private BigDecimal unitPrice; // gia tien
    private String unitName; //don vi tinh cua service
    private boolean metered; // true neu ma can chot so hang thang
}
