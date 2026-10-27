package com.example.airquality.dto;

public class RankingResponse {
    private String cityCode;
    private String cityName;
    private double value;

    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
