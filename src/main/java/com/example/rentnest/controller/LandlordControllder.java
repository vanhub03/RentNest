package com.example.rentnest.controller;

import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.HostelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/landlord")
@PreAuthorize("hasAuthority('LANDLORD')")
public class LandlordControllder {
    private final HostelService hostelService;

    public LandlordControllder(HostelService hostelService) {
        this.hostelService = hostelService;
    }

    @GetMapping("/hostels")
    public ResponseEntity<Page<HostelCardResponse>> getHostels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<HostelCardResponse> response = hostelService.getHostelsByLandlord(userDetails.getId(), keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
