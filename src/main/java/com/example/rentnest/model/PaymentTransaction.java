package com.example.rentnest.model;

import com.example.rentnest.enums.PaymentStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status;

    @Column(name = "order_id", unique = true)
    private String orderId; // ma don hang de gui sang momo

    @Column(name = "request_id", unique = true)
    private String requestId; //ma request de gui sang momo

    @Column(name = "payment_url", length = 1000)
    private String paymentUrl; //payurl momo tra ve de FE redirect

    @Column(name = "momo_trans_id")
    private String momoTransId; // ma giao dich momo tra ve sau khi thanh toan thanh cong

    @Column(name = "momo_result_code")
    private int momoResultCode; //result code tra ve tu momo

    @Nationalized
    @Column(name = "momo_message")
    private String momoMessage;

    @Column(name = "momo_pay_type")
    private String momoPayType; //hinh thuc thanh toan momo

    @Nationalized
    @Column(name = "failure_reason")
    private String failureReason; //ly do that bai

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Nationalized
    private String note; // Ghi chú (VD: "Mẹ em Thảo ck trả tiền phòng")
}