package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class UsageResponse {
    private String year;
    private int[] usages = new int[12];
}