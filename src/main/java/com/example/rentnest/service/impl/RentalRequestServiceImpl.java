package com.example.rentnest.service.impl;

import com.example.rentnest.Utils.EmailUtils;
import com.example.rentnest.enums.RequestStatus;
import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.RentalRequest;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.User;
import com.example.rentnest.model.dto.response.RequestRentRoom;
import com.example.rentnest.repository.RentalRequestRepository;
import com.example.rentnest.service.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
}
