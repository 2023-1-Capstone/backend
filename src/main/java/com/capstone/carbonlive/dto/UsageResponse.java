package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class UsageResponse {
    private int year;
    private int[] usages = new int[12];

    public UsageResponse() {
    }

    public UsageResponse(int year) {
        this.year = year;
    }
}