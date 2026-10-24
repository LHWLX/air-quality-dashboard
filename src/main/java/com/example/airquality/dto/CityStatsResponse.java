package com.example.airquality.dto;

public record CityStatsResponse(
        String cityCode,
        String cityName,
        String indicator,
        String indicatorName,
        String unit,
        double average,
        double maximum,
        double minimum,
        double goodDayRatio,
        long totalDays
) {
}

