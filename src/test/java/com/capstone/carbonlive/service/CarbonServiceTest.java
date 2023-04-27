package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Carbon;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.CarbonRepository;
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
class CarbonServiceTest {
    @Autowired
    private CarbonService carbonService;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private CarbonRepository carbonRepository;

    @BeforeEach
    void setUp() {
        Building sampleBuilding = Building.builder()
                .name("본관")
                .elecArea(new BigDecimal("10"))
                .gasArea(new BigDecimal("10"))
                .build();
        buildingRepository.save(sampleBuilding);

        List<Carbon> carbonList = new ArrayList<>();
        IntStream.range(3, 9).forEach(i -> {
            Carbon carbon = Carbon.builder()
                    .building(sampleBuilding)
                    .usages(50 + i)
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .build();
            carbonList.add(carbon);
        });
        Carbon carbon = Carbon.builder()
                .usages(27)
                .building(sampleBuilding)
                .recordedAt(LocalDate.of(2017, 7, 1))
                .build();
        carbonList.add(carbon);

        carbonRepository.saveAll(carbonList);
    }

    @Test
    void getYearsUsages() {
        UsageResult<CarbonYearResponse> yearsUsagesResult = carbonService.getYearsUsages();
        List<CarbonYearResponse> yearUsages = yearsUsagesResult.getResult();

        System.out.println("yearUsages = " + yearUsages);

        assertThat(yearUsages.get(0).getYear()).isEqualTo(2017);
        assertThat(yearUsages.get(0).getUsages()).isEqualTo(27);
        assertThat(yearUsages.get(1).getYear()).isEqualTo(2019);
        assertThat(yearUsages.get(1).getUsages()).isEqualTo(333);
    }

    @Test
    @DisplayName("건물별 탄소 배출량")
   public void getBuildingUsages() throws Exception {
        //when
        Building findBuilding = buildingRepository.findByName("본관");
        UsageResult<UsageResponse> result = carbonService.getBuildingUsages(findBuilding.getId());
        List<UsageResponse> buildingUsages = result.getResult();

        //then
        System.out.println("buildingUsages = " + buildingUsages);
        assertThat(buildingUsages.get(0).getYear()).isEqualTo(2017);
        assertThat(buildingUsages.get(0).getUsages()[0]).isEqualTo(0);
        assertThat(buildingUsages.get(0).getUsages()[6]).isEqualTo(27);
        assertThat(buildingUsages.get(1).getYear()).isEqualTo(2019);
        assertThat(buildingUsages.get(1).getUsages()[0]).isEqualTo(0);
        assertThat(buildingUsages.get(1).getUsages()[6]).isEqualTo(57);
    }
}