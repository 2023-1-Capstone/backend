package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.FeeResponse;
import com.capstone.carbonlive.dto.response.SeasonResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.entity.GasFee;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasFeeRepository;
import com.capstone.carbonlive.repository.GasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GasServiceTest {

    @Autowired BuildingRepository buildingRepository;
    @Autowired GasRepository gasRepository;
    @Autowired GasService gasService;
    @Autowired GasFeeRepository gasFeeRepository;

    @BeforeEach
    public void beforeEach() {
        Building building = Building.builder()
                .name("building1")
                .gasArea(new BigDecimal("1111.11"))
                .elecArea(new BigDecimal("2222.22"))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);

        //2018-01 부터 2019-09 까지 실제값
        for (int i = 1; i < 13; i++) {
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2018, i, 1))
                    .usages(i)
                    .building(building)
                    .build());
        }
        for (int i = 1; i < 10; i++) {
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2019, i, 1))
                    .usages(i)
                    .building(building)
                    .build());
        }

        //2019-10 부터 2019-12 까지 예측값
        for (int i = 10; i < 13; i++) {
            gasRepository.save(Gas.builder()
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .prediction(i)
                    .building(building)
                    .build());
        }
    }

    @Test
    @DisplayName("건물별 가스 정보 출력")
    public void findByBuilding() {
        //when
        Building findBuilding = buildingRepository.findByName("building1");
        UsageResult<UsageResponse> result = gasService.findByBuilding(findBuilding.getId());

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
    @DisplayName("계절별 가스 사용량 출력")
    public void findBySeason() {
        //when
        UsageResult<SeasonResponse> result = gasService.findBySeason();

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getStartYear()).isEqualTo(2018);
        assertThat(result.getResult().get(0).getEndYear()).isEqualTo(2019);
        assertThat(result.getResult().get(0).getUsages()[0]).isEqualTo(12);
        assertThat(result.getResult().get(0).getUsages()[1]).isEqualTo(21);
        assertThat(result.getResult().get(0).getUsages()[2]).isEqualTo(30);
        assertThat(result.getResult().get(0).getUsages()[3]).isEqualTo(15);
        assertThat(result.getResult().get(1).getStartYear()).isEqualTo(2019);
        assertThat(result.getResult().get(1).getEndYear()).isEqualTo(2020);
        assertThat(result.getResult().get(1).getUsages()[2]).isEqualTo(0);
        assertThat(result.getResult().get(1).getUsages()[3]).isEqualTo(0);
    }

    @Test
    @DisplayName("전체 가스 사용량 출력")
    public void findAll(){
        // when
        UsageResult<UsageWithNameResponse> usageResult = gasService.findAll();
        List<UsageWithNameResponse> result = usageResult.getResult();

        // then
        System.out.println("result = " + result);
        assertThat(result.get(0).getName()).isEqualTo("building1");
        assertThat(result.get(0).getUsagesList().get(0).getYear()).isEqualTo(2018);
        assertThat(result.get(0).getUsagesList().get(1).getYear()).isEqualTo(2019);
        for (int i = 0; i < 12; i++) {
            assertThat(result.get(0).getUsagesList().get(0).getUsages()[i].getData()).isEqualTo(i + 1);
            assertThat(result.get(0).getUsagesList().get(0).getUsages()[i].getPrediction()).isEqualTo(null);
        }
        for (int i = 0; i < 9; i++) {
            assertThat(result.get(0).getUsagesList().get(1).getUsages()[i].getData()).isEqualTo(i + 1);
            assertThat(result.get(0).getUsagesList().get(1).getUsages()[i].getPrediction()).isEqualTo(null);
        }
        for (int i = 9; i < 12; i++) {
            assertThat(result.get(0).getUsagesList().get(1).getUsages()[i].getData()).isEqualTo(null);
            assertThat(result.get(0).getUsagesList().get(1).getUsages()[i].getPrediction()).isEqualTo(i + 1);
        }
    }

    @Test
    @DisplayName("월별 단위 가스 사용요금 출력")
    public void findFee() throws Exception {
        //given
        for (int i = 1; i < 13; i++) {
            gasFeeRepository.save(GasFee.builder()
                    .recordedAt(LocalDate.of(2018, i, 1))
                    .usages(i)
                    .fee(i)
                    .build());
            gasFeeRepository.save(GasFee.builder()
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .usages(i)
                    .fee(i)
                    .build());
        }

        //when
        UsageResult<FeeResponse> result = gasService.findFee();

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