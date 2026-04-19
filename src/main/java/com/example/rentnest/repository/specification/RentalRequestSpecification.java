package com.example.rentnest.repository.specification;

import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.RentalRequest;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RentalRequestSpecification {
    public static Specification<RentalRequest> rentalRequestSpecification(Long landlordId, RequestStatus status, Long roomId, String tenantName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<RentalRequest, Room> roomJoin = root.join("room", JoinType.INNER);
            Join<Room, Hostel> hostelJoin = roomJoin.join("hostel", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(hostelJoin.get("owner").get("id"), landlordId));
            if(status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if(roomId != null) {
                predicates.add(criteriaBuilder.equal(roomJoin.get("id"), roomId));
            }
            if(StringUtils.hasText(tenantName)) {
                Join<RentalRequest, User> tenantJoin = root.join("tenant", JoinType.INNER);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(tenantJoin.get("fullname")), "%" +  tenantName.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
