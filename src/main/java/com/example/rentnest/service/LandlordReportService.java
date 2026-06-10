package com.example.rentnest.service;

import com.example.rentnest.model.dto.response.LandlordReportOverviewResponse;

public interface LandlordReportService {
   LandlordReportOverviewResponse getOverview(Long landlordID,String invoiceMonth);
}
