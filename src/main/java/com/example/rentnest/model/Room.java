package com.example.rentnest.model;

import com.example.rentnest.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE rooms set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class Room extends BaseEntity{

    @Column(name = "room_name", nullable = false)
    private String roomName;

    private Double area; //dien tich

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    private Integer floor; //tang may

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id",nullable = false)
    private Hostel hostel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImage> images;
}
