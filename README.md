# 城市空气质量监测看板

第9组《Web开发技术》结课项目。项目使用程序生成的教学演示数据，展示 50 个城市在 2191 天内的空气质量变化，共 109550 条监测记录。数据不代表国家监测站真实结果。

## 小组成员

- 钱沛桥：数据设计、数据规则与 SQL 说明
- 卢浩：后端接口、数据库访问与项目整合
- 李梦琪：前端页面、图表与交互展示

## 技术环境

- Oracle JDK 25
- Spring Boot 3.5.16
- MyBatis Spring Boot Starter 3.0.5
- MySQL 8（正式运行）
- H2（本地快速演示和自动测试）
- HTML5、CSS3、原生 JavaScript、Fetch API
- ECharts 5.5.1
- Swagger UI、JUnit 5、MockMvc、Maven

## 功能

- 6 个 KPI：平均值、优良天占比、记录数、峰值城市、最高值、最低值
- 5 类图表：折线图、柱状图、环形图、Top N 排行图、经纬度空间散点图
- 城市多选、指标切换、日期筛选、后端分页与排序
- 8 个统一返回 `Result<T>` 的 RESTful API

## 快速演示启动

1. 在 IntelliJ IDEA 中打开本目录，Project SDK 选择 JDK 25。
2. 等待 Maven 下载依赖。
3. 在运行配置的 Program arguments 中填写 `--spring.profiles.active=demo`。
4. 运行 `AirQualityDashboardApplication`。
5. 浏览器访问 `http://localhost:8080`。
6. Swagger 地址为 `http://localhost:8080/swagger-ui.html`。

`demo` 配置使用 H2 内存数据库，默认生成 50 × 2191 = 109550 条数据。首次启动需要等待数据写入完成，控制台出现 `Started AirQualityDashboardApplication` 后再打开网页。

## MySQL 8 启动

先创建数据库：

```sql
CREATE DATABASE air_quality_dashboard
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
```

然后配置环境变量：

```text
DB_URL=jdbc:mysql://localhost:3306/air_quality_dashboard?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
DB_USERNAME=root
DB_PASSWORD=你的MySQL密码
```

直接运行 `AirQualityDashboardApplication`。程序会执行 `src/main/resources/db/schema.sql` 建表，并在空表时生成演示数据。

## 测试与打包

在 IntelliJ IDEA 的 Maven 面板依次运行 `clean`、`test`、`package`，或者在已配置 Maven 的终端执行 `mvn clean test package`。

自动测试使用 H2，生成 6 个城市、120 天、720 条记录，覆盖初始化、分页筛选、统计、分布、排行、地图、详情和错误参数。

## 主要接口

| 方法 | 路径 | 作用 |
|---|---|---|
| GET | `/api/cities` | 城市列表 |
| GET | `/api/air-quality` | 明细分页查询 |
| GET | `/api/air-quality/trend` | 趋势数据 |
| GET | `/api/air-quality/stats` | KPI 与城市统计 |
| GET | `/api/air-quality/distribution` | 等级分布 |
| GET | `/api/air-quality/ranking` | 城市排行 |
| GET | `/api/air-quality/map` | 空间分布 |
| GET | `/api/air-quality/{id}` | 单条详情 |

## 数据说明

生成器使用固定随机种子 `2026`，按城市地区系数、年度周期、周周期和随机天气扰动生成 PM2.5、PM10、SO2、NO2、O3，再根据阈值折算 AQI 和质量等级。完整规则与清洗说明见提交包 `sql/数据生成与清洗说明.md`。
