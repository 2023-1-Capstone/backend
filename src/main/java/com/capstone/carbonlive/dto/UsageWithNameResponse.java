package com.capstone.carbonlive.dto;

import lombok.Data;

@Data
public class UsageWithNameResponse {
    private String name;
    private UsageResult<UsageResponse> usages;
}
