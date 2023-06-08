package com.capstone.carbonlive.dto.response;

import lombok.Data;

@Data
public class CarbonYearResponse {
    private int year;
    private UsagePredictionResponse[] usages = new UsagePredictionResponse[12];

    public CarbonYearResponse(int year) {
        this.year = year;
    }
}
