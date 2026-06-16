package com.example.rentnest.service;

import com.example.rentnest.model.Contract;
import com.example.rentnest.model.RentalRequest;
import com.example.rentnest.model.dto.request.TenantOnboardRequest;
import com.example.rentnest.model.dto.response.ContractListResponse;
import com.example.rentnest.model.dto.response.ContractPreviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ContractService extends BaseService<Contract, Long>{
    void onboardNewTenant(TenantOnboardRequest request, MultipartFile contractFile, List<MultipartFile> cccdFronts, List<MultipartFile> cccdBacks, Long landlordId) throws IOException;
    Contract createDraftFromRentalRequest(RentalRequest rentalRequest);
    ContractPreviewResponse getPreviewForLandlord(Long landlordId, Long rentalRequestId);
    ContractPreviewResponse getPreviewForTenant(Long tenantId, Long rentalRequestId);
    Page<ContractListResponse> getContractForLandlord(Long landlordId, String status, Pageable pageable);
    ContractListResponse renewContract(Long landlordId, Long contractId, int months);
}
