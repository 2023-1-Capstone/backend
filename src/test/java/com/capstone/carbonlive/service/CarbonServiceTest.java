package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.UsageWithNameResponse;
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
        Carbon predictionCarbon = Carbon.builder()
                .recordedAt(LocalDate.of(2018, 2, 1))
                .prediction(10023)
                .building(sampleBuilding)
                .build();
        carbonList.add(predictionCarbon);

        carbonRepository.saveAll(carbonList);
    }

    @Test
    @DisplayName("년도별 월 탄소 총 배출량")
    void getYearsUsages() {
        UsageResult<CarbonYearResponse> yearsUsagesResult = carbonService.getYearsUsages();
        List<CarbonYearResponse> yearUsages = yearsUsagesResult.getResult();

        System.out.println("yearUsages = " + yearUsages);

        assertThat(yearUsages.size()).isEqualTo(2);
        assertThat(yearUsages.get(0).getYear()).isEqualTo(2017);
        assertThat(yearUsages.get(1).getYear()).isEqualTo(2019);
        assertThat(yearUsages.get(0).getUsages()[6]).isEqualTo(27);
        IntStream.range(2, 8).forEach(i ->
                assertThat(yearUsages.get(1).getUsages()[i]).isEqualTo(51 + i)
        );
    }

    @Test
    @DisplayName("건물별 탄소 배출량")
   public void getBuildingUsages() {
        //when
        Building findBuilding = buildingRepository.findByName("본관");
        UsageResult<UsageResponse> result = carbonService.getBuildingUsages(findBuilding.getId());
        List<UsageResponse> buildingUsages = result.getResult();

        //then
        System.out.println("buildingUsages = " + buildingUsages);
        assertThat(buildingUsages.get(0).getYear()).isEqualTo(2017);
        assertThat(buildingUsages.get(0).getUsages()[0].getData()).isEqualTo(null);
        assertThat(buildingUsages.get(0).getUsages()[6].getData()).isEqualTo(27);
        assertThat(buildingUsages.get(1).getYear()).isEqualTo(2018);
        assertThat(buildingUsages.get(1).getUsages()[0].getData()).isEqualTo(null);
        assertThat(buildingUsages.get(1).getUsages()[1].getPrediction()).isEqualTo(10023);
    }

    @Test
    @DisplayName("전체 탄소 배출량")
    public void getAllUsages(){
        // when
        UsageResult<UsageWithNameResponse> usageResult = carbonService.getAllUsages();
        List<UsageWithNameResponse> result = usageResult.getResult();

        //then
        System.out.println("result = " + result);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("본관");

        IntStream.range(2, 8).forEach(i ->
                assertThat(result.get(0).getUsagesList().get(2).getUsages()[i].getData()).isEqualTo(50 + i + 1));
        IntStream.range(0, 8).forEach(i ->
                assertThat(result.get(0).getUsagesList().get(0).getUsages()[i].getPrediction()).isEqualTo(null));
        assertThat(result.get(0).getUsagesList().get(1).getUsages()[1].getPrediction()).isEqualTo(10023);
    }
}