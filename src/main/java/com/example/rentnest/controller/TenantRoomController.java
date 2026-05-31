package com.example.rentnest.controller;

import com.example.rentnest.model.dto.request.CoOccupantRequest;
import com.example.rentnest.model.dto.response.TenantRoomResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.OccupantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/rooms")
@PreAuthorize("hasAuthority('TENANT')")
public class TenantRoomController {
    private final OccupantService occupantService;

    public TenantRoomController(OccupantService occupantService) {
        this.occupantService = occupantService;
    }

    @GetMapping
    public ResponseEntity<List<TenantRoomResponse>> getMyRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        return ResponseEntity.ok(occupantService.getMyRooms(userDetails.getId()));
    }

    @PostMapping("/{roomId}/occupants")
    public ResponseEntity<TenantRoomResponse> addOccupant(
            @PathVariable Long roomId,
            @RequestBody CoOccupantRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        return ResponseEntity.ok(occupantService.addCoOccupant(userDetails.getId(), roomId, request));
    }
}
