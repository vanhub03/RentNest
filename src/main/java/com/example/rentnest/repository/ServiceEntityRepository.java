package com.example.rentnest.repository;

import com.example.rentnest.model.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceEntityRepository extends BaseRepository<ServiceEntity, Long>{
    // lay toan bo service thuoc cac hostel cua landlord dang dang nhap
    Page<ServiceEntity> findByHostel_Owner_Id(Long landlordId, Pageable pageable);
    // loc service theo mot hostel cu the cua landlord dang dang nhap
    Page<ServiceEntity> findByHostel_Owner_IdAndHostel_Id(Long landlordId, Long hostelId, Pageable pageable);
}
