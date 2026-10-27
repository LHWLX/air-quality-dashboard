CREATE TABLE IF NOT EXISTS cities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(32) NOT NULL UNIQUE,
    region VARCHAR(32) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
    latitude DECIMAL(9, 6) NOT NULL
);

CREATE TABLE IF NOT EXISTS air_quality_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city_id BIGINT NOT NULL,
    observed_date DATE NOT NULL,
    pm25 DECIMAL(8, 1) NOT NULL,
    pm10 DECIMAL(8, 1) NOT NULL,
    so2 DECIMAL(8, 1) NOT NULL,
    no2 DECIMAL(8, 1) NOT NULL,
    o3 DECIMAL(8, 1) NOT NULL,
    aqi INT NOT NULL,
    quality_level VARCHAR(16) NOT NULL,
    CONSTRAINT uk_city_date UNIQUE (city_id, observed_date),
    CONSTRAINT fk_record_city FOREIGN KEY (city_id) REFERENCES cities(id),
    INDEX idx_air_date (observed_date),
    INDEX idx_air_city_date (city_id, observed_date),
    INDEX idx_air_quality_level (quality_level)
);
