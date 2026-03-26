package com.example.rentnest.repository;

import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface HostelRepository extends BaseRepository<Hostel, Long>{
    Page<Hostel> findAll(Specification<Hostel> spec, Pageable pageable);

}
