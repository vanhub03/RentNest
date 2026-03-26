package com.example.rentnest.service.impl;

import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.repository.HostelRepository;
import com.example.rentnest.repository.specification.HostelSpecification;
import com.example.rentnest.service.HostelImageService;
import com.example.rentnest.service.HostelService;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class HostelServiceImpl extends BaseServiceImpl<Hostel, Long, HostelRepository> implements HostelService {
    private final HostelRepository hostelRepository ;
    private final CloudinaryService cloudinaryService;
    private final HostelImageService hostelImageService;




    public HostelServiceImpl(HostelRepository hostelRepository, CloudinaryService cloudinaryService, HostelImageService hostelImageService) {
        super();
        this.hostelRepository = hostelRepository;
        this.cloudinaryService = cloudinaryService;
        this.hostelImageService = hostelImageService;
    }



    @Override
    public Page<HostelCardResponse> getHostelsByLandlord(Long landlordId, String keyword, Pageable pageable) {
        Specification<Hostel> spec = HostelSpecification.filterHostelForLandlord(landlordId, keyword);
        Page<Hostel> hostels = hostelRepository.findAll(spec, pageable);
        return hostels.map(this::mapToHostelResponse);
    }

    @Override
    public HostelCardResponse updateByLandlord(Long id, RoomCreateRequestDTO roomCreateRequestDTO, List<MultipartFile> listImage, Long landlordID) throws IOException {
        return null;
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

    @Override
    public void deleteHostel(Hostel hostel) {

        hostelRepository.delete(hostel);
    }


}


