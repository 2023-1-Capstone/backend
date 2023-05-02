package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Electricity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ELECTRICITY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    @ToString.Exclude
    private Building building;

    @Builder(toBuilder = true)
    public Electricity(LocalDate recordedAt, int usages, boolean prediction, Building building) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.prediction = prediction;
        this.building = building;
    }

    @Override
    public String toString() {
        return "Electricity{" +
                "recordedAt=" + recordedAt +
                ", usages=" + usages +
                ", id=" + id +
                '}';
    }
}