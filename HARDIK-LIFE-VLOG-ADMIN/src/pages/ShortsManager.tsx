import React, { useState, useEffect } from 'react';
import {
  Plus,
  Search,
  Trash2,
  Edit2,
  Film,
  Play,
  Eye,
  Heart
} from 'lucide-react';
import { FirestoreService, defaultCategories } from '../services/firestoreService';
import { ShortVideo, ContentCategory } from '../types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';

interface ShortsManagerProps {
  initialOpenNewModal?: boolean;
}

export const ShortsManager: React.FC<ShortsManagerProps> = ({ initialOpenNewModal = false }) => {
  const [shorts, setShorts] = useState<ShortVideo[]>([]);
  const [categories, setCategories] = useState<ContentCategory[]>([]);
  const [loading, setLoading] = useState(true);

  const [searchQuery, setSearchQuery] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(initialOpenNewModal);
  const [editingShort, setEditingShort] = useState<ShortVideo[] | any>(null);
  const [deleteTarget, setDeleteTarget] = useState<ShortVideo | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  // Form fields
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [videoUrl, setVideoUrl] = useState('');
  const [thumbnailUrl, setThumbnailUrl] = useState('');
  const [categoryId, setCategoryId] = useState('cat_travel');
  const [status, setStatus] = useState<'published' | 'draft' | 'unpublished'>('published');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    const [s, c] = await Promise.all([
      FirestoreService.getShorts(),
      FirestoreService.getCategories()
    ]);
    setShorts(s);
    setCategories(c);
    setLoading(false);
  };

  const openNew = () => {
    setEditingShort(null);
    setTitle('');
    setDescription('');
    setVideoUrl('https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4');
    setThumbnailUrl('https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&q=80');
    setCategoryId('cat_travel');
    setStatus('published');
    setIsFormOpen(true);
  };

  const openEdit = (short: ShortVideo) => {
    setEditingShort(short);
    setTitle(short.title);
    setDescription(short.description);
    setVideoUrl(short.videoUrl);
    setThumbnailUrl(short.thumbnailUrl);
    setCategoryId(short.categoryId);
    setStatus(short.status);
    setIsFormOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    const item: ShortVideo = {
      id: editingShort ? editingShort.id : 'short_' + Date.now(),
      title,
      description,
      videoUrl,
      thumbnailUrl,
      categoryId,
      views: editingShort ? editingShort.views : 0,
      likes: editingShort ? editingShort.likes : 0,
      status,
      publishedAt: editingShort ? editingShort.publishedAt : 'Just now'
    };
    await FirestoreService.saveShort(item);
    await loadData();
    setSaving(false);
    setIsFormOpen(false);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await FirestoreService.deleteShort(deleteTarget.id);
    await loadData();
    setDeleteTarget(null);
  };

  const filtered = shorts.filter(s =>
    s.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    s.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Vertical Shorts Reel</h1>
          <p className="text-xs text-gray-400">Manage 9:16 vertical short stories with auto-loop playback</p>
        </div>
        <button
          onClick={openNew}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white font-extrabold text-xs shadow-lg shadow-purple-500/20 transition self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Upload Short Reel</span>
        </button>
      </div>

      {/* Search */}
      <div className="p-4 rounded-2xl bg-[#161616] border border-[#262626] max-w-md relative">
        <Search size={16} className="absolute left-7 top-1/2 -translate-y-1/2 text-gray-500" />
        <input
          type="text"
          placeholder="Search shorts..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-full pl-9 pr-4 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-white text-xs focus:outline-none focus:border-purple-500"
        />
      </div>

      {/* Grid of Shorts Cards (Vertical 9:16 Aspect) */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {filtered.map(short => (
          <div
            key={short.id}
            className="rounded-2xl bg-[#161616] border border-[#262626] overflow-hidden hover:border-[#383838] transition flex flex-col group relative shadow-lg"
          >
            {/* 9:16 vertical thumbnail */}
            <div className="relative aspect-[9/16] bg-black overflow-hidden">
              <img
                src={short.thumbnailUrl}
                alt={short.title}
                className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-black/30" />

              <span className={`absolute top-2 left-2 px-2 py-0.5 rounded-md text-[9px] font-bold uppercase ${
                short.status === 'published' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' : 'bg-yellow-500/20 text-yellow-400'
              }`}>
                {short.status}
              </span>

              {/* Action buttons */}
              <div className="absolute top-2 right-2 flex items-center gap-1">
                <button
                  onClick={() => openEdit(short)}
                  className="p-1.5 rounded-lg bg-black/60 text-gray-300 hover:text-white"
                >
                  <Edit2 size={13} />
                </button>
                <button
                  onClick={() => setDeleteTarget(short)}
                  className="p-1.5 rounded-lg bg-black/60 text-gray-300 hover:text-red-400"
                >
                  <Trash2 size={13} />
                </button>
              </div>

              {/* Play preview */}
              <button
                onClick={() => setPreviewUrl(short.videoUrl)}
                className="absolute inset-0 m-auto w-10 h-10 rounded-full bg-black/60 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 hover:scale-110 hover:bg-purple-500 transition"
              >
                <Play size={18} fill="currentColor" className="ml-0.5" />
              </button>

              {/* Info text */}
              <div className="absolute bottom-2 left-2 right-2">
                <h4 className="text-xs font-bold text-white line-clamp-2 leading-tight drop-shadow">
                  {short.title}
                </h4>
                <div className="flex items-center justify-between text-[10px] text-gray-300 mt-1 font-medium">
                  <span className="flex items-center gap-1">
                    <Eye size={11} /> {short.views.toLocaleString()}
                  </span>
                  <span className="flex items-center gap-1 text-rose-400">
                    <Heart size={11} fill="currentColor" /> {short.likes.toLocaleString()}
                  </span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Form Modal */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        title={editingShort ? 'Edit Short Video' : 'Upload Vertical Short Video'}
        maxWidth="md"
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Title *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Sunset Drone Chase 🌅"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none focus:border-purple-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Description / Tags
            </label>
            <textarea
              rows={2}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="#vlog #travel #hardik"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Vertical Video URL (.mp4) *
            </label>
            <input
              type="url"
              required
              value={videoUrl}
              onChange={(e) => setVideoUrl(e.target.value)}
              placeholder="https://..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Thumbnail Poster URL *
            </label>
            <input
              type="url"
              required
              value={thumbnailUrl}
              onChange={(e) => setThumbnailUrl(e.target.value)}
              placeholder="https://..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Category
              </label>
              <select
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs focus:outline-none"
              >
                {categories.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Status
              </label>
              <select
                value={status}
                onChange={(e: any) => setStatus(e.target.value)}
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs focus:outline-none"
              >
                <option value="published">Published</option>
                <option value="draft">Draft</option>
                <option value="unpublished">Unpublished</option>
              </select>
            </div>
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-[#2C2C2C]">
            <button
              type="button"
              onClick={() => setIsFormOpen(false)}
              className="px-4 py-2 text-xs font-bold rounded-xl text-gray-300 bg-[#252525] hover:bg-[#2F2F2F]"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="px-5 py-2 text-xs font-extrabold rounded-xl bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-lg shadow-purple-500/20"
            >
              {saving ? 'Saving...' : 'Save Short'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Video Preview Modal */}
      {previewUrl && (
        <Modal
          isOpen={true}
          onClose={() => setPreviewUrl(null)}
          title="Shorts Reel Preview"
          maxWidth="sm"
        >
          <div className="aspect-[9/16] max-h-[70vh] bg-black rounded-xl overflow-hidden mx-auto">
            <video
              src={previewUrl}
              controls
              autoPlay
              loop
              className="w-full h-full object-cover"
            />
          </div>
        </Modal>
      )}

      {/* Delete Dialog */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Short Video"
        message={`Are you sure you want to delete "${deleteTarget?.title}"?`}
      />
    </div>
  );
};
