package com.example.rentnest.service.impl;

import com.example.rentnest.model.Occupant;
import com.example.rentnest.model.dto.response.TenantResponse;
import com.example.rentnest.repository.OccupantRepository;
import com.example.rentnest.repository.specification.OccupantSpecification;
import com.example.rentnest.service.OccupantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OccupantServiceImpl extends BaseServiceImpl<Occupant, Long, OccupantRepository> implements OccupantService {

    @Autowired
    private OccupantRepository occupantRepository;

    @Override
    public Page<TenantResponse> getTenantsByLandlord(Long landlordId, String keyword, Pageable pageable) {
        Specification<Occupant> spec = OccupantSpecification.filterTenantsForLandlord(landlordId, keyword);
        Page<Occupant> occupants = occupantRepository.findAll(spec, pageable);
        return occupants.map(this::mapToResponse);
    }

    private TenantResponse mapToResponse(Occupant occupant) {
        return TenantResponse.builder()
                .id(occupant.getId())
                .fullName(occupant.getFullName())
                .phoneNumber(occupant.getPhoneNumber())
                .email(occupant.getUserAccount() != null ? occupant.getUserAccount().getEmail() : "Chưa có tài khoản")
                .identityCard(occupant.getIdentityCard() != null ? occupant.getIdentityCard() : "Chưa cập nhật")
                .roomName(occupant.getRoom() != null ? occupant.getRoom().getRoomName() : "Chưa cập nhật")
                .hostelName(occupant.getRoom() != null ? occupant.getRoom().getHostel().getName() : "Chưa cập nhật")
                .isRepresentative(occupant.isRepresentative())
                .build();
    }
}
