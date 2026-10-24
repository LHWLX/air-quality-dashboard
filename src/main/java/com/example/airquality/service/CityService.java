package com.example.airquality.service;

import com.example.airquality.dto.CityResponse;
import com.example.airquality.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<CityResponse> findAll() {
        return cityRepository.findAllByOrderByNameAsc().stream()
                .map(CityResponse::from)
                .toList();
    }
}

