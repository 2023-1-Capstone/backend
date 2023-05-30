package com.capstone.carbonlive.dto.response;

import lombok.Data;

@Data
public class UsageResponse {
    private int year;
    private UsagePredictionResponse[] usages = new UsagePredictionResponse[12];

    public UsageResponse() {
    }

    public UsageResponse(int year) {
        this.year = year;
    }
}