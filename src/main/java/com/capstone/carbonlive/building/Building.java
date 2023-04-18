package com.capstone.carbonlive.building;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BUILDING_ID")
    private Long id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal gasArea;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal elecArea;

    @Column(length = 45)
    private String gasDescription;

    @Column(length = 45)
    private String elecDescription;

    @Builder(toBuilder = true)
    public Building(Long id, String name, BigDecimal gasArea, BigDecimal elecArea, String gasDescription, String elecDescription) {
        this.id = id;
        this.name = name;
        this.gasArea = gasArea;
        this.elecArea = elecArea;
        this.gasDescription = gasDescription;
        this.elecDescription = elecDescription;
    }
}
