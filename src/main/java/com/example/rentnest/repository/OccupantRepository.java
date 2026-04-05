package com.example.rentnest.repository;

import com.example.rentnest.model.Occupant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OccupantRepository extends BaseRepository<Occupant, Long>{
    Page<Occupant> findAll(Specification<Occupant> spec, Pageable pageable);
}
