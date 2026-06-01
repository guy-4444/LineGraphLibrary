package com.guy.linegraph.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for line rendering.
 *
 * @param visible Whether the line is drawn.
 * @param width Stroke width of the line.
 * @param curved If true, uses cubic bezier curves. Straight segments by default.
 * @param showPoints If true, draws small circles at each data point.
 * @param pointRadius Radius of the data-point circles.
 * @param defaultColor Fallback line color when [com.guy.linegraph.model.GraphSeries.color] is null.
 * @param autoTrendColor If true, overrides line color based on trend direction.
 * @param positiveColor Color used when the series trend is positive (last Y > first Y).
 * @param negativeColor Color used when the series trend is negative (last Y < first Y).
 * @param neutralColor Color used when the series trend is neutral (last Y == first Y).
 */
data class LineConfig(
    val visible: Boolean = true,
    val width: Dp = 2.dp,
    val curved: Boolean = false,
    val showPoints: Boolean = false,
    val pointRadius: Dp = 3.dp,
    val defaultColor: Color = Color(0xFF4DB6A4),
    val autoTrendColor: Boolean = false,
    val positiveColor: Color = Color(0xFF16A34A),
    val negativeColor: Color = Color(0xFFDC2626),
    val neutralColor: Color = Color.Gray
)
