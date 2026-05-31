package com.example.rentnest.controller;

import com.example.rentnest.model.dto.request.ServiceEntityRequest;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.model.dto.response.ServiceEntityResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.ServiceEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landlord/services")
@PreAuthorize("hasAuthority('LANDLORD')")
public class LandlordServiceController {

    private final ServiceEntityService serviceEntityService;

    public LandlordServiceController(ServiceEntityService serviceEntityService) {
        this.serviceEntityService = serviceEntityService;
    }

    @GetMapping
    public ResponseEntity<Page<ServiceEntityResponse>> getServices(
            @RequestParam(required = false) Long hostelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(serviceEntityService.getServiceForLandlord(userDetails.getId(), hostelId, pageable));
    }

    @PostMapping
    public ResponseEntity<List<ServiceEntityResponse>> createService(
            @RequestBody ServiceEntityRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        return ResponseEntity.ok(serviceEntityService.createService(userDetails.getId(), request));
    }

    @PutMapping
    public ResponseEntity<ServiceEntityResponse> updateService(
            @PathVariable Long id,
            @RequestBody ServiceEntityRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(serviceEntityService.updateService(userDetails.getId(), id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> deleteService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        serviceEntityService.deleteService(userDetails.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Đã xóa dịch vụ được chỉ định"));
    }
}
