import React, { useState, useEffect } from 'react';
import {
  Plus,
  Trash2,
  Edit2,
  Sliders,
  Check,
  X,
  ExternalLink
} from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { HomeBanner } from '../types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';

export const BannersManager: React.FC = () => {
  const [banners, setBanners] = useState<HomeBanner[]>([]);
  const [loading, setLoading] = useState(true);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBanner, setEditingBanner] = useState<HomeBanner | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<HomeBanner | null>(null);

  // Form
  const [imageUrl, setImageUrl] = useState('');
  const [title, setTitle] = useState('');
  const [subtitle, setSubtitle] = useState('');
  const [buttonText, setButtonText] = useState('Watch Episode');
  const [destination, setDestination] = useState('hardiklifevlog://vlog/vlog_001');
  const [isActive, setIsActive] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    const b = await FirestoreService.getBanners();
    setBanners(b.sort((a, b) => a.order - b.order));
    setLoading(false);
  };

  const openNew = () => {
    setEditingBanner(null);
    setImageUrl('https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1200&q=80');
    setTitle('');
    setSubtitle('');
    setButtonText('Explore');
    setDestination('hardiklifevlog://vlogs');
    setIsActive(true);
    setIsModalOpen(true);
  };

  const openEdit = (b: HomeBanner) => {
    setEditingBanner(b);
    setImageUrl(b.imageUrl);
    setTitle(b.title);
    setSubtitle(b.subtitle);
    setButtonText(b.buttonText);
    setDestination(b.destination);
    setIsActive(b.isActive);
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    const item: HomeBanner = {
      id: editingBanner ? editingBanner.id : 'banner_' + Date.now(),
      imageUrl,
      title,
      subtitle,
      buttonText,
      destination,
      isActive,
      order: editingBanner ? editingBanner.order : banners.length + 1
    };
    await FirestoreService.saveBanner(item);
    await loadData();
    setSaving(false);
    setIsModalOpen(false);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await FirestoreService.deleteBanner(deleteTarget.id);
    await loadData();
    setDeleteTarget(null);
  };

  const toggleActive = async (banner: HomeBanner) => {
    const updated = { ...banner, isActive: !banner.isActive };
    await FirestoreService.saveBanner(updated);
    await loadData();
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Home Promo Banners</h1>
          <p className="text-xs text-gray-400">Manage sliding promotional cards on the viewer Home screen</p>
        </div>
        <button
          onClick={openNew}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-xs shadow-lg shadow-amber-500/20 transition self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>New Promo Banner</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {banners.map((b) => (
          <div
            key={b.id}
            className="rounded-3xl bg-[#161616] border border-[#262626] overflow-hidden flex flex-col justify-between shadow-xl"
          >
            <div className="relative h-44 bg-black overflow-hidden">
              <img src={b.imageUrl} alt={b.title} className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-black via-black/40 to-transparent" />

              <button
                onClick={() => toggleActive(b)}
                className={`absolute top-3 right-3 px-2.5 py-1 rounded-full text-[10px] font-bold uppercase transition flex items-center gap-1 ${
                  b.isActive
                    ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                    : 'bg-gray-800 text-gray-400'
                }`}
              >
                {b.isActive ? <Check size={12} /> : <X size={12} />}
                {b.isActive ? 'Active' : 'Disabled'}
              </button>

              <div className="absolute bottom-3 left-4 right-4">
                <h3 className="text-lg font-black text-white">{b.title}</h3>
                <p className="text-xs text-gray-300 line-clamp-1">{b.subtitle}</p>
              </div>
            </div>

            <div className="p-4 flex items-center justify-between border-t border-[#262626] text-xs">
              <div className="flex items-center gap-2 text-gray-400 font-mono">
                <span className="bg-[#242424] text-white px-2 py-0.5 rounded text-[11px] font-bold">
                  CTA: {b.buttonText}
                </span>
                <span className="truncate max-w-[150px]">{b.destination}</span>
              </div>

              <div className="flex items-center gap-1">
                <button
                  onClick={() => openEdit(b)}
                  className="p-2 text-gray-400 hover:text-amber-400 rounded-lg hover:bg-[#202020]"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  onClick={() => setDeleteTarget(b)}
                  className="p-2 text-gray-400 hover:text-red-400 rounded-lg hover:bg-[#202020]"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Modal Form */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingBanner ? 'Edit Promo Banner' : 'Create Promo Banner'}
        maxWidth="md"
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Banner Headline *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. New Himalayan Road Trip Series Out!"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Subtitle
            </label>
            <input
              type="text"
              value={subtitle}
              onChange={(e) => setSubtitle(e.target.value)}
              placeholder="e.g. Stream in 4K with Dolby Atmos audio"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Banner Background Image URL *
            </label>
            <input
              type="url"
              required
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Button Label
              </label>
              <input
                type="text"
                value={buttonText}
                onChange={(e) => setButtonText(e.target.value)}
                placeholder="Watch Now"
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Deep Link Destination
              </label>
              <input
                type="text"
                value={destination}
                onChange={(e) => setDestination(e.target.value)}
                placeholder="hardiklifevlog://vlog/..."
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs focus:outline-none"
              />
            </div>
          </div>

          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="banner_active"
              checked={isActive}
              onChange={(e) => setIsActive(e.target.checked)}
              className="w-4 h-4 rounded text-amber-500 bg-[#1E1E1E] border-gray-600"
            />
            <label htmlFor="banner_active" className="text-xs font-semibold text-gray-300 cursor-pointer">
              Active on Mobile Home Screen
            </label>
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-[#2C2C2C]">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-xs font-bold rounded-xl text-gray-300 bg-[#252525]"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="px-5 py-2 text-xs font-extrabold rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 text-black"
            >
              {saving ? 'Saving...' : 'Save Banner'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Promo Banner"
        message="Are you sure you want to remove this banner?"
      />
    </div>
  );
};
