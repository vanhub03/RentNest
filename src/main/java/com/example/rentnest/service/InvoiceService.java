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
}
