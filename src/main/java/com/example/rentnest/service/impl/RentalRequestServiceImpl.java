package com.example.rentnest.service.impl;

import com.example.rentnest.Utils.EmailUtils;
import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.*;
import com.example.rentnest.model.dto.response.RentalRequestResponse;
import com.example.rentnest.model.dto.response.RequestRentRoom;
import com.example.rentnest.repository.RentalRequestRepository;
import com.example.rentnest.service.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class RentalRequestServiceImpl extends BaseServiceImpl<RentalRequest, Long, RentalRequestRepository> implements RentalRequestService {
    @Autowired
    private RentalRequestRepository rentalRequestRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;
    @Autowired
    private Configuration configuration;
    @Autowired
    private EmailService emailService;
    @Override
    public void approveRequest(Long requestId, Long landlordId) {

    }

    @Override
    public void createRequest(RequestRentRoom requestRentRoom) throws IOException, TemplateException {
        Optional<Room> room = roomService.findById(requestRentRoom.getRoomId());
        if (!room.isPresent()) {
            throw new RuntimeException("Room not found");

        } else {
            if (!room.get().getStatus().equals(RoomStatus.AVAILABLE)) {
                throw new RuntimeException("phong nay da cho thue roi");
            }

        }
        RentalRequest rentalRequest = RentalRequest.builder()
                .tenant(userService.findById(requestRentRoom.getUserId()).get())
                .room(room.get())
                .expectedMoveInDate(requestRentRoom.getExpectedMoveInDate())
                .depositAmount(room.get().getBasePrice())
                .cccd(requestRentRoom.getCccd())
                .note(requestRentRoom.getNotes())
                .status(RequestStatus.PENDING)
                .build();
        rentalRequestRepository.save(rentalRequest);
        String subject = "Bạn vừa nhận được 1 yêu cầu thuê nhà mới";
        Template mailTemplate = configuration.getTemplate("RequestRentRoom.ftl");
        User user = userService.findById(requestRentRoom.getUserId()).get();
        Map<String, Object> model = new HashMap<>();
        model.put("tenantName", user.getFullname());
        model.put("roomName", room.get().getRoomName());
        model.put("hostelName", room.get().getHostel().getName());
        model.put("landlordName", room.get().getHostel().getOwner().getFullname());
        model.put("tenantPhone", user.getPhoneNumber());
        model.put("expectedMoveInDate", requestRentRoom.getExpectedMoveInDate());
        model.put("requestMessage", requestRentRoom.getNotes());
        model.put("actionUrl", "1");
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model);
        boolean sendMailCheck = EmailUtils.sendEmail(emailService, subject, // Gửi email
                Collections.singletonList(room.get().getHostel().getOwner().getEmail()), // Gửi đến chu nha
                null, null,// Không có file đính kèm
                content // Nội dung email
        );

    }

    @Override
    public RentalRequestResponse getRequestDetailForLandlord(Long landlordId, Long requestId) {
        RentalRequest rentalRequest = rentalRequestRepository.findByIdAndRoom_Hostel_Owner_Id(requestId, landlordId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu thuê"));
        return toResponse(rentalRequest);
    }

    @Override
    @Transactional
    public void updateStatus(
            Long landlordId,
            Long requestId,
            RequestStatus requestStatus,
            String rejectReason
    ) throws TemplateException, IOException {
        if (requestStatus == null) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        RentalRequest rentalRequest = rentalRequestRepository
                .findByIdAndRoom_Hostel_Owner_Id(requestId, landlordId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu thuê"));

        if (rentalRequest.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể cập nhật yêu cầu đang chờ duyệt");
        }

        rentalRequest.setStatus(requestStatus);

        if (requestStatus == RequestStatus.REJECTED) {
            rentalRequest.setRejectReason(
                    StringUtils.hasText(rejectReason)
                            ? rejectReason.trim()
                            : "Không phù hợp với tiêu chí hiện tại"
            );
        } else {
            rentalRequest.setRejectReason(null);
        }

        rentalRequestRepository.save(rentalRequest);

        if (requestStatus == RequestStatus.APPROVED) {
            List<RentalRequest> pendingRequests = rentalRequestRepository
                    .findByRoomIdAndStatusAndIdNot(
                            rentalRequest.getRoom().getId(),
                            RequestStatus.PENDING,
                            rentalRequest.getId()
                    );

            pendingRequests.forEach(request -> {
                request.setStatus(RequestStatus.REJECTED);
                request.setRejectReason("Đã có người khác thuê phòng này");
            });

            rentalRequestRepository.saveAll(pendingRequests);
            sendRentalRequestStatusEmail(rentalRequest);
            for(RentalRequest request : pendingRequests) {
                sendRentalRequestStatusEmail(request);
            }
        }else{
            sendRentalRequestStatusEmail(rentalRequest);
        }
    }

    private void sendRentalRequestStatusEmail(RentalRequest rentalRequest) throws IOException, TemplateException {
        String subject = "Yêu cầu thuê nhà của bạn đã được xử lý";
        Template mailTemplate = configuration.getTemplate("UpdateRequestRentRoomStatus.ftl");
        Map<String, Object> model = new HashMap<>();
        model.put("tenantName", rentalRequest.getTenant().getFullname());
        model.put("landlordName", rentalRequest.getRoom().getHostel().getOwner().getFullname());
        model.put("roomName", rentalRequest.getRoom().getRoomName());
        model.put("hostelName", rentalRequest.getRoom().getHostel().getName());
        model.put("expectedMoveInDate", rentalRequest.getExpectedMoveInDate());
        model.put("requestStatus", rentalRequest.getStatus().name());
        model.put("rejectReason", rentalRequest.getRejectReason());
        model.put("actionUrl", rentalRequest.getStatus() == RequestStatus.APPROVED ? "http://localhost:4200/rental-request/" + rentalRequest.getId() : "http://localhost:4200/rooms");
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model);
        boolean sendMailCheck = EmailUtils.sendEmail(emailService, subject, // Gửi email
                Collections.singletonList(rentalRequest.getTenant().getEmail()), // Gửi đến chu nha
                null, null,// Không có file đính kèm
                content // Nội dung email
        );
    }

    @Override
    public Page<RentalRequestResponse> getRequestsForTenant(Long tenantId, Pageable pageable) {
        return rentalRequestRepository.findByTenant_Id(tenantId, pageable).map(this::toResponse);
    }

    @Override
    public void cancelRequest(Long tenantId, Long requestId) {
        RentalRequest rentalRequest = rentalRequestRepository.findByIdAndTenant_Id(requestId, tenantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu thuê"));
        if(rentalRequest.getStatus() != RequestStatus.PENDING){
            throw new RuntimeException("Chỉ có thể cập nhật yêu cầu đang chờ duyệt");
        }
        rentalRequest.setStatus(RequestStatus.CANCELLED);
        rentalRequestRepository.save(rentalRequest);
    }
    private RentalRequestResponse toResponse(RentalRequest request){
        Room room = request.getRoom();
        Hostel hostel = room.getHostel();
        User tenant = request.getTenant();
        User landlord = hostel.getOwner();

        return RentalRequestResponse.builder()
                .id(request.getId())
                .roomId(room.getId())
                .tenantName(tenant.getFullname())
                .tenantPhone(tenant.getPhoneNumber())
                .tenantEmail(tenant.getEmail())
                .cccd(request.getCccd())
                .landlordName(landlord.getFullname())
                .landlordPhone(landlord.getPhoneNumber())
                .roomName(room.getRoomName())
                .roomThumbnail(room.getImages().stream().filter(RoomImage::isThumbnail).map(RoomImage::getUrl).findFirst().orElse(room.getImages().get(0).getUrl()))
                .hostelName(hostel.getName())
                .hostelAddress(hostel.getAddressDetail())
                .roomArea(room.getArea())
                .roomFloor(room.getFloor())
                .bathCount(room.getBathCount())
                .depositAmount(request.getDepositAmount())
                .note(request.getNote())
                .rejectReason(request.getRejectReason())
                .expectedMoveInDate(request.getExpectedMoveInDate())
                .createdAt(request.getCreatedAt())
                .status(request.getStatus().name())
                .roomStatus(room.getStatus().name())
                .build();
    }
}
