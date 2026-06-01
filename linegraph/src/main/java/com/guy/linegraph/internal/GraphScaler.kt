package com.guy.linegraph.internal

import com.guy.linegraph.model.GraphPoint
import com.guy.linegraph.model.GraphSeries

/**
 * Computes the value-to-canvas coordinate mapping for all series.
 *
 * Filters out NaN/Infinity points and adds Y-axis padding so lines
 * don't touch the very top/bottom of the chart area.
 */
internal class GraphScaler(
    series: List<GraphSeries>,
    private val chartLeft: Float,
    private val chartTop: Float,
    private val chartWidth: Float,
    private val chartHeight: Float
) {
    val minX: Float
    val maxX: Float
    val minY: Float
    val maxY: Float

    /** True when there is valid data to render. */
    val hasData: Boolean

    /** All series with their points filtered to valid (finite) values. */
    val validSeries: List<GraphSeries>

    init {
        // Filter each series' points to only finite values
        validSeries = series.map { s ->
            s.copy(points = s.points.filter { it.x.isFinite() && it.y.isFinite() })
        }.filter { it.points.isNotEmpty() }

        val allPoints = validSeries.flatMap { it.points }
        hasData = allPoints.isNotEmpty()

        if (hasData) {
            val rawMinX = allPoints.minOf { it.x }
            val rawMaxX = allPoints.maxOf { it.x }
            val rawMinY = allPoints.minOf { it.y }
            val rawMaxY = allPoints.maxOf { it.y }

            // Handle all-same values: provide a sensible range
            val xRange = if (rawMaxX - rawMinX == 0f) 1f else 0f
            val yRange = rawMaxY - rawMinY
            val yPadding = if (yRange == 0f) 1f else yRange * 0.05f

            minX = rawMinX - xRange
            maxX = rawMaxX + xRange
            minY = rawMinY - yPadding
            maxY = rawMaxY + yPadding
        } else {
            minX = 0f
            maxX = 1f
            minY = 0f
            maxY = 1f
        }
    }

    private val chartBottom: Float get() = chartTop + chartHeight

    /** Convert a graph X value to a canvas X coordinate. */
    fun toCanvasX(x: Float): Float {
        val range = maxX - minX
        if (range == 0f) return chartLeft + chartWidth / 2f
        return chartLeft + ((x - minX) / range) * chartWidth
    }

    /** Convert a graph Y value to a canvas Y coordinate (inverted for screen). */
    fun toCanvasY(y: Float): Float {
        val range = maxY - minY
        if (range == 0f) return chartTop + chartHeight / 2f
        return chartBottom - ((y - minY) / range) * chartHeight
    }

    /** Convert a [GraphPoint] to canvas coordinates. */
    fun toCanvas(point: GraphPoint): Pair<Float, Float> =
        toCanvasX(point.x) to toCanvasY(point.y)

    /** Generate evenly spaced label values along the Y axis. */
    fun yLabelValues(count: Int): List<Float> {
        if (count <= 1) return listOf((minY + maxY) / 2f)
        return (0 until count).map { i ->
            minY + (maxY - minY) * i / (count - 1).toFloat()
        }
    }

    /** Generate evenly spaced label values along the X axis. */
    fun xLabelValues(count: Int): List<Float> {
        if (count <= 1) return listOf((minX + maxX) / 2f)
        return (0 until count).map { i ->
            minX + (maxX - minX) * i / (count - 1).toFloat()
        }
    }
}
