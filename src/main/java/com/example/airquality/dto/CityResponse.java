package com.example.airquality.dto;

import com.example.airquality.domain.City;

public record CityResponse(Long id, String code, String name, String region, double longitude, double latitude) {
    public static CityResponse from(City city) {
        return new CityResponse(city.getId(), city.getCode(), city.getName(), city.getRegion(), city.getLongitude(), city.getLatitude());
    }
}
