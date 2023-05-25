package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.response.WaterResponse;
import com.capstone.carbonlive.entity.Water;
import com.capstone.carbonlive.repository.WaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.getWaterResult;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl implements WaterService {

    private final WaterRepository waterRepository;

    @Override
    public UsageResult<WaterResponse> getAll() {
        List<Water> waterList = waterRepository.findAll(
                Sort.by("recordedAt").ascending().and(Sort.by("prediction").descending()));

        return getWaterResult(waterList);
    }
}
