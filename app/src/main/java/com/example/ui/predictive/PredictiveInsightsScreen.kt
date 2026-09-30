package com.example.ui.predictive

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.analytics.*
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun PredictiveInsightsScreen(viewModel: MainViewModel) {
    val metrics by viewModel.monthlyMetrics.collectAsState()
    val simAdSpend by viewModel.simAdSpendDelta.collectAsState()
    val simPrice by viewModel.simPriceDelta.collectAsState()
    val simCostRed by viewModel.simCostReductionDelta.collectAsState()

    val regression = remember(metrics) {
        PredictiveEngine.calculateLinearRegression(metrics)
    }

    val forecastPoints = remember(metrics) {
        PredictiveEngine.generateForecast(metrics, periodsAhead = 3)
    }

    val latestMetric = metrics.lastOrNull()
    val baseRevenue = latestMetric?.revenue ?: 50000.0
    val baseExpenses = (latestMetric?.cogs ?: 20000.0) + (latestMetric?.operatingExpenses ?: 15000.0) + (latestMetric?.marketingSpend ?: 5000.0)
    val baseProfit = baseRevenue - baseExpenses

    val (simRevenue, simProfit) = remember(baseRevenue, baseExpenses, simAdSpend, simPrice, simCostRed) {
        PredictiveEngine.simulateScenario(
            baseRevenue = baseRevenue,
            baseExpenses = baseExpenses,
            adSpendDeltaPercent = simAdSpend,
            priceDeltaPercent = simPrice,
            costReductionPercent = simCostRed
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .statusBarsPadding()
            .testTag("predictive_insights_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp)
    ) {
        // Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Cyan500.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoGraph, contentDescription = null, tint = Cyan400)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Predictive Insights & Forecast",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                    Text(
                        text = "Time-series regression & scenario modeling",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Regression Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Navy900,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("MoM Growth Rate", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${regression.averageMonthlyGrowthPercent.format(1)}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald500
                            )
                        )
                    }
                }

                Surface(
                    color = Navy900,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Trend Slope", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${regression.slope.formatCurrency()}/mo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Cyan400
                            )
                        )
                    }
                }

                Surface(
                    color = Navy900,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Fit Confidence (R²)", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(regression.rSquared * 100).format(0)}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Forecast Canvas Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Navy700, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next 3-Month Projection",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )

                        Surface(
                            color = Cyan500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "90% Confidence Band",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Cyan400,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Canvas Forecast Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val allPoints = metrics.map { it.revenue } + forecastPoints.map { it.predictedRevenue }
                            val maxVal = (allPoints.maxOrNull() ?: 100000.0) * 1.2
                            val minVal = 0.0
                            val width = size.width
                            val height = size.height - 20.dp.toPx()
                            val totalCount = metrics.size + forecastPoints.size

                            if (totalCount < 2) return@Canvas

                            // Historical line
                            val histOffsets = metrics.mapIndexed { idx, m ->
                                val x = idx * (width / (totalCount - 1))
                                val y = height - ((m.revenue - minVal) / (maxVal - minVal) * height).toFloat()
                                Offset(x, y)
                            }

                            // Forecast line
                            val foreOffsets = forecastPoints.mapIndexed { idx, f ->
                                val x = (metrics.size + idx) * (width / (totalCount - 1))
                                val y = height - ((f.predictedRevenue - minVal) / (maxVal - minVal) * height).toFloat()
                                Offset(x, y)
                            }

                            // Upper / Lower bounds for forecast
                            val upperOffsets = forecastPoints.mapIndexed { idx, f ->
                                val x = (metrics.size + idx) * (width / (totalCount - 1))
                                val y = height - ((f.upperBound - minVal) / (maxVal - minVal) * height).toFloat()
                                Offset(x, y)
                            }
                            val lowerOffsets = forecastPoints.mapIndexed { idx, f ->
                                val x = (metrics.size + idx) * (width / (totalCount - 1))
                                val y = height - ((f.lowerBound - minVal) / (maxVal - minVal) * height).toFloat()
                                Offset(x, y)
                            }

                            // Draw Historical Path
                            val histPath = Path().apply {
                                moveTo(histOffsets[0].x, histOffsets[0].y)
                                for (i in 1 until histOffsets.size) {
                                    val prev = histOffsets[i - 1]
                                    val cur = histOffsets[i]
                                    val cX = (prev.x + cur.x) / 2
                                    cubicTo(cX, prev.y, cX, cur.y, cur.x, cur.y)
                                }
                            }

                            drawPath(
                                path = histPath,
                                color = Cyan400,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Connect historical to forecast
                            if (histOffsets.isNotEmpty() && foreOffsets.isNotEmpty()) {
                                drawLine(
                                    color = Emerald500,
                                    start = histOffsets.last(),
                                    end = foreOffsets.first(),
                                    strokeWidth = 2.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                                )
                            }

                            // Draw Forecast Path
                            val forePath = Path().apply {
                                if (foreOffsets.isNotEmpty()) {
                                    moveTo(foreOffsets[0].x, foreOffsets[0].y)
                                    for (i in 1 until foreOffsets.size) {
                                        lineTo(foreOffsets[i].x, foreOffsets[i].y)
                                    }
                                }
                            }
                            drawPath(
                                path = forePath,
                                color = Emerald500,
                                style = Stroke(
                                    width = 2.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                )
                            )

                            // Draw forecast confidence band area
                            if (upperOffsets.isNotEmpty() && lowerOffsets.isNotEmpty()) {
                                val bandPath = Path().apply {
                                    moveTo(upperOffsets.first().x, upperOffsets.first().y)
                                    for (i in 1 until upperOffsets.size) lineTo(upperOffsets[i].x, upperOffsets[i].y)
                                    for (i in lowerOffsets.indices.reversed()) lineTo(lowerOffsets[i].x, lowerOffsets[i].y)
                                    close()
                                }
                                drawPath(
                                    path = bandPath,
                                    color = Emerald500.copy(alpha = 0.12f)
                                )
                            }

                            // Marker dots
                            foreOffsets.forEach { pt ->
                                drawCircle(color = Emerald500, radius = 4.dp.toPx(), center = pt)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Projection Breakdown Table
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        forecastPoints.forEach { pt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Navy800)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pt.monthName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                )

                                Text(
                                    text = pt.predictedRevenue.formatCurrency(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald500
                                    )
                                )

                                Text(
                                    text = "Range: ${pt.lowerBound.formatCurrency()} – ${pt.upperBound.formatCurrency()}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Interactive What-If Scenario Simulator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Navy700, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "What-If Scenario Simulator",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            )
                            Text(
                                text = "Test sensitivity of marketing, pricing & cost adjustments",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                            )
                        }

                        Icon(Icons.Default.Tune, contentDescription = null, tint = Cyan400)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider 1: Ad Spend
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Marketing Ad Spend", style = MaterialTheme.typography.bodySmall.copy(color = Slate300))
                            Text(
                                text = if (simAdSpend >= 0) "+${simAdSpend.toInt()}%" else "${simAdSpend.toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(color = Cyan400, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = simAdSpend.toFloat(),
                            onValueChange = { viewModel.updateSimulationSliders(it.toDouble(), simPrice, simCostRed) },
                            valueRange = -30f..50f,
                            steps = 15,
                            colors = SliderDefaults.colors(thumbColor = Cyan400, activeTrackColor = Cyan500)
                        )
                    }

                    // Slider 2: Product Pricing
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Product Pricing Adjustment", style = MaterialTheme.typography.bodySmall.copy(color = Slate300))
                            Text(
                                text = if (simPrice >= 0) "+${simPrice.toInt()}%" else "${simPrice.toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(color = Emerald500, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = simPrice.toFloat(),
                            onValueChange = { viewModel.updateSimulationSliders(simAdSpend, it.toDouble(), simCostRed) },
                            valueRange = -10f..25f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = Emerald500, activeTrackColor = Emerald600)
                        )
                    }

                    // Slider 3: Cost Reduction
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("COGS & OPEX Efficiency Cut", style = MaterialTheme.typography.bodySmall.copy(color = Slate300))
                            Text(
                                text = "${simCostRed.toInt()}% cut",
                                style = MaterialTheme.typography.bodySmall.copy(color = Amber500, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = simCostRed.toFloat(),
                            onValueChange = { viewModel.updateSimulationSliders(simAdSpend, simPrice, it.toDouble()) },
                            valueRange = 0f..20f,
                            steps = 9,
                            colors = SliderDefaults.colors(thumbColor = Amber500, activeTrackColor = Amber500)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulation Output Card
                    Surface(
                        color = Navy800,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Simulated Monthly Impact",
                                style = MaterialTheme.typography.labelMedium.copy(color = Slate400)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Simulated Revenue", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                    Text(
                                        text = simRevenue.formatCurrency(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = White)
                                    )
                                    val revDiff = simRevenue - baseRevenue
                                    Text(
                                        text = if (revDiff >= 0) "+${revDiff.formatCurrency()}" else revDiff.formatCurrency(),
                                        style = MaterialTheme.typography.labelSmall.copy(color = if (revDiff >= 0) Emerald500 else Rose500)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Simulated Net Profit", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                    Text(
                                        text = simProfit.formatCurrency(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Emerald500)
                                    )
                                    val profitDiff = simProfit - baseProfit
                                    Text(
                                        text = if (profitDiff >= 0) "+${profitDiff.formatCurrency()} gain" else "${profitDiff.formatCurrency()} loss",
                                        style = MaterialTheme.typography.labelSmall.copy(color = if (profitDiff >= 0) Emerald500 else Rose500, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
