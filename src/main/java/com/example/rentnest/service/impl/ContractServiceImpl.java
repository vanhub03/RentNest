package com.example.rentnest.service.impl;

import com.example.rentnest.enums.ContractStatus;
import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.*;
import com.example.rentnest.model.dto.request.AddRoomOccupantsRequest;
import com.example.rentnest.model.dto.request.TenantOnboardRequest;
import com.example.rentnest.model.dto.response.ContractListResponse;
import com.example.rentnest.model.dto.response.ContractPreviewResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.OccupantRepository;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.RoomService;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContractServiceImpl extends BaseServiceImpl<Contract, Long, ContractRepository> implements ContractService {
    private final RoomService roomService;
    private final CloudinaryService cloudinaryService;
    private final OccupantRepository occupantRepository;
    private final ContractRepository contractRepository;

    public ContractServiceImpl(RoomService roomService, CloudinaryService cloudinaryService, OccupantRepository occupantRepository, ContractRepository contractRepository) {
        super();
        this.roomService = roomService;
        this.cloudinaryService = cloudinaryService;
        this.occupantRepository = occupantRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    @Transactional
    public void onboardNewTenant(TenantOnboardRequest request, MultipartFile contractFile, List<MultipartFile> cccdFronts, List<MultipartFile> cccdBacks, Long landlordId) throws IOException {
        //xu ly phong
        Room room = roomService.findById(request.getRoomId()).orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
        if(!room.getHostel().getOwner().getId().equals(landlordId)){
            throw new RuntimeException("Bạn không có quyền thao tác trên phòng này");
        }
        if(room.getStatus() != RoomStatus.AVAILABLE){
            throw new RuntimeException("Phòng này không ở trạng thái trống");
        }
        room.setStatus(RoomStatus.RENTED);
        roomService.save(room);

        //xu ly nguoi thue phong
        List<Occupant> occupantList = new ArrayList<>();
        Occupant representative = null;

        for(int i=0; i< request.getOccupants().size(); i++){
            TenantOnboardRequest.OccupantDto occupantDto = request.getOccupants().get(i);
            Occupant occ = Occupant.builder()
                    .fullName(occupantDto.getFullName())
                    .phoneNumber(occupantDto.getPhoneNumber())
                    .identityCard(occupantDto.getIdentityCard())
                    .isRepresentative(occupantDto.isRepresentative())
                    .identityCardFrontUrl(cloudinaryService.uploadImage(cccdFronts.get(i)))
                    .identityCardBackUrl(cloudinaryService.uploadImage(cccdBacks.get(i)))
                    .isActive(true)
                    .room(room)
                    .build();
            occupantList.add(occ);
            if(occ.isRepresentative()){
                representative = occ;
            }
        }
        if(representative == null && !occupantList.isEmpty()){
            representative = occupantList.get(0);
            representative.setRepresentative(true);
        }

        occupantRepository.saveAll(occupantList);

        //xu ly file hop dong
        String contractUrl = null;
        if(contractFile != null && !contractFile.isEmpty()){
            contractUrl = cloudinaryService.uploadImage(contractFile);
        }

        Contract contract = Contract.builder()
                .room(room)
                .representativeOccupant(representative)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .depositAmount(request.getDepositAmount())
                .contractFileUrl(contractUrl)
                .status(ContractStatus.ACTIVE)
                .build();
        contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Contract createDraftFromRentalRequest(RentalRequest rentalRequest) {
        return contractRepository.findByRentalRequest_Id(rentalRequest.getId()).orElseGet(() -> {
            Room room = rentalRequest.getRoom();
            User tenant = rentalRequest.getTenant();
            LocalDate startDate = rentalRequest.getExpectedMoveInDate() != null ? rentalRequest.getExpectedMoveInDate() : LocalDate.now().plusDays(7);
            Occupant representative = Occupant.builder()
                    .fullName(tenant.getFullname())
                    .phoneNumber(tenant.getPhoneNumber())
                    .identityCard(rentalRequest.getCccd())
                    .isRepresentative(true)
                    .isActive(false)
                    .room(room)
                    .userAccount(tenant)
                    .build();
            occupantRepository.save(representative);
            Contract contract = Contract.builder()
                    .room(room)
                    .rentalRequest(rentalRequest)
                    .representativeOccupant(representative)
                    .startDate(startDate)
                    .endDate(startDate.plusMonths(12).minusDays(1))
                    .depositAmount(rentalRequest.getDepositAmount())
                    .status(ContractStatus.WAITING_FOR_SIGNATURE)
                    .build();
            return contractRepository.save(contract);
        });
    }

    @Override
    public ContractPreviewResponse getPreviewForLandlord(Long landlordId, Long rentalRequestId) {
        Contract contract = contractRepository.findByRentalRequest_IdAndRoom_Hostel_Owner_Id(rentalRequestId, landlordId).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng nháp"));
        return toPreviewResponse(contract);
    }

    @Override
    public ContractPreviewResponse getPreviewForTenant(Long tenantId, Long rentalRequestId) {
        Contract contract = contractRepository.findByRentalRequest_IdAndRentalRequest_Tenant_Id(rentalRequestId, tenantId).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng nháp"));
        return toPreviewResponse(contract);
    }

    @Override
    public Page<ContractListResponse> getContractForLandlord(Long landlordId, String status, Pageable pageable) {
        Page<Contract> contracts;
        if("EXPIRING".equalsIgnoreCase(status)){
            contracts = contractRepository.findExpiringContracts(landlordId, ContractStatus.ACTIVE, LocalDate.now(), LocalDate.now()
                    .plusDays(30), pageable);
        }else if(status != null && !status.isBlank()){
            ContractStatus contractStatus = ContractStatus.valueOf(status.toUpperCase());
            contracts = contractRepository.findByRoom_Hostel_Owner_IdAndStatus(landlordId, contractStatus, pageable);
        }else{
            contracts = contractRepository.findByRoom_Hostel_Owner_Id(landlordId, pageable);
        }
        return contracts.map(this::toListResponse);
    }

    @Override
    public ContractListResponse renewContract(Long landlordId, Long contractId, int months) {
        if(months < 1 || months > 36){
            throw new RuntimeException("Thời gian gia hạn không hợp lệ");
        }
        Contract contract = contractRepository.findByIdAndRoom_Hostel_Owner_Id(contractId, landlordId).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
        LocalDate today = LocalDate.now();
        if(contract.getStatus() != ContractStatus.ACTIVE && contract.getStatus() != ContractStatus.EXPIRED){
            throw new RuntimeException("Chỉ có thể gia hạn hợp đồng đang có hiệu lực hoặc đã hết hạn");
        }
//        if(contract.getEndDate().isBefore(today)){
//            throw new RuntimeException("Hợp đồng đã hết hạn, không thuộc nhóm sắp hết hạn");
//        }
        if(contract.getEndDate().isAfter(today.plusDays(30))){
            throw new RuntimeException("Chỉ gia hạn hợp đồng sắp hết hạn trong 30 ngày");
        }
        contract.setEndDate(contract.getEndDate().plusMonths(months));
        contract.setStatus(ContractStatus.ACTIVE);
        return toListResponse(contractRepository.save(contract));
    }

    private ContractListResponse toListResponse(Contract contract){
        Room room = contract.getRoom();
        Hostel hostel = room.getHostel();
        Occupant tenant = contract.getRepresentativeOccupant();
        LocalDate today = LocalDate.now();
        boolean expiringSoon = contract.getStatus() == ContractStatus.ACTIVE
                && contract.getEndDate() != null
                && !contract.getEndDate().isBefore(today)
                && !contract.getEndDate().isAfter(today.plusDays(30));

        return ContractListResponse.builder()
                .id(contract.getId())
                .rentalRequestId(contract.getRentalRequest() != null ? contract.getRentalRequest().getId() : null)
                .contractCode(String.format("HD-%d-%30d", contract.getStartDate().getYear(), contract.getId()))
                .tenantName(tenant.getFullName())
                .tenantPhone(tenant.getPhoneNumber())
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .hostelName(hostel.getName())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .depositAmount(contract.getDepositAmount())
                .status(contract.getStatus().name())
                .expiringSoon(expiringSoon)
                .contractFileUrl(contract.getContractFileUrl())
                .build();
    }

    private ContractPreviewResponse toPreviewResponse(Contract contract) {
        RentalRequest rentalRequest = contract.getRentalRequest();
        Room room = rentalRequest.getRoom();
        Hostel hostel = room.getHostel();
        User landlord = hostel.getOwner();
        Occupant tenant = contract.getRepresentativeOccupant();
        return ContractPreviewResponse.builder()
                .id(contract.getId())
                .rentalRequestId(rentalRequest.getId())
                .contractCode(String.format("HD-%d-%03d", contract.getStartDate().getYear(), contract.getId()))
                .status(contract.getStatus().name())
                .createdAt(contract.getCreatedAt())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .durationMonths((int) ChronoUnit.MONTHS.between(contract.getStartDate(), contract.getEndDate().plusDays(1)))
                .depositAmount(contract.getDepositAmount())
                .monthlyRent(room.getBasePrice())
                .paymentDay(5)
                .services(toServiceChargeDtos(hostel))
                .landlordName(landlord.getFullname())
                .landlordPhone(landlord.getPhoneNumber())
                .landlordAddress(hostel.getAddressDetail())
                .tenantName(tenant.getFullName())
                .tenantPhone(tenant.getPhoneNumber())
                .tenantEmail(tenant.getUserAccount().getEmail())
                .tenantIdentityCard(tenant.getIdentityCard())
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .hostelName(hostel.getName())
                .hostelAddress(hostel.getAddressDetail())
                .roomArea(room.getArea())
                .roomFloor(room.getFloor())
                .bathCount(room.getBathCount())
                .build();
    }

    private List<ContractPreviewResponse.ServiceChargeDto> toServiceChargeDtos(Hostel hostel) {
        if(hostel.getServices() == null || hostel.getServices().isEmpty()){
            return List.of();
        }
        return hostel.getServices().stream()
                .map(service -> ContractPreviewResponse.ServiceChargeDto.builder()
                        .id(service.getId())
                        .serviceName(service.getServiceName())
                        .unitPrice(service.getUnitPrice())
                        .unitName(service.getUnitName())
                        .metered(service.isMetered())
                        .build()).toList();
    }
}
