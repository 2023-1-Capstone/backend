package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntireCarbon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENTIRE_CARBON_ID")
    private Long id;

    @Builder(toBuilder = true)
    public EntireCarbon(LocalDate recordedAt, Integer usages, Integer prediction) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.prediction = prediction;
    }
}
