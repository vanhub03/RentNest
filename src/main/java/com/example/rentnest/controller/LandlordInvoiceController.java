package com.example.rentnest.controller;

import com.example.rentnest.enums.InvoiceStatus;
import com.example.rentnest.model.dto.request.InvoiceGenerateRequest;
import com.example.rentnest.model.dto.response.InvoiceResponse;
import com.example.rentnest.model.dto.response.InvoiceStatsResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/landlord/invoices")
@PreAuthorize("hasAuthority('LANDLORD')")
public class LandlordInvoiceController {

    private final InvoiceService invoiceService;

    public LandlordInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceResponse>> getInvoices(@RequestParam(required = false) String invoiceMonth,
                                                             @RequestParam(required = false)InvoiceStatus status,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        //bang hoa don phan trang theo thang, trang thai, neu khong gui thang thi dung thang hien tai
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(invoiceService.getInvoicesForLandlord(userDetails.getId(), invoiceMonth, status, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<InvoiceStatsResponse> getStats(
            @RequestParam(required = false) String invoiceMonth,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        //stats phuc vu 3 card tren dau trang
        return ResponseEntity.ok(invoiceService.getStatsForLandlord(userDetails.getId(), normalizeMonth(invoiceMonth)));
    }

    @PostMapping("/generate")
    public ResponseEntity<List<InvoiceResponse>> generateInvoices(
            @RequestBody InvoiceGenerateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        return ResponseEntity.ok(invoiceService.generateInvoices(userDetails.getId(), request));
    }

    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<InvoiceResponse> markPaid(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        //xac nhan thu du mot hoa don, service se kiem tra hoa don thuoc landlord hien tai
        return ResponseEntity.ok(invoiceService.markPaid(userDetails.getId(), id));
    }

    private String normalizeMonth(String invoiceMonth){
        //FE co the khong truyen invoiceMonth khi mo trang lan dau, backend mac dinh se lay thang hien tai
        return invoiceMonth == null || invoiceMonth.isBlank() ? YearMonth.now().toString() : invoiceMonth;
    }
}
