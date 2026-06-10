package com.example.rentnest.repository;

import com.example.rentnest.model.InvoiceItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceItemRepository extends BaseRepository<InvoiceItem, Long>{
    @Query("""
            select h.id,
                   h.name,
                   coalesce(sum(case when item.service is null then item.amount else 0 end), 0), 
                   coalesce(sum(case when item.service is not null then item.amount else 0 end), 0),  
                   coalesce(sum(item.amount), 0) 
            from InvoiceItem item
            join item.invoice invoice
            join invoice.contract contract
            join contract.room room
            join room.hostel h
            where h.owner.id = :landlordId
              and invoice.invoiceMonth = :invoiceMonth
            group by h.id, h.name
            order by h.name
            """)
        // Gom doanh thu theo tung co so de ve bieu do cot va bang dong tien.
    List<Object[]> summarizeCashflowByHostel(@Param("landlordId") Long landlordId, @Param("invoiceMonth") String invoiceMonth);
   @Query("""
          select 
          coalesce( item.service.serviceName, 'Tien phong'),
          coalesce(sum(item.amount), 0)          
          from InvoiceItem item
          join item.invoice invoice
          join invoice.contract contract
          join contract.room room
          join room.hostel h
          where h.owner.id = :landlordId
            and invoice.invoiceMonth = :invoiceMonth
          group by item.service.serviceName
          order by coalesce(sum(item.amount), 0) DESC 
"""
   )
   // gom doanh thu theo loai item bieu do tron
   List<Object[]> summarizeRevenueStructure(@Param("landlordId") Long landlordId, @Param("invoiceMonth") String invoiceMonth);
}
//  dong 13 tinh tong tien phong
// dong 14 tinh tong tien dich vu
//  dong 15 tong tien ca phong va dich vu