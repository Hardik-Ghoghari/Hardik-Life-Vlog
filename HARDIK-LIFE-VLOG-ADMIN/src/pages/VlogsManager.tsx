import React, { useState, useEffect } from 'react';
import {
  Plus,
  Search,
  Filter,
  Trash2,
  Edit2,
  Star,
  ExternalLink,
  Eye,
  Heart,
  Clock,
  Play,
  CheckCircle,
  AlertTriangle
} from 'lucide-react';
import { FirestoreService, defaultCategories } from '../services/firestoreService';
import { Vlog, ContentCategory } from '../types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';

interface VlogsManagerProps {
  initialOpenNewModal?: boolean;
}

export const VlogsManager: React.FC<VlogsManagerProps> = ({ initialOpenNewModal = false }) => {
  const [vlogs, setVlogs] = useState<Vlog[]>([]);
  const [categories, setCategories] = useState<ContentCategory[]>([]);
  const [loading, setLoading] = useState(true);

  // Search, Filter & Sort
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [sortBy, setSortBy] = useState<'date' | 'views' | 'likes'>('date');

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(initialOpenNewModal);
  const [editingVlog, setEditingVlog] = useState<Vlog | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Vlog | null>(null);
  const [previewVideoUrl, setPreviewVideoUrl] = useState<string | null>(null);

  // Form states
  const [formTitle, setFormTitle] = useState('');
  const [formDescription, setFormDescription] = useState('');
  const [formThumbnailUrl, setFormThumbnailUrl] = useState('');
  const [formVideoUrl, setFormVideoUrl] = useState('');
  const [formYoutubeId, setFormYoutubeId] = useState('');
  const [formCategoryId, setFormCategoryId] = useState('cat_travel');
  const [formDuration, setFormDuration] = useState('15:00');
  const [formFeatured, setFormFeatured] = useState(false);
  const [formStatus, setFormStatus] = useState<'published' | 'draft' | 'unpublished'>('published');
  const [formPublishedAt, setFormPublishedAt] = useState('Today');
  const [formSaving, setFormSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    const [v, c] = await Promise.all([
      FirestoreService.getVlogs(),
      FirestoreService.getCategories()
    ]);
    setVlogs(v);
    setCategories(c);
    setLoading(false);
  };

  const openNewForm = () => {
    setEditingVlog(null);
    setFormTitle('');
    setFormDescription('');
    setFormThumbnailUrl('https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&q=80');
    setFormVideoUrl('https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4');
    setFormYoutubeId('');
    setFormCategoryId(categories[0]?.id || 'cat_travel');
    setFormDuration('14:30');
    setFormFeatured(false);
    setFormStatus('published');
    setFormPublishedAt('Just now');
    setIsFormOpen(true);
  };

  const openEditForm = (vlog: Vlog) => {
    setEditingVlog(vlog);
    setFormTitle(vlog.title);
    setFormDescription(vlog.description);
    setFormThumbnailUrl(vlog.thumbnailUrl);
    setFormVideoUrl(vlog.videoUrl);
    setFormYoutubeId(vlog.youtubeId || '');
    setFormCategoryId(vlog.categoryId);
    setFormDuration(vlog.duration);
    setFormFeatured(vlog.featured);
    setFormStatus(vlog.status);
    setFormPublishedAt(vlog.publishedAt);
    setIsFormOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormSaving(true);

    const catObj = categories.find(c => c.id === formCategoryId);
    const categoryName = catObj ? catObj.name : 'Vlogs';

    const vlogToSave: Vlog = {
      id: editingVlog ? editingVlog.id : 'vlog_' + Date.now(),
      title: formTitle,
      description: formDescription,
      thumbnailUrl: formThumbnailUrl,
      videoUrl: formVideoUrl,
      youtubeId: formYoutubeId || undefined,
      categoryId: formCategoryId,
      categoryName,
      publishedAt: formPublishedAt,
      duration: formDuration,
      views: editingVlog ? editingVlog.views : 0,
      likes: editingVlog ? editingVlog.likes : 0,
      featured: formFeatured,
      status: formStatus
    };

    await FirestoreService.saveVlog(vlogToSave);
    await loadData();
    setFormSaving(false);
    setIsFormOpen(false);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await FirestoreService.deleteVlog(deleteTarget.id);
    await loadData();
    setDeleteTarget(null);
  };

  const handleToggleFeatured = async (vlog: Vlog) => {
    const updated: Vlog = { ...vlog, featured: !vlog.featured };
    await FirestoreService.saveVlog(updated);
    await loadData();
  };

  const handleToggleStatus = async (vlog: Vlog) => {
    const newStatus = vlog.status === 'published' ? 'unpublished' : 'published';
    const updated: Vlog = { ...vlog, status: newStatus };
    await FirestoreService.saveVlog(updated);
    await loadData();
  };

  // Filter & sort
  const filteredVlogs = vlogs.filter(v => {
    const matchSearch = v.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                        v.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchCat = selectedCategory === 'all' || v.categoryId === selectedCategory;
    return matchSearch && matchCat;
  }).sort((a, b) => {
    if (sortBy === 'views') return b.views - a.views;
    if (sortBy === 'likes') return b.likes - a.likes;
    return (b.createdAt || 0) - (a.createdAt || 0);
  });

  return (
    <div className="space-y-6">
      {/* Header controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Vlog Episodes</h1>
          <p className="text-xs text-gray-400">Add, edit, feature, and broadcast high-definition vlogs</p>
        </div>
        <button
          onClick={openNewForm}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-xs shadow-lg shadow-amber-500/20 transition self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Add New Vlog</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="p-4 rounded-2xl bg-[#161616] border border-[#262626] flex flex-wrap items-center justify-between gap-4">
        <div className="flex-1 min-w-[240px] relative">
          <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-500" />
          <input
            type="text"
            placeholder="Search by title or description..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-white text-xs focus:outline-none focus:border-amber-500"
          />
        </div>

        <div className="flex items-center gap-3">
          {/* Category Filter */}
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-3 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-gray-300 text-xs focus:outline-none"
          >
            <option value="all">All Categories</option>
            {categories.map(c => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>

          {/* Sort By */}
          <select
            value={sortBy}
            onChange={(e: any) => setSortBy(e.target.value)}
            className="px-3 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-gray-300 text-xs focus:outline-none"
          >
            <option value="date">Sort: Recent</option>
            <option value="views">Sort: Most Viewed</option>
            <option value="likes">Sort: Most Liked</option>
          </select>
        </div>
      </div>

      {/* Vlogs List */}
      {loading ? (
        <div className="p-12 text-center text-gray-500 text-sm">Loading episodes from Firebase...</div>
      ) : filteredVlogs.length === 0 ? (
        <div className="p-12 text-center rounded-3xl bg-[#161616] border border-[#262626]">
          <p className="text-gray-400 text-sm">No vlogs found matching your search criteria.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredVlogs.map(vlog => (
            <div
              key={vlog.id}
              className="rounded-2xl bg-[#161616] border border-[#262626] overflow-hidden hover:border-[#383838] transition flex flex-col group shadow-lg"
            >
              {/* Thumbnail with overlay duration & preview button */}
              <div className="relative aspect-video bg-black overflow-hidden">
                <img
                  src={vlog.thumbnailUrl}
                  alt={vlog.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-black/30" />

                {/* Duration badge */}
                <div className="absolute bottom-2.5 right-2.5 px-2 py-0.5 rounded bg-black/80 backdrop-blur text-white text-[11px] font-mono font-bold flex items-center gap-1">
                  <Clock size={12} />
                  {vlog.duration}
                </div>

                {/* Featured Star toggle */}
                <button
                  onClick={() => handleToggleFeatured(vlog)}
                  className={`absolute top-2.5 left-2.5 p-1.5 rounded-lg backdrop-blur transition ${
                    vlog.featured
                      ? 'bg-amber-500 text-black shadow-lg shadow-amber-500/30'
                      : 'bg-black/60 text-gray-400 hover:text-white'
                  }`}
                  title={vlog.featured ? 'Featured on Home' : 'Mark as Featured'}
                >
                  <Star size={15} fill={vlog.featured ? 'currentColor' : 'none'} />
                </button>

                {/* Status badge */}
                <button
                  onClick={() => handleToggleStatus(vlog)}
                  className={`absolute top-2.5 right-2.5 px-2 py-0.5 rounded-md text-[10px] font-bold uppercase transition ${
                    vlog.status === 'published'
                      ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40'
                      : 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/40'
                  }`}
                >
                  {vlog.status}
                </button>

                {/* Play Preview Overlay button */}
                <button
                  onClick={() => setPreviewVideoUrl(vlog.videoUrl)}
                  className="absolute inset-0 m-auto w-12 h-12 rounded-full bg-black/60 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 hover:scale-110 hover:bg-amber-500 hover:text-black transition"
                >
                  <Play size={22} fill="currentColor" className="ml-1" />
                </button>
              </div>

              {/* Content info */}
              <div className="p-4 flex-1 flex flex-col justify-between">
                <div>
                  <div className="flex items-center gap-2 mb-1.5">
                    <span className="text-[11px] font-bold text-amber-400 bg-amber-400/10 px-2 py-0.5 rounded-md">
                      {vlog.categoryName}
                    </span>
                    <span className="text-[11px] text-gray-500">{vlog.publishedAt}</span>
                  </div>
                  <h3 className="text-sm font-bold text-white line-clamp-2 leading-snug">
                    {vlog.title}
                  </h3>
                  <p className="text-xs text-gray-400 line-clamp-2 mt-1.5 leading-relaxed">
                    {vlog.description}
                  </p>
                </div>

                {/* Footer stats & actions */}
                <div className="mt-4 pt-3 border-t border-[#262626] flex items-center justify-between">
                  <div className="flex items-center gap-3 text-xs text-gray-400 font-medium">
                    <span className="flex items-center gap-1">
                      <Eye size={13} className="text-gray-500" />
                      {vlog.views.toLocaleString()}
                    </span>
                    <span className="flex items-center gap-1">
                      <Heart size={13} className="text-rose-500" />
                      {vlog.likes.toLocaleString()}
                    </span>
                  </div>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => openEditForm(vlog)}
                      className="p-1.5 text-gray-400 hover:text-amber-400 rounded-lg hover:bg-[#222222] transition"
                      title="Edit Vlog"
                    >
                      <Edit2 size={16} />
                    </button>
                    <button
                      onClick={() => setDeleteTarget(vlog)}
                      className="p-1.5 text-gray-400 hover:text-red-400 rounded-lg hover:bg-[#222222] transition"
                      title="Delete Vlog"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add / Edit Vlog Modal Form */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        title={editingVlog ? 'Edit Vlog Episode' : 'Publish New Vlog Episode'}
        maxWidth="2xl"
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Title *
            </label>
            <input
              type="text"
              required
              value={formTitle}
              onChange={(e) => setFormTitle(e.target.value)}
              placeholder="e.g. 24 Hours in the Ancient City | Special Vlog"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Description *
            </label>
            <textarea
              required
              rows={3}
              value={formDescription}
              onChange={(e) => setFormDescription(e.target.value)}
              placeholder="Tell your viewers what this episode is about..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-amber-500"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Category *
              </label>
              <select
                value={formCategoryId}
                onChange={(e) => setFormCategoryId(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              >
                {categories.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Duration (MM:SS) *
              </label>
              <input
                type="text"
                required
                value={formDuration}
                onChange={(e) => setFormDuration(e.target.value)}
                placeholder="15:30"
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Thumbnail URL (or Firebase Storage URL) *
            </label>
            <input
              type="url"
              required
              value={formThumbnailUrl}
              onChange={(e) => setFormThumbnailUrl(e.target.value)}
              placeholder="https://..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
            {formThumbnailUrl && (
              <div className="mt-2 h-24 w-40 rounded-lg overflow-hidden border border-[#333333]">
                <img src={formThumbnailUrl} alt="Thumbnail preview" className="w-full h-full object-cover" />
              </div>
            )}
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Direct MP4 / HLS Video URL *
            </label>
            <input
              type="url"
              required
              value={formVideoUrl}
              onChange={(e) => setFormVideoUrl(e.target.value)}
              placeholder="https://commondatastorage.googleapis.com/..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Optional YouTube ID (for Open YouTube button)
              </label>
              <input
                type="text"
                value={formYoutubeId}
                onChange={(e) => setFormYoutubeId(e.target.value)}
                placeholder="e.g. dQw4w9WgXcQ"
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Publish Status
              </label>
              <select
                value={formStatus}
                onChange={(e: any) => setFormStatus(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
              >
                <option value="published">Published</option>
                <option value="draft">Draft</option>
                <option value="unpublished">Unpublished</option>
              </select>
            </div>
          </div>

          <div className="flex items-center gap-3 pt-2">
            <input
              type="checkbox"
              id="featured_check"
              checked={formFeatured}
              onChange={(e) => setFormFeatured(e.target.checked)}
              className="w-4 h-4 rounded text-amber-500 bg-[#1E1E1E] border-gray-600 focus:ring-amber-500"
            />
            <label htmlFor="featured_check" className="text-xs font-semibold text-gray-300 cursor-pointer">
              Pin as Featured Vlog on Android Home Screen
            </label>
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-[#2C2C2C]">
            <button
              type="button"
              onClick={() => setIsFormOpen(false)}
              className="px-4 py-2 text-xs font-bold rounded-xl text-gray-300 bg-[#252525] hover:bg-[#2F2F2F] transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={formSaving}
              className="px-5 py-2 text-xs font-extrabold rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black shadow-lg shadow-amber-500/20 transition disabled:opacity-50"
            >
              {formSaving ? 'Saving to Firebase...' : editingVlog ? 'Update Vlog' : 'Publish Vlog'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Video Preview Modal */}
      {previewVideoUrl && (
        <Modal
          isOpen={true}
          onClose={() => setPreviewVideoUrl(null)}
          title="Video Playback Preview"
          maxWidth="xl"
        >
          <div className="aspect-video bg-black rounded-xl overflow-hidden">
            <video
              src={previewVideoUrl}
              controls
              autoPlay
              className="w-full h-full object-contain"
            />
          </div>
        </Modal>
      )}

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Vlog Episode"
        message={`Are you sure you want to permanently delete "${deleteTarget?.title}"? This action cannot be undone and will immediately unpublish it from all viewer apps.`}
        confirmText="Delete Vlog"
      />
    </div>
  );
};
