package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hdil.saluschart.data.model.model.SleepSession
import com.hdil.saluschart.data.model.model.SleepStage
import com.hdil.saluschart.data.model.model.SleepStageType
import com.hdil.saluschart.ui.theme.LocalSalusChartColors
import kotlin.math.max
import kotlin.math.min

private data class MinimalSleepStageGeom(
    val stageType: SleepStageType,
    val stageIndex: Int,
    val centerY: Float,
    val centerX: Float,
    val width: Float
)

/**
 * Minimal sleep stage chart optimized for compact UI.
 *
 * Draws merged sleep stages as rounded horizontal capsules across four rows:
 * Deep, Core, REM, Awake. No axes, labels, or interactions.
 */
@Composable
fun MinimalSleepStageChart(
    modifier: Modifier = Modifier,
    sleepSession: SleepSession,
    barHeightRatio: Float = 0.56f,
    minBarWidthRatio: Float = 0.35f,
    cornerRadiusRatio: Float = 0.18f,
    connectorWidthRatio: Float = 0.12f,
    connectorAlpha: Float = 0.32f,
    showConnectors: Boolean = true
) {
    if (sleepSession.stages.isEmpty()) return

    val mergedStages = remember(sleepSession.stages) {
        mergeConsecutiveSleepStages(sleepSession.stages)
    }
    if (mergedStages.isEmpty()) return

    val palette = LocalSalusChartColors.current.palette
    val deepColor = palette.getOrElse(0) { Color(0xFF3A2B96) }
    val coreColor = palette.getOrElse(1) { Color(0xFF0099FF) }
    val remColor = palette.getOrElse(2) { Color(0xFF00D4FF) }
    val awakeColor = palette.getOrElse(3) { Color(0xFFFF4D4F) }

    fun stageIndex(type: SleepStageType): Int = when (type) {
        SleepStageType.DEEP -> 0
        SleepStageType.LIGHT -> 1
        SleepStageType.REM -> 2
        SleepStageType.AWAKE -> 3
        SleepStageType.UNKNOWN -> 1
    }

    fun colorForStage(type: SleepStageType): Color = when (type) {
        SleepStageType.DEEP -> deepColor
        SleepStageType.LIGHT -> coreColor
        SleepStageType.REM -> remColor
        SleepStageType.AWAKE -> awakeColor
        SleepStageType.UNKNOWN -> coreColor
    }

    val minX = mergedStages.minOf { it.startTime.toEpochMilli().toDouble() }
    val maxX = max(
        minX + 1.0,
        mergedStages.maxOf { it.endTime.toEpochMilli().toDouble() }
    )

    Canvas(modifier = modifier) {
        val stageCount = 4
        val rowHeight = size.height / stageCount.toFloat()
        val capsuleHeight = max(1f, rowHeight * barHeightRatio)
        val minCapsuleWidth = max(2f, capsuleHeight * minBarWidthRatio)
        val corner = CornerRadius(
            x = capsuleHeight * cornerRadiusRatio,
            y = capsuleHeight * cornerRadiusRatio
        )
        val connectorWidth = max(1f, capsuleHeight * connectorWidthRatio)

        fun toX(ms: Double): Float {
            val ratio = ((ms - minX) / (maxX - minX)).toFloat()
            return ratio * size.width
        }

        fun centerYForStageIndex(index: Int): Float {
            return size.height - (index + 0.5f) * rowHeight
        }

        val geoms = mergedStages.map { stage ->
            val startX = toX(stage.startTime.toEpochMilli().toDouble())
            val endX = toX(stage.endTime.toEpochMilli().toDouble())
            val width = max(minCapsuleWidth, endX - startX)
            val centerX = (startX + endX) / 2f
            val idx = stageIndex(stage.stage)
            MinimalSleepStageGeom(
                stageType = stage.stage,
                stageIndex = idx,
                centerY = centerYForStageIndex(idx),
                centerX = centerX,
                width = width
            )
        }

        if (showConnectors && geoms.size > 1) {
            for (i in 0 until geoms.lastIndex) {
                val current = geoms[i]
                val next = geoms[i + 1]
                if (current.stageIndex == next.stageIndex) continue

                val x = toX(mergedStages[i].endTime.toEpochMilli().toDouble())
                val topY = min(current.centerY, next.centerY) - capsuleHeight / 2f
                val bottomY = max(current.centerY, next.centerY) + capsuleHeight / 2f
                val height = bottomY - topY
                if (height <= 0f) continue

                val brush = Brush.verticalGradient(
                    colors = listOf(
                        colorForStage(current.stageType).copy(alpha = 0f),
                        colorForStage(current.stageType).copy(alpha = connectorAlpha),
                        colorForStage(next.stageType).copy(alpha = connectorAlpha),
                        colorForStage(next.stageType).copy(alpha = 0f)
                    ),
                    startY = topY,
                    endY = bottomY
                )

                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(x - connectorWidth / 2f, topY),
                    size = Size(connectorWidth, height),
                    cornerRadius = CornerRadius(connectorWidth / 2f)
                )
            }
        }

        geoms.forEach { geom ->
            drawRoundRect(
                color = colorForStage(geom.stageType),
                topLeft = Offset(
                    x = geom.centerX - geom.width / 2f,
                    y = geom.centerY - capsuleHeight / 2f
                ),
                size = Size(geom.width, capsuleHeight),
                cornerRadius = corner
            )
        }
    }
}

private fun mergeConsecutiveSleepStages(stages: List<SleepStage>): List<SleepStage> {
    if (stages.isEmpty()) return emptyList()
    if (stages.size == 1) return stages

    val result = mutableListOf<SleepStage>()
    var current = stages.first()

    for (i in 1 until stages.size) {
        val next = stages[i]
        if (next.stage == current.stage && !next.startTime.isAfter(current.endTime)) {
            current = SleepStage(
                startTime = current.startTime,
                endTime = next.endTime,
                stage = current.stage
            )
        } else {
            result += current
            current = next
        }
    }

    result += current
    return result
}
