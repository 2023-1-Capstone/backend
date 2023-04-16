package com.capstone.carbonlive.electricity;

import com.capstone.carbonlive.building.Building;
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
    private LocalDate recorded_at;

    @Column(nullable = false)
    private int usages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    @ToString.Exclude
    private Building building;

    @Builder(toBuilder = true)
    public Electricity(LocalDate recorded_at, int usages, Building building) {
        this.recorded_at = recorded_at;
        this.usages = usages;
        this.building = building;
    }
}