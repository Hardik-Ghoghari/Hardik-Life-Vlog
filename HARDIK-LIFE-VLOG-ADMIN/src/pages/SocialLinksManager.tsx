import React, { useState, useEffect } from 'react';
import { Share2, Check, X, Save, CheckCircle, ExternalLink } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { SocialLink } from '../types';

export const SocialLinksManager: React.FC = () => {
  const [links, setLinks] = useState<SocialLink[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    loadLinks();
  }, []);

  const loadLinks = async () => {
    setLoading(true);
    const l = await FirestoreService.getSocialLinks();
    setLinks(l.sort((a, b) => a.order - b.order));
    setLoading(false);
  };

  const handleUrlChange = (id: string, url: string) => {
    setLinks(links.map(l => l.id === id ? { ...l, url } : l));
  };

  const handleToggleActive = (id: string) => {
    setLinks(links.map(l => l.id === id ? { ...l, isActive: !l.isActive } : l));
  };

  const handleSave = async () => {
    setSaving(true);
    await FirestoreService.saveSocialLinks(links);
    setSaving(false);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Social Media & Community</h1>
          <p className="text-xs text-gray-400">
            Configure social channels displayed across the Android Home and Profile screens
          </p>
        </div>
        {savedSuccess && (
          <div className="px-3.5 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-bold flex items-center gap-2">
            <CheckCircle size={15} />
            Live Sync Complete
          </div>
        )}
      </div>

      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl space-y-4">
        {links.map((link) => (
          <div
            key={link.id}
            className="p-4 rounded-2xl bg-[#1E1E1E] border border-[#2A2A2A] flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:border-[#383838] transition"
          >
            <div className="flex items-center gap-3 sm:w-40 shrink-0">
              <div className="w-10 h-10 rounded-xl bg-[#262626] flex items-center justify-center text-amber-400 font-bold text-sm">
                {link.platform[0]}
              </div>
              <div>
                <h4 className="text-sm font-bold text-white">{link.platform}</h4>
                <span className="text-[10px] text-gray-400">Priority #{link.order}</span>
              </div>
            </div>

            <div className="flex-1">
              <input
                type="url"
                value={link.url}
                onChange={(e) => handleUrlChange(link.id, e.target.value)}
                placeholder={`https://${link.platform.toLowerCase()}.com/...`}
                className="w-full px-3.5 py-2.5 bg-[#141414] border border-[#333333] rounded-xl text-white text-xs focus:outline-none focus:border-amber-500 font-mono"
              />
            </div>

            <div className="flex items-center gap-3 justify-end">
              <button
                type="button"
                onClick={() => handleToggleActive(link.id)}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold flex items-center gap-1.5 transition ${
                  link.isActive
                    ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                    : 'bg-[#282828] text-gray-400 border border-transparent'
                }`}
              >
                {link.isActive ? <Check size={13} /> : <X size={13} />}
                {link.isActive ? 'Active' : 'Hidden'}
              </button>

              <a
                href={link.url}
                target="_blank"
                rel="noreferrer"
                className="p-2 text-gray-400 hover:text-white rounded-lg hover:bg-[#282828]"
                title="Open link"
              >
                <ExternalLink size={16} />
              </a>
            </div>
          </div>
        ))}

        <div className="pt-4 flex justify-end border-t border-[#242424]">
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex items-center gap-2 px-6 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-sm shadow-xl shadow-amber-500/20 transition"
          >
            <Save size={18} />
            <span>{saving ? 'Updating Firebase...' : 'Save Social Channels'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
