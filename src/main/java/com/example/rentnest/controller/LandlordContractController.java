package com.example.rentnest.controller;

import com.example.rentnest.model.dto.request.TenantOnboardRequest;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
}
