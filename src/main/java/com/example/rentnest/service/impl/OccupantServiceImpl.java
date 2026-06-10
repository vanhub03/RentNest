package com.example.rentnest.service.impl;

import com.example.rentnest.enums.ContractStatus;
import com.example.rentnest.model.*;
import com.example.rentnest.model.dto.request.CoOccupantRequest;
import com.example.rentnest.model.dto.response.TenantResponse;
import com.example.rentnest.model.dto.response.TenantRoomResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.OccupantRepository;
import com.example.rentnest.repository.specification.OccupantSpecification;
import com.example.rentnest.service.OccupantService;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class OccupantServiceImpl extends BaseServiceImpl<Occupant, Long, OccupantRepository> implements OccupantService {

    @Autowired
    private OccupantRepository occupantRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Page<TenantResponse> getTenantsByLandlord(Long landlordId, String keyword, Pageable pageable) {
        Specification<Occupant> spec = OccupantSpecification.filterTenantsForLandlord(landlordId, keyword);
        Page<Occupant> occupants = occupantRepository.findAll(spec, pageable);
        return occupants.map(this::mapToResponse);
    }

    @Override
    public List<TenantRoomResponse> getMyRooms(Long tenantId) {
        return contractRepository.findActiveContractsForTenant(tenantId, ContractStatus.ACTIVE)
                .stream().map(this::toRoomResponse).toList();
    }

    @Override
    public TenantRoomResponse addCoOccupant(Long tenantId, Long roomId, CoOccupantRequest request, MultipartFile cccdFront, MultipartFile cccdBack) throws IOException {
        Contract contract = contractRepository.findTenantActiveContractByRoom(tenantId, roomId, ContractStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Khong thay hop dong cua phong dang chi dinh"));
        validateRequest(request, cccdFront, cccdBack);
        String cccdFrontUrl = cloudinaryService.uploadImage(cccdFront);
        String cccdBackUrl = cloudinaryService.uploadImage(cccdBack);

        Occupant occupant = Occupant.builder()
                .room(contract.getRoom())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .identityCard(request.getIdentityCard())
                .identityCardFrontUrl(cccdFrontUrl)
                .identityCardBackUrl(cccdBackUrl)
                .isRepresentative(false)
                .isActive(true)
                .build();
        occupantRepository.save(occupant);
        return toRoomResponse(contract);
    }

    private void validateRequest(CoOccupantRequest request, MultipartFile cccdFront, MultipartFile cccdBack) {
        if(!StringUtils.hasText(request.getFullName())){
            throw new RuntimeException("Vui long nhap ho ten nguoi o cung");
        }
        if(!StringUtils.hasText(request.getIdentityCard()) || request.getIdentityCard().length() != 12){
            throw new RuntimeException("Cccd phai co 12 so");
        }
        if(!StringUtils.hasText(request.getPhoneNumber())){
            throw new RuntimeException("Vui long nhap so dien thoai nguoi o cung");
        }
        validateImageFile(cccdFront, "Vui long upload anh cccd mat truoc");
        validateImageFile(cccdBack, "Vui long upload anh cccd mat sau");
    }
        private void validateImageFile(MultipartFile file, String missingMessage) {
        if(file == null || file.isEmpty()){
            throw new RuntimeException(missingMessage);
        }
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")){
            throw new RuntimeException("Vui long nhap hinh anh vao");
        }

        }
    private TenantRoomResponse toRoomResponse(Contract contract){
        Room room = contract.getRoom();
        Hostel hostel = room.getHostel();
        User landlord = hostel.getOwner();
        return TenantRoomResponse.builder()
                .contractId(contract.getId())
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .hostelName(hostel.getName())
                .hostelAddress(hostel.getAddressDetail())
                .landlordName(landlord != null ? landlord.getFullname() : null)
                .landlordPhone(landlord != null ? landlord.getPhoneNumber() : null)
                .monthlyRent(room.getBasePrice())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .contractStatus(contract.getStatus().name())
                .thumbnailUrl(resolveThumbnail(room))
                .occupants(resolveOccupants(room.getId()))
                .build();
    }

    private List<TenantRoomResponse.OccupantDto> resolveOccupants(Long roomId){ //lay danh sach nguoi thue active cua phong
        return occupantRepository.findByRoom_IdAndIsActiveTrueOrderByIsRepresentativeDescIdAsc(roomId) //representative len dau
                .stream()
                .map(occupant -> TenantRoomResponse.OccupantDto.builder() //map sang nested dto
                        .id(occupant.getId())
                        .fullName(occupant.getFullName())
                        .phoneNumber(occupant.getPhoneNumber())
                        .identityCard(occupant.getIdentityCard())
                        .identityCardFrontUrl(occupant.getIdentityCardFrontUrl())
                        .identityCardBackUrl(occupant.getIdentityCardBackUrl())
                        .representative(occupant.isRepresentative())
                        .active(occupant.isActive())
                        .build())
                .toList();
    }

    private String resolveThumbnail(Room room){ //lay anh dau tien cua phong lam thumbnail
        if(room.getImages() == null || room.getImages().isEmpty()){ //neu phong chua co anh
            return null;
        }
        RoomImage firstImage = room.getImages().get(0);
        return firstImage.getUrl();
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
