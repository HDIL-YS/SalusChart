# CombinedChart

Overlay multiple series of different types — line, bar, scatter, area, range bar — on a single shared X axis, with an optional second (right) Y-axis so metrics with different units can share one chart (e.g. steps as bars and heart rate as a line).

**Import:** `com.hdil.saluschart.ui.compose.charts.CombinedChart`

## Signature

```kotlin
@Composable
fun CombinedChart(
    modifier: Modifier = Modifier,
    series: List<CombinedSeries>,
    xLabels: List<String> = emptyList(),
    title: String = "Combined Chart",
    showTitle: Boolean = true,
    xLabel: String = "",
    leftAxisLabel: String = "",
    leftMinY: Double? = null,
    leftMaxY: Double? = null,
    leftTickStep: Double? = null,
    showLeftAxis: Boolean = true,
    rightAxisLabel: String = "",
    rightMinY: Double? = null,
    rightMaxY: Double? = null,
    rightTickStep: Double? = null,
    showRightAxis: Boolean = true,
    showGrid: Boolean = true,
    xLabelTextSize: Float = 28f,
    xLabelAutoSkip: Boolean = true,
    maxXTicksLimit: Int? = null,
    showLegend: Boolean = true,
    legendPosition: LegendPosition = LegendPosition.BOTTOM,
    enableTooltip: Boolean = true,
    yAxisPaneWidth: Dp = 40.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
)
```

Each entry in `series` is a `CombinedSeries` describing one overlaid layer:

```kotlin
data class CombinedSeries(
    val type: CombinedSeriesType,          // LINE | BAR | SCATTER | AREA | RANGE_BAR
    val data: List<BaseChartMark>,      // ChartMark, or RangeChartMark for RANGE_BAR
    val color: Color = Color.Unspecified,
    val axis: CombinedAxis = CombinedAxis.LEFT,   // LEFT or RIGHT Y-axis
    val label: String = "",
    val strokeWidth: Float = 4f,        // line / area
    val showPoints: Boolean = false,    // line / area: draw a marker at each point
    val areaAlpha: Float = 0.25f,       // area fill opacity
    val pointType: PointType = PointType.Circle,
    val pointRadius: Dp = 4.dp,
    val barWidthRatio: Float = 0.8f,    // bar / range bar
    val barCornerRadiusFraction: Float = 0f,
)
```

Series are **index-aligned**: `data[i]` in every series occupies slot `i`, so supply each series in the same X order (ideally the same length). A shorter series simply draws fewer marks at the leading slots.

## Basic example — dual axis (bar + line)

Steps as bars on the left axis, heart rate as a line on the right axis.

![CombinedChart dual-axis example](/charts/combined-chart-basic.png)

```kotlin
import com.hdil.saluschart.core.chart.ChartMark
import com.hdil.saluschart.core.chart.CombinedAxis
import com.hdil.saluschart.core.chart.CombinedSeries
import com.hdil.saluschart.core.chart.CombinedSeriesType
import com.hdil.saluschart.ui.compose.charts.CombinedChart

CombinedChart(
    modifier = Modifier.fillMaxWidth().height(320.dp),
    series = listOf(
        CombinedSeries(
            type = CombinedSeriesType.BAR,
            data = steps,                 // List<ChartMark>
            color = Color(0xFF7C4DFF),
            axis = CombinedAxis.LEFT,
            label = "Steps",
            barCornerRadiusFraction = 0.3f,
        ),
        CombinedSeries(
            type = CombinedSeriesType.LINE,
            data = heartRate,             // List<ChartMark>
            color = Color(0xFFE91E63),
            axis = CombinedAxis.RIGHT,
            label = "Heart rate",
            showPoints = true,
        ),
    ),
    title = "Steps + Heart Rate",
    leftAxisLabel = "Steps",
    rightAxisLabel = "bpm",
    rightMinY = 60.0,
    rightMaxY = 100.0,
)
```

> Every example below is runnable in the sample app — **Examples** tab, entries ending in "Combined Chart". Series of different lengths stay aligned: each series fills the leading slots, so a shorter line lines up with its matching bars instead of stretching across the whole width.

## Area + line (dual axis)

Use `CombinedSeriesType.AREA` for a filled line (`areaAlpha` controls fill opacity). Here steps are a filled area on the left axis and heart rate is a line on the right.

![CombinedChart area + line](/charts/combined-chart-area.png)

```kotlin
CombinedChart(
    modifier = Modifier.fillMaxWidth().height(340.dp),
    series = listOf(
        CombinedSeries(CombinedSeriesType.AREA, steps, color = Color(0xFF7C4DFF), axis = CombinedAxis.LEFT, label = "Steps", areaAlpha = 0.3f),
        CombinedSeries(CombinedSeriesType.LINE, heartRate, color = Color(0xFFE91E63), axis = CombinedAxis.RIGHT, label = "Heart rate", showPoints = true),
    ),
    leftAxisLabel = "Steps",
    rightAxisLabel = "bpm",
    rightMinY = 60.0, rightMaxY = 100.0,
)
```

## Range bar + average (single axis)

A `RANGE_BAR` series takes `RangeChartMark`s and draws a min–max bar per slot — combine it with a line for a daily heart-rate range plus its average. With no `RIGHT` series the right axis pane is hidden automatically.

![CombinedChart range bar + average](/charts/combined-chart-range.png)

```kotlin
import com.hdil.saluschart.core.chart.RangeChartMark
import com.hdil.saluschart.core.chart.toRangeChartMarksByXGroup

// Group raw min/max samples into one RangeChartMark per x, then derive a midpoint line.
val hrRange = rawHrSamples.toRangeChartMarksByXGroup()    // List<RangeChartMark>
val hrAvg = hrRange.map { ChartMark(it.x, (it.minPoint.y + it.maxPoint.y) / 2.0, it.label) }

CombinedChart(
    series = listOf(
        CombinedSeries(CombinedSeriesType.RANGE_BAR, hrRange, color = Color(0xFF90CAF9), label = "HR range", barWidthRatio = 0.5f, barCornerRadiusFraction = 0.5f),
        CombinedSeries(CombinedSeriesType.LINE, hrAvg, color = Color(0xFFE91E63), label = "Average", showPoints = true),
    ),
    leftAxisLabel = "bpm",
    showRightAxis = false,
)
```

## Single axis — bars with a goal line

Bind every series to `CombinedAxis.LEFT` and set `showRightAxis = false` for a single-axis `CombinedChart` — e.g. step bars with a constant daily-goal line.

```kotlin
val goal = steps.map { ChartMark(it.x, 8000.0, it.label) }

CombinedChart(
    series = listOf(
        CombinedSeries(CombinedSeriesType.BAR, steps, color = Color(0xFF7C4DFF), axis = CombinedAxis.LEFT, label = "Steps", barCornerRadiusFraction = 0.3f),
        CombinedSeries(CombinedSeriesType.LINE, goal, color = Color(0xFFFF6D00), axis = CombinedAxis.LEFT, label = "Goal (8k)", strokeWidth = 5f),
    ),
    leftAxisLabel = "Steps",
    showRightAxis = false,
)
```

## Two lines on a dual axis

Series may be the same type. Weight (kg) and heart rate (bpm) as two lines on independent axes.

```kotlin
CombinedChart(
    series = listOf(
        CombinedSeries(CombinedSeriesType.LINE, weight, color = Color(0xFF009688), axis = CombinedAxis.LEFT, label = "Weight", showPoints = true),
        CombinedSeries(CombinedSeriesType.LINE, heartRate, color = Color(0xFFE91E63), axis = CombinedAxis.RIGHT, label = "Heart rate", showPoints = true),
    ),
    leftAxisLabel = "kg",
    rightAxisLabel = "bpm",
    rightMinY = 60.0, rightMaxY = 100.0,
)
```

## Scatter series

Overlay individual readings with a `SCATTER` series — e.g. weight readings on a steps chart. Tapping a slot shows a tooltip listing every series' value at that point.

```kotlin
CombinedChart(
    series = listOf(
        CombinedSeries(CombinedSeriesType.BAR, steps, color = Color(0xFF7C4DFF), axis = CombinedAxis.LEFT, label = "Steps"),
        CombinedSeries(
            CombinedSeriesType.SCATTER, weight,
            color = Color(0xFF009688), axis = CombinedAxis.RIGHT, label = "Weight",
            pointType = PointType.Triangle, pointRadius = 5.dp,
        ),
    ),
    leftAxisLabel = "Steps",
    rightAxisLabel = "kg",
)
```

## Parameters

#### Data & labels

| Parameter | Type | Default | Description |
|---|---|---|---|
| `series` | `List<CombinedSeries>` | — | Series to overlay (required) |
| `xLabels` | `List<String>` | `emptyList()` | Per-slot X labels; falls back to the longest series' labels |
| `title` | `String` | `"Combined Chart"` | Chart title |
| `showTitle` | `Boolean` | `true` | Whether to render `title` |
| `xLabel` | `String` | `""` | X-axis caption shown centered below the plot |

#### Left axis

| Parameter | Type | Default | Description |
|---|---|---|---|
| `leftAxisLabel` | `String` | `""` | Vertical caption for the left Y-axis |
| `leftMinY` | `Double?` | `null` | Lower bound; computed from left series when `null` |
| `leftMaxY` | `Double?` | `null` | Upper bound; computed when `null` |
| `leftTickStep` | `Double?` | `null` | Fixed tick interval; auto when `null` |
| `showLeftAxis` | `Boolean` | `true` | Whether to draw the left axis pane |

#### Right axis

| Parameter | Type | Default | Description |
|---|---|---|---|
| `rightAxisLabel` | `String` | `""` | Vertical caption for the right Y-axis |
| `rightMinY` | `Double?` | `null` | Lower bound for the right axis |
| `rightMaxY` | `Double?` | `null` | Upper bound for the right axis |
| `rightTickStep` | `Double?` | `null` | Fixed tick interval; auto when `null` |
| `showRightAxis` | `Boolean` | `true` | Draw the right axis pane (auto-hidden when no `RIGHT` series) |

#### Appearance & interaction

| Parameter | Type | Default | Description |
|---|---|---|---|
| `showGrid` | `Boolean` | `true` | Horizontal grid lines (keyed to the left axis ticks) |
| `xLabelTextSize` | `Float` | `28f` | X-axis tick label text size (px) |
| `xLabelAutoSkip` | `Boolean` | `true` | Auto-skip overlapping X labels |
| `maxXTicksLimit` | `Int?` | `null` | Cap on the number of X tick labels |
| `showLegend` | `Boolean` | `true` | Show a legend of labelled series |
| `legendPosition` | `LegendPosition` | `BOTTOM` | Legend placement |
| `enableTooltip` | `Boolean` | `true` | Tap a slot to show every series' value there |
| `yAxisPaneWidth` | `Dp` | `40.dp` | Width reserved for each Y-axis pane |
| `contentPadding` | `PaddingValues` | `PaddingValues(16.dp)` | Padding around the chart |

#### CombinedSeries fields

| Field | Type | Default | Description |
|---|---|---|---|
| `type` | `CombinedSeriesType` | — | `LINE`, `BAR`, `SCATTER`, `AREA`, or `RANGE_BAR` |
| `data` | `List<BaseChartMark>` | — | `ChartMark` per slot; `RangeChartMark` for `RANGE_BAR` |
| `color` | `Color` | theme palette | Series color |
| `axis` | `CombinedAxis` | `LEFT` | Which Y-axis the series is measured against |
| `label` | `String` | `""` | Legend / tooltip label |
| `strokeWidth` | `Float` | `4f` | Line/area thickness (px) |
| `showPoints` | `Boolean` | `false` | Line/area: draw a marker at each point |
| `areaAlpha` | `Float` | `0.25f` | Area fill opacity |
| `pointType` | `PointType` | `Circle` | Line/scatter marker shape |
| `pointRadius` | `Dp` | `4.dp` | Line/scatter marker radius |
| `barWidthRatio` | `Float` | `0.8f` | Bar/range-bar width as a fraction of slot width |
| `barCornerRadiusFraction` | `Float` | `0f` | Bar/range-bar corner rounding |

## Notes & limitations

- **At most one bar-family series** (`BAR` or `RANGE_BAR`) per chart in this version; grouped/side-by-side bars are planned.
- A bar-family axis is forced to start at zero so bars don't float. Bind bars to their own axis (or keep that axis zero-based) when mixing with a line.
- Paging, scrolling, and reference lines are not yet supported on CombinedChart — use the dedicated single-type charts for those.
