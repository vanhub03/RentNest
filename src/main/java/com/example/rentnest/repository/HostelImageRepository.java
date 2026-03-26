package com.example.rentnest.repository;

import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.HostelImage;
import com.example.rentnest.model.RoomImage;

import java.util.List;

public interface HostelImageRepository extends BaseRepository<HostelImage, Long>{
    List<HostelImage> findByHostelId(Long hostelId);
    void deleteAllByHostelId(Long carId);

}

