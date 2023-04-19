package com.capstone.carbonlive.service.common;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Carbon;
import com.capstone.carbonlive.entity.Electricity;
import com.capstone.carbonlive.entity.Gas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetUsageResult {
    public static UsageResult getGasUsageResult(List<Gas> result) {
        List<Integer> years = new ArrayList();
        for (Gas gas : result) {
            int year = gas.getRecordedAt().getYear();
            if (!years.contains(year)) {
                years.add(year);
            }
        }

        List<UsageResponse> collect = new ArrayList<>();
        for (int year : years) {
            UsageResponse usageResponse = new UsageResponse(year);
            for (int i = 0; i < result.size(); i++) {
                if (year == result.get(i).getRecordedAt().getYear()) {
                    int month = result.get(i).getRecordedAt().getMonthValue();
                    usageResponse.getUsages()[month - 1] = result.get(i).getUsages();
                }
            }
            collect.add(usageResponse);
        }

        return new UsageResult(collect);
    }

    public static UsageResult getElecUsageResult(List<Electricity> dataList) {
        UsageResult result = new UsageResult(new ArrayList<>());

        int year = -1;
        int[] usages = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (Electricity e : dataList){
            int curYear = e.getRecordedAt().getYear();
            if (curYear > year){
                UsageResponse usageResponse = new UsageResponse();

                usageResponse.setYear(curYear);
                for (int i = 0; i < usageResponse.getUsages().length; i++)
                    usageResponse.getUsages()[i] = usages[i];

                year = curYear;
                Arrays.fill(usages, 0);

                result.getResult().add(usageResponse);
            }
        }
        return result;
    }

    public static UsageResult getCarbonUsageResult(List<Carbon> dataList) {
        UsageResult result = new UsageResult(new ArrayList<>());

        int year = -1;
        int[] usages = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (Carbon e : dataList){
            int curYear = e.getRecordedAt().getYear();
            if (curYear > year){
                UsageResponse usageResponse = new UsageResponse();

                usageResponse.setYear(curYear);
                for (int i = 0; i < usageResponse.getUsages().length; i++)
                    usageResponse.getUsages()[i] = usages[i];

                year = curYear;
                Arrays.fill(usages, 0);

                result.getResult().add(usageResponse);
            }
        }
        return result;
    }
}
