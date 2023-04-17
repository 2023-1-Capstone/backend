package com.capstone.carbonlive.building;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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


}
