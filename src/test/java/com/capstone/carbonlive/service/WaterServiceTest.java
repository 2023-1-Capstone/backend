package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.WaterResponse;
import com.capstone.carbonlive.entity.Water;
import com.capstone.carbonlive.repository.WaterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WaterServiceTest {

    @Autowired
    private WaterService waterService;
    @Autowired
    private WaterRepository waterRepository;

    @BeforeEach
    void setUp(){

        //2018-10 부터 2018-09 까지는 실제값
        IntStream.rangeClosed(10, 12).forEach(i -> {
            Water water = Water.builder()
                    .usages(i)
                    .recordedAt(LocalDate.of(2018, i, 1))
                    .fee(i)
                    .build();
            waterRepository.save(water);
        });
        IntStream.rangeClosed(1, 9).forEach(i -> {
            Water water = Water.builder()
                    .usages(i)
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .fee(i)
                    .build();
            waterRepository.save(water);
        });

        //2019-10 부터 2019-12 까지는 예측값
        IntStream.rangeClosed(9, 12).forEach(i -> {
            Water water = Water.builder()
                    .recordedAt(LocalDate.of(2019, i, 1) )
                    .prediction(i)
                    .build();
            waterRepository.save(water);
        });
    }

    @Test
    @DisplayName("모든 수도 사용량을 반환")
    void getAll(){
        //given
        int[] expectYear = {2018, 2019};
        int[][] expectData = {{10, 11, 12}, {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}};

        // when
        UsageResult<WaterResponse> ascResult = waterService.getAll();

        // then
        System.out.println("ascResult = " + ascResult);
        for (int i = 0; i < 2; i++) {
            assertThat(ascResult.getResult().get(i).getYear()).isEqualTo(expectYear[i]);
            if (i == 0) { //getYear() == 2018
                for (int j = 0; j < 3; j++) { //실제값
                    assertThat(ascResult.getResult().get(i).getUsages()[j + 9].getData()).isEqualTo(expectData[i][j]);
                    assertThat(ascResult.getResult().get(i).getUsages()[j + 9].getPrediction()).isEqualTo(null);
                    assertThat(ascResult.getResult().get(i).getUsages()[j + 9].getFee()).isEqualTo(expectData[i][j]);
                }
            }
            else { // getYear() == 2019
                for (int j = 0; j < 9; j++) { //실제값
                    assertThat(ascResult.getResult().get(i).getUsages()[j].getData()).isEqualTo(expectData[i][j]);
                    assertThat(ascResult.getResult().get(i).getUsages()[j].getPrediction()).isEqualTo(null);
                    assertThat(ascResult.getResult().get(i).getUsages()[j].getFee()).isEqualTo(expectData[i][j]);
                }
                for (int j = 9; j < 12; j++) { //예측값
                    assertThat(ascResult.getResult().get(i).getUsages()[j].getData()).isEqualTo(null);
                    assertThat(ascResult.getResult().get(i).getUsages()[j].getPrediction()).isEqualTo(expectData[i][j]);
                    assertThat(ascResult.getResult().get(i).getUsages()[j].getFee()).isEqualTo(null);
                }
            }
        }
    }
}