import React, { useState, useEffect } from 'react';
import { Building2, X, Check, DollarSign, Users, Target, Globe, Save } from 'lucide-react';

export default function BusinessProfileModal({ isOpen, onClose, profile, onSave }) {
  const [formData, setFormData] = useState({ ...profile });

  useEffect(() => {
    if (profile) {
      setFormData({ ...profile });
    }
  }, [profile, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
      <div className="relative w-full max-w-lg glass-card rounded-2xl border border-slate-800 p-6 shadow-2xl">
        <div className="flex items-center justify-between pb-4 border-b border-slate-800">
          <div className="flex items-center gap-2.5">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
              <Building2 className="h-5 w-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Business Profile Setup</h3>
              <p className="text-xs text-slate-400">Configure parameters used by AI analytics & recommendations</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="rounded-lg p-1.5 text-slate-400 hover:bg-slate-800 hover:text-white transition-colors"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4 max-h-[70vh] overflow-y-auto pr-1">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Company / Brand Name</label>
            <input
              type="text"
              value={formData.companyName || ''}
              onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
              required
              className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3.5 py-2.5 text-sm text-white focus:border-cyan-500 focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Industry</label>
              <select
                value={formData.industry || 'E-Commerce & Retail'}
                onChange={(e) => setFormData({ ...formData, industry: e.target.value })}
                className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3 py-2.5 text-xs text-white focus:border-cyan-500 focus:outline-none"
              >
                <option value="E-Commerce & Retail">E-Commerce & Retail</option>
                <option value="SaaS & Tech">SaaS & B2B Tech</option>
                <option value="Food & Hospitality">Food & Hospitality</option>
                <option value="Professional Services">Professional Services</option>
                <option value="Healthcare & Wellness">Healthcare & Wellness</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Business Stage</label>
              <select
                value={formData.stage || 'Growth'}
                onChange={(e) => setFormData({ ...formData, stage: e.target.value })}
                className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3 py-2.5 text-xs text-white focus:border-cyan-500 focus:outline-none"
              >
                <option value="Early-Stage">Early-Stage / Seed</option>
                <option value="Growth">Growth / Scaling</option>
                <option value="Mature">Mature / Established</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Monthly Operating Budget ($)</label>
              <input
                type="number"
                value={formData.monthlyBudget || ''}
                onChange={(e) => setFormData({ ...formData, monthlyBudget: Number(e.target.value) })}
                className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3.5 py-2.5 text-sm text-white focus:border-cyan-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Team Size</label>
              <input
                type="number"
                value={formData.teamSize || ''}
                onChange={(e) => setFormData({ ...formData, teamSize: Number(e.target.value) })}
                className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3.5 py-2.5 text-sm text-white focus:border-cyan-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Primary Strategic Goal</label>
            <select
              value={formData.primaryGoal || 'Increase Profit Margin'}
              onChange={(e) => setFormData({ ...formData, primaryGoal: e.target.value })}
              className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3.5 py-2.5 text-xs text-white focus:border-cyan-500 focus:outline-none"
            >
              <option value="Increase Profit Margin">Increase Profit Margin</option>
              <option value="Accelerate Top-Line Revenue">Accelerate Top-Line Revenue</option>
              <option value="Reduce Customer Churn">Reduce Customer Churn</option>
              <option value="Optimize Marketing ROAS">Optimize Marketing ROAS</option>
              <option value="Extend Cash Runway">Extend Cash Runway</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Target Customer Profile</label>
            <textarea
              rows={2}
              value={formData.targetAudience || ''}
              onChange={(e) => setFormData({ ...formData, targetAudience: e.target.value })}
              placeholder="e.g. Enthusiast outdoors people aged 25-45 looking for premium ultralight equipment"
              className="w-full rounded-xl bg-slate-900 border border-slate-700 px-3.5 py-2 text-xs text-white focus:border-cyan-500 focus:outline-none"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl px-4 py-2 text-xs font-medium text-slate-400 hover:text-white"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 px-5 py-2 text-xs font-bold text-slate-950 shadow-md shadow-cyan-500/20 hover:from-cyan-400 hover:to-blue-500 transition-all"
            >
              <Save className="h-4 w-4" />
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
