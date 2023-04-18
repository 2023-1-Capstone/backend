package com.capstone.carbonlive.service;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.GasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GasService {

    private final BuildingRepository buildingRepository;
    private final GasRepository gasRepository;

    public List<UsageResponse> findByBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(
                () -> new RuntimeException());

        List<Gas> result = gasRepository.findByBuildingOrderByRecordedAtAsc(building);

        return getUsageResponses(result);
    }

    private List<UsageResponse> getUsageResponses(List<Gas> result) {
        List<Integer> years = new ArrayList();
        for (Gas gas : result) {
            int year = gas.getRecordedAt().getYear();
            if (!years.contains(year)) {
                years.add(year);
            }
        }

        List<UsageResponse> collect = new ArrayList<>();
        for (int year : years) {
            UsageResponse usageResponse = new UsageResponse(String.valueOf(year));
            for (int i = 0; i < result.size(); i++) {
                if (year == result.get(i).getRecordedAt().getYear()) {
                    usageResponse.getUsages()[i] = result.get(i).getUsages();
                }
            }
            collect.add(usageResponse);
        }

        return collect;
    }
}
