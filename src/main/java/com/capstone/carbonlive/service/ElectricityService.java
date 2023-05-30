package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.FeeResponse;
import com.capstone.carbonlive.dto.response.SeasonResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;

public interface ElectricityService {
    UsageResult<UsageResponse> getEachAll(Long id);
    UsageResult<SeasonResponse> getSeasonData();
    UsageResult<UsageWithNameResponse> getAll();
    UsageResult<FeeResponse> getFee();
}