package com.guy.linegraph.config

/**
 * Configuration for the gradient area fill below the line.
 *
 * @param visible Whether the area fill is drawn.
 * @param alpha Opacity of the fill at its strongest point.
 * @param gradientHeightRatio Fraction of the chart height covered by the gradient (1 = full).
 */
data class AreaConfig(
    val visible: Boolean = false,
    val alpha: Float = 0.18f,
    val gradientHeightRatio: Float = 1f
)
