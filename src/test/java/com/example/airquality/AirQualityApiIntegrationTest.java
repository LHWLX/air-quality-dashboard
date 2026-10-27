package com.example.airquality;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.mapper.AirQualityRecordMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AirQualityApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AirQualityRecordMapper recordMapper;

    @Test
    void initializesSixCitiesAndSevenHundredTwentyRecords() throws Exception {
        assertEquals(720, recordMapper.countAll());

        mockMvc.perform(get("/api/cities").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(6)))
                .andExpect(jsonPath("$.data[*].code", everyItem(greaterThan(""))));
    }

    @Test
    void filtersTwoCitiesForThirtyDaysAndSwitchesIndicator() throws Exception {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        mockMvc.perform(get("/api/air-quality")
                        .param("cities", "beijing,tianjin")
                        .param("indicator", "NO2")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .param("page", "1")
                        .param("size", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements", is(60)))
                .andExpect(jsonPath("$.data.content", hasSize(60)))
                .andExpect(jsonPath("$.data.content[*].indicator", everyItem(is("NO2"))))
                .andExpect(jsonPath("$.data.content[0].indicatorValue", greaterThan(0.0)));
    }

    @Test
    void calculatesGroupedStatistics() throws Exception {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        mockMvc.perform(get("/api/air-quality/stats")
                        .param("cities", "beijing,tianjin")
                        .param("indicator", "PM25")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].totalDays", everyItem(is(30))))
                .andExpect(jsonPath("$.data[0].average", greaterThan(0.0)))
                .andExpect(jsonPath("$.data[0].goodDayRatio", closeTo(50.0, 50.0)));
    }

    @Test
    void returnsSingleRecordDetails() throws Exception {
        AirQualityRecord record = recordMapper.findPage(null, null, null, "aqi", "desc", 0, 1).get(0);

        mockMvc.perform(get("/api/air-quality/{id}", record.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(record.getId().intValue())))
                .andExpect(jsonPath("$.data.cityName").isNotEmpty())
                .andExpect(jsonPath("$.data.qualityLevel").isNotEmpty());
    }

    @Test
    void rejectsUnsupportedIndicator() throws Exception {
        mockMvc.perform(get("/api/air-quality").param("indicator", "CO2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("不支持的指标：CO2")));
    }

    @Test
    void returnsQualityDistribution() throws Exception {
        mockMvc.perform(get("/api/air-quality/distribution").param("cities", "beijing,tianjin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].value", greaterThan(0)));
    }

    @Test
    void returnsRankingAndMapPoints() throws Exception {
        mockMvc.perform(get("/api/air-quality/ranking")
                        .param("cities", "beijing,tianjin")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));

        mockMvc.perform(get("/api/air-quality/map").param("cities", "beijing,tianjin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].longitude", greaterThan(0.0)));
    }

    @Test
    void rejectsReversedDateRange() throws Exception {
        mockMvc.perform(get("/api/air-quality")
                        .param("start", LocalDate.now().toString())
                        .param("end", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("开始日期不能晚于结束日期")));
    }
}

