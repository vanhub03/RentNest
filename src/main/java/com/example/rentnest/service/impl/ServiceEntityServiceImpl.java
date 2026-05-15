package com.example.rentnest.service.impl;

import com.example.rentnest.model.Contract;
import com.example.rentnest.model.Hostel;
import com.example.rentnest.model.ServiceEntity;
import com.example.rentnest.model.dto.request.ServiceEntityRequest;
import com.example.rentnest.model.dto.response.ServiceEntityResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.HostelRepository;
import com.example.rentnest.repository.ServiceEntityRepository;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.ServiceEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceEntityServiceImpl extends BaseServiceImpl<ServiceEntity, Long, ServiceEntityRepository> implements ServiceEntityService {
    private final ServiceEntityRepository serviceEntityRepository;
    private final HostelRepository hostelRepository;

    public ServiceEntityServiceImpl(ServiceEntityRepository serviceEntityRepository, HostelRepository hostelRepository) {
        super();
        this.serviceEntityRepository = serviceEntityRepository;
        this.hostelRepository = hostelRepository;
    }

    @Override
    public Page<ServiceEntityResponse> getServiceForLandlord(Long landlordId, Long hostelId, Pageable pageable) {
        Page<ServiceEntity> services = hostelId != null ? serviceEntityRepository.findByHostel_Owner_IdAndHostel_Id(landlordId, hostelId, pageable) :
                serviceEntityRepository.findByHostel_Owner_Id(landlordId, pageable);
        return services.map(this::toResponse);
    }

    @Override
    @Transactional
    public List<ServiceEntityResponse> createService(Long landlordId, ServiceEntityRequest request) {
        //validate truoc khi create
        validateRequest(request);
        //truong hop landlord chua tao toa nha nao thi khong the tao service vi thieu hostel id hop le
        List<Hostel> targetHostels = request.isApplyAllHostels() ? hostelRepository.findByOwner_Id(landlordId) : List.of(findOwnedHostel(request.getHostelId(), landlordId));
        if(targetHostels.isEmpty()){
            throw new RuntimeException("Bạn chưa có tòa nhà nào để tạo dịch vụ");
        }
        //tao mot service cho tung hostel de sau nay tinh hoa don co the query truc tiep theo hostel/phong
        return serviceEntityRepository.saveAll(
                targetHostels.stream().map(
                        hostel -> ServiceEntity.builder()
                                .hostel(hostel)
                                .serviceName(request.getServiceName().trim())
                                .unitPrice(request.getUnitPrice())
                                .unitName(request.getUnitName().trim())
                                .isMetered(request.isMetered())
                                .build()
                ).toList()
        ).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ServiceEntityResponse updateService(Long landlordId, Long serviceId, ServiceEntityRequest request) {
        //update se khong ho tro applyAllHostel, chi update dung service chi dinh
        validateRequest(request);
        ServiceEntity service = serviceEntityRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));
        if(!service.getHostel().getOwner().getId().equals(landlordId)){
            throw new RuntimeException("Bạn không có quyền sửa dịch vụ này");
        }

        //Cho phep chuyen service sang mot hostel khac, nhung hostel moi van phai cung chu nha
        Hostel hostel = findOwnedHostel(request.getHostelId(), landlordId);
        service.setHostel(hostel);
        service.setServiceName(request.getServiceName().trim());
        service.setUnitPrice(request.getUnitPrice());
        service.setUnitName(request.getUnitName().trim());
        service.setMetered(request.isMetered());
        return toResponse(serviceEntityRepository.save(service));
    }

    @Override
    @Transactional
    public void deleteService(Long landlordId, Long serviceId) {
        ServiceEntity service = serviceEntityRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));
        if(!service.getHostel().getOwner().getId().equals(landlordId)){
            throw new RuntimeException("Bạn không có quyền xóa dịch vụ này");
        }
        serviceEntityRepository.delete(service);
    }

    private ServiceEntityResponse toResponse(ServiceEntity service){
        Hostel hostel = service.getHostel();
        return ServiceEntityResponse.builder()
                .id(service.getId())
                .hostelId(hostel.getId())
                .hostelName(hostel.getName())
                .serviceName(service.getServiceName())
                .unitPrice(service.getUnitPrice())
                .unitName(service.getUnitName())
                .metered(service.isMetered())
                .build();
    }

    private void validateRequest(ServiceEntityRequest request){
        if(request.getServiceName() == null || request.getServiceName().isBlank()){
            throw new RuntimeException("Tên dịch vụ không được để trống");
        }
        //cho phep don gia = 0 nhung khong cho so am
        if(request.getUnitPrice() == null || request.getUnitPrice().signum() < 0){
            throw new RuntimeException("Đơn giá không hợp lệ");
        }
        // unitName cần có de tao hoa don sau nay
        if(request.getUnitName() == null || request.getUnitName().isBlank()){
            throw new RuntimeException("Đơn vị tính không được để trống");
        }
    }

    private Hostel findOwnedHostel(Long hostelId, Long landlordId){
        //moi service entity luon phai gan voi 1 hostel
        if(hostelId == null){
            throw new RuntimeException("Vui lòng chọn tòa nhà áp dụng");
        }
        return hostelRepository.findByIdAndOwner_Id(hostelId, landlordId).orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà"));
    }
}
