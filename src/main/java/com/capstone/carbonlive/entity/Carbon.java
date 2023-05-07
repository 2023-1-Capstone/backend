package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Carbon extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARBON_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    @ToString.Exclude
    private Building building;

    @Builder(toBuilder = true)
    public Carbon(LocalDate recordedAt, Integer usages, Integer prediction, Building building) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.prediction = prediction;
        this.building = building;
    }

    @Override
    public String toString() {
        return "Carbon{" +
                "recordedAt=" + recordedAt +
                ", usages=" + usages +
                ", id=" + id +
                '}';
    }
}