package com.example.rentnest.controller;

import com.example.rentnest.model.Room;
import com.example.rentnest.model.dto.response.RoomCardResponse;
import com.example.rentnest.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final RoomService roomService;

    public PublicController(RoomService roomService) {
        this.roomService = roomService;
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
        }).toList();
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
}
