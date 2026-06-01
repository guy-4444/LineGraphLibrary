package com.guy.linegraph.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Configuration for an axis (X or Y).
 *
 * @param visible Whether the axis labels are drawn.
 * @param labelCount Desired number of labels along the axis.
 * @param labelColor Text color for axis labels.
 * @param labelTextSize Font size for axis labels.
 * @param formatter Converts a numeric value to a display string.
 */
data class AxisConfig(
    val visible: Boolean = true,
    val labelCount: Int = 5,
    val labelColor: Color = Color(0xFF666666),
    val labelTextSize: TextUnit = 12.sp,
    val formatter: (Float) -> String = { value ->
        if (value % 1f == 0f) value.toInt().toString() else String.format("%.1f", value)
    }
)
