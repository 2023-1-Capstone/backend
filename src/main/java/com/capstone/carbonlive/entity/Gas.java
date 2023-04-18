package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GAS_ID")
    private Long id;

    @Column(nullable = false)
    private LocalDate recordedAt;

    @Column(nullable = false)
    private int usages;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    @ToString.Exclude
    private Building building;

    @Builder(toBuilder = true)
    public Gas(Long id, LocalDate recordedAt, int usages, Building building) {
        this.id = id;
        this.recordedAt = recordedAt;
        this.usages = usages;
        this.building = building;
    }
}
