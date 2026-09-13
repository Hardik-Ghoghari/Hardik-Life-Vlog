import React, { useState, useEffect } from 'react';
import {
  Plus,
  Trash2,
  Edit2,
  Image as ImageIcon,
  MapPin,
  Upload,
  CheckCircle,
  Eye
} from 'lucide-react';
import { FirestoreService, defaultCategories } from '../services/firestoreService';
import { GalleryPhoto, ContentCategory } from '../types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';

interface GalleryManagerProps {
  initialOpenUploadModal?: boolean;
}

export const GalleryManager: React.FC<GalleryManagerProps> = ({ initialOpenUploadModal = false }) => {
  const [photos, setPhotos] = useState<GalleryPhoto[]>([]);
  const [categories, setCategories] = useState<ContentCategory[]>([]);
  const [loading, setLoading] = useState(true);

  const [isModalOpen, setIsModalOpen] = useState(initialOpenUploadModal);
  const [editingPhoto, setEditingPhoto] = useState<GalleryPhoto | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<GalleryPhoto | null>(null);
  const [previewPhoto, setPreviewPhoto] = useState<GalleryPhoto | null>(null);

  // Form states
  const [imageUrl, setImageUrl] = useState('');
  const [caption, setCaption] = useState('');
  const [location, setLocation] = useState('');
  const [categoryId, setCategoryId] = useState('cat_travel');
  const [uploadProgress, setUploadProgress] = useState(0);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    const [p, c] = await Promise.all([
      FirestoreService.getPhotos(),
      FirestoreService.getCategories()
    ]);
    setPhotos(p);
    setCategories(c);
    setLoading(false);
  };

  const openNew = () => {
    setEditingPhoto(null);
    setImageUrl('https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1000&q=80');
    setCaption('');
    setLocation('Himachal, India');
    setCategoryId('cat_travel');
    setUploadProgress(0);
    setIsModalOpen(true);
  };

  const openEdit = (photo: GalleryPhoto) => {
    setEditingPhoto(photo);
    setImageUrl(photo.imageUrl);
    setCaption(photo.caption);
    setLocation(photo.location || '');
    setCategoryId(photo.categoryId);
    setUploadProgress(100);
    setIsModalOpen(true);
  };

  const handleSimulatedFileUpload = () => {
    setUploadProgress(10);
    const timer = setInterval(() => {
      setUploadProgress(prev => {
        if (prev >= 100) {
          clearInterval(timer);
          return 100;
        }
        return prev + 25;
      });
    }, 200);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    const item: GalleryPhoto = {
      id: editingPhoto ? editingPhoto.id : 'photo_' + Date.now(),
      imageUrl,
      caption,
      location,
      categoryId,
      publishedAt: editingPhoto ? editingPhoto.publishedAt : 'Just now',
      status: 'published'
    };
    await FirestoreService.savePhoto(item);
    await loadData();
    setSaving(false);
    setIsModalOpen(false);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await FirestoreService.deletePhoto(deleteTarget.id);
    await loadData();
    setDeleteTarget(null);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Photo Moments Gallery</h1>
          <p className="text-xs text-gray-400">High-resolution photography, behind-the-scenes & location stills</p>
        </div>
        <button
          onClick={openNew}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-600 hover:to-teal-600 text-black font-extrabold text-xs shadow-lg shadow-emerald-500/20 transition self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Upload Photos</span>
        </button>
      </div>

      {/* Photos Grid */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {photos.map(photo => (
          <div
            key={photo.id}
            className="rounded-2xl bg-[#161616] border border-[#262626] overflow-hidden hover:border-[#383838] transition flex flex-col group relative shadow-lg"
          >
            <div className="relative aspect-square bg-black overflow-hidden">
              <img
                src={photo.imageUrl}
                alt={photo.caption}
                className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-black/30" />

              {/* Location pin badge */}
              {photo.location && (
                <div className="absolute top-2 left-2 px-2 py-0.5 rounded-md bg-black/60 backdrop-blur text-[10px] font-semibold text-emerald-400 flex items-center gap-1">
                  <MapPin size={11} /> {photo.location}
                </div>
              )}

              {/* Action buttons */}
              <div className="absolute top-2 right-2 flex items-center gap-1">
                <button
                  onClick={() => openEdit(photo)}
                  className="p-1.5 rounded-lg bg-black/60 text-gray-300 hover:text-white"
                >
                  <Edit2 size={13} />
                </button>
                <button
                  onClick={() => setDeleteTarget(photo)}
                  className="p-1.5 rounded-lg bg-black/60 text-gray-300 hover:text-red-400"
                >
                  <Trash2 size={13} />
                </button>
              </div>

              {/* Fullscreen view trigger */}
              <button
                onClick={() => setPreviewPhoto(photo)}
                className="absolute inset-0 m-auto w-10 h-10 rounded-full bg-black/60 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 hover:scale-110 hover:bg-emerald-500 hover:text-black transition"
              >
                <Eye size={18} />
              </button>

              <div className="absolute bottom-2 left-2 right-2">
                <p className="text-xs font-semibold text-white truncate drop-shadow">
                  {photo.caption || 'Captured moment'}
                </p>
                <span className="text-[10px] text-gray-400">{photo.publishedAt}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Upload/Edit Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingPhoto ? 'Edit Photo Details' : 'Upload Photography to Firebase'}
        maxWidth="md"
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Image URL (Direct or Firebase Storage) *
            </label>
            <input
              type="url"
              required
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
              placeholder="https://..."
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
            {imageUrl && (
              <div className="mt-2 h-36 rounded-xl overflow-hidden border border-[#303030]">
                <img src={imageUrl} alt="Preview" className="w-full h-full object-cover" />
              </div>
            )}
          </div>

          <div className="p-3 bg-[#1C1C1C] rounded-xl border border-dashed border-[#333333] text-center">
            <button
              type="button"
              onClick={handleSimulatedFileUpload}
              className="inline-flex items-center gap-2 text-xs text-emerald-400 font-bold hover:underline"
            >
              <Upload size={14} /> Upload via Firebase Storage File Picker
            </button>
            {uploadProgress > 0 && (
              <div className="mt-2 w-full bg-[#262626] rounded-full h-1.5 overflow-hidden">
                <div
                  className="bg-emerald-500 h-full transition-all duration-300"
                  style={{ width: `${uploadProgress}%` }}
                />
              </div>
            )}
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Caption
            </label>
            <input
              type="text"
              value={caption}
              onChange={(e) => setCaption(e.target.value)}
              placeholder="e.g. Misty mountain pass at dawn"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
                Location
              </label>
              <input
                type="text"
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                placeholder="e.g. Manali Valley"
                className="w-full px-3 py-2 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-xs focus:outline-none"
              />
            </div>
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
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-[#2C2C2C]">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-xs font-bold rounded-xl text-gray-300 bg-[#252525] hover:bg-[#2F2F2F]"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="px-5 py-2 text-xs font-extrabold rounded-xl bg-gradient-to-r from-emerald-500 to-teal-500 text-black shadow-lg shadow-emerald-500/20"
            >
              {saving ? 'Saving...' : 'Publish Photo'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Preview Fullscreen */}
      {previewPhoto && (
        <Modal
          isOpen={true}
          onClose={() => setPreviewPhoto(null)}
          title={previewPhoto.caption || 'Photo Viewer'}
          maxWidth="xl"
        >
          <div className="max-h-[75vh] flex flex-col items-center">
            <img
              src={previewPhoto.imageUrl}
              alt={previewPhoto.caption}
              className="max-h-[60vh] object-contain rounded-xl"
            />
            {previewPhoto.location && (
              <p className="mt-3 text-xs text-emerald-400 font-semibold flex items-center gap-1">
                <MapPin size={14} /> {previewPhoto.location}
              </p>
            )}
          </div>
        </Modal>
      )}

      {/* Delete confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Photo"
        message="Are you sure you want to delete this photo from the gallery?"
      />
    </div>
  );
};
