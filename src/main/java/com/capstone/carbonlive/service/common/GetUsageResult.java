package com.capstone.carbonlive.service.common;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.BaseEntity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetUsageResult {
    public static <T extends BaseEntity> UsageResult<UsageResponse> getBuildingUsageResult(List<T> ascDataList) {
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

    public static <T extends BaseEntity> UsageResult<SeasonResponse> getSeasonUsageResult(List<T> ascDataList) {
        UsageResult<SeasonResponse> result = new UsageResult<>(new ArrayList<>());

        List<Integer> yearList = new ArrayList<>();
        for (T data : ascDataList) {
            int curYear = data.getRecordedAt().getYear();
            if (!yearList.contains(curYear))
                yearList.add(curYear);
        }


        int[] count = {0, 0, 0, 0};
        for (Integer year : yearList) {
            insertSeasonResponse(ascDataList, result, count, year);

            Arrays.fill(count, 0);  //초기화
        }

        return result;
    }

    private static <T extends BaseEntity> void insertSeasonResponse(List<T> ascDataList, UsageResult<SeasonResponse> result, int[] count, Integer year) {
        SeasonResponse seasonResponse = new SeasonResponse(year.intValue());

        for (T data : ascDataList) {
            if (data.getRecordedAt().getYear() == year.intValue()) {
                int month = data.getRecordedAt().getMonth().getValue();
                switch (month / 3) {
                    case 1:     //3,4,5월
                        seasonResponse.getUsages()[0] += data.getUsages();
                        count[0]++;
                        break;
                    case 2:     //6,7,8월
                        seasonResponse.getUsages()[1] += data.getUsages();
                        count[1]++;
                        break;
                    case 3:     //9,10,11월
                        seasonResponse.getUsages()[2] += data.getUsages();
                        count[2]++;
                        break;
                    case 4:    //12월
                        seasonResponse.getUsages()[3] += data.getUsages();
                        count[3]++;
                        break;
                }
            }
            //다음 해 1,2월
            else if (data.getRecordedAt().getYear() == seasonResponse.getEndYear()) {
                int month = data.getRecordedAt().getMonth().getValue();
                if (month == 1 || month == 2) {
                    seasonResponse.getUsages()[3] += data.getUsages();
                    count[3]++;
                }
            }
        }
        //데이터가 하나라도 부족하면 0으로 처리
        for (int i = 0; i < 4; i++) {
            if (count[i] < 3) {
                seasonResponse.getUsages()[i] = 0;
            }
        }

        result.getResult().add(seasonResponse);
    }
}
