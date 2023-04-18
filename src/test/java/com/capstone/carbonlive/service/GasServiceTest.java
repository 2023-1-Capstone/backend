package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import com.capstone.carbonlive.utils.LocalDateToYear;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GasServiceTest {

    @Autowired BuildingRepository buildingRepository;
    @Autowired GasRepository gasRepository;
    @Autowired GasService gasService;

    @Test
    @DisplayName("recordedAt 에서 year 추출")
    public void LocalDateToYear() throws Exception {
        //given,when
        String year = LocalDateToYear.getYear(LocalDate.now());

        //then
        assertThat(year).isEqualTo("2023");
    }

    @Test
    @DisplayName("건물별 가스 정보")
    public void findByBuilding() throws Exception {
        //given
        Building building = Building.builder()
                .id(1L)
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);

        Gas gas = Gas.builder()
                .id(1L)
                .recordedAt(LocalDate.now())
                .usages(111)
                .building(building)
                .build();
        gasRepository.save(gas);

        //when
        List<UsageResponse> findGas = gasService.findByBuilding(building.getId());

        //then
        assertThat(findGas.size()).isEqualTo(1);
        assertThat(findGas.get(0).getYear()).isEqualTo("2023");
        assertThat(findGas.get(0).getUsages()).isEqualTo(111);
    }

}