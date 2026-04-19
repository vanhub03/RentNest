package com.example.rentnest.repository;

import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.model.RentalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RentalRequestRepository extends BaseRepository<RentalRequest, Long> {
    Page<RentalRequest> findAll(Specification<RentalRequest> specification, Pageable pageable);
    long countByRoomHostelOwnerId(Long ownerId);
    long countByRoomHostelOwnerIdAndStatus(Long ownerId, RequestStatus status);
}
