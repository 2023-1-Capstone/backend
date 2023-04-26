package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private Long buildingId;

    @BeforeEach
    void beforeEach() {
        Building building = Building.builder()
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);
        this.buildingId = building.getId();

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
    @DisplayName("건물별 가스 사용량 받아오기")
    public void findGasByBuilding() throws Exception {
        //when
        ResultActions result = this.mockMvc.perform(get("/api/gas/{buildingCode}", buildingId));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2021))
                .andExpect(jsonPath("$.result[0].usages[0]").value(2))
                .andExpect(jsonPath("$.result[0].usages[11]").value(24))
                .andExpect(jsonPath("$.result[1].year").value(2022))
                .andExpect(jsonPath("$.result[1].usages[0]").value(1))
                .andExpect(jsonPath("$.result[1].usages[11]").value(12));

        //docs
        result.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("buildingCode").description("건물 코드 (id)")),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages").description("해당 년도에 따른 월별 사용량 집합(1월-12월)")
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
                .andExpect(jsonPath("$.result[0].startYear").value(2021))
                .andExpect(jsonPath("$.result[0].endYear").value(2022))
                .andExpect(jsonPath("$.result[0].usages[0]").value(24))
                .andExpect(jsonPath("$.result[0].usages[1]").value(42))
                .andExpect(jsonPath("$.result[0].usages[2]").value(60))
                .andExpect(jsonPath("$.result[0].usages[3]").value(27))
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
                .andExpect(jsonPath("$.result[0].usagesList[0].year").value(2021))
                .andExpect(jsonPath("$.result[0].usagesList[1].year").value(2022));

        for (int i = 0; i < 12; i++) {
            resultActions.andExpect(
                    jsonPath("$.result[0].usagesList[1].usages[" + i + "]").value(i + 1)
            );
        }

        resultActions.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].name").description("건물명"),
                                fieldWithPath("result[].usagesList").description("사용량 데이터"),
                                fieldWithPath("result[].usagesList[].year").description("해당 사용량 데이터의 년도"),
                                fieldWithPath("result[].usagesList[].usages").description("월별 사용량(1월-12월)")
                        )
                ));
    }
}