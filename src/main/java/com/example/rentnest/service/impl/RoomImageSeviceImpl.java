package com.example.rentnest.service.impl;

import com.example.rentnest.model.RoomImage;
import com.example.rentnest.repository.RoomImageRepository;
import com.example.rentnest.service.RoomImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class RoomImageSeviceImpl extends BaseServiceImpl<RoomImage, Long, RoomImageRepository> implements RoomImageService {
    @Autowired
    private RoomImageRepository roomImageRepository;
    @Override
    public List<RoomImage> findByRoomId(Long roomId) {
        return roomImageRepository.findByRoomId((roomId));

    }

    @Override
    public void deleteAllByRoomId(Long roomId) { roomImageRepository.deleteAllByRoomId(roomId);

    }

}
