package com.guy.linegraph.model

/**
 * Represents a single data point on the graph.
 *
 * @param x Numeric X coordinate.
 * @param y Numeric Y coordinate.
 * @param label Optional display label (e.g. "Jan", "Feb").
 * @param metadata Optional arbitrary data for custom usage.
 */
data class GraphPoint(
    val x: Float,
    val y: Float,
    val label: String? = null,
    val metadata: Any? = null
)
