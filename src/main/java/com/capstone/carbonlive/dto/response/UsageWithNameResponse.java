package com.capstone.carbonlive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageWithNameResponse {
    private String name;
    private List<UsageResponse> usagesList;
}
