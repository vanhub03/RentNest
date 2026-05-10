package com.example.rentnest.model;

import com.example.rentnest.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposit_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE deposit_payments set is_deleted = 1 where id = ?")
@SQLRestriction("is_deleted = 0")
public class DepositPayment extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract; //Hợp đng cần kích hoạt sau khi khách thanh toán cọc thành công

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant; //Tài koản tenant tạo giao dịch, dùng để kiểm tra quyền khi redirect về frontend

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId; //Mã đơn hàng gửi sang Momo

    @Column(name = "request_id", nullable = false, unique = true, length = 100)
    private String requestId; //mã request gửi sang momo

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // Số tiền cọc cần thanh toán

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status; //Trạng thái xử lý giao dịch

    @Column(name = "payment_url", length = 2000)
    private String paymentUrl; //url momo tr về để frontend chuyển người dùng sang trang thanh toán

    @Column(name = "momo_trans_id", length = 100)
    private String momoTransId; //Mã giao dịch phía Momo trả về sau khi thanh toán

    @Column(name = "momo_result_code")
    private Integer momoResultCode; // Mã kết quả momo, nếu là 0 -> thành công

    @Column(name = "momo_message", length = 500)
    private String momoMessage; //Nội dung message trả về từ momos

    @Column(name = "momo_pay_type", length = 50)
    private String momoPayType; // Hình thức thanh toán momo trar về -> ví dụ web, app, qr

    @Column(name = "paid_at")
    private LocalDateTime paidAt; //Thời điểm mà RentNest xác định giao dịch thành công

    @Column(name = "failure_reason")
    private String failureReason; //lý do thất bại
}
