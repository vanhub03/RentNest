package com.example.rentnest.repository;

import com.example.rentnest.model.Hostel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface HostelRepository extends BaseRepository<Hostel, Long>{
    Page<Hostel> findAll(Specification<Hostel> spec, Pageable pageable);
}
