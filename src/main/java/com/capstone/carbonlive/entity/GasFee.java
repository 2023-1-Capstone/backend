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
public class GasFee extends FeeBaseEntity {

    @Id @GeneratedValue
    @Column(name = "GAS_FEE_ID")
    private Long id;

    @Builder(toBuilder = true)
    public GasFee(LocalDate recordedAt, Integer usages, Integer fee) {
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.fee = fee;
    }
}
