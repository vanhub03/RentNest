package com.example.rentnest.enums;

public enum PaymentStatus {
    PENDING, //Giao dịch đã được tạo ở RentNest, nhưng khách hàng chưa thanh tóoán ở momo
    SUCCESS, //Momo đã trả về thanh toán thành công
    FAILED // Momo đã trả về thất bại
}
