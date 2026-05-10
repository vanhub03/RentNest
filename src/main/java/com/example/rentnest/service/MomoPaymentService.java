package com.example.rentnest.service;

import com.example.rentnest.model.dto.response.MomoPaymentUrlResponse;
import com.example.rentnest.model.dto.response.MomoReturnResponse;

import java.util.Map;

public interface MomoPaymentService {
    MomoPaymentUrlResponse createDepositPaymentUrl(Long tenantId, Long rentalRequestId);
    MomoReturnResponse handleDepositReturn(Long tenantId, Map<String, String> momoParams);
//    Map<String, Object> handleDepositIpn(Map<String, Object> momoParams);
}
