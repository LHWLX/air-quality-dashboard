package com.example.airquality.service;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.dto.AirQualityResponse;
import com.example.airquality.dto.CityStatsResponse;
import com.example.airquality.dto.DistributionResponse;
import com.example.airquality.dto.MapPointResponse;
import com.example.airquality.dto.PageResponse;
import com.example.airquality.dto.RankingResponse;
import com.example.airquality.mapper.AirQualityRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AirQualityService {
    private final AirQualityRecordMapper recordMapper;

    public AirQualityService(AirQualityRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    public PageResponse<AirQualityResponse> findPage(List<String> cityCodes, String indicatorValue,
                                                     LocalDate start, LocalDate end, int page, int size, String sort) {
        validateDates(start, end);
        if (page < 1 || size < 1 || size > 200) {
            throw new IllegalArgumentException("页码必须从 1 开始，每页数量必须在 1 至 200 之间");
        }
        Indicator indicator = Indicator.parse(indicatorValue);
        List<String> cities = normalizeCities(cityCodes);
        long total = recordMapper.countByFilter(cities, start, end);
        List<AirQualityResponse> content = recordMapper.findPage(cities, start, end, indicator.getColumnName(),
                        "asc".equalsIgnoreCase(sort) ? "asc" : "desc", (page - 1) * size, size)
                .stream().map(record -> AirQualityResponse.from(record, indicator)).toList();
        return PageResponse.of(content, total, page, size);
    }

    public List<AirQualityResponse> findTrend(List<String> cityCodes, String indicatorValue, LocalDate start, LocalDate end) {
        validateDates(start, end);
        Indicator indicator = Indicator.parse(indicatorValue);
        return recordMapper.findTrend(normalizeCities(cityCodes), start, end).stream()
                .map(record -> AirQualityResponse.from(record, indicator)).toList();
    }

    public List<CityStatsResponse> calculateStats(List<String> cityCodes, String indicatorValue,
                                                  LocalDate start, LocalDate end) {
        validateDates(start, end);
        Indicator indicator = Indicator.parse(indicatorValue);
        return recordMapper.findStats(normalizeCities(cityCodes), start, end, indicator.getColumnName(),
                indicator.name(), indicator.getDisplayName(), indicator.getUnit());
    }

    public List<DistributionResponse> distribution(List<String> cityCodes, LocalDate start, LocalDate end) {
        validateDates(start, end);
        return recordMapper.findDistribution(normalizeCities(cityCodes), start, end);
    }

    public List<RankingResponse> ranking(List<String> cityCodes, String indicatorValue,
                                         LocalDate start, LocalDate end, int limit) {
        validateDates(start, end);
        Indicator indicator = Indicator.parse(indicatorValue);
        return recordMapper.findRanking(normalizeCities(cityCodes), start, end, indicator.getColumnName(),
                Math.max(1, Math.min(limit, 20)));
    }

    public List<MapPointResponse> mapPoints(List<String> cityCodes, String indicatorValue,
                                            LocalDate start, LocalDate end) {
        validateDates(start, end);
        Indicator indicator = Indicator.parse(indicatorValue);
        return recordMapper.findMapPoints(normalizeCities(cityCodes), start, end, indicator.getColumnName());
    }

    public AirQualityResponse findById(Long id) {
        AirQualityRecord record = recordMapper.findById(id);
        if (record == null) {
            throw new RecordNotFoundException(id);
        }
        return AirQualityResponse.from(record, Indicator.AQI);
    }

    private static List<String> normalizeCities(List<String> cityCodes) {
        return cityCodes == null ? List.of() : cityCodes.stream()
                .filter(code -> code != null && !code.isBlank()).map(String::trim).distinct().toList();
    }

    private static void validateDates(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
    }
}
