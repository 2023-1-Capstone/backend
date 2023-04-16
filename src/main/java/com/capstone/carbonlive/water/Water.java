package com.capstone.carbonlive.water;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Water {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WATER_ID")
    private Long id;

    @Column(nullable = false)
    private LocalDate recordedAt;

    @Column(nullable = false)
    private Integer usages;

}
