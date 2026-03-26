package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE invoice_items set is_deleted = 1 where id = ?")
@SQLRestriction("is_deleted = 0")
public class InvoiceItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Nationalized
    @Column(nullable = false)
    private String description; // VD: "Tiền phòng tháng 3", "Tiền điện (Chỉ số: 100-150)"

    @Column(nullable = false)
    private BigDecimal amount; // Thành tiền của mục này

    // Có thể lưu thêm ID của Service nếu mục này là tiền dịch vụ (Tùy chọn)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;
}