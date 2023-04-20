package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import com.capstone.carbonlive.service.common.GetUsageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GasService {

    private final BuildingRepository buildingRepository;
    private final GasRepository gasRepository;

    public UsageResult findByBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(
                () -> new RuntimeException());

        List<Gas> gasList = gasRepository.findByBuildingOrderByRecordedAtAsc(building);

        return GetUsageResult.getUsageResult(gasList);
    }
}
