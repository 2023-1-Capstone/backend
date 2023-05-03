package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.ElectricityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ElectricityServiceTest {
    @Autowired
    private ElectricityService electricityService;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private ElectricityRepository electricityRepository;

    private final String[] name = {"본관", "60주년기념관"};
    private final int[] expectYear = {2018, 2019};
    private final int[][] expectUsages = { {0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 0, 0}, {0, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0} };
    private final boolean[][] expectPrediction = {
            {false, false, false, false, false, true, false, true, false, true, false, false},
            {false, true, false, true, false, false, false, false, false, false, false, false}
    };

    @BeforeEach
    void setUp(){
        for (String n : name) {
            Building sampleBuilding = Building.builder()
                    .name(n)
                    .elecArea(new BigDecimal("10"))
                    .gasArea(new BigDecimal("10"))
                    .build();
            buildingRepository.save(sampleBuilding);

            List<Electricity> tempList = new ArrayList<>();
            IntStream.rangeClosed(2, 10).forEach(i -> {
                int year = i > 5 ? 2018 : 2019;
                boolean sw = (i % 2 == 0);
                Electricity e = Electricity.builder()
                        .recordedAt(LocalDate.of(year, i, 1))
                        .building(sampleBuilding)
                        .usages(i)
                        .prediction(sw)
                        .build();
                tempList.add(e);
            });

            if (n.equals("본관")) {
                tempList.add(Electricity.builder()
                        .usages(1420)
                        .recordedAt(LocalDate.of(2018, 7, 1))
                        .prediction(true)
                        .building(sampleBuilding)
                        .build());
            } // 이 데이터는 포함되면 안된다.
            electricityRepository.saveAll(tempList);
        }
    }

    @Test
    @DisplayName("개별 건물 전기 사용량 값 년도/월 별 정리 확인")
    void getEachAll() {
        Building building = buildingRepository.findByName("본관");
        UsageResult<UsageResponse> usageResult = electricityService.getEachAll(building.getId());
        System.out.println("usageResult = " + usageResult);

        for (int i = 0; i < usageResult.getResult().size(); i++){
            UsageResponse curUsageResponse = usageResult.getResult().get(i);
            int curYear = curUsageResponse.getYear();
            UsagePredictionResponse[] curUsages = curUsageResponse.getUsages();

            assertThat(curYear).isEqualTo(expectYear[i]);

            for (int j = 0; j < 12; j++){
                assertThat(curUsages[j].getData()).isEqualTo(expectUsages[i][j]);
                assertThat(curUsages[j].isPrediction()).isEqualTo(expectPrediction[i][j]);
            }
        }
    }

    @Test
    @DisplayName("계절별 전기 사용량 출력")
    public void getSeasonData() {
        //when
        UsageResult<SeasonResponse> result = electricityService.getSeasonData();

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getUsages()[0]).isEqualTo(0);
        assertThat(result.getResult().get(0).getUsages()[3]).isEqualTo(0);
        assertThat(result.getResult().get(1).getUsages()[0]).isEqualTo(0);
        assertThat(result.getResult().get(1).getUsages()[2]).isEqualTo(0);
    }

    @Test
    void getAll() {
        // when
        UsageResult<UsageWithNameResponse> usageResult = electricityService.getAll();
        List<UsageWithNameResponse> result = usageResult.getResult();

        System.out.println("result = " + result);
        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo(this.name[0]);
        assertThat(result.get(1).getName()).isEqualTo(this.name[1]);

        IntStream.rangeClosed(0, 11).forEach(i -> assertThat(result.get(1).getUsagesList().get(0).getUsages()[i].getData()).isEqualTo(expectUsages[0][i]));
        IntStream.rangeClosed(0, 11).forEach(i -> assertThat(result.get(1).getUsagesList().get(0).getUsages()[i].isPrediction()).isEqualTo(expectPrediction[0][i]));
    }
}