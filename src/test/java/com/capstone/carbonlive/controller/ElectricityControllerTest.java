package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.*;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.security.jwt.JwtAuthenticationFilter;
import com.capstone.carbonlive.security.jwt.JwtTokenProvider;
import com.capstone.carbonlive.service.ElectricityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
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

@WebMvcTest(ElectricityController.class)
class ElectricityControllerTest extends AbstractRestDocsTest {

    @MockBean private ElectricityService electricityService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    private final String uri = "/api/electricity/";

    @Test
        //@WithMockUser // 스프링 시큐리티 회피
    void getElectricityEach() throws Exception {
        ArrayList<UsageResponse> list = new ArrayList<>();

        // 2018-01 부터 2019-09 까지 실제값
        UsageResponse response1 = new UsageResponse();
        response1.setYear(2018);
        IntStream.range(0, 12).forEach(i ->
                response1.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).build()
        );
        UsageResponse response2 = new UsageResponse();
        response2.setYear(2019);
        IntStream.range(0, 9).forEach(i ->
                response2.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).build()
        );

        // 2019-10 부터 2019-12 까지 예측값
        IntStream.range(9, 12).forEach(i ->
                response2.getUsages()[i] = UsagePredictionResponse.builder().prediction(i + 1).build()
        );

        list.add(response1);
        list.add(response2);

        UsageResult<UsageResponse> result = new UsageResult<>(list);
        System.out.println("result = " + result);
        given(electricityService.getEachAll(1L))
                .willReturn(result);

        mockMvc.perform(get(uri + "{buildingCode}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[0].usages[11].data").value(12))
                .andExpect(jsonPath("$.result[0].usages[11].prediction", nullValue()))
                .andExpect(jsonPath("$.result[1].year").value(2019))
                .andExpect(jsonPath("$.result[1].usages[8].data").value(9))
                .andExpect(jsonPath("$.result[1].usages[8].prediction", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[9].data", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[9].prediction").value(10))
                .andDo(print())
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

        then(electricityService).should(times(1)).getEachAll(1L);
    }

    @Test
    @DisplayName("계절별 전기 사용량 받아오기")
    public void findElectricityBySeason() throws Exception {
        //given
        int[] usages = IntStream.rangeClosed(1, 12).toArray();
        ArrayList<SeasonResponse> list = new ArrayList<>();

        SeasonResponse response1 = new SeasonResponse(2018);
        System.arraycopy(usages, 0, response1.getUsages(), 0, 4);

        SeasonResponse response2 = new SeasonResponse(2019);
        System.arraycopy(usages, 0, response2.getUsages(), 0, 4);
        response2.getUsages()[3] = 16;

        list.add(response1);
        list.add(response2);

        UsageResult<SeasonResponse> result = new UsageResult<>(list);
        System.out.println("result = " + result);
        given(electricityService.getSeasonData())
                .willReturn(result);

        //when
        ResultActions perform = this.mockMvc.perform(get("/api/electricity/season"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].startYear").value(2018))
                .andExpect(jsonPath("$.result[0].endYear").value(2019))
                .andExpect(jsonPath("$.result[0].usages[0]").value(1))
                .andExpect(jsonPath("$.result[0].usages[1]").value(2))
                .andExpect(jsonPath("$.result[0].usages[2]").value(3))
                .andExpect(jsonPath("$.result[0].usages[3]").value(4))
                .andExpect(jsonPath("$.result[1].startYear").value(2019))
                .andExpect(jsonPath("$.result[1].endYear").value(2020))
                .andExpect(jsonPath("$.result[1].usages[3]").value(16));

        //docs
        perform.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].startYear").description("해당 기간의 시작 년도"),
                                fieldWithPath("result[].endYear").description("해당 기간의 끝 년도"),
                                fieldWithPath("result[].usages").description("해당 기간의 계절별 학교 전기 사용량 집합(3월- 다음해2월)")
                        )
                ));
    }

    @Test
    void getElectricityAll() throws Exception {
        // given
        UsageResult<UsageWithNameResponse> result = new UsageResult<>(new ArrayList<>());
        UsageWithNameResponse response1 = new UsageWithNameResponse();
        response1.setName("본관");
        UsageWithNameResponse response2 = new UsageWithNameResponse();
        response2.setName("60주년기념관");

        UsageResult<UsageResponse> usageResult = new UsageResult<>(new ArrayList<>());
        UsageResponse usageResponse1 = new UsageResponse(2017);

        IntStream.range(0, 11).forEach(i ->
                usageResponse1.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).build()
        );
        usageResponse1.getUsages()[11] = UsagePredictionResponse.builder().prediction(100).build();

        UsageResponse usageResponse2 = new UsageResponse(2018);
        IntStream.range(0, 12).forEach(i ->
                usageResponse2.getUsages()[i] = UsagePredictionResponse.builder().data(i + 13).build()
        );

        usageResult.add(usageResponse1);
        usageResult.add(usageResponse2);

        response1.setUsagesList(usageResult.getResult());
        response2.setUsagesList(usageResult.getResult());

        result.add(response1);
        result.add(response2);

        System.out.println("result = " + result);
        given(electricityService.getAll())
                .willReturn(result);

        // when
        this.mockMvc.perform(get(uri + "/area")
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("본관"))
                .andExpect(jsonPath("$.result[1].name").value("60주년기념관"))
                .andExpect(jsonPath("$.result[0].usagesList[0].usages[11].prediction").value(100))
                .andExpect(jsonPath("$.result[1].usagesList.length()").value(2))
                .andExpect(jsonPath("$.result[1].usagesList[0].year").value(2017))
                .andExpect(jsonPath("$.result[1].usagesList[1].usages[11].data").value(24))
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
                                        .description("실 사용량. null이면 실 사용량 없음").optional(),
                                fieldWithPath("result[].usagesList[].usages[].prediction").type(JsonFieldType.NUMBER)
                                        .description("예측 사용량. null이면 실 사용량이 있거나 예측 사용량이 없음").optional()
                        )
                ));

        // then
        then(electricityService).should(times(1)).getAll();
    }

    @Test
    @DisplayName("월별 단위 전기 사용요금 받아오기")
    public void findElectricityFee() throws Exception {
        //given
        UsageResult<FeeResponse> result = new UsageResult<>(new ArrayList<>());

        FeeResponse response = new FeeResponse(2018);
        IntStream.range(0, 12).forEach(i ->
                response.getFeeResponses()[i] = UsageFeeResponse.builder().usages(i + 1).fee(i + 1).build()
        );
        FeeResponse response2 = new FeeResponse(2019);
        IntStream.range(0, 12).forEach(i ->
                response2.getFeeResponses()[i] = UsageFeeResponse.builder().usages(i + 1).fee(i + 1).build()
        );

        result.add(response);
        result.add(response2);

        System.out.println("result = " + result);
        given(electricityService.getFee())
                .willReturn(result);

        //when
        this.mockMvc.perform(get(uri + "/fee")
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

        //then
        then(electricityService).should(times(1)).getFee();
    }
}
