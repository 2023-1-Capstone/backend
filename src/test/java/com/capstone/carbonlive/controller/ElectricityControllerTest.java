package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.service.ElectricityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.stream.IntStream;

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
    @MockBean
    private ElectricityService electricityService;
    private final String uri = "/api/electricity/";

    @Test
        //@WithMockUser // 스프링 시큐리티 회피
    void getElectricityEach() throws Exception {
        int[] usages = IntStream.rangeClosed(1, 12).toArray();
        ArrayList<UsageResponse> list = new ArrayList<>();

        UsageResponse response1 = new UsageResponse();
        response1.setYear(2018);
        System.arraycopy(usages, 0, response1.getUsages(), 0, 12);

        UsageResponse response2 = new UsageResponse();
        response2.setYear(2019);
        System.arraycopy(usages, 0, response2.getUsages(), 0, 12);
        response2.getUsages()[11] = 16;

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
                .andExpect(jsonPath("$.result[0].usages[11]").value(12))
                .andExpect(jsonPath("$.result[1].year").value(2019))
                .andExpect(jsonPath("$.result[1].usages[11]").value(16))
                .andDo(print())
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
}