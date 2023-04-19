package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ElectricityRepositoryTest {
    @Autowired
    private ElectricityRepository electricityRepository;
    @Autowired
    private BuildingRepository buildingRepository;

    @BeforeEach
    void setUp(){
        Building sampleBuilding = Building.builder()
                .name("본관")
                .elecArea(new BigDecimal("10"))
                .gasArea(new BigDecimal("10"))
                .build();
        buildingRepository.save(sampleBuilding);

        List<Electricity> tempList = new ArrayList<>();
        IntStream.rangeClosed(2, 10).forEach(i -> {
            int year = i > 5 ? 2018 : 2019;
            Electricity e = Electricity.builder()
                    .recordedAt(LocalDate.of(year, i, 1))
                    .building(sampleBuilding)
                    .usages(12000 + i)
                    .build();
            tempList.add(e);
        });
        electricityRepository.saveAll(tempList);
    }

    @Test
    void findAllByIdTest(){
        // given
        Building building = buildingRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        // when
        List<Electricity> list = electricityRepository.findAllByBuilding(building);

        // then
        System.out.println("list = " + list);
        assertThat(list.size()).isEqualTo(9);

    }
}