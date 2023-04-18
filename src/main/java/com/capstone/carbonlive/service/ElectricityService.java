package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResult;
import org.springframework.stereotype.Service;

public interface ElectricityService {
    UsageResult getEachAll(Long id);
}