package com.guy.linegraph.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.guy.linegraph.config.AxisConfig

/**
 * DrawScope extension functions for rendering axis labels.
 */
internal object AxisRenderer {

    /**
     * Draws Y-axis labels along the left side of the chart area.
     *
     * @param scaler Coordinate mapper for value-to-canvas conversion.
     * @param config Y-axis configuration.
     * @param textMeasurer Text measurer for label layout.
     * @param chartLeft Left edge of the chart drawing area.
     */
    fun DrawScope.drawYAxis(
        scaler: GraphScaler,
        config: AxisConfig,
        textMeasurer: TextMeasurer,
        chartLeft: Float
    ) {
        if (!config.visible) return

        val values = scaler.yLabelValues(config.labelCount)
        val style = TextStyle(
            color = config.labelColor,
            fontSize = config.labelTextSize
        )

        for (value in values) {
            val label = config.formatter(value)
            val layoutResult = textMeasurer.measure(label, style)
            val canvasY = scaler.toCanvasY(value)

            drawText(
                textLayoutResult = layoutResult,
                topLeft = Offset(
                    x = chartLeft - layoutResult.size.width - 8f,
                    y = canvasY - layoutResult.size.height / 2f
                )
            )
        }
    }

    /**
     * Draws X-axis labels along the bottom of the chart area.
     *
     * @param scaler Coordinate mapper for value-to-canvas conversion.
     * @param config X-axis configuration.
     * @param textMeasurer Text measurer for label layout.
     * @param chartBottom Bottom edge of the chart drawing area.
     */
    fun DrawScope.drawXAxis(
        scaler: GraphScaler,
        config: AxisConfig,
        textMeasurer: TextMeasurer,
        chartBottom: Float
    ) {
        if (!config.visible) return

        val values = scaler.xLabelValues(config.labelCount)
        val style = TextStyle(
            color = config.labelColor,
            fontSize = config.labelTextSize
        )

        for (value in values) {
            val label = config.formatter(value)
            val layoutResult = textMeasurer.measure(label, style)
            val canvasX = scaler.toCanvasX(value)

            drawText(
                textLayoutResult = layoutResult,
                topLeft = Offset(
                    x = canvasX - layoutResult.size.width / 2f,
                    y = chartBottom + 8f
                )
            )
        }
    }

    /**
     * Measures the width of the widest Y-axis label to determine left padding.
     */
    fun measureYAxisWidth(
        scaler: GraphScaler,
        config: AxisConfig,
        textMeasurer: TextMeasurer
    ): Float {
        if (!config.visible) return 0f
        val style = TextStyle(
            color = config.labelColor,
            fontSize = config.labelTextSize
        )
        val values = scaler.yLabelValues(config.labelCount)
        var maxWidth = 0f
        for (value in values) {
            val label = config.formatter(value)
            val layoutResult = textMeasurer.measure(label, style)
            if (layoutResult.size.width > maxWidth) {
                maxWidth = layoutResult.size.width.toFloat()
            }
        }
        return maxWidth + 12f // 12px gap
    }

    /**
     * Measures the height of an X-axis label to determine bottom padding.
     */
    fun measureXAxisHeight(
        config: AxisConfig,
        textMeasurer: TextMeasurer
    ): Float {
        if (!config.visible) return 0f
        val style = TextStyle(
            color = config.labelColor,
            fontSize = config.labelTextSize
        )
        val layoutResult = textMeasurer.measure("Xg", style)
        return layoutResult.size.height.toFloat() + 12f // 12px gap
    }
}
