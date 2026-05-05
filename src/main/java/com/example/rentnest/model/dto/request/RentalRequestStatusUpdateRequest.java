package com.example.rentnest.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequestStatusUpdateRequest {
    private String status;
    private String rejectReason;
}
