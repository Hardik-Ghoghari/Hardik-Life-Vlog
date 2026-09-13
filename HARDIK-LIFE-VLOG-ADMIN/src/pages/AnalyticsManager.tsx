import React, { useState, useEffect } from 'react';
import {
  TrendingUp,
  Eye,
  Heart,
  Clock,
  Users,
  Flame,
  PieChart,
  BarChart2
} from 'lucide-react';
import { FirestoreService } from '../services/firestoreService';
import { Vlog } from '../types';

export const AnalyticsManager: React.FC = () => {
  const [vlogs, setVlogs] = useState<Vlog[]>([]);

  useEffect(() => {
    FirestoreService.getVlogs().then(setVlogs);
  }, []);

  const mostViewed = [...vlogs].sort((a, b) => b.views - a.views).slice(0, 5);
  const mostLiked = [...vlogs].sort((a, b) => b.likes - a.likes).slice(0, 5);

  const categoriesDist = [
    { label: 'Travel Adventures', pct: 45, color: 'bg-amber-500' },
    { label: 'Daily Vlogs', pct: 28, color: 'bg-orange-500' },
    { label: 'Food & Street Culture', pct: 17, color: 'bg-emerald-500' },
    { label: 'Gear & BTS', pct: 10, color: 'bg-blue-500' }
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">Platform Analytics & Metrics</h1>
        <p className="text-xs text-gray-400">Audience reach, engagement retention, and top-performing vlog content</p>
      </div>

      {/* 4 Metric overview blocks */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-[#161616] border border-[#262626]">
          <div className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Total Watch Time</div>
          <div className="text-2xl font-black text-white mt-1">48,290 hrs</div>
          <div className="text-[11px] text-emerald-400 font-semibold mt-1">↑ +18.4% this month</div>
        </div>
        <div className="p-5 rounded-2xl bg-[#161616] border border-[#262626]">
          <div className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Average Retention</div>
          <div className="text-2xl font-black text-white mt-1">72.4%</div>
          <div className="text-[11px] text-emerald-400 font-semibold mt-1">High audience loyalty</div>
        </div>
        <div className="p-5 rounded-2xl bg-[#161616] border border-[#262626]">
          <div className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Daily Active Viewers</div>
          <div className="text-2xl font-black text-white mt-1">2,410</div>
          <div className="text-[11px] text-amber-400 font-semibold mt-1">Peak: 8:00 PM - 10:30 PM</div>
        </div>
        <div className="p-5 rounded-2xl bg-[#161616] border border-[#262626]">
          <div className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Engagement Rate</div>
          <div className="text-2xl font-black text-white mt-1">8.9%</div>
          <div className="text-[11px] text-rose-400 font-semibold mt-1">Likes & comments per view</div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Most Viewed List */}
        <div className="lg:col-span-2 p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl">
          <h3 className="text-base font-bold text-white mb-4 flex items-center gap-2">
            <Flame size={18} className="text-amber-500" />
            Top Performing Vlogs (By Views)
          </h3>
          <div className="space-y-3">
            {mostViewed.map((v, i) => (
              <div key={v.id} className="p-3.5 rounded-2xl bg-[#1C1C1C] border border-[#282828] flex items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                  <span className="w-6 text-center font-black text-amber-400 text-sm">#{i + 1}</span>
                  <img src={v.thumbnailUrl} alt={v.title} className="w-16 h-10 object-cover rounded-lg" />
                  <div>
                    <h4 className="text-xs font-bold text-white line-clamp-1">{v.title}</h4>
                    <span className="text-[11px] text-gray-400">{v.categoryName} • {v.duration}</span>
                  </div>
                </div>
                <div className="text-right shrink-0">
                  <div className="text-sm font-black text-white">{v.views.toLocaleString()}</div>
                  <div className="text-[10px] text-gray-400">views</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Popular Categories Distribution */}
        <div className="p-6 rounded-3xl bg-[#161616] border border-[#262626] shadow-xl flex flex-col justify-between">
          <div>
            <h3 className="text-base font-bold text-white mb-4 flex items-center gap-2">
              <PieChart size={18} className="text-amber-400" />
              Category Popularity
            </h3>
            <div className="space-y-4">
              {categoriesDist.map(c => (
                <div key={c.label}>
                  <div className="flex justify-between text-xs mb-1">
                    <span className="text-gray-300 font-medium">{c.label}</span>
                    <span className="text-white font-bold">{c.pct}%</span>
                  </div>
                  <div className="w-full bg-[#242424] rounded-full h-2 overflow-hidden">
                    <div className={`${c.color} h-full rounded-full`} style={{ width: `${c.pct}%` }} />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-[#1F1F1F] border border-[#2B2B2B] text-xs text-gray-300 mt-6">
            💡 Travel content yields 2.4x higher watch retention than average reels.
          </div>
        </div>
      </div>
    </div>
  );
};
