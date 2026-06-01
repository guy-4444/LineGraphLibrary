package com.guy.linegraph.internal

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.guy.linegraph.config.TooltipConfig
import com.guy.linegraph.model.GraphPoint
import com.guy.linegraph.model.GraphSeries

/**
 * DrawScope extension functions for rendering the tooltip overlay.
 */
internal object TooltipRenderer {

    /**
     * Draws the tooltip: vertical indicator line, highlighted circle, and text box.
     *
     * @param canvasX Canvas X coordinate of the selected point.
     * @param canvasY Canvas Y coordinate of the selected point.
     * @param series The series the selected point belongs to.
     * @param point The selected data point.
     * @param seriesColor The resolved color of the series.
     * @param config Tooltip configuration.
     * @param textMeasurer Text measurer for tooltip text layout.
     * @param chartLeft Left boundary of the chart area.
     * @param chartTop Top boundary of the chart area.
     * @param chartRight Right boundary of the chart area.
     * @param chartBottom Bottom boundary of the chart area.
     */
    fun DrawScope.drawTooltip(
        canvasX: Float,
        canvasY: Float,
        series: GraphSeries,
        point: GraphPoint,
        seriesColor: Color,
        config: TooltipConfig,
        textMeasurer: TextMeasurer,
        chartLeft: Float,
        chartTop: Float,
        chartRight: Float,
        chartBottom: Float
    ) {
        if (!config.enabled) return

        // Vertical indicator line
        drawLine(
            color = seriesColor.copy(alpha = 0.4f),
            start = Offset(canvasX, chartTop),
            end = Offset(canvasX, chartBottom),
            strokeWidth = 1.5f
        )

        // Highlighted circle
        drawCircle(
            color = Color.White,
            radius = 7f,
            center = Offset(canvasX, canvasY)
        )
        drawCircle(
            color = seriesColor,
            radius = 5f,
            center = Offset(canvasX, canvasY)
        )

        // Tooltip text
        val tooltipText = config.formatter(series, point)
        val textStyle = TextStyle(
            color = config.textColor,
            fontSize = config.textSize
        )
        val textLayout = textMeasurer.measure(tooltipText, textStyle)

        val paddingH = 12f
        val paddingV = 8f
        val boxWidth = textLayout.size.width + paddingH * 2
        val boxHeight = textLayout.size.height + paddingV * 2
        val cornerRadius = config.cornerRadius.toPx()

        // Position: prefer above the point, offset to the right
        var boxX = canvasX - boxWidth / 2f
        var boxY = canvasY - boxHeight - 16f

        // Clamp to chart bounds
        if (boxX < chartLeft) boxX = chartLeft
        if (boxX + boxWidth > chartRight) boxX = chartRight - boxWidth
        if (boxY < chartTop) boxY = canvasY + 16f // flip below

        // Background box
        drawRoundRect(
            color = config.backgroundColor,
            topLeft = Offset(boxX, boxY),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // Text
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(boxX + paddingH, boxY + paddingV)
        )
    }
}
