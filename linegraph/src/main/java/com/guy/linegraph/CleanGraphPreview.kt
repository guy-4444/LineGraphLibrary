package com.guy.linegraph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guy.linegraph.config.AreaConfig
import com.guy.linegraph.config.AxisConfig
import com.guy.linegraph.config.CleanGraphConfig
import com.guy.linegraph.config.GraphMode
import com.guy.linegraph.config.GridConfig
import com.guy.linegraph.config.LegendConfig
import com.guy.linegraph.config.LineConfig
import com.guy.linegraph.config.TooltipConfig
import com.guy.linegraph.model.GraphPoint
import com.guy.linegraph.model.GraphSeries
import kotlin.math.sin

// ────────────────────────────────────────────────────────────────
//  Preview data
// ────────────────────────────────────────────────────────────────

private val previewPoints = listOf(
    GraphPoint(1f, 20f, "Jan"),
    GraphPoint(2f, 35f, "Feb"),
    GraphPoint(3f, 28f, "Mar"),
    GraphPoint(4f, 60f, "Apr"),
    GraphPoint(5f, 48f, "May"),
    GraphPoint(6f, 72f, "Jun"),
    GraphPoint(7f, 65f, "Jul")
)

private val previewSparklineUp = listOf(
    GraphPoint(1f, 10f), GraphPoint(2f, 14f), GraphPoint(3f, 12f),
    GraphPoint(4f, 18f), GraphPoint(5f, 22f), GraphPoint(6f, 20f),
    GraphPoint(7f, 28f)
)

private val previewSparklineDown = listOf(
    GraphPoint(1f, 30f), GraphPoint(2f, 28f), GraphPoint(3f, 32f),
    GraphPoint(4f, 24f), GraphPoint(5f, 20f), GraphPoint(6f, 18f),
    GraphPoint(7f, 12f)
)

// ────────────────────────────────────────────────────────────────
//  Previews
// ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Simple Line Chart")
@Composable
private fun PreviewSimpleLineChart() {
    CleanGraph(
        points = previewPoints,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(8.dp),
        config = CleanGraphConfig(animationEnabled = false)
    )
}

@Preview(showBackground = true, name = "Area Chart (Curved)")
@Composable
private fun PreviewAreaChart() {
    CleanGraph(
        points = previewPoints,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(8.dp),
        config = CleanGraphConfig(
            area = AreaConfig(visible = true),
            line = LineConfig(curved = true),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "Line with Points")
@Composable
private fun PreviewLineWithPoints() {
    CleanGraph(
        points = previewPoints,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(8.dp),
        config = CleanGraphConfig(
            line = LineConfig(showPoints = true),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "Sparkline Positive")
@Composable
private fun PreviewSparklinePositive() {
    CleanGraph(
        points = previewSparklineUp,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        config = CleanGraphConfig(
            mode = GraphMode.Sparkline,
            line = LineConfig(autoTrendColor = true, curved = true),
            area = AreaConfig(visible = true),
            contentPadding = PaddingValues(4.dp),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "Sparkline Negative")
@Composable
private fun PreviewSparklineNegative() {
    CleanGraph(
        points = previewSparklineDown,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        config = CleanGraphConfig(
            mode = GraphMode.Sparkline,
            line = LineConfig(autoTrendColor = true, curved = true),
            area = AreaConfig(visible = true),
            contentPadding = PaddingValues(4.dp),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "Multi-Line Chart")
@Composable
private fun PreviewMultiLineChart() {
    val lead = GraphSeries(
        name = "Lead Gen",
        points = listOf(
            GraphPoint(1f, 40f), GraphPoint(2f, 55f), GraphPoint(3f, 48f),
            GraphPoint(4f, 62f), GraphPoint(5f, 58f), GraphPoint(6f, 75f)
        ),
        color = Color(0xFF6C7AE0)
    )
    val sales = GraphSeries(
        name = "Sales",
        points = listOf(
            GraphPoint(1f, 20f), GraphPoint(2f, 30f), GraphPoint(3f, 35f),
            GraphPoint(4f, 28f), GraphPoint(5f, 42f), GraphPoint(6f, 50f)
        ),
        color = Color(0xFF4DB6A4)
    )
    val engagement = GraphSeries(
        name = "Engagement",
        points = listOf(
            GraphPoint(1f, 60f), GraphPoint(2f, 52f), GraphPoint(3f, 58f),
            GraphPoint(4f, 70f), GraphPoint(5f, 65f), GraphPoint(6f, 68f)
        ),
        color = Color(0xFFE9A66B)
    )

    CleanGraph(
        series = listOf(lead, sales, engagement),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(8.dp),
        config = CleanGraphConfig(
            legend = LegendConfig(visible = true),
            line = LineConfig(showPoints = true),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "Dense Data (365 points)")
@Composable
private fun PreviewDenseData() {
    val densePoints = (0 until 365).map { i ->
        val x = i.toFloat()
        val y = 50f + 30f * sin(x * 0.05f).toFloat() + (i % 7) * 2f
        GraphPoint(x, y)
    }

    CleanGraph(
        points = densePoints,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(8.dp),
        config = CleanGraphConfig(
            area = AreaConfig(visible = true, alpha = 0.12f),
            line = LineConfig(width = 1.5.dp, defaultColor = Color(0xFF6C7AE0)),
            xAxis = AxisConfig(
                labelCount = 6,
                formatter = { v ->
                    val month = (v / 30).toInt().coerceIn(0, 11)
                    listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")[month]
                }
            ),
            grid = GridConfig(horizontalLines = true, verticalLines = true),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "Custom Formatted Axes")
@Composable
private fun PreviewCustomFormatted() {
    val revenue = listOf(
        GraphPoint(1f, 12f, "Jan"), GraphPoint(2f, 18f, "Feb"),
        GraphPoint(3f, 15f, "Mar"), GraphPoint(4f, 28f, "Apr"),
        GraphPoint(5f, 35f, "May"), GraphPoint(6f, 42f, "Jun")
    )

    CleanGraph(
        points = revenue,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(8.dp),
        config = CleanGraphConfig(
            area = AreaConfig(visible = true, alpha = 0.15f),
            line = LineConfig(curved = true, showPoints = true, defaultColor = Color(0xFF6C7AE0)),
            xAxis = AxisConfig(
                labelCount = 6,
                formatter = { v ->
                    when (v.toInt()) {
                        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"
                        4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
                        else -> ""
                    }
                }
            ),
            yAxis = AxisConfig(formatter = { v -> "$${v.toInt()}M" }),
            animationEnabled = false
        )
    )
}

@Preview(showBackground = true, name = "No Grid / Minimal")
@Composable
private fun PreviewMinimal() {
    CleanGraph(
        points = previewPoints,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
        config = CleanGraphConfig(
            grid = GridConfig(visible = false),
            xAxis = AxisConfig(visible = false),
            line = LineConfig(curved = true, defaultColor = Color(0xFFE06C8E)),
            area = AreaConfig(visible = true, alpha = 0.2f),
            animationEnabled = false
        )
    )
}
