package com.example.rentnest.controller;

import com.example.rentnest.model.dto.response.InvoiceResponse;
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

import java.time.Year;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/tenant/invoices")
@PreAuthorize("hasAuthority('TENANT')")
public class TenantInvoiceController {
    private final InvoiceService invoiceService;

    public TenantInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceResponse>> getMyInvoices(
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        //sort ky hoa don moi nhat truoc, neu trung ky hoa don thi so sanh id moi hon truoc
        Pageable pageable = PageRequest.of(page, size);
        //tra page hoa don thuoc tenant hien tai
        return ResponseEntity.ok(invoiceService.getInvoicesForTenant(userDetails.getId(), normalizeYear(year), pageable));
    }
    @GetMapping("/current-unpaid")
    public ResponseEntity<InvoiceResponse> getCurrentUnpaidInvoice(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(invoiceService.getCurrentUnpaidInvoiceForTenant(userDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        //tra chi tiet hoa don neu hoa don thuoc tenant hien tai
        return ResponseEntity.ok(invoiceService.getInvoiceForTenant(userDetails.getId(), id));
    }

    private String normalizeYear(String year){
        //FE co the khong truyen year khi mo trang lan dau, backend mac dinh se lay nam hien tai
        return year == null || year.isBlank() ? String.valueOf(Year.now().getValue()) : year;
    }
}
