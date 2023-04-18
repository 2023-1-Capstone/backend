package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class UsageResponse {
    private String year;
    private int usages;

    public UsageResponse(String year, int usages) {
        this.year = year;
        this.usages = usages;
    }
}
