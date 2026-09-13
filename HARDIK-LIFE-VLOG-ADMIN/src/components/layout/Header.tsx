import React from 'react';
import { Menu, LogOut, Plus, Bell, Shield, ExternalLink } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { AdminTab } from './Sidebar';

interface HeaderProps {
  onOpenSidebar: () => void;
  currentTab: AdminTab;
  onQuickAction: (action: 'add_vlog' | 'send_notif') => void;
}

export const Header: React.FC<HeaderProps> = ({
  onOpenSidebar,
  currentTab,
  onQuickAction
}) => {
  const { currentUser, logout } = useAuth();

  const tabTitles: Record<AdminTab, string> = {
    dashboard: 'Admin Dashboard',
    vlogs: 'Vlogs Management',
    shorts: 'Shorts Reel Management',
    gallery: 'Photo Moments Gallery',
    categories: 'Content Categories',
    featured: 'Featured Content Controls',
    users: 'Registered Users Directory',
    analytics: 'Analytics & Watch Stats',
    'watch-stats': 'Detailed Watch Statistics',
    notifications: 'Push Notification Broadcast',
    banners: 'Home Promo Banners',
    'creator-profile': 'Creator Profile Management',
    'social-links': 'Social Channels & Community',
    'app-settings': 'App Configuration & Maintenance',
    'admin-users': 'Administrators & Roles',
    'activity-logs': 'System Activity Audit Log'
  };

  return (
    <header className="sticky top-0 z-30 h-18 bg-[#121212]/95 backdrop-blur border-b border-[#222222] px-4 lg:px-8 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <button
          onClick={onOpenSidebar}
          className="lg:hidden p-2 text-gray-400 hover:text-white rounded-xl hover:bg-[#202020]"
        >
          <Menu size={22} />
        </button>
        <div>
          <h2 className="text-lg lg:text-xl font-bold text-white tracking-tight">
            {tabTitles[currentTab] || 'Admin Console'}
          </h2>
          <p className="hidden sm:block text-xs text-gray-400">
            Hardik Life Vlog Platform • Realtime Database Sync
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3">
        {/* Quick Action Buttons */}
        <button
          onClick={() => onQuickAction('add_vlog')}
          className="hidden sm:flex items-center gap-2 px-3.5 py-2 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-black font-bold text-xs shadow-md shadow-amber-500/10 transition"
        >
          <Plus size={16} />
          <span>New Vlog</span>
        </button>

        <button
          onClick={() => onQuickAction('send_notif')}
          className="hidden md:flex items-center gap-2 px-3 py-2 rounded-xl bg-[#202020] hover:bg-[#2A2A2A] text-gray-300 hover:text-white text-xs font-semibold border border-[#333333] transition"
        >
          <Bell size={15} className="text-amber-400" />
          <span>Notify</span>
        </button>

        {/* Admin info badge */}
        <div className="flex items-center gap-3 pl-3 border-l border-[#262626]">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-amber-400 to-orange-500 p-[1px]">
            <div className="w-full h-full bg-[#161616] rounded-xl flex items-center justify-center text-amber-400 font-bold text-sm">
              {currentUser?.name?.[0] || 'H'}
            </div>
          </div>
          <div className="hidden xl:block text-left">
            <div className="text-xs font-bold text-white leading-tight flex items-center gap-1.5">
              <span>{currentUser?.name || 'Hardik Patel'}</span>
              <Shield size={12} className="text-amber-400" />
            </div>
            <div className="text-[11px] text-gray-400">{currentUser?.email || 'hardik@lifevlog.com'}</div>
          </div>

          <button
            onClick={logout}
            title="Sign out of Admin Panel"
            className="p-2 text-gray-400 hover:text-red-400 rounded-xl hover:bg-[#202020] transition ml-1"
          >
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </header>
  );
};
