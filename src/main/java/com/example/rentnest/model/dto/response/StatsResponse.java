package com.example.rentnest.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsResponse {
    private long total;
    private long pending;
    private long approved;
    private long rejected;
}
