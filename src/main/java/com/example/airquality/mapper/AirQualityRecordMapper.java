package com.example.airquality.mapper;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.dto.CityStatsResponse;
import com.example.airquality.dto.DistributionResponse;
import com.example.airquality.dto.MapPointResponse;
import com.example.airquality.dto.RankingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AirQualityRecordMapper {
    long countAll();
    int insert(AirQualityRecord record);
    long countByFilter(@Param("cities") List<String> cities, @Param("start") LocalDate start, @Param("end") LocalDate end);
    List<AirQualityRecord> findPage(@Param("cities") List<String> cities, @Param("start") LocalDate start,
                                    @Param("end") LocalDate end, @Param("indicatorColumn") String indicatorColumn,
                                    @Param("sort") String sort, @Param("offset") int offset, @Param("size") int size);
    List<AirQualityRecord> findTrend(@Param("cities") List<String> cities, @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);
    List<CityStatsResponse> findStats(@Param("cities") List<String> cities, @Param("start") LocalDate start,
                                      @Param("end") LocalDate end, @Param("indicatorColumn") String indicatorColumn,
                                      @Param("indicator") String indicator, @Param("indicatorName") String indicatorName,
                                      @Param("unit") String unit);
    List<DistributionResponse> findDistribution(@Param("cities") List<String> cities, @Param("start") LocalDate start,
                                                 @Param("end") LocalDate end);
    List<RankingResponse> findRanking(@Param("cities") List<String> cities, @Param("start") LocalDate start,
                                      @Param("end") LocalDate end, @Param("indicatorColumn") String indicatorColumn,
                                      @Param("limit") int limit);
    List<MapPointResponse> findMapPoints(@Param("cities") List<String> cities, @Param("start") LocalDate start,
                                         @Param("end") LocalDate end, @Param("indicatorColumn") String indicatorColumn);
    AirQualityRecord findById(@Param("id") Long id);
}
