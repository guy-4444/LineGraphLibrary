package com.guy.linegraphapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guy.linegraph.CleanGraph
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
import com.guy.linegraphapp.ui.theme.LineGraphAppTheme
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LineGraphAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChartShowcase(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ChartShowcase(modifier: Modifier = Modifier) {
    val samplePoints = listOf(
        GraphPoint(1f, 20f, "Jan"),
        GraphPoint(2f, 35f, "Feb"),
        GraphPoint(3f, 28f, "Mar"),
        GraphPoint(4f, 60f, "Apr"),
        GraphPoint(5f, 48f, "May"),
        GraphPoint(6f, 72f, "Jun"),
        GraphPoint(7f, 65f, "Jul")
    )

    val sparklinePositive = listOf(
        GraphPoint(1f, 10f),
        GraphPoint(2f, 14f),
        GraphPoint(3f, 12f),
        GraphPoint(4f, 18f),
        GraphPoint(5f, 22f),
        GraphPoint(6f, 20f),
        GraphPoint(7f, 28f)
    )

    val sparklineNegative = listOf(
        GraphPoint(1f, 30f),
        GraphPoint(2f, 28f),
        GraphPoint(3f, 32f),
        GraphPoint(4f, 24f),
        GraphPoint(5f, 20f),
        GraphPoint(6f, 18f),
        GraphPoint(7f, 12f)
    )

    val leadPoints = listOf(
        GraphPoint(1f, 40f), GraphPoint(2f, 55f), GraphPoint(3f, 48f),
        GraphPoint(4f, 62f), GraphPoint(5f, 58f), GraphPoint(6f, 75f),
        GraphPoint(7f, 70f), GraphPoint(8f, 82f)
    )
    val salesPoints = listOf(
        GraphPoint(1f, 20f), GraphPoint(2f, 30f), GraphPoint(3f, 35f),
        GraphPoint(4f, 28f), GraphPoint(5f, 42f), GraphPoint(6f, 50f),
        GraphPoint(7f, 55f), GraphPoint(8f, 60f)
    )
    val engagementPoints = listOf(
        GraphPoint(1f, 60f), GraphPoint(2f, 52f), GraphPoint(3f, 58f),
        GraphPoint(4f, 70f), GraphPoint(5f, 65f), GraphPoint(6f, 68f),
        GraphPoint(7f, 72f), GraphPoint(8f, 78f)
    )

    val revenuePoints = listOf(
        GraphPoint(1f, 12f, "Jan"),
        GraphPoint(2f, 18f, "Feb"),
        GraphPoint(3f, 15f, "Mar"),
        GraphPoint(4f, 28f, "Apr"),
        GraphPoint(5f, 35f, "May"),
        GraphPoint(6f, 42f, "Jun")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Text(
                text = "CleanGraph",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chart Component Showcase",
                fontSize = 15.sp,
                color = Color(0xFF888888)
            )
        }

        // 1. Simple Line Chart
        item {
            ChartCard(title = "Simple Line Chart", subtitle = "Default configuration") {
                CleanGraph(
                    points = samplePoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            }
        }

        // 2. Area Chart
        item {
            ChartCard(title = "Area Chart", subtitle = "Gradient fill enabled") {
                CleanGraph(
                    points = samplePoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    config = CleanGraphConfig(
                        area = AreaConfig(visible = true),
                        line = LineConfig(curved = true)
                    )
                )
            }
        }

        // 3. Sparklines
        item {
            Text(
                text = "Sparklines",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Compact trend indicators",
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SparklineCard(
                    title = "Revenue",
                    value = "$28K",
                    change = "+18%",
                    positive = true,
                    points = sparklinePositive,
                    modifier = Modifier.weight(1f)
                )
                SparklineCard(
                    title = "Churn",
                    value = "12%",
                    change = "-8%",
                    positive = false,
                    points = sparklineNegative,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Multi-Line Chart
        item {
            ChartCard(title = "Multi-Line Chart", subtitle = "Three series with legend") {
                CleanGraph(
                    series = listOf(
                        GraphSeries(
                            name = "Lead Generation",
                            points = leadPoints,
                            color = Color(0xFF6C7AE0)
                        ),
                        GraphSeries(
                            name = "Sales Conversion",
                            points = salesPoints,
                            color = Color(0xFF4DB6A4)
                        ),
                        GraphSeries(
                            name = "Engagement",
                            points = engagementPoints,
                            color = Color(0xFFE9A66B)
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    config = CleanGraphConfig(
                        legend = LegendConfig(visible = true),
                        area = AreaConfig(visible = false),
                        grid = GridConfig(visible = true),
                        line = LineConfig(showPoints = true)
                    )
                )
            }
        }

        // 5. Custom Formatted Revenue Chart
        item {
            ChartCard(
                title = "Revenue Overview",
                subtitle = "Custom axis formatters & tooltip"
            ) {
                CleanGraph(
                    points = revenuePoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    config = CleanGraphConfig(
                        area = AreaConfig(visible = true, alpha = 0.15f),
                        line = LineConfig(
                            curved = true,
                            showPoints = true,
                            defaultColor = Color(0xFF6C7AE0)
                        ),
                        xAxis = AxisConfig(
                            labelCount = 6,
                            formatter = { value ->
                                when (value.toInt()) {
                                    1 -> "Jan"
                                    2 -> "Feb"
                                    3 -> "Mar"
                                    4 -> "Apr"
                                    5 -> "May"
                                    6 -> "Jun"
                                    else -> ""
                                }
                            }
                        ),
                        yAxis = AxisConfig(
                            formatter = { value ->
                                "$${value.toInt()}M"
                            }
                        ),
                        tooltip = TooltipConfig(
                            formatter = { _, point ->
                                "${point.label ?: ""}\nRevenue: $${point.y.toInt()}M"
                            }
                        )
                    )
                )
            }
        }

        // 6. Dense Data Chart (365 points)
        item {
            val densePoints = remember {
                (0 until 365).map { i ->
                    val x = i.toFloat()
                    val y = 50f + 30f * sin(x * 0.05f).toFloat() +
                            15f * sin(x * 0.12f).toFloat() + (i % 7) * 1.5f
                    GraphPoint(x, y, label = "Day $i")
                }
            }

            ChartCard(
                title = "Daily Metrics (365 Points)",
                subtitle = "Dense dataset with vertical grid"
            ) {
                CleanGraph(
                    points = densePoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    config = CleanGraphConfig(
                        area = AreaConfig(visible = true, alpha = 0.12f),
                        line = LineConfig(
                            width = 1.5.dp,
                            defaultColor = Color(0xFF6C7AE0)
                        ),
                        xAxis = AxisConfig(
                            labelCount = 6,
                            formatter = { value ->
                                val month = (value / 30).toInt().coerceIn(0, 11)
                                listOf(
                                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                                )[month]
                            }
                        ),
                        grid = GridConfig(
                            horizontalLines = true,
                            verticalLines = true
                        ),
                        tooltip = TooltipConfig(
                            formatter = { _, point ->
                                "Day ${point.x.toInt()}\nValue: ${String.format("%.1f", point.y)}"
                            }
                        )
                    )
                )
            }
        }

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ────────────────────────────────────────────────────────────────
//  Previews
// ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Full Showcase")
@Composable
private fun PreviewChartShowcase() {
    LineGraphAppTheme {
        ChartShowcase()
    }
}

@Preview(showBackground = true, name = "Chart Card")
@Composable
private fun PreviewChartCard() {
    val points = listOf(
        GraphPoint(1f, 20f, "Jan"),
        GraphPoint(2f, 35f, "Feb"),
        GraphPoint(3f, 28f, "Mar"),
        GraphPoint(4f, 60f, "Apr")
    )
    LineGraphAppTheme {
        ChartCard(title = "Sample", subtitle = "Preview") {
            CleanGraph(
                points = points,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                config = CleanGraphConfig(animationEnabled = false)
            )
        }
    }
}

@Preview(showBackground = true, name = "Sparkline Card")
@Composable
private fun PreviewSparklineCard() {
    val points = listOf(
        GraphPoint(1f, 10f), GraphPoint(2f, 14f), GraphPoint(3f, 12f),
        GraphPoint(4f, 18f), GraphPoint(5f, 22f), GraphPoint(6f, 28f)
    )
    LineGraphAppTheme {
        SparklineCard(
            title = "Revenue",
            value = "$28K",
            change = "+18%",
            positive = true,
            points = points,
            modifier = Modifier.width(180.dp)
        )
    }
}

@Composable
fun ChartCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun SparklineCard(
    title: String,
    value: String,
    change: String,
    positive: Boolean,
    points: List<GraphPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = change,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (positive) Color(0xFF16A34A) else Color(0xFFDC2626),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            CleanGraph(
                points = points,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                config = CleanGraphConfig(
                    mode = GraphMode.Sparkline,
                    line = LineConfig(
                        autoTrendColor = true,
                        curved = true
                    ),
                    area = AreaConfig(visible = true),
                    contentPadding = PaddingValues(4.dp)
                )
            )
        }
    }
}