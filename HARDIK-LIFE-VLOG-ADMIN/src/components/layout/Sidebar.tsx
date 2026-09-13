import React from 'react';
import {
  LayoutDashboard,
  Video,
  Film,
  Image as ImageIcon,
  FolderTree,
  Star,
  Users,
  BarChart3,
  Bell,
  Sliders,
  UserCheck,
  Share2,
  FileText,
  ShieldCheck,
  History,
  X
} from 'lucide-react';

export type AdminTab =
  | 'dashboard'
  | 'vlogs'
  | 'shorts'
  | 'gallery'
  | 'categories'
  | 'featured'
  | 'users'
  | 'analytics'
  | 'watch-stats'
  | 'notifications'
  | 'banners'
  | 'creator-profile'
  | 'social-links'
  | 'app-settings'
  | 'admin-users'
  | 'activity-logs';

interface SidebarProps {
  currentTab: AdminTab;
  onSelectTab: (tab: AdminTab) => void;
  isOpen: boolean;
  onClose: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentTab,
  onSelectTab,
  isOpen,
  onClose
}) => {
  const sections = [
    {
      title: 'OVERVIEW',
      items: [
        { id: 'dashboard' as AdminTab, label: 'Dashboard', icon: LayoutDashboard }
      ]
    },
    {
      title: 'CONTENT MANAGEMENT',
      items: [
        { id: 'vlogs' as AdminTab, label: 'Vlogs', icon: Video, badge: 'HD' },
        { id: 'shorts' as AdminTab, label: 'Shorts', icon: Film },
        { id: 'gallery' as AdminTab, label: 'Photo Gallery', icon: ImageIcon },
        { id: 'categories' as AdminTab, label: 'Categories', icon: FolderTree },
        { id: 'featured' as AdminTab, label: 'Featured Content', icon: Star }
      ]
    },
    {
      title: 'USERS & ENGAGEMENT',
      items: [
        { id: 'users' as AdminTab, label: 'All Users', icon: Users },
        { id: 'analytics' as AdminTab, label: 'Analytics', icon: BarChart3 },
        { id: 'notifications' as AdminTab, label: 'Push Notifications', icon: Bell }
      ]
    },
    {
      title: 'CREATOR & APP SETTINGS',
      items: [
        { id: 'banners' as AdminTab, label: 'Home Banners', icon: Sliders },
        { id: 'creator-profile' as AdminTab, label: 'Creator Profile', icon: UserCheck },
        { id: 'social-links' as AdminTab, label: 'Social Channels', icon: Share2 },
        { id: 'app-settings' as AdminTab, label: 'App Settings', icon: FileText }
      ]
    },
    {
      title: 'SYSTEM & SECURITY',
      items: [
        { id: 'admin-users' as AdminTab, label: 'Admin Roles', icon: ShieldCheck },
        { id: 'activity-logs' as AdminTab, label: 'Activity Logs', icon: History }
      ]
    }
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/60 lg:hidden"
          onClick={onClose}
        />
      )}

      <aside
        className={`fixed top-0 bottom-0 left-0 z-40 w-72 bg-[#121212] border-r border-[#222222] flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Header */}
        <div className="h-18 px-6 py-4 flex items-center justify-between border-b border-[#222222]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-500 flex items-center justify-center font-black text-black text-xl shadow-lg shadow-amber-500/20">
              H
            </div>
            <div>
              <h1 className="font-bold text-base tracking-tight text-white leading-tight">Hardik Life Vlog</h1>
              <span className="text-[11px] font-semibold text-amber-400 uppercase tracking-wider bg-amber-400/10 px-2 py-0.5 rounded-full">
                Admin Studio
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="lg:hidden p-1.5 text-gray-400 hover:text-white rounded-lg hover:bg-[#202020]"
          >
            <X size={20} />
          </button>
        </div>

        {/* Navigation list */}
        <div className="flex-1 overflow-y-auto px-4 py-5 space-y-6">
          {sections.map((sec, idx) => (
            <div key={idx}>
              <h2 className="px-3 text-[11px] font-bold text-gray-400 tracking-wider mb-2">
                {sec.title}
              </h2>
              <div className="space-y-1">
                {sec.items.map(item => {
                  const Icon = item.icon;
                  const isActive = currentTab === item.id;
                  return (
                    <button
                      key={item.id}
                      onClick={() => {
                        onSelectTab(item.id);
                        onClose();
                      }}
                      className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                        isActive
                          ? 'bg-gradient-to-r from-amber-500/15 to-orange-500/15 text-amber-400 border border-amber-500/30 font-semibold'
                          : 'text-gray-400 hover:text-white hover:bg-[#1C1C1C]'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <Icon size={18} className={isActive ? 'text-amber-400' : 'text-gray-400'} />
                        <span>{item.label}</span>
                      </div>
                      {item.badge && (
                        <span className="text-[10px] bg-[#2A2A2A] text-gray-300 px-1.5 py-0.5 rounded font-bold">
                          {item.badge}
                        </span>
                      )}
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        {/* Footer info */}
        <div className="p-4 border-t border-[#222222] bg-[#0E0E0E]">
          <div className="flex items-center justify-between text-xs text-gray-400">
            <span>Firebase Connected</span>
            <span className="flex items-center gap-1 text-emerald-400 font-medium">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Live Sync
            </span>
          </div>
        </div>
      </aside>
    </>
  );
};
