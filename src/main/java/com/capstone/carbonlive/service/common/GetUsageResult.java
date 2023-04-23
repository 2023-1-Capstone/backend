package com.capstone.carbonlive.service.common;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.BaseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetUsageResult {
    public static <T extends BaseEntity> UsageResult<UsageResponse> getUsageResult(List<T> ascDataList) {
        UsageResult<UsageResponse> result = new UsageResult<>(new ArrayList<>());

        int year = -1;
        int[] usages = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (T data : ascDataList){
            int curYear = data.getRecordedAt().getYear();
            if (curYear > year){
                if (year > 0) {
                    insertUsageResponse(result, year, usages);
                }
                year = curYear;
                Arrays.fill(usages, 0);
            }

            int eMonth = data.getRecordedAt().getMonth().getValue() - 1;
            usages[eMonth] = data.getUsages();
        }
        if (year > 0){
            insertUsageResponse(result, year, usages);
        }

        return result;
    }

    private static void insertUsageResponse(UsageResult<UsageResponse> result, int year, int[] usages) {
        UsageResponse usageResponse = new UsageResponse();
        usageResponse.setYear(year);
        for (int i = 0; i < usageResponse.getUsages().length; i++)
            usageResponse.getUsages()[i] = usages[i];
        result.getResult().add(usageResponse);
    }
}
