package com.example.rentnest.service;

import com.example.rentnest.model.Room;
import com.example.rentnest.model.RoomImage;

import java.util.List;

public interface RoomImageService extends BaseService<RoomImage, Long>{
    List<RoomImage> findByRoomId(Long roomId);
    void deleteAllByRoomId(Long roomId);

    }
