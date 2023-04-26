package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GasServiceTest {

    @Autowired BuildingRepository buildingRepository;
    @Autowired GasRepository gasRepository;
    @Autowired GasService gasService;

    @BeforeEach
    public void beforeEach() {
        Building building = Building.builder()
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);

        for (int i = 1; i < 13; i++) {
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2022, i, 01))
                    .usages(i)
                    .building(building)
                    .build());
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2021, i, 01))
                    .usages(2 * i)
                    .building(building)
                    .build());
        }
    }

    @Test
    @DisplayName("건물별 가스 정보 출력")
    public void findByBuilding() throws Exception {
        //when
        Building findBuilding = buildingRepository.findByName("building1");
        UsageResult<UsageResponse> result = gasService.findByBuilding(findBuilding.getId());

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getYear()).isEqualTo(2021);
        assertThat(result.getResult().get(0).getUsages().length).isEqualTo(12);
    }

    @Test
    @DisplayName("계절별 가스 사용량 출력")
    public void findBySeason() throws Exception {
        //when
        UsageResult<SeasonResponse> result = gasService.findBySeason();

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getUsages()[0]).isEqualTo(24);
        assertThat(result.getResult().get(1).getUsages()[3]).isEqualTo(0);
    }
}