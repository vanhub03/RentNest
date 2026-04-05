package com.example.rentnest.service.impl;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Contract;
import com.example.rentnest.model.Occupant;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.dto.request.TenantOnboardRequest;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.OccupantRepository;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.RoomService;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public void onboardNewTenant(TenantOnboardRequest request, MultipartFile contractFile, MultipartFile cccdFront, MultipartFile cccdBack, Long landlordId) throws IOException {
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

        for(TenantOnboardRequest.OccupantDto dto : request.getOccupants()){
            Occupant occ = Occupant.builder()
                    .fullName(dto.getFullName())
                    .phoneNumber(dto.getPhoneNumber())
                    .identityCard(dto.getIdentityCard())
                    .isRepresentative(dto.isRepresentative())
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

        if(representative != null){
            if(cccdFront != null && !cccdFront.isEmpty()){
                representative.setIdentityCardFrontUrl(cloudinaryService.uploadImage(cccdFront));
            }
            if(cccdBack != null && !cccdBack.isEmpty()){
                representative.setIdentityCardBackUrl(cloudinaryService.uploadImage(cccdBack));
            }
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
                .isActive(true)
                .build();
        contractRepository.save(contract);
    }
}
