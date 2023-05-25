package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.FeeResponse;
import com.capstone.carbonlive.dto.response.SeasonResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import com.capstone.carbonlive.entity.ElectricityFee;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.ElectricityFeeRepository;
import com.capstone.carbonlive.repository.ElectricityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private ElectricityFeeRepository electricityFeeRepository;

    private final String[] name = {"본관", "60주년기념관"};

    @BeforeEach
    void setUp(){
        for (String n : name) {
            Building building = Building.builder()
                    .name(n)
                    .elecArea(new BigDecimal("10"))
                    .gasArea(new BigDecimal("10"))
                    .build();
            buildingRepository.save(building);

            //2018-01 부터 2019-09 까지 실제값
            for (int i = 1; i < 13; i++) {
                electricityRepository.save(Electricity.builder().
                        recordedAt(LocalDate.of(2018, i, 1))
                        .usages(i)
                        .building(building)
                        .build());
            }
            for (int i = 1; i < 10; i++) {
                electricityRepository.save(Electricity.builder().
                        recordedAt(LocalDate.of(2019, i, 1))
                        .usages(i)
                        .building(building)
                        .build());
            }

            //2019-10 부터 2019-12 까지 예측값
            for (int i = 10; i < 13; i++) {
                electricityRepository.save(Electricity.builder()
                        .recordedAt(LocalDate.of(2019, i, 1))
                        .prediction(i)
                        .building(building)
                        .build());
            }
        }

        System.out.println(" = " + electricityRepository.findAll());
    }

    @Test
    @DisplayName("개별 건물 전기 사용량 값 년도/월 별 정리 확인")
    void getEachAll() {
        //when
        Building building = buildingRepository.findByName("본관");
        UsageResult<UsageResponse> result = electricityService.getEachAll(building.getId());

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getYear()).isEqualTo(2018);
        assertThat(result.getResult().get(1).getYear()).isEqualTo(2019);
        for (int i = 0; i < 12; i++) {
            assertThat(result.getResult().get(0).getUsages()[i].getData()).isEqualTo(i + 1);
            assertThat(result.getResult().get(0).getUsages()[i].getPrediction()).isEqualTo(null);
        }
        for (int i = 0; i < 9; i++) {
            assertThat(result.getResult().get(1).getUsages()[i].getData()).isEqualTo(i + 1);
            assertThat(result.getResult().get(1).getUsages()[i].getPrediction()).isEqualTo(null);
        }
        for (int i = 9; i < 12; i++) {
            assertThat(result.getResult().get(1).getUsages()[i].getData()).isEqualTo(null);
            assertThat(result.getResult().get(1).getUsages()[i].getPrediction()).isEqualTo(i + 1);
        }
    }

    @Test
    @DisplayName("계절별 전기 사용량 출력")
    public void getSeasonData() {
        //when
        UsageResult<SeasonResponse> result = electricityService.getSeasonData();

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getStartYear()).isEqualTo(2018);
        assertThat(result.getResult().get(0).getEndYear()).isEqualTo(2019);
        assertThat(result.getResult().get(0).getUsages()[0]).isEqualTo(24);
        assertThat(result.getResult().get(0).getUsages()[1]).isEqualTo(42);
        assertThat(result.getResult().get(0).getUsages()[2]).isEqualTo(60);
        assertThat(result.getResult().get(0).getUsages()[3]).isEqualTo(30);
        assertThat(result.getResult().get(1).getStartYear()).isEqualTo(2019);
        assertThat(result.getResult().get(1).getEndYear()).isEqualTo(2020);
        assertThat(result.getResult().get(1).getUsages()[2]).isEqualTo(0);
        assertThat(result.getResult().get(1).getUsages()[3]).isEqualTo(0);
    }

    @Test
    @DisplayName("전체 전기 사용량 확인")
    void getAll() {
        // when
        UsageResult<UsageWithNameResponse> usageResult = electricityService.getAll();
        List<UsageWithNameResponse> result = usageResult.getResult();

        //then
        System.out.println("result = " + result);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo(this.name[0]);
        assertThat(result.get(1).getName()).isEqualTo(this.name[1]);

        IntStream.rangeClosed(0, 11).forEach(i ->
                assertThat(result.get(1).getUsagesList().get(0).getUsages()[i].getData()).isEqualTo(i + 1));
        IntStream.rangeClosed(0, 8).forEach(i ->
                assertThat(result.get(1).getUsagesList().get(1).getUsages()[i].getPrediction()).isEqualTo(null));
        IntStream.rangeClosed(9, 11).forEach(i ->
                assertThat(result.get(1).getUsagesList().get(1).getUsages()[i].getPrediction()).isEqualTo(i + 1));
    }

    @Test
    @DisplayName("월별 단위 전기 사용요금 출력")
    public void findFee() throws Exception {
        //given
        for (int i = 1; i < 13; i++) {
            electricityFeeRepository.save(ElectricityFee.builder()
                    .recordedAt(LocalDate.of(2018, i, 1))
                    .usages(i)
                    .fee(i)
                    .build());
            electricityFeeRepository.save(ElectricityFee.builder()
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .usages(i)
                    .fee(i)
                    .build());
        }

        //when
        UsageResult<FeeResponse> result = electricityService.getFee();

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getYear()).isEqualTo(2018);
        assertThat(result.getResult().get(1).getYear()).isEqualTo(2019);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 12; j++) {
                assertThat(result.getResult().get(i).getFeeResponses()[j].getUsages()).isEqualTo(j + 1);
                assertThat(result.getResult().get(i).getFeeResponses()[j].getFee()).isEqualTo(j + 1);
            }
        }
    }
}