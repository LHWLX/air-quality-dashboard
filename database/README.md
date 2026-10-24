# 数据库说明

## 数据库配置

- 类型：H2 文件数据库
- JDBC URL：`jdbc:h2:file:./data/airquality`
- 用户名：`sa`
- 密码：空
- 表结构维护：Hibernate `ddl-auto=update`

## 数据表

### cities

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| code | 城市英文代码，唯一 |
| name | 城市中文名，唯一 |
| region | 所属区域 |

### air_quality_records

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| city_id | 城市外键 |
| observed_date | 监测日期 |
| pm25 | PM2.5 浓度 |
| pm10 | PM10 浓度 |
| so2 | SO₂ 浓度 |
| no2 | NO₂ 浓度 |
| o3 | O₃ 浓度 |
| aqi | 空气质量指数 |
| quality_level | 空气质量等级枚举 |

`city_id + observed_date` 有唯一约束，保证同一城市每天只有一条记录。

## 初始化方式

项目首次启动时，`DemoDataInitializer` 创建 6 个城市和 720 条记录，并调用 Repository 写入数据库。API 每次查询都从 H2 数据库读取，不直接读取静态文件，符合课程技术考核要求。

## H2 控制台

启动项目后访问 `http://localhost:8080/h2-console`，输入上面的 JDBC URL、用户名和密码即可查看数据。

可执行以下 SQL 验证数据量：

```sql
SELECT COUNT(*) FROM cities;
SELECT COUNT(*) FROM air_quality_records;
SELECT c.name, COUNT(*)
FROM air_quality_records r
JOIN cities c ON c.id = r.city_id
GROUP BY c.name;
```

