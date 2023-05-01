package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Carbon;
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
class CarbonRepositoryTest {

    @Autowired CarbonRepository carbonRepository;
    @Autowired BuildingRepository buildingRepository;

    @Test
    public void findByBuildingOrderByRecordedAtAsc() throws Exception {
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

        carbonRepository.save(Carbon.builder()
                .recordedAt(LocalDate.of(2022, 01, 01))
                .usages(111)
                .building(building)
                .build());
        carbonRepository.save(Carbon.builder()
                .recordedAt(LocalDate.of(2022, 02, 01))
                .usages(222)
                .building(building)
                .build());
        carbonRepository.save(Carbon.builder()
                .recordedAt(LocalDate.of(2022, 03, 01))
                .usages(333)
                .building(building2)
                .build());

        //when
        List<Carbon> carbonList = carbonRepository.findByBuildingOrderByRecordedAtAsc(building);

        //then
        System.out.println("carbonList = " + carbonList);
        assertThat(carbonList.size()).isEqualTo(2);
        assertThat(carbonList.get(0).getUsages()).isEqualTo(111);
        assertThat(carbonList.get(1).getUsages()).isEqualTo(222);
    }

}