package com.example.rentnest.service.impl;

import com.example.rentnest.model.HostelImage;
import com.example.rentnest.repository.HostelImageRepository;
import com.example.rentnest.repository.RoomImageRepository;
import com.example.rentnest.service.HostelImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostelImageServiceImpl extends BaseServiceImpl<HostelImage, Long, HostelImageRepository> implements HostelImageService {
    @Autowired
    private HostelImageRepository hostelImageRepository;


    @Override

    public List<HostelImage> findByHostelId(Long hostelId) {
        return hostelImageRepository.findByHostelId((hostelId));
    }

    @Override
    public void deleteAllByHostelId(Long hostelId) {hostelImageRepository.deleteAllByHostelId(hostelId);

    }
}
