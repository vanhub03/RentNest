package com.example.rentnest.service.impl;

import com.example.rentnest.model.Contract;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.HostelRepository;
import com.example.rentnest.repository.specification.HostelSpecification;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class HostelServiceImpl extends BaseServiceImpl<Hostel, Long, HostelRepository> implements HostelService {

    @Autowired
    private HostelRepository hostelRepository;

    @Override
    public Page<HostelCardResponse> getHostelsByLandlord(Long landlordId, String keyword, Pageable pageable) {
        Specification<Hostel> spec = HostelSpecification.filterHostelForLandlord(landlordId, keyword);
        Page<Hostel> hostels = hostelRepository.findAll(spec, pageable);
        return hostels.map(this::mapToHostelResponse);
    }
    private HostelCardResponse mapToHostelResponse(Hostel hostel) {
        return HostelCardResponse.builder()
                .id(hostel.getId())
                .name(hostel.getName())
                .addressDetail(hostel.getAddressDetail())
                .ward(hostel.getWard())
                .district(hostel.getDistrict())
                .city(hostel.getCity())
                .description(hostel.getDescription())
                .roomCount(hostel.getRooms() != null ? hostel.getRooms().size() : 0)
                .serviceCount(hostel.getServices() != null ? hostel.getServices().size() : 0)
                .imageCount(hostel.getImages() != null ? hostel.getImages().size() : 0)
                .build();
    }
}
