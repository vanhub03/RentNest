package com.example.rentnest.service;

import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.model.RentalRequest;
import com.example.rentnest.model.dto.response.RentalRequestResponse;
import com.example.rentnest.model.dto.response.RequestRentRoom;
import freemarker.template.TemplateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface RentalRequestService extends BaseService<RentalRequest, Long>{
    void approveRequest(Long requestId, Long landlordId);
    void createRequest(RequestRentRoom requestRentRoom) throws IOException, TemplateException;
    RentalRequestResponse getRequestDetailForLandlord(Long landlordId, Long requestId);
    void updateStatus(Long landlordId, Long requestId, RequestStatus requestStatus, String rejectReason) throws TemplateException, IOException;
    Page<RentalRequestResponse> getRequestsForTenant(Long tenantId, Pageable pageable);
    void cancelRequest(Long tenantId, Long requestId);
}
