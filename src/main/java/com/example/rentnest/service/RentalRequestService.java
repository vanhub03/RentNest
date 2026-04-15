package com.example.rentnest.service;

import com.example.rentnest.model.RentalRequest;
import com.example.rentnest.model.dto.response.RequestRentRoom;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface RentalRequestService extends BaseService<RentalRequest, Long>{
    void approveRequest(Long requestId, Long landlordId);
    void createRequest(RequestRentRoom requestRentRoom) throws IOException, TemplateException;
}
