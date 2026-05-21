package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class InvoiceStatsResponse {
    private Long totalInvoices; //tong so hoa don
    private BigDecimal paidAmount; //so tien da thanh toan
    private BigDecimal debtAmount; //so tien chua thanh toan
}
