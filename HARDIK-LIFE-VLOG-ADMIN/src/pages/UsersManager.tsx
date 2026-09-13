import React, { useState, useEffect } from 'react';
import { Users, Search, Shield, UserX, UserCheck, Eye, Clock } from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { UserAccount } from '../types';
import { Modal } from '../components/common/Modal';

export const UsersManager: React.FC = () => {
  const [users, setUsers] = useState<UserAccount[]>([]);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedUser, setSelectedUser] = useState<UserAccount | null>(null);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    const u = await FirestoreService.getUsers();
    setUsers(u);
  };

  const handleToggleStatus = async (user: UserAccount) => {
    const newStatus = user.status === 'active' ? 'disabled' : 'active';
    await FirestoreService.updateUserStatus(user.id, newStatus);
    await loadUsers();
    if (selectedUser?.id === user.id) {
      setSelectedUser({ ...selectedUser, status: newStatus });
    }
  };

  const filtered = users.filter(u => {
    const matchQuery = u.name.toLowerCase().includes(search.toLowerCase()) ||
                       u.email.toLowerCase().includes(search.toLowerCase()) ||
                       u.id.toLowerCase().includes(search.toLowerCase());
    const matchRole = roleFilter === 'all' || u.role === roleFilter;
    const matchStatus = statusFilter === 'all' || u.status === statusFilter;
    return matchQuery && matchRole && matchStatus;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">User Accounts Directory</h1>
          <p className="text-xs text-gray-400">View authenticated audience members, manage statuses, and review permissions</p>
        </div>
      </div>

      {/* Filter bar */}
      <div className="p-4 rounded-2xl bg-[#161616] border border-[#262626] flex flex-wrap items-center justify-between gap-4">
        <div className="flex-1 min-w-[240px] relative">
          <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-500" />
          <input
            type="text"
            placeholder="Search by name, email, or user ID..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-white text-xs focus:outline-none focus:border-amber-500"
          />
        </div>

        <div className="flex items-center gap-3">
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="px-3 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-gray-300 text-xs focus:outline-none"
          >
            <option value="all">All Roles</option>
            <option value="admin">Admin</option>
            <option value="user">User / Viewer</option>
            <option value="editor">Editor</option>
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 bg-[#1F1F1F] border border-[#303030] rounded-xl text-gray-300 text-xs focus:outline-none"
          >
            <option value="all">All Statuses</option>
            <option value="active">Active</option>
            <option value="disabled">Disabled</option>
          </select>
        </div>
      </div>

      {/* Users table */}
      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-[#262626] text-gray-400 uppercase tracking-wider font-semibold">
                <th className="pb-3">User</th>
                <th className="pb-3">Role</th>
                <th className="pb-3">Status</th>
                <th className="pb-3">Registered Date</th>
                <th className="pb-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#222222]">
              {filtered.map(user => (
                <tr key={user.id} className="hover:bg-[#1C1C1C] transition">
                  <td className="py-3.5 flex items-center gap-3">
                    <div className="w-9 h-9 rounded-xl bg-[#262626] border border-[#333333] flex items-center justify-center font-bold text-white text-xs">
                      {user.name[0]}
                    </div>
                    <div>
                      <div className="font-bold text-white text-sm">{user.name}</div>
                      <div className="text-[11px] text-gray-400 font-mono">{user.email}</div>
                    </div>
                  </td>
                  <td className="py-3.5">
                    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-extrabold uppercase ${
                      user.role === 'admin'
                        ? 'bg-amber-500/15 text-amber-400 border border-amber-500/30'
                        : 'bg-[#252525] text-gray-300'
                    }`}>
                      {user.role}
                    </span>
                  </td>
                  <td className="py-3.5">
                    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase ${
                      user.status === 'active'
                        ? 'bg-emerald-500/10 text-emerald-400'
                        : 'bg-red-500/10 text-red-400'
                    }`}>
                      {user.status}
                    </span>
                  </td>
                  <td className="py-3.5 text-gray-400 font-mono">
                    {new Date(user.createdAt).toLocaleDateString()}
                  </td>
                  <td className="py-3.5 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => setSelectedUser(user)}
                        className="px-2.5 py-1 rounded-lg bg-[#242424] hover:bg-[#303030] text-gray-300 font-medium"
                      >
                        Details
                      </button>
                      {user.role !== 'admin' && (
                        <button
                          onClick={() => handleToggleStatus(user)}
                          className={`p-1.5 rounded-lg transition ${
                            user.status === 'active'
                              ? 'text-gray-400 hover:text-red-400 hover:bg-red-500/10'
                              : 'text-gray-400 hover:text-emerald-400 hover:bg-emerald-500/10'
                          }`}
                          title={user.status === 'active' ? 'Disable Account' : 'Re-enable Account'}
                        >
                          {user.status === 'active' ? <UserX size={15} /> : <UserCheck size={15} />}
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* User Details Modal */}
      {selectedUser && (
        <Modal
          isOpen={true}
          onClose={() => setSelectedUser(null)}
          title="User Account Details"
          maxWidth="md"
        >
          <div className="space-y-4 text-xs">
            <div className="p-4 rounded-2xl bg-[#1E1E1E] border border-[#2A2A2A] flex items-center gap-4">
              <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-amber-500 to-orange-500 flex items-center justify-center font-black text-black text-xl">
                {selectedUser.name[0]}
              </div>
              <div>
                <h3 className="text-base font-bold text-white">{selectedUser.name}</h3>
                <p className="text-gray-400 font-mono">{selectedUser.email}</p>
                <div className="mt-1 flex items-center gap-2">
                  <span className="px-2 py-0.5 rounded bg-amber-500/10 text-amber-400 font-bold uppercase text-[10px]">
                    {selectedUser.role}
                  </span>
                  <span className={`px-2 py-0.5 rounded font-bold uppercase text-[10px] ${
                    selectedUser.status === 'active' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'
                  }`}>
                    {selectedUser.status}
                  </span>
                </div>
              </div>
            </div>

            <div className="space-y-2 p-4 rounded-xl bg-[#1A1A1A] border border-[#262626]">
              <div className="flex justify-between py-1 border-b border-[#242424]">
                <span className="text-gray-400">User Document ID</span>
                <span className="font-mono text-white">{selectedUser.id}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-[#242424]">
                <span className="text-gray-400">Registration Date</span>
                <span className="text-white">{new Date(selectedUser.createdAt).toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-gray-400">Last Session</span>
                <span className="text-white">{selectedUser.lastLogin ? new Date(selectedUser.lastLogin).toLocaleString() : 'Recently'}</span>
              </div>
            </div>

            <div className="pt-2 flex justify-end gap-3">
              {selectedUser.role !== 'admin' && (
                <button
                  type="button"
                  onClick={() => handleToggleStatus(selectedUser)}
                  className={`px-4 py-2 rounded-xl font-bold transition ${
                    selectedUser.status === 'active'
                      ? 'bg-red-600/20 text-red-400 border border-red-600/30 hover:bg-red-600/30'
                      : 'bg-emerald-600/20 text-emerald-400 border border-emerald-600/30 hover:bg-emerald-600/30'
                  }`}
                >
                  {selectedUser.status === 'active' ? 'Suspend Account' : 'Reactivate Account'}
                </button>
              )}
              <button
                type="button"
                onClick={() => setSelectedUser(null)}
                className="px-4 py-2 rounded-xl bg-[#262626] text-white font-bold"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
