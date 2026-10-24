package com.example.airquality.repository;

import com.example.airquality.domain.AirQualityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AirQualityRecordRepository
        extends JpaRepository<AirQualityRecord, Long>, JpaSpecificationExecutor<AirQualityRecord> {
}

