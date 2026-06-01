package com.guy.linegraph.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guy.linegraph.model.GraphPoint
import com.guy.linegraph.model.GraphSeries

/**
 * Configuration for the tap-tooltip overlay.
 *
 * @param enabled Whether tapping shows a tooltip.
 * @param backgroundColor Background color of the tooltip box.
 * @param textColor Text color inside the tooltip.
 * @param textSize Font size of the tooltip text.
 * @param cornerRadius Corner radius of the tooltip box.
 * @param formatter Produces the tooltip text from the series and selected point.
 */
data class TooltipConfig(
    val enabled: Boolean = true,
    val backgroundColor: Color = Color(0xFF111111),
    val textColor: Color = Color.White,
    val textSize: TextUnit = 13.sp,
    val cornerRadius: Dp = 8.dp,
    val formatter: (GraphSeries, GraphPoint) -> String = { series, point ->
        val namePrefix = if (series.name.isNotBlank()) "${series.name}: " else ""
        "$namePrefix${point.y}"
    }
)
