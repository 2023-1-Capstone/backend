package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class SeasonResponse {
    private int startYear;
    private int endYear;
    private int[] usages = new int[4];

    public SeasonResponse(int startYear) {
        this.startYear = startYear;
        this.endYear = startYear + 1;
    }
}
