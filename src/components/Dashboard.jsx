import React, { useState } from 'react';
import { 
  TrendingUp, 
  DollarSign, 
  Users, 
  ShieldAlert, 
  ArrowUpRight, 
  ArrowDownRight, 
  PieChart, 
  BarChart3, 
  AlertTriangle, 
  ChevronRight,
  Sparkles,
  Layers,
  ShoppingBag,
  Clock
} from 'lucide-react';
import { 
  computeHealthScore, 
  detectRisks, 
  formatCurrency, 
  calculateLinearRegression 
} from '../data/mockData';

export default function Dashboard({ 
  profile, 
  metrics, 
  products, 
  segments, 
  channels, 
  onNavigate 
}) {
  const [hoveredMonth, setHoveredMonth] = useState(null);

  const healthScore = computeHealthScore(metrics, channels, products);
  const risks = detectRisks(metrics, channels, products);
  const regression = calculateLinearRegression(metrics);

  const latest = metrics[metrics.length - 1] || {};
  const revenue = latest.revenue || 0;
  const totalCost = (latest.cogs || 0) + (latest.operatingExpenses || 0) + (latest.marketingSpend || 0);
  const netProfit = revenue - totalCost;
  const marginPercent = revenue > 0 ? (netProfit / revenue) * 100 : 0;
  const churn = (latest.churnRate || 0.04) * 100;
  const runwayMonths = totalCost > 0 ? (latest.cashBuffer || 50000) / totalCost : 12;

  // Max value for revenue trend chart SVG
  const maxRevenue = Math.max(...metrics.map(m => m.revenue), 100000) * 1.15;
  const chartHeight = 160;
  const chartWidth = 560;

  // SVG Points
  const revenuePoints = metrics.map((m, idx) => {
    const x = metrics.length > 1 ? (idx / (metrics.length - 1)) * (chartWidth - 40) + 20 : chartWidth / 2;
    const y = chartHeight - (m.revenue / maxRevenue) * (chartHeight - 30) - 15;
    return { x, y, data: m };
  });

  const expensePoints = metrics.map((m, idx) => {
    const cost = (m.cogs || 0) + (m.operatingExpenses || 0) + (m.marketingSpend || 0);
    const x = metrics.length > 1 ? (idx / (metrics.length - 1)) * (chartWidth - 40) + 20 : chartWidth / 2;
    const y = chartHeight - (cost / maxRevenue) * (chartHeight - 30) - 15;
    return { x, y, cost };
  });

  const pathD = revenuePoints.reduce((acc, pt, i) => 
    i === 0 ? `M ${pt.x},${pt.y}` : `${acc} L ${pt.x},${pt.y}`, ''
  );

  const areaD = `${pathD} L ${revenuePoints[revenuePoints.length - 1]?.x || 0},${chartHeight} L ${revenuePoints[0]?.x || 0},${chartHeight} Z`;

  const expensePathD = expensePoints.reduce((acc, pt, i) => 
    i === 0 ? `M ${pt.x},${pt.y}` : `${acc} L ${pt.x},${pt.y}`, ''
  );

  // Customer segments total
  const totalCustomers = segments.reduce((sum, s) => sum + s.count, 0);

  return (
    <div className="space-y-6">
      
      {/* Top Banner / Health Score */}
      <div className="glass-card rounded-2xl p-5 sm:p-6 border border-slate-800 relative overflow-hidden">
        <div className="absolute right-0 top-0 w-80 h-full bg-gradient-to-l from-cyan-500/10 via-transparent to-transparent pointer-events-none" />
        
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 relative z-10">
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-xl sm:text-2xl font-bold text-white">{profile.companyName}</h2>
              <span className="rounded-full bg-cyan-950 px-2.5 py-0.5 text-xs font-semibold text-cyan-400 border border-cyan-800">
                {profile.industry}
              </span>
            </div>
            <p className="text-xs sm:text-sm text-slate-400 mt-1">
              Primary Goal: <strong className="text-slate-200">{profile.primaryGoal}</strong> • Team Size: {profile.teamSize} • Target: {profile.targetAudience}
            </p>
          </div>

          {/* Health Score Pill */}
          <div className="flex items-center gap-3 bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-2.5 shadow-sm">
            <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-gradient-to-br from-cyan-500 to-emerald-500 text-slate-950 font-black text-lg">
              {healthScore.grade}
            </div>
            <div>
              <div className="text-[11px] uppercase tracking-wider text-slate-400 font-semibold">Business Health Score</div>
              <div className="flex items-center gap-1.5">
                <span className="text-base font-bold text-white">{healthScore.overall}</span>
                <span className="text-xs text-slate-500">/ 100</span>
                <span className="text-xs text-emerald-400 font-medium ml-1">Grade {healthScore.grade}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 4 Executive KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        
        {/* KPI 1 */}
        <div className="glass-card glass-card-hover rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between text-slate-400 text-xs">
            <span>Monthly Revenue</span>
            <div className="p-2 rounded-lg bg-cyan-500/10 text-cyan-400">
              <TrendingUp className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl font-bold text-white tracking-tight">
            {formatCurrency(revenue)}
          </div>
          <div className="mt-1 flex items-center gap-1 text-xs text-emerald-400 font-medium">
            <ArrowUpRight className="h-3.5 w-3.5" />
            <span>+{regression.avgGrowthRate.toFixed(1)}% avg monthly</span>
          </div>
        </div>

        {/* KPI 2 */}
        <div className="glass-card glass-card-hover rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between text-slate-400 text-xs">
            <span>Net Operating Profit</span>
            <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-400">
              <DollarSign className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl font-bold text-white tracking-tight">
            {formatCurrency(netProfit)}
          </div>
          <div className="mt-1 text-xs text-cyan-400 font-semibold">
            {marginPercent.toFixed(1)}% net margin
          </div>
        </div>

        {/* KPI 3 */}
        <div className="glass-card glass-card-hover rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between text-slate-400 text-xs">
            <span>Customer Churn Rate</span>
            <div className="p-2 rounded-lg bg-amber-500/10 text-amber-400">
              <Users className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl font-bold text-white tracking-tight">
            {churn.toFixed(1)}%
          </div>
          <div className="mt-1 text-xs text-slate-400">
            {churn > 4.5 ? 'Above 3.5% healthy target' : 'Within benchmark range'}
          </div>
        </div>

        {/* KPI 4 */}
        <div className="glass-card glass-card-hover rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between text-slate-400 text-xs">
            <span>Operating Runway</span>
            <div className="p-2 rounded-lg bg-blue-500/10 text-blue-400">
              <ShieldAlert className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl font-bold text-white tracking-tight">
            {runwayMonths.toFixed(1)} Mo
          </div>
          <div className="mt-1 text-xs text-slate-300">
            {formatCurrency(latest.cashBuffer || 50000)} cash buffer
          </div>
        </div>
      </div>

      {/* Middle Grid: Revenue Trend & Customer Segmentation */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Left 2 Cols: Interactive Revenue & Expense Chart */}
        <div className="lg:col-span-2 glass-card rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-base font-bold text-white">Revenue & Expense Trajectory</h3>
              <p className="text-xs text-slate-400">Interactive historical performance & trend analysis</p>
            </div>
            <button
              onClick={() => onNavigate('predictive')}
              className="text-xs text-cyan-400 hover:text-cyan-300 font-semibold flex items-center gap-1"
            >
              <span>3-Mo Forecast</span>
              <ChevronRight className="h-3.5 w-3.5" />
            </button>
          </div>

          {/* Scrubbed inspect banner */}
          {hoveredMonth && (
            <div className="mb-3 rounded-lg bg-slate-900 border border-cyan-500/30 px-3 py-1.5 flex items-center justify-between text-xs">
              <span className="font-bold text-cyan-400">{hoveredMonth.monthName}: Revenue {formatCurrency(hoveredMonth.revenue)}</span>
              <span className="text-slate-300">Net Profit: <strong className="text-emerald-400">{formatCurrency(hoveredMonth.revenue - (hoveredMonth.cogs + hoveredMonth.operatingExpenses + hoveredMonth.marketingSpend))}</strong></span>
            </div>
          )}

          {/* SVG Canvas Chart */}
          <div className="w-full overflow-x-auto">
            <svg 
              viewBox={`0 0 ${chartWidth} ${chartHeight}`} 
              className="w-full h-44 select-none"
            >
              <defs>
                <linearGradient id="revenueGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#06B6D4" stopOpacity="0.4" />
                  <stop offset="100%" stopColor="#06B6D4" stopOpacity="0.0" />
                </linearGradient>
              </defs>

              {/* Grid Lines */}
              {[0.25, 0.5, 0.75, 1].map((lvl, i) => (
                <line
                  key={i}
                  x1="20"
                  y1={chartHeight * lvl - 10}
                  x2={chartWidth - 20}
                  y2={chartHeight * lvl - 10}
                  stroke="#1e293b"
                  strokeWidth="1"
                  strokeDasharray="4 4"
                />
              ))}

              {/* Revenue Area */}
              <path d={areaD} fill="url(#revenueGrad)" />

              {/* Expenses Line (Dashed Red) */}
              <path
                d={expensePathD}
                fill="none"
                stroke="#EF4444"
                strokeWidth="2"
                strokeDasharray="4 4"
                opacity="0.8"
              />

              {/* Revenue Stroke (Cyan) */}
              <path
                d={pathD}
                fill="none"
                stroke="#22D3EE"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              />

              {/* Data points */}
              {revenuePoints.map((pt, idx) => (
                <g 
                  key={idx} 
                  className="cursor-pointer"
                  onMouseEnter={() => setHoveredMonth(pt.data)}
                  onMouseLeave={() => setHoveredMonth(null)}
                >
                  <circle
                    cx={pt.x}
                    cy={pt.y}
                    r={hoveredMonth?.id === pt.data.id ? "6" : "4"}
                    fill="#0F172A"
                    stroke="#22D3EE"
                    strokeWidth="2.5"
                  />
                  <text
                    x={pt.x}
                    y={chartHeight - 2}
                    textAnchor="middle"
                    fill="#94a3b8"
                    fontSize="10"
                    fontWeight="500"
                  >
                    {pt.data.monthName}
                  </text>
                </g>
              ))}
            </svg>
          </div>

          {/* Chart Legend */}
          <div className="mt-3 flex items-center justify-center gap-6 text-xs text-slate-400">
            <div className="flex items-center gap-2">
              <span className="h-2.5 w-2.5 rounded-full bg-cyan-400" />
              <span>Gross Revenue</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="h-2.5 w-2.5 rounded-full bg-rose-500" />
              <span>Total Expenses</span>
            </div>
          </div>
        </div>

        {/* Right 1 Col: Customer Segmentation */}
        <div className="glass-card rounded-2xl p-5 border border-slate-800 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-base font-bold text-white">Customer Segments</h3>
              <PieChart className="h-4 w-4 text-cyan-400" />
            </div>
            <p className="text-xs text-slate-400 mb-4">Value-based tiering ({totalCustomers.toLocaleString()} total)</p>

            {/* Segments List */}
            <div className="space-y-3">
              {segments.map((seg) => (
                <div key={seg.id} className="rounded-xl bg-slate-900/60 p-2.5 border border-slate-800">
                  <div className="flex items-center justify-between text-xs">
                    <div className="flex items-center gap-2">
                      <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: seg.color }} />
                      <span className="font-semibold text-slate-200">{seg.name}</span>
                    </div>
                    <span className="font-bold text-white">{seg.share}%</span>
                  </div>
                  <div className="mt-1 flex items-center justify-between text-[11px] text-slate-400">
                    <span>{seg.count} users • Avg LTV ${seg.avgLtv}</span>
                    <span className="text-emerald-400 font-medium">{seg.retention}% ret</span>
                  </div>
                  <div className="mt-1 text-[10px] text-cyan-300/80 truncate">
                    Action: {seg.action}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-4 pt-3 border-t border-slate-800 text-[11px] text-slate-400 flex items-center justify-between">
            <span>RFM Model Class</span>
            <span className="text-cyan-400 font-medium">Auto-Optimized</span>
          </div>
        </div>
      </div>

      {/* Bottom Grid: Marketing ROAS, Products, Risks */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Marketing Channels ROAS */}
        <div className="glass-card rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-base font-bold text-white">Marketing ROI & ROAS</h3>
            <BarChart3 className="h-4 w-4 text-cyan-400" />
          </div>
          <p className="text-xs text-slate-400 mb-4">Spend efficiency across active acquisition channels</p>

          <div className="space-y-3.5">
            {channels.map((ch) => {
              const maxRoas = 18;
              const barWidth = Math.min(100, Math.max(12, (ch.roas / maxRoas) * 100));
              return (
                <div key={ch.id} className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-medium text-slate-200 truncate max-w-[140px]">{ch.name}</span>
                    <div className="flex items-center gap-2">
                      <span className="text-slate-400 text-[11px]">${ch.spend.toLocaleString()}</span>
                      <span className={`px-1.5 py-0.5 rounded text-[10px] font-bold ${
                        ch.roas >= 3.5 ? 'bg-emerald-500/20 text-emerald-400' :
                        ch.roas >= 2.0 ? 'bg-cyan-500/20 text-cyan-400' : 'bg-rose-500/20 text-rose-400'
                      }`}>
                        {ch.roas.toFixed(1)}x ROAS
                      </span>
                    </div>
                  </div>
                  <div className="w-full h-1.5 rounded-full bg-slate-900 overflow-hidden">
                    <div 
                      className={`h-full rounded-full ${
                        ch.roas >= 3.5 ? 'bg-emerald-500' : ch.roas >= 2.0 ? 'bg-cyan-400' : 'bg-rose-500'
                      }`}
                      style={{ width: `${barWidth}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Product Profitability Matrix */}
        <div className="glass-card rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-base font-bold text-white">Top Products</h3>
            <ShoppingBag className="h-4 w-4 text-cyan-400" />
          </div>
          <p className="text-xs text-slate-400 mb-4">Volume, gross margin & turnover rate</p>

          <div className="space-y-3">
            {products.slice(0, 4).map((p) => (
              <div key={p.id} className="flex items-center justify-between pb-2 border-b border-slate-800/80 last:border-none">
                <div className="max-w-[170px]">
                  <div className="text-xs font-semibold text-slate-200 truncate">{p.name}</div>
                  <div className="text-[10px] text-slate-400">
                    {p.unitsSold} sold • {p.turnoverDays}d turnover
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-xs font-bold text-white">{formatCurrency(p.revenue)}</div>
                  <div className={`text-[10px] font-semibold ${
                    p.margin >= 50 ? 'text-emerald-400' : p.margin >= 25 ? 'text-cyan-400' : 'text-rose-400'
                  }`}>
                    {p.margin.toFixed(1)}% margin
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Risk Indicators */}
        <div className="glass-card rounded-2xl p-5 border border-slate-800">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-base font-bold text-white">Vital Risk Alerts</h3>
            <AlertTriangle className="h-4 w-4 text-amber-400" />
          </div>
          <p className="text-xs text-slate-400 mb-4">Real-time flags requiring operational attention</p>

          <div className="space-y-2.5">
            {risks.map((risk) => (
              <div 
                key={risk.id}
                className="rounded-xl bg-slate-900/90 border border-slate-800 p-3 text-xs"
              >
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-slate-200">{risk.title}</span>
                  <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                    risk.severity === 'CRITICAL' ? 'bg-rose-500/20 text-rose-400' :
                    risk.severity === 'WARNING' ? 'bg-amber-500/20 text-amber-400' : 'bg-emerald-500/20 text-emerald-400'
                  }`}>
                    {risk.value}
                  </span>
                </div>
                <p className="mt-1 text-[11px] text-slate-400">{risk.description}</p>
                <div className="mt-2 flex items-center gap-1 text-[10px] text-cyan-400 font-medium">
                  <Sparkles className="h-3 w-3" />
                  <span>{risk.hint}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>

    </div>
  );
}
