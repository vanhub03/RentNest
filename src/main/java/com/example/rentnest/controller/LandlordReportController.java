package com.example.rentnest.controller;

import com.example.rentnest.model.dto.response.LandlordReportOverviewResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.LandlordReportService;
import com.example.rentnest.service.impl.LandlordReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/landlord/reports")
@PreAuthorize("hasAuthority('LANDLORD')")
public class LandlordReportController {
    @Autowired
    private LandlordReportService landlordReportService;
    @GetMapping("/overview")
    public ResponseEntity<LandlordReportOverviewResponse> getOverview(@RequestParam(required = false) String invoiceMonth,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String normalizedInvoiceMonth = normalizeMonth(invoiceMonth);
        Long landlordID = userDetails.getId();
        return ResponseEntity.ok(landlordReportService.getOverview(landlordID, normalizedInvoiceMonth));
    }
    private String normalizeMonth(String invoiceMonth) {
        //FE co the khong truyen invoiceMonth khi mo trang lan dau, backend mac dinh se lay thang hien tai
        return invoiceMonth == null || invoiceMonth.isBlank() ? YearMonth.now().toString() : invoiceMonth;
    }
}
