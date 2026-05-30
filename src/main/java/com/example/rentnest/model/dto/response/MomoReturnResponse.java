package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MomoReturnResponse {
    private final boolean success; // true khi momo hợp lệ và hợp đồng đã được kích hoạt
    private final String message; //thôngbasoo để frontend hiển thị
    private String paymentType; //DEPOSIT hoac INVOICE de FE biet ma hien thi
    private final Long contractId; // id hợp đồng đã thanh toán
    private final Long rentalRequestId; // ID yêu caầu thuê để redirect lại màn hợp đồng
    private final Long invoiceId; //id hoa don neu thanh toan hoa don

}
