package com.guy.linegraph.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Position of the legend relative to the chart area.
 */
enum class LegendPosition {
    TopStart,
    TopEnd,
    BottomStart,
    BottomEnd
}

/**
 * Configuration for the series legend.
 *
 * @param visible Whether the legend is shown.
 *   Legend is automatically hidden if all series names are blank.
 * @param position Where the legend is placed relative to the chart.
 * @param textColor Text color for legend labels.
 * @param textSize Font size for legend labels.
 */
data class LegendConfig(
    val visible: Boolean = true,
    val position: LegendPosition = LegendPosition.TopEnd,
    val textColor: Color = Color(0xFF333333),
    val textSize: TextUnit = 12.sp
)
