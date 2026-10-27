package com.example.airquality.controller;

import com.example.airquality.dto.CityResponse;
import com.example.airquality.dto.Result;
import com.example.airquality.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@Tag(name = "城市")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "查询全部城市")
    public Result<List<CityResponse>> findAll() {
        return Result.success(cityService.findAll());
    }
}
