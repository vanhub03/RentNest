package com.example.rentnest.controller;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.RoomImage;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.repository.RoomRepository;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.HostelService;
import com.example.rentnest.service.RoomImageService;
import com.example.rentnest.service.RoomService;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController // Đánh dấu class là một RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private CloudinaryService cloudinaryService; // Service để upload ảnh lên cloudinary

    @Autowired
    private RoomImageService roomImageSevice;
    @Autowired
    private HostelService hostelService;
    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<?> addRoom(@RequestParam("rooms") String Json,
                                     @RequestPart("images") List<MultipartFile> images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(); // Khởi tạo object mapper
        RoomCreateRequestDTO roomCreateRequestDTO = objectMapper.readValue(Json, RoomCreateRequestDTO.class); // Convert JSON sang DTO
        Hostel hostel = hostelService.findById(roomCreateRequestDTO.getHostelId()) // Tìm loại xe theo ID
                .orElseThrow(() -> new RuntimeException("Room type not found")); // Nếu không có thì báo lỗi
        Room room = Room.builder()
                .hostel(hostel)
                .roomName(roomCreateRequestDTO.getRoomName())
                .basePrice(roomCreateRequestDTO.getBasePrice())
                .area(roomCreateRequestDTO.getArea())
                .floor(roomCreateRequestDTO.getFloor())
                .bathCount(roomCreateRequestDTO.getBathCount())
                .status(RoomStatus.valueOf(roomCreateRequestDTO.getStatus()))
                .bedType(roomCreateRequestDTO.getBedType())
                .build();
        roomService.save(room); // Lưu xe
        boolean a = true;
        for (MultipartFile image : images) { // Lặp qua danh sách ảnh upload
            String imageUrl = cloudinaryService.uploadImage(image); // Upload lên Cloudinary
            RoomImage roomImage = RoomImage.builder() // Tạo đối tượng ảnh
                    .room(room)
                    .url(imageUrl)
                    .isThumbnail(a)
                    .publicId(cloudinaryService.extractPublicId(imageUrl))
                    .build();
            roomImageSevice.save(roomImage);
            a = false;// Lưu ảnh vào DB
        }

        return ResponseEntity.ok(new MessageResponse("them phong moi thanh cong")); // Trả lại DTO đã nhận
    }


    @PutMapping("/{id}") // PUT /api/cars/{id}
    public ResponseEntity<?> updateRoom(@PathVariable Long id,
                           @RequestParam("room") String roomJson,
                           @RequestPart(value = "images", required = false) List<MultipartFile> imagesInput,
                           @AuthenticationPrincipal UserDetailsImpl userDetails ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RoomCreateRequestDTO roomCreateRequestDTO = objectMapper.readValue(roomJson, RoomCreateRequestDTO.class);
        return ResponseEntity.ok(roomService.updateByLandlord(id,roomCreateRequestDTO,imagesInput,userDetails.getId()));

    }

    @DeleteMapping("/room/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        if(!room.getHostel().getOwner().getId().equals(userDetails.getId())) {
            throw new RuntimeException("Ban khong co quyen sua phong nay");
        }
        if(room.getStatus() == RoomStatus.RENTED) throw new RuntimeException("Khong the xoa phong dang co nguoi thue");

        List<RoomImage> roomImages = roomImageSevice.findByRoomId(id);
        for (RoomImage image : roomImages){
            cloudinaryService.deleteImageByUrl(image.getUrl());
            roomImageSevice.delete(image);
        }

        roomService.deleteRoom(room);
        return ResponseEntity.ok(new MessageResponse("Xoa phong thanh cong"));
    }

}

