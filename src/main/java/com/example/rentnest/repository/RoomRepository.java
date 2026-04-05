package com.example.rentnest.repository;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Room;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room, Long>{
    Page<Room> findAll(Specification<Room> specification, Pageable pageable);
    boolean existsByHostelIdAndStatus(Long hostelId, RoomStatus status);
    List<Room> findTop4ByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT CONCAT(h.ward, ', ', h.city) " +
            "FROM Room r JOIN r.hostel h " +
            "WHERE r.status = 'AVAILABLE'")
    List<String> findAvailableLocations();

    @Query("SELECT r FROM Room r WHERE r.hostel.owner.id = :landlordId AND r.status = :status")
    List<Room> findAvailableRoomsByLandlord(@Param("landlordId") Long landlordId, @Param("status") RoomStatus status);
}