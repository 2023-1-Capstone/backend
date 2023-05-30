package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Water extends PredictionBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WATER_ID")
    private Long id;

    private Integer fee;

    @Builder(toBuilder = true)
    public Water(LocalDate recordedAt, Integer usages, Integer prediction, Integer fee){
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.prediction = prediction;
        this.fee = fee;
    }
}
