package com.example.airquality.domain;

import java.time.LocalDate;

public class AirQualityRecord {

    private Long id;
    private Long cityId;
    private String cityCode;
    private String cityName;
    private String region;
    private LocalDate observedDate;
    private double pm25;
    private double pm10;
    private double so2;
    private double no2;
    private double o3;
    private int aqi;
    private String qualityLevel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public LocalDate getObservedDate() { return observedDate; }
    public void setObservedDate(LocalDate observedDate) { this.observedDate = observedDate; }
    public double getPm25() { return pm25; }
    public void setPm25(double pm25) { this.pm25 = pm25; }
    public double getPm10() { return pm10; }
    public void setPm10(double pm10) { this.pm10 = pm10; }
    public double getSo2() { return so2; }
    public void setSo2(double so2) { this.so2 = so2; }
    public double getNo2() { return no2; }
    public void setNo2(double no2) { this.no2 = no2; }
    public double getO3() { return o3; }
    public void setO3(double o3) { this.o3 = o3; }
    public int getAqi() { return aqi; }
    public void setAqi(int aqi) { this.aqi = aqi; }
    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }
}
