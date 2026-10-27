# 数据库说明

正式环境使用 MySQL 8，演示和测试可使用 H2 的 MySQL 兼容模式。

- 建表脚本：`src/main/resources/db/schema.sql`
- 数据生成器：`src/main/java/com/example/airquality/config/DemoDataInitializer.java`
- 默认数据量：50 个城市 × 2191 天 = 109550 条
- 时间字段：`observed_date`
- 分类字段：`city_id`、`region`、`quality_level`
- 索引：日期索引、城市日期联合索引、质量等级索引

数据为程序生成的教学演示数据，不是国家监测站真实监测值。程序使用固定随机种子，生成过程可以复现。

验证数据量：

```sql
SELECT COUNT(*) AS total_records FROM air_quality_records;
```
