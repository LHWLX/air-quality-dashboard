package com.example.airquality;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.repository.AirQualityRecordRepository;
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
    private AirQualityRecordRepository recordRepository;

    @Test
    void initializesSixCitiesAndSevenHundredTwentyRecords() throws Exception {
        assertEquals(720, recordRepository.count());

        mockMvc.perform(get("/api/cities").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[*].code", everyItem(greaterThan(""))));
    }

    @Test
    void filtersTwoCitiesForThirtyDaysAndSwitchesIndicator() throws Exception {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        mockMvc.perform(get("/api/air-quality")
                        .param("cities", "beijing,shanghai")
                        .param("indicator", "NO2")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(60)))
                .andExpect(jsonPath("$[*].indicator", everyItem(is("NO2"))))
                .andExpect(jsonPath("$[0].indicatorValue", greaterThan(0.0)));
    }

    @Test
    void calculatesGroupedStatistics() throws Exception {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        mockMvc.perform(get("/api/air-quality/stats")
                        .param("cities", "beijing,shanghai")
                        .param("indicator", "PM25")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].totalDays", everyItem(is(30))))
                .andExpect(jsonPath("$[0].average", greaterThan(0.0)))
                .andExpect(jsonPath("$[0].goodDayRatio", closeTo(50.0, 50.0)));
    }

    @Test
    void returnsSingleRecordDetails() throws Exception {
        AirQualityRecord record = recordRepository.findAll().get(0);

        mockMvc.perform(get("/api/air-quality/{id}", record.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(record.getId().intValue())))
                .andExpect(jsonPath("$.cityName").isNotEmpty())
                .andExpect(jsonPath("$.qualityLevel").isNotEmpty());
    }

    @Test
    void rejectsUnsupportedIndicator() throws Exception {
        mockMvc.perform(get("/api/air-quality").param("indicator", "CO2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("不支持的指标：CO2")));
    }
}

