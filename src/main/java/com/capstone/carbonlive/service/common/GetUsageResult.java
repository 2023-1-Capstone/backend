package com.capstone.carbonlive.service.common;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.*;
import com.capstone.carbonlive.entity.BaseEntity;
import com.capstone.carbonlive.entity.FeeBaseEntity;
import com.capstone.carbonlive.entity.Water;

import java.util.ArrayList;
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

    public static UsageResult<WaterResponse> getWaterResult(List<Water> ascDataList) {
        UsageResult<WaterResponse> result = new UsageResult<>(new ArrayList<>());

        List<Integer> yearList = new ArrayList<>();
        for (Water data : ascDataList) {
            int curYear = data.getRecordedAt().getYear();
            if (!yearList.contains(curYear))
                yearList.add(curYear);
        }

        for (int year : yearList) {
            WaterPredictionFeeResponse[] usages = new WaterPredictionFeeResponse[12];

            for (Water data : ascDataList) {
                if (data.getRecordedAt().getYear() == year) {
                    WaterPredictionFeeResponse feeResponse = WaterPredictionFeeResponse.builder()
                            .data(data.getUsages())
                            .prediction(data.getPrediction())
                            .fee(data.getFee())
                            .fee_prediction(data.getFee_prediction())
                            .build();

                    int month = data.getRecordedAt().getMonth().getValue();
                    usages[month - 1] = feeResponse;
                }
            }

            result.add(new WaterResponse(year, usages));
        }

        return result;
    }

    public static <T extends FeeBaseEntity> UsageResult<FeeResponse> getUsageFeeResult(List<T> ascDataList) {
        UsageResult<FeeResponse> result = new UsageResult<>(new ArrayList<>());

        List<Integer> yearList = new ArrayList<>();
        for (T data : ascDataList) {
            int curYear = data.getRecordedAt().getYear();
            if (!yearList.contains(curYear))
                yearList.add(curYear);
        }

        for (Integer year : yearList) {
            UsageFeeResponse[] usages = new UsageFeeResponse[12];

            for (T data : ascDataList) {
                if (data.getRecordedAt().getYear() == year) {
                    UsageFeeResponse feeResponse = UsageFeeResponse.builder()
                            .usages(data.getUsages())
                            .fee(data.getFee())
                            .prediction(data.getPrediction())
                            .fee_prediction(data.getFee_prediction())
                            .build();

                    int month = data.getRecordedAt().getMonth().getValue();
                    usages[month - 1] = feeResponse;
                }
            }

            result.add(new FeeResponse(year, usages));
        }

        return result;
    }

    public static <T extends FeeBaseEntity> UsageResult<SeasonResponse> getSeasonUsageResult(List<T> ascDataList) {
        UsageResult<SeasonResponse> result = new UsageResult<>(new ArrayList<>());

        List<Integer> yearList = new ArrayList<>();
        for (T data : ascDataList) {
            int curYear = data.getRecordedAt().getYear();
            if (!yearList.contains(curYear))
                yearList.add(curYear);
        }

        for (Integer year : yearList) {
            insertSeasonResponse(ascDataList, result, year);
        }

        return result;
    }

    private static <T extends FeeBaseEntity> void insertSeasonResponse(
            List<T> ascDataList, UsageResult<SeasonResponse> result, Integer year) {

        SeasonResponse seasonResponse = new SeasonResponse(year.intValue());

        int[] count = {0, 0, 0, 0};
        for (T data : ascDataList) {
            if (data.getRecordedAt().getYear() == year.intValue()) {
                int month = data.getRecordedAt().getMonth().getValue();
                switch (month / 3) {
                    case 1:     //3,4,5월
                        if (data.getUsages() != null) {
                            seasonResponse.getUsages()[0] += data.getUsages();
                            count[0]++;
                        }
                        break;
                    case 2:     //6,7,8월
                        if (data.getUsages() != null) {
                            seasonResponse.getUsages()[1] += data.getUsages();
                            count[1]++;
                        }
                        break;
                    case 3:     //9,10,11월
                        if (data.getUsages() != null) {
                            seasonResponse.getUsages()[2] += data.getUsages();
                            count[2]++;
                        }
                        break;
                    case 4:    //12월
                        if (data.getUsages() != null) {
                            seasonResponse.getUsages()[3] += data.getUsages();
                            count[3]++;
                        }
                        break;
                }
            }
            //다음 해 1,2월
            else if (data.getRecordedAt().getYear() == seasonResponse.getEndYear()) {
                int month = data.getRecordedAt().getMonth().getValue();
                if (month == 1 || month == 2) {
                    if (data.getUsages() != null) {
                        seasonResponse.getUsages()[3] += data.getUsages();
                        count[3]++;
                    }
                }
            }
        }
        //데이터가 하나라도 부족하면 0으로 처리
        for (int i = 0; i < 4; i++) {
            if (count[i] != 3) {
                seasonResponse.getUsages()[i] = 0;
            }
        }

        result.getResult().add(seasonResponse);
    }
}
