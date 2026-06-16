package com.example.rentnest.service;

import com.example.rentnest.model.Occupant;
import com.example.rentnest.model.dto.request.AddRoomOccupantsRequest;
import com.example.rentnest.model.dto.request.CoOccupantRequest;
import com.example.rentnest.model.dto.response.TenantResponse;
import com.example.rentnest.model.dto.response.TenantRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OccupantService extends BaseService<Occupant, Long>{
    Page<TenantResponse> getTenantsByLandlord(Long landlordId, String keyword, Pageable pageable);
    List<TenantRoomResponse> getMyRooms(Long tenantId);
    TenantRoomResponse addCoOccupant(Long tenantId, Long roomId, CoOccupantRequest request, MultipartFile cccdFront, MultipartFile cccdBack) throws IOException;
    TenantResponse getTenantDetailForLandlord(Long landlordId, Long tenantId);
    List<TenantResponse> addOccupantsToRentedRooms(Long landlordId, Long roomId, AddRoomOccupantsRequest request, List<MultipartFile> cccdFronts, List<MultipartFile> cccdBacks) throws IOException;

}
