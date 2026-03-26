package com.example.rentnest.service;

import com.example.rentnest.model.HostelImage;
import com.example.rentnest.model.RoomImage;

import java.util.List;

public interface HostelImageService extends BaseService<HostelImage, Long>{
    List<HostelImage> findByHostelId(Long hostelId);
    void deleteAllByHostelId(Long hostelId);

}
