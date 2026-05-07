package com.example.rentnest.controller;

import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.RentalRequestService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/rental-requests")
@PreAuthorize("hasAuthority('TENANT')")
public class TenantRentalRequestController {

    private final RentalRequestService rentalRequestService;

    public TenantRentalRequestController(RentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    @GetMapping
    public ResponseEntity<?> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(rentalRequestService.getRequestsForTenant(userDetails.getId(), pageable));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelRequest(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        rentalRequestService.cancelRequest(userDetails.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Hủy yêu cầu thành công!"));
    }
}
