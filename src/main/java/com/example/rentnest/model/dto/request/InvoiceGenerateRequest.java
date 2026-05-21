package com.example.rentnest.model.dto.request;

import com.example.rentnest.model.MeterReading;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceGenerateRequest {
    private Long hostelId; //toa nha can sinh hoa don, se chi sinh hoa don cho hop dong con active cua toa nha nay
    private String invoiceMonth; //thang sinh hoa don
    private List<MeterReadingDto> readings; //danh sach chi so dien/nuoc landlord nhap trong model

    //3 dong MetterReadingDto -> chay qua 3 dong va chi lay dong nao co du 3 thong tin roomId, serviceId, newIndex

    @Getter
    @Setter
    public static class MeterReadingDto {
        private Long roomId; // phong duoc chot chi so
        private Long serviceId; //dich vu duoc chot chi so (dien/nuoc)
        private Double oldIndex; //chi so cu, neu null thi backend se tu lay chi so cua thang truoc
        private Double newIndex; //chi so moi bat buoc phai co de tinh luong tieu thu
    }
}
// vu du
//dong 1: list readings co 3 item:
//item1: roomId: 1, serviceId: 1(dien), oldIndex: 0, newIndex: 2
//item2: roomId: 1, serviceId: 2(nuoc), oldIndex: 0, newIndex: 3

//readingMap = {<"1:1", item1>, <"1:2", item2>}