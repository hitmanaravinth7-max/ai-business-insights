import React, { useState } from 'react';
import { 
  TrendingUp, 
  Sliders, 
  Target, 
  CheckCircle2, 
  Sparkles, 
  ArrowRight,
  Calculator,
  RotateCcw
} from 'lucide-react';
import { 
  calculateLinearRegression, 
  generateForecast, 
  formatCurrency 
} from '../data/mockData';

export default function PredictiveInsights({ metrics }) {
  const regression = calculateLinearRegression(metrics);
  const forecast = generateForecast(metrics, 3);

  // Scenario Simulator State
  const [adSpendDelta, setAdSpendDelta] = useState(0); // %
  const [priceDelta, setPriceDelta] = useState(0); // %
  const [costReductionDelta, setCostReductionDelta] = useState(0); // %

  const latest = metrics[metrics.length - 1] || {};
  const baseRevenue = latest.revenue || 50000;
  const baseCost = (latest.cogs || 20000) + (latest.operatingExpenses || 15000) + (latest.marketingSpend || 5000);
  const baseProfit = baseRevenue - baseCost;

  // Elasticity calculations:
  // Ad spend elasticity ~ 0.45
  const adEffect = (adSpendDelta / 100) * 0.45;
  // Price elasticity ~ -0.65 volume, +1.0 price
  const priceVolumeEffect = (priceDelta / 100) * -0.65;
  const priceDirectEffect = priceDelta / 100;

  const revMultiplier = 1.0 + adEffect + priceVolumeEffect + priceDirectEffect;
  const simRevenue = Math.max(0, baseRevenue * revMultiplier);

  // Expenses with cost cut and marketing change
  const marketingDeltaSpend = (latest.marketingSpend || 5000) * (adSpendDelta / 100);
  const simCost = (baseCost - (baseCost * (costReductionDelta / 100))) + marketingDeltaSpend;
  const simProfit = simRevenue - simCost;
  const profitDiff = simProfit - baseProfit;

  const resetSimulation = () => {
    setAdSpendDelta(0);
    setPriceDelta(0);
    setCostReductionDelta(0);
  };

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div>
        <h2 className="text-xl sm:text-2xl font-bold text-white flex items-center gap-2">
          <span>Predictive Insights & Forecasting</span>
          <span className="rounded-full bg-cyan-950 px-2.5 py-0.5 text-xs font-semibold text-cyan-400 border border-cyan-800">
            Time-Series Engine
          </span>
        </h2>
        <p className="text-xs sm:text-sm text-slate-400 mt-1">
          Machine-learned linear regression, trend analysis, and sensitivity scenario modeling
        </p>
      </div>

      {/* Regression Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="glass-card rounded-2xl p-5 border border-slate-800">
          <div className="text-xs text-slate-400">Monthly Growth Velocity</div>
          <div className="mt-2 text-2xl font-bold text-emerald-400">
            +{regression.avgGrowthRate.toFixed(1)}% / mo
          </div>
          <p className="text-[11px] text-slate-400 mt-1">Average historical expansion</p>
        </div>

        <div className="glass-card rounded-2xl p-5 border border-slate-800">
          <div className="text-xs text-slate-400">Trendline Slope (m)</div>
          <div className="mt-2 text-2xl font-bold text-cyan-400">
            +{formatCurrency(regression.slope)} / mo
          </div>
          <p className="text-[11px] text-slate-400 mt-1">Predicted monthly increment</p>
        </div>

        <div className="glass-card rounded-2xl p-5 border border-slate-800">
          <div className="text-xs text-slate-400">Model Fit (R² Coefficient)</div>
          <div className="mt-2 text-2xl font-bold text-white">
            {(regression.rSquared * 100).toFixed(0)}%
          </div>
          <p className="text-[11px] text-slate-400 mt-1">Statistical confidence level</p>
        </div>
      </div>

      {/* Forecast Table & Visual */}
      <div className="glass-card rounded-2xl p-6 border border-slate-800">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 mb-4">
          <div>
            <h3 className="text-base font-bold text-white">Next 3-Month Revenue Forecast</h3>
            <p className="text-xs text-slate-400">Projected with 90% confidence boundaries</p>
          </div>
          <span className="rounded-lg bg-emerald-500/10 px-2.5 py-1 text-xs font-semibold text-emerald-400 border border-emerald-500/20">
            Positive Trajectory Expected
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {forecast.map((f, i) => (
            <div key={i} className="rounded-xl bg-slate-900/90 border border-slate-800 p-4">
              <div className="flex items-center justify-between text-xs text-slate-400">
                <span className="font-bold text-white">{f.monthName}</span>
                <span className="text-cyan-400 font-medium">Month +{i + 1}</span>
              </div>
              <div className="mt-2 text-2xl font-bold text-emerald-400">
                {formatCurrency(f.predicted)}
              </div>
              <div className="mt-2 pt-2 border-t border-slate-800 text-[11px] text-slate-400 flex justify-between">
                <span>Range Estimate:</span>
                <span className="text-slate-200 font-mono">{formatCurrency(f.lowerBound)} – {formatCurrency(f.upperBound)}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* What-If Scenario Simulator */}
      <div className="glass-card rounded-2xl p-6 border border-slate-800">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-lg bg-cyan-500/10 text-cyan-400">
              <Sliders className="h-5 w-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">What-If Sensitivity Simulator</h3>
              <p className="text-xs text-slate-400">Test how strategic levers affect monthly bottom-line revenue & profit</p>
            </div>
          </div>

          <button
            onClick={resetSimulation}
            className="flex items-center gap-1 text-xs text-slate-400 hover:text-cyan-400 transition-colors"
          >
            <RotateCcw className="h-3.5 w-3.5" />
            <span>Reset</span>
          </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 pt-2">
          
          {/* Controls */}
          <div className="lg:col-span-2 space-y-5">
            
            {/* Slider 1 */}
            <div>
              <div className="flex justify-between text-xs mb-1.5">
                <span className="font-semibold text-slate-300">Marketing & Ad Spend Adjustment</span>
                <span className="font-bold text-cyan-400">{adSpendDelta >= 0 ? `+${adSpendDelta}%` : `${adSpendDelta}%`}</span>
              </div>
              <input
                type="range"
                min="-30"
                max="50"
                step="5"
                value={adSpendDelta}
                onChange={(e) => setAdSpendDelta(Number(e.target.value))}
                className="w-full accent-cyan-400 h-2 bg-slate-900 rounded-lg cursor-pointer"
              />
              <div className="flex justify-between text-[10px] text-slate-500 mt-1">
                <span>-30% (Conserve)</span>
                <span>0% (Baseline)</span>
                <span>+50% (Scale)</span>
              </div>
            </div>

            {/* Slider 2 */}
            <div>
              <div className="flex justify-between text-xs mb-1.5">
                <span className="font-semibold text-slate-300">Average Product Price Shift</span>
                <span className="font-bold text-emerald-400">{priceDelta >= 0 ? `+${priceDelta}%` : `${priceDelta}%`}</span>
              </div>
              <input
                type="range"
                min="-10"
                max="25"
                step="1"
                value={priceDelta}
                onChange={(e) => setPriceDelta(Number(e.target.value))}
                className="w-full accent-emerald-400 h-2 bg-slate-900 rounded-lg cursor-pointer"
              />
              <div className="flex justify-between text-[10px] text-slate-500 mt-1">
                <span>-10% (Discount)</span>
                <span>0% (Current)</span>
                <span>+25% (Premium)</span>
              </div>
            </div>

            {/* Slider 3 */}
            <div>
              <div className="flex justify-between text-xs mb-1.5">
                <span className="font-semibold text-slate-300">Operational & Vendor Cost Cut</span>
                <span className="font-bold text-amber-400">{costReductionDelta}% cut</span>
              </div>
              <input
                type="range"
                min="0"
                max="20"
                step="1"
                value={costReductionDelta}
                onChange={(e) => setCostReductionDelta(Number(e.target.value))}
                className="w-full accent-amber-400 h-2 bg-slate-900 rounded-lg cursor-pointer"
              />
              <div className="flex justify-between text-[10px] text-slate-500 mt-1">
                <span>0% (Standard)</span>
                <span>10% (Negotiate)</span>
                <span>20% (Aggressive)</span>
              </div>
            </div>
          </div>

          {/* Results Pane */}
          <div className="rounded-xl bg-slate-900/90 border border-slate-700/80 p-5 flex flex-col justify-between">
            <div>
              <div className="text-xs uppercase tracking-wider text-slate-400 font-semibold mb-3">Simulated Monthly Impact</div>
              
              <div className="space-y-4">
                <div>
                  <div className="text-xs text-slate-400">Simulated Revenue</div>
                  <div className="text-xl font-bold text-white">{formatCurrency(simRevenue)}</div>
                  <div className={`text-[11px] font-medium ${simRevenue >= baseRevenue ? 'text-emerald-400' : 'text-rose-400'}`}>
                    {simRevenue >= baseRevenue ? `+${formatCurrency(simRevenue - baseRevenue)}` : `-${formatCurrency(baseRevenue - simRevenue)}`} vs current
                  </div>
                </div>

                <div>
                  <div className="text-xs text-slate-400">Simulated Net Profit</div>
                  <div className="text-xl font-bold text-emerald-400">{formatCurrency(simProfit)}</div>
                  <div className={`text-xs font-bold ${profitDiff >= 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
                    {profitDiff >= 0 ? `+${formatCurrency(profitDiff)} gain` : `-${formatCurrency(Math.abs(profitDiff))} drop`}
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-800 text-[11px] text-slate-400">
              Assumes realistic elasticity coefficients based on SMB industry benchmarks.
            </div>
          </div>

        </div>
      </div>

    </div>
  );
}
