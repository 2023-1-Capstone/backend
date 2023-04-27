package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class CarbonYearResponse {
    private int year;
    private int usages;

    public CarbonYearResponse(int year, int usages) {
        this.year = year;
        this.usages = usages;
    }
}
