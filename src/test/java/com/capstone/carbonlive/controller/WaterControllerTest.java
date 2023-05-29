package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.WaterPredictionFeeResponse;
import com.capstone.carbonlive.dto.response.WaterResponse;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.security.jwt.JwtAuthenticationFilter;
import com.capstone.carbonlive.security.jwt.JwtTokenProvider;
import com.capstone.carbonlive.service.WaterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaterController.class)
class WaterControllerTest extends AbstractRestDocsTest {

    @MockBean private WaterService waterService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    private final String uri = "/api/water";

    @Test
    void getWater() throws Exception {
        List<WaterResponse> responses = new ArrayList<>();

        //2018-01 부터 2019-09 까지 실제값
        WaterResponse response1 = new WaterResponse(2018);
        IntStream.range(0, 12).forEach(i ->
                response1.getUsages()[i] = WaterPredictionFeeResponse.builder().data(i + 1).fee(i + 1).build());
        WaterResponse response2 = new WaterResponse(2019);
        IntStream.range(0, 9).forEach(i ->
                response2.getUsages()[i] = WaterPredictionFeeResponse.builder().data(i + 1).fee(i + 1).build());

        //2019-10 부터 2019-12 까지 예측값
        IntStream.range(8, 12).forEach(i ->
                response2.getUsages()[i] = WaterPredictionFeeResponse.builder().prediction(i + 1).build());

        responses.add(response1);
        responses.add(response2);

        UsageResult<WaterResponse> usageResult = new UsageResult<>(responses);

        System.out.println("usageResult = " + usageResult);

        given(waterService.getAll())
                .willReturn(usageResult);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[1].year").value(2019));

        for (int i = 0; i < 12; i++) {
            resultActions.andExpect(jsonPath("$.result[0].usages[" + i + "].data").value(i + 1));
            resultActions.andExpect(jsonPath("$.result[0].usages[" + i + "].fee").value(i + 1));
        }

        resultActions
                .andExpect(jsonPath("$.result[1].usages[0].data").value(1))
                .andExpect(jsonPath("$.result[1].usages[0].fee").value(1))
                .andExpect(jsonPath("$.result[1].usages[0].prediction", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[8].prediction").value(9))
                .andExpect(jsonPath("$.result[1].usages[8].data", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[8].fee", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[11].prediction").value(12))
                .andExpect(jsonPath("$.result[1].usages[11].data", nullValue()))
                .andExpect(jsonPath("$.result[1].usages[11].fee", nullValue()))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages").description("해당 년도에 따른 월별 사용량 집합(1월-12월)"),
                                fieldWithPath("result[].usages[].data").type(JsonFieldType.NUMBER)
                                        .description("실 사용량. null인 경우, 실측 사용량이 없음").optional(),
                                fieldWithPath("result[].usages[].prediction").type(JsonFieldType.NUMBER)
                                        .description("예측 사용량. null인 경우, 실측값이 존재하거나 예측 사용량이 없음").optional(),
                                fieldWithPath("result[].usages[].fee").type(JsonFieldType.NUMBER)
                                        .description("사용요금. null인 경우, 사용요금 데이터가 없음").optional()
                        ))
                );

        then(waterService).should(times(1)).getAll();
    }
}
