package com.example.rentnest.controller;

import com.example.rentnest.model.dto.request.TenantOnboardRequest;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.ContractService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@RestController
@RequestMapping("/api/landlord/contracts")
@PreAuthorize("hasAuthority('LANDLORD')")
public class LandlordContractController {

    private final ContractService contractService;

    public LandlordContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardTenant(
            @RequestPart("data") String jsonData,
            @RequestPart(value = "contractFile", required = false) MultipartFile contractFile,
            @RequestPart(value = "cccdFront", required = false) MultipartFile cccdFront,
            @RequestPart(value = "cccdBack", required = false) MultipartFile cccdBack,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        try {
            ObjectMapper mapper = new ObjectMapper();
            TenantOnboardRequest request = mapper.readValue(jsonData, TenantOnboardRequest.class);
            contractService.onboardNewTenant(request, contractFile, cccdFront, cccdBack, userDetails.getId());
            return ResponseEntity.ok(new MessageResponse("Thêm khách thuê và kích hoạt hợp đồng thành công"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/by-request/{requestId}/preview")
    public ResponseEntity<?> getContractPreview(@PathVariable Long requestId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(contractService.getPreviewForLandlord(userDetails.getId(), requestId));
    }

    @GetMapping
    public ResponseEntity<?> getContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contractService.getContractForLandlord(userDetails.getId(), status, pageable));
    }

    @PostMapping("/{contractId}/renew")
    public ResponseEntity<?> renewContract(
            @PathVariable Long contractId,
            @RequestParam(defaultValue = "12") int months,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(contractService.renewContract(userDetails.getId(), contractId, months));
    }
}
