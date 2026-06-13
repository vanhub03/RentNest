package com.example.rentnest.repository;

import com.example.rentnest.enums.ContractStatus;
import com.example.rentnest.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends BaseRepository<Contract, Long>{
    Optional<Contract> findByRentalRequest_Id(Long rentalRequestId);
    Optional<Contract> findByRentalRequest_IdAndRoom_Hostel_Owner_Id(Long rentalRequestId, Long landlordId);
    Optional<Contract> findByRentalRequest_IdAndRentalRequest_Tenant_Id(Long rentalRequestId, Long tenantId);
    Page<Contract> findByRoom_Hostel_Owner_IdAndStatus(Long landlordId, ContractStatus status, Pageable pageable);
    Page<Contract> findByRoom_Hostel_Owner_Id(Long landlordId, Pageable pageable);
    Optional<Contract> findByIdAndRoom_Hostel_Owner_Id(Long id, Long landlordId);
    Optional<Contract> findByRoom_IdAndRoom_Hostel_Owner_Id(Long id, Long landlordId);
    @Query("select c from Contract c " +
            "where c.room.hostel.owner.id = :landlordId " +
            "and c.status = :status " +
            "and c.endDate is not null " +
            "and c.endDate between :fromDate and :toDate")
    Page<Contract> findExpiringContracts(
            @Param("landlordId") Long landlordId,
            @Param("status") ContractStatus status,
            @Param("fromDate")LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
            );
    // vd: endDate cua hop dong la 1/5
    // hom nay la 1/5 -> fromDate la 1/5, toDate la 30/5
    List<Contract> findByRoom_Hostel_Owner_IdAndRoom_Hostel_IdAndStatus(Long landlordId, Long hostelId, ContractStatus status);

    @Query("select c " +
            "from Contract c " +
            "left join c.representativeOccupant o " +
            "left join o.userAccount u " +
            "left join c.rentalRequest rr " +
            "left join rr.tenant requestTenant " +
            "where c.status = :status " +
            "and (u.id = :tenantId) or requestTenant.id = :tenantId " +
            "order by c.startDate desc, c.id desc")
    List<Contract> findActiveContractsForTenant(@Param("tenantId") Long tenantId, @Param("status") ContractStatus status);

    @Query("select c " +
            "from Contract c " +
            "left join c.representativeOccupant o " +
            "left join o.userAccount u " +
            "left join c.rentalRequest rr " +
            "left join rr.tenant requestTenant " +
            "where c.status = :status " +
            "and c.room.id = :roomId " +
            "and (u.id = :tenantId or requestTenant.id = :tenantId) " +
            "order by c.startDate desc, c.id desc")
    Optional<Contract> findTenantActiveContractByRoom(@Param("tenantId") Long tenantId, @Param("roomId") Long roomId, @Param("status") ContractStatus status);
}
