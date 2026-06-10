package com.example.rentnest.repository;

import com.example.rentnest.model.Occupant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface OccupantRepository extends BaseRepository<Occupant, Long>{
    Page<Occupant> findAll(Specification<Occupant> spec, Pageable pageable);
    List<Occupant> findByRoom_IdAndIsActiveTrueOrderByIsRepresentativeDescIdAsc(Long roomId);

}
