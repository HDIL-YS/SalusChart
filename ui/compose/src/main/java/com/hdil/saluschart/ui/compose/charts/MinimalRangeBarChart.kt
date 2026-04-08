package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hdil.saluschart.core.chart.ChartMark
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.model.BarCornerRadiusFractions
import com.hdil.saluschart.core.chart.toRangeChartMarksByXGroup

/**
 * Minimal range bar chart for small screens such as widgets or smartwatches.
 * Marks with the same x value are grouped into min/max pairs and rendered as range bars.
 */
@Composable
fun MinimalRangeBarChart(
    modifier: Modifier = Modifier,
    data: List<ChartMark>,
    color: Color = Color.Blue,
    barWidthRatio: Float = 0.8f,
    barCornerRadiusFraction: Float = 0f,
    barCornerRadiusFractions: BarCornerRadiusFractions? = null,
    roundTopOnly: Boolean = true,
) {
    if (data.isEmpty()) return
    val chartType = ChartType.RANGE_BAR

    // Transform ChartMarks -> RangeChartMarks
    val rangeData = remember(data) {
        data.toRangeChartMarksByXGroup(
            minValueSelector = { group -> group.minByOrNull { it.y } ?: group.first() },
            maxValueSelector = { group -> group.maxByOrNull { it.y } ?: group.first() }
        )
    }

    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }

    Box(
        Modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val metrics = ChartMath.RangeBar.computeRangeMetrics(size, rangeData)
            chartMetrics = metrics
        }
        chartMetrics?.let { metrics ->
            ChartDraw.Bar.BarMarker(
                data = rangeData,
                minValues = rangeData.map { it.minPoint.y },
                maxValues = rangeData.map { it.maxPoint.y },
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
    }
}
