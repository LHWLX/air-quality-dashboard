package com.example.airquality.dto;

public class MapPointResponse {
    private String cityCode;
    private String cityName;
    private String region;
    private double longitude;
    private double latitude;
    private double value;

    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
