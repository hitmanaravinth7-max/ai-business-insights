import React, { useState, useEffect } from 'react';
import { Routes, Route, useNavigate, useLocation, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Dashboard from './components/Dashboard';
import PredictiveInsights from './components/PredictiveInsights';
import Recommendations from './components/Recommendations';
import AiChat from './components/AiChat';
import DataManagement from './components/DataManagement';
import AuthModal from './components/AuthModal';
import BusinessProfileModal from './components/BusinessProfileModal';
import ExecutiveReportModal from './components/ExecutiveReportModal';
import { 
  INITIAL_DATASETS, 
  computeHealthScore, 
  detectRisks, 
  formatCurrency, 
  calculateLinearRegression 
} from './data/mockData';

export default function App() {
  const navigate = useNavigate();
  const location = useLocation();

  // Authentication State
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('bizconsult_user');
      return saved ? JSON.parse(saved) : {
        fullName: "Elena Vance",
        email: "elena@apexgearoutdoors.com",
        businessName: "ApexGear Outdoors"
      };
    } catch (e) {
      return {
        fullName: "Elena Vance",
        email: "elena@apexgearoutdoors.com",
        businessName: "ApexGear Outdoors"
      };
    }
  });

  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    try {
      return localStorage.getItem('bizconsult_logged_in') !== 'false';
    } catch (e) {
      return true;
    }
  });

  // Current Dataset Key
  const [currentDatasetKey, setCurrentDatasetKey] = useState('Retail');

  // Derive Current Tab from URL path
  const currentTab = location.pathname.startsWith('/predictive') || location.pathname.startsWith('/forecast')
    ? 'predictive'
    : location.pathname.startsWith('/recommendations')
    ? 'recommendations'
    : location.pathname.startsWith('/chat') || location.pathname.startsWith('/advisor')
    ? 'chat'
    : location.pathname.startsWith('/data')
    ? 'data'
    : location.pathname.startsWith('/login') || location.pathname.startsWith('/register')
    ? 'login'
    : 'dashboard';

  // Modals
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);

  // Active Business Data
  const [profile, setProfile] = useState(INITIAL_DATASETS.Retail.profile);
  const [metrics, setMetrics] = useState(INITIAL_DATASETS.Retail.metrics);
  const [products, setProducts] = useState(INITIAL_DATASETS.Retail.products);
  const [segments, setSegments] = useState(INITIAL_DATASETS.Retail.segments);
  const [channels, setChannels] = useState(INITIAL_DATASETS.Retail.channels);
  const [recommendations, setRecommendations] = useState(INITIAL_DATASETS.Retail.recommendations);

  // AI Chat & Advice State
  const [isGeneratingRecs, setIsGeneratingRecs] = useState(false);
  const [isChatThinking, setIsChatThinking] = useState(false);
  const [chatMessages, setChatMessages] = useState([
    {
      isUser: false,
      text: `Hello ${user?.fullName?.split(' ')[0] || 'there'}! I am your AI Business Consultant. I've analyzed ${profile.companyName}'s financial records, unit margins, and channel returns.\n\nAsk me anything about improving net profitability, managing cash flow, or mitigating customer churn.`
    }
  ]);

  // Switch Dataset
  const handleSwitchDataset = (key) => {
    const data = INITIAL_DATASETS[key];
    if (data) {
      setCurrentDatasetKey(key);
      setProfile({ ...data.profile });
      setMetrics([...data.metrics]);
      setProducts([...data.products]);
      setSegments([...data.segments]);
      setChannels([...data.channels]);
      setRecommendations([...data.recommendations]);
      
      setChatMessages([
        {
          isUser: false,
          text: `Switched active analysis context to **${data.profile.companyName}** (${data.profile.industry}). All KPIs, forecasts, and unit economics have been updated.`
        }
      ]);
    }
  };

  // Add new monthly metric
  const handleAddMetric = (newMetric) => {
    setMetrics(prev => [
      ...prev,
      {
        id: prev.length + 1,
        monthIndex: prev.length + 1,
        ...newMetric
      }
    ]);
  };

  // Update Recommendation Status
  const handleUpdateRecStatus = (id, newStatus) => {
    setRecommendations(prev =>
      prev.map(r => r.id === id ? { ...r, status: newStatus } : r)
    );
  };

  // Save profile updates
  const handleSaveProfile = (updatedProfile) => {
    setProfile(updatedProfile);
  };

  // Login Handler
  const handleLoginSuccess = (userData) => {
    setUser(userData);
    setIsLoggedIn(true);
    try {
      localStorage.setItem('bizconsult_user', JSON.stringify(userData));
      localStorage.setItem('bizconsult_logged_in', 'true');
    } catch (e) {}
    navigate('/dashboard');
  };

  // Logout Handler
  const handleLogout = () => {
    setIsLoggedIn(false);
    try {
      localStorage.setItem('bizconsult_logged_in', 'false');
    } catch (e) {}
    navigate('/login');
  };

  // Call Gemini or AI Decision Engine for Chat
  const handleSendMessage = async (userText) => {
    const newMsgs = [...chatMessages, { isUser: true, text: userText }];
    setChatMessages(newMsgs);
    setIsChatThinking(true);

    const latest = metrics[metrics.length - 1] || {};
    const totalCost = (latest.cogs || 0) + (latest.operatingExpenses || 0) + (latest.marketingSpend || 0);
    const netProfit = (latest.revenue || 0) - totalCost;
    const margin = latest.revenue ? ((netProfit / latest.revenue) * 100).toFixed(1) : "0";
    const regression = calculateLinearRegression(metrics);
    const bestChannel = [...channels].sort((a, b) => b.roas - a.roas)[0];
    const worstChannel = [...channels].sort((a, b) => a.roas - b.roas)[0];

    // Try Gemini API if key is present
    const geminiKey = import.meta.env?.VITE_GEMINI_API_KEY || "";
    let responseText = "";

    if (geminiKey) {
      try {
        const systemPrompt = `You are an elite small-to-medium business consultant assisting ${profile.companyName} (${profile.industry}).
Financial Vitals:
- Revenue: $${latest.revenue?.toLocaleString()}
- Operating Costs: $${totalCost.toLocaleString()}
- Net Profit: $${netProfit.toLocaleString()} (Margin: ${margin}%)
- Growth Trend: ${regression.trend} (${(regression.avgGrowthRate * 100).toFixed(1)}% MoM)
- Best Channel: ${bestChannel?.name} (${bestChannel?.roas}x ROAS)
- Weakest Channel: ${worstChannel?.name} (${worstChannel?.roas}x ROAS)
- Cash Runway: ${(latest.cashBuffer / (totalCost || 1)).toFixed(1)} months
Give concise, strategic, actionable, and executive business advice.`;

        const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${geminiKey}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [
              { role: 'user', parts: [{ text: `${systemPrompt}\n\nUser Question: ${userText}` }] }
            ]
          })
        });

        if (res.ok) {
          const json = await res.json();
          responseText = json.candidates?.[0]?.content?.parts?.[0]?.text;
        }
      } catch (err) {
        console.warn("Gemini call failed, falling back to local domain model:", err);
      }
    }

    // Fallback domain consultation engine if no API response
    if (!responseText) {
      await new Promise(r => setTimeout(r, 650));
      const query = userText.toLowerCase();

      if (query.includes('profit') || query.includes('margin')) {
        responseText = `Based on your latest month, ${profile.companyName} is generating a net profit margin of ${margin}% (${formatCurrency(netProfit)}).
        
Key levers to expand margin:
1. **Reduce Ad Spend Waste:** Reallocate $1,500 from ${worstChannel?.name} (${worstChannel?.roas}x ROAS) to ${bestChannel?.name} (${bestChannel?.roas}x ROAS).
2. **COGS Renegotiation:** Unit costs on your top volume products represent your largest margin leakage. Renegotiating 5% supplier discounts adds directly to net income.
3. **Price Elasticity:** Given your customer retention, a 3% price adjustment on your top-performing line would yield an estimated +$4,200 monthly profit with negligible volume loss.`;
      } else if (query.includes('marketing') || query.includes('roas') || query.includes('ad')) {
        responseText = `Marketing Performance Audit for ${profile.companyName}:
        
- **Top Performer:** ${bestChannel?.name} with ${bestChannel?.roas}x ROAS on $${bestChannel?.spend?.toLocaleString()} spend.
- **Underperforming:** ${worstChannel?.name} delivering only ${worstChannel?.roas}x ROAS on $${worstChannel?.spend?.toLocaleString()} spend.

**Recommendation:** Scale ${bestChannel?.name} budget by 20% while pausing non-converting ad sets on ${worstChannel?.name}. This rebalancing typically increases blended ROAS by 1.2x.`;
      } else if (query.includes('churn') || query.includes('retention') || query.includes('customer')) {
        responseText = `Your current monthly churn rate is ${(latest.churnRate * 100).toFixed(1)}%.
        
Strategic Fixes:
1. **VIP Protection:** Your ${segments[0]?.name || 'top tier'} represent high lifetime value. Introduce an exclusive loyalty perk.
2. **At-Risk Interventions:** Implement a 30-day inactivity trigger offering a curated discount or value-add check-in.
3. **Onboarding Tightening:** 60% of customer churn occurs in the first 45 days. Enhancing welcome guides reduces drop-off by up to 25%.`;
      } else if (query.includes('runway') || query.includes('cash') || query.includes('burn')) {
        const runway = (latest.cashBuffer / (totalCost || 1)).toFixed(1);
        responseText = `Liquidity Analysis:
- Total Cash Buffer: ${formatCurrency(latest.cashBuffer || 0)}
- Monthly Operating Burn: ${formatCurrency(totalCost)}
- Effective Cash Runway: **${runway} Months**

${Number(runway) >= 6 ? 'Your cash buffer is healthy (>6 months). You have sufficient runway to test expansion initiatives.' : 'Caution: Runway is below 6 months. Prioritize cash preservation and immediate gross-margin positive sales activities.'}`;
      } else {
        responseText = `Strategic Assessment for ${profile.companyName}:
Your current revenue trajectory is **${regression.trend}** with an average MoM growth rate of ${(regression.avgGrowthRate * 100).toFixed(1)}%.

Recommended priorities:
1. Double down on **${bestChannel?.name}** which is delivering your highest return (${bestChannel?.roas}x ROAS).
2. Protect gross margins by bundling lower-margin inventory with top-selling core products.
3. Target the **${segments[0]?.name || 'primary customer segment'}** for higher-tier service upgrades.`;
      }
    }

    setChatMessages(prev => [...prev, { isUser: false, text: responseText }]);
    setIsChatThinking(false);
  };

  // Generate Fresh AI Recommendations
  const handleGenerateAiRecs = async () => {
    setIsGeneratingRecs(true);
    await new Promise(r => setTimeout(r, 1200));

    const latest = metrics[metrics.length - 1] || {};
    const newItems = [
      {
        id: Date.now(),
        title: `Scale ${channels[0]?.name || 'Search Ads'} by 25% to Capture High-Intent Demand`,
        category: "Marketing ROI",
        priority: "High",
        summary: `Strong ROAS (${channels[0]?.roas || 4.2}x) indicates unexhausted search market volume.`,
        expectedImpact: "+$4,800 Incremental Monthly Net Profit",
        actionSteps: "1. Increase daily budget cap on top-performing search terms.\n2. Add negative keywords to prevent wasteful clicks.\n3. Track customer conversion cost weekly.",
        status: "Pending"
      },
      {
        id: Date.now() + 1,
        title: `Implement Minimum Order Value (AOV) Threshold for Free Freight`,
        category: "Revenue Optimization",
        priority: "Medium",
        summary: `Average order value is currently $${latest.avgOrderValue || 95}. Raising the free-shipping threshold to $${(latest.avgOrderValue || 95) + 25} nudges cart sizes.`,
        expectedImpact: "+12% Increase in Average Order Value",
        actionSteps: "1. Update cart checkout progress banner.\n2. Recommend high-margin accessories at checkout.",
        status: "Pending"
      }
    ];

    setRecommendations(prev => [...newItems, ...prev]);
    setIsGeneratingRecs(false);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      
      {/* Navigation Header */}
      <Navbar
        currentTab={currentTab}
        setCurrentTab={(tab) => {
          if (tab === 'dashboard') navigate('/dashboard');
          else navigate(`/${tab}`);
        }}
        profile={profile}
        onOpenProfile={() => setShowProfileModal(true)}
        onOpenReport={() => setShowReportModal(true)}
        user={user}
        onLogout={handleLogout}
      />

      {/* Main Container with React Router Routes */}
      <main className="flex-1 mx-auto w-full max-w-7xl px-4 py-6 sm:px-6">
        <Routes>
          {/* Root and Dashboard */}
          <Route path="/" element={
            <Dashboard
              profile={profile}
              metrics={metrics}
              products={products}
              segments={segments}
              channels={channels}
              onNavigate={(tab) => navigate(`/${tab}`)}
            />
          } />

          <Route path="/dashboard" element={
            <Dashboard
              profile={profile}
              metrics={metrics}
              products={products}
              segments={segments}
              channels={channels}
              onNavigate={(tab) => navigate(`/${tab}`)}
            />
          } />

          {/* Predictive Insights */}
          <Route path="/predictive" element={
            <PredictiveInsights metrics={metrics} />
          } />
          <Route path="/forecast" element={
            <PredictiveInsights metrics={metrics} />
          } />

          {/* Recommendations Engine */}
          <Route path="/recommendations" element={
            <Recommendations
              recommendations={recommendations}
              onUpdateStatus={handleUpdateRecStatus}
              onGenerateAiRecs={handleGenerateAiRecs}
              isGenerating={isGeneratingRecs}
            />
          } />

          {/* AI Advisor Chat */}
          <Route path="/chat" element={
            <AiChat
              profile={profile}
              metrics={metrics}
              products={products}
              channels={channels}
              segments={segments}
              messages={chatMessages}
              onSendMessage={handleSendMessage}
              isThinking={isChatThinking}
            />
          } />
          <Route path="/advisor" element={
            <AiChat
              profile={profile}
              metrics={metrics}
              products={products}
              channels={channels}
              segments={segments}
              messages={chatMessages}
              onSendMessage={handleSendMessage}
              isThinking={isChatThinking}
            />
          } />

          {/* Business Data Management */}
          <Route path="/data" element={
            <DataManagement
              metrics={metrics}
              onAddMetric={handleAddMetric}
              onSwitchDataset={handleSwitchDataset}
              currentDatasetKey={currentDatasetKey}
            />
          } />

          {/* Authentication Pages */}
          <Route path="/login" element={
            <AuthModal
              onLoginSuccess={handleLoginSuccess}
              onBack={() => navigate('/dashboard')}
            />
          } />
          <Route path="/register" element={
            <AuthModal
              onLoginSuccess={handleLoginSuccess}
              onBack={() => navigate('/dashboard')}
            />
          } />

          {/* Fallback unknown routes safely redirect to root application */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      {/* Modals */}
      <BusinessProfileModal
        isOpen={showProfileModal}
        onClose={() => setShowProfileModal(false)}
        profile={profile}
        onSave={handleSaveProfile}
      />

      <ExecutiveReportModal
        isOpen={showReportModal}
        onClose={() => setShowReportModal(false)}
        profile={profile}
        metrics={metrics}
        products={products}
        channels={channels}
        segments={segments}
        recommendations={recommendations}
      />

      {/* Minimal Footer */}
      <footer className="border-t border-slate-900 bg-slate-950 py-4 px-6 text-center text-xs text-slate-500">
        <p>BizConsult AI • Intelligent Decision Support & Business Advisory Platform</p>
      </footer>
    </div>
  );
}
