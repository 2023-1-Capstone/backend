package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Water extends FeeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WATER_ID")
    private Long id;

    @Builder(toBuilder = true)
    public Water(LocalDate recordedAt, Integer usages, Integer prediction, Integer fee, Integer fee_prediction){
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.prediction = prediction;
        this.fee = fee;
        this.fee_prediction = fee_prediction;
    }
}
