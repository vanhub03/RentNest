package com.example.rentnest.repository;

import com.example.rentnest.model.Contract;

import java.util.Optional;

public interface ContractRepository extends BaseRepository<Contract, Long>{
    Optional<Contract> findByRentalRequest_Id(Long rentalRequestId);
    Optional<Contract> findByRentalRequest_IdAndRoom_Hostel_Owner_Id(Long rentalRequestId, Long landlordId);
    Optional<Contract> findByRentalRequest_IdAndRentalRequest_Tenant_Id(Long rentalRequestId, Long tenantId);
}
