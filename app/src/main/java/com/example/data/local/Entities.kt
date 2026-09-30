package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val fullName: String,
    val businessName: String,
    val passwordHash: String,
    val salt: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "business_profiles")
data class BusinessProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val companyName: String,
    val industry: String, // "E-Commerce", "SaaS & Tech", "Retail & Storefront", "Hospitality & Food", "Professional Services", "Manufacturing"
    val monthlyBudget: Double,
    val teamSize: Int,
    val primaryGoal: String, // "Increase Profit Margin", "Accelerate Revenue", "Reduce Customer Churn", "Optimize Marketing ROI"
    val targetAudience: String,
    val currencySymbol: String = "$",
    val fiscalYear: String = "2026",
    val stage: String = "Growth" // "Startup", "Growth", "Mature"
)

@Entity(tableName = "monthly_metrics")
data class MonthlyMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val monthIndex: Int, // 1 to 12
    val monthName: String, // "Jan", "Feb", ...
    val year: Int = 2026,
    val revenue: Double,
    val cogs: Double, // Cost of goods sold
    val operatingExpenses: Double,
    val marketingSpend: Double,
    val newCustomers: Int,
    val churnRate: Double, // e.g. 0.042 for 4.2%
    val avgOrderValue: Double,
    val cashBuffer: Double = 50000.0
)

@Entity(tableName = "product_performances")
data class ProductPerformanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val productName: String,
    val category: String,
    val unitsSold: Int,
    val unitPrice: Double,
    val unitCost: Double,
    val revenue: Double,
    val profitMarginPercent: Double,
    val stockLevel: Int,
    val stockTurnoverDays: Int
)

@Entity(tableName = "customer_segments")
data class CustomerSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val segmentName: String, // "VIP & Champions", "Loyal Spenders", "High Potential", "At Risk / Inactive"
    val customerCount: Int,
    val percentageOfBase: Double,
    val avgLifetimeValue: Double,
    val retentionRate: Double,
    val keyAction: String,
    val colorHex: String = "#06B6D4"
)

@Entity(tableName = "marketing_channels")
data class MarketingChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val channelName: String, // "Google Ads", "Meta & Instagram", "Email Marketing", "SEO & Organic", "Influencer"
    val monthlySpend: Double,
    val revenueAttributed: Double,
    val leadsGenerated: Int,
    val customersAcquired: Int,
    val roas: Double // Return on Ad Spend (e.g. 3.4x)
)

@Entity(tableName = "recommendations")
data class BusinessRecommendationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val title: String,
    val category: String, // "Revenue Optimization", "Cost Reduction", "Marketing ROI", "Risk & Churn", "Inventory"
    val priority: String, // "High", "Medium", "Low"
    val summary: String,
    val expectedImpact: String, // "+$14,200 Annual Profit", "Reduce Churn by 18%"
    val actionSteps: String, // Markdown or numbered steps
    val status: String = "Pending", // "Pending", "In Progress", "Completed"
    val isAiGenerated: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
