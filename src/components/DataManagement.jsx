import React, { useState } from 'react';
import { 
  Database, 
  Plus, 
  Upload, 
  FileSpreadsheet, 
  Check, 
  Building2, 
  Layers,
  Trash2,
  Download
} from 'lucide-react';
import { formatCurrency } from '../data/mockData';

export default function DataManagement({ 
  metrics, 
  onAddMetric, 
  onSwitchDataset, 
  currentDatasetKey 
}) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [showCsvModal, setShowCsvModal] = useState(false);

  // New Record Form
  const [monthName, setMonthName] = useState('Sep');
  const [revenue, setRevenue] = useState('88500');
  const [cogs, setCogs] = useState('41200');
  const [operatingExpenses, setOperatingExpenses] = useState('18200');
  const [marketingSpend, setMarketingSpend] = useState('9600');
  const [newCustomers, setNewCustomers] = useState('590');
  const [churnPercent, setChurnPercent] = useState('3.3');

  // CSV Text
  const [csvText, setCsvText] = useState(
    "Sep, 88500, 41200, 18200, 9600, 590, 0.033\nOct, 92400, 42900, 18500, 9900, 620, 0.031"
  );

  const handleAddSubmit = (e) => {
    e.preventDefault();
    onAddMetric({
      monthName,
      revenue: Number(revenue) || 0,
      cogs: Number(cogs) || 0,
      operatingExpenses: Number(operatingExpenses) || 0,
      marketingSpend: Number(marketingSpend) || 0,
      newCustomers: Number(newCustomers) || 0,
      churnRate: (Number(churnPercent) || 3) / 100,
      cashBuffer: 120000
    });
    setShowAddModal(false);
  };

  const handleCsvImport = () => {
    const lines = csvText.trim().split('\n');
    lines.forEach((line) => {
      const parts = line.split(',').map(s => s.trim());
      if (parts.length >= 5) {
        onAddMetric({
          monthName: parts[0],
          revenue: Number(parts[1]) || 50000,
          cogs: Number(parts[2]) || 20000,
          operatingExpenses: Number(parts[3]) || 15000,
          marketingSpend: Number(parts[4]) || 5000,
          newCustomers: Number(parts[5]) || 300,
          churnRate: Number(parts[6]) || 0.04,
          cashBuffer: 100000
        });
      }
    });
    setShowCsvModal(false);
  };

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-white flex items-center gap-2">
            <span>Business Data Management</span>
            <span className="rounded-full bg-cyan-950 px-2.5 py-0.5 text-xs font-semibold text-cyan-400 border border-cyan-800">
              {metrics.length} Months Logged
            </span>
          </h2>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Input monthly financials, paste CSV bulk datasets, or load industry benchmarks
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowAddModal(true)}
            className="flex items-center gap-1.5 rounded-xl bg-cyan-500 px-3.5 py-2 text-xs font-bold text-slate-950 hover:bg-cyan-400 transition-colors shadow-sm"
          >
            <Plus className="h-4 w-4" />
            <span>Add Monthly Record</span>
          </button>
          <button
            onClick={() => setShowCsvModal(true)}
            className="flex items-center gap-1.5 rounded-xl border border-slate-700 bg-slate-900 px-3.5 py-2 text-xs font-semibold text-slate-200 hover:bg-slate-800 transition-colors"
          >
            <Upload className="h-4 w-4 text-cyan-400" />
            <span>Paste CSV</span>
          </button>
        </div>
      </div>

      {/* Dataset Benchmark Switcher */}
      <div className="glass-card rounded-2xl p-5 border border-slate-800">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <Building2 className="h-4 w-4 text-cyan-400" />
            <h3 className="text-sm font-bold text-white">Switch Industry Benchmark Dataset</h3>
          </div>
          <span className="text-xs text-slate-400">Current: <strong className="text-cyan-400">{currentDatasetKey}</strong></span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          {[
            { key: 'Retail', name: 'ApexGear Outdoors', type: 'E-Commerce / Retail', desc: 'High inventory volume, seasonality, multi-channel ad spend' },
            { key: 'SaaS', name: 'CloudPulse Analytics', type: 'B2B SaaS / Tech', desc: 'Subscription ARR, churn reduction, customer success metrics' },
            { key: 'Bakery', name: "Baker's Hearth", type: 'Food & Hospitality', desc: 'High gross margin sourdough, corporate catering subscriptions' }
          ].map((d) => (
            <button
              key={d.key}
              onClick={() => onSwitchDataset(d.key)}
              className={`p-3.5 rounded-xl text-left border transition-all ${
                currentDatasetKey === d.key
                  ? 'border-cyan-400 bg-cyan-950/30 shadow-md'
                  : 'border-slate-800 bg-slate-900/60 hover:border-slate-700'
              }`}
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-white">{d.name}</span>
                {currentDatasetKey === d.key && <Check className="h-3.5 w-3.5 text-cyan-400" />}
              </div>
              <div className="text-[11px] text-cyan-400 font-medium mt-0.5">{d.type}</div>
              <p className="text-[10px] text-slate-400 mt-1 line-clamp-2">{d.desc}</p>
            </button>
          ))}
        </div>
      </div>

      {/* Historical Data Table */}
      <div className="glass-card rounded-2xl border border-slate-800 overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between">
          <h3 className="text-sm font-bold text-white">Financial History Logs</h3>
          <span className="text-xs text-slate-400 font-mono">Sorted chronologically</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-semibold text-[10px] tracking-wider">
              <tr>
                <th className="py-3 px-4">Period</th>
                <th className="py-3 px-4">Revenue</th>
                <th className="py-3 px-4">COGS</th>
                <th className="py-3 px-4">OPEX</th>
                <th className="py-3 px-4">Marketing</th>
                <th className="py-3 px-4">Net Profit</th>
                <th className="py-3 px-4">New Cust</th>
                <th className="py-3 px-4">Churn</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/80">
              {metrics.map((m) => {
                const totalCost = (m.cogs || 0) + (m.operatingExpenses || 0) + (m.marketingSpend || 0);
                const profit = m.revenue - totalCost;
                return (
                  <tr key={m.id} className="hover:bg-slate-900/40 transition-colors">
                    <td className="py-3 px-4 font-bold text-white">{m.monthName}</td>
                    <td className="py-3 px-4 font-semibold text-cyan-300">{formatCurrency(m.revenue)}</td>
                    <td className="py-3 px-4 text-slate-400">{formatCurrency(m.cogs)}</td>
                    <td className="py-3 px-4 text-slate-400">{formatCurrency(m.operatingExpenses)}</td>
                    <td className="py-3 px-4 text-slate-400">{formatCurrency(m.marketingSpend)}</td>
                    <td className={`py-3 px-4 font-bold ${profit >= 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
                      {formatCurrency(profit)}
                    </td>
                    <td className="py-3 px-4 text-slate-200">{m.newCustomers}</td>
                    <td className="py-3 px-4 text-slate-300">{((m.churnRate || 0.04) * 100).toFixed(1)}%</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="glass-card rounded-2xl p-6 border border-slate-700 w-full max-w-md shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">Add Financial Period</h3>
            <p className="text-xs text-slate-400 mb-4">Input actual financial figures for the month</p>

            <form onSubmit={handleAddSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Month Name</label>
                <input
                  type="text"
                  value={monthName}
                  onChange={(e) => setMonthName(e.target.value)}
                  className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Revenue ($)</label>
                  <input
                    type="number"
                    value={revenue}
                    onChange={(e) => setRevenue(e.target.value)}
                    className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">COGS ($)</label>
                  <input
                    type="number"
                    value={cogs}
                    onChange={(e) => setCogs(e.target.value)}
                    className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">OPEX ($)</label>
                  <input
                    type="number"
                    value={operatingExpenses}
                    onChange={(e) => setOperatingExpenses(e.target.value)}
                    className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Marketing ($)</label>
                  <input
                    type="number"
                    value={marketingSpend}
                    onChange={(e) => setMarketingSpend(e.target.value)}
                    className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">New Customers</label>
                  <input
                    type="number"
                    value={newCustomers}
                    onChange={(e) => setNewCustomers(e.target.value)}
                    className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Churn Rate (%)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={churnPercent}
                    onChange={(e) => setChurnPercent(e.target.value)}
                    className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
                    required
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="rounded-lg px-3 py-1.5 text-xs font-medium text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-lg bg-cyan-500 px-4 py-1.5 text-xs font-bold text-slate-950 hover:bg-cyan-400"
                >
                  Save Record
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CSV Modal */}
      {showCsvModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="glass-card rounded-2xl p-6 border border-slate-700 w-full max-w-md shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">Paste CSV Data</h3>
            <p className="text-xs text-slate-400 mb-3">
              Format: Month, Revenue, COGS, OPEX, Marketing, NewCust, ChurnRate
            </p>

            <textarea
              rows="5"
              value={csvText}
              onChange={(e) => setCsvText(e.target.value)}
              className="w-full font-mono text-xs rounded-xl border border-slate-700 bg-slate-900 p-3 text-slate-200 focus:border-cyan-400 focus:outline-none"
            />

            <div className="flex justify-end gap-2 mt-4">
              <button
                type="button"
                onClick={() => setShowCsvModal(false)}
                className="rounded-lg px-3 py-1.5 text-xs font-medium text-slate-400 hover:text-white"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleCsvImport}
                className="rounded-lg bg-cyan-500 px-4 py-1.5 text-xs font-bold text-slate-950 hover:bg-cyan-400"
              >
                Import Rows
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
