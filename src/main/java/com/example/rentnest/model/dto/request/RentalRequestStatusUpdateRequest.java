package com.example.rentnest.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalRequestStatusUpdateRequest {
    private String status;
    private String rejectReason;
}
