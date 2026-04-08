package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hdil.saluschart.core.chart.ChartMark
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.ReferenceLineSpec
import com.hdil.saluschart.core.chart.chartDraw.LineChartDraw
import com.hdil.saluschart.core.chart.chartDraw.ReferenceLine
import com.hdil.saluschart.core.chart.chartDraw.YAxisPosition
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartMath.ChartMath

/**
 * Minimal line (sparkline) chart for small screens such as widgets or smartwatches.
 * Renders the line only — no axes, grid, or labels.
 *
 * @param referenceLines Optional reference lines drawn across the plot area.
 */
@Composable
fun MinimalLineChart(
    modifier: Modifier = Modifier,
    data: List<ChartMark>,
    color: Color = Color.Blue,
    strokeWidth: Float = 2f,
    padding: Float = 4f,
    showPoints: Boolean = false,
    referenceLines: List<ReferenceLineSpec> = emptyList(),
) {
    if (data.isEmpty()) return
    val chartType = ChartType.LINE

    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val labelReservePx = if (referenceLines.any { it.showLabel || it.label != null }) 20.dp.toPx() else 0f
            val metrics = ChartMath.computeMetrics(
                size = Size(size.width - labelReservePx, size.height),
                values = data.map { it.y },
                chartType = chartType,
                isMinimal = true,
                paddingX = padding,
                paddingY = padding
            )
            val points = ChartMath.Line.mapLineToCanvasPoints(data, size, metrics)

            chartMetrics = metrics

            LineChartDraw.drawLine(this, points, color, strokeWidth)
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
