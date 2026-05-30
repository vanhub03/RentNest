package com.example.rentnest.controller;

import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.MomoPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tenant/payments")
@PreAuthorize("hasAuthority('TENANT')")
public class TenantPaymentController {

    private final MomoPaymentService momoPaymentService;

    public TenantPaymentController(MomoPaymentService momoPaymentService) {
        this.momoPaymentService = momoPaymentService;
    }

    @PostMapping("deposit/by-request/{requestId}/momo")
    public ResponseEntity<?> createDepositPaymentUrl(@PathVariable Long requestId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(momoPaymentService.createDepositPaymentUrl(userDetails.getId(), requestId));
    }

    @PostMapping("/deposit/momo-return")
    public ResponseEntity<?> handleDepositReturn(@RequestBody Map<String, String> momoParams, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(
                momoPaymentService.handleDepositReturn(userDetails.getId(), momoParams)
        );
    }

    @PostMapping("/invoice/{invoiceId}/momo")
    public ResponseEntity<?> createinvoicePaymentUrl(
            @PathVariable Long invoiceId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(momoPaymentService.createInvoicePaymentUrl(userDetails.getId(), invoiceId));
    }

    @PostMapping("/invoice/momo-return")
    public ResponseEntity<?> handleInvoiceReturn(
            @RequestBody Map<String, String> momoParams,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(momoPaymentService.handleInvoiceReturn(userDetails.getId(), momoParams));
    }
}
