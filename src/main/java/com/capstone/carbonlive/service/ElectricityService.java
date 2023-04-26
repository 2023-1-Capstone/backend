package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.UsageWithNameResponse;

public interface ElectricityService {
    UsageResult<UsageResponse> getEachAll(Long id);
    UsageResult<SeasonResponse> getSeasonData();
    UsageResult<UsageWithNameResponse> getAll();
}