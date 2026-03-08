package com.example.rentnest.service.impl;

import com.example.rentnest.model.Contract;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.RoomRepository;
import com.example.rentnest.repository.specification.HostelSpecification;
import com.example.rentnest.repository.specification.RoomSpecification;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl extends BaseServiceImpl<Room, Long, RoomRepository> implements RoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        super();
        this.roomRepository = roomRepository;
    }

    @Override
    public Page<RoomCardResponse> getRoomByLandlord(Long landlordId, String keyword, String status, Long hostelId, Pageable pageable) {
        Specification<Room> spec = RoomSpecification.filterRoomsForLandlord(landlordId, keyword, status, hostelId);
        Page<Room> rooms = roomRepository.findAll(spec, pageable);
        return rooms.map(this::mapToRoomResponse);
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
                .bathCount(room.getBathCount())
                .build();
    }
}
