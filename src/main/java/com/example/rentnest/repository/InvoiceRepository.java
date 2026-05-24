package com.example.rentnest.repository;

import com.example.rentnest.enums.InvoiceStatus;
import com.example.rentnest.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends BaseRepository<Invoice, Long>{
    Page<Invoice> findByContract_Room_Hostel_Owner_IdAndInvoiceMonth(Long contractRoomHostelOwnerId, String invoiceMonth, Pageable pageable);
    Page<Invoice> findByContract_Room_Hostel_Owner_IdAndInvoiceMonthAndStatus(Long contractRoomHostelOwnerId, String invoiceMonth, InvoiceStatus status, Pageable pageable);

    Optional<Invoice> findByContract_IdAndInvoiceMonth(Long contractId, String invoiceMonth);

    Optional<Invoice> findByIdAndContract_Room_Hostel_Owner_Id(Long id, Long landlordId);
    long countByContract_Room_Hostel_Owner_IdAndInvoiceMonth(Long landlordId, String invoiceMonth);

    @Query("select coalesce(sum(i.amountPaid), 0) " +
            "from Invoice i " +
            "where i.contract.room.hostel.owner.id = :landlordId " +
            "and i.invoiceMonth = :invoiceMonth")
    BigDecimal sumPaidByLandlordAndMonth(@Param("landlordId") Long landlordId, @Param("invoiceMonth") String invoiceMonth);

    @Query("select coalesce(sum(i.totalAmount - i.amountPaid), 0) " +
            "from Invoice i " +
            "where i.contract.room.hostel.owner.id = :landlordId " +
            "and i.invoiceMonth = :invoiceMonth " +
            "and i.status <> com.example.rentnest.enums.InvoiceStatus.PAID")
    BigDecimal sumDebtByLandlordAndMonth(@Param("landlordId") Long landlordId, @Param("invoiceMonth") String invoiceMonth);

    //lay hoa don chua thanh toan gan nhat cua tenant de FE hien thi thanh card canh bao phia tren
    @Query("select i from Invoice i " +
            "left join i.contract c " +
            "left join c.representativeOccupant o " +
            "left join o.userAccount u " +
            "left join c.rentalRequest rr " +
            "left join rr.tenant requestTenant " +
            "where (u.id = :tenantId or requestTenant.id = :tenantId) " +
            "and i.status <> com.example.rentnest.enums.InvoiceStatus.PAID " +
            "order by i.dueDate asc, i.id desc")
    List<Invoice> findCurrentUnoaidTenantInvoices(@Param("tenantId") Long tenantId, Pageable pageable);


    //lay hoa don cua tenant theo nam
    @Query(
            value = "select i " +
                    "from Invoice i " +
                    "left join i.contract c " +
                    "left join c.representativeOccupant o " +
                    "left join o.userAccount u " +
                    "left join c.rentalRequest rr " +
                    "left join rr.tenant requestTenant " +
                    "where (u.id = :tenantId or requestTenant.id = :tenantId) " +
                    "and (:year is null or i.invoiceMonth like concat(:year, '-%'))",
            countQuery = "select count(i) " +
                    "from Invoice i " +
                    "left join i.contract c " +
                    "left join c.representativeOccupant o " +
                    "left join o.userAccount u " +
                    "left join c.rentalRequest rr " +
                    "left join rr.tenant requestTenant " +
                    "where (u.id = :tenantId or requestTenant.id = :tenantId) " +
                    "and (:year is null or i.invoiceMonth like concat(:year, '-%'))"
    )
    Page<Invoice> findTenantInvoices(@Param("tenantId") Long tenantId, @Param("year") String year, Pageable pageable);

    //lay chi tiet 1 hoa don, dong thoi kiem tra hoa don do co that su thuoc tenant dang dang nhap
    @Query("select i " +
            "from Invoice i " +
            "left join i.contract c " +
            "left join c.representativeOccupant o " +
            "left join o.userAccount u " +
            "left join c.rentalRequest rr " +
            "left join rr.tenant requestTenant " +
            "where i.id = :invoiceId and " +
            "(u.id = :tenantId or requestTenant.id = :tenantId)"
    )
    Optional<Invoice> findTenantInvoiceById(@Param("tenantUd") Long tenantId, @Param("invoiceId") Long invoiceId);
}
