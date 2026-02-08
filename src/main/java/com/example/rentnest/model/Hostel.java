package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "hostel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE hostel set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class Hostel extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "ward_code")
    private String wardCode; //ma phuong

    private String ward; //ten phuong

    @Column(name="district_code")
    private String districtCode; //ma quan huyen

    private String district;

    private String cityCode; //ma thanh pho, tinh

    private String city;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    private List<Room> rooms;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    private List<ServiceEntity> services;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HostelImage> images;
}
