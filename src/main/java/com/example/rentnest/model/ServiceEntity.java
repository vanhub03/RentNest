package com.example.rentnest.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
