package com.example.airquality.mapper;

import com.example.airquality.domain.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface CityMapper {
    long count();
    List<City> findAll();

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(City city);
}
