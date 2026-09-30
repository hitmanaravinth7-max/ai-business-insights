package com.example.data.repository

import com.example.data.local.*
import com.example.data.remote.*
import com.example.domain.analytics.PredictiveEngine
import com.example.domain.analytics.RiskScoringEngine
import com.example.domain.analytics.format
import com.example.domain.analytics.formatCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BusinessRepository(
    private val appDao: AppDao
) {
    fun getProfileFlow(userId: Long): Flow<BusinessProfileEntity?> =
        appDao.getBusinessProfileFlow(userId)

    suspend fun getProfile(userId: Long): BusinessProfileEntity? = withContext(Dispatchers.IO) {
        appDao.getBusinessProfile(userId)
    }

    suspend fun saveProfile(profile: BusinessProfileEntity): Long = withContext(Dispatchers.IO) {
        if (profile.id == 0L) {
            appDao.insertBusinessProfile(profile)
        } else {
            appDao.updateBusinessProfile(profile)
            profile.id
        }
    }

    fun getMonthlyMetricsFlow(businessId: Long): Flow<List<MonthlyMetricEntity>> =
        appDao.getMonthlyMetricsFlow(businessId)

    fun getProductsFlow(businessId: Long): Flow<List<ProductPerformanceEntity>> =
        appDao.getProductsFlow(businessId)

    fun getSegmentsFlow(businessId: Long): Flow<List<CustomerSegmentEntity>> =
        appDao.getSegmentsFlow(businessId)

    fun getChannelsFlow(businessId: Long): Flow<List<MarketingChannelEntity>> =
        appDao.getChannelsFlow(businessId)

    fun getRecommendationsFlow(businessId: Long): Flow<List<BusinessRecommendationEntity>> =
        appDao.getRecommendationsFlow(businessId)

    suspend fun updateRecommendationStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        appDao.updateRecommendationStatus(id, status)
    }

    fun getChatMessagesFlow(businessId: Long): Flow<List<ChatMessageEntity>> =
        appDao.getChatMessagesFlow(businessId)

    suspend fun addChatMessage(businessId: Long, isUser: Boolean, text: String) = withContext(Dispatchers.IO) {
        appDao.insertChatMessage(
            ChatMessageEntity(
                businessId = businessId,
                isUser = isUser,
                text = text
            )
        )
    }

    suspend fun addMonthlyMetric(metric: MonthlyMetricEntity) = withContext(Dispatchers.IO) {
        appDao.insertMonthlyMetric(metric)
    }

    suspend fun addProduct(product: ProductPerformanceEntity) = withContext(Dispatchers.IO) {
        appDao.insertProduct(product)
    }

    suspend fun addChannel(channel: MarketingChannelEntity) = withContext(Dispatchers.IO) {
        appDao.insertChannel(channel)
    }

    suspend fun seedDemoDataset(userId: Long, datasetType: String): BusinessProfileEntity = withContext(Dispatchers.IO) {
        when (datasetType) {
            "SaaS" -> seedSaaSData(userId)
            "Bakery" -> seedBakeryData(userId)
            else -> seedRetailData(userId)
        }
    }

    private suspend fun seedRetailData(userId: Long): BusinessProfileEntity {
        val profile = BusinessProfileEntity(
            userId = userId,
            companyName = "ApexGear Outdoors",
            industry = "E-Commerce & Retail",
            monthlyBudget = 18500.0,
            teamSize = 12,
            primaryGoal = "Increase Profit Margin",
            targetAudience = "Hikers, backpackers, and outdoor enthusiasts aged 24-48",
            currencySymbol = "$",
            fiscalYear = "2026",
            stage = "Growth"
        )
        val profileId = appDao.insertBusinessProfile(profile)
        val businessId = profileId

        // Clear previous data for this business
        appDao.clearMonthlyMetrics(businessId)
        appDao.clearProducts(businessId)
        appDao.clearSegments(businessId)
        appDao.clearChannels(businessId)
        appDao.clearRecommendations(businessId)
        appDao.clearChatMessages(businessId)

        // Seed 8 months of data
        val months = listOf(
            MonthlyMetricEntity(businessId = businessId, monthIndex = 1, monthName = "Jan", revenue = 48500.0, cogs = 24200.0, operatingExpenses = 14200.0, marketingSpend = 6500.0, newCustomers = 280, churnRate = 0.048, avgOrderValue = 88.0, cashBuffer = 65000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 2, monthName = "Feb", revenue = 51200.0, cogs = 25100.0, operatingExpenses = 14500.0, marketingSpend = 6800.0, newCustomers = 310, churnRate = 0.046, avgOrderValue = 91.0, cashBuffer = 66500.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 3, monthName = "Mar", revenue = 55800.0, cogs = 26900.0, operatingExpenses = 14800.0, marketingSpend = 7200.0, newCustomers = 345, churnRate = 0.042, avgOrderValue = 94.0, cashBuffer = 72000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 4, monthName = "Apr", revenue = 61400.0, cogs = 29500.0, operatingExpenses = 15200.0, marketingSpend = 7900.0, newCustomers = 390, churnRate = 0.039, avgOrderValue = 96.0, cashBuffer = 78000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 5, monthName = "May", revenue = 68900.0, cogs = 32800.0, operatingExpenses = 15900.0, marketingSpend = 8400.0, newCustomers = 440, churnRate = 0.038, avgOrderValue = 98.0, cashBuffer = 86000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 6, monthName = "Jun", revenue = 74200.0, cogs = 35100.0, operatingExpenses = 16400.0, marketingSpend = 8900.0, newCustomers = 485, churnRate = 0.037, avgOrderValue = 102.0, cashBuffer = 94500.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 7, monthName = "Jul", revenue = 79800.0, cogs = 37600.0, operatingExpenses = 17100.0, marketingSpend = 9400.0, newCustomers = 520, churnRate = 0.035, avgOrderValue = 105.0, cashBuffer = 103000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 8, monthName = "Aug", revenue = 84500.0, cogs = 39800.0, operatingExpenses = 17500.0, marketingSpend = 9800.0, newCustomers = 560, churnRate = 0.034, avgOrderValue = 108.0, cashBuffer = 114000.0)
        )
        appDao.insertAllMonthlyMetrics(months)

        // Seed products
        val products = listOf(
            ProductPerformanceEntity(businessId = businessId, productName = "Ultralight Alpine Tent", category = "Shelter", unitsSold = 220, unitPrice = 289.0, unitCost = 138.0, revenue = 63580.0, profitMarginPercent = 52.2, stockLevel = 84, stockTurnoverDays = 32),
            ProductPerformanceEntity(businessId = businessId, productName = "Carbon Trekking Poles (Pair)", category = "Gear", unitsSold = 480, unitPrice = 79.0, unitCost = 28.0, revenue = 37920.0, profitMarginPercent = 64.5, stockLevel = 190, stockTurnoverDays = 26),
            ProductPerformanceEntity(businessId = businessId, productName = "Hydro-Shield Rain Jacket", category = "Apparel", unitsSold = 310, unitPrice = 149.0, unitCost = 82.0, revenue = 46190.0, profitMarginPercent = 44.9, stockLevel = 62, stockTurnoverDays = 44),
            ProductPerformanceEntity(businessId = businessId, productName = "Merino Wool Thermal Base", category = "Apparel", unitsSold = 590, unitPrice = 64.0, unitCost = 22.0, revenue = 37760.0, profitMarginPercent = 65.6, stockLevel = 310, stockTurnoverDays = 21),
            ProductPerformanceEntity(businessId = businessId, productName = "Titanium Cookset & Burner", category = "Camp Kitchen", unitsSold = 290, unitPrice = 54.0, unitCost = 39.0, revenue = 15660.0, profitMarginPercent = 27.7, stockLevel = 420, stockTurnoverDays = 78),
            ProductPerformanceEntity(businessId = businessId, productName = "Solar Trail Powerbank 10k", category = "Electronics", unitsSold = 180, unitPrice = 49.0, unitCost = 41.0, revenue = 8820.0, profitMarginPercent = 16.3, stockLevel = 260, stockTurnoverDays = 94)
        )
        appDao.insertAllProducts(products)

        // Seed Customer Segments
        val segments = listOf(
            CustomerSegmentEntity(businessId = businessId, segmentName = "VIP Mountain Trekkers", customerCount = 420, percentageOfBase = 15.0, avgLifetimeValue = 680.0, retentionRate = 0.88, keyAction = "Invite to exclusive early gear drop and offer bespoke bundle accessories.", colorHex = "#06B6D4"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "Seasonal Explorers", customerCount = 890, percentageOfBase = 32.0, avgLifetimeValue = 310.0, retentionRate = 0.64, keyAction = "Send seasonal spring/autumn trip prep checklists with cross-sell vouchers.", colorHex = "#10B981"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "First-Time Day Hikers", customerCount = 1050, percentageOfBase = 38.0, avgLifetimeValue = 115.0, retentionRate = 0.42, keyAction = "Automate 3-part trail guide email sequence with 15% second-order discount.", colorHex = "#F59E0B"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "Dormant / At-Risk", customerCount = 420, percentageOfBase = 15.0, avgLifetimeValue = 180.0, retentionRate = 0.22, keyAction = "Reactivation win-back campaign with free gift on orders over $50.", colorHex = "#EF4444")
        )
        appDao.insertAllSegments(segments)

        // Seed Marketing Channels
        val channels = listOf(
            MarketingChannelEntity(businessId = businessId, channelName = "Google Search Ads", monthlySpend = 3400.0, revenueAttributed = 14960.0, leadsGenerated = 890, customersAcquired = 175, roas = 4.4),
            MarketingChannelEntity(businessId = businessId, channelName = "Meta & Instagram", monthlySpend = 3200.0, revenueAttributed = 9280.0, leadsGenerated = 1450, customersAcquired = 112, roas = 2.9),
            MarketingChannelEntity(businessId = businessId, channelName = "Klaviyo Email Automations", monthlySpend = 450.0, revenueAttributed = 8100.0, leadsGenerated = 3200, customersAcquired = 195, roas = 18.0),
            MarketingChannelEntity(businessId = businessId, channelName = "Outdoor Influencer Partners", monthlySpend = 1800.0, revenueAttributed = 4680.0, leadsGenerated = 620, customersAcquired = 54, roas = 2.6),
            MarketingChannelEntity(businessId = businessId, channelName = "Organic SEO & Blog Content", monthlySpend = 950.0, revenueAttributed = 6650.0, leadsGenerated = 2100, customersAcquired = 82, roas = 7.0)
        )
        appDao.insertAllChannels(channels)

        // Seed Initial Prioritized Recommendations
        val recs = listOf(
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Reallocate $1,000 from Meta Ads to High-ROAS Email & Search",
                category = "Marketing ROI",
                priority = "High",
                summary = "Meta ad ROAS has dropped to 2.9x while Search Ads deliver 4.4x and Email produces 18.0x return.",
                expectedImpact = "+$3,200 Monthly Net Revenue",
                actionSteps = "1. Cap low-intent Meta prospecting campaigns.\n2. Scale Google Shopping campaigns targeting 'ultralight tent' keywords.\n3. Increase post-purchase email flow frequency.",
                status = "In Progress",
                isAiGenerated = true
            ),
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Liquidate Solar Trail Powerbank Overstock",
                category = "Inventory & Cash Flow",
                priority = "High",
                summary = "Solar Powerbank inventory has a 94-day turnover rate with only 16.3% profit margin.",
                expectedImpact = "Free up $10,600 trapped cash flow",
                actionSteps = "1. Bundle the powerbank with high-margin Alpine Tents at a 20% bundle discount.\n2. Stop replenishment orders until inventory falls below 45 units.",
                status = "Pending",
                isAiGenerated = true
            ),
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Launch VIP Concierge Up-Sell for Top 15% Customers",
                category = "Revenue Optimization",
                priority = "Medium",
                summary = "Your 420 VIP Mountain Trekkers drive an LTV of $680 with 88% retention.",
                expectedImpact = "+$14,500 Quarterly Gross Profit",
                actionSteps = "1. Segment customers with >$500 lifetime spend.\n2. Send personalized SMS/email preview of limited-edition trekking gear.",
                status = "Pending",
                isAiGenerated = true
            ),
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Negotiate Titanium Cookset Supplier Unit Cost",
                category = "Cost Reduction",
                priority = "Medium",
                summary = "Cookset unit cost ($39 on $54 retail) limits margin to 27.7% compared to category average of 54%.",
                expectedImpact = "+3.5% Overall Product Line Margin",
                actionSteps = "1. Request tiered volume pricing for next production run.\n2. Explore secondary alternative suppliers.",
                status = "Pending",
                isAiGenerated = true
            )
        )
        appDao.insertAllRecommendations(recs)

        // Seed introductory chat message
        appDao.insertChatMessage(
            ChatMessageEntity(
                businessId = businessId,
                isUser = false,
                text = "Welcome to your AI Business Consultant! I have analyzed ApexGear Outdoors' latest numbers. Your revenue is trending upwards (+14.2% projected), but we have key opportunities to liberate $10.6K in slow inventory and boost ad ROAS. Ask me any question about your profitability, marketing channels, or growth strategy!"
            )
        )

        return profile.copy(id = profileId)
    }

    private suspend fun seedSaaSData(userId: Long): BusinessProfileEntity {
        val profile = BusinessProfileEntity(
            userId = userId,
            companyName = "CloudPulse Analytics",
            industry = "SaaS & Tech",
            monthlyBudget = 24000.0,
            teamSize = 8,
            primaryGoal = "Reduce Customer Churn",
            targetAudience = "B2B SaaS product managers and growth engineers",
            currencySymbol = "$",
            fiscalYear = "2026",
            stage = "Growth"
        )
        val profileId = appDao.insertBusinessProfile(profile)
        val businessId = profileId

        appDao.clearMonthlyMetrics(businessId)
        appDao.clearProducts(businessId)
        appDao.clearSegments(businessId)
        appDao.clearChannels(businessId)
        appDao.clearRecommendations(businessId)
        appDao.clearChatMessages(businessId)

        val months = listOf(
            MonthlyMetricEntity(businessId = businessId, monthIndex = 1, monthName = "Jan", revenue = 32000.0, cogs = 7200.0, operatingExpenses = 18500.0, marketingSpend = 8500.0, newCustomers = 45, churnRate = 0.058, avgOrderValue = 240.0, cashBuffer = 92000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 2, monthName = "Feb", revenue = 35400.0, cogs = 7600.0, operatingExpenses = 18800.0, marketingSpend = 8800.0, newCustomers = 52, churnRate = 0.054, avgOrderValue = 245.0, cashBuffer = 94000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 3, monthName = "Mar", revenue = 39800.0, cogs = 8100.0, operatingExpenses = 19200.0, marketingSpend = 9200.0, newCustomers = 61, churnRate = 0.049, avgOrderValue = 250.0, cashBuffer = 98000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 4, monthName = "Apr", revenue = 44200.0, cogs = 8700.0, operatingExpenses = 19600.0, marketingSpend = 9800.0, newCustomers = 68, churnRate = 0.045, avgOrderValue = 255.0, cashBuffer = 104000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 5, monthName = "May", revenue = 49100.0, cogs = 9400.0, operatingExpenses = 20200.0, marketingSpend = 10400.0, newCustomers = 76, churnRate = 0.042, avgOrderValue = 260.0, cashBuffer = 112000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 6, monthName = "Jun", revenue = 54800.0, cogs = 10100.0, operatingExpenses = 20800.0, marketingSpend = 11000.0, newCustomers = 84, churnRate = 0.039, avgOrderValue = 265.0, cashBuffer = 124000.0)
        )
        appDao.insertAllMonthlyMetrics(months)

        val products = listOf(
            ProductPerformanceEntity(businessId = businessId, productName = "Growth Tier (Annual)", category = "Subscription", unitsSold = 115, unitPrice = 299.0, unitCost = 42.0, revenue = 34385.0, profitMarginPercent = 85.9, stockLevel = 999, stockTurnoverDays = 0),
            ProductPerformanceEntity(businessId = businessId, productName = "Starter Tier (Monthly)", category = "Subscription", unitsSold = 240, unitPrice = 79.0, unitCost = 18.0, revenue = 18960.0, profitMarginPercent = 77.2, stockLevel = 999, stockTurnoverDays = 0),
            ProductPerformanceEntity(businessId = businessId, productName = "Enterprise Custom SLA", category = "Add-On", unitsSold = 14, unitPrice = 850.0, unitCost = 120.0, revenue = 11900.0, profitMarginPercent = 85.8, stockLevel = 999, stockTurnoverDays = 0)
        )
        appDao.insertAllProducts(products)

        val segments = listOf(
            CustomerSegmentEntity(businessId = businessId, segmentName = "Enterprise Annual", customerCount = 120, percentageOfBase = 22.0, avgLifetimeValue = 3500.0, retentionRate = 0.94, keyAction = "Assign dedicated technical success manager and quarterly business reviews.", colorHex = "#06B6D4"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "Growth Scale-Ups", customerCount = 280, percentageOfBase = 52.0, avgLifetimeValue = 950.0, retentionRate = 0.78, keyAction = "Introduce feature unlock notifications when approaching usage thresholds.", colorHex = "#10B981"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "At-Risk Monthly Starters", customerCount = 140, percentageOfBase = 26.0, avgLifetimeValue = 230.0, retentionRate = 0.44, keyAction = "Trigger automated in-app onboarding checklist for users with low 7-day logins.", colorHex = "#EF4444")
        )
        appDao.insertAllSegments(segments)

        val channels = listOf(
            MarketingChannelEntity(businessId = businessId, channelName = "Developer Community & Podcasts", monthlySpend = 4200.0, revenueAttributed = 18900.0, leadsGenerated = 940, customersAcquired = 48, roas = 4.5),
            MarketingChannelEntity(businessId = businessId, channelName = "Google Search (High Intent)", monthlySpend = 3800.0, revenueAttributed = 14440.0, leadsGenerated = 520, customersAcquired = 32, roas = 3.8),
            MarketingChannelEntity(businessId = businessId, channelName = "LinkedIn Ads (B2B)", monthlySpend = 3000.0, revenueAttributed = 6300.0, leadsGenerated = 310, customersAcquired = 12, roas = 2.1)
        )
        appDao.insertAllChannels(channels)

        val recs = listOf(
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Incentivize Monthly-to-Annual Subscription Conversion",
                category = "Revenue & Churn",
                priority = "High",
                summary = "Annual subscribers have a 94% retention rate vs only 44% for monthly users.",
                expectedImpact = "+$28,000 Annualized ARR, Churn reduction from 3.9% to 2.4%",
                actionSteps = "Offer 2 months free + premium data retention for upgrading to annual before Day 30.",
                status = "In Progress",
                isAiGenerated = true
            ),
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Trim Underperforming LinkedIn Ad Spend",
                category = "Marketing ROI",
                priority = "Medium",
                summary = "LinkedIn CAC is $250/customer with only 2.1x ROAS, vs $87 CAC on Developer Podcasts.",
                expectedImpact = "Save $1,500/mo or acquire 17 extra customers on top channel",
                actionSteps = "Shift 50% of LinkedIn ad budget to developer podcast sponsorship slots.",
                status = "Pending",
                isAiGenerated = true
            )
        )
        appDao.insertAllRecommendations(recs)

        appDao.insertChatMessage(
            ChatMessageEntity(
                businessId = businessId,
                isUser = false,
                text = "Hello! I am your CloudPulse AI Consultant. Your SaaS MRR is accelerating nicely toward $55K/month. My top priority recommendation is driving monthly users into annual plans to slash churn from 3.9% down to 2.4%. How can I help you today?"
            )
        )

        return profile.copy(id = profileId)
    }

    private suspend fun seedBakeryData(userId: Long): BusinessProfileEntity {
        val profile = BusinessProfileEntity(
            userId = userId,
            companyName = "Baker's Hearth Artisan Bakery",
            industry = "Hospitality & Food",
            monthlyBudget = 12000.0,
            teamSize = 9,
            primaryGoal = "Optimize Ad Spend",
            targetAudience = "Neighborhood foodies, specialty coffee drinkers, catering clients",
            currencySymbol = "$",
            fiscalYear = "2026",
            stage = "Mature"
        )
        val profileId = appDao.insertBusinessProfile(profile)
        val businessId = profileId

        appDao.clearMonthlyMetrics(businessId)
        appDao.clearProducts(businessId)
        appDao.clearSegments(businessId)
        appDao.clearChannels(businessId)
        appDao.clearRecommendations(businessId)
        appDao.clearChatMessages(businessId)

        val months = listOf(
            MonthlyMetricEntity(businessId = businessId, monthIndex = 1, monthName = "Jan", revenue = 28000.0, cogs = 10500.0, operatingExpenses = 11200.0, marketingSpend = 1800.0, newCustomers = 320, churnRate = 0.065, avgOrderValue = 18.5, cashBuffer = 38000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 2, monthName = "Feb", revenue = 31000.0, cogs = 11400.0, operatingExpenses = 11500.0, marketingSpend = 2100.0, newCustomers = 360, churnRate = 0.058, avgOrderValue = 19.2, cashBuffer = 41000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 3, monthName = "Mar", revenue = 34500.0, cogs = 12400.0, operatingExpenses = 11800.0, marketingSpend = 2200.0, newCustomers = 410, churnRate = 0.052, avgOrderValue = 20.1, cashBuffer = 46000.0),
            MonthlyMetricEntity(businessId = businessId, monthIndex = 4, monthName = "Apr", revenue = 38200.0, cogs = 13600.0, operatingExpenses = 12200.0, marketingSpend = 2400.0, newCustomers = 450, churnRate = 0.048, avgOrderValue = 21.0, cashBuffer = 52000.0)
        )
        appDao.insertAllMonthlyMetrics(months)

        val products = listOf(
            ProductPerformanceEntity(businessId = businessId, productName = "Sourdough Country Loaf", category = "Bread", unitsSold = 1420, unitPrice = 9.5, unitCost = 2.4, revenue = 13490.0, profitMarginPercent = 74.7, stockLevel = 120, stockTurnoverDays = 1),
            ProductPerformanceEntity(businessId = businessId, productName = "Almond Croissant & Pastry", category = "Pastry", unitsSold = 1180, unitPrice = 5.5, unitCost = 1.6, revenue = 6490.0, profitMarginPercent = 70.9, stockLevel = 80, stockTurnoverDays = 1),
            ProductPerformanceEntity(businessId = businessId, productName = "Corporate Morning Catering Box", category = "Catering", unitsSold = 85, unitPrice = 140.0, unitCost = 48.0, revenue = 11900.0, profitMarginPercent = 65.7, stockLevel = 25, stockTurnoverDays = 3)
        )
        appDao.insertAllProducts(products)

        val segments = listOf(
            CustomerSegmentEntity(businessId = businessId, segmentName = "Daily Morning Commuters", customerCount = 650, percentageOfBase = 48.0, avgLifetimeValue = 180.0, retentionRate = 0.82, keyAction = "Launch digital stamp card on mobile wallet.", colorHex = "#06B6D4"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "Corporate Catering Accounts", customerCount = 45, percentageOfBase = 12.0, avgLifetimeValue = 1600.0, retentionRate = 0.91, keyAction = "Offer monthly recurring invoice subscription for office breakfasts.", colorHex = "#10B981"),
            CustomerSegmentEntity(businessId = businessId, segmentName = "Weekend Walk-Ins", customerCount = 520, percentageOfBase = 40.0, avgLifetimeValue = 65.0, retentionRate = 0.40, keyAction = "Promote take-home bake-at-home weekend sourdough kits.", colorHex = "#F59E0B")
        )
        appDao.insertAllSegments(segments)

        val channels = listOf(
            MarketingChannelEntity(businessId = businessId, channelName = "Local Instagram & Reels", monthlySpend = 1100.0, revenueAttributed = 6600.0, leadsGenerated = 880, customersAcquired = 240, roas = 6.0),
            MarketingChannelEntity(businessId = businessId, channelName = "Google Maps Local SEO", monthlySpend = 400.0, revenueAttributed = 4800.0, leadsGenerated = 1200, customersAcquired = 310, roas = 12.0),
            MarketingChannelEntity(businessId = businessId, channelName = "Local Flyer & Print", monthlySpend = 900.0, revenueAttributed = 1350.0, leadsGenerated = 180, customersAcquired = 40, roas = 1.5)
        )
        appDao.insertAllChannels(channels)

        val recs = listOf(
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Scale B2B Corporate Catering Subscription",
                category = "Revenue Optimization",
                priority = "High",
                summary = "Catering generates $140 average order with 65.7% margin and high client stickiness.",
                expectedImpact = "+$4,500 Monthly Predictable Cash Flow",
                actionSteps = "1. Outreach to 30 local tech & law firms within a 3-mile radius.\n2. Bundle coffee dispenser + 12 pastry box.",
                status = "Pending",
                isAiGenerated = true
            ),
            BusinessRecommendationEntity(
                businessId = businessId,
                title = "Eliminate Paper Flyer Distribution",
                category = "Marketing ROI",
                priority = "Medium",
                summary = "Print flyers generated only 1.5x ROAS compared to 12.0x on Google Maps Local Search.",
                expectedImpact = "Save $900/mo and reinvest in Local Reels",
                actionSteps = "Cancel print contract and reallocate 50% to Instagram micro-influencers.",
                status = "Pending",
                isAiGenerated = true
            )
        )
        appDao.insertAllRecommendations(recs)

        appDao.insertChatMessage(
            ChatMessageEntity(
                businessId = businessId,
                isUser = false,
                text = "Welcome Baker's Hearth! Your bakery has exceptional 70%+ gross margins on sourdough and pastries. My recommendation is to rapidly expand your B2B corporate catering packages for stable recurring cash flow. How can I advise you today?"
            )
        )

        return profile.copy(id = profileId)
    }

    suspend fun generateAiRecommendationsWithGemini(businessId: Long): List<BusinessRecommendationEntity> = withContext(Dispatchers.IO) {
        val profile = appDao.getBusinessProfile(businessId)
        val metrics = appDao.getMonthlyMetricsList(businessId)
        val products = appDao.getProductsList(businessId)
        val channels = appDao.getChannelsList(businessId)
        val segments = appDao.getSegmentsList(businessId)

        val companyName = profile?.companyName ?: "Business"
        val industry = profile?.industry ?: "General"
        val goal = profile?.primaryGoal ?: "Increase Profit"
        val latestMetric = metrics.lastOrNull()
        val latestRevenue = latestMetric?.revenue ?: 50000.0
        val latestProfit = latestRevenue - ((latestMetric?.cogs ?: 20000.0) + (latestMetric?.operatingExpenses ?: 15000.0) + (latestMetric?.marketingSpend ?: 5000.0))
        val margin = if (latestRevenue > 0) (latestProfit / latestRevenue) * 100 else 15.0

        val prompt = buildString {
            appendLine("You are an executive SMB Business Consultant and CFO. Analyze the following business performance data and generate exactly 3 prioritized, highly actionable recommendations for $companyName ($industry).")
            appendLine("Primary Goal: $goal")
            appendLine("Monthly Revenue: ${latestRevenue.formatCurrency()}, Net Profit: ${latestProfit.formatCurrency()} (${margin.format(1)}% margin)")
            appendLine("Products: ${products.take(4).joinToString { "${it.productName} (${it.profitMarginPercent.format(1)}% margin, ${it.stockTurnoverDays} turnover days)" }}")
            appendLine("Marketing Channels: ${channels.joinToString { "${it.channelName} (${it.roas.format(1)}x ROAS, ${it.monthlySpend.formatCurrency()} spend)" }}")
            appendLine("Customer Segments: ${segments.joinToString { "${it.segmentName} (${it.customerCount} customers, retention ${(it.retentionRate * 100).format(0)}%)" }}")
            appendLine()
            appendLine("Return your response formatted with each recommendation separated by '===REC===' containing:")
            appendLine("TITLE: <concise title>")
            appendLine("CATEGORY: <Revenue Optimization | Cost Reduction | Marketing ROI | Risk & Cash Flow | Inventory>")
            appendLine("PRIORITY: <High | Medium | Low>")
            appendLine("IMPACT: <estimated dollar or % impact>")
            appendLine("SUMMARY: <1-2 sentences rationale>")
            appendLine("STEPS: <numbered action steps>")
        }

        val generatedList = mutableListOf<BusinessRecommendationEntity>()
        val apiKey = GeminiClient.getApiKey()

        if (GeminiClient.isApiKeyConfigured()) {
            try {
                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    generationConfig = GeminiGenerationConfig(temperature = 0.4f, maxOutputTokens = 1500),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are a professional management consultant and financial analyst. Provide sharp, realistic, high-ROI business recommendations.")))
                )
                val response = GeminiClient.apiService.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

                if (text.isNotBlank()) {
                    val rawRecs = text.split("===REC===").filter { it.isNotBlank() }
                    for (raw in rawRecs) {
                        val lines = raw.lines().map { it.trim() }
                        val title = lines.find { it.startsWith("TITLE:", ignoreCase = true) }?.substringAfter(":")?.trim() ?: "Strategic Optimization Initiative"
                        val category = lines.find { it.startsWith("CATEGORY:", ignoreCase = true) }?.substringAfter(":")?.trim() ?: "Revenue Optimization"
                        val priority = lines.find { it.startsWith("PRIORITY:", ignoreCase = true) }?.substringAfter(":")?.trim() ?: "High"
                        val impact = lines.find { it.startsWith("IMPACT:", ignoreCase = true) }?.substringAfter(":")?.trim() ?: "+12% Net Margin"
                        val summary = lines.find { it.startsWith("SUMMARY:", ignoreCase = true) }?.substringAfter(":")?.trim() ?: "Data-driven initiative based on current performance numbers."
                        val steps = lines.filter { it.startsWith("1.") || it.startsWith("2.") || it.startsWith("3.") || it.startsWith("-") }.joinToString("\n")

                        generatedList.add(
                            BusinessRecommendationEntity(
                                businessId = businessId,
                                title = title,
                                category = category,
                                priority = priority,
                                summary = summary,
                                expectedImpact = impact,
                                actionSteps = if (steps.isNotBlank()) steps else "1. Review unit economics.\n2. Execute operational adjustments.\n3. Monitor KPIs weekly.",
                                status = "Pending",
                                isAiGenerated = true
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // If API call encounters an error, proceed to algorithmic fallback below
            }
        }

        // If Gemini was not configured or didn't return parsed recommendations, synthesize high-accuracy rule-based recommendations
        if (generatedList.isEmpty()) {
            val lowestRoasChannel = channels.minByOrNull { it.roas }
            val highestRoasChannel = channels.maxByOrNull { it.roas }
            val lowestMarginProduct = products.minByOrNull { it.profitMarginPercent }

            if (lowestRoasChannel != null && highestRoasChannel != null && lowestRoasChannel != highestRoasChannel) {
                generatedList.add(
                    BusinessRecommendationEntity(
                        businessId = businessId,
                        title = "Shift Budget from ${lowestRoasChannel.channelName} to ${highestRoasChannel.channelName}",
                        category = "Marketing ROI",
                        priority = "High",
                        summary = "${lowestRoasChannel.channelName} generates only ${lowestRoasChannel.roas.format(1)}x ROAS vs ${highestRoasChannel.roas.format(1)}x on ${highestRoasChannel.channelName}.",
                        expectedImpact = "+$2,800 Monthly Revenue",
                        actionSteps = "1. Reallocate 25% of ${lowestRoasChannel.channelName} ad budget.\n2. Scale ad groups with proven conversion rates on ${highestRoasChannel.channelName}.\n3. Review blended CAC in 14 days.",
                        status = "Pending",
                        isAiGenerated = true
                    )
                )
            }

            if (lowestMarginProduct != null) {
                generatedList.add(
                    BusinessRecommendationEntity(
                        businessId = businessId,
                        title = "Renegotiate or Repackage ${lowestMarginProduct.productName}",
                        category = "Cost Reduction",
                        priority = "Medium",
                        summary = "${lowestMarginProduct.productName} currently produces a narrow ${lowestMarginProduct.profitMarginPercent.format(1)}% margin with ${lowestMarginProduct.stockTurnoverDays} turnover days.",
                        expectedImpact = "+4.2% Line Item Profit",
                        actionSteps = "1. Bundle with higher-margin accessories.\n2. Request 5% volume discount from primary vendor.\n3. Adjust retail pricing by 3-5%.",
                        status = "Pending",
                        isAiGenerated = true
                    )
                )
            }

            generatedList.add(
                BusinessRecommendationEntity(
                    businessId = businessId,
                    title = "Automate Targeted Nurture Sequence for Dormant Customers",
                    category = "Customer Retention",
                    priority = "High",
                    summary = "Reactivating just 10% of dormant accounts generates immediate high-margin revenue with $0 ad spend.",
                    expectedImpact = "Reduce overall churn by 1.2% / month",
                    actionSteps = "1. Export at-risk accounts inactive for >60 days.\n2. Send a 2-part personalized check-in with an exclusive incentive.\n3. Solicit feedback on product satisfaction.",
                    status = "Pending",
                    isAiGenerated = true
                )
            )
        }

        appDao.insertAllRecommendations(generatedList)
        generatedList
    }

    suspend fun askConsultantChat(businessId: Long, userQuestion: String): String = withContext(Dispatchers.IO) {
        val profile = appDao.getBusinessProfile(businessId)
        val metrics = appDao.getMonthlyMetricsList(businessId)
        val products = appDao.getProductsList(businessId)
        val channels = appDao.getChannelsList(businessId)
        val segments = appDao.getSegmentsList(businessId)

        // Save user message first
        appDao.insertChatMessage(
            ChatMessageEntity(
                businessId = businessId,
                isUser = true,
                text = userQuestion
            )
        )

        val companyName = profile?.companyName ?: "Business"
        val industry = profile?.industry ?: "General SMB"
        val latestMetric = metrics.lastOrNull()
        val latestRevenue = latestMetric?.revenue ?: 50000.0
        val totalCost = (latestMetric?.cogs ?: 20000.0) + (latestMetric?.operatingExpenses ?: 15000.0) + (latestMetric?.marketingSpend ?: 5000.0)
        val latestProfit = latestRevenue - totalCost
        val margin = if (latestRevenue > 0) (latestProfit / latestRevenue) * 100 else 15.0
        val healthScore = RiskScoringEngine.computeHealthScore(metrics, channels, products)

        val contextInfo = buildString {
            appendLine("Company: $companyName ($industry)")
            appendLine("Primary Goal: ${profile?.primaryGoal}")
            appendLine("Latest Month Revenue: ${latestRevenue.formatCurrency()}, Expenses: ${totalCost.formatCurrency()}, Net Profit: ${latestProfit.formatCurrency()} (${margin.format(1)}% margin)")
            appendLine("Cash Reserves: ${(latestMetric?.cashBuffer ?: 50000.0).formatCurrency()}, Churn Rate: ${((latestMetric?.churnRate ?: 0.04) * 100).format(1)}%")
            appendLine("Health Score: ${healthScore.overallScore}/100 (Grade ${healthScore.ratingGrade})")
            appendLine("Top Products: ${products.take(3).joinToString { "${it.productName} (Margin ${it.profitMarginPercent.format(1)}%)" }}")
            appendLine("Marketing Channels: ${channels.joinToString { "${it.channelName} (${it.roas.format(1)}x ROAS)" }}")
            appendLine("Customer Segments: ${segments.joinToString { "${it.segmentName} (${it.customerCount} users)" }}")
        }

        var aiAnswer = ""
        val apiKey = GeminiClient.getApiKey()

        if (GeminiClient.isApiKeyConfigured()) {
            try {
                val systemPrompt = "You are an elite, practical SMB Business Consultant and CFO. You give concise, highly specific, data-backed advice using the user's provided numbers. Use short paragraphs and bullet points where helpful. Avoid generic boilerplate."
                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "BUSINESS DATA CONTEXT:\n$contextInfo\n\nUSER QUESTION:\n$userQuestion")
                            )
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 1200),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
                )
                val response = GeminiClient.apiService.generateContent(apiKey, request)
                aiAnswer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            } catch (e: Exception) {
                aiAnswer = ""
            }
        }

        if (aiAnswer.isBlank()) {
            // Intelligent domain-grounded fallback response based on question keywords
            val q = userQuestion.lowercase()
            aiAnswer = when {
                q.contains("profit") || q.contains("margin") -> {
                    "Based on $companyName's data, your current net profit margin is **${margin.format(1)}%** on **${latestRevenue.formatCurrency()}** monthly revenue.\n\n" +
                    "To expand margins toward **${(margin + 5.0).format(1)}%**, execute on these two leverage points:\n" +
                    "• **Product Mix:** Focus promotions on your high-margin lines (${products.maxByOrNull { it.profitMarginPercent }?.productName ?: "Flagship products"} at ${products.maxByOrNull { it.profitMarginPercent }?.profitMarginPercent?.format(1) ?: "60"}% margin).\n" +
                    "• **Cost Rationalization:** Re-evaluate unit costs on lower-margin inventory items."
                }
                q.contains("marketing") || q.contains("ad") || q.contains("roas") || q.contains("cac") -> {
                    val best = channels.maxByOrNull { it.roas }
                    val worst = channels.minByOrNull { it.roas }
                    "Reviewing your acquisition channels:\n\n" +
                    "• **Top Performer:** **${best?.channelName ?: "Search"}** is delivering **${best?.roas?.format(1) ?: "4.0"}x ROAS**.\n" +
                    "• **Lagging Channel:** **${worst?.channelName ?: "Social Ads"}** is lagging at **${worst?.roas?.format(1) ?: "2.0"}x ROAS**.\n\n" +
                    "**Consultant Recommendation:** Reallocate 20-30% of spend from ${worst?.channelName ?: "the weakest channel"} to ${best?.channelName ?: "your top channel"} to immediately improve blended CAC."
                }
                q.contains("churn") || q.contains("customer") || q.contains("retention") -> {
                    "Your monthly customer churn rate is currently **${((latestMetric?.churnRate ?: 0.04) * 100).format(1)}%**.\n\n" +
                    "With **${segments.find { it.segmentName.contains("At Risk", true) || it.segmentName.contains("Dormant", true) }?.customerCount ?: 150} customers** flagged as at-risk or dormant:\n" +
                    "1. Trigger an automated win-back workflow offering a custom incentive.\n" +
                    "2. Schedule 15-minute feedback calls with cancelling clients to identify feature gaps.\n" +
                    "3. Bolster post-purchase onboarding in the first 14 days."
                }
                q.contains("risk") || q.contains("runway") || q.contains("cash") -> {
                    val burn = totalCost
                    val runway = if (burn > 0) (latestMetric?.cashBuffer ?: 50000.0) / burn else 12.0
                    "Here is your financial risk assessment:\n\n" +
                    "• **Cash Reserves:** ${(latestMetric?.cashBuffer ?: 50000.0).formatCurrency()}\n" +
                    "• **Operating Runway:** **${runway.format(1)} months** at current monthly expenditure of ${totalCost.formatCurrency()}.\n" +
                    "• **Business Health Score:** **${healthScore.overallScore}/100** (Grade ${healthScore.ratingGrade}).\n\n" +
                    "Maintain at least 4.5 months of cash buffer to guard against seasonal demand fluctuations."
                }
                else -> {
                    "Looking at $companyName's trajectory:\n\n" +
                    "• **Current Revenue:** ${latestRevenue.formatCurrency()} with a healthy **${margin.format(1)}% net profit margin**.\n" +
                    "• **Growth Projection:** Revenue is projected to grow by +${PredictiveEngine.calculateLinearRegression(metrics).averageMonthlyGrowthPercent.format(1)}% monthly.\n" +
                    "• **Primary Focus:** Align your marketing spend with your ${profile?.primaryGoal ?: "strategic goals"} and protect your cash runway."
                }
            }
        }

        // Save AI response
        appDao.insertChatMessage(
            ChatMessageEntity(
                businessId = businessId,
                isUser = false,
                text = aiAnswer
            )
        )

        aiAnswer
    }
}
