package com.example.rentnest.repository.specification;

import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Occupant;
import com.example.rentnest.model.Room;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OccupantSpecification {
    public static Specification<Occupant> filterTenantsForLandlord(Long landlordId, String keyword) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Occupant, Room> roomJoin = root.join("room");
            Join<Room, Hostel> hostelJoin = roomJoin.join("hostel");

            predicates.add(cb.equal(hostelJoin.get("owner").get("id"), landlordId));

            if(keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate namePred = cb.like(cb.lower(root.get("fullName")), likeKeyword);
                Predicate idCardPred = cb.like(cb.lower(root.get("identityCard")), likeKeyword);
                Predicate phonePred = cb.like(cb.lower(root.get("phoneNumber")), likeKeyword);
                Predicate emailPred = cb.like(cb.lower(root.get("phoneNumber")), likeKeyword);

                predicates.add(cb.or(namePred, idCardPred, phonePred, emailPred));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
