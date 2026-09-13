import React, { useState, useEffect } from 'react';
import { Star, CheckCircle, Video, Film, Image as ImageIcon } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { Vlog, ShortVideo, GalleryPhoto } from '../types';

export const FeaturedManager: React.FC = () => {
  const [vlogs, setVlogs] = useState<Vlog[]>([]);
  const [shorts, setShorts] = useState<ShortVideo[]>([]);
  const [photos, setPhotos] = useState<GalleryPhoto[]>([]);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [v, s, p] = await Promise.all([
      FirestoreService.getVlogs(),
      FirestoreService.getShorts(),
      FirestoreService.getPhotos()
    ]);
    setVlogs(v);
    setShorts(s);
    setPhotos(p);
  };

  const handleSelectFeaturedVlog = async (vlogId: string) => {
    setSaving(true);
    for (const v of vlogs) {
      const isTarget = v.id === vlogId;
      if (v.featured !== isTarget) {
        await FirestoreService.saveVlog({ ...v, featured: isTarget });
      }
    }
    await loadData();
    setSaving(false);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  const currentFeaturedVlog = vlogs.find(v => v.featured) || vlogs[0];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Featured Content Controls</h1>
          <p className="text-xs text-gray-400">
            Control what appears on the Android Home Hero banner without releasing a new APK build
          </p>
        </div>
        {savedSuccess && (
          <div className="px-3.5 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-bold flex items-center gap-2">
            <CheckCircle size={15} />
            Updated Live on Android
          </div>
        )}
      </div>

      {/* Hero Featured Preview Card */}
      {currentFeaturedVlog && (
        <div className="p-6 rounded-3xl bg-gradient-to-r from-amber-500/10 via-[#1E1E1E] to-[#161616] border border-amber-500/30 shadow-2xl">
          <div className="flex items-center gap-2 text-xs font-extrabold text-amber-400 uppercase tracking-wider mb-4">
            <Star size={16} fill="currentColor" /> Currently Pinpointed on Home Hero
          </div>

          <div className="flex flex-col md:flex-row items-center gap-6">
            <div className="w-full md:w-72 aspect-video rounded-2xl overflow-hidden border border-amber-500/30 shrink-0">
              <img
                src={currentFeaturedVlog.thumbnailUrl}
                alt={currentFeaturedVlog.title}
                className="w-full h-full object-cover"
              />
            </div>
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <span className="text-xs bg-amber-500/20 text-amber-300 font-bold px-2 py-0.5 rounded">
                  {currentFeaturedVlog.categoryName}
                </span>
                <span className="text-xs text-gray-400 font-mono">{currentFeaturedVlog.duration}</span>
              </div>
              <h2 className="text-xl font-black text-white">{currentFeaturedVlog.title}</h2>
              <p className="text-xs text-gray-300 leading-relaxed max-w-xl">
                {currentFeaturedVlog.description}
              </p>
              <div className="text-xs text-gray-500 font-mono pt-2">
                Document ID: {currentFeaturedVlog.id} • Views: {currentFeaturedVlog.views.toLocaleString()}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Selectable Vlogs for Hero */}
      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626]">
        <h3 className="text-base font-bold text-white mb-1 flex items-center gap-2">
          <Video size={18} className="text-amber-400" />
          Select Episode for Featured Home Spot
        </h3>
        <p className="text-xs text-gray-400 mb-5">Click "Set as Featured" on any episode below</p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {vlogs.map(v => (
            <div
              key={v.id}
              className={`p-4 rounded-2xl border transition flex flex-col justify-between ${
                v.featured
                  ? 'bg-amber-500/10 border-amber-500/50'
                  : 'bg-[#1E1E1E] border-[#2A2A2A] hover:border-[#383838]'
              }`}
            >
              <div className="flex gap-3 mb-3">
                <img
                  src={v.thumbnailUrl}
                  alt={v.title}
                  className="w-20 h-14 object-cover rounded-xl shrink-0"
                />
                <div>
                  <h4 className="text-xs font-bold text-white line-clamp-2">{v.title}</h4>
                  <span className="text-[10px] text-gray-400">{v.categoryName}</span>
                </div>
              </div>

              <div className="flex items-center justify-between pt-2 border-t border-[#2A2A2A]">
                <span className="text-[11px] text-gray-400 font-mono">{v.duration}</span>
                {v.featured ? (
                  <span className="text-xs font-bold text-amber-400 flex items-center gap-1">
                    <Star size={13} fill="currentColor" /> Active Feature
                  </span>
                ) : (
                  <button
                    onClick={() => handleSelectFeaturedVlog(v.id)}
                    disabled={saving}
                    className="px-3 py-1 bg-[#282828] hover:bg-amber-500 hover:text-black text-gray-300 text-xs font-semibold rounded-lg transition"
                  >
                    Set as Featured
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
