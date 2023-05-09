package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class FeeResponse {
    private int year;
    private UsageFeeResponse[] feeResponses = new UsageFeeResponse[12];

    public FeeResponse(int year) {
        this.year = year;
    }

    public FeeResponse(int year, UsageFeeResponse[] feeResponses) {
        this.year = year;
        this.feeResponses = feeResponses;
    }
}
