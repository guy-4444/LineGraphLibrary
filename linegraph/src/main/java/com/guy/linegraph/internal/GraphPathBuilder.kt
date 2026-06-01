package com.guy.linegraph.internal

import androidx.compose.ui.graphics.Path
import com.guy.linegraph.model.GraphPoint

/**
 * Builds [Path] objects from graph points for line and area rendering.
 */
internal object GraphPathBuilder {

    /**
     * Creates a line path (straight segments) from the given points.
     *
     * @param points Data points to connect.
     * @param scaler Coordinate mapper.
     * @param animationProgress Fraction of the chart width revealed (0..1).
     */
    fun buildLinePath(
        points: List<GraphPoint>,
        scaler: GraphScaler,
        animationProgress: Float = 1f
    ): Path {
        val path = Path()
        if (points.isEmpty()) return path

        val maxCanvasX = scaler.toCanvasX(scaler.minX) +
                (scaler.toCanvasX(scaler.maxX) - scaler.toCanvasX(scaler.minX)) * animationProgress

        val visiblePoints = points.filter { scaler.toCanvasX(it.x) <= maxCanvasX }
        if (visiblePoints.isEmpty()) return path

        val (startX, startY) = scaler.toCanvas(visiblePoints.first())
        path.moveTo(startX, startY)

        for (i in 1 until visiblePoints.size) {
            val (cx, cy) = scaler.toCanvas(visiblePoints[i])
            path.lineTo(cx, cy)
        }

        // If animation is in progress, interpolate to the cut-off X position
        if (animationProgress < 1f && visiblePoints.size < points.size) {
            val nextPoint = points[visiblePoints.size]
            val prevPoint = visiblePoints.last()
            val nextCX = scaler.toCanvasX(nextPoint.x)
            val prevCX = scaler.toCanvasX(prevPoint.x)
            if (nextCX > prevCX) {
                val t = (maxCanvasX - prevCX) / (nextCX - prevCX)
                val interpY = scaler.toCanvasY(prevPoint.y) +
                        t * (scaler.toCanvasY(nextPoint.y) - scaler.toCanvasY(prevPoint.y))
                path.lineTo(maxCanvasX, interpY)
            }
        }

        return path
    }

    /**
     * Creates a curved line path (cubic bezier) from the given points.
     */
    fun buildCurvedPath(
        points: List<GraphPoint>,
        scaler: GraphScaler,
        animationProgress: Float = 1f
    ): Path {
        val path = Path()
        if (points.isEmpty()) return path

        val maxCanvasX = scaler.toCanvasX(scaler.minX) +
                (scaler.toCanvasX(scaler.maxX) - scaler.toCanvasX(scaler.minX)) * animationProgress

        val canvasPoints = points.map { scaler.toCanvas(it) }
            .filter { it.first <= maxCanvasX + 1f } // small tolerance

        if (canvasPoints.isEmpty()) return path

        path.moveTo(canvasPoints[0].first, canvasPoints[0].second)

        for (i in 1 until canvasPoints.size) {
            val prev = canvasPoints[i - 1]
            val curr = canvasPoints[i]

            // Clip if current point exceeds animation boundary
            val actualX = curr.first.coerceAtMost(maxCanvasX)
            val t = if (curr.first > prev.first && curr.first > maxCanvasX) {
                (maxCanvasX - prev.first) / (curr.first - prev.first)
            } else 1f
            val actualY = if (t < 1f) {
                prev.second + t * (curr.second - prev.second)
            } else curr.second

            val controlOffset = (actualX - prev.first) * 0.4f
            path.cubicTo(
                prev.first + controlOffset, prev.second,
                actualX - controlOffset, actualY,
                actualX, actualY
            )

            if (t < 1f) break
        }

        return path
    }

    /**
     * Creates a closed area path for gradient fill.
     * The area path follows the line path, then drops to the chart bottom
     * and closes back to the start.
     *
     * @param linePath The existing line path to base the area on.
     * @param points Data points (for start/end X coordinates).
     * @param scaler Coordinate mapper.
     * @param chartBottom The bottom Y coordinate of the chart area.
     * @param animationProgress Fraction revealed (0..1).
     */
    fun buildAreaPath(
        linePath: Path,
        points: List<GraphPoint>,
        scaler: GraphScaler,
        chartBottom: Float,
        animationProgress: Float = 1f
    ): Path {
        if (points.isEmpty()) return Path()

        val areaPath = Path()
        areaPath.addPath(linePath)

        val maxCanvasX = scaler.toCanvasX(scaler.minX) +
                (scaler.toCanvasX(scaler.maxX) - scaler.toCanvasX(scaler.minX)) * animationProgress

        val endX = if (animationProgress < 1f) {
            maxCanvasX
        } else {
            scaler.toCanvasX(points.last().x)
        }
        val startX = scaler.toCanvasX(points.first().x)

        areaPath.lineTo(endX, chartBottom)
        areaPath.lineTo(startX, chartBottom)
        areaPath.close()

        return areaPath
    }
}
