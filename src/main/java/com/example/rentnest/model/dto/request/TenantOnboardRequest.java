package com.example.rentnest.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TenantOnboardRequest {
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal depositAmount;
    private String depositMethod;
    private List<OccupantDto> occupants;

    @Data
    public static class OccupantDto{
        private String fullName;
        private String phoneNumber;
        private String identityCard;

        @JsonProperty("isRepresentative")
        private boolean isRepresentative;
    }
}
