package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MomoPaymentUrlResponse {
    private final String orderId; // Mã đơn hàng gửi sang momo
    private final String requestId; // Mã request gửi sang momo
    private final BigDecimal amount; // Số tiền cọc
    private final String paymentUrl; // url thanh toán momo để frontend redirect
    private final String qrCodeUrl; // chuỗi qr momo trả về nếu sau này muốn tự render QR trong app
    private final String deepLink; // Deeplink mở app momo trên mobile
}
