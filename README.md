# 城市空气质量监测看板

这是一个城市空气质量数据看板。项目使用 Spring Boot 3、JDK 25、Spring Data JPA、H2、原生 HTML/CSS/JavaScript、fetch 和 ECharts，实现多城市筛选、日期筛选、指标切换、趋势分析、城市对比、质量等级分布、明细分页和单条记录查询。

## 运行效果

- 看板首页：`http://localhost:8080/`
- Swagger：`http://localhost:8080/swagger-ui.html`
- H2 控制台：`http://localhost:8080/h2-console`
- JDBC URL：`jdbc:h2:file:./data/airquality`
- 用户名：`sa`
- 密码：留空

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3.5.16、Spring Web、Spring Data JPA |
| Java | Oracle JDK 25 LTS |
| 数据库 | H2 文件数据库 |
| API 文档 | springdoc OpenAPI、Swagger UI |
| 前端 | HTML5、CSS3、JavaScript ES6+、fetch |
| 图表 | ECharts 5.5.1，本地 WebJar 资源 |
| 测试 | JUnit 5、Spring Boot Test、MockMvc |

## 快速启动

### 方式一 使用 IntelliJ IDEA

1. 用 IntelliJ IDEA 打开本项目根目录。
2. 确认 Project SDK 为 JDK 25。
3. 等待 Maven 依赖下载完成。
4. 运行 `AirQualityDashboardApplication`。
5. 浏览器访问 `http://localhost:8080/`。

### 方式二 使用 Maven

```powershell
mvn spring-boot:run
```

也可以先打包再运行：

```powershell
mvn test package
java -jar target/air-quality-dashboard-1.0.0.jar
```

首次启动会创建 `data/airquality.mv.db`，并向数据库写入 6 个城市、连续 120 天、共 720 条空气质量记录。再次启动不会重复写入。

## 功能完成情况

| 编号 | 要求 | 实现位置 |
| --- | --- | --- |
| F1 | 看板首页 | 顶部状态栏、左侧筛选、主区域摘要和图表 |
| F2 | 至少 5 个城市 | 北京、上海、广州、成都、西安、杭州，可多选 |
| F3 | 起止日期 | 默认最近 30 天，校验日期先后关系 |
| F4 | 指标切换 | PM2.5、PM10、SO₂、NO₂、O₃、AQI |
| F5 | 趋势折线图 | 按城市绘制每日指标变化，多系列联动 |
| F6 | 城市对比柱状图 | 对比所选时段各城市指标平均值 |
| F7 | 等级分布 | 环形图展示优良、轻度、中度、重度污染占比 |
| F8 | 数据表格 | 每页 10 条、上一页/下一页、单条详情弹窗 |
| A1 | `GET /api/cities` | 返回城市编号、代码、名称和区域 |
| A2 | `GET /api/air-quality` | 支持多城市、指标、起止日期组合查询 |
| A3 | `GET /api/air-quality/stats` | 返回平均值、最大值、最小值、优良天占比 |
| A4 | `GET /api/air-quality/{id}` | 返回单条记录全部污染物字段 |

## API 示例

查询北京和上海最近 30 天的 PM2.5：

```http
GET /api/air-quality?cities=beijing,shanghai&indicator=PM25&start=2026-09-25&end=2026-10-24
```

可用指标：`PM25`、`PM10`、`SO2`、`NO2`、`O3`、`AQI`。

错误参数会返回统一格式：

```json
{
  "timestamp": "2026-10-24T19:00:00",
  "status": 400,
  "message": "不支持的指标：CO2",
  "path": "/api/air-quality"
}
```

## 数据说明

本项目使用合理模拟数据，不从静态 JSON 或 CSV 直接返回。首次启动时，`DemoDataInitializer` 按以下规则生成数据并通过 JPA 写入 H2 数据库：

- 城市基线体现不同地区污染物水平差异。
- 正弦和余弦周期模拟周变化与月变化。
- 固定随机种子 `2026` 产生可复现的天气扰动。
- AQI 根据五项污染物相对阈值计算，并映射为质量等级。
- 生成日期始终覆盖启动当天及此前 119 天，因此默认最近 30 天一定有数据。

更详细的数据库说明见 `database/README.md`。

## 项目结构

```text
src/main/java/com/example/airquality
├─ config       数据初始化、CORS、OpenAPI
├─ controller   REST 接口和统一异常处理
├─ domain       JPA 实体与空气质量等级
├─ dto          API 返回对象
├─ repository   数据访问层
└─ service      筛选、指标转换和统计逻辑

src/main/resources
├─ application.yml
└─ static
   ├─ index.html
   ├─ css/app.css
   └─ js/app.js
```

## 测试

```powershell
mvn test
```

集成测试会启动 Spring 上下文和内存 H2 数据库，验证：

- 初始化 6 个城市和 720 条记录。
- 两城市 30 天筛选返回 60 条记录。
- 指标切换正确返回动态指标值。
- 统计接口按城市分组并返回 30 天统计。
- 详情接口和非法指标错误响应正常。

## Git 提交要求

仓库已经包含不少于 10 次有明确说明的提交。当前仓库使用占位作者，请在推送前改为自己的身份：

```powershell
git config user.name "你的姓名"
git config user.email "你的邮箱"
```

查看提交记录：

```powershell
git log --oneline
```

## 你提交前必须完成的内容

1. 把 README、PPT 和 AI 使用记录中的姓名、班级、学号、组员信息补全。
2. 安装 JDK 25，并在自己的电脑上执行 `mvn test package`。
3. 启动项目，打开首页、Swagger 和 H2 控制台各检查一次。
4. 在浏览器开发者工具中检查 1440px、768px、390px 三种宽度。
5. 选择两个城市和一个污染物，确认三类图表和表格同步变化。
6. 点击一条记录的“详情”，确认 A4 接口结果正常。
7. 把仓库推送到自己的 GitHub、Gitee 或学校 Git 服务。
8. 按 `docs/提交与答辩流程.md` 录制演示或参加现场答辩。

## 常见问题

**8080 端口被占用**

修改 `application.yml` 中的 `server.port`，例如改为 `8081`。

**想重新生成 720 条数据**

停止项目后删除 `data` 目录，再次启动。不要在项目运行时删除数据库文件。

**图表为空**

确认所选日期在最近 120 天内，并检查浏览器网络面板中的 `/api/air-quality` 请求。

