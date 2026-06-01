package com.guy.linegraph.internal

import androidx.compose.ui.graphics.Color
import com.guy.linegraph.model.GraphPoint
import com.guy.linegraph.model.GraphSeries
import kotlin.math.sqrt

/**
 * Result of a point-selection operation.
 *
 * @param seriesIndex Index of the series in the list.
 * @param series The matched series.
 * @param point The nearest point within that series.
 * @param color The resolved color of the series.
 * @param canvasX Canvas X coordinate of the matched point.
 * @param canvasY Canvas Y coordinate of the matched point.
 */
internal data class SelectedPoint(
    val seriesIndex: Int,
    val series: GraphSeries,
    val point: GraphPoint,
    val color: Color,
    val canvasX: Float,
    val canvasY: Float
)

/**
 * Utility for finding the nearest data point to a tap position.
 */
internal object PointSelector {

    /**
     * Finds the closest point across all series to the given canvas coordinates.
     *
     * @param tapX Canvas X of the tap.
     * @param tapY Canvas Y of the tap.
     * @param seriesWithColors Pairs of (series, resolved color).
     * @param scaler Coordinate mapper.
     * @return The nearest [SelectedPoint], or null if no data exists.
     */
    fun findNearest(
        tapX: Float,
        tapY: Float,
        seriesWithColors: List<Pair<GraphSeries, Color>>,
        scaler: GraphScaler
    ): SelectedPoint? {
        var best: SelectedPoint? = null
        var bestDist = Float.MAX_VALUE

        for ((index, pair) in seriesWithColors.withIndex()) {
            val (series, color) = pair
            for (point in series.points) {
                if (!point.x.isFinite() || !point.y.isFinite()) continue
                val cx = scaler.toCanvasX(point.x)
                val cy = scaler.toCanvasY(point.y)
                val dx = tapX - cx
                val dy = tapY - cy
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < bestDist) {
                    bestDist = dist
                    best = SelectedPoint(
                        seriesIndex = index,
                        series = series,
                        point = point,
                        color = color,
                        canvasX = cx,
                        canvasY = cy
                    )
                }
            }
        }

        return best
    }
}
