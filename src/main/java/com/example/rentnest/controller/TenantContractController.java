package com.example.rentnest.controller;

import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenant/contracts")
@PreAuthorize("hasAuthority('TENANT')")
public class TenantContractController {

    private final ContractService contractService;

    public TenantContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/by-request/{requestId}/preview")
    public ResponseEntity<?> getContractPreview(@PathVariable Long requestId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contractService.getPreviewForTenant(userDetails.getId(), requestId));
    }
}
