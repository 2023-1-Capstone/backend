package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.getBuildingUsageResult;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GasService {

    private final BuildingRepository buildingRepository;
    private final GasRepository gasRepository;

    /**
     * 건물별 가스 사용량
     */
    public UsageResult<UsageResponse> findByBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(
                () -> new RuntimeException());

        List<Gas> gasList = gasRepository.findByBuildingOrderByRecordedAtAsc(building);

        return getBuildingUsageResult(gasList);
    }

//    /**
//     * 계절별 가스 사용량
//     */
//    public UsageResult<SeasonResponse> findSeasonData() {
//        List<Gas> gasList = gasRepository.findAll(Sort.by("recordedAt").ascending());
//
//        //getUsageResult
//
//        return null;
//    }
}
