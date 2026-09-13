import React, { useState, useEffect } from 'react';
import { UserCheck, CheckCircle, Save, Sparkles, Mail, Eye } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { CreatorProfile } from '../types';

export const CreatorProfileManager: React.FC = () => {
  const [profile, setProfile] = useState<CreatorProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setLoading(true);
    const p = await FirestoreService.getCreatorProfile();
    setProfile(p);
    setLoading(false);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;
    setSaving(true);
    await FirestoreService.saveCreatorProfile(profile);
    setSaving(false);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3500);
  };

  if (loading || !profile) {
    return <div className="p-12 text-center text-gray-500 text-sm">Loading profile from Firebase...</div>;
  }

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Creator Profile Management</h1>
          <p className="text-xs text-gray-400">
            Edit bio, avatar, and creator story that viewers see on the Android Profile screen
          </p>
        </div>
        {savedSuccess && (
          <div className="px-3.5 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-bold flex items-center gap-2">
            <CheckCircle size={15} />
            Updated Live in Android App
          </div>
        )}
      </div>

      <form onSubmit={handleSave} className="space-y-6">
        {/* Creator Identity & Avatar Preview */}
        <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl space-y-6">
          <div className="flex flex-col sm:flex-row items-center gap-6 pb-6 border-b border-[#242424]">
            <div className="relative">
              <div className="w-24 h-24 rounded-2xl overflow-hidden border-2 border-amber-500 shadow-xl bg-black">
                <img
                  src={profile.photoUrl}
                  alt={profile.name}
                  className="w-full h-full object-cover"
                />
              </div>
              <span className="absolute -bottom-2 -right-2 bg-amber-500 text-black text-[10px] font-extrabold px-2 py-0.5 rounded-full shadow">
                Live
              </span>
            </div>
            <div className="flex-1 space-y-2 text-center sm:text-left">
              <h2 className="text-xl font-bold text-white flex items-center justify-center sm:justify-start gap-2">
                <span>{profile.name}</span>
                <span className="text-xs font-semibold text-amber-400 bg-amber-400/10 px-2 py-0.5 rounded">
                  {profile.brand}
                </span>
              </h2>
              <p className="text-xs text-gray-400 max-w-lg">{profile.tagline}</p>
              <div className="flex items-center justify-center sm:justify-start gap-4 text-xs text-gray-400 font-mono pt-1">
                <span>Subscribers: <strong className="text-white">{profile.subscribersCount}</strong></span>
                <span>Vlogs: <strong className="text-white">{profile.totalVlogsCount}</strong></span>
                <span>Shorts: <strong className="text-white">{profile.totalShortsCount}</strong></span>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Creator Name *
              </label>
              <input
                type="text"
                required
                value={profile.name}
                onChange={(e) => setProfile({ ...profile, name: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Brand / Channel Name *
              </label>
              <input
                type="text"
                required
                value={profile.brand}
                onChange={(e) => setProfile({ ...profile, brand: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Tagline / Specialty
              </label>
              <input
                type="text"
                value={profile.tagline}
                onChange={(e) => setProfile({ ...profile, tagline: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Contact Email
              </label>
              <input
                type="email"
                value={profile.contactEmail}
                onChange={(e) => setProfile({ ...profile, contactEmail: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Profile Photo URL (or Storage URL)
            </label>
            <input
              type="url"
              required
              value={profile.photoUrl}
              onChange={(e) => setProfile({ ...profile, photoUrl: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Short Bio (Profile Header)
            </label>
            <textarea
              rows={2}
              value={profile.bio}
              onChange={(e) => setProfile({ ...profile, bio: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              About Hardik (In-depth Biography & Journey)
            </label>
            <textarea
              rows={4}
              value={profile.about}
              onChange={(e) => setProfile({ ...profile, about: e.target.value })}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none leading-relaxed"
            />
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-400 mb-1">
                Subscribers Badge
              </label>
              <input
                type="text"
                value={profile.subscribersCount}
                onChange={(e) => setProfile({ ...profile, subscribersCount: e.target.value })}
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-400 mb-1">
                Total Vlogs Count
              </label>
              <input
                type="number"
                value={profile.totalVlogsCount}
                onChange={(e) => setProfile({ ...profile, totalVlogsCount: parseInt(e.target.value) || 0 })}
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-400 mb-1">
                Total Shorts Count
              </label>
              <input
                type="number"
                value={profile.totalShortsCount}
                onChange={(e) => setProfile({ ...profile, totalShortsCount: parseInt(e.target.value) || 0 })}
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs"
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            disabled={saving}
            className="flex items-center gap-2 px-6 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-sm shadow-xl shadow-amber-500/20 transition disabled:opacity-50"
          >
            <Save size={18} />
            <span>{saving ? 'Publishing Changes...' : 'Save & Publish Creator Profile'}</span>
          </button>
        </div>
      </form>
    </div>
  );
};
