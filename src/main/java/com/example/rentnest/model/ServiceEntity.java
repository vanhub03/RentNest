package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE services set is_deleted = true where id = ?")
@SQLRestriction("is_deleted = 0")
public class ServiceEntity extends BaseEntity{
    @Column(name="service_name", nullable = false)
    private String serviceName; //dien, nuoc, wifi

    @Column(name="unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "unit_name")
    private String unitName; //dien kw, nuoc khoi

    @Column(name="is_metered")
    private boolean isMetered; //true: can chot so dien nuoc moi thang, false: tron goi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;
}
