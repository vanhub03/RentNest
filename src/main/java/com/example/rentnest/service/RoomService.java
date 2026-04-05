package com.example.rentnest.service;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RoomService extends BaseService<Room, Long>{

    Page<RoomCardResponse> getRoomByLandlord(Long landlordId, String keyword, String status, Long hostelId, Pageable pageable);
    void deleteRoom(Room room);
    boolean existsByHostelIdAndStatus(Long hostelId, RoomStatus status);
    List<Room> findTop4ByOrderByCreatedAtDesc();
    List<String> findAvailableLocations();
    List<RoomCardResponse> getAvailableRooms(Long landlordId);
}


