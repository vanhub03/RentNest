package com.example.rentnest.controller;

import com.example.rentnest.model.dto.request.CoOccupantRequest;
import com.example.rentnest.model.dto.response.TenantRoomResponse;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.OccupantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tenant/rooms")
@PreAuthorize("hasAuthority('TENANT')")
public class TenantRoomController {
    private final OccupantService occupantService;
    private final ObjectMapper objectMapper;

    public TenantRoomController(OccupantService occupantService, ObjectMapper objectMapper) {
        this.occupantService = occupantService;
        this.objectMapper = objectMapper;
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
            @RequestPart("data") String jsonData,
            @RequestPart(value = "cccdFront", required = false) MultipartFile cccdFront,
            @RequestPart(value = "cccdBack", required = false) MultipartFile cccdBack,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) throws IOException {
        CoOccupantRequest request = objectMapper.readValue(jsonData, CoOccupantRequest.class);
        return ResponseEntity.ok(occupantService.addCoOccupant(userDetails.getId(), roomId, request, cccdFront, cccdBack));
    }
}
