package com.example.rentnest.service;

import com.example.rentnest.model.Room;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService extends BaseService<Room, Long>{
    Page<RoomCardResponse> getRoomByLandlord(Long landlordId, String keyword, String status, Long hostelId, Pageable pageable);
}
