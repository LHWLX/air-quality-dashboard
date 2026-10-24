package com.example.airquality.repository;

import com.example.airquality.domain.AirQualityRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface AirQualityRecordRepository
        extends JpaRepository<AirQualityRecord, Long>, JpaSpecificationExecutor<AirQualityRecord> {

    @Override
    @EntityGraph(attributePaths = "city")
    List<AirQualityRecord> findAll(Specification<AirQualityRecord> specification, Sort sort);
}


