import React, { useState, useRef, useEffect } from 'react';
import { 
  Send, 
  Sparkles, 
  Bot, 
  User, 
  CornerDownLeft, 
  Flame, 
  Lightbulb,
  TrendingUp,
  ShieldCheck
} from 'lucide-react';
import { formatCurrency } from '../data/mockData';

export default function AiChat({ 
  profile, 
  metrics, 
  products, 
  channels, 
  segments,
  messages,
  onSendMessage,
  isThinking
}) {
  const [input, setInput] = useState('');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isThinking]);

  const handleSend = (e) => {
    e?.preventDefault();
    if (!input.trim() || isThinking) return;
    const text = input;
    setInput('');
    onSendMessage(text);
  };

  const suggestions = [
    "How can I increase profit margins by 5%?",
    "Which marketing channel has the highest ROAS?",
    "Analyze our customer churn and how to fix it",
    "What is our operating cash runway?"
  ];

  return (
    <div className="flex flex-col h-[calc(100vh-12rem)] min-h-[500px] glass-card rounded-2xl border border-slate-800 overflow-hidden">
      
      {/* Chat Top Bar */}
      <div className="flex items-center justify-between border-b border-slate-800 bg-slate-900/80 px-6 py-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-cyan-500 to-blue-600 text-slate-950 font-bold">
            <Bot className="h-5 w-5 text-white" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="text-sm font-bold text-white">BizConsult AI Advisor</h3>
              <span className="flex h-2 w-2 rounded-full bg-emerald-400" />
            </div>
            <p className="text-xs text-slate-400">
              Grounded on <strong className="text-cyan-400">{profile.companyName}</strong> financial models & metrics
            </p>
          </div>
        </div>

        <span className="rounded-lg bg-slate-800/80 px-2.5 py-1 text-[11px] font-semibold text-slate-300 border border-slate-700">
          Gemini Decision Engine
        </span>
      </div>

      {/* Messages Scroll Area */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-4">
        {messages.map((msg, i) => (
          <div
            key={i}
            className={`flex items-start gap-3 ${msg.isUser ? 'justify-end' : 'justify-start'}`}
          >
            {!msg.isUser && (
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
                <Bot className="h-4 w-4" />
              </div>
            )}

            <div
              className={`max-w-[85%] sm:max-w-[75%] rounded-2xl px-4 py-3 text-xs sm:text-sm leading-relaxed ${
                msg.isUser
                  ? 'bg-cyan-500 text-slate-950 font-medium'
                  : 'bg-slate-900/90 border border-slate-800 text-slate-200'
              }`}
            >
              {!msg.isUser && (
                <div className="text-[10px] font-bold uppercase tracking-wider text-cyan-400 mb-1">
                  AI Consultant
                </div>
              )}
              <div className="whitespace-pre-line">{msg.text}</div>
            </div>

            {msg.isUser && (
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-blue-600 text-white">
                <User className="h-4 w-4" />
              </div>
            )}
          </div>
        ))}

        {isThinking && (
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
              <Bot className="h-4 w-4" />
            </div>
            <div className="rounded-2xl bg-slate-900 border border-slate-800 px-4 py-2.5 text-xs text-slate-400 flex items-center gap-2">
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-cyan-400 border-t-transparent" />
              <span>Analyzing financial models, channel elasticity, and margins...</span>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Suggested Prompts */}
      <div className="border-t border-slate-800/80 bg-slate-950/60 px-4 py-2 flex items-center gap-2 overflow-x-auto scrollbar-none">
        <span className="text-[11px] font-semibold text-slate-500 whitespace-nowrap">Suggested:</span>
        {suggestions.map((prompt, i) => (
          <button
            key={i}
            onClick={() => onSendMessage(prompt)}
            className="rounded-lg border border-slate-800 bg-slate-900/80 px-2.5 py-1 text-[11px] text-slate-300 hover:border-cyan-500/50 hover:text-cyan-300 whitespace-nowrap transition-colors"
          >
            {prompt}
          </button>
        ))}
      </div>

      {/* Input Box */}
      <div className="border-t border-slate-800 bg-slate-900/90 p-3 sm:p-4">
        <form onSubmit={handleSend} className="flex items-center gap-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Ask a strategic business question (e.g., 'How do I cut CAC?')..."
            className="flex-1 rounded-xl border border-slate-700 bg-slate-950 px-4 py-2.5 text-xs sm:text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-400 focus:outline-none"
          />
          <button
            type="submit"
            disabled={!input.trim() || isThinking}
            className="flex h-10 w-10 items-center justify-center rounded-xl bg-cyan-500 text-slate-950 hover:bg-cyan-400 disabled:opacity-40 transition-colors shadow-md shadow-cyan-500/20"
          >
            <Send className="h-4 w-4" />
          </button>
        </form>
      </div>

    </div>
  );
}
