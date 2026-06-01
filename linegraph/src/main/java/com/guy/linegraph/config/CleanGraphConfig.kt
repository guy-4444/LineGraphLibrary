package com.guy.linegraph.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

/**
 * Top-level configuration for [com.guy.linegraph.CleanGraph].
 *
 * Aggregates all sub-configurations and sets the overall display mode.
 * All fields have sensible defaults — zero-config usage produces a clean chart.
 *
 * @param mode Chart display mode ([GraphMode.Chart] or [GraphMode.Sparkline]).
 * @param line Line rendering settings.
 * @param area Area fill settings.
 * @param xAxis X-axis label settings.
 * @param yAxis Y-axis label settings.
 * @param grid Grid line settings.
 * @param tooltip Tap-tooltip settings.
 * @param legend Legend settings.
 * @param contentPadding Inner padding of the chart area.
 * @param animationEnabled Whether the first-render reveal animation plays.
 */
data class CleanGraphConfig(
    val mode: GraphMode = GraphMode.Chart,
    val line: LineConfig = LineConfig(),
    val area: AreaConfig = AreaConfig(),
    val xAxis: AxisConfig = AxisConfig(),
    val yAxis: AxisConfig = AxisConfig(),
    val grid: GridConfig = GridConfig(),
    val tooltip: TooltipConfig = TooltipConfig(),
    val legend: LegendConfig = LegendConfig(),
    val contentPadding: PaddingValues = PaddingValues(16.dp),
    val animationEnabled: Boolean = true
)
