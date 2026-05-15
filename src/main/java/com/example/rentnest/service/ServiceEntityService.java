package com.example.rentnest.service;

import com.example.rentnest.model.ServiceEntity;
import com.example.rentnest.model.dto.request.ServiceEntityRequest;
import com.example.rentnest.model.dto.response.ServiceEntityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceEntityService extends BaseService<ServiceEntity, Long>{
    Page<ServiceEntityResponse> getServiceForLandlord(Long landlordId, Long hostelId, Pageable pageable);
    List<ServiceEntityResponse> createService(Long landlordId, ServiceEntityRequest request);
    ServiceEntityResponse updateService(Long landlordId, Long serviceId, ServiceEntityRequest request);
    void deleteService(Long landlordId, Long serviceId);
}
