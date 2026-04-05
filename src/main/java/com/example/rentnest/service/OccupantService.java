package com.example.rentnest.service;

import com.example.rentnest.model.Occupant;
import com.example.rentnest.model.dto.response.TenantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OccupantService extends BaseService<Occupant, Long>{
    Page<TenantResponse> getTenantsByLandlord(Long landlordId, String keyword, Pageable pageable);
}
