package com.capstone.carbonlive.service.common;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsagePredictionResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.BaseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class GetUsageResult {
    public static <T extends BaseEntity> UsageResult<UsageResponse> getBuildingUsageResult(List<T> ascDataList) {
        UsageResult<UsageResponse> result = new UsageResult<>(new ArrayList<>());

        int year = -1;
        UsagePredictionResponse[] usages = new UsagePredictionResponse[12];

        for (T data : ascDataList){
            int curYear = data.getRecordedAt().getYear();
            if (curYear > year){
                if (year > 0) {
                    insertUsageResponse(result, year, usages);
                }
                year = curYear;
                IntStream.range(0, 12).forEach(i ->
                        usages[i] = UsagePredictionResponse.builder()
                        .build());
            }

            int eMonth = data.getRecordedAt().getMonth().getValue() - 1;
            if (usages[eMonth].getData() == null) {
                usages[eMonth].setData(data.getUsages());
                usages[eMonth].setPrediction(data.getPrediction());
            }
        }
        if (year > 0){
            insertUsageResponse(result, year, usages);
        }

        return result;
    }

    private static void insertUsageResponse(UsageResult<UsageResponse> result, int year, UsagePredictionResponse[] usages) {
        UsageResponse usageResponse = new UsageResponse();
        usageResponse.setYear(year);
        for (int i = 0; i < usageResponse.getUsages().length; i++)
            usageResponse.getUsages()[i] = usages[i];
        result.getResult().add(usageResponse);
    }

    public static <T extends BaseEntity> UsageResult<SeasonResponse> getSeasonUsageResult(List<T> ascDataList) {
        UsageResult<SeasonResponse> result = new UsageResult<>(new ArrayList<>());

        List<Integer> yearList = new ArrayList<>();
        for (T data : ascDataList) {
            int curYear = data.getRecordedAt().getYear();
            if (!yearList.contains(curYear))
                yearList.add(curYear);
        }

        UsagePredictionResponse[] usages = new UsagePredictionResponse[4];
        boolean[] isPrediction = {false, false, false, false};
        for (Integer year : yearList) {
            insertSeasonResponse(ascDataList, result, isPrediction, year);

            Arrays.fill(isPrediction, false);  //초기화
        }

        return result;
    }

    private static <T extends BaseEntity> void insertSeasonResponse(
            List<T> ascDataList, UsageResult<SeasonResponse> result, boolean[] isPrediction, Integer year) {

        SeasonResponse seasonResponse = new SeasonResponse(year.intValue());

        for (T data : ascDataList) {
            if (data.getRecordedAt().getYear() == year.intValue()) {
                int month = data.getRecordedAt().getMonth().getValue();
                switch (month / 3) {
                    case 1:     //3,4,5월
                        if (data.isPrediction()) isPrediction[0] = true;
                        else seasonResponse.getUsages()[0] += data.getUsages();
                        break;
                    case 2:     //6,7,8월
                        if (data.isPrediction()) isPrediction[1] = true;
                        else seasonResponse.getUsages()[1] += data.getUsages();
                        break;
                    case 3:     //9,10,11월
                        if (data.isPrediction()) isPrediction[2] = true;
                        else seasonResponse.getUsages()[2] += data.getUsages();
                        break;
                    case 4:    //12월
                        if (data.isPrediction()) isPrediction[3] = true;
                        else seasonResponse.getUsages()[3] += data.getUsages();
                        break;
                }
            }
            //다음 해 1,2월
            else if (data.getRecordedAt().getYear() == seasonResponse.getEndYear()) {
                int month = data.getRecordedAt().getMonth().getValue();
                if (month == 1 || month == 2) {
                    if (data.isPrediction()) isPrediction[3] = true;
                    else seasonResponse.getUsages()[3] += data.getUsages();
                }
            }
        }
        System.out.println("seasonResponse = " + seasonResponse);
        //데이터가 하나라도 부족하면 0으로 처리
        for (int i = 0; i < 4; i++) {
            if (isPrediction[i]) {
                seasonResponse.getUsages()[i] = 0;
            }
        }

        result.getResult().add(seasonResponse);
    }
}
