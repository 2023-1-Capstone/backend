package com.capstone.carbonlive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FeeBaseEntity {

    @Column(nullable = false)
    protected LocalDate recordedAt;

    protected Integer usages;

    protected Integer fee;
}