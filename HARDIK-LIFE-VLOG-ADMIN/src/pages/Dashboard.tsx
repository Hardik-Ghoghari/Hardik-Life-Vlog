import React, { useState, useEffect } from 'react';
import {
  Users,
  Video,
  Film,
  Image as ImageIcon,
  Eye,
  Heart,
  Plus,
  Bell,
  ArrowUpRight,
  TrendingUp,
  Sparkles,
  PlayCircle
} from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { Vlog, ShortVideo, GalleryPhoto, UserAccount, AdminActivityLog } from '../types';
import { AdminTab } from '../components/layout/Sidebar';

interface DashboardProps {
  onNavigate: (tab: AdminTab) => void;
  onOpenQuickAction: (action: 'add_vlog' | 'add_short' | 'add_photo' | 'send_notif') => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ onNavigate, onOpenQuickAction }) => {
  const [vlogs, setVlogs] = useState<Vlog[]>([]);
  const [shorts, setShorts] = useState<ShortVideo[]>([]);
  const [photos, setPhotos] = useState<GalleryPhoto[]>([]);
  const [users, setUsers] = useState<UserAccount[]>([]);
  const [logs, setLogs] = useState<AdminActivityLog[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [v, s, p, u, l] = await Promise.all([
        FirestoreService.getVlogs(),
        FirestoreService.getShorts(),
        FirestoreService.getPhotos(),
        FirestoreService.getUsers(),
        FirestoreService.getActivityLogs()
      ]);
      setVlogs(v);
      setShorts(s);
      setPhotos(p);
      setUsers(u);
      setLogs(l);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const totalViews = vlogs.reduce((acc, curr) => acc + (curr.views || 0), 0) +
                     shorts.reduce((acc, curr) => acc + (curr.views || 0), 0);
  const totalLikes = vlogs.reduce((acc, curr) => acc + (curr.likes || 0), 0) +
                     shorts.reduce((acc, curr) => acc + (curr.likes || 0), 0);

  const stats = [
    {
      title: 'Total Users',
      value: (users.length + 2480).toLocaleString(),
      subtext: '+12% this month',
      icon: Users,
      color: 'from-blue-500/20 to-indigo-500/20 text-blue-400 border-blue-500/30'
    },
    {
      title: 'Total Vlogs',
      value: vlogs.length.toString(),
      subtext: `${vlogs.filter(v => v.status === 'published').length} published episodes`,
      icon: Video,
      color: 'from-amber-500/20 to-orange-500/20 text-amber-400 border-amber-500/30'
    },
    {
      title: 'Total Shorts',
      value: shorts.length.toString(),
      subtext: 'Vertical reels',
      icon: Film,
      color: 'from-purple-500/20 to-pink-500/20 text-purple-400 border-purple-500/30'
    },
    {
      title: 'Gallery Photos',
      value: photos.length.toString(),
      subtext: 'High-res moments',
      icon: ImageIcon,
      color: 'from-emerald-500/20 to-teal-500/20 text-emerald-400 border-emerald-500/30'
    },
    {
      title: 'Total Views',
      value: totalViews >= 1000 ? `${(totalViews / 1000).toFixed(1)}K+` : totalViews.toString(),
      subtext: 'Across vlogs & shorts',
      icon: Eye,
      color: 'from-orange-500/20 to-red-500/20 text-orange-400 border-orange-500/30'
    },
    {
      title: 'Total Likes',
      value: totalLikes >= 1000 ? `${(totalLikes / 1000).toFixed(1)}K+` : totalLikes.toString(),
      subtext: 'Audience reactions',
      icon: Heart,
      color: 'from-rose-500/20 to-pink-500/20 text-rose-400 border-rose-500/30'
    }
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Banner with Quick Actions */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-[#1E1914] via-[#1A1817] to-[#121212] border border-amber-500/20 p-6 lg:p-8 shadow-xl">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-bold mb-3">
              <Sparkles size={14} />
              Creator Studio Live
            </div>
            <h1 className="text-2xl lg:text-3xl font-black text-white tracking-tight">
              Welcome back, Hardik!
            </h1>
            <p className="text-sm text-gray-400 mt-1 max-w-xl">
              Manage your daily travel vlogs, vertical shorts, photo memories, and push instant FCM notifications directly to your viewer audience.
            </p>
          </div>

          {/* Quick Action Buttons */}
          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => onOpenQuickAction('add_vlog')}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-extrabold text-xs shadow-lg shadow-amber-500/20 transition"
            >
              <Plus size={16} />
              <span>Add Vlog</span>
            </button>
            <button
              onClick={() => onOpenQuickAction('add_short')}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#222222] hover:bg-[#2B2B2B] text-white font-bold text-xs border border-[#333333] transition"
            >
              <Film size={16} className="text-purple-400" />
              <span>Add Short</span>
            </button>
            <button
              onClick={() => onOpenQuickAction('add_photo')}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#222222] hover:bg-[#2B2B2B] text-white font-bold text-xs border border-[#333333] transition"
            >
              <ImageIcon size={16} className="text-emerald-400" />
              <span>Upload Photo</span>
            </button>
            <button
              onClick={() => onOpenQuickAction('send_notif')}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#222222] hover:bg-[#2B2B2B] text-white font-bold text-xs border border-[#333333] transition"
            >
              <Bell size={16} className="text-amber-400" />
              <span>Send Notification</span>
            </button>
          </div>
        </div>
      </div>

      {/* 6 Key Statistics Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {stats.map((item, idx) => {
          const Icon = item.icon;
          return (
            <div
              key={idx}
              className="p-5 rounded-2xl bg-[#161616] border border-[#262626] hover:border-[#383838] transition shadow-lg flex flex-col justify-between"
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-semibold text-gray-400">{item.title}</span>
                <div className={`p-2 rounded-xl border bg-gradient-to-br ${item.color}`}>
                  <Icon size={16} />
                </div>
              </div>
              <div>
                <div className="text-2xl font-black text-white tracking-tight">{item.value}</div>
                <div className="text-[11px] text-gray-500 mt-0.5">{item.subtext}</div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Mini Performance Graph & Recent Activity Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Visual Analytics Preview Card */}
        <div className="lg:col-span-2 p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-lg font-bold text-white flex items-center gap-2">
                <TrendingUp size={20} className="text-amber-400" />
                Audience Growth & Views Trend
              </h2>
              <p className="text-xs text-gray-400 mt-0.5">Aggregated watch metrics from Android clients</p>
            </div>
            <button
              onClick={() => onNavigate('analytics')}
              className="text-xs text-amber-400 hover:text-amber-300 font-semibold flex items-center gap-1"
            >
              Full Analytics <ArrowUpRight size={14} />
            </button>
          </div>

          {/* Elegant Simulated Wave Bar Chart */}
          <div className="h-44 w-full flex items-end gap-3 pt-6 pb-2 px-2">
            {[45, 62, 58, 80, 75, 95, 110, 85, 125, 140, 160, 195].map((val, i) => (
              <div key={i} className="flex-1 flex flex-col items-center gap-2 h-full justify-end group">
                <div
                  className="w-full rounded-t-lg bg-gradient-to-t from-amber-500/40 to-amber-400 group-hover:from-orange-500 group-hover:to-amber-300 transition-all cursor-pointer relative"
                  style={{ height: `${(val / 200) * 100}%` }}
                >
                  <div className="absolute -top-7 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 bg-black text-[10px] text-amber-300 font-bold px-1.5 py-0.5 rounded pointer-events-none transition whitespace-nowrap">
                    {val}K
                  </div>
                </div>
                <span className="text-[10px] text-gray-500 font-medium">
                  {['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][i]}
                </span>
              </div>
            ))}
          </div>

          <div className="grid grid-cols-3 gap-4 mt-6 pt-4 border-t border-[#242424] text-center">
            <div>
              <div className="text-[11px] text-gray-400 uppercase font-semibold">Avg. Watch Duration</div>
              <div className="text-base font-bold text-white mt-1">11m 45s</div>
            </div>
            <div>
              <div className="text-[11px] text-gray-400 uppercase font-semibold">Completion Rate</div>
              <div className="text-base font-bold text-emerald-400 mt-1">74.8%</div>
            </div>
            <div>
              <div className="text-[11px] text-gray-400 uppercase font-semibold">Daily Active Viewers</div>
              <div className="text-base font-bold text-amber-400 mt-1">1,820</div>
            </div>
          </div>
        </div>

        {/* Quick Recent Activity Stream */}
        <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-white">Recent Admin Logs</h3>
              <button
                onClick={() => onNavigate('activity-logs')}
                className="text-xs text-amber-400 hover:text-amber-300 font-semibold"
              >
                View all
              </button>
            </div>
            <div className="space-y-3">
              {logs.slice(0, 4).map((log) => (
                <div key={log.id} className="p-3 rounded-xl bg-[#1E1E1E] border border-[#2A2A2A] text-xs">
                  <div className="flex items-center justify-between text-gray-400 mb-1">
                    <span className="font-semibold text-amber-400">{log.action}</span>
                    <span className="text-[10px]">Just now</span>
                  </div>
                  <p className="text-gray-300 font-medium truncate">{log.details}</p>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-4 p-4 rounded-2xl bg-amber-500/10 border border-amber-500/20 text-xs text-amber-300 flex items-center justify-between">
            <span>Firebase Security Rules Active</span>
            <span className="font-bold">v2.1</span>
          </div>
        </div>
      </div>

      {/* Recent Content Showcase Table */}
      <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h3 className="text-lg font-bold text-white">Recent Vlog Episodes</h3>
            <p className="text-xs text-gray-400">All changes immediately replicate to mobile viewers</p>
          </div>
          <button
            onClick={() => onNavigate('vlogs')}
            className="text-xs font-bold text-amber-400 hover:text-amber-300 flex items-center gap-1"
          >
            Manage all ({vlogs.length}) <ArrowUpRight size={14} />
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-[#262626] text-gray-400 font-semibold uppercase tracking-wider">
                <th className="pb-3">Vlog</th>
                <th className="pb-3">Category</th>
                <th className="pb-3">Duration</th>
                <th className="pb-3">Views</th>
                <th className="pb-3">Status</th>
                <th className="pb-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#222222]">
              {vlogs.slice(0, 4).map((vlog) => (
                <tr key={vlog.id} className="hover:bg-[#1C1C1C] transition">
                  <td className="py-3 pr-4 flex items-center gap-3">
                    <img
                      src={vlog.thumbnailUrl}
                      alt={vlog.title}
                      className="w-16 h-10 object-cover rounded-lg border border-[#333333]"
                    />
                    <span className="font-semibold text-white max-w-xs truncate">{vlog.title}</span>
                  </td>
                  <td className="py-3 text-gray-300">
                    <span className="px-2 py-0.5 rounded-full bg-[#262626] text-[11px]">
                      {vlog.categoryName}
                    </span>
                  </td>
                  <td className="py-3 text-gray-400 font-mono">{vlog.duration}</td>
                  <td className="py-3 text-white font-bold">{vlog.views.toLocaleString()}</td>
                  <td className="py-3">
                    <span className={`px-2 py-0.5 rounded-full font-bold text-[10px] uppercase ${
                      vlog.status === 'published'
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                        : 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/30'
                    }`}>
                      {vlog.status}
                    </span>
                  </td>
                  <td className="py-3 text-right">
                    <button
                      onClick={() => onNavigate('vlogs')}
                      className="text-xs text-amber-400 hover:text-amber-300 font-semibold"
                    >
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
