package com.example.airquality.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(
        name = "air_quality_records",
        uniqueConstraints = @UniqueConstraint(name = "uk_city_date", columnNames = {"city_id", "observed_date"})
)
public class AirQualityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "observed_date", nullable = false)
    private LocalDate observedDate;

    @Column(nullable = false)
    private double pm25;

    @Column(nullable = false)
    private double pm10;

    @Column(nullable = false)
    private double so2;

    @Column(nullable = false)
    private double no2;

    @Column(nullable = false)
    private double o3;

    @Column(nullable = false)
    private int aqi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private QualityLevel qualityLevel;

    protected AirQualityRecord() {
    }

    public AirQualityRecord(
            City city,
            LocalDate observedDate,
            double pm25,
            double pm10,
            double so2,
            double no2,
            double o3,
            int aqi,
            QualityLevel qualityLevel
    ) {
        this.city = city;
        this.observedDate = observedDate;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.so2 = so2;
        this.no2 = no2;
        this.o3 = o3;
        this.aqi = aqi;
        this.qualityLevel = qualityLevel;
    }

    public Long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public LocalDate getObservedDate() {
        return observedDate;
    }

    public double getPm25() {
        return pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public double getSo2() {
        return so2;
    }

    public double getNo2() {
        return no2;
    }

    public double getO3() {
        return o3;
    }

    public int getAqi() {
        return aqi;
    }

    public QualityLevel getQualityLevel() {
        return qualityLevel;
    }
}

