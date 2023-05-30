package com.capstone.carbonlive.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UsageWithNameResponse {
    private String name;
    private List<UsageResponse> usagesList;
}
