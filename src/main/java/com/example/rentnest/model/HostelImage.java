package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hostel_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostelImage extends BaseEntity{

    @Column(nullable = false)
    private String url;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "is_thumbnail")
    private boolean isThumbnail = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;
}
