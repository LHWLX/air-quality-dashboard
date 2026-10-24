package com.example.airquality.controller;

import com.example.airquality.dto.AirQualityResponse;
import com.example.airquality.dto.CityStatsResponse;
import com.example.airquality.service.AirQualityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/air-quality")
@Tag(name = "空气质量")
public class AirQualityController {

    private final AirQualityService airQualityService;

    public AirQualityController(AirQualityService airQualityService) {
        this.airQualityService = airQualityService;
    }

    @GetMapping
    @Operation(summary = "按城市、指标和日期范围查询空气质量明细")
    public List<AirQualityResponse> findRecords(
            @Parameter(description = "城市代码，多个值以逗号分隔")
            @RequestParam(required = false) List<String> cities,
            @Parameter(description = "PM25、PM10、SO2、NO2、O3 或 AQI")
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return airQualityService.findRecords(cities, indicator, start, end);
    }

    @GetMapping("/stats")
    @Operation(summary = "按城市统计指标值和优良天占比")
    public List<CityStatsResponse> calculateStats(
            @RequestParam(required = false) List<String> cities,
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return airQualityService.calculateStats(cities, indicator, start, end);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单条空气质量记录")
    public AirQualityResponse findById(@PathVariable Long id) {
        return airQualityService.findById(id);
    }
}

