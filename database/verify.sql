SELECT COUNT(*) AS total_records FROM air_quality_records;
SELECT COUNT(DISTINCT city_id) AS city_count FROM air_quality_records;
SELECT MIN(observed_date) AS first_date, MAX(observed_date) AS last_date FROM air_quality_records;
SELECT quality_level, COUNT(*) AS level_count
FROM air_quality_records
GROUP BY quality_level
ORDER BY level_count DESC;
