import React, { useState } from 'react';
import { 
  Lock, 
  Mail, 
  User, 
  Building, 
  Eye, 
  EyeOff, 
  CheckCircle2, 
  AlertCircle, 
  Sparkles,
  ArrowRight,
  ShieldCheck
} from 'lucide-react';

export default function AuthModal({ onLoginSuccess }) {
  const [isLoginMode, setIsLoginMode] = useState(true);
  
  // Fields
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [businessName, setBusinessName] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [rememberMe, setRememberMe] = useState(true);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [infoMessage, setInfoMessage] = useState('');

  // Password strength calculation
  const getPasswordStrength = (pass) => {
    if (!pass) return { label: 'Empty', score: 0, color: 'bg-slate-700' };
    let score = 0;
    if (pass.length >= 8) score++;
    if (/[A-Z]/.test(pass) && /[a-z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;

    switch (score) {
      case 4: return { label: 'Strong', score: 4, color: 'bg-emerald-500' };
      case 3: return { label: 'Good', score: 3, color: 'bg-cyan-400' };
      case 2: return { label: 'Fair', score: 2, color: 'bg-amber-400' };
      default: return { label: 'Weak', score: 1, color: 'bg-rose-500' };
    }
  };

  const strength = getPasswordStrength(password);

  const handleLogin = (e) => {
    e?.preventDefault();
    setError('');
    setInfoMessage('');

    if (!email || !email.includes('@')) {
      setError('Please provide a valid business email.');
      return;
    }
    if (!password) {
      setError('Password is required.');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onLoginSuccess({
        id: Date.now(),
        email: email.trim().toLowerCase(),
        fullName: fullName || email.split('@')[0],
        businessName: businessName || 'Enterprise Growth Co.'
      });
    }, 600);
  };

  const handleRegister = (e) => {
    e?.preventDefault();
    setError('');
    setInfoMessage('');

    if (!fullName.trim()) {
      setError('Full Name is required.');
      return;
    }
    if (!businessName.trim()) {
      setError('Business Name is required.');
      return;
    }
    if (!email || !email.includes('@')) {
      setError('Please provide a valid work email.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (!termsAccepted) {
      setError('Please accept the Terms & Conditions.');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onLoginSuccess({
        id: Date.now(),
        email: email.trim().toLowerCase(),
        fullName: fullName.trim(),
        businessName: businessName.trim()
      });
    }, 700);
  };

  const handleForgotPassword = () => {
    if (!email || !email.includes('@')) {
      setError('Enter your work email address above to receive reset instructions.');
    } else {
      setError('');
      setInfoMessage(`Password reset link dispatched to ${email}. Check your inbox.`);
    }
  };

  const loadDemo = (demoEmail, demoName, demoBiz) => {
    setEmail(demoEmail);
    setPassword('Password123!');
    setConfirmPassword('Password123!');
    setFullName(demoName);
    setBusinessName(demoBiz);
    setTermsAccepted(true);
    setError('');
    setInfoMessage(`Loaded demo profile: ${demoBiz}. Click Submit to proceed.`);
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center p-4 sm:p-6 bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 relative overflow-hidden">
      {/* Background Ambient Glows */}
      <div className="absolute top-1/4 left-1/4 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/4 translate-x-1/2 translate-y-1/2 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md relative z-10">
        {/* Brand Header */}
        <div className="text-center mb-6">
          <div className="inline-flex h-14 w-14 items-center justify-center rounded-2xl bg-gradient-to-tr from-cyan-500 to-blue-600 shadow-lg shadow-cyan-500/25 mb-3">
            <Sparkles className="h-7 w-7 text-white" />
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-white">BizConsult<span className="text-cyan-400">AI</span></h1>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">Autonomous decision-support platform for modern business leaders</p>
        </div>

        {/* Card */}
        <div className="glass-card rounded-2xl p-6 sm:p-8 shadow-2xl">
          {/* Mode Switcher */}
          <div className="grid grid-cols-2 gap-1 rounded-xl bg-slate-950 p-1 mb-6 border border-slate-800">
            <button
              type="button"
              onClick={() => { setIsLoginMode(true); setError(''); setInfoMessage(''); }}
              className={`py-2 text-xs sm:text-sm font-semibold rounded-lg transition-all ${
                isLoginMode ? 'bg-cyan-500 text-slate-950 shadow' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Sign In
            </button>
            <button
              type="button"
              onClick={() => { setIsLoginMode(false); setError(''); setInfoMessage(''); }}
              className={`py-2 text-xs sm:text-sm font-semibold rounded-lg transition-all ${
                !isLoginMode ? 'bg-cyan-500 text-slate-950 shadow' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Create Account
            </button>
          </div>

          {/* Feedback Banners */}
          {error && (
            <div className="mb-4 flex items-center gap-2 rounded-lg bg-rose-500/10 border border-rose-500/30 p-3 text-xs text-rose-400">
              <AlertCircle className="h-4 w-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}
          {infoMessage && (
            <div className="mb-4 flex items-center gap-2 rounded-lg bg-cyan-500/10 border border-cyan-500/30 p-3 text-xs text-cyan-300">
              <CheckCircle2 className="h-4 w-4 shrink-0" />
              <span>{infoMessage}</span>
            </div>
          )}

          <form onSubmit={isLoginMode ? handleLogin : handleRegister} className="space-y-4">
            {!isLoginMode && (
              <>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Full Name</label>
                  <div className="relative">
                    <User className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
                    <input
                      type="text"
                      value={fullName}
                      onChange={(e) => setFullName(e.target.value)}
                      placeholder="e.g. Elena Vance"
                      className="w-full rounded-lg border border-slate-700 bg-slate-900/90 pl-9 pr-3 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-400 focus:outline-none"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Business Name</label>
                  <div className="relative">
                    <Building className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
                    <input
                      type="text"
                      value={businessName}
                      onChange={(e) => setBusinessName(e.target.value)}
                      placeholder="e.g. ApexGear Outdoors"
                      className="w-full rounded-lg border border-slate-700 bg-slate-900/90 pl-9 pr-3 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-400 focus:outline-none"
                    />
                  </div>
                </div>
              </>
            )}

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Business Email</label>
              <div className="relative">
                <Mail className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="elena@apexgear.com"
                  className="w-full rounded-lg border border-slate-700 bg-slate-900/90 pl-9 pr-3 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-400 focus:outline-none"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full rounded-lg border border-slate-700 bg-slate-900/90 pl-9 pr-10 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-400 focus:outline-none"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-2.5 text-slate-500 hover:text-slate-300"
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>

              {/* Password strength meter on register */}
              {!isLoginMode && password && (
                <div className="mt-2 space-y-1">
                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400">Strength: <strong className="text-slate-200">{strength.label}</strong></span>
                    <span className="text-slate-500">Min 8 chars, numbers & symbols</span>
                  </div>
                  <div className="grid grid-cols-4 gap-1 h-1.5">
                    {[1, 2, 3, 4].map((step) => (
                      <div
                        key={step}
                        className={`rounded-full h-full ${
                          step <= strength.score ? strength.color : 'bg-slate-800'
                        }`}
                      />
                    ))}
                  </div>
                </div>
              )}
            </div>

            {!isLoginMode && (
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Confirm Password</label>
                <div className="relative">
                  <Lock className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full rounded-lg border border-slate-700 bg-slate-900/90 pl-9 pr-3 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-400 focus:outline-none"
                  />
                </div>
              </div>
            )}

            {/* Checkboxes Row */}
            <div className="flex items-center justify-between text-xs pt-1">
              {isLoginMode ? (
                <>
                  <label className="flex items-center gap-2 cursor-pointer text-slate-300">
                    <input
                      type="checkbox"
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                      className="rounded border-slate-700 bg-slate-900 text-cyan-500 focus:ring-0"
                    />
                    <span>Remember me</span>
                  </label>
                  <button
                    type="button"
                    onClick={handleForgotPassword}
                    className="text-cyan-400 hover:text-cyan-300 font-medium"
                  >
                    Forgot password?
                  </button>
                </>
              ) : (
                <label className="flex items-center gap-2 cursor-pointer text-slate-300">
                  <input
                    type="checkbox"
                    checked={termsAccepted}
                    onChange={(e) => setTermsAccepted(e.target.checked)}
                    className="rounded border-slate-700 bg-slate-900 text-cyan-500 focus:ring-0"
                  />
                  <span>I agree to the Terms of Service & Privacy Policy</span>
                </label>
              )}
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 py-2.5 text-sm font-bold text-slate-950 shadow-lg shadow-cyan-500/20 hover:from-cyan-400 hover:to-blue-500 transition-all disabled:opacity-50 flex items-center justify-center gap-2"
            >
              {loading ? (
                <div className="h-5 w-5 animate-spin rounded-full border-2 border-slate-950 border-t-transparent" />
              ) : (
                <>
                  <span>{isLoginMode ? 'Log In to Workspace' : 'Create My Account'}</span>
                  <ArrowRight className="h-4 w-4" />
                </>
              )}
            </button>
          </form>

          {/* Social Sign-in Option */}
          <div className="mt-5 pt-4 border-t border-slate-800">
            <button
              type="button"
              onClick={() => loadDemo('demo@google.com', 'Google Demo User', 'Summit Enterprises')}
              className="w-full flex items-center justify-center gap-2 rounded-xl border border-slate-700 bg-slate-900 py-2 text-xs font-semibold text-slate-300 hover:bg-slate-800 transition-colors"
            >
              <svg className="h-4 w-4" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
              </svg>
              <span>Continue with Google</span>
            </button>
          </div>

          {/* Demo Data Quick Fills */}
          <div className="mt-5 pt-4 border-t border-slate-800 text-center">
            <span className="text-[11px] font-semibold tracking-wider uppercase text-slate-500">Explore Demo Workspaces</span>
            <div className="grid grid-cols-2 gap-2 mt-2">
              <button
                type="button"
                onClick={() => loadDemo('elena@apexgear.com', 'Elena Vance', 'ApexGear Outdoors')}
                className="rounded-lg border border-slate-800 bg-slate-900/60 p-2 text-left hover:border-cyan-500/50 transition-colors"
              >
                <div className="text-xs font-semibold text-slate-200">ApexGear</div>
                <div className="text-[10px] text-cyan-400">Retail / E-Commerce</div>
              </button>
              <button
                type="button"
                onClick={() => loadDemo('marcus@cloudpulse.io', 'Marcus Cole', 'CloudPulse Analytics')}
                className="rounded-lg border border-slate-800 bg-slate-900/60 p-2 text-left hover:border-emerald-500/50 transition-colors"
              >
                <div className="text-xs font-semibold text-slate-200">CloudPulse</div>
                <div className="text-[10px] text-emerald-400">B2B SaaS / Tech</div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
