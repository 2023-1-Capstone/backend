package com.capstone.carbonlive.service;

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

import static org.assertj.core.api.Assertions.*;

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

        Gas gas = Gas.builder()
                .recordedAt(LocalDate.of(2022, 2, 1))
                .usages(111)
                .building(building)
                .build();
        gasRepository.save(gas);
        Gas gas2 = Gas.builder()
                .recordedAt(LocalDate.of(2022, 12, 1))
                .usages(222)
                .building(building)
                .build();
        gasRepository.save(gas2);
        Gas gas3 = Gas.builder()
                .recordedAt(LocalDate.of(2022, 7, 1))
                .usages(333)
                .building(building)
                .build();
        gasRepository.save(gas3);
    }

    @Test
    @DisplayName("건물별 가스 정보")
    public void findByBuilding() throws Exception {
        //given, when
        UsageResult result = gasService.findByBuilding(1L);

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getYear()).isEqualTo(2022);
        assertThat(result.getResult().get(0).getUsages().length).isEqualTo(12);
    }
}