package com.example.rentnest.model;
import com.example.rentnest.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE invoices set is_deleted = 1 where id = ?")
@SQLRestriction("is_deleted = 0")
public class Invoice extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "invoice_month")
    private String invoiceMonth; // Format: "2024-02"

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @Column(name = "due_date")
    private LocalDate dueDate; // Hạn đóng tiền
    // THÊM CÁC DÒNG NÀY VÀO BẢNG Invoice.java

    @Column(name = "amount_paid")
    private BigDecimal amountPaid = BigDecimal.ZERO; // Số tiền đã trả (để tính nợ)

    // Chi tiết các khoản thu trong hóa đơn này
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;

    // Lịch sử các lần chuyển tiền cho hóa đơn này
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentTransaction> transactions;
}
