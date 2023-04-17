package com.capstone.carbonlive.carbon;

import com.capstone.carbonlive.building.Building;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Carbon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARBON_ID")
    private Long id;

    @Column(nullable = false)
    private LocalDate recordedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal usages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    @ToString.Exclude
    private Building building;

    @Builder(toBuilder = true)
    public Carbon(LocalDate recordedAt, BigDecimal usages, Building building) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.building = building;
    }
}