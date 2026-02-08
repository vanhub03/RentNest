package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE contracts set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class Contract extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant; // Người thuê

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount; // Tiền cọc

    @Column(name = "contract_file_url")
    private String contractFileUrl;

    @Column(name = "contract_file_public_id")
    private String contractFilePublicId;

    private boolean isActive = true; // true: Hợp đồng đang hiệu lực
}
