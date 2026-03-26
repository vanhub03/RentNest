package com.example.rentnest.repository;

import com.example.rentnest.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room, Long>{
    Page<Room> findAll(Specification<Room> specification, Pageable pageable);

}
