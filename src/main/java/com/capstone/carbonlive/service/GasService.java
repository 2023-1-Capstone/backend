package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.getUsageResult;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GasService {

    private final BuildingRepository buildingRepository;
    private final GasRepository gasRepository;

    public UsageResult<UsageResponse> findByBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(
                () -> new RuntimeException());

        List<Gas> gasList = gasRepository.findByBuildingOrderByRecordedAtAsc(building);

        return getUsageResult(gasList);
    }
}
