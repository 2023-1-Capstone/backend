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
    public void findByBuildingOrderByRecordedAtDesc() throws Exception {
        //given
        Building building = Building.builder()
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);

        Building building2 = Building.builder()
                .name("building2")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building2);

        Gas gas = Gas.builder()
                .recordedAt(LocalDate.of(2022, 2, 2))
                .usages(111)
                .building(building)
                .build();
        gasRepository.save(gas);
        Gas gas2 = Gas.builder()
                .recordedAt(LocalDate.of(2022, 5, 3))
                .usages(222)
                .building(building)
                .build();
        gasRepository.save(gas2);
        Gas gas3 = Gas.builder()
                .recordedAt(LocalDate.of(2021, 3, 3))
                .usages(333)
                .building(building2)
                .build();
        gasRepository.save(gas3);

        //when
        Building findBuilding = buildingRepository.findById(1L).orElseThrow(() -> new RuntimeException());
        List<Gas> result = gasRepository.findByBuildingOrderByRecordedAtAsc(findBuilding);

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(gas);
        assertThat(result.get(1)).isEqualTo(gas2);
    }
}