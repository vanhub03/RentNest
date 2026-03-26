package com.example.rentnest.repository;

import com.example.rentnest.model.Room;
import com.example.rentnest.model.RoomImage;

import java.util.List;

public interface RoomImageRepository extends BaseRepository<RoomImage, Long>{
    List<RoomImage> findByRoomId(Long roomId);
    void deleteAllByRoomId(Long carId);


}
