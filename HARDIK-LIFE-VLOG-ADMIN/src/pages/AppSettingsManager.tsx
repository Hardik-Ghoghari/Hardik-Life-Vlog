import React, { useState, useEffect } from 'react';
import { Settings, Save, CheckCircle, ShieldAlert, Wrench, AlertTriangle, Key } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { AppSettings } from '../types';

export const AppSettingsManager: React.FC = () => {
  const [settings, setSettings] = useState<AppSettings | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    const s = await FirestoreService.getAppSettings();
    setSettings(s);
    setLoading(false);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!settings) return;
    setSaving(true);
    await FirestoreService.saveAppSettings(settings);
    setSaving(false);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3500);
  };

  if (loading || !settings) {
    return <div className="p-12 text-center text-gray-500 text-sm">Loading config...</div>;
  }

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Global Android App Configuration</h1>
          <p className="text-xs text-gray-400">
            Control dynamic app behaviors, maintenance banners, version enforcement, and legal policies
          </p>
        </div>
        {savedSuccess && (
          <div className="px-3.5 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-bold flex items-center gap-2">
            <CheckCircle size={15} />
            Android App Config Updated!
          </div>
        )}
      </div>

      <form onSubmit={handleSave} className="space-y-6">
        {/* Maintenance Mode Alert Box */}
        <div className={`p-6 rounded-3xl border transition ${
          settings.maintenanceMode
            ? 'bg-red-950/20 border-red-500/50'
            : 'bg-[#161616] border-[#262626]'
        }`}>
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Wrench size={20} className={settings.maintenanceMode ? 'text-red-400' : 'text-amber-400'} />
              <h3 className="text-base font-bold text-white">Emergency Maintenance Mode</h3>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={settings.maintenanceMode}
                onChange={(e) => setSettings({ ...settings, maintenanceMode: e.target.checked })}
                className="sr-only peer"
              />
              <div className="w-11 h-6 bg-gray-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-red-600"></div>
            </label>
          </div>
          <p className="text-xs text-gray-400 mb-3">
            When enabled, all Android apps display a full-screen maintenance message and lock playback features.
          </p>
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Custom Maintenance Message
            </label>
            <input
              type="text"
              value={settings.maintenanceMessage}
              onChange={(e) => setSettings({ ...settings, maintenanceMessage: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs focus:outline-none"
            />
          </div>
        </div>

        {/* Version Enforcements */}
        <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl space-y-4">
          <h3 className="text-base font-bold text-white">App Version Enforcement</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Current Production Version
              </label>
              <input
                type="text"
                value={settings.appVersion}
                onChange={(e) => setSettings({ ...settings, appVersion: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Minimum Supported Version
              </label>
              <input
                type="text"
                value={settings.minimumVersion}
                onChange={(e) => setSettings({ ...settings, minimumVersion: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs"
              />
            </div>
          </div>

          <div className="flex items-center gap-2 pt-1">
            <input
              type="checkbox"
              id="force_update"
              checked={settings.forceUpdate}
              onChange={(e) => setSettings({ ...settings, forceUpdate: e.target.checked })}
              className="w-4 h-4 rounded text-amber-500 bg-[#1E1E1E] border-gray-600"
            />
            <label htmlFor="force_update" className="text-xs font-semibold text-gray-300 cursor-pointer">
              Force Update (Users below minimum version must update before continuing)
            </label>
          </div>
        </div>

        {/* Legal & Support Links */}
        <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl space-y-4">
          <h3 className="text-base font-bold text-white">Support & Legal URLs</h3>
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Support Email
            </label>
            <input
              type="email"
              value={settings.supportEmail}
              onChange={(e) => setSettings({ ...settings, supportEmail: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Privacy Policy URL
            </label>
            <input
              type="url"
              value={settings.privacyPolicyUrl}
              onChange={(e) => setSettings({ ...settings, privacyPolicyUrl: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs font-mono"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Terms of Service URL
            </label>
            <input
              type="url"
              value={settings.termsOfServiceUrl}
              onChange={(e) => setSettings({ ...settings, termsOfServiceUrl: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs font-mono"
            />
          </div>
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            disabled={saving}
            className="flex items-center gap-2 px-6 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-sm shadow-xl shadow-amber-500/20 transition disabled:opacity-50"
          >
            <Save size={18} />
            <span>{saving ? 'Updating...' : 'Save Global Settings'}</span>
          </button>
        </div>
      </form>
    </div>
  );
};
