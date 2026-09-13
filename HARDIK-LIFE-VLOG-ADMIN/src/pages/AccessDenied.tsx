import React from 'react';
import { ShieldAlert, ArrowLeft, Lock } from 'lucide-react';

interface AccessDeniedProps {
  onBackToLogin: () => void;
}

export const AccessDenied: React.FC<AccessDeniedProps> = ({ onBackToLogin }) => {
  return (
    <div className="min-h-screen bg-[#0A0A0A] flex flex-col justify-center items-center p-6 text-center">
      <div className="w-full max-w-md bg-[#161616] border border-red-500/30 rounded-3xl p-8 shadow-2xl">
        <div className="w-16 h-16 rounded-2xl bg-red-500/10 text-red-500 border border-red-500/20 flex items-center justify-center mx-auto mb-5">
          <ShieldAlert size={36} />
        </div>

        <h1 className="text-2xl font-black text-white tracking-tight mb-2">
          403 — Access Denied
        </h1>

        <p className="text-sm text-gray-400 mb-6 leading-relaxed">
          You are authenticated as a standard viewer account. The <strong>Hardik Life Vlog Admin Panel</strong> is strictly restricted to accounts with administrator role <code className="text-amber-400 bg-amber-400/10 px-1.5 py-0.5 rounded font-mono text-xs">role = "admin"</code>.
        </p>

        <div className="p-4 bg-[#1F1F1F] rounded-2xl border border-[#2D2D2D] text-left text-xs text-gray-300 mb-6 space-y-2">
          <div className="flex items-center gap-2 text-red-400 font-semibold">
            <Lock size={14} />
            <span>Security Policy Violation Prevented</span>
          </div>
          <p className="text-gray-400">
            Frontend router blocks access and Firestore Security Rules automatically reject write and administrative read operations for non-admin tokens.
          </p>
        </div>

        <button
          onClick={onBackToLogin}
          className="w-full py-3 bg-[#262626] hover:bg-[#303030] text-white font-bold text-sm rounded-xl transition flex items-center justify-center gap-2"
        >
          <ArrowLeft size={16} />
          <span>Return to Admin Login</span>
        </button>
      </div>
    </div>
  );
};
