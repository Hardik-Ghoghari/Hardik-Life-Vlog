import React, { useState, useEffect } from 'react';
import {
  Plus,
  Trash2,
  Edit2,
  FolderTree,
  Check,
  X,
  ArrowUp,
  ArrowDown
} from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { ContentCategory } from '../types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';

export const CategoriesManager: React.FC = () => {
  const [categories, setCategories] = useState<ContentCategory[]>([]);
  const [loading, setLoading] = useState(true);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<ContentCategory | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<ContentCategory | null>(null);

  // Form states
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [iconName, setIconName] = useState('Compass');
  const [isActive, setIsActive] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    const c = await FirestoreService.getCategories();
    setCategories(c.sort((a, b) => a.order - b.order));
    setLoading(false);
  };

  const openNew = () => {
    setEditingCategory(null);
    setName('');
    setDescription('');
    setIconName('Compass');
    setIsActive(true);
    setIsModalOpen(true);
  };

  const openEdit = (cat: ContentCategory) => {
    setEditingCategory(cat);
    setName(cat.name);
    setDescription(cat.description || '');
    setIconName(cat.iconName || 'Compass');
    setIsActive(cat.isActive);
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    const item: ContentCategory = {
      id: editingCategory ? editingCategory.id : 'cat_' + name.toLowerCase().replace(/[^a-z0-9]/g, '_'),
      name,
      description,
      iconName,
      isActive,
      order: editingCategory ? editingCategory.order : categories.length + 1
    };
    await FirestoreService.saveCategory(item);
    await loadData();
    setSaving(false);
    setIsModalOpen(false);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await FirestoreService.deleteCategory(deleteTarget.id);
    await loadData();
    setDeleteTarget(null);
  };

  const toggleActive = async (cat: ContentCategory) => {
    const updated = { ...cat, isActive: !cat.isActive };
    await FirestoreService.saveCategory(updated);
    await loadData();
  };

  const moveOrder = async (index: number, direction: 'up' | 'down') => {
    if (direction === 'up' && index === 0) return;
    if (direction === 'down' && index === categories.length - 1) return;
    const targetIdx = direction === 'up' ? index - 1 : index + 1;
    const list = [...categories];
    const temp = list[index];
    list[index] = list[targetIdx];
    list[targetIdx] = temp;

    // reindex order
    for (let i = 0; i < list.length; i++) {
      list[i].order = i + 1;
      await FirestoreService.saveCategory(list[i]);
    }
    await loadData();
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Content Categories</h1>
          <p className="text-xs text-gray-400">Categories automatically populate Android category carousels & filter tabs</p>
        </div>
        <button
          onClick={openNew}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-xs shadow-lg shadow-amber-500/20 transition self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>New Category</span>
        </button>
      </div>

      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-[#262626] text-gray-400 uppercase tracking-wider">
                <th className="pb-3 w-16">Order</th>
                <th className="pb-3">Category</th>
                <th className="pb-3">Description</th>
                <th className="pb-3">Status</th>
                <th className="pb-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#222222]">
              {categories.map((cat, idx) => (
                <tr key={cat.id} className="hover:bg-[#1C1C1C] transition">
                  <td className="py-3.5">
                    <div className="flex items-center gap-1">
                      <button
                        onClick={() => moveOrder(idx, 'up')}
                        disabled={idx === 0}
                        className="p-1 rounded text-gray-400 hover:text-white disabled:opacity-20"
                      >
                        <ArrowUp size={13} />
                      </button>
                      <button
                        onClick={() => moveOrder(idx, 'down')}
                        disabled={idx === categories.length - 1}
                        className="p-1 rounded text-gray-400 hover:text-white disabled:opacity-20"
                      >
                        <ArrowDown size={13} />
                      </button>
                    </div>
                  </td>
                  <td className="py-3.5">
                    <div className="font-bold text-white text-sm flex items-center gap-2">
                      <span className="w-7 h-7 rounded-lg bg-[#242424] flex items-center justify-center text-amber-400 text-xs">
                        {cat.name[0]}
                      </span>
                      <span>{cat.name}</span>
                    </div>
                  </td>
                  <td className="py-3.5 text-gray-400 max-w-xs truncate">
                    {cat.description || '—'}
                  </td>
                  <td className="py-3.5">
                    <button
                      onClick={() => toggleActive(cat)}
                      className={`px-2.5 py-1 rounded-full text-[10px] font-bold uppercase transition flex items-center gap-1 ${
                        cat.isActive
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                          : 'bg-gray-700/30 text-gray-400 border border-gray-700'
                      }`}
                    >
                      {cat.isActive ? <Check size={12} /> : <X size={12} />}
                      {cat.isActive ? 'Active' : 'Inactive'}
                    </button>
                  </td>
                  <td className="py-3.5 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => openEdit(cat)}
                        className="p-1.5 text-gray-400 hover:text-amber-400 rounded-lg hover:bg-[#222222]"
                      >
                        <Edit2 size={15} />
                      </button>
                      <button
                        onClick={() => setDeleteTarget(cat)}
                        className="p-1.5 text-gray-400 hover:text-red-400 rounded-lg hover:bg-[#222222]"
                      >
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCategory ? 'Edit Category' : 'Create Category'}
        maxWidth="md"
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Category Name *
            </label>
            <input
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. Travel, Daily Life, Food"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Description
            </label>
            <textarea
              rows={2}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Short category summary"
              className="w-full px-3.5 py-2.5 bg-[#1E1E1E] border border-[#303030] rounded-xl text-white text-sm focus:outline-none"
            />
          </div>

          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="cat_active"
              checked={isActive}
              onChange={(e) => setIsActive(e.target.checked)}
              className="w-4 h-4 rounded text-amber-500 bg-[#1E1E1E] border-gray-600 focus:ring-amber-500"
            />
            <label htmlFor="cat_active" className="text-xs font-semibold text-gray-300 cursor-pointer">
              Active & Visible in Android App
            </label>
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
              className="px-5 py-2 text-xs font-extrabold rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 text-black shadow-lg shadow-amber-500/20"
            >
              {saving ? 'Saving...' : 'Save Category'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Category"
        message={`Are you sure you want to delete category "${deleteTarget?.name}"? Existing vlogs under this category will remain intact.`}
      />
    </div>
  );
};
