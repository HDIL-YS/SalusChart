package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders a ladder (multi-band) chart where one band is highlighted and a positional marker dot
 * is drawn within it.
 *
 * Bands are stacked vertically and centered within the available height. The selected band is
 * filled with [selectedColor]; all others use [trackColor].
 *
 * @param modifier Modifier applied to the Canvas.
 * @param bandCount Total number of horizontal bands to draw.
 * @param selectedBandIndex Index of the active band (0 = top).
 * @param markerRatio Horizontal position of the marker dot within the selected band (0 = left, 1 = right).
 * @param trackColor Color of inactive bands.
 * @param selectedColor Color of the active band.
 * @param markerColor Fill color of the marker dot.
 * @param bandHeight Height of each band.
 * @param bandGap Vertical gap between consecutive bands.
 * @param cornerRadiusRatio Corner radius of each band as a fraction of [bandHeight].
 * @param markerRadius Radius of the marker dot.
 * @param markerRingWidth Width of the white ring drawn around the marker dot.
 * @param markerRingColor Color of the ring surrounding the marker dot.
 */
@Composable
fun MinimalLadderChart(
    modifier: Modifier = Modifier,
    bandCount: Int = 3,
    selectedBandIndex: Int,
    markerRatio: Float, // 0f..1f
    trackColor: Color,
    selectedColor: Color,
    markerColor: Color,

    bandHeight: Dp = 10.dp,
    bandGap: Dp = 2.dp,
    cornerRadiusRatio: Float = 0.95f,

    markerRadius: Dp = 4.dp,
    markerRingWidth: Dp = 2.dp,
    markerRingColor: Color = Color.White,
) {
    val safeRatio = markerRatio.coerceIn(0f, 1f)
    val safeIndex = selectedBandIndex.coerceIn(0, bandCount - 1)

    Canvas(modifier = modifier) {
        val hPx = bandHeight.toPx()
        val gapPx = bandGap.toPx()

        val totalHeight = bandCount * hPx + (bandCount - 1) * gapPx
        val startY = (size.height - totalHeight) / 2f

        val width = size.width
        val corner = CornerRadius(
            x = hPx * cornerRadiusRatio,
            y = hPx * cornerRadiusRatio
        )

        repeat(bandCount) { index ->
            val y = startY + index * (hPx + gapPx)

            // Base track
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, y),
                size = Size(width, hPx),
                cornerRadius = corner
            )

            // Selected band + marker
            if (index == safeIndex) {
                drawRoundRect(
                    color = selectedColor,
                    topLeft = Offset(0f, y),
                    size = Size(width, hPx),
                    cornerRadius = corner
                )

                val cx = width * safeRatio
                val cy = y + hPx / 2f

                val r = markerRadius.toPx()
                val ring = markerRingWidth.toPx()

                // white ring
                drawCircle(
                    color = markerRingColor,
                    radius = r + ring,
                    center = Offset(cx, cy)
                )
                // inner dot
                drawCircle(
                    color = markerColor,
                    radius = r,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}
