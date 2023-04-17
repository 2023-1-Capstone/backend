package com.capstone.carbonlive.gas;

import com.capstone.carbonlive.building.Building;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private Integer usages;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private Building building;

}
