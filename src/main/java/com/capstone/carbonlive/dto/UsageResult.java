package com.capstone.carbonlive.dto;

import lombok.Data;

import java.util.List;

@Data
public class UsageResult {
    private List<UsageResponse> result;

    public UsageResult(List<UsageResponse> result) {
        this.result = result;
    }
}