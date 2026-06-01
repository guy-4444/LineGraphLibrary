package com.guy.linegraph

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.guy.linegraph.config.AxisConfig
import com.guy.linegraph.config.CleanGraphConfig
import com.guy.linegraph.config.GraphMode
import com.guy.linegraph.config.LegendPosition
import com.guy.linegraph.internal.AxisRenderer
import com.guy.linegraph.internal.GraphPathBuilder
import com.guy.linegraph.internal.GraphRenderer
import com.guy.linegraph.internal.GraphScaler
import com.guy.linegraph.internal.GridRenderer
import com.guy.linegraph.internal.PointSelector
import com.guy.linegraph.internal.SelectedPoint
import com.guy.linegraph.internal.TooltipRenderer
import com.guy.linegraph.model.GraphPoint
import com.guy.linegraph.model.GraphSeries
import com.guy.linegraph.model.GraphTrend

/** Default color palette for auto-coloring series without explicit colors. */
private val DefaultPalette = listOf(
    Color(0xFF4DB6A4),
    Color(0xFF6C7AE0),
    Color(0xFFE9A66B),
    Color(0xFFE06C8E),
    Color(0xFF5BC0EB),
    Color(0xFFA78BFA),
    Color(0xFF34D399),
    Color(0xFFFBBF24)
)

// ────────────────────────────────────────────────────────────────
//  Public API
// ────────────────────────────────────────────────────────────────

/**
 * A clean, configurable chart composable for Jetpack Compose.
 *
 * Supports line charts, area charts, sparklines, and multi-line charts
 * through a single entry point controlled by [CleanGraphConfig].
 *
 * @param series The list of data series to plot.
 * @param modifier Standard Compose modifier (set size here).
 * @param config Chart configuration. Defaults produce a clean chart out of the box.
 */
@Composable
fun CleanGraph(
    series: List<GraphSeries>,
    modifier: Modifier = Modifier,
    config: CleanGraphConfig = CleanGraphConfig()
) {
    val textMeasurer = rememberTextMeasurer()
    val layoutDirection = LocalLayoutDirection.current

    // Resolve effective config for Sparkline mode
    val effectiveConfig = remember(config) {
        if (config.mode == GraphMode.Sparkline) {
            config.copy(
                xAxis = config.xAxis.copy(visible = config.xAxis.visible && config.mode != GraphMode.Sparkline),
                yAxis = config.yAxis.copy(visible = config.yAxis.visible && config.mode != GraphMode.Sparkline),
                grid = config.grid.copy(visible = config.grid.visible && config.mode != GraphMode.Sparkline),
                tooltip = config.tooltip.copy(enabled = config.tooltip.enabled && config.mode != GraphMode.Sparkline),
                legend = config.legend.copy(visible = config.legend.visible && config.mode != GraphMode.Sparkline)
            )
        } else config
    }

    // Animation
    var animationTarget by remember { mutableStateOf(if (effectiveConfig.animationEnabled) 0f else 1f) }
    LaunchedEffect(Unit) { animationTarget = 1f }
    val animationProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = if (effectiveConfig.animationEnabled) 800 else 0),
        label = "chartReveal"
    )

    // Tooltip state
    var selectedPoint by remember { mutableStateOf<SelectedPoint?>(null) }

    // Resolve colors for each series
    val seriesWithColors = remember(series, effectiveConfig.line) {
        series.mapIndexed { index, s ->
            val baseColor = s.color
                ?: if (effectiveConfig.line.autoTrendColor) {
                    when (GraphTrend.fromPoints(s.points)) {
                        GraphTrend.Positive -> effectiveConfig.line.positiveColor
                        GraphTrend.Negative -> effectiveConfig.line.negativeColor
                        GraphTrend.Neutral -> effectiveConfig.line.neutralColor
                    }
                } else {
                    DefaultPalette[index % DefaultPalette.size]
                }
            s to baseColor
        }
    }

    // Pre-compute for scaler creation (need a dummy scaler for axis measurement)
    val contentPadding = effectiveConfig.contentPadding

    Canvas(
        modifier = modifier
            .pointerInput(effectiveConfig.tooltip.enabled, series) {
                if (!effectiveConfig.tooltip.enabled) return@pointerInput
                detectTapGestures { offset ->
                    // We need to recompute scaler at tap time with actual canvas size
                    // The scaler is embedded inside the draw lambda, so we do a simpler
                    // nearest-point search using the stored selectedPoint or recalculating
                    selectedPoint = null // will be recalculated in draw

                    // Store tap position for draw-time calculation
                    selectedPoint = SelectedPoint(
                        seriesIndex = -1,
                        series = GraphSeries(points = emptyList()),
                        point = GraphPoint(offset.x, offset.y),
                        color = Color.Transparent,
                        canvasX = offset.x,
                        canvasY = offset.y
                    )
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (canvasWidth <= 0f || canvasHeight <= 0f) return@Canvas

        val paddingLeft = contentPadding.calculateStartPadding(layoutDirection).toPx()
        val paddingRight = contentPadding.calculateEndPadding(layoutDirection).toPx()
        val paddingTop = contentPadding.calculateTopPadding().toPx()
        val paddingBottom = contentPadding.calculateBottomPadding().toPx()

        // Pre-measure axis space using a temporary scaler
        val tempScaler = GraphScaler(
            series = series,
            chartLeft = 0f,
            chartTop = 0f,
            chartWidth = canvasWidth,
            chartHeight = canvasHeight
        )

        val yAxisWidth = if (effectiveConfig.yAxis.visible) {
            AxisRenderer.measureYAxisWidth(tempScaler, effectiveConfig.yAxis, textMeasurer)
        } else 0f

        val xAxisHeight = if (effectiveConfig.xAxis.visible) {
            AxisRenderer.measureXAxisHeight(effectiveConfig.xAxis, textMeasurer)
        } else 0f

        // Calculate chart drawing area
        val chartLeft = paddingLeft + yAxisWidth
        val chartTop = paddingTop
        val chartRight = canvasWidth - paddingRight
        val chartBottom = canvasHeight - paddingBottom - xAxisHeight
        val chartWidth = (chartRight - chartLeft).coerceAtLeast(0f)
        val chartHeight = (chartBottom - chartTop).coerceAtLeast(0f)

        if (chartWidth <= 0f || chartHeight <= 0f) return@Canvas

        // Build the real scaler with correct chart bounds
        val scaler = GraphScaler(
            series = series,
            chartLeft = chartLeft,
            chartTop = chartTop,
            chartWidth = chartWidth,
            chartHeight = chartHeight
        )

        if (!scaler.hasData) return@Canvas

        // ── 1. Grid ──
        with(GridRenderer) {
            drawGrid(
                scaler = scaler,
                config = effectiveConfig.grid,
                chartLeft = chartLeft,
                chartTop = chartTop,
                chartRight = chartRight,
                chartBottom = chartBottom,
                yLabelCount = effectiveConfig.yAxis.labelCount,
                xLabelCount = effectiveConfig.xAxis.labelCount
            )
        }

        // ── 2. Axes ──
        with(AxisRenderer) {
            drawYAxis(scaler, effectiveConfig.yAxis, textMeasurer, chartLeft)
            drawXAxis(scaler, effectiveConfig.xAxis, textMeasurer, chartBottom)
        }

        // ── 3. Area fills ──
        for ((s, color) in seriesWithColors) {
            val validPoints = s.points.filter { it.x.isFinite() && it.y.isFinite() }
            if (validPoints.isEmpty()) continue
            if (!effectiveConfig.area.visible) continue

            val linePath = if (effectiveConfig.line.curved) {
                GraphPathBuilder.buildCurvedPath(validPoints, scaler, animationProgress)
            } else {
                GraphPathBuilder.buildLinePath(validPoints, scaler, animationProgress)
            }
            val areaPath = GraphPathBuilder.buildAreaPath(
                linePath, validPoints, scaler, chartBottom, animationProgress
            )
            with(GraphRenderer) {
                drawArea(areaPath, color, effectiveConfig.area, chartTop, chartBottom)
            }
        }

        // ── 4. Lines ──
        for ((s, color) in seriesWithColors) {
            val validPoints = s.points.filter { it.x.isFinite() && it.y.isFinite() }
            if (validPoints.isEmpty()) continue

            val linePath = if (effectiveConfig.line.curved) {
                GraphPathBuilder.buildCurvedPath(validPoints, scaler, animationProgress)
            } else {
                GraphPathBuilder.buildLinePath(validPoints, scaler, animationProgress)
            }
            with(GraphRenderer) {
                drawLine(linePath, color, effectiveConfig.line)
            }
        }

        // ── 5. Points ──
        for ((s, color) in seriesWithColors) {
            val validPoints = s.points.filter { it.x.isFinite() && it.y.isFinite() }
            if (validPoints.isEmpty()) continue
            with(GraphRenderer) {
                drawPoints(validPoints, scaler, color, effectiveConfig.line, animationProgress)
            }
        }

        // ── 6. Legend ──
        val namedSeries = seriesWithColors.filter { it.first.name.isNotBlank() }
        if (effectiveConfig.legend.visible && namedSeries.size > 1) {
            drawLegend(
                namedSeries = namedSeries,
                config = effectiveConfig,
                textMeasurer = textMeasurer,
                chartLeft = chartLeft,
                chartTop = chartTop,
                chartRight = chartRight,
                chartBottom = chartBottom
            )
        }

        // ── 7. Tooltip ──
        val tapPoint = selectedPoint
        if (tapPoint != null && effectiveConfig.tooltip.enabled) {
            // Recalculate nearest point using actual scaler
            val nearest = PointSelector.findNearest(
                tapX = tapPoint.canvasX,
                tapY = tapPoint.canvasY,
                seriesWithColors = seriesWithColors,
                scaler = scaler
            )
            if (nearest != null) {
                with(TooltipRenderer) {
                    drawTooltip(
                        canvasX = nearest.canvasX,
                        canvasY = nearest.canvasY,
                        series = nearest.series,
                        point = nearest.point,
                        seriesColor = nearest.color,
                        config = effectiveConfig.tooltip,
                        textMeasurer = textMeasurer,
                        chartLeft = chartLeft,
                        chartTop = chartTop,
                        chartRight = chartRight,
                        chartBottom = chartBottom
                    )
                }
            }
        }
    }
}

/**
 * Convenience overload for single-series usage.
 *
 * @param points Data points for a single line.
 * @param modifier Standard Compose modifier.
 * @param config Chart configuration.
 */
@JvmName("CleanGraphPoints")
@Composable
fun CleanGraph(
    points: List<GraphPoint>,
    modifier: Modifier = Modifier,
    config: CleanGraphConfig = CleanGraphConfig()
) {
    CleanGraph(
        series = listOf(GraphSeries(points = points)),
        modifier = modifier,
        config = config
    )
}

// ────────────────────────────────────────────────────────────────
//  Private helpers
// ────────────────────────────────────────────────────────────────

/**
 * Draws the chart legend with colored indicators and series names.
 */
private fun DrawScope.drawLegend(
    namedSeries: List<Pair<GraphSeries, Color>>,
    config: CleanGraphConfig,
    textMeasurer: TextMeasurer,
    chartLeft: Float,
    chartTop: Float,
    chartRight: Float,
    chartBottom: Float
) {
    val legendConfig = config.legend
    val style = TextStyle(
        color = legendConfig.textColor,
        fontSize = legendConfig.textSize
    )

    val circleRadius = 5f
    val itemSpacing = 16f
    val circleTextGap = 6f

    // Measure all items
    data class LegendItem(val text: String, val color: Color, val textWidth: Int, val textHeight: Int)

    val items = namedSeries.map { (series, color) ->
        val layout = textMeasurer.measure(series.name, style)
        LegendItem(series.name, color, layout.size.width, layout.size.height)
    }

    val totalWidth = items.sumOf { (circleRadius * 2 + circleTextGap + it.textWidth).toInt() } +
            ((items.size - 1) * itemSpacing).toInt()

    // Determine position
    val startX = when (legendConfig.position) {
        LegendPosition.TopStart, LegendPosition.BottomStart -> chartLeft + 4f
        LegendPosition.TopEnd, LegendPosition.BottomEnd -> chartRight - totalWidth
    }
    val startY = when (legendConfig.position) {
        LegendPosition.TopStart, LegendPosition.TopEnd -> chartTop
        LegendPosition.BottomStart, LegendPosition.BottomEnd -> chartBottom - (items.firstOrNull()?.textHeight?.toFloat() ?: 14f)
    }

    var currentX = startX
    for (item in items) {
        val centerY = startY + item.textHeight / 2f

        drawCircle(
            color = item.color,
            radius = circleRadius,
            center = Offset(currentX + circleRadius, centerY)
        )

        val layout = textMeasurer.measure(item.text, style)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(currentX + circleRadius * 2 + circleTextGap, startY)
        )

        currentX += circleRadius * 2 + circleTextGap + item.textWidth + itemSpacing
    }
}
