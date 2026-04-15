package com.example.rentnest.model;


import com.example.rentnest.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rental_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE rental_requests set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class RentalRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "expected_move_in_date")
    private LocalDate expectedMoveInDate;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "reject_reason")
    private String rejectReason;
}
