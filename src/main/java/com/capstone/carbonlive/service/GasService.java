package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.entity.GasFee;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasFeeRepository;
import com.capstone.carbonlive.repository.GasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GasService {

    private final BuildingRepository buildingRepository;
    private final GasRepository gasRepository;
    private final GasFeeRepository gasFeeRepository;

    /**
     * 건물별 가스 사용량
     */
    public UsageResult<UsageResponse> findByBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(RuntimeException::new);

        return getBuildingUsageResult(gasRepository.findByBuildingOrderByRecordedAtAscPredictionDesc(building));
    }

    /**
     * 계절별 가스 사용량
     */
    public UsageResult<SeasonResponse> findBySeason() {
        return getSeasonUsageResult(gasRepository.findAll(Sort.by("recordedAt").ascending()));
    }

    public UsageResult<UsageWithNameResponse> findAll() {
        UsageResult<UsageWithNameResponse> result = new UsageResult<>(new ArrayList<>());

        List<Building> buildings = buildingRepository.findAll();
        for (Building b : buildings){
            UsageResult<UsageResponse> usageResult = findByBuilding(b.getId());

            UsageWithNameResponse response = new UsageWithNameResponse();
            response.setName(b.getName());
            response.setUsagesList(usageResult.getResult());
            result.add(response);
        }

        return result;
    }

    /**
     * 월별 단위 가스 사용요금
     */
    public UsageResult<FeeResponse> findFee() {
        return getUsageFeeResult(gasFeeRepository.findAll(Sort.by("recordedAt").ascending()));
    }
}
