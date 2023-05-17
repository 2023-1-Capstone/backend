package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class CarbonYearResponse {
    private int year;
    private int[] usages = new int[12];

    public CarbonYearResponse(int year) {
        this.year = year;
    }
}
