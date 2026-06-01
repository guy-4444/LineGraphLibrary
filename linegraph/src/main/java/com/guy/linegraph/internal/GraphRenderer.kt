package com.guy.linegraph.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.guy.linegraph.config.AreaConfig
import com.guy.linegraph.config.LineConfig
import com.guy.linegraph.model.GraphPoint

/**
 * DrawScope extension functions for rendering lines, areas, and points.
 */
internal object GraphRenderer {

    /**
     * Draws the line path with the configured stroke style.
     */
    fun DrawScope.drawLine(
        path: Path,
        color: Color,
        lineConfig: LineConfig
    ) {
        if (!lineConfig.visible) return
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = lineConfig.width.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    /**
     * Draws the area gradient fill below the line.
     */
    fun DrawScope.drawArea(
        areaPath: Path,
        color: Color,
        areaConfig: AreaConfig,
        chartTop: Float,
        chartBottom: Float
    ) {
        if (!areaConfig.visible) return

        val gradientTop = chartBottom - (chartBottom - chartTop) * areaConfig.gradientHeightRatio

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = areaConfig.alpha),
                    color.copy(alpha = 0f)
                ),
                startY = gradientTop,
                endY = chartBottom
            ),
            style = Fill
        )
    }

    /**
     * Draws circles at each data point position.
     */
    fun DrawScope.drawPoints(
        points: List<GraphPoint>,
        scaler: GraphScaler,
        color: Color,
        lineConfig: LineConfig,
        animationProgress: Float
    ) {
        if (!lineConfig.showPoints) return

        val maxCanvasX = scaler.toCanvasX(scaler.minX) +
                (scaler.toCanvasX(scaler.maxX) - scaler.toCanvasX(scaler.minX)) * animationProgress

        val radius = lineConfig.pointRadius.toPx()

        for (point in points) {
            val cx = scaler.toCanvasX(point.x)
            if (cx > maxCanvasX) break
            val cy = scaler.toCanvasY(point.y)

            // White border circle
            drawCircle(
                color = Color.White,
                radius = radius + 1.5f,
                center = Offset(cx, cy)
            )
            // Colored inner circle
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(cx, cy)
            )
        }
    }
}
