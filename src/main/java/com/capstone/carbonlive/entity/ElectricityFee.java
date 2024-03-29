package com.capstone.carbonlive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ElectricityFee extends FeeBaseEntity {

    @Id @GeneratedValue
    @Column(name = "ELECTRICITY_FEE_ID")
    private Long id;

    @Builder(toBuilder = true)
    public ElectricityFee(LocalDate recordedAt, Integer usages, Integer prediction, Integer fee, Integer fee_prediction) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.prediction = prediction;
        this.fee = fee;
        this.fee_prediction = fee_prediction;
    }
}
