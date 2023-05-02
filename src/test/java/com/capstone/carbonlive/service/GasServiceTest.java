package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.UsageWithNameResponse;
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
import java.util.List;
import java.util.stream.IntStream;

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
                .gasArea(new BigDecimal("1111.11"))
                .elecArea(new BigDecimal("2222.22"))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);

        for (int i = 1; i < 13; i++) {
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2022, i, 1))
                    .usages(i)
                    .prediction((i % 3 == 0))
                    .building(building)
                    .build());
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2021, i, 1))
                    .usages(2 * i)
                    .prediction((i % 2 == 0))
                    .building(building)
                    .build());
        }
        gasRepository.save(Gas.builder()
                .recordedAt(LocalDate.of(2021, 1, 1))
                .usages(52349)
                .building(building)
                .prediction(true)
                .build()); // 이 값이 반영되면 안된다.
    }

    @Test
    @DisplayName("건물별 가스 정보 출력")
    public void findByBuilding() {
        //when
        Building findBuilding = buildingRepository.findByName("building1");
        UsageResult<UsageResponse> result = gasService.findByBuilding(findBuilding.getId());

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getYear()).isEqualTo(2021);
        assertThat(result.getResult().get(0).getUsages().length).isEqualTo(12);
        for (int i = 0; i < 12; i++) {
            assertThat(result.getResult().get(0).getUsages()[i].getData()).isEqualTo(2 * (i + 1));
            assertThat(result.getResult().get(0).getUsages()[i].isPrediction()).isEqualTo( (i + 1) % 2 == 0 );
            assertThat(result.getResult().get(1).getUsages()[i].getData()).isEqualTo(i + 1);
            assertThat(result.getResult().get(1).getUsages()[i].isPrediction()).isEqualTo( (i + 1) % 3 == 0 );
        }
    }

    @Test
    @DisplayName("계절별 가스 사용량 출력")
    public void findBySeason() {
        //when
        UsageResult<SeasonResponse> result = gasService.findBySeason();

        //then
        System.out.println("result = " + result);
        assertThat(result.getResult().get(0).getUsages()[0]).isEqualTo(24);
        assertThat(result.getResult().get(1).getUsages()[3]).isEqualTo(0);
    }

    @Test
    @DisplayName("전체 가스 사용량 출력")
    public void findAll(){
        // when
        UsageResult<UsageWithNameResponse> usageResult = gasService.findAll();
        List<UsageWithNameResponse> result = usageResult.getResult();

        System.out.println("result = " + result);
        // then
        assertThat(result.get(0).getName()).isEqualTo("building1");

        IntStream.range(0, 2).forEach(i -> {
            assertThat(result.get(0).getUsagesList().get(i).getYear()).isEqualTo(2021 + i);

            IntStream.range(0, 12).forEach(j -> {
                assertThat(result.get(0).getUsagesList().get(i).getUsages()[j].getData()).isEqualTo((j + 1) * (2 - i));
                assertThat(result.get(0).getUsagesList().get(i).getUsages()[j].isPrediction()).isEqualTo( (j + 1) % (2 + i) == 0);
            });
        });
    }
}