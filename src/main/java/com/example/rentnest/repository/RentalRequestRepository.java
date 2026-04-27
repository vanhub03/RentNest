package com.example.rentnest.repository;

import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.model.RentalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface RentalRequestRepository extends BaseRepository<RentalRequest, Long> {
    Page<RentalRequest> findAll(Specification<RentalRequest> specification, Pageable pageable);
    long countByRoomHostelOwnerId(Long ownerId);
    long countByRoomHostelOwnerIdAndStatus(Long ownerId, RequestStatus status);
    Page<RentalRequest> findByTenant_Id(Long tenantId, Pageable pageable);
    Optional<RentalRequest> findByIdAndTenant_Id(Long id, Long tenantId);
    Optional<RentalRequest> findByIdAndRoom_Hostel_Owner_Id(Long id, Long ownerId);
    List<RentalRequest> findByRoomIdAndStatusAndIdNot(Long roomId, RequestStatus status, Long requestId);
}
