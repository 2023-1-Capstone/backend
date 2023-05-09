package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class WaterResponse {
    private int year;
    private WaterPredictionFeeResponse[] usages = new WaterPredictionFeeResponse[12];

    public WaterResponse() {
    }

    public WaterResponse(int year) {
        this.year = year;
    }

    public WaterResponse(int year, WaterPredictionFeeResponse[] usages) {
        this.year = year;
        this.usages = usages;
    }
}
