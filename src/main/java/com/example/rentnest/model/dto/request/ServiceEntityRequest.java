package com.example.rentnest.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceEntityRequest {
    private Long hostelId; // hostel cụ thể được gắn service; có thể null khi apply cho tất cả tòa nhà
    private boolean applyAllHostels; // khi biến này true -> apply cho tất cả tòa nhá
    private String serviceName; //tên dịch vụ
    private BigDecimal unitPrice; //giá
    private String unitName; // đơn vị tính như kwh, khối, người
    private boolean metered; // check cần chot so hay tinh tron goi theo thang
}
