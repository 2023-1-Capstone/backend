package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Carbon;
import com.capstone.carbonlive.repository.CarbonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarbonServiceImpl implements CarbonService{
    private final CarbonRepository carbonRepository;

    @Override
    public UsageResult<CarbonYearResponse> getYearsUsages() {
        UsageResult<CarbonYearResponse> result = new UsageResult<>(new ArrayList<>());
        List<Carbon> carbonList = carbonRepository.findAll(Sort.by("recordedAt").ascending());
        int year = -1, usages = 0;
        for (Carbon c : carbonList){
            if (c.getRecordedAt().getYear() != year){
                CarbonYearResponse carbonYearResponse = new CarbonYearResponse(year, usages);
                result.add(carbonYearResponse);
                year = c.getRecordedAt().getYear();
                usages = c.getUsages();
            }
            else{
                usages += c.getUsages();
            }
        }
        if (year > 0){
            CarbonYearResponse carbonYearResponse = new CarbonYearResponse(year, usages);
            result.add(carbonYearResponse);
        }
        result.getResult().remove(0); // 최초 year = -1 인 부분 삭제.

        return result;
    }
}
