package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "occupants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE occupants set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class Occupant extends BaseEntity {

    @Nationalized
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "identity_card", length = 20)
    private String identityCard; // Số CCCD/CMND

    @Column(name = "identity_card_front_url")
    private String identityCardFrontUrl; // Ảnh CCCD mặt trước

    @Column(name = "identity_card_back_url")
    private String identityCardBackUrl; // Ảnh CCCD mặt sau

    @Column(name = "is_representative")
    private boolean isRepresentative = false; // Người đại diện ký hợp đồng?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Optional: Nếu khách tải App và tạo tài khoản, ta sẽ link tài khoản đó vào đây
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private User userAccount;
}