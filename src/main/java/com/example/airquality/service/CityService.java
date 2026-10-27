package com.example.airquality.service;

import com.example.airquality.dto.CityResponse;
import com.example.airquality.mapper.CityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityMapper cityMapper;

    public CityService(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    public List<CityResponse> findAll() {
        return cityMapper.findAll().stream().map(CityResponse::from).toList();
    }
}
