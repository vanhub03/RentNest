package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "meter_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE meter_readings set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class MeterReading extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service; // Trỏ tới Dịch vụ Điện hoặc Nước

    @Column(name = "reading_month", nullable = false, length = 7)
    private String readingMonth; // Format: "2024-03"

    @Column(name = "old_index")
    private Double oldIndex; // Chỉ số cũ (tháng trước)

    @Column(name = "new_index", nullable = false)
    private Double newIndex; // Chỉ số mới (tháng này)

    @Column(name = "proof_image_url")
    private String proofImageUrl; // Ảnh chụp đồng hồ điện/nước tránh cãi nhau
}