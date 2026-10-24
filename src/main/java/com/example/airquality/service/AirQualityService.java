package com.example.airquality.service;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.dto.AirQualityResponse;
import com.example.airquality.dto.CityStatsResponse;
import com.example.airquality.repository.AirQualityRecordRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AirQualityService {

    private final AirQualityRecordRepository recordRepository;

    public AirQualityService(AirQualityRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Transactional(readOnly = true)
    public List<AirQualityResponse> findRecords(
            List<String> cityCodes,
            String indicatorValue,
            LocalDate start,
            LocalDate end
    ) {
        Indicator indicator = Indicator.parse(indicatorValue);
        return findEntities(cityCodes, start, end).stream()
                .map(record -> AirQualityResponse.from(record, indicator))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CityStatsResponse> calculateStats(
            List<String> cityCodes,
            String indicatorValue,
            LocalDate start,
            LocalDate end
    ) {
        Indicator indicator = Indicator.parse(indicatorValue);
        Map<String, List<AirQualityRecord>> byCity = new LinkedHashMap<>();
        for (AirQualityRecord record : findEntities(cityCodes, start, end)) {
            byCity.computeIfAbsent(record.getCity().getCode(), ignored -> new ArrayList<>()).add(record);
        }

        return byCity.values().stream()
                .map(records -> toStats(records, indicator))
                .sorted(Comparator.comparing(CityStatsResponse::cityName))
                .toList();
    }

    @Transactional(readOnly = true)
    public AirQualityResponse findById(Long id) {
        AirQualityRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        record.getCity().getName();
        return AirQualityResponse.from(record, Indicator.AQI);
    }

    private List<AirQualityRecord> findEntities(List<String> cityCodes, LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        Specification<AirQualityRecord> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cityCodes != null && !cityCodes.isEmpty()) {
                List<String> normalizedCodes = cityCodes.stream()
                        .filter(code -> code != null && !code.isBlank())
                        .map(String::trim)
                        .toList();
                if (!normalizedCodes.isEmpty()) {
                    predicates.add(root.get("city").get("code").in(normalizedCodes));
                }
            }
            if (start != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("observedDate"), start));
            }
            if (end != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("observedDate"), end));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        return recordRepository.findAll(
                specification,
                Sort.by(Sort.Order.asc("observedDate"), Sort.Order.asc("city.name"))
        );
    }

    private CityStatsResponse toStats(List<AirQualityRecord> records, Indicator indicator) {
        AirQualityRecord first = records.get(0);
        double average = records.stream().mapToDouble(indicator::valueOf).average().orElse(0);
        double maximum = records.stream().mapToDouble(indicator::valueOf).max().orElse(0);
        double minimum = records.stream().mapToDouble(indicator::valueOf).min().orElse(0);
        long goodDays = records.stream().filter(record -> record.getQualityLevel().isGoodDay()).count();

        return new CityStatsResponse(
                first.getCity().getCode(),
                first.getCity().getName(),
                indicator.name(),
                indicator.getDisplayName(),
                indicator.getUnit(),
                round(average),
                round(maximum),
                round(minimum),
                round(goodDays * 100.0 / records.size()),
                records.size()
        );
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

