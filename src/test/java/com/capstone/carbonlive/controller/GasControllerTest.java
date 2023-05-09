package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.FeeResponse;
import com.capstone.carbonlive.dto.UsageFeeResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.entity.GasFee;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasFeeRepository;
import com.capstone.carbonlive.repository.GasRepository;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class GasControllerTest extends AbstractRestDocsTest {

    @Autowired GasRepository gasRepository;
    @Autowired BuildingRepository buildingRepository;
    @Autowired GasFeeRepository gasFeeRepository;

    private Long buildingId;

    @BeforeEach
    void beforeEach() {
        Building building = Building.builder()
                .name("building1")
                .gasArea(new BigDecimal("1111.11"))
                .elecArea(new BigDecimal("2222.22"))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);
        this.buildingId = building.getId();

        //2018-01 부터 2019-09 까지 실제값
        for (int i = 1; i < 13; i++) {
            gasRepository.save(Gas.builder()
                    .recordedAt(LocalDate.of(2018, i, 1))
                    .usages(i)
                    .building(building)
                    .build());
        }
        for (int i = 1; i < 10; i++) {
            gasRepository.save(Gas.builder()
                    .recordedAt(LocalDate.of(2019, i, 1))
                    .usages(i)
                    .building(building)
                    .build());
        }
        //2019-10 부터 2019-12 까지 예측값
        for (int i = 10; i < 13; i++) {
            gasRepository.save(Gas.builder().
                    recordedAt(LocalDate.of(2019, i, 1))
                    .prediction(i)
                    .building(building)
                    .build());
        }
    }

    @Test
    @DisplayName("건물별 가스 사용량 받아오기")
    public void findGasByBuilding() throws Exception {
        //when
        ResultActions result = this.mockMvc.perform(get("/api/gas/{buildingCode}", buildingId));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[0].usages[0].data").value(1))
                .andExpect(jsonPath("$.result[0].usages[0].prediction", nullValue()))
                .andExpect(jsonPath("$.result[1].year").value(2019))
                .andExpect(jsonPath("$.result[1].usages[8].data").value(9))
                .andExpect(jsonPath("$.result[1].usages[8].prediction", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[9].data", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[9].prediction").value(10));

        //docs
        result.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("buildingCode").description("건물 코드 (id)")),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages").description("해당 년도에 따른 월별 사용량 집합(1월-12월)"),
                                fieldWithPath("result[].usages[].data").type(JsonFieldType.NUMBER)
                                        .description("실 사용량. null인 경우, 실측 사용량이 없음").optional(),
                                fieldWithPath("result[].usages[].prediction").type(JsonFieldType.NUMBER)
                                        .description("예측 사용량. null인 경우, 실측값이 존재하거나 예측 사용량이 없음").optional()
                        )
                ));
    }

    @Test
    @DisplayName("계절별 가스 사용량 받아오기")
    public void findGasBySeason() throws Exception {
        //when
        ResultActions result = this.mockMvc.perform(get("/api/gas/season"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].startYear").value(2018))
                .andExpect(jsonPath("$.result[0].endYear").value(2019))
                .andExpect(jsonPath("$.result[0].usages[0]").value(12))
                .andExpect(jsonPath("$.result[0].usages[1]").value(21))
                .andExpect(jsonPath("$.result[0].usages[2]").value(30))
                .andExpect(jsonPath("$.result[0].usages[3]").value(15))
                .andExpect(jsonPath("$.result[1].startYear").value(2019))
                .andExpect(jsonPath("$.result[1].endYear").value(2020))
                .andExpect(jsonPath("$.result[1].usages[2]").value(0))
                .andExpect(jsonPath("$.result[1].usages[3]").value(0));

        //docs
        result.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].startYear").description("해당 기간의 시작 년도"),
                                fieldWithPath("result[].endYear").description("해당 기간의 끝 년도"),
                                fieldWithPath("result[].usages").description("해당 기간의 계절별 학교 가스 사용량 집합(3월- 다음해2월)")
                        )
                ));
    }

    @Test
    public void findGasAll() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/api/gas/area")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("building1"))
                .andExpect(jsonPath("$.result[0].usagesList[0].year").value(2018))
                .andExpect(jsonPath("$.result[0].usagesList[0].usages[0].data").value(1))
                .andExpect(jsonPath("$.result[0].usagesList[0].usages[0].prediction", nullValue()))
                .andExpect(jsonPath("$.result[0].usagesList[1].year").value(2019))
                .andExpect(jsonPath("$.result[0].usagesList[1].usages[8].data").value(9))
                .andExpect(jsonPath("$.result[0].usagesList[1].usages[8].prediction", nullValue()))
                .andExpect(jsonPath("$.result[0].usagesList[1].usages[9].data", nullValue()))
                .andExpect(jsonPath("$.result[0].usagesList[1].usages[9].prediction").value(10));

        resultActions.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].name").description("건물명"),
                                fieldWithPath("result[].usagesList").description("사용량 데이터"),
                                fieldWithPath("result[].usagesList[].year").description("해당 사용량 데이터의 년도"),
                                fieldWithPath("result[].usagesList[].usages").description("월별 사용량(1월-12월)"),
                                fieldWithPath("result[].usagesList[].usages[].data").type(JsonFieldType.NUMBER)
                                        .description("실 사용량. null인 경우, 실측 사용량이 없음").optional(),
                                fieldWithPath("result[].usagesList[].usages[].prediction").type(JsonFieldType.NUMBER)
                                        .description("예측 사용량. null인 경우, 실측값이 존재하거나 예측 사용량이 없음").optional()
                        )
                ));
    }

    @Test
    @DisplayName("월별 단위 가스 사용요금 받아오기")
    public void findGasFee() throws Exception {
        //given
        IntStream.range(0, 12).forEach(i ->
                gasFeeRepository.save(GasFee.builder().
                        recordedAt(LocalDate.of(2018, i + 1, 1)).usages(i + 1).fee(i  + 1).build())
        );
        IntStream.range(0, 12).forEach(i ->
                gasFeeRepository.save(GasFee.builder().
                        recordedAt(LocalDate.of(2019, i + 1, 1)).usages(i + 1).fee(i  + 1).build())
        );

        //when, then
        this.mockMvc.perform(get("/api/gas/fee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[0].feeResponses[0].usages").value(1))
                .andExpect(jsonPath("$.result[0].feeResponses[0].fee").value(1))
                .andExpect(jsonPath("$.result[1].year").value(2019))
                .andExpect(jsonPath("$.result[1].feeResponses[11].usages").value(12))
                .andExpect(jsonPath("$.result[1].feeResponses[11].fee").value(12))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("해당 사용요금 데이터의 년도"),
                                fieldWithPath("result[].feeResponses").description("사용요금 데이터"),
                                fieldWithPath("result[].feeResponses[].usages").description("월별 전체 사용량(1월-12월)"),
                                fieldWithPath("result[].feeResponses[].fee").description("월별 사용요금(1월-12월)")
                        )
                ));
    }
}
