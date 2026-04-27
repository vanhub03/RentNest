package com.example.rentnest.controller;

import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.model.RentalRequest;
import com.example.rentnest.model.dto.request.RentalRequestStatusUpdateRequest;
import com.example.rentnest.model.dto.response.*;
import com.example.rentnest.repository.RentalRequestRepository;
import com.example.rentnest.repository.specification.RentalRequestSpecification;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.HostelService;
import com.example.rentnest.service.OccupantService;
import com.example.rentnest.service.RentalRequestService;
import com.example.rentnest.service.RoomService;
import freemarker.template.TemplateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/landlord")
@PreAuthorize("hasAuthority('LANDLORD')")
public class LandlordControllder {
    private final HostelService hostelService;
    private final RoomService roomService;
    private final OccupantService occupantService;
    private final RentalRequestRepository rentalRequestRepository;
    private final RentalRequestService rentalRequestService;

    public LandlordControllder(HostelService hostelService, RoomService roomService, OccupantService occupantService, RentalRequestRepository rentalRequestRepository, RentalRequestService rentalRequestService) {
        this.hostelService = hostelService;
        this.roomService = roomService;
        this.occupantService = occupantService;
        this.rentalRequestRepository = rentalRequestRepository;
        this.rentalRequestService = rentalRequestService;
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

    @GetMapping("/rooms")
    public ResponseEntity<Page<RoomCardResponse>> getRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long hostelId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Page<RoomCardResponse> response = roomService.getRoomByLandlord(userDetails.getId(), keyword, status, hostelId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomCardResponse>> getAvailableRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<RoomCardResponse> rooms = roomService.getAvailableRooms(userDetails.getId());
        return ResponseEntity.ok(rooms);
    }


    @GetMapping("/tenants")
    public ResponseEntity<Page<TenantResponse>> getTenants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<TenantResponse> responses = occupantService.getTenantsByLandlord(userDetails.getId(), keyword, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/rental-requests/stats")
    public ResponseEntity<?> getRentalRequests(@AuthenticationPrincipal UserDetailsImpl userDetails){
        StatsResponse statsResponse = StatsResponse.builder()
                .total(rentalRequestRepository.countByRoomHostelOwnerId(userDetails.getId()))
                .pending(rentalRequestRepository.countByRoomHostelOwnerIdAndStatus(userDetails.getId(), RequestStatus.PENDING))
                .approved(rentalRequestRepository.countByRoomHostelOwnerIdAndStatus(userDetails.getId(), RequestStatus.APPROVED))
                .rejected(rentalRequestRepository.countByRoomHostelOwnerIdAndStatus(userDetails.getId(), RequestStatus.REJECTED))
                .build();
        return ResponseEntity.ok(statsResponse);
    }

    @GetMapping("/rental-requests")
    public ResponseEntity<?> getRentalRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String tenantName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<RentalRequest> spec = RentalRequestSpecification.rentalRequestSpecification(userDetails.getId(), status, roomId, tenantName);
        Page<RentalRequest> requests = rentalRequestRepository.findAll(spec, pageable);
        return ResponseEntity.ok(requests.map(req -> RentalRequestResponse.builder()
                        .id(req.getId())
                        .tenantName(req.getTenant().getFullname())
                        .tenantPhone(req.getTenant().getPhoneNumber())
                        .roomId(req.getRoom().getId())
                        .roomName(req.getRoom().getRoomName())
                        .hostelAddress(req.getRoom().getHostel().getAddressDetail())
                        .expectedMoveInDate(req.getExpectedMoveInDate())
                        .createdAt(req.getCreatedAt())
                        .status(req.getStatus().name())
                        .build()));
    }

    @GetMapping("/rental-requests/{id}")
    public ResponseEntity<RentalRequestResponse> getRentalRequestDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(rentalRequestService.getRequestDetailForLandlord(userDetails.getId(), id));
    }

    @GetMapping("rental-requests/{id}/status")
    public ResponseEntity<MessageResponse> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody RentalRequestStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) throws TemplateException, IOException {
        RequestStatus status = RequestStatus.valueOf(request.getStatus());
        rentalRequestService.updateStatus(userDetails.getId(), id, status, request.getRejectReason());
        return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái thành công"));
    }
}
