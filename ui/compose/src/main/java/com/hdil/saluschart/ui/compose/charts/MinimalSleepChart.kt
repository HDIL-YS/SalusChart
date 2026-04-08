package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.max

/**
 * A single colored sleep stage segment for use in [SleepColumn].
 *
 * @param value Duration of this stage in any consistent unit (e.g., minutes).
 * @param color Fill color representing this sleep stage.
 */
data class SleepSegment(
    val value: Float,      // e.g., minutes (any unit is fine)
    val color: Color
)

/**
 * A single column of sleep stage segments for use in [MinimalSleepChart].
 *
 * @param segments Ordered list of sleep stage segments stacked from bottom to top.
 */
data class SleepColumn(
    val segments: List<SleepSegment>
)

/**
 * Renders a compact multi-column sleep chart where each column is a vertical stack of
 * rounded-rectangle sleep stage capsules.
 *
 * Columns are placed side-by-side with equal slot widths. Heights are normalized so the
 * tallest column fills the available canvas height (or [maxValueOverride] if provided).
 *
 * @param modifier Modifier applied to the Canvas.
 * @param columns Ordered list of day columns to render.
 * @param barWidthRatio Width of each capsule as a fraction of its slot width.
 * @param columnGapRatio Horizontal gap between columns as a fraction of the capsule width.
 * @param segmentGapRatio Vertical gap between segments within a column as a fraction of the capsule width.
 * @param trackColor Optional background track color drawn behind each column when [trackAlpha] > 0.
 * @param trackAlpha Alpha of the background track; set to 0 to hide it.
 * @param cornerRadiusRatio Corner radius of each capsule as a fraction of the capsule width.
 * @param maxValueOverride Optional total value that maps to the full canvas height; computed from
 *   the tallest column when null.
 */
@Composable
fun MinimalSleepChart(
    modifier: Modifier = Modifier,
    columns: List<SleepColumn>,

    // sizing
    barWidthRatio: Float = 0.55f,   // capsule width within each slot
    columnGapRatio: Float = 0.55f,  // gap between columns relative to bar width
    segmentGapRatio: Float = 0.30f, // vertical gap between segments relative to bar width

    // styling
    trackColor: Color = Color(0xFFEFEFEF), // optional background track (very subtle)
    trackAlpha: Float = 0.0f,              // set >0 if you want a faint track behind each column
    cornerRadiusRatio: Float = 0.45f,      // capsule rounding relative to width

    // normalization
    maxValueOverride: Float? = null,       // if null, uses max(total of column)
) {
    if (columns.isEmpty()) return

    Canvas(modifier = modifier) {
        val count = columns.size
        if (count <= 0) return@Canvas

        // compute totals
        val totals = columns.map { col -> col.segments.sumOf { it.value.toDouble() }.toFloat() }
        val maxTotal = maxValueOverride ?: (totals.maxOrNull() ?: 0f)
        if (maxTotal <= 0f) return@Canvas

        val chartW = size.width
        val chartH = size.height

        // Each column gets a slot
        val slotW = chartW / count.toFloat()
        val barW = slotW * barWidthRatio

        val colGap = barW * columnGapRatio
        val segGap = barW * segmentGapRatio

        val corner = CornerRadius(
            x = barW * cornerRadiusRatio,
            y = barW * cornerRadiusRatio
        )

        columns.forEachIndexed { i, col ->
            val total = totals[i]
            if (total <= 0f) return@forEachIndexed

            val usableH = chartH
            val scale = usableH / maxTotal

            val xCenter = slotW * (i + 0.5f)
            val xLeft = xCenter - barW / 2f

            // optional track
            if (trackAlpha > 0f) {
                drawRoundRect(
                    color = trackColor.copy(alpha = trackAlpha),
                    topLeft = Offset(xLeft, 0f),
                    size = Size(barW, chartH),
                    cornerRadius = corner
                )
            }

            // stack from bottom upwards
            var yBottom = chartH

            // Normalize segment heights to total * scale, with gaps between segments
            col.segments.forEach { seg ->
                val h = max(0f, seg.value) * scale
                if (h <= 0f) return@forEach

                val top = yBottom - h
                val rectTop = top + segGap / 2f
                val rectH = max(0f, h - segGap)

                if (rectH > 0f) {
                    drawRoundRect(
                        color = seg.color,
                        topLeft = Offset(xLeft, rectTop),
                        size = Size(barW, rectH),
                        cornerRadius = corner
                    )
                }

                yBottom = top
            }
        }
    }
}
