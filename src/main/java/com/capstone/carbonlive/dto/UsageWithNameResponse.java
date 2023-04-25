package com.capstone.carbonlive.dto;

import lombok.Data;

import java.util.List;

@Data
public class UsageWithNameResponse {
    private String name;
    private List<UsageResponse> usagesList;
}
