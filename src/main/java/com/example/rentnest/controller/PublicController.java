package com.example.rentnest.controller;

import com.example.rentnest.Utils.EmailUtils;
import com.example.rentnest.model.Room;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.model.dto.response.RequestRentRoom;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import com.example.rentnest.service.EmailService;
import com.example.rentnest.service.RentalRequestService;
import com.example.rentnest.service.RoomService;
import com.example.rentnest.service.UserService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final RoomService roomService;
    private final Configuration configuration;
    private final EmailService emailService;
    private final RentalRequestService rentalRequestService;

    public PublicController(RoomService roomService , Configuration configuration, EmailService emailService, RentalRequestService rentalRequestService) {
        this.roomService = roomService;
        this.configuration = configuration;
        this.emailService = emailService;
        this.rentalRequestService = rentalRequestService;
    }

    @GetMapping("/latest-rooms")
    public ResponseEntity<List<RoomCardResponse>> getLatesRooms(){
        List<Room> rooms = roomService.findTop4ByOrderByCreatedAtDesc();
        List<RoomCardResponse> response = rooms.stream().map(room -> {
           String location = "";
           if(room.getHostel() != null){
               location = room.getHostel().getWard() + ", " + room.getHostel().getCity();
           }
           String thumnail = null;
           if(room.getImages() != null && !room.getImages().isEmpty()){
               thumnail = room.getImages().get(0).getUrl();
           }
           return RoomCardResponse.builder()
                   .id(room.getId())
                   .title(room.getRoomName())
                   .price(room.getBasePrice())
                   .area(room.getArea())
                   .status(room.getStatus().name())
                   .location(location)
                   .thumbnail(thumnail)
                   .bedType(room.getBedType())
                   .bathCount(room.getBathCount())
                   .floor(room.getFloor().toString())
                    .build();
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/locations")
    public ResponseEntity<List<String>> getAvailableLocations(){
        List<String> locations = roomService.findAvailableLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getPublicRooms(
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false) String wardCode,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
            ){
        Page<RoomCardResponse> result = roomService.getPublicRooms(cityCode, wardCode, minPrice, maxPrice, sort, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<?> getRoomDetail(@PathVariable Long id){
        return ResponseEntity.ok(roomService.getRoomDetailPublic(id));
    }
    @PostMapping("/request-rooms")
    public ResponseEntity<?> requestRentRoom(@RequestBody RequestRentRoom requestRentRoom ) throws IOException, TemplateException {
        rentalRequestService.createRequest(requestRentRoom);

        return ResponseEntity.ok(new MessageResponse("Gửi yêu cầu thuê phòng thành công"));



    }
}
