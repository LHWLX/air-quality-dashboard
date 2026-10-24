package com.example.airquality.domain;

public enum QualityLevel {
    EXCELLENT("优"),
    GOOD("良"),
    LIGHT("轻度污染"),
    MODERATE("中度污染"),
    HEAVY("重度污染");

    private final String displayName;

    QualityLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isGoodDay() {
        return this == EXCELLENT || this == GOOD;
    }

    public static QualityLevel fromAqi(int aqi) {
        if (aqi <= 50) {
            return EXCELLENT;
        }
        if (aqi <= 100) {
            return GOOD;
        }
        if (aqi <= 150) {
            return LIGHT;
        }
        if (aqi <= 200) {
            return MODERATE;
        }
        return HEAVY;
    }
}

