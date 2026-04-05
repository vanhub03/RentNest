package com.example.rentnest.service;

import com.example.rentnest.model.Contract;
import com.example.rentnest.model.dto.request.TenantOnboardRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ContractService extends BaseService<Contract, Long>{
    void onboardNewTenant(TenantOnboardRequest request, MultipartFile contractFile, MultipartFile cccdFront, MultipartFile cccdBack, Long landlordId) throws IOException;
}
