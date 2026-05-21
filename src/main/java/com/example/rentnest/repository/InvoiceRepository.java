package com.example.rentnest.repository;

import com.example.rentnest.enums.InvoiceStatus;
import com.example.rentnest.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
}
