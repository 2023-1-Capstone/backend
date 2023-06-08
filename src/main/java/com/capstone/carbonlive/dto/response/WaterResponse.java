package com.capstone.carbonlive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterResponse {
    private int year;
    private WaterPredictionFeeResponse[] usages = new WaterPredictionFeeResponse[12];

    public WaterResponse(int year) {
        this.year = year;
    }
}
