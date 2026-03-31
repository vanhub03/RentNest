package com.example.rentnest.controller;

import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.*;
import com.example.rentnest.model.dto.request.HostelCreateRequestDTO;
import com.example.rentnest.model.dto.request.RoomCreateRequestDTO;
import com.example.rentnest.model.dto.response.HostelCardResponse;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import com.example.rentnest.repository.HostelImageRepository;
import com.example.rentnest.repository.HostelRepository;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.*;
import com.example.rentnest.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController // Đánh dấu class là một RestController
@RequestMapping("/api/hostels")
public class HostelController {
    @Autowired
    private HostelService hostelService;

    @Autowired
    private CloudinaryService cloudinaryService; // Service để upload ảnh lên cloudinary

    @Autowired
    private HostelImageService hostelImageService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostelRepository hostelRepository;
    @Autowired
    private HostelImageRepository hostelImageRepository;
    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<?> addHostel(@RequestParam("hostels") String Json,
                                     @RequestPart("images") List<MultipartFile> images,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(); // Khởi tạo object mapper
        HostelCreateRequestDTO hostelCreateRequestDTO = objectMapper.readValue(Json, HostelCreateRequestDTO.class); // Convert JSON sang DTO
        User landlord = userService.findById(userDetails.getId()).orElseThrow(); //tim user đang đăng nhập được lưu trong AuthenticationPrincipals
        Hostel hostel = Hostel.builder()
               // .hostel(hostel))
                .name(hostelCreateRequestDTO.getName())
                .addressDetail(hostelCreateRequestDTO.getAddressDetail())
                .wardCode(hostelCreateRequestDTO.getWardCode())
                .ward(hostelCreateRequestDTO.getWard())
                .districtCode(hostelCreateRequestDTO.getDistrictCode())
                .district(hostelCreateRequestDTO.getDistrict())
                .cityCode(hostelCreateRequestDTO.getCityCode())
                .city(hostelCreateRequestDTO.getCity())
                .description(hostelCreateRequestDTO.getDescription())
                .owner(landlord)
                .build();
        hostelService.save(hostel);// Lưu xe
        boolean a = true;
        for(MultipartFile image : images) { // Lặp qua danh sách ảnh upload
            String imageUrl = cloudinaryService.uploadImage(image); // Upload lên Cloudinary
          HostelImage hostelImage = HostelImage.builder() // Tạo đối tượng ảnh
                    .hostel(hostel)
                    .url(imageUrl)
                    .isThumbnail(a)
                    .publicId(cloudinaryService.extractPublicId(imageUrl))
                    .build();
            hostelImageService.save(hostelImage);
            a= false;// Lưu ảnh vào DB
        }

        return ResponseEntity.ok(new MessageResponse("them day tro thanh cong"));
    }

    @PutMapping("/{id}") // PUT /api/cars/{id}
    @Transactional
    public ResponseEntity<?> updateHostel(@PathVariable Long id,
                                        @RequestParam("hostels") String hostelJson,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> listImage,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        HostelCreateRequestDTO hostelCreateRequestDTO = objectMapper.readValue(hostelJson, HostelCreateRequestDTO.class);
            Hostel hostel = hostelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hostel not found"));
            if (!hostel.getOwner().getId().equals(userDetails.getId())) {
                throw new RuntimeException("Ban khong co quyen sua hostel nay");
            }
            HostelCardResponse hostelCardResponse = new HostelCardResponse();
            hostel.setName(hostelCreateRequestDTO.getName());
            hostel.setWardCode(hostelCreateRequestDTO.getWardCode());
            hostel.setAddressDetail(hostelCreateRequestDTO.getAddressDetail());
            hostel.setWard(hostelCreateRequestDTO.getWard());
            hostel.setDistrictCode(hostelCreateRequestDTO.getDistrictCode());

            hostel.setDistrict(hostelCreateRequestDTO.getDistrict());
            hostel.setCityCode(hostelCreateRequestDTO.getCityCode());
            hostel.setCity(hostelCreateRequestDTO.getCity());
            hostel.setDescription(hostelCreateRequestDTO.getDescription());
            if(listImage != null) { // Nếu có ảnh mới
                List<HostelImage> images = hostelImageService.findByHostelId(id); // Lấy danh sách ảnh cũ
                for(HostelImage image : images) {
                    cloudinaryService.deleteImageByUrl(image.getUrl()); // Xóa ảnh khỏi Cloudinary
                }
                hostelImageService.deleteAllByHostelId(hostel.getId()); // Xóa ảnh khỏi DB


                for(MultipartFile imageUpdate : listImage) { // Upload ảnh mới
                    String imageUrl = cloudinaryService.uploadImage((imageUpdate));
                    HostelImage hostelImage = HostelImage.builder()
                            .hostel(hostel)
                            .url(imageUrl)
                            .build();
                    hostelImageService.save(hostelImage);
                }
            }

           return ResponseEntity.ok(mapToHostelResponse(hostel));


        }
    private HostelCardResponse mapToHostelResponse(Hostel hostel) {
        return HostelCardResponse.builder()
                .id(hostel.getId())
                .name(hostel.getName())
                .ward(hostel.getWard())
                .addressDetail(hostel.getAddressDetail())
                .district(hostel.getDistrict())
                .city(hostel.getCity())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHostel(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Hostel hostel = hostelRepository.findById(id).orElseThrow(() -> new RuntimeException("Hostel not found"));
        if(!hostel.getOwner().getId().equals(userDetails.getId())) {
//            throw new RuntimeException("Ban khong co quyen sua phong nay");
            return ResponseEntity.badRequest().body(new MessageResponse("Bạn không có quyền sửa phòng này"));
        }
        if(roomService.existsByHostelIdAndStatus(hostel.getId(), RoomStatus.RENTED)){
//            throw new RuntimeException("Khong the xoa toa nha co phong dang cho thue");
            return ResponseEntity.badRequest().body(new MessageResponse("Không thể xóa tòa nhà có phòng đang cho thuê"));
        }
        List<HostelImage> hostelImages = hostelImageService.findByHostelId(id);
        for (HostelImage image : hostelImages){
            cloudinaryService.deleteImageByUrl(image.getUrl());
            hostelImageRepository.delete( image);
        }

        hostelService.deleteHostel(hostel);
        return ResponseEntity.ok(new MessageResponse("Xoa phong thanh cong"));
    }

    }


