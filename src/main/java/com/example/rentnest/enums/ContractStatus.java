package com.example.rentnest.enums;

public enum ContractStatus {
    DRAFT,                 // Hợp đồng nháp (Hệ thống tự tạo)
    WAITING_FOR_SIGNATURE, // Chờ khách ký & thanh toán
    ACTIVE,                // Đang hiệu lực
    EXPIRED,               // Hết hạn
    TERMINATED             // Chấm dứt trước hạn
}
