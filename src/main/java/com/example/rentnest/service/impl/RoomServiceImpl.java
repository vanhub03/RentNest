package com.example.rentnest.service.impl;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Contract;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.RoomImage;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.RoomRepository;
import com.example.rentnest.repository.specification.HostelSpecification;
import com.example.rentnest.repository.specification.RoomSpecification;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.RoomImageService;
import com.example.rentnest.service.RoomService;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl extends BaseServiceImpl<Room, Long, RoomRepository> implements RoomService {
    private final RoomRepository roomRepository;
    private final CloudinaryService cloudinaryService;
    private final RoomImageService roomImageService;

    @Override
    public Optional<Room> findById(Long aLong) {
        return super.findById(aLong);
    }

    public RoomServiceImpl(RoomRepository roomRepository, CloudinaryService cloudinaryService, RoomImageService roomImageService) {
        super();
        this.roomRepository = roomRepository;
        this.cloudinaryService = cloudinaryService;
        this.roomImageService = roomImageService;
    }

    @Override
    public Page<RoomCardResponse> getRoomByLandlord(Long landlordId, String keyword, String status, Long hostelId, Pageable pageable) {
        Specification<Room> spec = RoomSpecification.filterRoomsForLandlord(landlordId, keyword, status, hostelId);
        Page<Room> rooms = roomRepository.findAll(spec, pageable);
        return rooms.map(this::mapToRoomResponse);
    }

    @Override
    public void deleteRoom(Room room) {

        roomRepository.delete(room);
    }

    private RoomCardResponse mapToRoomResponse(Room room) {
        return RoomCardResponse.builder()
                .id(room.getId())
                .title(room.getRoomName())
                .area(room.getArea())
                .price(room.getBasePrice())
                .status(room.getStatus().name())
                .location(room.getHostel() != null ? room.getHostel().getName() : "")
                .bedType(room.getBedType())
                .floor(room.getFloor().toString())
                .bathCount(room.getBathCount())
                .images(room.getImages().stream().map(RoomImage::getUrl).collect(Collectors.toList()))
                .build();
    }
}
