package com.example.domain.analytics

import com.example.data.local.CustomerSegmentEntity
import com.example.data.local.MarketingChannelEntity
import com.example.data.local.MonthlyMetricEntity
import com.example.data.local.ProductPerformanceEntity
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class RegressionResult(
    val slope: Double,
    val intercept: Double,
    val rSquared: Double,
    val averageMonthlyGrowthPercent: Double
)

data class ForecastPoint(
    val monthName: String,
    val monthIndex: Int,
    val predictedRevenue: Double,
    val lowerBound: Double,
    val upperBound: Double
)

data class RiskIndicator(
    val title: String,
    val severity: RiskSeverity, // CRITICAL, WARNING, HEALTHY
    val description: String,
    val metricValue: String,
    val recommendationHint: String
)

enum class RiskSeverity {
    CRITICAL, WARNING, HEALTHY
}

data class BusinessHealthScore(
    val overallScore: Int, // 0 - 100
    val ratingGrade: String, // "A+", "A", "B", "C", "D"
    val summaryText: String,
    val profitabilityScore: Int, // 0 - 25
    val growthScore: Int, // 0 - 25
    val cashRunwayScore: Int, // 0 - 25
    val customerRetentionScore: Int // 0 - 25
)

object PredictiveEngine {

    fun calculateLinearRegression(metrics: List<MonthlyMetricEntity>): RegressionResult {
        if (metrics.size < 2) {
            return RegressionResult(slope = 0.0, intercept = metrics.firstOrNull()?.revenue ?: 0.0, rSquared = 1.0, averageMonthlyGrowthPercent = 0.0)
        }

        val n = metrics.size.toDouble()
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0
        var sumY2 = 0.0

        metrics.forEachIndexed { index, item ->
            val x = (index + 1).toDouble()
            val y = item.revenue
            sumX += x
            sumY += y
            sumXY += x * y
            sumX2 += x * x
            sumY2 += y * y
        }

        val denominator = (n * sumX2) - (sumX * sumX)
        val slope = if (denominator != 0.0) ((n * sumXY) - (sumX * sumY)) / denominator else 0.0
        val intercept = (sumY - (slope * sumX)) / n

        // Calculate R-squared
        val meanY = sumY / n
        var ssTot = 0.0
        var ssRes = 0.0
        metrics.forEachIndexed { index, item ->
            val x = (index + 1).toDouble()
            val yPred = slope * x + intercept
            ssTot += (item.revenue - meanY).pow(2.0)
            ssRes += (item.revenue - yPred).pow(2.0)
        }
        val rSquared = if (ssTot != 0.0) max(0.0, min(1.0, 1.0 - (ssRes / ssTot))) else 1.0

        val firstRev = metrics.first().revenue
        val lastRev = metrics.last().revenue
        val avgGrowth = if (firstRev > 0) ((lastRev - firstRev) / firstRev / (metrics.size - 1)) * 100.0 else 0.0

        return RegressionResult(slope, intercept, rSquared, avgGrowth)
    }

    fun generateForecast(metrics: List<MonthlyMetricEntity>, periodsAhead: Int = 3): List<ForecastPoint> {
        if (metrics.isEmpty()) return emptyList()
        val reg = calculateLinearRegression(metrics)
        val lastIndex = metrics.size

        // Residual std dev for confidence interval
        val residuals = metrics.mapIndexed { idx, m ->
            val pred = reg.slope * (idx + 1) + reg.intercept
            (m.revenue - pred).pow(2.0)
        }
        val stdDev = if (metrics.size > 2) sqrt(residuals.sum() / (metrics.size - 2)) else 1000.0

        val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val forecast = mutableListOf<ForecastPoint>()

        for (i in 1..periodsAhead) {
            val step = lastIndex + i
            val pred = max(0.0, reg.slope * step + reg.intercept)
            val margin = stdDev * 1.645 * sqrt(1.0 + (1.0 / metrics.size) + ((step - (metrics.size / 2.0)).pow(2.0) / 100.0))
            val monthName = monthLabels[(metrics.last().monthIndex + i - 1) % 12] + " (F)"
            forecast.add(
                ForecastPoint(
                    monthName = monthName,
                    monthIndex = step,
                    predictedRevenue = pred,
                    lowerBound = max(0.0, pred - margin),
                    upperBound = pred + margin
                )
            )
        }
        return forecast
    }

    fun simulateScenario(
        baseRevenue: Double,
        baseExpenses: Double,
        adSpendDeltaPercent: Double,
        priceDeltaPercent: Double,
        costReductionPercent: Double
    ): Pair<Double, Double> { // Pair of (Simulated Revenue, Simulated Profit)
        // Ad spend elasticity ~ 0.45
        val adEffect = (adSpendDeltaPercent / 100.0) * 0.45
        // Price elasticity ~ -0.65
        val priceVolumeEffect = (priceDeltaPercent / 100.0) * -0.65
        val priceDirectEffect = priceDeltaPercent / 100.0

        val revMultiplier = 1.0 + adEffect + priceVolumeEffect + priceDirectEffect
        val simulatedRevenue = max(0.0, baseRevenue * revMultiplier)

        val simExpenses = baseExpenses * (1.0 - (costReductionPercent / 100.0)) + (baseExpenses * 0.15 * (adSpendDeltaPercent / 100.0))
        val simulatedProfit = simulatedRevenue - simExpenses

        return Pair(simulatedRevenue, simulatedProfit)
    }
}

object RiskScoringEngine {

    fun computeHealthScore(
        metrics: List<MonthlyMetricEntity>,
        channels: List<MarketingChannelEntity>,
        products: List<ProductPerformanceEntity>
    ): BusinessHealthScore {
        if (metrics.isEmpty()) {
            return BusinessHealthScore(75, "B+", "Stable baseline with room for profit optimization.", 18, 18, 20, 19)
        }

        val latest = metrics.last()
        val totalRevenue = latest.revenue
        val totalCost = latest.cogs + latest.operatingExpenses + latest.marketingSpend
        val netProfit = totalRevenue - totalCost
        val netMargin = if (totalRevenue > 0) (netProfit / totalRevenue) else 0.0

        // 1. Profitability Score (0 - 25)
        val profitScore = when {
            netMargin >= 0.25 -> 25
            netMargin >= 0.15 -> 22
            netMargin >= 0.08 -> 18
            netMargin >= 0.02 -> 13
            netMargin >= 0.0 -> 10
            else -> 5
        }

        // 2. Growth Score (0 - 25)
        val reg = PredictiveEngine.calculateLinearRegression(metrics)
        val growthScore = when {
            reg.averageMonthlyGrowthPercent >= 8.0 -> 25
            reg.averageMonthlyGrowthPercent >= 4.0 -> 22
            reg.averageMonthlyGrowthPercent >= 1.5 -> 18
            reg.averageMonthlyGrowthPercent >= 0.0 -> 14
            reg.averageMonthlyGrowthPercent >= -3.0 -> 10
            else -> 6
        }

        // 3. Cash Runway Score (0 - 25)
        val monthlyBurn = max(1000.0, totalCost)
        val runwayMonths = latest.cashBuffer / monthlyBurn
        val runwayScore = when {
            runwayMonths >= 12.0 -> 25
            runwayMonths >= 6.0 -> 22
            runwayMonths >= 3.5 -> 18
            runwayMonths >= 2.0 -> 12
            else -> 6
        }

        // 4. Customer Retention & Efficiency Score (0 - 25)
        val churn = latest.churnRate
        val avgRoas = if (channels.isNotEmpty()) channels.map { it.roas }.average() else 2.5
        var retentionScore = when {
            churn <= 0.02 -> 15
            churn <= 0.04 -> 12
            churn <= 0.07 -> 9
            else -> 5
        }
        retentionScore += when {
            avgRoas >= 4.0 -> 10
            avgRoas >= 2.5 -> 8
            avgRoas >= 1.8 -> 5
            else -> 2
        }

        val overall = min(100, profitScore + growthScore + runwayScore + retentionScore)
        val grade = when {
            overall >= 90 -> "A+"
            overall >= 82 -> "A"
            overall >= 74 -> "B+"
            overall >= 65 -> "B"
            overall >= 55 -> "C"
            else -> "D"
        }

        val summary = when {
            overall >= 80 -> "Strong financial fundamentals with scalable growth trajectory."
            overall >= 65 -> "Solid operating base; optimize marketing ROAS and cut low-margin overhead."
            else -> "Immediate attention required on cash runway and high customer acquisition cost."
        }

        return BusinessHealthScore(
            overallScore = overall,
            ratingGrade = grade,
            summaryText = summary,
            profitabilityScore = profitScore,
            growthScore = growthScore,
            cashRunwayScore = runwayScore,
            customerRetentionScore = retentionScore
        )
    }

    fun detectRisks(
        metrics: List<MonthlyMetricEntity>,
        channels: List<MarketingChannelEntity>,
        products: List<ProductPerformanceEntity>
    ): List<RiskIndicator> {
        val risks = mutableListOf<RiskIndicator>()

        if (metrics.isNotEmpty()) {
            val latest = metrics.last()
            // 1. Churn Risk
            if (latest.churnRate > 0.045) {
                risks.add(
                    RiskIndicator(
                        title = "Elevated Customer Churn Rate",
                        severity = if (latest.churnRate > 0.07) RiskSeverity.CRITICAL else RiskSeverity.WARNING,
                        description = "Monthly customer churn is currently ${(latest.churnRate * 100).format(1)}%, outpacing healthy industry benchmarks (under 3.5%).",
                        metricValue = "${(latest.churnRate * 100).format(1)}% / mo",
                        recommendationHint = "Deploy targeted onboarding email flows and survey cancelling customers immediately."
                    )
                )
            }

            // 2. Cash Runway Alert
            val totalCost = latest.cogs + latest.operatingExpenses + latest.marketingSpend
            val runway = if (totalCost > 0) latest.cashBuffer / totalCost else 12.0
            if (runway < 4.0) {
                risks.add(
                    RiskIndicator(
                        title = "Tight Liquidity Runway",
                        severity = if (runway < 2.5) RiskSeverity.CRITICAL else RiskSeverity.WARNING,
                        description = "Current cash reserves (${latest.cashBuffer.formatCurrency()}) provide only ${runway.format(1)} months of operating runway at current burn rate.",
                        metricValue = "${runway.format(1)} Mo Runway",
                        recommendationHint = "Conserve discretionary marketing spend and negotiate supplier payment terms."
                    )
                )
            }
        }

        // 3. Inefficient Ad Channels
        channels.filter { it.roas < 2.0 && it.monthlySpend > 500 }.forEach { ch ->
            risks.add(
                RiskIndicator(
                    title = "Underperforming Channel: ${ch.channelName}",
                    severity = RiskSeverity.WARNING,
                    description = "${ch.channelName} generated only ${ch.roas.format(2)}x ROAS on a spend of ${ch.monthlySpend.formatCurrency()}.",
                    metricValue = "${ch.roas.format(2)}x ROAS",
                    recommendationHint = "Pause or reallocate 30% of this budget to top-performing channels."
                )
            )
        }

        // 4. Low Margin or Overstocked Products
        products.filter { it.profitMarginPercent < 15.0 }.take(2).forEach { p ->
            risks.add(
                RiskIndicator(
                    title = "Sub-15% Margin on ${p.productName}",
                    severity = RiskSeverity.WARNING,
                    description = "${p.productName} has a narrow profit margin of ${p.profitMarginPercent.format(1)}% (Cost: ${p.unitCost.formatCurrency()}, Price: ${p.unitPrice.formatCurrency()}).",
                    metricValue = "${p.profitMarginPercent.format(1)}% Margin",
                    recommendationHint = "Bundle with high-margin accessories or renegotiate unit manufacturing cost."
                )
            )
        }

        if (risks.isEmpty()) {
            risks.add(
                RiskIndicator(
                    title = "All Key Vital Signs Healthy",
                    severity = RiskSeverity.HEALTHY,
                    description = "Margins, cash runway, and advertising channels are performing within optimal target ranges.",
                    metricValue = "Optimal",
                    recommendationHint = "Consider accelerating growth reinvestments."
                )
            )
        }

        return risks
    }
}

fun Double.format(digits: Int): String = String.format("%.${digits}f", this)
fun Double.formatCurrency(symbol: String = "$"): String {
    return if (this >= 1000000) {
        "$symbol${String.format("%.2f", this / 1000000.0)}M"
    } else if (this >= 1000) {
        "$symbol${String.format("%,.0f", this)}"
    } else {
        "$symbol${String.format("%.2f", this)}"
    }
}
