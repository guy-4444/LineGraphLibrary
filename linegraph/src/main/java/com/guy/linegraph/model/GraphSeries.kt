package com.guy.linegraph.model

import androidx.compose.ui.graphics.Color

/**
 * Represents a named series of data points to be plotted as a line.
 *
 * @param name Display name for the legend. If blank, excluded from legend.
 * @param points The data points in this series.
 * @param color Line color. If null, an automatic color from the default palette is used.
 */
data class GraphSeries(
    val name: String = "",
    val points: List<GraphPoint> = emptyList(),
    val color: Color? = null
)
