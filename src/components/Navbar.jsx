import React from 'react';
import { 
  LayoutDashboard, 
  TrendingUp, 
  Lightbulb, 
  MessageSquare, 
  Database, 
  FileText, 
  Settings, 
  LogOut, 
  Sparkles,
  Building2
} from 'lucide-react';

export default function Navbar({ 
  currentTab, 
  setCurrentTab, 
  profile, 
  onOpenProfile, 
  onOpenReport, 
  user, 
  onLogout 
}) {
  const navItems = [
    { id: 'dashboard', label: 'Executive Dashboard', icon: LayoutDashboard },
    { id: 'predictive', label: 'Predictive Insights', icon: TrendingUp },
    { id: 'recommendations', label: 'Recommendations', icon: Lightbulb },
    { id: 'chat', label: 'AI Advisor', icon: MessageSquare },
    { id: 'data', label: 'Business Data', icon: Database },
  ];

  return (
    <header className="sticky top-0 z-30 border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6">
        
        {/* Brand */}
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-cyan-500 to-blue-600 text-slate-950 shadow-md shadow-cyan-500/20">
            <Sparkles className="h-5 w-5 text-white" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-bold tracking-tight text-white">BizConsult<span className="text-cyan-400">AI</span></span>
              <span className="rounded-full bg-cyan-950/80 px-2 py-0.5 text-[10px] font-semibold text-cyan-400 border border-cyan-800">SMB Intelligence</span>
            </div>
            <p className="text-xs text-slate-400 hidden sm:block">AI-Powered Decision Support</p>
          </div>
        </div>

        {/* Desktop Navigation */}
        <nav className="hidden lg:flex items-center gap-1 rounded-xl bg-slate-900/80 p-1 border border-slate-800">
          {navItems.map((item) => {
            const Icon = item.icon;
            const active = currentTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setCurrentTab(item.id)}
                className={`flex items-center gap-2 rounded-lg px-3.5 py-1.5 text-xs font-semibold transition-all ${
                  active 
                    ? 'bg-cyan-500 text-slate-950 shadow-sm' 
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`}
              >
                <Icon className={`h-4 w-4 ${active ? 'text-slate-950' : 'text-slate-400'}`} />
                {item.label}
              </button>
            );
          })}
        </nav>

        {/* Action Controls */}
        <div className="flex items-center gap-2">
          {/* Executive Report Button */}
          <button
            onClick={onOpenReport}
            className="flex items-center gap-1.5 rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs font-semibold text-cyan-300 hover:bg-slate-800 transition-colors"
            title="Generate Executive Briefing"
          >
            <FileText className="h-4 w-4 text-cyan-400" />
            <span className="hidden sm:inline">Executive Brief</span>
          </button>

          {/* Profile / Settings Button */}
          <button
            onClick={onOpenProfile}
            className="flex items-center gap-2 rounded-lg border border-slate-800 bg-slate-900 px-3 py-1.5 text-xs font-medium text-slate-300 hover:border-slate-700 transition-colors"
            title="Edit Business Profile"
          >
            <Building2 className="h-4 w-4 text-slate-400" />
            <span className="max-w-[120px] truncate hidden md:inline">{profile?.companyName || 'Business Profile'}</span>
          </button>

          {/* Logout */}
          <button
            onClick={onLogout}
            className="rounded-lg border border-slate-800 p-2 text-slate-400 hover:bg-slate-800 hover:text-rose-400 transition-colors"
            title={`Log out (${user?.fullName || 'User'})`}
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>

      {/* Mobile Sub-Navigation Bar */}
      <div className="flex lg:hidden overflow-x-auto border-t border-slate-800 bg-slate-950 px-2 py-1 scrollbar-none">
        {navItems.map((item) => {
          const Icon = item.icon;
          const active = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setCurrentTab(item.id)}
              className={`flex shrink-0 items-center gap-1.5 rounded-md px-3 py-1.5 text-xs font-medium ${
                active ? 'bg-cyan-500 text-slate-950 font-bold' : 'text-slate-400'
              }`}
            >
              <Icon className="h-3.5 w-3.5" />
              {item.label}
            </button>
          );
        })}
      </div>
    </header>
  );
}
