package com.example.rentnest.model.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
    private Long id; //id hoa don
    private String hostelName; //ten co so
    private Long roomId; //id phong
    private String roomName; //ten phong
    private String tenantName; //nguoi dai dien hop dong
    private String invoiceMonth; //thang sinh hoa don
    private BigDecimal roomAmount; //tien phong
    private BigDecimal serviceAmount; //tien dich vu
    private BigDecimal totalAmount; //tong so tien phai thu
    private BigDecimal amountPaid; //so tien da thu
    private String status; //PENDING, PAID, OVERDUE, CANCELLED
    private LocalDate duwDate; //han thanh toan hoa don
    private List<ItemDto> items;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDto {
        private Long id; //id dong hoa don
        private Long serviceId; //dich vu
        private Double oldIndex; // so moi
        private Double newIndex;
        private String description; //mo ta
        private BigDecimal amount; //thanh tien cua dong
    }
}
