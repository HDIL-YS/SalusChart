package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hdil.saluschart.core.chart.ChartMark
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.ReferenceLineSpec
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartDraw.ReferenceLine
import com.hdil.saluschart.core.chart.chartDraw.YAxisPosition
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.model.BarCornerRadiusFractions

/**
 * Minimal bar chart for small screens such as widgets or smartwatches.
 * Renders bars only — no axes, grid, or labels.
 *
 * @param referenceLines Optional reference lines drawn across the plot area.
 */
@Composable
fun MinimalBarChart(
    modifier: Modifier = Modifier,
    data: List<ChartMark>,
    color: Color = Color.Blue,
    padding: Float = 4f,
    barWidthRatio: Float = 0.8f,
    barCornerRadiusFraction: Float = 0f,
    barCornerRadiusFractions: BarCornerRadiusFractions? = null,
    roundTopOnly: Boolean = true,
    referenceLines: List<ReferenceLineSpec> = emptyList(),
) {
    if (data.isEmpty()) return

    val yValues = data.map { it.y }
    val chartType = ChartType.BAR

    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val labelReservePx = if (referenceLines.any { it.showLabel || it.label != null }) 20.dp.toPx() else 0f
            val metrics = ChartMath.computeMetrics(
                size = Size(size.width - labelReservePx, size.height),
                values = yValues,
                isMinimal = true,
                paddingX = padding,
                paddingY = padding,
                chartType = chartType
            )

            chartMetrics = metrics
        }

        chartMetrics?.let { metrics ->
            ChartDraw.Bar.BarMarker(
                data = data,
                minValues = List(yValues.size) { 0.0 },
                maxValues = yValues,
                metrics = metrics,
                color = color,
                barWidthRatio = barWidthRatio,
                interactive = false,
                chartType = chartType,
                barCornerRadiusFraction = barCornerRadiusFraction,
                barCornerRadiusFractions = barCornerRadiusFractions,
                roundTopOnly = roundTopOnly,
            )
        }

        if (referenceLines.isNotEmpty()) {
            chartMetrics?.let { metrics ->
                ReferenceLine.ReferenceLines(
                    modifier = Modifier.fillMaxSize(),
                    specs = referenceLines,
                    data = data,
                    metrics = metrics,
                    chartType = chartType,
                    yAxisPosition = YAxisPosition.LEFT,
                )
            }
        }
    }
}
