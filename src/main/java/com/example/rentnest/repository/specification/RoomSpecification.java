package com.example.rentnest.repository.specification;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Room;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

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
}
