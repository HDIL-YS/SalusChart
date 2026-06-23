package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hdil.saluschart.core.chart.BaseChartMark
import com.hdil.saluschart.core.chart.ChartMark
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.CombinedAxis
import com.hdil.saluschart.core.chart.CombinedSeries
import com.hdil.saluschart.core.chart.CombinedSeriesType
import com.hdil.saluschart.core.chart.RangeChartMark
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartDraw.ChartLegend
import com.hdil.saluschart.core.chart.chartDraw.LegendPosition
import com.hdil.saluschart.core.chart.chartDraw.TooltipContainer
import com.hdil.saluschart.core.chart.chartDraw.VerticalAxisLabel
import com.hdil.saluschart.core.chart.chartDraw.YAxisPosition
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.ui.theme.LocalSalusChartColors

// Vertical room (px) reserved below the plot so x-axis tick labels (drawn at chartBottom + 50f)
// stay inside the canvas. Shared by the plot and the side axis panes so their plot rects match.
private const val COMBINED_PADDING_BOTTOM = 64f

/**
 * A combined chart that overlays multiple series of different types
 * (line, bar, scatter, area, range bar) on a single shared X axis, with an optional
 * second (right) Y-axis so series with different units/scales can be compared.
 *
 * Series are index-aligned (`data[i]` occupies slot `i`) and each is measured against the
 * Y-axis it binds to via [CombinedSeries.axis]. The left and right axes share one plot rectangle,
 * so bars, lines and points always line up on the same slot centers.
 *
 * v1 limitations: at most one bar-family series (BAR or RANGE_BAR); no paging/scrolling.
 *
 * @param modifier Modifier applied to the outer container.
 * @param series Series to overlay (see [CombinedSeries]).
 * @param xLabels Optional per-slot X labels; falls back to the longest series' labels.
 * @param title Chart title shown above the plot when [showTitle] is true.
 * @param showTitle Whether to render [title].
 * @param xLabel Optional X-axis caption shown centered below the plot.
 * @param leftAxisLabel Vertical caption for the left Y-axis.
 * @param leftMinY Optional lower bound for the left axis; computed from left series when null.
 * @param leftMaxY Optional upper bound for the left axis; computed when null.
 * @param leftTickStep Fixed tick interval for the left axis; auto when null.
 * @param showLeftAxis Whether to draw the left Y-axis pane.
 * @param rightAxisLabel Vertical caption for the right Y-axis.
 * @param rightMinY Optional lower bound for the right axis.
 * @param rightMaxY Optional upper bound for the right axis.
 * @param rightTickStep Fixed tick interval for the right axis; auto when null.
 * @param showRightAxis Whether to draw the right axis pane (auto-hidden when no RIGHT series).
 * @param showGrid Whether to draw horizontal grid lines (keyed to the left axis ticks).
 * @param xLabelTextSize Text size (px) for X-axis tick labels.
 * @param xLabelAutoSkip Whether to auto-skip overlapping X labels.
 * @param maxXTicksLimit Optional cap on the number of X tick labels.
 * @param showLegend Whether to show a legend of labelled series.
 * @param legendPosition Legend placement.
 * @param enableTooltip When true, tapping a slot shows a tooltip listing every series' value there.
 * @param yAxisPaneWidth Width reserved for each Y-axis pane.
 * @param contentPadding Padding around the whole chart.
 */
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
) {
    if (series.isEmpty()) return
    require(series.count { it.type == CombinedSeriesType.BAR || it.type == CombinedSeriesType.RANGE_BAR } <= 1) {
        "CombinedChart v1 supports at most one bar-family series (BAR or RANGE_BAR); grouped bars are not yet supported."
    }

    val scheme = LocalSalusChartColors.current
    val palette = scheme.palette
    val resolvedColors = remember(series, scheme) {
        series.mapIndexed { i, s ->
            if (s.color != Color.Unspecified) s.color
            else palette.getOrElse(i % palette.size.coerceAtLeast(1)) { scheme.primary }
        }
    }

    val leftSeries = series.filter { it.axis == CombinedAxis.LEFT }
    val rightSeries = series.filter { it.axis == CombinedAxis.RIGHT }
    val hasRight = rightSeries.isNotEmpty() && showRightAxis
    val hasLeft = leftSeries.isNotEmpty() && showLeftAxis

    fun axisValues(list: List<CombinedSeries>): List<Double> = list.flatMap { s ->
        s.data.flatMap { m ->
            if (m is RangeChartMark) listOf(m.minPoint.y, m.maxPoint.y) else listOf(m.y)
        }
    }
    // Bar-family axes must be zero-based so bars don't float.
    fun axisChartType(list: List<CombinedSeries>) =
        if (list.any { it.type == CombinedSeriesType.BAR || it.type == CombinedSeriesType.RANGE_BAR })
            com.hdil.saluschart.core.chart.ChartType.BAR
        else com.hdil.saluschart.core.chart.ChartType.LINE

    val leftRange = remember(series, leftMinY, leftMaxY, leftTickStep) {
        ChartMath.computeYAxisRange(
            values = axisValues(leftSeries).ifEmpty { listOf(0.0, 1.0) },
            chartType = axisChartType(leftSeries),
            minY = leftMinY, maxY = leftMaxY, fixedTickStep = leftTickStep
        )
    }
    val rightRange = remember(series, rightMinY, rightMaxY, rightTickStep) {
        if (rightSeries.isEmpty()) null
        else ChartMath.computeYAxisRange(
            values = axisValues(rightSeries).ifEmpty { listOf(0.0, 1.0) },
            chartType = axisChartType(rightSeries),
            minY = rightMinY, maxY = rightMaxY, fixedTickStep = rightTickStep
        )
    }

    val effectiveXLabels = remember(xLabels, series) {
        xLabels.ifEmpty {
            series.maxByOrNull { it.data.size }?.data?.map { it.label ?: it.x.toString() } ?: emptyList()
        }
    }
    val slotCount = maxOf(effectiveXLabels.size, series.maxOf { it.data.size }).coerceAtLeast(1)

    var plotSize by remember { mutableStateOf(Size.Zero) }
    var baseMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.padding(contentPadding)) {
        if (showTitle) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        BoxWithConstraints(Modifier.weight(1f)) {
            Row(Modifier.fillMaxSize()) {
                // ---- LEFT axis pane ----
                if (hasLeft && leftAxisLabel.isNotBlank()) VerticalAxisLabel(leftAxisLabel)
                if (hasLeft) {
                    Canvas(
                        Modifier.width(yAxisPaneWidth).fillMaxHeight().clipToBounds()
                    ) {
                        val m = paneMetrics(size).copy(yAxisRange = leftRange)
                        ChartDraw.drawYAxisStandalone(this, m, YAxisPosition.LEFT, size.width)
                    }
                }

                // ---- PLOT ----
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(enableTooltip, slotCount) {
                            if (enableTooltip) detectTapGestures { o ->
                                val w = size.width.toFloat()
                                if (w > 0f) {
                                    val idx = (o.x / (w / slotCount)).toInt().coerceIn(0, slotCount - 1)
                                    selectedIndex = if (selectedIndex == idx) null else idx
                                }
                            }
                        }
                ) {
                    // Helper: per-axis metrics sharing the same plot rect.
                    fun axisMetrics(base: ChartMath.ChartMetrics, s: CombinedSeries) =
                        base.copy(yAxisRange = if (s.axis == CombinedAxis.RIGHT) (rightRange ?: leftRange) else leftRange)

                    // ---- Layer 1 (bottom): grid + selected guide + AREA fills + X labels ----
                    Canvas(Modifier.fillMaxSize()) {
                        plotSize = size
                        val base = ChartMath.computeMetrics(
                            size = size,
                            values = listOf(0.0),
                            chartType = null,
                            includeYAxisPadding = false,
                            paddingBottom = COMBINED_PADDING_BOTTOM
                        )
                        baseMetrics = base
                        val leftM = base.copy(yAxisRange = leftRange)
                        val baselineY = base.paddingY + base.chartHeight

                        if (showGrid) {
                            ChartDraw.drawGrid(this, size, leftM, YAxisPosition.LEFT, drawLabels = false)
                        }

                        selectedIndex?.let { idx ->
                            if (idx in 0 until slotCount) {
                                val gx = base.paddingX + (idx + 0.5f) * (base.chartWidth / slotCount)
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.35f),
                                    start = Offset(gx, base.paddingY),
                                    end = Offset(gx, baselineY),
                                    strokeWidth = 2f
                                )
                            }
                        }

                        // AREA fills go under bars/lines
                        series.forEachIndexed { i, s ->
                            if (s.type == CombinedSeriesType.AREA) {
                                val pts = mapSlotPoints(s.data, axisMetrics(base, s), slotCount)
                                ChartDraw.Line.drawArea(this, pts, baselineY, resolvedColors[i].copy(alpha = s.areaAlpha))
                            }
                        }

                        if (effectiveXLabels.isNotEmpty()) {
                            ChartDraw.Line.drawLineXAxisLabels(
                                ctx = drawContext,
                                labels = effectiveXLabels,
                                metrics = leftM,
                                textSize = xLabelTextSize,
                                maxXTicksLimit = maxXTicksLimit,
                                xLabelAutoSkip = xLabelAutoSkip
                            )
                        }
                    }

                    // ---- Layer 2: bars via the shared BarMarker (over grid/area) ----
                    baseMetrics?.let { base ->
                        series.forEachIndexed { i, s ->
                            val m = axisMetrics(base, s)
                            // Pad bar-family series to slotCount so bars share the same x slots
                            // as the other series (BarMarker positions by index/data.size).
                            when (s.type) {
                                CombinedSeriesType.BAR -> ChartDraw.Bar.BarMarker(
                                    data = (0 until slotCount).map { s.data.getOrNull(it) ?: ChartMark(it.toDouble(), m.minY) },
                                    minValues = List(slotCount) { m.minY },
                                    maxValues = (0 until slotCount).map { s.data.getOrNull(it)?.y ?: m.minY },
                                    metrics = m,
                                    color = resolvedColors[i],
                                    barWidthRatio = s.barWidthRatio,
                                    useLineChartPositioning = true,
                                    interactive = false,
                                    chartType = ChartType.BAR,
                                    barCornerRadiusFraction = s.barCornerRadiusFraction,
                                )
                                CombinedSeriesType.RANGE_BAR -> {
                                    val ranges = s.data.filterIsInstance<RangeChartMark>()
                                    ChartDraw.Bar.BarMarker(
                                        data = (0 until slotCount).map { ranges.getOrNull(it) ?: ChartMark(it.toDouble(), m.minY) },
                                        minValues = (0 until slotCount).map { ranges.getOrNull(it)?.minPoint?.y ?: m.minY },
                                        maxValues = (0 until slotCount).map { ranges.getOrNull(it)?.maxPoint?.y ?: m.minY },
                                        metrics = m,
                                        color = resolvedColors[i],
                                        barWidthRatio = s.barWidthRatio,
                                        useLineChartPositioning = true,
                                        interactive = false,
                                        chartType = ChartType.RANGE_BAR,
                                        barCornerRadiusFraction = s.barCornerRadiusFraction,
                                    )
                                }
                                else -> {}
                            }
                        }
                    }

                    // ---- Layer 3: lines (over bars) ----
                    Canvas(Modifier.fillMaxSize()) {
                        val base = ChartMath.computeMetrics(
                            size = size,
                            values = listOf(0.0),
                            chartType = null,
                            includeYAxisPadding = false,
                            paddingBottom = COMBINED_PADDING_BOTTOM
                        )
                        series.forEachIndexed { i, s ->
                            if (s.type == CombinedSeriesType.LINE || s.type == CombinedSeriesType.AREA) {
                                val pts = mapSlotPoints(s.data, axisMetrics(base, s), slotCount)
                                ChartDraw.Line.drawLine(this, pts, resolvedColors[i], s.strokeWidth)
                            }
                        }
                    }

                    // ---- Layer 4: points via the shared PointMarker (scatter + line dots) ----
                    baseMetrics?.let { base ->
                        series.forEachIndexed { i, s ->
                            val showPts = s.type == CombinedSeriesType.SCATTER ||
                                ((s.type == CombinedSeriesType.LINE || s.type == CombinedSeriesType.AREA) && s.showPoints)
                            if (showPts) {
                                val cm = s.data.asChartMarks()
                                val pts = mapSlotPoints(s.data, axisMetrics(base, s), slotCount)
                                ChartDraw.Scatter.PointMarker(
                                    data = cm,
                                    points = pts,
                                    values = cm.map { it.y },
                                    color = resolvedColors[i],
                                    pointRadius = s.pointRadius,
                                    innerRadius = 0.dp,
                                    interactive = false,
                                    showPoint = true,
                                    pointType = s.pointType,
                                    chartType = if (s.type == CombinedSeriesType.SCATTER) ChartType.SCATTERPLOT else ChartType.LINE,
                                    canvasSize = plotSize,
                                )
                            }
                        }
                    }

                    // ---- Tap tooltip overlay ----
                    val idx = selectedIndex
                    if (enableTooltip && idx != null && plotSize != Size.Zero) {
                        val density = LocalDensity.current
                        val anchorX = (idx + 0.5f) * (plotSize.width / slotCount)
                        val estW = with(density) { 150.dp.toPx() }
                        val gap = with(density) { 8.dp.toPx() }
                        val xClamped = (anchorX + gap)
                            .coerceIn(0f, (plotSize.width - estW).coerceAtLeast(0f))
                        val yPlaced = with(density) { 8.dp.toPx() }

                        Box(Modifier.matchParentSize().zIndex(2f)) {
                            CombinedTooltip(
                                slotLabel = effectiveXLabels.getOrNull(idx),
                                rows = series.mapIndexedNotNull { i, s ->
                                    val mark = s.data.getOrNull(idx) ?: return@mapIndexedNotNull null
                                    val value =
                                        if (s.type == CombinedSeriesType.RANGE_BAR && mark is RangeChartMark)
                                            "${fmtValue(mark.minPoint.y)}–${fmtValue(mark.maxPoint.y)}"
                                        else fmtValue(mark.y)
                                    CombinedTooltipRow(
                                        color = resolvedColors[i],
                                        label = s.label.ifBlank { "Series ${i + 1}" },
                                        value = value
                                    )
                                },
                                modifier = Modifier.offset { IntOffset(xClamped.toInt(), yPlaced.toInt()) }
                            )
                        }
                    }
                }

                // ---- RIGHT axis pane ----
                if (hasRight && rightRange != null) {
                    Canvas(
                        Modifier.width(yAxisPaneWidth).fillMaxHeight().clipToBounds()
                    ) {
                        val m = paneMetrics(size).copy(yAxisRange = rightRange)
                        ChartDraw.drawYAxisStandalone(this, m, YAxisPosition.RIGHT, size.width)
                    }
                    if (rightAxisLabel.isNotBlank()) VerticalAxisLabel(rightAxisLabel)
                }
            }
        }

        if (xLabel.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth()) {
                if (hasLeft) Spacer(Modifier.width(yAxisPaneWidth + if (leftAxisLabel.isNotBlank()) 20.dp else 0.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(xLabel, style = MaterialTheme.typography.bodySmall)
                }
                if (hasRight) Spacer(Modifier.width(yAxisPaneWidth + if (rightAxisLabel.isNotBlank()) 20.dp else 0.dp))
            }
        }

        if (showLegend) {
            val labelled = series.withIndex().filter { it.value.label.isNotBlank() }
            if (labelled.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    ChartLegend(
                        labels = labelled.map { it.value.label },
                        colors = labelled.map { resolvedColors[it.index] },
                        position = legendPosition
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

/** Metrics for a Y-axis side pane: same paddingY/chartHeight as the plot (heights match). */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.paneMetrics(size: Size): ChartMath.ChartMetrics =
    ChartMath.computeMetrics(
        size = size,
        values = listOf(0.0),
        chartType = null,
        includeYAxisPadding = false,
        paddingBottom = COMBINED_PADDING_BOTTOM
    )

/** Adapts any [BaseChartMark] list to [ChartMark]s for the line/scatter mapping + marker reuse. */
private fun List<BaseChartMark>.asChartMarks(): List<ChartMark> =
    map { it as? ChartMark ?: ChartMark(x = it.x, y = it.y, label = it.label) }

/**
 * Maps marks to canvas points using a SHARED [slotCount] so series of different lengths still
 * land on the same x slots as the bars (index i → slot i). This is what keeps a shorter line
 * aligned with a longer bar series instead of being stretched across the full width.
 */
private fun mapSlotPoints(
    data: List<BaseChartMark>,
    metrics: ChartMath.ChartMetrics,
    slotCount: Int
): List<Offset> {
    val spacing = if (slotCount > 0) metrics.chartWidth / slotCount else metrics.chartWidth
    val denom = metrics.maxY - metrics.minY
    return data.mapIndexed { i, mark ->
        val x = metrics.paddingX + (i + 0.5f) * spacing
        val norm = if (denom == 0.0) 0f else ((mark.y - metrics.minY) / denom).toFloat().coerceIn(0f, 1f)
        Offset(x, metrics.paddingY + metrics.chartHeight * (1f - norm))
    }
}

private data class CombinedTooltipRow(val color: Color, val label: String, val value: String)

@Composable
private fun CombinedTooltip(
    slotLabel: String?,
    rows: List<CombinedTooltipRow>,
    modifier: Modifier = Modifier
) {
    TooltipContainer(modifier = modifier) {
        if (!slotLabel.isNullOrBlank()) {
            Text(
                text = slotLabel,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(8.dp).background(row.color, CircleShape))
                Text(
                    text = row.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = row.value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun fmtValue(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else "%.1f".format(v)
