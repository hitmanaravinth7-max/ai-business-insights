import React, { useState } from 'react';
import { 
  Lightbulb, 
  Sparkles, 
  CheckCircle, 
  Clock, 
  ArrowRight, 
  ChevronDown, 
  ChevronUp, 
  Filter,
  TrendingUp,
  Tag
} from 'lucide-react';

export default function Recommendations({ 
  recommendations, 
  onUpdateStatus, 
  onGenerateAiRecs, 
  isGenerating 
}) {
  const [priorityFilter, setPriorityFilter] = useState('All');
  const [expandedId, setExpandedId] = useState(null);

  const filtered = recommendations.filter(rec => {
    if (priorityFilter === 'All') return true;
    return rec.priority.toLowerCase() === priorityFilter.toLowerCase();
  });

  const toggleExpand = (id) => {
    setExpandedId(expandedId === id ? null : id);
  };

  const getPriorityBadge = (prio) => {
    switch (prio.toLowerCase()) {
      case 'high':
        return 'bg-rose-500/20 text-rose-400 border border-rose-500/30';
      case 'medium':
        return 'bg-amber-500/20 text-amber-400 border border-amber-500/30';
      default:
        return 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/30';
    }
  };

  const getStatusBadge = (status) => {
    switch (status.toLowerCase()) {
      case 'completed':
        return 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
      case 'in progress':
        return 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/30';
      default:
        return 'bg-slate-800 text-slate-400 border border-slate-700';
    }
  };

  return (
    <div className="space-y-6">
      
      {/* Header and Trigger */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-white flex items-center gap-2">
            <span>Executive Recommendation Engine</span>
            <span className="rounded-full bg-cyan-950 px-2.5 py-0.5 text-xs font-semibold text-cyan-400 border border-cyan-800">
              {filtered.length} Actions
            </span>
          </h2>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Data-driven strategic interventions ranked by profit impact and execution velocity
          </p>
        </div>

        <button
          onClick={onGenerateAiRecs}
          disabled={isGenerating}
          className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 px-4 py-2.5 text-xs font-bold text-slate-950 shadow-lg shadow-cyan-500/20 hover:from-cyan-400 hover:to-blue-500 transition-all disabled:opacity-50"
        >
          <Sparkles className="h-4 w-4" />
          <span>{isGenerating ? 'Analyzing Business Vitals...' : 'Generate AI Advice'}</span>
        </button>
      </div>

      {/* Filter Chips */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1">
        <span className="text-xs text-slate-400 flex items-center gap-1 mr-2">
          <Filter className="h-3.5 w-3.5" /> Filter:
        </span>
        {['All', 'High', 'Medium', 'Low'].map((prio) => (
          <button
            key={prio}
            onClick={() => setPriorityFilter(prio)}
            className={`px-3 py-1 text-xs font-semibold rounded-lg transition-all ${
              priorityFilter === prio
                ? 'bg-cyan-500 text-slate-950'
                : 'bg-slate-900 border border-slate-800 text-slate-400 hover:text-slate-200'
            }`}
          >
            {prio} {prio !== 'All' && 'Priority'}
          </button>
        ))}
      </div>

      {/* Cards List */}
      <div className="space-y-4">
        {filtered.map((rec) => {
          const isExpanded = expandedId === rec.id;
          return (
            <div
              key={rec.id}
              className="glass-card rounded-2xl p-5 border border-slate-800 transition-all hover:border-slate-700"
            >
              {/* Card Header Row */}
              <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
                <div className="flex items-center gap-2">
                  <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider ${getPriorityBadge(rec.priority)}`}>
                    {rec.priority} Priority
                  </span>
                  <span className="rounded bg-slate-900 px-2 py-0.5 text-[11px] font-medium text-slate-300 border border-slate-800">
                    {rec.category}
                  </span>
                </div>

                {/* Status Switcher Button */}
                <button
                  onClick={() => {
                    const next = rec.status === 'Pending' ? 'In Progress' : rec.status === 'In Progress' ? 'Completed' : 'Pending';
                    onUpdateStatus(rec.id, next);
                  }}
                  className={`px-2.5 py-1 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition-colors ${getStatusBadge(rec.status)}`}
                  title="Click to toggle status"
                >
                  <span className="h-2 w-2 rounded-full bg-current" />
                  <span>{rec.status}</span>
                </button>
              </div>

              {/* Title & Impact */}
              <div className="mt-3">
                <h3 className="text-base font-bold text-white leading-snug">{rec.title}</h3>
                <div className="mt-1 flex items-center gap-1.5 text-xs text-emerald-400 font-semibold">
                  <TrendingUp className="h-3.5 w-3.5" />
                  <span>Expected Impact: {rec.expectedImpact}</span>
                </div>
              </div>

              {/* Summary */}
              <p className="mt-2 text-xs sm:text-sm text-slate-300 leading-relaxed">
                {rec.summary}
              </p>

              {/* Action Steps Accordion */}
              {isExpanded && rec.actionSteps && (
                <div className="mt-4 pt-3 border-t border-slate-800">
                  <div className="text-xs font-bold text-cyan-400 mb-2">Step-by-Step Implementation Plan:</div>
                  <div className="rounded-xl bg-slate-900/90 p-3.5 border border-slate-800 text-xs text-slate-200 whitespace-pre-line leading-relaxed font-mono">
                    {rec.actionSteps}
                  </div>
                </div>
              )}

              {/* Footer Toggle */}
              <div className="mt-4 pt-3 border-t border-slate-800/80 flex items-center justify-between">
                <button
                  onClick={() => toggleExpand(rec.id)}
                  className="text-xs font-semibold text-cyan-400 hover:text-cyan-300 flex items-center gap-1"
                >
                  <span>{isExpanded ? 'Hide Implementation Steps' : 'View Implementation Steps'}</span>
                  {isExpanded ? <ChevronUp className="h-3.5 w-3.5" /> : <ChevronDown className="h-3.5 w-3.5" />}
                </button>

                <span className="text-[11px] text-slate-500">Autonomous Business Logic</span>
              </div>
            </div>
          );
        })}
      </div>

    </div>
  );
}
