package com.example.rentnest.service.impl;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Contract;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.RoomImage;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import com.example.rentnest.model.dto.response.RoomDetailResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
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

    @Override
    public boolean existsByHostelIdAndStatus(Long hostelId, RoomStatus status) {
        return roomRepository.existsByHostelIdAndStatus(hostelId, status);
    }

    @Override
    public List<Room> findTop4ByOrderByCreatedAtDesc() {
        return roomRepository.findTop4ByOrderByCreatedAtDesc();
    }

    @Override
    public List<String> findAvailableLocations() {
        return roomRepository.findAvailableLocations();
    }

    @Override
    public List<RoomCardResponse> getAvailableRooms(Long landlordId) {
        List<Room> availableRooms = roomRepository.findAvailableRoomsByLandlord(landlordId, RoomStatus.AVAILABLE);

        return availableRooms.stream().map(this::mapToRoomResponse).collect(Collectors.toList());
    }

    @Override
    public Page<RoomCardResponse> getPublicRooms(String cityCode, String wardCode, BigDecimal minPrice, BigDecimal maxPrice, String sort, int page, int size) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if("PRICE_ASC".equalsIgnoreCase(sort)){
            sortObj = Sort.by(Sort.Direction.ASC, "basePrice");
        }else if("PRICE_DESC".equalsIgnoreCase(sort)){
            sortObj = Sort.by(Sort.Direction.DESC, "basePrice");
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Specification<Room> spec = RoomSpecification.filterPublicRooms(cityCode, wardCode, minPrice, maxPrice);
        Page<Room> rooms = roomRepository.findAll(spec, pageable);
        return rooms.map(this::mapToRoomResponse);
    }

    @Override
    public RoomDetailResponse getRoomDetailPublic(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(()->new RuntimeException("Phòng không tồn tại hoặc đã bị xóa"));

        return RoomDetailResponse.builder()
                .id(room.getId())
                .roomName(room.getRoomName() + " " + room.getHostel().getName())
                .area(room.getArea())
                .basePrice(room.getBasePrice())
                .status(room.getStatus().name())
                .bedType(room.getBedType())
                .bathCount(room.getBathCount())
                .hostelName(room.getHostel().getName())
                .fullAddress(room.getHostel().getAddressDetail() + ", " + room.getHostel().getWard() + ", " + room.getHostel().getCity())
                .description(room.getHostel().getDescription())
                .images(room.getImages() != null ? room.getImages().stream().map(RoomImage::getUrl).collect(Collectors.toList()) : List.of())
                .services(room.getHostel().getServices() != null ?
                        room.getHostel().getServices().stream().map(
                                a -> RoomDetailResponse.ServiceDto.builder()
                                        .serviceName(a.getServiceName())
                                        .price(a.getUnitPrice())
                                        .unit(a.getUnitName())
                                        .build()).collect(Collectors.toList()) : List.of()
                        )
                .landlordName(room.getHostel().getOwner().getFullname())
                .landlordPhone(room.getHostel().getOwner().getPhoneNumber())
                .build();
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
                .hostelId(room.getHostel().getId())
                .images(room.getImages().stream().map(RoomImage::getUrl).collect(Collectors.toList()))
                .thumbnail(room.getImages().get(0).getUrl())
                .build();
    }
}
