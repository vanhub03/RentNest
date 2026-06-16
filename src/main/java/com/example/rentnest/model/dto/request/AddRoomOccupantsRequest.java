package com.example.rentnest.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AddRoomOccupantsRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OccupantDto{
        private String fullName;
        private String phoneNumber;
        private String identityCard;
    }
    List<OccupantDto> occupants;


}
