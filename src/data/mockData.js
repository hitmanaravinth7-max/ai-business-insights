// Initial Datasets for BizConsult AI

export const INITIAL_DATASETS = {
  Retail: {
    profile: {
      companyName: "ApexGear Outdoors",
      industry: "E-Commerce & Retail",
      monthlyBudget: 18500,
      teamSize: 12,
      primaryGoal: "Increase Profit Margin",
      targetAudience: "Hikers, backpackers, and outdoor enthusiasts aged 24-48",
      currencySymbol: "$",
      fiscalYear: "2026",
      stage: "Growth"
    },
    metrics: [
      { id: 1, monthIndex: 1, monthName: "Jan", revenue: 48500, cogs: 24200, operatingExpenses: 14200, marketingSpend: 6500, newCustomers: 280, churnRate: 0.048, avgOrderValue: 88, cashBuffer: 65000 },
      { id: 2, monthIndex: 2, monthName: "Feb", revenue: 51200, cogs: 25100, operatingExpenses: 14500, marketingSpend: 6800, newCustomers: 310, churnRate: 0.046, avgOrderValue: 91, cashBuffer: 66500 },
      { id: 3, monthIndex: 3, monthName: "Mar", revenue: 55800, cogs: 26900, operatingExpenses: 14800, marketingSpend: 7200, newCustomers: 345, churnRate: 0.042, avgOrderValue: 94, cashBuffer: 72000 },
      { id: 4, monthIndex: 4, monthName: "Apr", revenue: 61400, cogs: 29500, operatingExpenses: 15200, marketingSpend: 7900, newCustomers: 390, churnRate: 0.039, avgOrderValue: 96, cashBuffer: 78000 },
      { id: 5, monthIndex: 5, monthName: "May", revenue: 68900, cogs: 32800, operatingExpenses: 15900, marketingSpend: 8400, newCustomers: 440, churnRate: 0.038, avgOrderValue: 98, cashBuffer: 86000 },
      { id: 6, monthIndex: 6, monthName: "Jun", revenue: 74200, cogs: 35100, operatingExpenses: 16400, marketingSpend: 8900, newCustomers: 485, churnRate: 0.037, avgOrderValue: 102, cashBuffer: 94500 },
      { id: 7, monthIndex: 7, monthName: "Jul", revenue: 79800, cogs: 37600, operatingExpenses: 17100, marketingSpend: 9400, newCustomers: 520, churnRate: 0.035, avgOrderValue: 105, cashBuffer: 103000 },
      { id: 8, monthIndex: 8, monthName: "Aug", revenue: 84500, cogs: 39800, operatingExpenses: 17500, marketingSpend: 9800, newCustomers: 560, churnRate: 0.034, avgOrderValue: 108, cashBuffer: 114000 }
    ],
    products: [
      { id: 1, name: "Ultralight Alpine Tent", category: "Shelter", unitsSold: 220, unitPrice: 289, unitCost: 138, revenue: 63580, margin: 52.2, stock: 84, turnoverDays: 32 },
      { id: 2, name: "Carbon Trekking Poles", category: "Gear", unitsSold: 480, unitPrice: 79, unitCost: 28, revenue: 37920, margin: 64.5, stock: 190, turnoverDays: 26 },
      { id: 3, name: "Hydro-Shield Rain Jacket", category: "Apparel", unitsSold: 310, unitPrice: 149, unitCost: 82, revenue: 46190, margin: 44.9, stock: 62, turnoverDays: 44 },
      { id: 4, name: "Merino Wool Thermal Base", category: "Apparel", unitsSold: 590, unitPrice: 64, unitCost: 22, revenue: 37760, margin: 65.6, stock: 310, turnoverDays: 21 },
      { id: 5, name: "Titanium Cookset & Burner", category: "Camp Kitchen", unitsSold: 290, unitPrice: 54, unitCost: 39, revenue: 15660, margin: 27.7, stock: 420, turnoverDays: 78 },
      { id: 6, name: "Solar Trail Powerbank 10k", category: "Electronics", unitsSold: 180, unitPrice: 49, unitCost: 41, revenue: 8820, margin: 16.3, stock: 260, turnoverDays: 94 }
    ],
    segments: [
      { id: 1, name: "VIP Mountain Trekkers", count: 420, share: 15, avgLtv: 680, retention: 88, color: "#06B6D4", action: "Invite to exclusive early gear drop and offer bespoke bundle accessories." },
      { id: 2, name: "Seasonal Explorers", count: 890, share: 32, avgLtv: 310, retention: 64, color: "#10B981", action: "Send seasonal spring/autumn trip prep checklists with cross-sell vouchers." },
      { id: 3, name: "First-Time Day Hikers", count: 1050, share: 38, avgLtv: 115, retention: 42, color: "#F59E0B", action: "Automate 3-part trail guide email sequence with 15% second-order discount." },
      { id: 4, name: "Dormant / At-Risk", count: 420, share: 15, avgLtv: 180, retention: 22, color: "#EF4444", action: "Reactivation win-back campaign with free gift on orders over $50." }
    ],
    channels: [
      { id: 1, name: "Google Search Ads", spend: 3400, revenue: 14960, roas: 4.4, leads: 890, conversions: 175 },
      { id: 2, name: "Meta & Instagram", spend: 3200, revenue: 9280, roas: 2.9, leads: 1450, conversions: 112 },
      { id: 3, name: "Klaviyo Email Automations", spend: 450, revenue: 8100, roas: 18.0, leads: 3200, conversions: 195 },
      { id: 4, name: "Outdoor Influencer Partners", spend: 1800, revenue: 4680, roas: 2.6, leads: 620, conversions: 54 },
      { id: 5, name: "Organic SEO & Blog Content", spend: 950, revenue: 6650, roas: 7.0, leads: 2100, conversions: 82 }
    ],
    recommendations: [
      {
        id: 1,
        title: "Reallocate $1,000 from Meta Ads to High-ROAS Email & Search",
        category: "Marketing ROI",
        priority: "High",
        summary: "Meta ad ROAS has dropped to 2.9x while Search delivers 4.4x and Email produces 18.0x return.",
        expectedImpact: "+$3,200 Monthly Net Revenue",
        actionSteps: "1. Cap low-intent Meta prospecting campaigns.\n2. Scale Google Shopping campaigns targeting 'ultralight tent' keywords.\n3. Increase post-purchase email flow frequency.",
        status: "In Progress"
      },
      {
        id: 2,
        title: "Liquidate Solar Trail Powerbank Overstock",
        category: "Inventory & Cash Flow",
        priority: "High",
        summary: "Solar Powerbank inventory has a 94-day turnover rate with only 16.3% profit margin.",
        expectedImpact: "Free up $10,600 trapped cash flow",
        actionSteps: "1. Bundle the powerbank with high-margin Alpine Tents at a 20% bundle discount.\n2. Stop replenishment orders until inventory falls below 45 units.",
        status: "Pending"
      },
      {
        id: 3,
        title: "Launch VIP Concierge Up-Sell for Top 15% Customers",
        category: "Revenue Optimization",
        priority: "Medium",
        summary: "Your 420 VIP Mountain Trekkers drive an LTV of $680 with 88% retention.",
        expectedImpact: "+$14,500 Quarterly Gross Profit",
        actionSteps: "1. Segment customers with >$500 lifetime spend.\n2. Send personalized SMS/email preview of limited-edition trekking gear.",
        status: "Pending"
      },
      {
        id: 4,
        title: "Negotiate Titanium Cookset Supplier Unit Cost",
        category: "Cost Reduction",
        priority: "Medium",
        summary: "Cookset unit cost ($39 on $54 retail) limits margin to 27.7% compared to category average of 54%.",
        expectedImpact: "+3.5% Overall Product Line Margin",
        actionSteps: "1. Request tiered volume pricing for next production run.\n2. Explore secondary alternative suppliers.",
        status: "Pending"
      }
    ]
  },

  SaaS: {
    profile: {
      companyName: "CloudPulse Analytics",
      industry: "SaaS & Tech",
      monthlyBudget: 24000,
      teamSize: 8,
      primaryGoal: "Reduce Customer Churn",
      targetAudience: "B2B SaaS product managers and growth engineers",
      currencySymbol: "$",
      fiscalYear: "2026",
      stage: "Growth"
    },
    metrics: [
      { id: 1, monthIndex: 1, monthName: "Jan", revenue: 32000, cogs: 7200, operatingExpenses: 18500, marketingSpend: 8500, newCustomers: 45, churnRate: 0.058, avgOrderValue: 240, cashBuffer: 92000 },
      { id: 2, monthIndex: 2, monthName: "Feb", revenue: 35400, cogs: 7600, operatingExpenses: 18800, marketingSpend: 8800, newCustomers: 52, churnRate: 0.054, avgOrderValue: 245, cashBuffer: 94000 },
      { id: 3, monthIndex: 3, monthName: "Mar", revenue: 39800, cogs: 8100, operatingExpenses: 19200, marketingSpend: 9200, newCustomers: 61, churnRate: 0.049, avgOrderValue: 250, cashBuffer: 98000 },
      { id: 4, monthIndex: 4, monthName: "Apr", revenue: 44200, cogs: 8700, operatingExpenses: 19600, marketingSpend: 9800, newCustomers: 68, churnRate: 0.045, avgOrderValue: 255, cashBuffer: 104000 },
      { id: 5, monthIndex: 5, monthName: "May", revenue: 49100, cogs: 9400, operatingExpenses: 20200, marketingSpend: 10400, newCustomers: 76, churnRate: 0.042, avgOrderValue: 260, cashBuffer: 112000 },
      { id: 6, monthIndex: 6, monthName: "Jun", revenue: 54800, cogs: 10100, operatingExpenses: 20800, marketingSpend: 11000, newCustomers: 84, churnRate: 0.039, avgOrderValue: 265, cashBuffer: 124000 }
    ],
    products: [
      { id: 1, name: "Growth Tier (Annual)", category: "Subscription", unitsSold: 115, unitPrice: 299, unitCost: 42, revenue: 34385, margin: 85.9, stock: 999, turnoverDays: 0 },
      { id: 2, name: "Starter Tier (Monthly)", category: "Subscription", unitsSold: 240, unitPrice: 79, unitCost: 18, revenue: 18960, margin: 77.2, stock: 999, turnoverDays: 0 },
      { id: 3, name: "Enterprise Custom SLA", category: "Add-On", unitsSold: 14, unitPrice: 850, unitCost: 120, revenue: 11900, margin: 85.8, stock: 999, turnoverDays: 0 }
    ],
    segments: [
      { id: 1, name: "Enterprise Annual", count: 120, share: 22, avgLtv: 3500, retention: 94, color: "#06B6D4", action: "Assign dedicated technical success manager and quarterly business reviews." },
      { id: 2, name: "Growth Scale-Ups", count: 280, share: 52, avgLtv: 950, retention: 78, color: "#10B981", action: "Introduce feature unlock notifications when approaching usage thresholds." },
      { id: 3, name: "At-Risk Monthly Starters", count: 140, share: 26, avgLtv: 230, retention: 44, color: "#EF4444", action: "Trigger automated in-app onboarding checklist for users with low 7-day logins." }
    ],
    channels: [
      { id: 1, name: "Developer Community & Podcasts", spend: 4200, revenue: 18900, roas: 4.5, leads: 940, conversions: 48 },
      { id: 2, name: "Google Search (High Intent)", spend: 3800, revenue: 14440, roas: 3.8, leads: 520, conversions: 32 },
      { id: 3, name: "LinkedIn Ads (B2B)", spend: 3000, revenue: 6300, roas: 2.1, leads: 310, conversions: 12 }
    ],
    recommendations: [
      {
        id: 1,
        title: "Incentivize Monthly-to-Annual Subscription Conversion",
        category: "Revenue & Churn",
        priority: "High",
        summary: "Annual subscribers have a 94% retention rate vs only 44% for monthly users.",
        expectedImpact: "+$28,000 Annualized ARR, Churn reduction from 3.9% to 2.4%",
        actionSteps: "Offer 2 months free + premium data retention for upgrading to annual before Day 30.",
        status: "In Progress"
      },
      {
        id: 2,
        title: "Trim Underperforming LinkedIn Ad Spend",
        category: "Marketing ROI",
        priority: "Medium",
        summary: "LinkedIn CAC is $250/customer with only 2.1x ROAS, vs $87 CAC on Developer Podcasts.",
        expectedImpact: "Save $1,500/mo or acquire 17 extra customers on top channel",
        actionSteps: "Shift 50% of LinkedIn ad budget to developer podcast sponsorship slots.",
        status: "Pending"
      }
    ]
  },

  Bakery: {
    profile: {
      companyName: "Baker's Hearth Artisan Bakery",
      industry: "Hospitality & Food",
      monthlyBudget: 12000,
      teamSize: 9,
      primaryGoal: "Optimize Ad Spend",
      targetAudience: "Neighborhood foodies, specialty coffee drinkers, catering clients",
      currencySymbol: "$",
      fiscalYear: "2026",
      stage: "Mature"
    },
    metrics: [
      { id: 1, monthIndex: 1, monthName: "Jan", revenue: 28000, cogs: 10500, operatingExpenses: 11200, marketingSpend: 1800, newCustomers: 320, churnRate: 0.065, avgOrderValue: 18.5, cashBuffer: 38000 },
      { id: 2, monthIndex: 2, monthName: "Feb", revenue: 31000, cogs: 11400, operatingExpenses: 11500, marketingSpend: 2100, newCustomers: 360, churnRate: 0.058, avgOrderValue: 19.2, cashBuffer: 41000 },
      { id: 3, monthIndex: 3, monthName: "Mar", revenue: 34500, cogs: 12400, operatingExpenses: 11800, marketingSpend: 2200, newCustomers: 410, churnRate: 0.052, avgOrderValue: 20.1, cashBuffer: 46000 },
      { id: 4, monthIndex: 4, monthName: "Apr", revenue: 38200, cogs: 13600, operatingExpenses: 12200, marketingSpend: 2400, newCustomers: 450, churnRate: 0.048, avgOrderValue: 21.0, cashBuffer: 52000 }
    ],
    products: [
      { id: 1, name: "Sourdough Country Loaf", category: "Bread", unitsSold: 1420, unitPrice: 9.5, unitCost: 2.4, revenue: 13490, margin: 74.7, stock: 120, turnoverDays: 1 },
      { id: 2, name: "Almond Croissant & Pastry", category: "Pastry", unitsSold: 1180, unitPrice: 5.5, unitCost: 1.6, revenue: 6490, margin: 70.9, stock: 80, turnoverDays: 1 },
      { id: 3, name: "Corporate Morning Catering Box", category: "Catering", unitsSold: 85, unitPrice: 140, unitCost: 48, revenue: 11900, margin: 65.7, stock: 25, turnoverDays: 3 }
    ],
    segments: [
      { id: 1, name: "Daily Morning Commuters", count: 650, share: 48, avgLtv: 180, retention: 82, color: "#06B6D4", action: "Launch digital stamp card on mobile wallet." },
      { id: 2, name: "Corporate Catering Accounts", count: 45, share: 12, avgLtv: 1600, retention: 91, color: "#10B981", action: "Offer monthly recurring invoice subscription for office breakfasts." },
      { id: 3, name: "Weekend Walk-Ins", count: 520, share: 40, avgLtv: 65, retention: 40, color: "#F59E0B", action: "Promote take-home bake-at-home weekend sourdough kits." }
    ],
    channels: [
      { id: 1, name: "Local Instagram & Reels", spend: 1100, revenue: 6600, roas: 6.0, leads: 880, conversions: 240 },
      { id: 2, name: "Google Maps Local SEO", spend: 400, revenue: 4800, roas: 12.0, leads: 1200, conversions: 310 },
      { id: 3, name: "Local Flyer & Print", spend: 900, revenue: 1350, roas: 1.5, leads: 180, conversions: 40 }
    ],
    recommendations: [
      {
        id: 1,
        title: "Scale B2B Corporate Catering Subscription",
        category: "Revenue Optimization",
        priority: "High",
        summary: "Catering generates $140 average order with 65.7% margin and high client stickiness.",
        expectedImpact: "+$4,500 Monthly Predictable Cash Flow",
        actionSteps: "1. Outreach to 30 local tech & law firms within a 3-mile radius.\n2. Bundle coffee dispenser + 12 pastry box.",
        status: "Pending"
      },
      {
        id: 2,
        title: "Eliminate Paper Flyer Distribution",
        category: "Marketing ROI",
        priority: "Medium",
        summary: "Print flyers generated only 1.5x ROAS compared to 12.0x on Google Maps Local Search.",
        expectedImpact: "Save $900/mo and reinvest in Local Reels",
        actionSteps: "Cancel print contract and reallocate 50% to Instagram micro-influencers.",
        status: "Pending"
      }
    ]
  }
};

// Statistical & Predictive Helpers
export function calculateLinearRegression(metrics) {
  if (!metrics || metrics.length < 2) {
    return { slope: 0, intercept: metrics?.[0]?.revenue || 0, rSquared: 1, avgGrowthRate: 0 };
  }

  const n = metrics.length;
  let sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

  metrics.forEach((m, i) => {
    const x = i + 1;
    const y = m.revenue;
    sumX += x;
    sumY += y;
    sumXY += x * y;
    sumX2 += x * x;
    sumY2 += y * y;
  });

  const denominator = n * sumX2 - sumX * sumX;
  const slope = denominator !== 0 ? (n * sumXY - sumX * sumY) / denominator : 0;
  const intercept = (sumY - slope * sumX) / n;

  // R-squared
  const meanY = sumY / n;
  let ssTot = 0, ssRes = 0;
  metrics.forEach((m, i) => {
    const x = i + 1;
    const yPred = slope * x + intercept;
    ssTot += Math.pow(m.revenue - meanY, 2);
    ssRes += Math.pow(m.revenue - yPred, 2);
  });
  const rSquared = ssTot !== 0 ? Math.max(0, Math.min(1, 1 - ssRes / ssTot)) : 1;

  const firstRev = metrics[0].revenue;
  const lastRev = metrics[metrics.length - 1].revenue;
  const avgGrowthRate = firstRev > 0 ? (((lastRev - firstRev) / firstRev) / (metrics.length - 1)) * 100 : 0;

  return { slope, intercept, rSquared, avgGrowthRate };
}

export function generateForecast(metrics, periodsAhead = 3) {
  if (!metrics || metrics.length === 0) return [];
  const { slope, intercept } = calculateLinearRegression(metrics);
  const monthLabels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
  const lastMetric = metrics[metrics.length - 1];
  const lastMonthIdx = lastMetric.monthIndex;

  const forecast = [];
  const stdDev = 2200;

  for (let i = 1; i <= periodsAhead; i++) {
    const step = metrics.length + i;
    const predicted = Math.max(0, slope * step + intercept);
    const margin = stdDev * 1.645 * Math.sqrt(1 + 1 / metrics.length + Math.pow(step - metrics.length / 2, 2) / 80);
    const mName = monthLabels[(lastMonthIdx + i - 1) % 12] + " (F)";

    forecast.push({
      monthName: mName,
      monthIndex: step,
      predicted: Math.round(predicted),
      lowerBound: Math.max(0, Math.round(predicted - margin)),
      upperBound: Math.round(predicted + margin)
    });
  }

  return forecast;
}

export function computeHealthScore(metrics, channels = [], products = []) {
  if (!metrics || metrics.length === 0) {
    return { overall: 75, grade: "B+", summary: "Stable baseline with room for profit optimization." };
  }

  const latest = metrics[metrics.length - 1];
  const totalCost = (latest.cogs || 0) + (latest.operatingExpenses || 0) + (latest.marketingSpend || 0);
  const netProfit = latest.revenue - totalCost;
  const margin = latest.revenue > 0 ? netProfit / latest.revenue : 0;

  // Margin score (0 - 25)
  let pScore = 10;
  if (margin >= 0.25) pScore = 25;
  else if (margin >= 0.15) pScore = 22;
  else if (margin >= 0.08) pScore = 18;
  else if (margin >= 0.02) pScore = 13;

  // Growth score (0 - 25)
  const reg = calculateLinearRegression(metrics);
  let gScore = 14;
  if (reg.avgGrowthRate >= 8) gScore = 25;
  else if (reg.avgGrowthRate >= 4) gScore = 22;
  else if (reg.avgGrowthRate >= 1.5) gScore = 18;
  else if (reg.avgGrowthRate < 0) gScore = 8;

  // Runway score (0 - 25)
  const runway = totalCost > 0 ? (latest.cashBuffer || 50000) / totalCost : 12;
  let rScore = 15;
  if (runway >= 12) rScore = 25;
  else if (runway >= 6) rScore = 22;
  else if (runway >= 3.5) rScore = 18;
  else if (runway < 2) rScore = 7;

  // Retention & efficiency score (0 - 25)
  let retScore = latest.churnRate <= 0.03 ? 24 : latest.churnRate <= 0.05 ? 18 : 10;

  const overall = Math.min(100, pScore + gScore + rScore + retScore);
  const grade = overall >= 90 ? "A+" : overall >= 82 ? "A" : overall >= 74 ? "B+" : overall >= 65 ? "B" : "C";
  const summary = overall >= 80
    ? "Strong financial fundamentals with scalable growth trajectory."
    : overall >= 65
    ? "Solid operating base; optimize marketing ROAS and cut low-margin overhead."
    : "Attention required on cash runway and high customer acquisition cost.";

  return { overall, grade, summary, pScore, gScore, rScore, retScore };
}

export function detectRisks(metrics, channels = [], products = []) {
  const risks = [];
  if (metrics && metrics.length > 0) {
    const latest = metrics[metrics.length - 1];
    if (latest.churnRate > 0.045) {
      risks.push({
        id: 'churn',
        title: "Elevated Customer Churn Rate",
        severity: latest.churnRate > 0.07 ? "CRITICAL" : "WARNING",
        description: `Monthly churn is currently ${(latest.churnRate * 100).toFixed(1)}%, above healthy benchmark (<3.5%).`,
        value: `${(latest.churnRate * 100).toFixed(1)}% / mo`,
        hint: "Deploy onboarding email automations and survey cancelling customers."
      });
    }

    const totalCost = (latest.cogs || 0) + (latest.operatingExpenses || 0) + (latest.marketingSpend || 0);
    const runway = totalCost > 0 ? (latest.cashBuffer || 50000) / totalCost : 12;
    if (runway < 4.0) {
      risks.push({
        id: 'runway',
        title: "Tight Liquidity Runway",
        severity: runway < 2.5 ? "CRITICAL" : "WARNING",
        description: `Cash reserves ($${(latest.cashBuffer || 0).toLocaleString()}) provide only ${runway.toFixed(1)} months of runway.`,
        value: `${runway.toFixed(1)} Mo Runway`,
        hint: "Conserve discretionary marketing spend and negotiate supplier terms."
      });
    }
  }

  // Underperforming channels
  channels.filter(ch => ch.roas < 2.5 && ch.spend > 1000).forEach(ch => {
    risks.push({
      id: `roas-${ch.id}`,
      title: `Underperforming Channel: ${ch.name}`,
      severity: "WARNING",
      description: `${ch.name} produced only ${ch.roas.toFixed(1)}x ROAS on spend of $${ch.spend.toLocaleString()}.`,
      value: `${ch.roas.toFixed(1)}x ROAS`,
      hint: "Pause or reallocate 30% of this budget to top-performing channels."
    });
  });

  // Low margin products
  products.filter(p => p.margin < 20).slice(0, 1).forEach(p => {
    risks.push({
      id: `margin-${p.id}`,
      title: `Low Margin on ${p.name}`,
      severity: "WARNING",
      description: `${p.name} yields a narrow ${p.margin.toFixed(1)}% gross margin with ${p.turnoverDays} turnover days.`,
      value: `${p.margin.toFixed(1)}% Margin`,
      hint: "Bundle with high-margin items or renegotiate manufacturing unit costs."
    });
  });

  if (risks.length === 0) {
    risks.push({
      id: 'healthy',
      title: "All Vital Signs Healthy",
      severity: "HEALTHY",
      description: "Margins, cash runway, and marketing return are within optimal target ranges.",
      value: "Optimal",
      hint: "Explore strategic growth reinvestment initiatives."
    });
  }

  return risks;
}

export function formatCurrency(num) {
  if (num === undefined || num === null) return "$0";
  if (Math.abs(num) >= 1000000) return `$${(num / 1000000).toFixed(2)}M`;
  if (Math.abs(num) >= 1000) return `$${num.toLocaleString(undefined, { maximumFractionDigits: 0 })}`;
  return `$${num.toFixed(2)}`;
}
