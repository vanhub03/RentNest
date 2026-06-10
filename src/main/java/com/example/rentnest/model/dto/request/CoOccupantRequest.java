package com.example.rentnest.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoOccupantRequest {
    private String fullName; //ten
    private String phoneNumber; // sdt
    private String identityCard; // so cccd
   // private String identityCardFrontUrl; //url anh cccd mat truoc
    //private String identityCardBackUrl; //url anh cccd mat sau
}
