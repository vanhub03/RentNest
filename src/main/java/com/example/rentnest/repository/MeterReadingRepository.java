package com.example.rentnest.repository;

import com.example.rentnest.model.MeterReading;

import java.util.Optional;

public interface MeterReadingRepository extends BaseRepository<MeterReading, Long>{
    Optional<MeterReading> findByRoom_IdAndService_IdAndReadingMonth(Long roomId, Long serviceId, String readingMonth);
    Optional<MeterReading> findTopByRoom_IdAndService_IdAndReadingMonthLessThanOrderByReadingMonthDesc(Long roomId, Long serviceId, String readingMonth);
}
