package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.*
import com.example.domain.analytics.*
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val profile by viewModel.businessProfile.collectAsState()
    val metrics by viewModel.monthlyMetrics.collectAsState()
    val products by viewModel.products.collectAsState()
    val segments by viewModel.customerSegments.collectAsState()
    val channels by viewModel.marketingChannels.collectAsState()

    val healthScore = remember(metrics, channels, products) {
        RiskScoringEngine.computeHealthScore(metrics, channels, products)
    }

    val risks = remember(metrics, channels, products) {
        RiskScoringEngine.detectRisks(metrics, channels, products)
    }

    val latestMetric = metrics.lastOrNull()
    val latestRevenue = latestMetric?.revenue ?: 0.0
    val latestCost = (latestMetric?.cogs ?: 0.0) + (latestMetric?.operatingExpenses ?: 0.0) + (latestMetric?.marketingSpend ?: 0.0)
    val latestProfit = latestRevenue - latestCost
    val profitMargin = if (latestRevenue > 0) (latestProfit / latestRevenue) * 100.0 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .testTag("dashboard_scroll_list"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Header Bar
        item {
            DashboardHeader(
                profile = profile,
                healthScore = healthScore,
                onEditProfile = { viewModel.openProfileDialog() },
                onGenerateReport = { viewModel.openReportDialog() }
            )
        }

        // 2. Executive KPI Cards
        item {
            ExecutiveKpisSection(
                revenue = latestRevenue,
                profit = latestProfit,
                profitMargin = profitMargin,
                churnRate = latestMetric?.churnRate ?: 0.0,
                cashBuffer = latestMetric?.cashBuffer ?: 0.0,
                burnRate = latestCost
            )
        }

        // 3. Interactive Revenue Trend Chart
        item {
            RevenueTrendChartCard(
                metrics = metrics,
                onForecastClick = { viewModel.navigateTo(ScreenDestination.Predictive) }
            )
        }

        // 4. Customer Segmentation Donut Chart
        item {
            CustomerSegmentationCard(segments = segments)
        }

        // 5. Marketing Channel ROAS Performance
        item {
            MarketingChannelRoasCard(channels = channels)
        }

        // 6. Top Products & Inventory Turnover
        item {
            TopProductsCard(products = products)
        }

        // 7. Active Risk & Vital Signs Alert
        item {
            RisksAndAlertsCard(
                risks = risks,
                onViewAdvisor = { viewModel.navigateTo(ScreenDestination.AiChat) }
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    profile: BusinessProfileEntity?,
    healthScore: BusinessHealthScore,
    onEditProfile: () -> Unit,
    onGenerateReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy900, Navy950)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile?.companyName ?: "ApexGear Outdoors",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = onEditProfile,
                        modifier = Modifier.size(32.dp).testTag("edit_profile_button")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Navy800,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                    ) {
                        Text(
                            text = profile?.industry ?: "E-Commerce",
                            style = MaterialTheme.typography.labelSmall.copy(color = Cyan400),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "Goal: ${profile?.primaryGoal ?: "Maximize Margin"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Export Brief Button
            IconButton(
                onClick = onGenerateReport,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Navy800)
                    .border(1.dp, Navy700, RoundedCornerShape(12.dp))
                    .size(42.dp)
                    .testTag("export_report_button")
            ) {
                Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = "Export Brief",
                    tint = Cyan400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Health Score Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Navy900,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Cyan500, Emerald500)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = healthScore.ratingGrade,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Navy950
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Business Health Score",
                                style = MaterialTheme.typography.labelMedium.copy(color = Slate400)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${healthScore.overallScore}/100",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald500
                                )
                            )
                        }
                        Text(
                            text = healthScore.summaryText,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate300),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecutiveKpisSection(
    revenue: Double,
    profit: Double,
    profitMargin: Double,
    churnRate: Double,
    cashBuffer: Double,
    burnRate: Double
) {
    val runwayMonths = if (burnRate > 0) cashBuffer / burnRate else 12.0

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMetricCard(
                title = "Monthly Revenue",
                value = revenue.formatCurrency(),
                subValue = "+14.2% MoM",
                subValueColor = Emerald500,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                modifier = Modifier.weight(1f)
            )

            KpiMetricCard(
                title = "Net Profit",
                value = profit.formatCurrency(),
                subValue = "${profitMargin.format(1)}% margin",
                subValueColor = Cyan400,
                icon = Icons.Default.AccountBalanceWallet,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMetricCard(
                title = "Customer Churn",
                value = "${(churnRate * 100).format(1)}%",
                subValue = if (churnRate > 0.04) "Needs focus" else "Healthy",
                subValueColor = if (churnRate > 0.04) Amber500 else Emerald500,
                icon = Icons.Default.PeopleOutline,
                modifier = Modifier.weight(1f)
            )

            KpiMetricCard(
                title = "Operating Runway",
                value = "${runwayMonths.format(1)} Mo",
                subValue = "${cashBuffer.formatCurrency()} buffer",
                subValueColor = Slate300,
                icon = Icons.Default.Shield,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    subValue: String,
    subValueColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, Navy700, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Cyan400,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = subValueColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun RevenueTrendChartCard(
    metrics: List<MonthlyMetricEntity>,
    onForecastClick: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
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
                        text = "Revenue & Expense Trajectory",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                    Text(
                        text = "Touch points to inspect monthly variance",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }

                TextButton(
                    onClick = onForecastClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Cyan400)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("3-Mo Forecast", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Highlight info if scrubbed
            val currentSelected = selectedIndex?.let { if (it in metrics.indices) metrics[it] else null }
            if (currentSelected != null) {
                val totalCost = currentSelected.cogs + currentSelected.operatingExpenses + currentSelected.marketingSpend
                val profit = currentSelected.revenue - totalCost
                Surface(
                    color = Navy800,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${currentSelected.monthName} ${currentSelected.year}: Revenue ${currentSelected.revenue.formatCurrency()}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Net Profit: ${profit.formatCurrency()}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Emerald500, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(metrics) {
                            detectTapGestures { offset ->
                                if (metrics.isNotEmpty()) {
                                    val step = size.width / metrics.size
                                    val idx = (offset.x / step).toInt().coerceIn(0, metrics.size - 1)
                                    selectedIndex = idx
                                }
                            }
                        }
                ) {
                    if (metrics.isEmpty()) return@Canvas

                    val maxRev = (metrics.maxOfOrNull { it.revenue } ?: 100000.0) * 1.15
                    val minRev = 0.0
                    val width = size.width
                    val height = size.height - 25.dp.toPx()

                    // Draw grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = height * (i.toFloat() / gridLines)
                        drawLine(
                            color = Navy700.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    val points = metrics.mapIndexed { idx, m ->
                        val x = if (metrics.size > 1) idx * (width / (metrics.size - 1)) else width / 2
                        val y = height - ((m.revenue - minRev) / (maxRev - minRev) * height).toFloat()
                        Offset(x, y)
                    }

                    val costPoints = metrics.mapIndexed { idx, m ->
                        val cost = m.cogs + m.operatingExpenses + m.marketingSpend
                        val x = if (metrics.size > 1) idx * (width / (metrics.size - 1)) else width / 2
                        val y = height - ((cost - minRev) / (maxRev - minRev) * height).toFloat()
                        Offset(x, y)
                    }

                    // Build Revenue Curve Path
                    val revPath = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val cur = points[i]
                                val cX = (prev.x + cur.x) / 2
                                cubicTo(cX, prev.y, cX, cur.y, cur.x, cur.y)
                            }
                        }
                    }

                    // Fill under curve
                    val fillPath = Path().apply {
                        addPath(revPath)
                        lineTo(points.last().x, height)
                        lineTo(points.first().x, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Cyan500.copy(alpha = 0.35f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Revenue Stroke
                    drawPath(
                        path = revPath,
                        color = Cyan400,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Cost Curve
                    val costPath = Path().apply {
                        if (costPoints.isNotEmpty()) {
                            moveTo(costPoints[0].x, costPoints[0].y)
                            for (i in 1 until costPoints.size) {
                                val prev = costPoints[i - 1]
                                val cur = costPoints[i]
                                val cX = (prev.x + cur.x) / 2
                                cubicTo(cX, prev.y, cX, cur.y, cur.x, cur.y)
                            }
                        }
                    }

                    drawPath(
                        path = costPath,
                        color = Rose500.copy(alpha = 0.7f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    )

                    // Draw markers
                    points.forEachIndexed { index, pt ->
                        val isSel = selectedIndex == index
                        drawCircle(
                            color = Navy900,
                            radius = if (isSel) 7.dp.toPx() else 4.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = if (isSel) White else Cyan400,
                            radius = if (isSel) 5.dp.toPx() else 3.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            // Month Labels & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                metrics.forEachIndexed { idx, m ->
                    Text(
                        text = m.monthName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (selectedIndex == idx) Cyan400 else Slate400,
                            fontWeight = if (selectedIndex == idx) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Cyan400))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Revenue", style = MaterialTheme.typography.labelSmall.copy(color = Slate300))

                Spacer(modifier = Modifier.width(20.dp))

                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Rose500))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Total Expenses", style = MaterialTheme.typography.labelSmall.copy(color = Slate300))
            }
        }
    }
}

@Composable
private fun CustomerSegmentationCard(segments: List<CustomerSegmentEntity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .border(1.dp, Navy700, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Customer Segmentation Model",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )
            Text(
                text = "Value-based tiering & automated targeting actions",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val totalCustomers = segments.sumOf { it.customerCount }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Donut Chart
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (totalCustomers == 0) return@Canvas
                        var startAngle = -90f
                        val strokeWidth = 14.dp.toPx()

                        segments.forEach { seg ->
                            val sweep = (seg.customerCount.toFloat() / totalCustomers) * 360f
                            val color = try {
                                Color(android.graphics.Color.parseColor(seg.colorHex))
                            } catch (e: Exception) {
                                Cyan400
                            }

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += sweep
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalCustomers",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = White
                            )
                        )
                        Text(
                            text = "Users",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Segments Summary
                Column(modifier = Modifier.weight(1f)) {
                    segments.forEach { seg ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(seg.colorHex))
                        } catch (e: Exception) {
                            Cyan400
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = seg.segmentName,
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate200),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Text(
                                text = "${seg.percentageOfBase.format(0)}% (LTV \$${seg.avgLifetimeValue.format(0)})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Tip from VIP segment
            val topSegment = segments.maxByOrNull { it.avgLifetimeValue }
            if (topSegment != null) {
                Surface(
                    color = Navy800,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Amber500, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${topSegment.segmentName}: ${topSegment.keyAction}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketingChannelRoasCard(channels: List<MarketingChannelEntity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
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
                        text = "Marketing Channels & ROAS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                    Text(
                        text = "Return on Ad Spend efficiency ranking",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }

                Icon(Icons.Default.Campaign, contentDescription = null, tint = Cyan400)
            }

            Spacer(modifier = Modifier.height(14.dp))

            val maxRoas = (channels.maxOfOrNull { it.roas } ?: 5.0).coerceAtLeast(1.0)

            channels.forEach { ch ->
                Column(modifier = Modifier.padding(vertical = 5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ch.channelName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate200,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Spend ${ch.monthlySpend.formatCurrency()} → ${ch.revenueAttributed.formatCurrency()}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (ch.roas >= 3.5) Emerald500.copy(alpha = 0.15f) else if (ch.roas >= 2.0) Cyan500.copy(alpha = 0.15f) else Rose500.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${ch.roas.format(1)}x ROAS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (ch.roas >= 3.5) Emerald500 else if (ch.roas >= 2.0) Cyan400 else Rose500,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Progress bar
                    val fraction = (ch.roas / maxRoas).toFloat().coerceIn(0.05f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Navy800)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = if (ch.roas >= 3.0) listOf(Cyan500, Emerald500) else listOf(Cyan600, Amber500)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopProductsCard(products: List<ProductPerformanceEntity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .border(1.dp, Navy700, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Product Profitability Matrix",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )
            Text(
                text = "Volume vs Gross Margin breakdown",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
            )

            Spacer(modifier = Modifier.height(12.dp))

            products.take(4).forEach { p ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = p.productName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate200
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${p.unitsSold} units sold • Stock: ${p.stockLevel} (${p.stockTurnoverDays}d turnover)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = p.revenue.formatCurrency(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )
                        Text(
                            text = "${p.profitMarginPercent.format(1)}% margin",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (p.profitMarginPercent >= 50) Emerald500 else if (p.profitMarginPercent >= 25) Cyan400 else Rose500,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                HorizontalDivider(color = Navy800, thickness = 0.8.dp)
            }
        }
    }
}

@Composable
private fun RisksAndAlertsCard(
    risks: List<RiskIndicator>,
    onViewAdvisor: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Operational Risk Indicators",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                }

                TextButton(
                    onClick = onViewAdvisor,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Ask AI", style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            risks.take(2).forEach { risk ->
                val badgeColor = when (risk.severity) {
                    RiskSeverity.CRITICAL -> Rose500
                    RiskSeverity.WARNING -> Amber500
                    RiskSeverity.HEALTHY -> Emerald500
                }

                Surface(
                    color = Navy800,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = risk.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            )
                            Text(
                                text = risk.metricValue,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = badgeColor,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = risk.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Cyan400, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = risk.recommendationHint,
                                style = MaterialTheme.typography.labelSmall.copy(color = Cyan400)
                            )
                        }
                    }
                }
            }
        }
    }
}
