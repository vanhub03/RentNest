package com.example.rentnest.repository.specification;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Room;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {
    public static Specification<Room> filterRoomsForLandlord(Long landlordId, String keyword, String status, Long hostelId) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("hostel").get("owner").get("id"), landlordId));
            if(StringUtils.hasText(keyword)) {
                predicates.add(criteriaBuilder.like(root.get("roomName"), "%" + keyword + "%"));
            }
            if(StringUtils.hasText(status)) {
                RoomStatus roomStatus = RoomStatus.valueOf(status);
                predicates.add(criteriaBuilder.equal(root.get("status"), roomStatus));
            }
            if(hostelId != null) {
                predicates.add(criteriaBuilder.equal(root.get("hostel").get("id"), hostelId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public static Specification<Room> filterPublicRooms(String cityCode, String wardCode, BigDecimal minPrice, BigDecimal maxPrice){
        return (root, query, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          predicates.add(criteriaBuilder.equal(root.get("status"), RoomStatus.AVAILABLE));
          Join<Room, Hostel> hostelJoin = root.join("hostel", JoinType.INNER);
          if(StringUtils.hasText(cityCode)){
              predicates.add(criteriaBuilder.equal(hostelJoin.get("cityCode"), cityCode));
          }
          if(StringUtils.hasText(wardCode)){
              predicates.add(criteriaBuilder.equal(hostelJoin.get("wardCode"), wardCode));
          }
          if(minPrice != null){
              predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
          }
          if(maxPrice != null){
              predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
