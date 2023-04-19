package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.ElectricityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectricityServiceImpl implements ElectricityService {
    private final ElectricityRepository electricityRepository;
    private final BuildingRepository buildingRepository;

    @Override
    @Transactional
    public UsageResult getEachAll(Long id) {
        Building building = buildingRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        List<Electricity> electricityList = electricityRepository.findAllByBuilding(building,
                Sort.by("recordedAt").ascending());

        return getUsageResult(electricityList);
    }

    private static UsageResult getUsageResult(List<Electricity> electricityList) {
        UsageResult result = new UsageResult(new ArrayList<>());

        int year = -1;
        int[] usages = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (Electricity e : electricityList){
            int curYear = e.getRecordedAt().getYear();
            if (curYear > year){
                if (year > 0) {
                    insertUsageResponse(result, year, usages);
                }

                year = curYear;
                Arrays.fill(usages, 0);
            }

            int eMonth = e.getRecordedAt().getMonth().getValue() - 1;
            usages[eMonth] = e.getUsages();
        }
        if (year > 0){
            insertUsageResponse(result, year, usages);
        }

        return result;
    }

    private static void insertUsageResponse(UsageResult result, int year, int[] usages) {
        UsageResponse usageResponse = new UsageResponse();

        usageResponse.setYear(String.valueOf(year));
        for (int i = 0; i < usageResponse.getUsages().length; i++)
            usageResponse.getUsages()[i] = usages[i];

        result.getResult().add(usageResponse);
    }
}