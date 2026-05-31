package com.example.rentnest.service;

import com.example.rentnest.model.Occupant;
import com.example.rentnest.model.dto.request.CoOccupantRequest;
import com.example.rentnest.model.dto.response.TenantResponse;
import com.example.rentnest.model.dto.response.TenantRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OccupantService extends BaseService<Occupant, Long>{
    Page<TenantResponse> getTenantsByLandlord(Long landlordId, String keyword, Pageable pageable);
    List<TenantRoomResponse> getMyRooms(Long tenantId);
    TenantRoomResponse addCoOccupant(Long tenantId, Long roomId, CoOccupantRequest request);
}
