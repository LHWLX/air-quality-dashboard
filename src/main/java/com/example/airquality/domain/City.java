package com.example.airquality.domain;

public class City {

    private Long id;
    private String code;
    private String name;
    private String region;
    private double longitude;
    private double latitude;

    public City() {
    }

    public City(String code, String name, String region, double longitude, double latitude) {
        this.code = code;
        this.name = name;
        this.region = region;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
}
