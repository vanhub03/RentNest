package com.example.rentnest.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestRentRoom {
    private Long roomId;
    private Long userId;
    private LocalDate expectedMoveInDate;
    private String notes;


}
