package com.capstone.carbonlive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FeeBaseEntity extends BaseEntity {

    @Column
    protected Integer fee;

    @Column
    protected Integer fee_prediction;
}
