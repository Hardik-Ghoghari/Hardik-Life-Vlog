import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Sidebar, AdminTab } from './components/layout/Sidebar';
import { Header } from './components/layout/Header';
import { Login } from './pages/Login';
import { AccessDenied } from './pages/AccessDenied';
import { Dashboard } from './pages/Dashboard';
import { VlogsManager } from './pages/VlogsManager';
import { ShortsManager } from './pages/ShortsManager';
import { GalleryManager } from './pages/GalleryManager';
import { CategoriesManager } from './pages/CategoriesManager';
import { FeaturedManager } from './pages/FeaturedManager';
import { BannersManager } from './pages/BannersManager';
import { CreatorProfileManager } from './pages/CreatorProfileManager';
import { SocialLinksManager } from './pages/SocialLinksManager';
import { UsersManager } from './pages/UsersManager';
import { NotificationsManager } from './pages/NotificationsManager';
import { AnalyticsManager } from './pages/AnalyticsManager';
import { ActivityLogsManager } from './pages/ActivityLogsManager';
import { AppSettingsManager } from './pages/AppSettingsManager';

const AdminLayout: React.FC = () => {
  const { user, isAuthenticated, loading, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<AdminTab>('dashboard');
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isAccessDenied, setIsAccessDenied] = useState(false);
  const [quickActionState, setQuickActionState] = useState<string | null>(null);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0D0D0D] flex items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 border-4 border-amber-500 border-t-transparent rounded-full animate-spin" />
          <p className="text-gray-400 text-xs font-semibold tracking-wider uppercase">Loading Hardik Life Vlog Admin...</p>
        </div>
      </div>
    );
  }

  if (isAccessDenied) {
    return <AccessDenied onBackToLogin={() => { setIsAccessDenied(false); logout(); }} />;
  }

  if (!isAuthenticated || !user) {
    return <Login onAccessDenied={() => setIsAccessDenied(true)} />;
  }

  // Security barrier: Role Check
  if (user.role !== 'admin') {
    return <AccessDenied onBackToLogin={() => logout()} />;
  }

  const handleQuickAction = (action: 'add_vlog' | 'add_short' | 'add_photo' | 'send_notif') => {
    setQuickActionState(action);
    if (action === 'add_vlog') setActiveTab('vlogs');
    else if (action === 'add_short') setActiveTab('shorts');
    else if (action === 'add_photo') setActiveTab('gallery');
    else if (action === 'send_notif') setActiveTab('notifications');
  };

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return (
          <Dashboard
            onNavigate={(tab) => { setActiveTab(tab); setQuickActionState(null); }}
            onOpenQuickAction={handleQuickAction}
          />
        );
      case 'vlogs':
        return <VlogsManager initialOpenNewModal={quickActionState === 'add_vlog'} />;
      case 'shorts':
        return <ShortsManager initialOpenNewModal={quickActionState === 'add_short'} />;
      case 'gallery':
        return <GalleryManager initialOpenUploadModal={quickActionState === 'add_photo'} />;
      case 'categories':
        return <CategoriesManager />;
      case 'featured':
        return <FeaturedManager />;
      case 'banners':
        return <BannersManager />;
      case 'creator-profile':
        return <CreatorProfileManager />;
      case 'social-links':
        return <SocialLinksManager />;
      case 'users':
        return <UsersManager />;
      case 'notifications':
        return <NotificationsManager initialOpenModal={quickActionState === 'send_notif'} />;
      case 'analytics':
        return <AnalyticsManager />;
      case 'activity-logs':
        return <ActivityLogsManager />;
      case 'settings':
        return <AppSettingsManager />;
      default:
        return <Dashboard onNavigate={setActiveTab} onOpenQuickAction={handleQuickAction} />;
    }
  };

  return (
    <div className="flex min-h-screen bg-[#0D0D0D] text-gray-100 font-sans">
      {/* Sidebar navigation */}
      <Sidebar
        activeTab={activeTab}
        onTabChange={(tab) => {
          setActiveTab(tab);
          setQuickActionState(null);
        }}
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)}
      />

      {/* Main content area */}
      <div className="flex-1 flex flex-col min-w-0">
        <Header
          activeTab={activeTab}
          onOpenSidebar={() => setIsSidebarOpen(true)}
          onNavigate={(tab) => {
            setActiveTab(tab);
            setQuickActionState(null);
          }}
        />

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          {renderContent()}
        </main>
      </div>
    </div>
  );
};

export default function App() {
  return (
    <AuthProvider>
      <AdminLayout />
    </AuthProvider>
  );
}
