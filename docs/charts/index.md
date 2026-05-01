# Charts Overview

SalusChart provides chart composables in two tiers. Full charts support detailed health views, while minimal charts support compact dashboard and wearable-style summaries.

- **Full charts** — titled, axes, legends, tooltips, paging/scrolling
- **Minimal charts** — compact, card-sized, no title or axes; suitable for dashboards

## Full charts

| Composable | Data type | Health data examples |
|---|---|---|
| [BarChart](./bar-chart) | `List<ChartMark>` | Steps, active energy, exercise minutes |
| [LineChart](./line-chart) | `List<ChartMark>` | Weight, energy level, resting heart rate |
| [ScatterPlot](./scatter-plot) | `List<ChartMark>` | Blood pressure or glucose samples |
| [RangeBarChart](./range-bar-chart) | `List<RangeChartMark>` | Heart rate range, blood oxygen range, sleep range |
| [HorizontalRangeBarChart](./horizontal-charts#horizontalrangebarchart) | `List<RangeChartMark>` | Sleep start/end windows, daily range summaries |
| [StackedBarChart](./stacked-bar-chart) | `List<ChartMark>` | Sleep duration by stage, nutrition segments |
| [HorizontalStackedBarChartList](./horizontal-charts#horizontalstackedbarchartlist) | custom | Macronutrients, activity category totals |
| [PieChart](./pie-chart) | `List<ChartMark>` | Nutrition distribution |
| [ProgressChart](./progress-chart) | `List<ProgressChartMark>` | Daily goals, activity rings, hydration progress |
| [SleepStageChart](./sleep-stage-chart) | `SleepSession` | Awake, REM, light/core, and deep sleep |
| [CalendarChart](./calendar-chart) | `List<CalendarEntry>` | Activity overview, habit completion, symptom intensity |
| [PagedCalendarChart](./calendar-chart#pagedcalendarchart) | `List<CalendarEntry>` | Multi-month activity or adherence history |
| [MultiSegmentGaugeChart](./gauge-charts#multisegmentgaugechart) | `Float?` | Stress level, health score, AGEs index |
| [RangeGaugeChart](./gauge-charts#rangegaugechart) | range values | Blood glucose or vital range status |
| [MiniActivityRings](./minimal-charts#miniactivityrings) | `List<ProgressChartMark>` | Move, exercise, and stand goals |

## Minimal charts

| Composable | Description |
|---|---|
| [MinimalBarChart](./minimal-charts#minimalbar) | Compact bar chart |
| [MinimalLineChart](./minimal-charts#minimalline) | Compact line chart |
| [MinimalRangeBarChart](./minimal-charts#minimalrangebar) | Compact range bar |
| [MinimalProgressBar](./minimal-charts#minimalprogress) | Single progress bar |
| [MinimalGaugeChart](./minimal-charts#minimalgauge) | Compact gauge |
| [MinimalMultiSegmentGauge](./minimal-charts#minimalmultisegment) | Compact segmented gauge |
| [MinimalSleepChart](./minimal-charts#minimalsleep) | Compact sleep timeline |
| [MinimalSleepStageChart](./minimal-charts#minimalsleepstage) | Compact sleep stage |
| [MinimalHorizontalStackedBar](./minimal-charts#minimalhorizontalstacked) | Compact stacked row |
| [MinimalLadderChart](./minimal-charts#minimalladder) | Compact ladder chart |

## Wear OS charts

See [Wear OS Charts](./wear-os-charts) for the `ui:wear-compose` module.
