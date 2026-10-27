package com.example.airquality.controller;

import com.example.airquality.dto.AirQualityResponse;
import com.example.airquality.dto.CityStatsResponse;
import com.example.airquality.dto.DistributionResponse;
import com.example.airquality.dto.MapPointResponse;
import com.example.airquality.dto.PageResponse;
import com.example.airquality.dto.RankingResponse;
import com.example.airquality.dto.Result;
import com.example.airquality.service.AirQualityService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "分页查询空气质量明细")
    public Result<PageResponse<AirQualityResponse>> findPage(
            @RequestParam(required = false) List<String> cities,
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort) {
        return Result.success(airQualityService.findPage(cities, indicator, start, end, page, size, sort));
    }

    @GetMapping("/trend")
    @Operation(summary = "查询趋势图数据")
    public Result<List<AirQualityResponse>> trend(@RequestParam(required = false) List<String> cities,
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(airQualityService.findTrend(cities, indicator, start, end));
    }

    @GetMapping("/stats")
    @Operation(summary = "查询 KPI 和城市统计")
    public Result<List<CityStatsResponse>> stats(@RequestParam(required = false) List<String> cities,
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(airQualityService.calculateStats(cities, indicator, start, end));
    }

    @GetMapping("/distribution")
    @Operation(summary = "查询空气质量等级分布")
    public Result<List<DistributionResponse>> distribution(@RequestParam(required = false) List<String> cities,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(airQualityService.distribution(cities, start, end));
    }

    @GetMapping("/ranking")
    @Operation(summary = "查询城市排行")
    public Result<List<RankingResponse>> ranking(@RequestParam(required = false) List<String> cities,
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(airQualityService.ranking(cities, indicator, start, end, limit));
    }

    @GetMapping("/map")
    @Operation(summary = "查询地图分布数据")
    public Result<List<MapPointResponse>> map(@RequestParam(required = false) List<String> cities,
            @RequestParam(defaultValue = "PM25") String indicator,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(airQualityService.mapPoints(cities, indicator, start, end));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单条记录")
    public Result<AirQualityResponse> findById(@PathVariable Long id) {
        return Result.success(airQualityService.findById(id));
    }
}
