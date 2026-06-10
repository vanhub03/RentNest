package com.example.rentnest.model.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class LandlordReportOverviewResponse {
    private String invoiceMonth; //bao cao thang dang xem
    private Long totalInvoices;
    private BigDecimal totalRevenue; //tong doanh thu
    private BigDecimal paidAmount; //tong tien da thu
    private BigDecimal debtAmount; //tong tien no.
    private List<RevenueSlice> revenueStructure; // du lieu bieu do tron
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RevenueSlice {  //bieu do tron
        private String label;  // ten khoan thu
        private BigDecimal amount; // so tien cua khoan thu day
    };
    private List<CashFlowRow> cashFlowByHostel; //du lieu bieu do cot
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CashFlowRow  { // bieu do cot
        private Long hostelId;
        private String hostelName;
        private BigDecimal roomRevenue; //tong tien phong
        private BigDecimal serviceRevenue; //tong tien dich vu
        private BigDecimal totalRevenue; //tong tien phong cua co so do
        private BigDecimal expenseAmount; //tong chi phi dich vu khac
        private BigDecimal grossProfit; // loi nhuan gop
    }


}


