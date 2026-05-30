package com.example.rentnest.service.impl;

import com.example.rentnest.enums.ContractStatus;
import com.example.rentnest.enums.InvoiceStatus;
import com.example.rentnest.model.*;
import com.example.rentnest.model.dto.request.InvoiceGenerateRequest;
import com.example.rentnest.model.dto.response.InvoiceResponse;
import com.example.rentnest.model.dto.response.InvoiceStatsResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.InvoiceRepository;
import com.example.rentnest.repository.MeterReadingRepository;
import com.example.rentnest.service.ContractService;
import com.example.rentnest.service.InvoiceService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final MeterReadingRepository meterReadingRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, ContractRepository contractRepository, MeterReadingRepository meterReadingRepository) {
        super();
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = contractRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    @Override
    @Transactional
    public Page<InvoiceResponse> getInvoicesForLandlord(Long landlordId, String invoiceMonth, InvoiceStatus status, Pageable pageable) {
        //neu khong chon status thi se lay tat ca status
        Page<Invoice> invoices = status == null
                ? invoiceRepository.findByContract_Room_Hostel_Owner_IdAndInvoiceMonth(landlordId,
                invoiceMonth,
                pageable)
                : invoiceRepository.findByContract_Room_Hostel_Owner_IdAndInvoiceMonthAndStatus(landlordId,
                invoiceMonth,
                status,
                pageable);
        return invoices.map(this::toResponse);
    }

    @Override
    @Transactional
    public InvoiceStatsResponse getStatsForLandlord(Long landlordId, String invoiceMonth) {
        return InvoiceStatsResponse.builder()
                .totalInvoices(invoiceRepository.countByContract_Room_Hostel_Owner_IdAndInvoiceMonth(landlordId, invoiceMonth))
                .paidAmount(invoiceRepository.sumPaidByLandlordAndMonth(landlordId, invoiceMonth))
                .debtAmount(invoiceRepository.sumDebtByLandlordAndMonth(landlordId, invoiceMonth))
                .build();
    }

    @Override
    public List<InvoiceResponse> generateInvoices(Long landlordId, InvoiceGenerateRequest request) {
        //moi lan sinh hoa don, can phai biet toa nha nao va thang nao
        if(request.getHostelId() == null || request.getInvoiceMonth() == null || request.getInvoiceMonth().isBlank()){
            throw new RuntimeException("Vui lòng chọn tòa nhà và tháng sinh hóa đơn");
        }
        //FE gui danh sach chi so theo tung phong + tung service
        //map danh sach voi key roomId:serviceId de khi duyet tung contract/service tra cuu
        Map<String, InvoiceGenerateRequest.MeterReadingDto> readingMap = (
                request.getReadings() == null ? List.<InvoiceGenerateRequest.MeterReadingDto>of() : request.getReadings()
                ).stream()
                //chi giu nhung dong co du roomId, serviceId a chi so moi, nhung dong thieu du lieu thi bo qua
                .filter(
                        reading -> reading.getRoomId() != null
                                && reading.getServiceId() != null
                                && reading.getNewIndex() != null
                )
                .collect(
                        Collectors.toMap(
                                reading -> reading.getRoomId() + ":" + reading.getServiceId(),
                                Function.identity(),
                                //neu FE gui trung 1 phong/service, thi lay dong cuoi cung
                                (first, second) -> second));

        //chi sinh hoa don cho hop dong dang hieu luc trong toa nha landlord chon
        //contract active nghia la tenant da dat coc/dang thue phong, nen co nghia vu thu tien hang thang
        List<Contract> contracts = contractRepository.findByRoom_Hostel_Owner_IdAndRoom_Hostel_IdAndStatus(landlordId, request.getHostelId(), ContractStatus.ACTIVE);

        //voi moi hop dong active, tao moi hoac ghi de hoa don cua thang do roi tra dto cho FE
        return contracts.stream().map(
                contract ->
                createOrReplaceInvoice(contract, request.getInvoiceMonth(), readingMap))
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public InvoiceResponse markPaid(Long landlordId, Long invoiceId) {
        //tim theo invoice id + owner id de landlord khong the xac nhan hoa don cua landlord khac
        Invoice invoice = invoiceRepository.findByIdAndContract_Room_Hostel_Owner_Id(invoiceId, landlordId).orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        invoice.setAmountPaid(invoice.getTotalAmount());
        invoice.setStatus(InvoiceStatus.PAID);
        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public Page<InvoiceResponse> getInvoicesForTenant(Long tenantId, String year, Pageable pageable) {
        return invoiceRepository.findTenantInvoices(tenantId, year, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public InvoiceResponse getCurrentUnpaidInvoiceForTenant(Long tenantId) {
        return invoiceRepository.findCurrentUnpaidTenantInvoices(tenantId, Pageable.ofSize(1))
                .stream().findFirst().map(this::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public InvoiceResponse getInvoiceForTenant(Long tenantId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findTenantInvoiceById(tenantId, invoiceId).orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        return toResponse(invoice);
    }

    private Invoice createOrReplaceInvoice(Contract contract, String invoiceMonth, Map<String, InvoiceGenerateRequest.MeterReadingDto> readingMap){
        //neu hoa don thang nay da ton tai thi cap nhat lai items/tong tien
        //neu chua co thi tao hoa don nhap PENDING
        Invoice invoice = invoiceRepository.findByContract_IdAndInvoiceMonth(contract.getId(), invoiceMonth).orElseGet(
                () -> Invoice.builder()
                        .contract(contract)
                        .invoiceMonth(invoiceMonth)
                        .amountPaid(BigDecimal.ZERO)
                        .status(InvoiceStatus.PENDING)
                        .dueDate(LocalDate.now().plusDays(7))
                        .totalAmount(BigDecimal.ZERO)
                        .build()
        );

        //Invoice.items can phai clear collection de hibernate xoa item cu
        if(invoice.getItems() == null){
            invoice.setItems(new ArrayList<>());
        }else{
            invoice.getItems().clear();
        }
        //save truoc de dam bao invoice co id
        invoice = invoiceRepository.save(invoice);
        //danh sach item moi se gom: 1 tien phong + nhieu tien dich vu
        List<InvoiceItem> items = new ArrayList<>();
        //total la tong phai thu cua hoa don
        BigDecimal total = BigDecimal.ZERO;
        //tien phong co dinh
        BigDecimal roomAmount = contract.getRoom().getBasePrice();
        total = total.add(roomAmount);
        //dong dau tien luon luon la tien phong
        items.add(InvoiceItem.builder()
                        .invoice(invoice)
                        .description("Tiền phòng tháng: " + invoiceMonth)
                        .amount(roomAmount)
                .build());
        //duyet toan bo dich vu da cau hinh cho hostel cua phong
        //dich vu tron goi thi tu cong, dich vu phai chot so se chi cong neu co chi so trong readingMap
        for(ServiceEntity service : contract.getRoom().getHostel().getServices()){
            BigDecimal serviceAmount = resolveServiceAmount(contract.getRoom(), service, invoiceMonth, readingMap);
            //serviceAmount null nghia la dich vu can chot so nhung landlord chua nhap chi so, nen chua dua vao hoa don
            if(serviceAmount == null) continue;
            total = total.add(serviceAmount);
            //moi service duoc luu thanh mot InvoiceItem trong hoa don
            items.add(InvoiceItem.builder()
                            .invoice(invoice)
                            .service(service)
                            .description(service.getServiceName() + " tháng " + invoiceMonth)
                            .amount(serviceAmount)
                    .build());
        }
        //gan toan bo item moi vao collection cua invoice
        invoice.getItems().addAll(items);
        //cap nhat tong tien sau khi da cong tien phong va cac dich vu
        invoice.setTotalAmount(total);
        if(invoice.getAmountPaid() == null){
            invoice.setAmountPaid(BigDecimal.ZERO);
        }
        //neu da thu du thi PAID, con khong thi pending
        //truong hop generate lai thi hoa don sau khi da paid nhung tong tang len thi se chuyen ve pending
        invoice.setStatus(invoice.getAmountPaid().compareTo(total) >= 0 ? InvoiceStatus.PAID : InvoiceStatus.PENDING);
        return invoiceRepository.save(invoice);
    }

    private BigDecimal resolveServiceAmount(Room room, ServiceEntity service, String invoiceMonth, Map<String, InvoiceGenerateRequest.MeterReadingDto> readingMap){
        //dich vu tron goi khong can chi so, lay nguyen unitPrice lam tien dich vu
        if(!service.isMetered()){
            return service.getUnitPrice();
        }

        //dich vu metered can tim dong chi so ma FE gui cho dung phong va dung service
        InvoiceGenerateRequest.MeterReadingDto input = readingMap.get(room.getId() + ":" + service.getId());
        if(input == null){
            //neu khong co chi so thi chua the tinh tien dien/nuoc, tra null de bo qua item nay
            return null;
        }
        Double oldIndex = input.getOldIndex();
        if(oldIndex == null){
            //neu landlord khong nhap chi so cu, tu lay chi so moi gan nhat cua thang truoc do
            //neu chua tung co lich su thi mac dinh la 0
            oldIndex = meterReadingRepository.findTopByRoom_IdAndService_IdAndReadingMonthLessThanOrderByReadingMonthDesc(
                    room.getId(), service.getId(), invoiceMonth).map(MeterReading::getNewIndex).orElse(0D);
        }
        //usage la luong tieu thu = chi so moi - chi so cu
        //Math.max de tranh tao so tien am neu nhap nham chi so new nho hon old
        double usage = Math.max(input.getNewIndex() - oldIndex, 0D);

        //neu da co reading cho room/service/month thi cap nhat, chua co thi tao moi
        MeterReading reading = meterReadingRepository.findByRoom_IdAndService_IdAndReadingMonth(room.getId(), service.getId(), invoiceMonth)
                .orElseGet(() -> MeterReading.builder()
                        .room(room)
                        .service(service)
                        .readingMonth(invoiceMonth)
                        .build());
        //luu lai chi so cu/moi de thang sau tinh tiep
        reading.setOldIndex(oldIndex);
        reading.setNewIndex(input.getNewIndex());
        meterReadingRepository.save(reading);

        //thanh tien = don gia * luong tieu thu
        return service.getUnitPrice().multiply(BigDecimal.valueOf(usage));
    }

    private InvoiceResponse toResponse(Invoice invoice){
        Contract contract = invoice.getContract();
        Room room = contract.getRoom();
        Occupant tenant = contract.getRepresentativeOccupant();
        //quy uoc item dau tien la tien phong, ne lay item[0]
        BigDecimal roomAmount = invoice.getItems() == null || invoice.getItems().isEmpty() ? BigDecimal.ZERO : invoice.getItems().get(0).getAmount();
        BigDecimal serviceAmount = invoice.getTotalAmount().subtract(roomAmount);
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .hostelName(room.getHostel().getName())
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .tenantName(tenant != null ? tenant.getFullName() : null)
                .invoiceMonth(invoice.getInvoiceMonth())
                .roomAmount(roomAmount)
                .serviceAmount(serviceAmount)
                .totalAmount(invoice.getTotalAmount())
                .amountPaid(invoice.getAmountPaid())
                .status(invoice.getStatus().name())
                .duwDate(invoice.getDueDate())
                .items(invoice.getItems() == null ? List.of() : invoice.getItems().stream()
                        .map(item -> InvoiceResponse.ItemDto.builder()
                                .id(item.getId())
                                .serviceId(item.getService() != null ? item.getService().getId() : null)
                                .amount(item.getAmount())
                                .description(item.getDescription())
                                .build()).toList()
                        ).build();
    }
}
