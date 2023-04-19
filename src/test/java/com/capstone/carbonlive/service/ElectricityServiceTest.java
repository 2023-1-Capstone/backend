package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
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

    @BeforeEach
    void setUp(){
        Building sampleBuilding = Building.builder()
                .name("본관")
                .elecArea(new BigDecimal("10"))
                .gasArea(new BigDecimal("10"))
                .build();
        buildingRepository.save(sampleBuilding);

        List<Electricity> tempList = new ArrayList<>();
        IntStream.rangeClosed(2, 10).forEach(i -> {
            int year = i > 5 ? 2018 : 2019;
            Electricity e = Electricity.builder()
                    .recordedAt(LocalDate.of(year, i, 1))
                    .building(sampleBuilding)
                    .usages(i)
                    .build();
            tempList.add(e);
        });
        electricityRepository.saveAll(tempList);
    }

    @Test
    @DisplayName("개별 건물 전기 사용량 값 년도/월 별 정리 확인")
    void getEachAll() {
        UsageResult usageResult = electricityService.getEachAll(1L);
        System.out.println("usageResult = " + usageResult);

        String[] expectYear = {"2018", "2019"};
        int[][] expectUsages = { {0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 0, 0}, {0, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0} };
        for (int i = 0; i < usageResult.getResult().size(); i++){


            UsageResponse curUsageResponse = usageResult.getResult().get(i);
            String curYear = curUsageResponse.getYear();
            int[] curUsages = curUsageResponse.getUsages();

            assertThat(curYear).isEqualTo(expectYear[i]);

            for (int j = 0; j < 12; j++){
                assertThat(curUsages[j]).isEqualTo(expectUsages[i][j]);
            }
        }

    }
}