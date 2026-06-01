package com.guy.linegraph.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.guy.linegraph.config.GridConfig

/**
 * DrawScope extension functions for rendering background grid lines.
 */
internal object GridRenderer {

    /**
     * Draws horizontal and/or vertical grid lines within the chart area.
     *
     * @param scaler Coordinate mapper for determining grid line positions.
     * @param config Grid configuration.
     * @param chartLeft Left edge of the chart area.
     * @param chartTop Top edge of the chart area.
     * @param chartRight Right edge of the chart area.
     * @param chartBottom Bottom edge of the chart area.
     * @param yLabelCount Number of horizontal grid lines (matches Y-axis label count).
     * @param xLabelCount Number of vertical grid lines (matches X-axis label count).
     */
    fun DrawScope.drawGrid(
        scaler: GraphScaler,
        config: GridConfig,
        chartLeft: Float,
        chartTop: Float,
        chartRight: Float,
        chartBottom: Float,
        yLabelCount: Int,
        xLabelCount: Int
    ) {
        if (!config.visible) return

        val lineWidth = config.lineWidth.toPx()

        // Horizontal grid lines
        if (config.horizontalLines) {
            val yValues = scaler.yLabelValues(yLabelCount)
            for (value in yValues) {
                val y = scaler.toCanvasY(value)
                drawLine(
                    color = config.lineColor,
                    start = Offset(chartLeft, y),
                    end = Offset(chartRight, y),
                    strokeWidth = lineWidth
                )
            }
        }

        // Vertical grid lines
        if (config.verticalLines) {
            val xValues = scaler.xLabelValues(xLabelCount)
            for (value in xValues) {
                val x = scaler.toCanvasX(value)
                drawLine(
                    color = config.lineColor,
                    start = Offset(x, chartTop),
                    end = Offset(x, chartBottom),
                    strokeWidth = lineWidth
                )
            }
        }
    }
}
