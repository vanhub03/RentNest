package com.example.rentnest.service;

import com.example.rentnest.enums.InvoiceStatus;
import com.example.rentnest.model.Invoice;
import com.example.rentnest.model.dto.request.InvoiceGenerateRequest;
import com.example.rentnest.model.dto.response.InvoiceResponse;
import com.example.rentnest.model.dto.response.InvoiceStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService extends BaseService<Invoice, Long>{
    Page<InvoiceResponse> getInvoicesForLandlord(Long landlordId, String invoiceMonth, InvoiceStatus status, Pageable pageable);
    InvoiceStatsResponse getStatsForLandlord(Long landlordId, String invoiceMonth);
    List<InvoiceResponse> generateInvoices(Long landlordId, InvoiceGenerateRequest request);
    InvoiceResponse markPaid(Long landlordId, Long invoiceId);
    //tenant xem danh sach hoa don cua chinh minh theo nam va phan trang
    Page<InvoiceResponse> getInvoicesForTenant(Long tenantId, String year, Pageable pageable);
    //tenant lay hoa don chua thanh toan gan nhat de FE hien thij noi bat phia tren
    InvoiceResponse getCurrentUnpaidInvoiceForTenant(Long tenantId);
    //tenant xem chi tiet hoa don
    InvoiceResponse getInvoiceForTenant(Long tenantId, Long invoiceId);


}
