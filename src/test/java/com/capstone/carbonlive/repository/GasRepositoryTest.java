package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import org.assertj.core.api.Assertions;
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
class GasRepositoryTest {

    @Autowired GasRepository gasRepository;
    @Autowired BuildingRepository buildingRepository;

    @Test
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
        Building findBuilding = buildingRepository.findById(1L).orElseThrow(() -> new RuntimeException());
        List<Gas> findGas = gasRepository.findByBuilding(findBuilding);

        //then
        assertThat(findGas.size()).isEqualTo(1);
        assertThat(findGas.get(0).getBuilding()).isEqualTo(findBuilding);
    }
}