package com.example.rentnest.service;

import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HostelService extends BaseService<Hostel, Long>{
    Page<HostelCardResponse> getHostelsByLandlord(Long landlordId, String keyword, Pageable pageable);
}
