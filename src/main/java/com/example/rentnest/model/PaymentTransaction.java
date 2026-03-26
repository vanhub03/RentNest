package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE payment_transactions set is_deleted = 1 where id = ?")
@SQLRestriction("is_deleted = 0")
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid; // Số tiền đã trả trong đợt này

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // VD: "CASH", "BANK_TRANSFER", "VNPAY"

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Nationalized
    private String note; // Ghi chú (VD: "Mẹ em Thảo ck trả tiền phòng")
}