package com.example.rentnest.repository.specification;

import com.example.rentnest.model.Hostel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HostelSpecification {
    public static Specification<Hostel> filterHostelForLandlord(Long landlordId, String keyword) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), landlordId));
            if(keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate namePred = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likeKeyword);
                Predicate addressPred = criteriaBuilder.like(criteriaBuilder.lower(root.get("addressDetail")), likeKeyword);
                Predicate wardPred = criteriaBuilder.like(criteriaBuilder.lower(root.get("ward")), likeKeyword);
                Predicate districtPred = criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), likeKeyword);
                Predicate cityPred = criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), likeKeyword);
                predicates.add(criteriaBuilder.or(namePred, addressPred, wardPred, districtPred, cityPred));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
