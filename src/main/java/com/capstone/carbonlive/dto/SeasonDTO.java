package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class SeasonDTO {
    int startYear;
    int endYear;
    int[] usages = new int[4];
}
