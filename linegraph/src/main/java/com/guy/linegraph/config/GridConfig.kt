package com.guy.linegraph.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for background grid lines.
 *
 * @param visible Whether grid lines are drawn at all.
 * @param lineColor Color of the grid lines.
 * @param lineWidth Stroke width of each grid line.
 * @param horizontalLines Whether horizontal grid lines are drawn.
 * @param verticalLines Whether vertical grid lines are drawn.
 */
data class GridConfig(
    val visible: Boolean = true,
    val lineColor: Color = Color(0xFFE5E7EB),
    val lineWidth: Dp = 1.dp,
    val horizontalLines: Boolean = true,
    val verticalLines: Boolean = false
)
