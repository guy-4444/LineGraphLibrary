package com.guy.linegraph.model

/**
 * Describes the overall trend direction of a series,
 * determined by comparing the first and last Y values.
 */
enum class GraphTrend {
    Positive,
    Negative,
    Neutral;

    companion object {
        /**
         * Determines the trend of a list of [GraphPoint]s by comparing
         * the first valid Y value with the last valid Y value.
         */
        fun fromPoints(points: List<GraphPoint>): GraphTrend {
            val valid = points.filter { it.y.isFinite() }
            if (valid.size < 2) return Neutral
            val firstY = valid.first().y
            val lastY = valid.last().y
            return when {
                lastY > firstY -> Positive
                lastY < firstY -> Negative
                else -> Neutral
            }
        }
    }
}
