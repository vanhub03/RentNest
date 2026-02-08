package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImage extends BaseEntity{
    @Column(nullable = false)
    private String url;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "is_thumbnail")
    private boolean isThumbnail = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
}
