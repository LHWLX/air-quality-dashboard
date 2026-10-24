package com.example.airquality.config;

import com.example.airquality.domain.AirQualityRecord;
import com.example.airquality.domain.City;
import com.example.airquality.domain.QualityLevel;
import com.example.airquality.repository.AirQualityRecordRepository;
import com.example.airquality.repository.CityRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private static final int DAYS = 120;

    private final CityRepository cityRepository;
    private final AirQualityRecordRepository recordRepository;

    public DemoDataInitializer(CityRepository cityRepository, AirQualityRecordRepository recordRepository) {
        this.cityRepository = cityRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (recordRepository.count() > 0) {
            return;
        }

        List<CityProfile> profiles = List.of(
                new CityProfile("beijing", "北京", "华北", 62, 96, 10, 43, 78),
                new CityProfile("shanghai", "上海", "华东", 39, 63, 7, 34, 92),
                new CityProfile("guangzhou", "广州", "华南", 31, 52, 6, 29, 112),
                new CityProfile("chengdu", "成都", "西南", 51, 79, 8, 38, 76),
                new CityProfile("xian", "西安", "西北", 58, 91, 11, 41, 82),
                new CityProfile("hangzhou", "杭州", "华东", 35, 57, 6, 31, 101)
        );

        List<City> cities = profiles.stream()
                .map(profile -> new City(profile.code(), profile.name(), profile.region()))
                .toList();
        cityRepository.saveAll(cities);

        LocalDate firstDate = LocalDate.now().minusDays(DAYS - 1L);
        Random random = new Random(2026L);
        List<AirQualityRecord> records = new ArrayList<>(profiles.size() * DAYS);

        for (int cityIndex = 0; cityIndex < profiles.size(); cityIndex++) {
            CityProfile profile = profiles.get(cityIndex);
            City city = cities.get(cityIndex);
            for (int day = 0; day < DAYS; day++) {
                double weeklyWave = Math.sin(day * Math.PI / 7.0) * 8;
                double monthlyWave = Math.cos(day * Math.PI / 30.0 + cityIndex) * 11;
                double weatherNoise = random.nextGaussian() * 7;
                double pm25 = positive(profile.pm25() + weeklyWave + monthlyWave + weatherNoise);
                double pm10 = positive(profile.pm10() + weeklyWave * 1.3 + weatherNoise * 1.4);
                double so2 = positive(profile.so2() + random.nextGaussian() * 1.8);
                double no2 = positive(profile.no2() + weeklyWave * 0.35 + random.nextGaussian() * 4.2);
                double ozoneSeason = Math.sin((day + 18) * Math.PI / 60.0) * 18;
                double o3 = positive(profile.o3() + ozoneSeason + random.nextGaussian() * 8);
                int aqi = calculateAqi(pm25, pm10, so2, no2, o3);

                records.add(new AirQualityRecord(
                        city,
                        firstDate.plusDays(day),
                        round(pm25),
                        round(pm10),
                        round(so2),
                        round(no2),
                        round(o3),
                        aqi,
                        QualityLevel.fromAqi(aqi)
                ));
            }
        }

        recordRepository.saveAll(records);
    }

    private static int calculateAqi(double pm25, double pm10, double so2, double no2, double o3) {
        double index = Math.max(pm25 / 75.0 * 100, pm10 / 150.0 * 100);
        index = Math.max(index, so2 / 50.0 * 100);
        index = Math.max(index, no2 / 80.0 * 100);
        index = Math.max(index, o3 / 160.0 * 100);
        return Math.min(300, Math.max(20, (int) Math.round(index)));
    }

    private static double positive(double value) {
        return Math.max(1, value);
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record CityProfile(
            String code,
            String name,
            String region,
            double pm25,
            double pm10,
            double so2,
            double no2,
            double o3
    ) {
    }
}

