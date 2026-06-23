package com.hdil.saluschart.core.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Which Y-axis a combined series is measured against. */
enum class CombinedAxis { LEFT, RIGHT }

/** Render type of a single series inside a [CombinedSeries] overlay. */
enum class CombinedSeriesType { LINE, BAR, SCATTER, AREA, RANGE_BAR }

/**
 * One overlaid series in a combined chart (see `CombinedChart`).
 *
 * All series share the same X slots: `data[i]` occupies slot `i`. Supply series in the same
 * X order; ideally the same length. A shorter series simply renders fewer marks at the leading
 * slots. Each series is measured against the Y-axis it is bound to via [axis].
 *
 * @param type How this series is drawn (LINE / BAR / SCATTER / AREA / RANGE_BAR).
 * @param data Index-aligned marks. Use [ChartMark] for LINE/BAR/SCATTER/AREA and [RangeChartMark]
 *   for RANGE_BAR (its [RangeChartMark.minPoint]/[RangeChartMark.maxPoint] define the bar span).
 * @param color Series color. [Color.Unspecified] falls back to the chart palette at draw time.
 * @param axis Which Y-axis this series is measured against ([CombinedAxis.LEFT] or [CombinedAxis.RIGHT]).
 * @param label Legend / tooltip label for this series.
 * @param strokeWidth LINE/AREA: line thickness in pixels.
 * @param showPoints LINE/AREA: also draw a marker at each point. (SCATTER always shows markers.)
 * @param areaAlpha AREA: opacity of the filled region below the line (0..1).
 * @param pointType LINE/SCATTER marker shape.
 * @param pointRadius LINE/SCATTER marker radius.
 * @param barWidthRatio BAR/RANGE_BAR: bar width as a fraction of its slot (0..1).
 * @param barCornerRadiusFraction BAR/RANGE_BAR: corner rounding as a fraction of the bar half-width.
 */
data class CombinedSeries(
    val type: CombinedSeriesType,
    val data: List<BaseChartMark>,
    val color: Color = Color.Unspecified,
    val axis: CombinedAxis = CombinedAxis.LEFT,
    val label: String = "",
    // line / area
    val strokeWidth: Float = 4f,
    val showPoints: Boolean = false,
    val areaAlpha: Float = 0.25f,
    // markers (line / scatter)
    val pointType: PointType = PointType.Circle,
    val pointRadius: Dp = 4.dp,
    // bar / range-bar
    val barWidthRatio: Float = 0.8f,
    val barCornerRadiusFraction: Float = 0f,
)
