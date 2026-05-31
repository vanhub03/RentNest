package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class TenantRoomResponse {
    private Long contractId; //Id hop dong active
    private Long roomId; //id phong
    private String roomName; //ten phong
    private String hostelName; //tem toa nha
    private String hostelAddress; //dia chi toa nha
    private String landlordName; //ten chu nha
    private String landlordPhone; //so dien thoai chu nha
    private BigDecimal monthlyRent; //gia thue hang thang
    private LocalDate startDate; //ngay bat dau hop dong
    private LocalDate endDate; //ngay ket thuc hop dong
    private String contractStatus; //trang thai hop dong
    private String thumbnailUrl; //anh dai dien phong
    private List<OccupantDto> occupants;

    @Getter
    @Builder
    public static class OccupantDto {
        private Long id;
        private String fullName;
        private String phoneNumber;
        private String identityCard; //cccd
        private boolean representative; //se la true neu la nguoi dai dien hop dong
        private boolean active;
    }
}
