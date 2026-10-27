package com.example.airquality.dto;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.service.Indicator;

import java.time.LocalDate;

public record AirQualityResponse(
        Long id, String cityCode, String cityName, String region, LocalDate date,
        double pm25, double pm10, double so2, double no2, double o3, int aqi,
        String qualityLevel, String indicator, String indicatorName, String unit, double indicatorValue
) {
    public static AirQualityResponse from(AirQualityRecord record, Indicator indicator) {
        return new AirQualityResponse(
                record.getId(), record.getCityCode(), record.getCityName(), record.getRegion(), record.getObservedDate(),
                record.getPm25(), record.getPm10(), record.getSo2(), record.getNo2(), record.getO3(), record.getAqi(),
                record.getQualityLevel(), indicator.name(), indicator.getDisplayName(), indicator.getUnit(), indicator.valueOf(record)
        );
    }
}
