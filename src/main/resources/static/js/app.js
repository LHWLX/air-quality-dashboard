const state = {
    cities: [],
    records: [],
    stats: [],
    distribution: [],
    ranking: [],
    mapPoints: [],
    currentPage: 1,
    pageSize: 10,
    totalElements: 0,
    totalPages: 1,
    charts: {}
};

const indicatorLabels = {
    PM25: { name: "PM2.5", unit: "μg/m³" },
    PM10: { name: "PM10", unit: "μg/m³" },
    SO2: { name: "SO₂", unit: "μg/m³" },
    NO2: { name: "NO₂", unit: "μg/m³" },
    O3: { name: "O₃", unit: "μg/m³" },
    AQI: { name: "AQI", unit: "" }
};

const chartColors = ["#1f8a70", "#3d7ea6", "#d79d32", "#cc5d3f", "#735d9d", "#668c4a"];

const elements = {
    cityOptions: document.querySelector("#cityOptions"),
    startDate: document.querySelector("#startDate"),
    endDate: document.querySelector("#endDate"),
    applyButton: document.querySelector("#applyButton"),
    resetButton: document.querySelector("#resetButton"),
    filterMeta: document.querySelector("#filterMeta"),
    dateBadge: document.querySelector("#dateBadge"),
    overviewTitle: document.querySelector("#overviewTitle"),
    averageMetric: document.querySelector("#averageMetric"),
    averageUnit: document.querySelector("#averageUnit"),
    goodDayMetric: document.querySelector("#goodDayMetric"),
    recordMetric: document.querySelector("#recordMetric"),
    peakCityMetric: document.querySelector("#peakCityMetric"),
    peakValueMetric: document.querySelector("#peakValueMetric"),
    maximumMetric: document.querySelector("#maximumMetric"),
    maximumUnit: document.querySelector("#maximumUnit"),
    minimumMetric: document.querySelector("#minimumMetric"),
    minimumUnit: document.querySelector("#minimumUnit"),
    trendSubtitle: document.querySelector("#trendSubtitle"),
    indicatorHeader: document.querySelector("#indicatorHeader"),
    tableBody: document.querySelector("#dataTableBody"),
    tableCount: document.querySelector("#tableCount"),
    tableSummary: document.querySelector("#tableSummary"),
    previousPage: document.querySelector("#previousPage"),
    nextPage: document.querySelector("#nextPage"),
    pageIndicator: document.querySelector("#pageIndicator"),
    detailDialog: document.querySelector("#detailDialog"),
    detailTitle: document.querySelector("#detailTitle"),
    detailGrid: document.querySelector("#detailGrid"),
    closeDialog: document.querySelector("#closeDialog"),
    loadingLayer: document.querySelector("#loadingLayer"),
    toast: document.querySelector("#toast"),
    currentTime: document.querySelector("#currentTime")
};

document.addEventListener("DOMContentLoaded", initialize);

async function initialize() {
    setDefaultDates();
    initializeCharts();
    bindEvents();
    updateClock();
    window.setInterval(updateClock, 60_000);

    try {
        setLoading(true);
        state.cities = await requestJson("/api/cities");
        renderCityOptions();
        await refreshDashboard();
    } catch (error) {
        showToast(error.message);
    } finally {
        setLoading(false);
    }
}

function bindEvents() {
    elements.applyButton.addEventListener("click", refreshDashboard);
    elements.resetButton.addEventListener("click", resetFilters);
    elements.previousPage.addEventListener("click", () => changePage(-1));
    elements.nextPage.addEventListener("click", () => changePage(1));
    elements.closeDialog.addEventListener("click", () => elements.detailDialog.close());
    elements.detailDialog.addEventListener("click", event => {
        if (event.target === elements.detailDialog) {
            elements.detailDialog.close();
        }
    });
    elements.tableBody.addEventListener("click", event => {
        const button = event.target.closest("[data-record-id]");
        if (button) {
            showRecordDetail(button.dataset.recordId);
        }
    });

    let resizeTimer;
    window.addEventListener("resize", () => {
        window.clearTimeout(resizeTimer);
        resizeTimer = window.setTimeout(() => Object.values(state.charts).forEach(chart => chart.resize()), 120);
    });
}

function initializeCharts() {
    state.charts.trend = echarts.init(document.querySelector("#trendChart"));
    state.charts.comparison = echarts.init(document.querySelector("#comparisonChart"));
    state.charts.distribution = echarts.init(document.querySelector("#distributionChart"));
    state.charts.ranking = echarts.init(document.querySelector("#rankingChart"));
    state.charts.map = echarts.init(document.querySelector("#mapChart"));
}

function setDefaultDates() {
    const end = new Date();
    const start = new Date();
    start.setDate(end.getDate() - 29);
    elements.startDate.value = toIsoDate(start);
    elements.endDate.value = toIsoDate(end);
}

function renderCityOptions() {
    const fragment = document.createDocumentFragment();
    state.cities.forEach(city => {
        const label = document.createElement("label");
        label.className = "city-option";

        const input = document.createElement("input");
        input.type = "checkbox";
        input.name = "city";
        input.value = city.code;
        input.checked = true;

        const text = document.createElement("span");
        text.textContent = city.name;

        label.append(input, text);
        fragment.append(label);
    });
    elements.cityOptions.replaceChildren(fragment);
}

async function refreshDashboard() {
    const filters = readFilters();
    if (!filters) {
        return;
    }

    try {
        setLoading(true);
        const query = buildQuery(filters);
        const [records, stats, distribution, ranking, mapPoints] = await Promise.all([
            requestJson(`/api/air-quality/trend?${query}`),
            requestJson(`/api/air-quality/stats?${query}`),
            requestJson(`/api/air-quality/distribution?${query}`),
            requestJson(`/api/air-quality/ranking?${query}&limit=10`),
            requestJson(`/api/air-quality/map?${query}`)
        ]);
        state.records = records;
        state.stats = stats;
        state.distribution = distribution;
        state.ranking = ranking;
        state.mapPoints = mapPoints;
        state.currentPage = 1;
        await loadTablePage(query);
        renderDashboard(filters);
    } catch (error) {
        showToast(error.message);
    } finally {
        setLoading(false);
    }
}

function readFilters() {
    const cities = [...document.querySelectorAll('input[name="city"]:checked')].map(input => input.value);
    const indicator = document.querySelector('input[name="indicator"]:checked').value;
    const start = elements.startDate.value;
    const end = elements.endDate.value;

    if (cities.length === 0) {
        showToast("请至少选择一个城市");
        return null;
    }
    if (!start || !end) {
        showToast("请选择完整的开始和结束日期");
        return null;
    }
    if (start > end) {
        showToast("开始日期不能晚于结束日期");
        return null;
    }
    return { cities, indicator, start, end };
}

function buildQuery(filters) {
    return new URLSearchParams({
        cities: filters.cities.join(","),
        indicator: filters.indicator,
        start: filters.start,
        end: filters.end
    }).toString();
}

function renderDashboard(filters) {
    const indicator = indicatorLabels[filters.indicator];
    const cityNames = state.cities
        .filter(city => filters.cities.includes(city.code))
        .map(city => city.name);

    elements.overviewTitle.textContent = `${indicator.name} 空气质量总览`;
    elements.dateBadge.textContent = `${formatShortDate(filters.start)} 至 ${formatShortDate(filters.end)}`;
    elements.filterMeta.textContent = `已选择 ${cityNames.length} 个城市，共 ${state.records.length} 条记录`;
    elements.trendSubtitle.textContent = `${cityNames.join("、")} · ${indicator.name} 日均监测值`;
    elements.indicatorHeader.textContent = indicator.name;

    renderMetrics(indicator);
    renderTrendChart(indicator);
    renderComparisonChart(indicator);
    renderDistributionChart();
    renderRankingChart(indicator);
    renderMapChart(indicator);
    renderTable();
}

function renderMetrics(indicator) {
    const average = state.records.length
        ? state.records.reduce((sum, record) => sum + record.indicatorValue, 0) / state.records.length
        : 0;
    const goodDays = state.records.filter(record => record.qualityLevel === "优" || record.qualityLevel === "良").length;
    const goodDayRatio = state.records.length ? goodDays * 100 / state.records.length : 0;
    const peak = state.stats.reduce((current, item) => !current || item.maximum > current.maximum ? item : current, null);

    elements.averageMetric.textContent = formatNumber(average);
    elements.averageUnit.textContent = indicator.unit || "指数";
    elements.goodDayMetric.textContent = formatNumber(goodDayRatio);
    elements.recordMetric.textContent = state.records.length.toLocaleString("zh-CN");
    elements.peakCityMetric.textContent = peak?.cityName ?? "--";
    elements.peakValueMetric.textContent = peak
        ? `${formatNumber(peak.maximum)} ${indicator.unit}`.trim()
        : "--";
    const maximum = state.stats.length ? Math.max(...state.stats.map(item => item.maximum)) : 0;
    const minimum = state.stats.length ? Math.min(...state.stats.map(item => item.minimum)) : 0;
    elements.maximumMetric.textContent = formatNumber(maximum);
    elements.maximumUnit.textContent = indicator.unit || "指数";
    elements.minimumMetric.textContent = formatNumber(minimum);
    elements.minimumUnit.textContent = indicator.unit || "指数";
}

function renderTrendChart(indicator) {
    const byCity = groupBy(state.records, record => record.cityName);
    const dates = [...new Set(state.records.map(record => record.date))].sort();
    const series = [...byCity.entries()].map(([cityName, records]) => {
        const valueByDate = new Map(records.map(record => [record.date, record.indicatorValue]));
        return {
            name: cityName,
            type: "line",
            smooth: 0.25,
            symbol: "circle",
            symbolSize: 5,
            showSymbol: dates.length <= 31,
            data: dates.map(date => valueByDate.get(date) ?? null),
            connectNulls: false,
            lineStyle: { width: 2 }
        };
    });

    state.charts.trend.setOption({
        color: chartColors,
        animationDuration: 500,
        tooltip: {
            trigger: "axis",
            valueFormatter: value => `${formatNumber(value)} ${indicator.unit}`.trim()
        },
        legend: {
            type: "scroll",
            top: 16,
            right: 20,
            left: 52,
            itemWidth: 16,
            itemHeight: 8,
            textStyle: { color: "#56605d", fontSize: 11 }
        },
        grid: { left: 56, right: 28, top: 58, bottom: 48, containLabel: false },
        xAxis: {
            type: "category",
            boundaryGap: false,
            data: dates.map(formatShortDate),
            axisLine: { lineStyle: { color: "#cfd7d3" } },
            axisTick: { show: false },
            axisLabel: { color: "#77807d", fontSize: 10, hideOverlap: true }
        },
        yAxis: {
            type: "value",
            name: indicator.unit,
            nameTextStyle: { color: "#77807d", fontSize: 10 },
            splitLine: { lineStyle: { color: "#edf0ee" } },
            axisLabel: { color: "#77807d", fontSize: 10 }
        },
        series
    }, true);
}

function renderComparisonChart(indicator) {
    const sortedStats = [...state.stats].sort((a, b) => b.average - a.average);
    state.charts.comparison.setOption({
        color: ["#3d7ea6"],
        animationDuration: 500,
        tooltip: {
            trigger: "axis",
            axisPointer: { type: "shadow" },
            valueFormatter: value => `${formatNumber(value)} ${indicator.unit}`.trim()
        },
        grid: { left: 42, right: 22, top: 24, bottom: 46, containLabel: true },
        xAxis: {
            type: "category",
            data: sortedStats.map(item => item.cityName),
            axisTick: { show: false },
            axisLine: { lineStyle: { color: "#cfd7d3" } },
            axisLabel: { color: "#66706d", fontSize: 11 }
        },
        yAxis: {
            type: "value",
            name: indicator.unit,
            nameTextStyle: { color: "#77807d", fontSize: 10 },
            splitLine: { lineStyle: { color: "#edf0ee" } },
            axisLabel: { color: "#77807d", fontSize: 10 }
        },
        series: [{
            name: "平均值",
            type: "bar",
            barMaxWidth: 42,
            data: sortedStats.map(item => ({
                value: item.average,
                itemStyle: {
                    color: item.average === Math.max(...sortedStats.map(stat => stat.average)) ? "#d79d32" : "#3d7ea6",
                    borderRadius: [3, 3, 0, 0]
                }
            }))
        }]
    }, true);
}

function renderDistributionChart() {
    state.charts.distribution.setOption({
        color: ["#1f8a70", "#d79d32", "#cc5d3f", "#a33b2f"],
        animationDuration: 500,
        tooltip: { trigger: "item", formatter: "{b}<br>{c} 天次 · {d}%" },
        legend: {
            bottom: 17,
            left: "center",
            itemWidth: 10,
            itemHeight: 10,
            textStyle: { color: "#66706d", fontSize: 10 }
        },
        series: [{
            type: "pie",
            radius: ["48%", "70%"],
            center: ["50%", "43%"],
            avoidLabelOverlap: true,
            itemStyle: { borderColor: "#ffffff", borderWidth: 3 },
            label: { show: false },
            emphasis: { label: { show: true, fontSize: 13, fontWeight: 700 } },
            data: state.distribution
        }]
    }, true);
}

function renderRankingChart(indicator) {
    const rows = [...state.ranking].reverse();
    state.charts.ranking.setOption({
        color: ["#d79d32"],
        tooltip: { trigger: "axis", valueFormatter: value => `${formatNumber(value)} ${indicator.unit}`.trim() },
        grid: { left: 68, right: 28, top: 22, bottom: 28, containLabel: true },
        xAxis: { type: "value", splitLine: { lineStyle: { color: "#edf0ee" } } },
        yAxis: { type: "category", data: rows.map(item => item.cityName), axisTick: { show: false } },
        series: [{ type: "bar", data: rows.map(item => item.value), barMaxWidth: 22 }]
    }, true);
}

function renderMapChart(indicator) {
    const values = state.mapPoints.map(item => item.value);
    const min = values.length ? Math.min(...values) : 0;
    const max = values.length ? Math.max(...values) : 1;
    state.charts.map.setOption({
        tooltip: { formatter: item => `${item.data.name}<br>${formatNumber(item.data.value[2])} ${indicator.unit}`.trim() },
        grid: { left: 48, right: 28, top: 26, bottom: 40 },
        xAxis: { name: "经度", min: 85, max: 130, splitLine: { lineStyle: { color: "#edf0ee" } } },
        yAxis: { name: "纬度", min: 18, max: 48, splitLine: { lineStyle: { color: "#edf0ee" } } },
        visualMap: { min, max, show: false, inRange: { color: ["#8fcbb7", "#d79d32", "#a33b2f"] } },
        series: [{ type: "scatter", symbolSize: value => 10 + (value[2] - min) / Math.max(1, max - min) * 18,
            data: state.mapPoints.map(item => ({ name: item.cityName, value: [item.longitude, item.latitude, item.value] })) }]
    }, true);
}

function renderTable() {
    const pageRows = state.pageRecords || [];
    const startIndex = (state.currentPage - 1) * state.pageSize;

    if (pageRows.length === 0) {
        elements.tableBody.innerHTML = '<tr class="empty-row"><td colspan="6">当前筛选条件下没有监测记录</td></tr>';
    } else {
        elements.tableBody.innerHTML = pageRows.map(record => `
            <tr>
                <td>${record.date}</td>
                <td>${escapeHtml(record.cityName)}</td>
                <td><strong>${formatNumber(record.indicatorValue)}</strong> ${escapeHtml(record.unit)}</td>
                <td>${record.aqi}</td>
                <td><span class="level-badge ${levelClass(record.qualityLevel)}">${escapeHtml(record.qualityLevel)}</span></td>
                <td><button class="detail-button" type="button" data-record-id="${record.id}">详情</button></td>
            </tr>
        `).join("");
    }

    elements.tableCount.textContent = `${state.totalElements} 条`;
    elements.tableSummary.textContent = state.totalElements
        ? `显示第 ${startIndex + 1}–${Math.min(startIndex + state.pageSize, state.totalElements)} 条，每页 10 条`
        : "每页显示 10 条记录";
    elements.pageIndicator.textContent = `第 ${state.currentPage} / ${state.totalPages} 页`;
    elements.previousPage.disabled = state.currentPage <= 1;
    elements.nextPage.disabled = state.currentPage >= state.totalPages;
}

async function changePage(offset) {
    state.currentPage += offset;
    const filters = readFilters();
    if (!filters) return;
    await loadTablePage(buildQuery(filters));
    renderTable();
    document.querySelector(".table-panel").scrollIntoView({ behavior: "smooth", block: "start" });
}

async function showRecordDetail(id) {
    try {
        const record = await requestJson(`/api/air-quality/${id}`);
        elements.detailTitle.textContent = `${record.cityName} · ${record.date}`;
        const detailItems = [
            ["PM2.5", `${record.pm25} μg/m³`],
            ["PM10", `${record.pm10} μg/m³`],
            ["SO₂", `${record.so2} μg/m³`],
            ["NO₂", `${record.no2} μg/m³`],
            ["O₃", `${record.o3} μg/m³`],
            ["AQI", record.aqi],
            ["质量等级", record.qualityLevel],
            ["城市代码", record.cityCode],
            ["记录编号", record.id]
        ];
        elements.detailGrid.innerHTML = detailItems.map(([label, value]) => `
            <div class="detail-item"><span>${label}</span><strong>${escapeHtml(String(value))}</strong></div>
        `).join("");
        elements.detailDialog.showModal();
    } catch (error) {
        showToast(error.message);
    }
}

function resetFilters() {
    document.querySelectorAll('input[name="city"]').forEach(input => { input.checked = true; });
    document.querySelector('input[name="indicator"][value="PM25"]').checked = true;
    setDefaultDates();
    refreshDashboard();
}

async function requestJson(url) {
    const response = await fetch(url, { headers: { Accept: "application/json" } });
    if (!response.ok) {
        let message = `请求失败（${response.status}）`;
        try {
            const error = await response.json();
            message = error.message || message;
        } catch (ignored) {
            // The status code is sufficient when the server did not return JSON.
        }
        throw new Error(message);
    }
    const body = await response.json();
    return body.data;
}

async function loadTablePage(query) {
    const page = await requestJson(`/api/air-quality?${query}&page=${state.currentPage}&size=${state.pageSize}&sort=desc`);
    state.pageRecords = page.content;
    state.totalElements = page.totalElements;
    state.totalPages = page.totalPages;
}

function groupBy(items, keySelector) {
    const result = new Map();
    items.forEach(item => {
        const key = keySelector(item);
        if (!result.has(key)) {
            result.set(key, []);
        }
        result.get(key).push(item);
    });
    return result;
}

function setLoading(visible) {
    elements.loadingLayer.classList.toggle("is-visible", visible);
}

function showToast(message) {
    elements.toast.textContent = message;
    elements.toast.classList.add("is-visible");
    window.clearTimeout(showToast.timer);
    showToast.timer = window.setTimeout(() => elements.toast.classList.remove("is-visible"), 3600);
}

function updateClock() {
    elements.currentTime.textContent = new Intl.DateTimeFormat("zh-CN", {
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        hour12: false
    }).format(new Date());
}

function levelClass(level) {
    return {
        优: "level-excellent",
        良: "level-good",
        轻度污染: "level-light",
        中度污染: "level-moderate",
        重度污染: "level-heavy"
    }[level] || "level-good";
}

function formatNumber(value) {
    if (value === null || value === undefined || Number.isNaN(Number(value))) {
        return "--";
    }
    return Number(value).toLocaleString("zh-CN", { maximumFractionDigits: 1 });
}

function formatShortDate(value) {
    const text = typeof value === "string" ? value : toIsoDate(value);
    return text.slice(5).replace("-", "/");
}

function toIsoDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function escapeHtml(value) {
    return value
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

