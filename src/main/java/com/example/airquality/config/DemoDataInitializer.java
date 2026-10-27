package com.example.airquality.config;

import com.example.airquality.domain.City;
import com.example.airquality.domain.QualityLevel;
import com.example.airquality.mapper.AirQualityRecordMapper;
import com.example.airquality.mapper.CityMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final CityMapper cityMapper;
    private final AirQualityRecordMapper recordMapper;
    private final JdbcTemplate jdbcTemplate;
    private final boolean initialize;
    private final int cityLimit;
    private final int days;
    private final int batchSize;

    public DemoDataInitializer(CityMapper cityMapper, AirQualityRecordMapper recordMapper, JdbcTemplate jdbcTemplate,
            @Value("${app.data.initialize:true}") boolean initialize,
            @Value("${app.data.city-limit:50}") int cityLimit,
            @Value("${app.data.days:2191}") int days,
            @Value("${app.data.batch-size:1000}") int batchSize) {
        this.cityMapper = cityMapper;
        this.recordMapper = recordMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.initialize = initialize;
        this.cityLimit = cityLimit;
        this.days = days;
        this.batchSize = batchSize;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!initialize || recordMapper.countAll() > 0) {
            return;
        }

        List<CityProfile> profiles = profiles().stream().limit(cityLimit).toList();
        List<City> cities = new ArrayList<>();
        for (CityProfile profile : profiles) {
            City city = new City(profile.code(), profile.name(), profile.region(), profile.longitude(), profile.latitude());
            cityMapper.insert(city);
            cities.add(city);
        }

        LocalDate firstDate = LocalDate.now().minusDays(days - 1L);
        Random random = new Random(2026L);
        List<Object[]> batch = new ArrayList<>(batchSize);
        String sql = "INSERT INTO air_quality_records(city_id, observed_date, pm25, pm10, so2, no2, o3, aqi, quality_level) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int cityIndex = 0; cityIndex < cities.size(); cityIndex++) {
            City city = cities.get(cityIndex);
            double regionalFactor = 0.85 + (cityIndex % 9) * 0.045;
            for (int day = 0; day < days; day++) {
                double annual = Math.cos(day * Math.PI * 2 / 365.25) * 15;
                double weekly = Math.sin(day * Math.PI / 7.0 + cityIndex) * 7;
                double noise = random.nextGaussian() * 6;
                double pm25 = positive((42 + annual + weekly + noise) * regionalFactor);
                double pm10 = positive((68 + annual * 1.25 + weekly + noise * 1.4) * regionalFactor);
                double so2 = positive(7 * regionalFactor + random.nextGaussian() * 1.5);
                double no2 = positive((32 + weekly * 0.35 + random.nextGaussian() * 4) * regionalFactor);
                double o3 = positive(92 - annual * 0.8 + random.nextGaussian() * 9);
                int aqi = calculateAqi(pm25, pm10, so2, no2, o3);
                batch.add(new Object[]{city.getId(), Date.valueOf(firstDate.plusDays(day)), round(pm25), round(pm10),
                        round(so2), round(no2), round(o3), aqi, QualityLevel.fromAqi(aqi).getDisplayName()});
                if (batch.size() >= batchSize) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batch);
        }
    }

    private static int calculateAqi(double pm25, double pm10, double so2, double no2, double o3) {
        double index = Math.max(pm25 / 75.0 * 100, pm10 / 150.0 * 100);
        index = Math.max(index, so2 / 50.0 * 100);
        index = Math.max(index, no2 / 80.0 * 100);
        index = Math.max(index, o3 / 160.0 * 100);
        return Math.min(300, Math.max(20, (int) Math.round(index)));
    }

    private static double positive(double value) { return Math.max(1, value); }
    private static double round(double value) { return Math.round(value * 10.0) / 10.0; }

    private static List<CityProfile> profiles() {
        return List.of(
                p("beijing", "北京", "华北", 116.4074, 39.9042), p("tianjin", "天津", "华北", 117.2009, 39.0842),
                p("shijiazhuang", "石家庄", "华北", 114.5149, 38.0428), p("taiyuan", "太原", "华北", 112.5492, 37.8570),
                p("hohhot", "呼和浩特", "华北", 111.7492, 40.8426), p("shenyang", "沈阳", "东北", 123.4315, 41.8057),
                p("dalian", "大连", "东北", 121.6147, 38.9140), p("changchun", "长春", "东北", 125.3235, 43.8171),
                p("harbin", "哈尔滨", "东北", 126.5349, 45.8038), p("shanghai", "上海", "华东", 121.4737, 31.2304),
                p("nanjing", "南京", "华东", 118.7969, 32.0603), p("suzhou", "苏州", "华东", 120.5853, 31.2989),
                p("hangzhou", "杭州", "华东", 120.1551, 30.2741), p("ningbo", "宁波", "华东", 121.5503, 29.8746),
                p("hefei", "合肥", "华东", 117.2272, 31.8206), p("fuzhou", "福州", "华东", 119.2965, 26.0745),
                p("xiamen", "厦门", "华东", 118.0894, 24.4798), p("nanchang", "南昌", "华东", 115.8579, 28.6829),
                p("jinan", "济南", "华东", 117.1205, 36.6512), p("qingdao", "青岛", "华东", 120.3826, 36.0671),
                p("zhengzhou", "郑州", "华中", 113.6254, 34.7466), p("wuhan", "武汉", "华中", 114.3054, 30.5931),
                p("changsha", "长沙", "华中", 112.9388, 28.2282), p("guangzhou", "广州", "华南", 113.2644, 23.1291),
                p("shenzhen", "深圳", "华南", 114.0579, 22.5431), p("zhuhai", "珠海", "华南", 113.5767, 22.2707),
                p("nanning", "南宁", "华南", 108.3669, 22.8170), p("haikou", "海口", "华南", 110.1983, 20.0440),
                p("chongqing", "重庆", "西南", 106.5516, 29.5630), p("chengdu", "成都", "西南", 104.0665, 30.5728),
                p("guiyang", "贵阳", "西南", 106.6302, 26.6470), p("kunming", "昆明", "西南", 102.8329, 24.8801),
                p("lhasa", "拉萨", "西南", 91.1409, 29.6456), p("xian", "西安", "西北", 108.9398, 34.3416),
                p("lanzhou", "兰州", "西北", 103.8343, 36.0611), p("xining", "西宁", "西北", 101.7782, 36.6171),
                p("yinchuan", "银川", "西北", 106.2309, 38.4872), p("urumqi", "乌鲁木齐", "西北", 87.6168, 43.8256),
                p("yichang", "宜昌", "华中", 111.2865, 30.6919), p("luoyang", "洛阳", "华中", 112.4540, 34.6197),
                p("wuxi", "无锡", "华东", 120.3119, 31.4912), p("wenzhou", "温州", "华东", 120.6994, 27.9949),
                p("quanzhou", "泉州", "华东", 118.6757, 24.8741), p("foshan", "佛山", "华南", 113.1214, 23.0215),
                p("dongguan", "东莞", "华南", 113.7518, 23.0207), p("guilin", "桂林", "华南", 110.2902, 25.2736),
                p("mianyang", "绵阳", "西南", 104.6796, 31.4675), p("zunyi", "遵义", "西南", 106.9274, 27.7257),
                p("baotou", "包头", "华北", 109.8403, 40.6574), p("yantai", "烟台", "华东", 121.4479, 37.4638)
        );
    }

    private static CityProfile p(String code, String name, String region, double longitude, double latitude) {
        return new CityProfile(code, name, region, longitude, latitude);
    }

    private record CityProfile(String code, String name, String region, double longitude, double latitude) { }
}
