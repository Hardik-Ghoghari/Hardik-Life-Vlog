export interface Vlog {
  id: string;
  title: string;
  description: string;
  thumbnailUrl: string;
  videoUrl: string;
  youtubeId?: string;
  categoryId: string;
  categoryName: string;
  publishedAt: string;
  duration: string;
  views: number;
  likes: number;
  featured: boolean;
  status: 'published' | 'draft' | 'unpublished';
  createdAt?: number;
  updatedAt?: number;
}

export interface ShortVideo {
  id: string;
  title: string;
  description: string;
  videoUrl: string;
  thumbnailUrl: string;
  categoryId: string;
  views: number;
  likes: number;
  status: 'published' | 'draft' | 'unpublished';
  publishedAt: string;
  createdAt?: number;
  updatedAt?: number;
}

export interface GalleryPhoto {
  id: string;
  imageUrl: string;
  caption: string;
  categoryId: string;
  location?: string;
  publishedAt: string;
  status: 'published' | 'draft' | 'unpublished';
  createdAt?: number;
}

export interface ContentCategory {
  id: string;
  name: string;
  description?: string;
  iconName?: string;
  isActive: boolean;
  order: number;
}

export interface HomeBanner {
  id: string;
  imageUrl: string;
  title: string;
  subtitle: string;
  buttonText: string;
  destination: string;
  isActive: boolean;
  order: number;
}

export interface CreatorProfile {
  name: string;
  brand: string;
  tagline: string;
  photoUrl: string;
  bio: string;
  about: string;
  contactEmail: string;
  subscribersCount: string;
  totalVlogsCount: number;
  totalShortsCount: number;
}

export interface SocialLink {
  id: string;
  platform: 'YouTube' | 'Instagram' | 'Facebook' | 'Telegram' | 'WhatsApp' | 'X';
  url: string;
  isActive: boolean;
  order: number;
}

export interface UserAccount {
  id: string;
  name: string;
  email: string;
  photoUrl?: string;
  role: 'admin' | 'user' | 'editor' | 'moderator';
  status: 'active' | 'disabled';
  createdAt: number;
  lastLogin?: number;
}

export interface PushNotification {
  id: string;
  title: string;
  message: string;
  imageUrl?: string;
  targetType: 'all' | 'vlog' | 'short' | 'gallery';
  targetId: string;
  sentAt: number;
  sentBy: string;
}

export interface AdminActivityLog {
  id: string;
  adminId: string;
  adminEmail: string;
  action: string;
  details: string;
  contentId?: string;
  timestamp: number;
}

export interface AppSettings {
  appName: string;
  maintenanceMode: boolean;
  defaultLanguage: 'en' | 'gu' | 'hi';
  contactEmail: string;
  privacyPolicyUrl: string;
  termsConditionsUrl: string;
  appVersion: string;
}
