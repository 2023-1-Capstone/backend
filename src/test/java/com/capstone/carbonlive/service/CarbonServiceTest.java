package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.response.CarbonYearResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Carbon;
import com.capstone.carbonlive.entity.EntireCarbon;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.CarbonRepository;
import com.capstone.carbonlive.repository.EntireCarbonRepository;
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

    @Autowired private CarbonService carbonService;
    @Autowired private BuildingRepository buildingRepository;
    @Autowired private CarbonRepository carbonRepository;
    @Autowired private EntireCarbonRepository entireCarbonRepository;

    @BeforeEach
    void setUp() {
        Building sampleBuilding1 = Building.builder()
                .name("본관")
                .elecArea(new BigDecimal("10"))
                .gasArea(new BigDecimal("10"))
                .build();
        Building sampleBuilding2 = sampleBuilding1.toBuilder()
                .name("힘들다")
                .build();
        buildingRepository.save(sampleBuilding1);
        buildingRepository.save(sampleBuilding2);

        List<Carbon> carbonList = new ArrayList<>();
        IntStream.range(3, 9).forEach(i -> {
            Carbon carbon1 = Carbon.builder()
                    .building(sampleBuilding1)
                    .usages(50 + i)
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .build();
            Carbon carbon2 = carbon1.toBuilder()
                    .building(sampleBuilding2)
                    .build();
            carbonList.add(carbon1);
            carbonList.add(carbon2);
        });
        Carbon carbon = Carbon.builder()
                .usages(27)
                .building(sampleBuilding1)
                .recordedAt(LocalDate.of(2017, 7, 1))
                .build();
        carbonList.add(carbon);
        Carbon predictionCarbon = Carbon.builder()
                .recordedAt(LocalDate.of(2018, 2, 1))
                .prediction(10023)
                .building(sampleBuilding1)
                .build();
        carbonList.add(predictionCarbon);

        carbonRepository.saveAll(carbonList);

        for (int i = 1; i < 13; i++) {
            entireCarbonRepository.save(EntireCarbon.builder()
                    .recordedAt(LocalDate.of(2017, i,1))
                    .usages(i).build());
            entireCarbonRepository.save(EntireCarbon.builder()
                    .recordedAt(LocalDate.of(2018, i,1))
                    .usages(i)
                    .prediction(i * 2).build());
        }
    }

    @Test
    @DisplayName("년도별 월 탄소 총 배출량")
    void getYearsUsages() {
        UsageResult<CarbonYearResponse> yearsUsagesResult = carbonService.getYearsUsages();
        List<CarbonYearResponse> yearUsages = yearsUsagesResult.getResult();

        System.out.println("yearUsages = " + yearUsages);

        assertThat(yearUsages.size()).isEqualTo(2);
        assertThat(yearUsages.get(0).getYear()).isEqualTo(2017);
        IntStream.range(0, 12).forEach(i ->
                assertThat(yearUsages.get(0).getUsages()[i].getData()).isEqualTo(i + 1)
        );
        assertThat(yearUsages.get(1).getYear()).isEqualTo(2018);
        IntStream.range(0, 12).forEach(i ->
                assertThat(yearUsages.get(1).getUsages()[i].getData()).isEqualTo(i + 1)
        );
        IntStream.range(0, 12).forEach(i ->
                assertThat(yearUsages.get(1).getUsages()[i].getPrediction()).isEqualTo(2 * (i + 1))
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
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("본관");
        assertThat(result.get(1).getName()).isEqualTo("힘들다");

        IntStream.range(2, 8).forEach(i ->
                assertThat(result.get(0).getUsagesList().get(2).getUsages()[i].getData()).isEqualTo(50 + i + 1));
        IntStream.range(0, 8).forEach(i ->
                assertThat(result.get(0).getUsagesList().get(0).getUsages()[i].getPrediction()).isEqualTo(null));
        assertThat(result.get(0).getUsagesList().get(1).getUsages()[1].getPrediction()).isEqualTo(10023);
    }
}