import React, { useState } from 'react';
import { Shield, Lock, Mail, AlertCircle, ArrowRight, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface LoginProps {
  onAccessDenied: () => void;
}

export const Login: React.FC<LoginProps> = ({ onAccessDenied }) => {
  const { login } = useAuth();
  const [email, setEmail] = useState('hardik@lifevlog.com');
  const [password, setPassword] = useState('hardik@2026');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [forgotSent, setForgotSent] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await login(email, password);
      if (!res.success) {
        if (res.role === 'user') {
          onAccessDenied();
        } else {
          setError(res.error || 'Login failed');
        }
      }
    } catch (err: any) {
      setError(err.message || 'An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleForgotPassword = () => {
    if (!email) {
      setError('Please enter your admin email address first');
      return;
    }
    setForgotSent(true);
    setTimeout(() => setForgotSent(false), 5000);
  };

  return (
    <div className="min-h-screen bg-[#0A0A0A] flex flex-col justify-center items-center p-4 relative overflow-hidden">
      {/* Background glowing orbs */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-orange-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md relative z-10">
        {/* Logo Card */}
        <div className="text-center mb-8">
          <div className="inline-flex w-16 h-16 rounded-2xl bg-gradient-to-tr from-amber-500 to-orange-500 items-center justify-center font-black text-black text-3xl shadow-xl shadow-amber-500/20 mb-4">
            H
          </div>
          <h1 className="text-2xl lg:text-3xl font-black tracking-tight text-white">
            Hardik Life Vlog
          </h1>
          <div className="flex items-center justify-center gap-2 mt-2">
            <span className="text-xs font-bold text-amber-400 uppercase tracking-widest bg-amber-400/10 border border-amber-400/20 px-3 py-0.5 rounded-full flex items-center gap-1.5">
              <Shield size={12} />
              Admin Control Console
            </span>
          </div>
          <p className="text-gray-400 text-xs mt-3">
            Secure multi-tenant creator dashboard & Firebase sync
          </p>
        </div>

        {/* Login Form Container */}
        <div className="bg-[#141414] border border-[#252525] rounded-2xl p-8 shadow-2xl">
          {error && (
            <div className="mb-5 p-3.5 bg-red-500/10 border border-red-500/30 rounded-xl flex items-start gap-3 text-red-400 text-xs">
              <AlertCircle size={18} className="shrink-0 mt-0.5" />
              <div>
                <p className="font-semibold">Authentication Error</p>
                <p className="mt-0.5 text-gray-300">{error}</p>
              </div>
            </div>
          )}

          {forgotSent && (
            <div className="mb-5 p-3.5 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-400 text-xs">
              Password reset link sent to <strong>{email}</strong> if registered as an administrator.
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-2">
                Admin Email
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-500">
                  <Mail size={18} />
                </div>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="admin@lifevlog.com"
                  className="w-full pl-10 pr-4 py-3 bg-[#1C1C1C] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="text-xs font-semibold text-gray-300 uppercase tracking-wider">
                  Password
                </label>
                <button
                  type="button"
                  onClick={handleForgotPassword}
                  className="text-xs text-amber-400 hover:text-amber-300 font-medium transition"
                >
                  Forgot Password?
                </button>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-500">
                  <Lock size={18} />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  className="w-full pl-10 pr-10 py-3 bg-[#1C1C1C] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-gray-500 hover:text-gray-300"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <div className="pt-2">
              <button
                type="submit"
                disabled={loading}
                className="w-full py-3.5 bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-sm rounded-xl shadow-lg shadow-amber-500/20 flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                {loading ? (
                  <div className="w-5 h-5 border-2 border-black border-t-transparent rounded-full animate-spin" />
                ) : (
                  <>
                    <span>Sign In to Admin Console</span>
                    <ArrowRight size={18} />
                  </>
                )}
              </button>
            </div>
          </form>

          {/* Quick Demo Credentials hint */}
          <div className="mt-6 pt-5 border-t border-[#222222] text-center">
            <p className="text-[11px] text-gray-500 mb-2">Default Admin Credentials:</p>
            <div className="flex flex-wrap items-center justify-center gap-2 text-[11px] text-gray-400">
              <span className="bg-[#1C1C1C] px-2.5 py-1 rounded-md border border-[#2D2D2D] font-mono">
                hardik@lifevlog.com
              </span>
              <span className="bg-[#1C1C1C] px-2.5 py-1 rounded-md border border-[#2D2D2D] font-mono">
                hardik@2026
              </span>
            </div>
          </div>
        </div>

        {/* Security badge */}
        <div className="mt-6 flex items-center justify-center gap-2 text-xs text-gray-500">
          <Shield size={14} className="text-emerald-500" />
          <span>Role-Based Access Control • Firestore Security Rules Enforced</span>
        </div>
      </div>
    </div>
  );
};
