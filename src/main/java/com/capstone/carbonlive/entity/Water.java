package com.capstone.carbonlive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Water extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WATER_ID")
    private Long id;

    @Builder(toBuilder = true)
    public Water(LocalDate recordedAt, int usages){
        this.recordedAt = recordedAt;
        this.usages = usages;
    }
}
