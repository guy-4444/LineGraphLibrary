package com.guy.linegraph.config

/**
 * Determines the overall display mode of the chart.
 */
enum class GraphMode {
    /**
     * Regular chart mode.
     * Shows axes, grid, legend, and tooltip by default.
     * Good for dashboards and full-size graphs.
     */
    Chart,

    /**
     * Mini chart mode (sparkline).
     * Hides axes, grid, legend, and tooltip by default.
     * Uses compact padding. Ideal for small cards and inline indicators.
     */
    Sparkline
}
