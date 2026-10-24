package com.example.airquality.service;

import com.example.airquality.domain.AirQualityRecord;

import java.util.Locale;

public enum Indicator {
    PM25("PM2.5", "μg/m³"),
    PM10("PM10", "μg/m³"),
    SO2("SO₂", "μg/m³"),
    NO2("NO₂", "μg/m³"),
    O3("O₃", "μg/m³"),
    AQI("AQI", "");

    private final String displayName;
    private final String unit;

    Indicator(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUnit() {
        return unit;
    }

    public double valueOf(AirQualityRecord record) {
        return switch (this) {
            case PM25 -> record.getPm25();
            case PM10 -> record.getPm10();
            case SO2 -> record.getSo2();
            case NO2 -> record.getNo2();
            case O3 -> record.getO3();
            case AQI -> record.getAqi();
        };
    }

    public static Indicator parse(String value) {
        if (value == null || value.isBlank()) {
            return PM25;
        }
        String normalized = value.toUpperCase(Locale.ROOT)
                .replace(".", "")
                .replace("_", "")
                .replace("₂", "2")
                .replace("₃", "3");
        return Indicator.valueOf(normalized);
    }
}

