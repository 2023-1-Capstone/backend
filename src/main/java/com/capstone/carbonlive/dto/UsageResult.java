package com.capstone.carbonlive.dto;

import java.util.List;

public class UsageResult {
    private List<UsageResponse> result;

    public UsageResult(List<UsageResponse> result) {
        this.result = result;
    }
}
