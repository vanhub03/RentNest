package com.example.rentnest.service;

import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HostelService extends BaseService<Hostel, Long>{
    void deleteHostel(Hostel hostel);

    Page<HostelCardResponse> getHostelsByLandlord(Long landlordId, String keyword, Pageable pageable);
    HostelCardResponse updateByLandlord(Long id, RoomCreateRequestDTO roomCreateRequestDTO, List<MultipartFile> listImage, Long landlordID
    ) throws IOException;

    void delete(Hostel hostel);
}

