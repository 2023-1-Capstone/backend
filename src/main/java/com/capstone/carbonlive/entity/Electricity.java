package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Electricity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ELECTRICITY_ID")
    private Long id;

    @Column(nullable = false)
    private LocalDate recordedAt;

    @Column(nullable = false)
    private int usages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    @ToString.Exclude
    private Building building;

    @Builder(toBuilder = true)
    public Electricity(LocalDate recordedAt, int usages, Building building) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.building = building;
    }
}