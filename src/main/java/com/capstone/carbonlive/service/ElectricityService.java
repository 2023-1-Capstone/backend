package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;

public interface ElectricityService {
    UsageResult<UsageResponse> getEachAll(Long id);
    UsageResult<SeasonResponse> getSeasonData();
    UsageResult<UsageWithNameResponse> getAll();
    UsageResult<FeeResponse> getFee();
}