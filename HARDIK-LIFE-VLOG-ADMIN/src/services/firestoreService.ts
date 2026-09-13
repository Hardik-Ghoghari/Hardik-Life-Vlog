import {
  collection,
  doc,
  getDocs,
  getDoc,
  setDoc,
  updateDoc,
  deleteDoc,
  query,
  orderBy,
  limit
} from 'firebase/firestore';
import { db } from '../firebase/config';
import {
  Vlog,
  ShortVideo,
  GalleryPhoto,
  ContentCategory,
  HomeBanner,
  CreatorProfile,
  SocialLink,
  UserAccount,
  PushNotification,
  AdminActivityLog,
  AppSettings
} from '../types';

// Default mock datasets matching Android app datasource
export const defaultCategories: ContentCategory[] = [
  { id: 'cat_daily', name: 'Daily Life', description: 'Raw unfiltered daily life vlogs', iconName: 'Home', isActive: true, order: 1 },
  { id: 'cat_travel', name: 'Travel', description: 'Road trips, mountain hikes & explorations', iconName: 'Compass', isActive: true, order: 2 },
  { id: 'cat_lifestyle', name: 'Lifestyle', description: 'Morning routines, gym workouts & productivity', iconName: 'Sparkles', isActive: true, order: 3 },
  { id: 'cat_food', name: 'Food', description: 'Street food hunts, cooking & tasting sessions', iconName: 'Utensils', isActive: true, order: 4 },
  { id: 'cat_events', name: 'Events', description: 'Festivals, weddings, fan meetups & celebrations', iconName: 'Calendar', isActive: true, order: 5 },
  { id: 'cat_family', name: 'Family', description: 'Heartfelt moments with parents & grandparents', iconName: 'Users', isActive: true, order: 6 },
  { id: 'cat_friends', name: 'Friends', description: 'College reunions, pranks & weekend hangouts', iconName: 'Smile', isActive: true, order: 7 },
  { id: 'cat_adventure', name: 'Adventure', description: 'Camping, off-roading & thrilling challenges', iconName: 'Zap', isActive: true, order: 8 },
  { id: 'cat_other', name: 'Other', description: 'Q&As, announcements & behind the scenes', iconName: 'MoreHorizontal', isActive: true, order: 9 }
];

export const defaultVlogs: Vlog[] = [
  {
    id: 'vlog_001',
    title: 'Exploring The Hidden Valley: Our 3-Day Mountain Road Trip!',
    description: 'Come along with me as we pack our bags, fuel up the SUV, and drive through the foggy hairpin turns of the northern valley.',
    thumbnailUrl: 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&q=80',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    youtubeId: 'dQw4w9WgXcQ',
    categoryId: 'cat_travel',
    categoryName: 'Travel',
    publishedAt: '2 days ago',
    duration: '18:42',
    views: 45200,
    likes: 3820,
    featured: true,
    status: 'published',
    createdAt: Date.now() - 172800000
  },
  {
    id: 'vlog_002',
    title: 'Surprising My Mom With Her Dream Garden Makeover ❤️',
    description: 'Mom has been talking about rebuilding our backyard veranda for over two years. With the help of my childhood friends, we turned it into a paradise!',
    thumbnailUrl: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800&q=80',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
    categoryId: 'cat_family',
    categoryName: 'Family',
    publishedAt: '5 days ago',
    duration: '22:15',
    views: 89400,
    likes: 9150,
    featured: false,
    status: 'published',
    createdAt: Date.now() - 432000000
  },
  {
    id: 'vlog_003',
    title: '24 Hours Surviving Only On Iconic Old City Street Food!',
    description: 'We took the early morning train to the heritage food quarters! From piping hot maska bun & chai to midnight spicy samosas.',
    thumbnailUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
    categoryId: 'cat_food',
    categoryName: 'Food',
    publishedAt: '1 week ago',
    duration: '16:50',
    views: 64100,
    likes: 5400,
    featured: false,
    status: 'published',
    createdAt: Date.now() - 604800000
  },
  {
    id: 'vlog_004',
    title: 'A Productive Day In My Life: Vlogger Studio Setup & Workout',
    description: 'Starting 5:30 AM with morning meditation, heavy kettlebell workout, coffee brewing, editing vlog rushes, and unboxing new studio gear.',
    thumbnailUrl: 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=800&q=80',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4',
    categoryId: 'cat_lifestyle',
    categoryName: 'Lifestyle',
    publishedAt: '2 weeks ago',
    duration: '14:20',
    views: 31200,
    likes: 2750,
    featured: false,
    status: 'published',
    createdAt: Date.now() - 1209600000
  }
];

export const defaultShorts: ShortVideo[] = [
  {
    id: 'short_001',
    title: 'Quick 60s Sunset Drone Shot over the Cliffs! 🌅',
    description: 'Caught this golden hour rim light just before battery died!',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&q=80',
    categoryId: 'cat_travel',
    views: 125000,
    likes: 14200,
    status: 'published',
    publishedAt: '1 day ago'
  },
  {
    id: 'short_002',
    title: 'The crunch on this hot Jalebi is unreal! 🤤',
    description: 'Fresh out of the boiling sugar cauldron in Old Bazaar.',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80',
    categoryId: 'cat_food',
    views: 98000,
    likes: 11200,
    status: 'published',
    publishedAt: '3 days ago'
  }
];

export const defaultPhotos: GalleryPhoto[] = [
  {
    id: 'photo_001',
    imageUrl: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1000&q=80',
    caption: 'Sunset reflections across the Himalayan foothills.',
    categoryId: 'cat_travel',
    location: 'Himachal Valley',
    publishedAt: '3 days ago',
    status: 'published'
  },
  {
    id: 'photo_002',
    imageUrl: 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=1000&q=80',
    caption: 'Late night brainstorming for our upcoming community meet.',
    categoryId: 'cat_friends',
    location: 'Studio Loft',
    publishedAt: '5 days ago',
    status: 'published'
  }
];

export const defaultBanners: HomeBanner[] = [
  {
    id: 'banner_001',
    imageUrl: 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1200&q=80',
    title: 'New Mountain Road Trip Live!',
    subtitle: 'Stream 4K episode now with behind-the-scenes footage',
    buttonText: 'Watch Episode',
    destination: 'hardiklifevlog://vlog/vlog_001',
    isActive: true,
    order: 1
  }
];

export const defaultCreatorProfile: CreatorProfile = {
  name: 'Hardik',
  brand: 'Hardik Life Vlog',
  tagline: 'Filmmaker • Traveler • Storyteller',
  photoUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&q=80',
  bio: 'Sharing raw, authentic adventures, family warmth, road trips, and daily stories.',
  about: 'Welcome to Hardik Life Vlog! I started this channel in 2021 to document the vibrant beauty of daily Indian life, unexplored highways, local delicacies, and candid conversations with the people who make this world beautiful.',
  contactEmail: 'hardik@lifevlog.com',
  subscribersCount: '250K+',
  totalVlogsCount: 148,
  totalShortsCount: 320
};

export const defaultSocialLinks: SocialLink[] = [
  { id: 'yt', platform: 'YouTube', url: 'https://youtube.com/@hardiklifevlog', isActive: true, order: 1 },
  { id: 'ig', platform: 'Instagram', url: 'https://instagram.com/hardiklifevlog', isActive: true, order: 2 },
  { id: 'tg', platform: 'Telegram', url: 'https://t.me/hardiklifevlog', isActive: true, order: 3 },
  { id: 'fb', platform: 'Facebook', url: 'https://facebook.com/hardiklifevlog', isActive: true, order: 4 },
  { id: 'wa', platform: 'WhatsApp', url: 'https://whatsapp.com/channel/hardikvlog', isActive: true, order: 5 },
  { id: 'x', platform: 'X', url: 'https://x.com/hardiklifevlog', isActive: true, order: 6 }
];

export const defaultUsers: UserAccount[] = [
  { id: 'usr_admin', name: 'Hardik Patel', email: 'hardik@lifevlog.com', role: 'admin', status: 'active', createdAt: Date.now() - 31536000000, lastLogin: Date.now() },
  { id: 'usr_002', name: 'Aarav Mehta', email: 'aarav@gmail.com', role: 'user', status: 'active', createdAt: Date.now() - 5000000000, lastLogin: Date.now() - 1000000 },
  { id: 'usr_003', name: 'Priya Sharma', email: 'priya@yahoo.com', role: 'user', status: 'active', createdAt: Date.now() - 12000000000, lastLogin: Date.now() - 86400000 },
  { id: 'usr_004', name: 'Rohan Gupta', email: 'rohan@outlook.com', role: 'user', status: 'active', createdAt: Date.now() - 2000000000, lastLogin: Date.now() - 3600000 }
];

export const defaultAppSettings: AppSettings = {
  appName: 'Hardik Life Vlog',
  maintenanceMode: false,
  defaultLanguage: 'en',
  contactEmail: 'contact@hardiklifevlog.com',
  privacyPolicyUrl: 'https://hardiklifevlog.com/privacy',
  termsConditionsUrl: 'https://hardiklifevlog.com/terms',
  appVersion: '1.0.0'
};

// Helper to get from local storage with fallback
function getStorageItem<T>(key: string, fallback: T): T {
  try {
    const data = localStorage.getItem(`hlv_admin_${key}`);
    return data ? JSON.parse(data) : fallback;
  } catch {
    return fallback;
  }
}

function setStorageItem<T>(key: string, val: T): void {
  try {
    localStorage.setItem(`hlv_admin_${key}`, JSON.stringify(val));
  } catch (e) {
    console.error('Storage error', e);
  }
}

// Service methods
export const FirestoreService = {
  // Activity Logging
  async logAction(action: string, details: string, contentId?: string): Promise<void> {
    const logs = getStorageItem<AdminActivityLog[]>('logs', []);
    const newLog: AdminActivityLog = {
      id: 'log_' + Date.now(),
      adminId: 'usr_admin',
      adminEmail: 'hardik@lifevlog.com',
      action,
      details,
      contentId,
      timestamp: Date.now()
    };
    const updated = [newLog, ...logs].slice(0, 100);
    setStorageItem('logs', updated);

    try {
      await setDoc(doc(db, 'adminLogs', newLog.id), newLog);
    } catch {
      // safe fallback
    }
  },

  async getActivityLogs(): Promise<AdminActivityLog[]> {
    try {
      const snap = await getDocs(query(collection(db, 'adminLogs'), orderBy('timestamp', 'desc'), limit(50)));
      if (!snap.empty) {
        return snap.docs.map(d => d.data() as AdminActivityLog);
      }
    } catch {
      // fallback
    }
    return getStorageItem<AdminActivityLog[]>('logs', [
      { id: '1', adminId: 'usr_admin', adminEmail: 'hardik@lifevlog.com', action: 'Vlog Published', details: 'Published "Exploring The Hidden Valley"', contentId: 'vlog_001', timestamp: Date.now() - 172800000 },
      { id: '2', adminId: 'usr_admin', adminEmail: 'hardik@lifevlog.com', action: 'Banner Updated', details: 'Updated main promotional banner', timestamp: Date.now() - 3600000 }
    ]);
  },

  // Vlogs
  async getVlogs(): Promise<Vlog[]> {
    try {
      const snap = await getDocs(collection(db, 'vlogs'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as Vlog));
      }
    } catch {
      // fallback
    }
    return getStorageItem<Vlog[]>('vlogs', defaultVlogs);
  },

  async saveVlog(vlog: Vlog): Promise<void> {
    const vlogs = await this.getVlogs();
    const index = vlogs.findIndex(v => v.id === vlog.id);
    let updated: Vlog[];
    if (index >= 0) {
      updated = [...vlogs];
      updated[index] = { ...vlog, updatedAt: Date.now() };
    } else {
      updated = [{ ...vlog, createdAt: Date.now(), updatedAt: Date.now() }, ...vlogs];
    }
    setStorageItem('vlogs', updated);
    await this.logAction(index >= 0 ? 'Vlog Edited' : 'Vlog Created', `Title: ${vlog.title}`, vlog.id);

    try {
      await setDoc(doc(db, 'vlogs', vlog.id), vlog);
    } catch {
      // safe fallback
    }
  },

  async deleteVlog(id: string): Promise<void> {
    const vlogs = await this.getVlogs();
    const target = vlogs.find(v => v.id === id);
    const updated = vlogs.filter(v => v.id !== id);
    setStorageItem('vlogs', updated);
    await this.logAction('Vlog Deleted', `Deleted "${target?.title || id}"`, id);

    try {
      await deleteDoc(doc(db, 'vlogs', id));
    } catch {
      // safe fallback
    }
  },

  // Shorts
  async getShorts(): Promise<ShortVideo[]> {
    try {
      const snap = await getDocs(collection(db, 'shorts'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as ShortVideo));
      }
    } catch {
      // fallback
    }
    return getStorageItem<ShortVideo[]>('shorts', defaultShorts);
  },

  async saveShort(short: ShortVideo): Promise<void> {
    const shorts = await this.getShorts();
    const index = shorts.findIndex(s => s.id === short.id);
    let updated: ShortVideo[];
    if (index >= 0) {
      updated = [...shorts];
      updated[index] = { ...short, updatedAt: Date.now() };
    } else {
      updated = [{ ...short, createdAt: Date.now(), updatedAt: Date.now() }, ...shorts];
    }
    setStorageItem('shorts', updated);
    await this.logAction(index >= 0 ? 'Short Edited' : 'Short Created', `Title: ${short.title}`, short.id);

    try {
      await setDoc(doc(db, 'shorts', short.id), short);
    } catch {
      // safe fallback
    }
  },

  async deleteShort(id: string): Promise<void> {
    const shorts = await this.getShorts();
    const updated = shorts.filter(s => s.id !== id);
    setStorageItem('shorts', updated);
    await this.logAction('Short Deleted', `ID: ${id}`, id);

    try {
      await deleteDoc(doc(db, 'shorts', id));
    } catch {
      // safe fallback
    }
  },

  // Gallery Photos
  async getPhotos(): Promise<GalleryPhoto[]> {
    try {
      const snap = await getDocs(collection(db, 'photos'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as GalleryPhoto));
      }
    } catch {
      // fallback
    }
    return getStorageItem<GalleryPhoto[]>('photos', defaultPhotos);
  },

  async savePhoto(photo: GalleryPhoto): Promise<void> {
    const photos = await this.getPhotos();
    const index = photos.findIndex(p => p.id === photo.id);
    let updated: GalleryPhoto[];
    if (index >= 0) {
      updated = [...photos];
      updated[index] = photo;
    } else {
      updated = [{ ...photo, createdAt: Date.now() }, ...photos];
    }
    setStorageItem('photos', updated);
    await this.logAction(index >= 0 ? 'Photo Edited' : 'Photo Uploaded', `Caption: ${photo.caption}`, photo.id);

    try {
      await setDoc(doc(db, 'photos', photo.id), photo);
    } catch {
      // safe fallback
    }
  },

  async deletePhoto(id: string): Promise<void> {
    const photos = await this.getPhotos();
    const updated = photos.filter(p => p.id !== id);
    setStorageItem('photos', updated);
    await this.logAction('Photo Deleted', `ID: ${id}`, id);

    try {
      await deleteDoc(doc(db, 'photos', id));
    } catch {
      // safe fallback
    }
  },

  // Categories
  async getCategories(): Promise<ContentCategory[]> {
    try {
      const snap = await getDocs(collection(db, 'categories'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as ContentCategory));
      }
    } catch {
      // fallback
    }
    return getStorageItem<ContentCategory[]>('categories', defaultCategories);
  },

  async saveCategory(category: ContentCategory): Promise<void> {
    const cats = await this.getCategories();
    const index = cats.findIndex(c => c.id === category.id);
    let updated: ContentCategory[];
    if (index >= 0) {
      updated = [...cats];
      updated[index] = category;
    } else {
      updated = [...cats, category];
    }
    setStorageItem('categories', updated);
    await this.logAction('Category Saved', `Name: ${category.name}`, category.id);

    try {
      await setDoc(doc(db, 'categories', category.id), category);
    } catch {
      // safe fallback
    }
  },

  async deleteCategory(id: string): Promise<void> {
    const cats = await this.getCategories();
    const updated = cats.filter(c => c.id !== id);
    setStorageItem('categories', updated);
    await this.logAction('Category Deleted', `ID: ${id}`, id);

    try {
      await deleteDoc(doc(db, 'categories', id));
    } catch {
      // safe fallback
    }
  },

  // Banners
  async getBanners(): Promise<HomeBanner[]> {
    try {
      const snap = await getDocs(collection(db, 'banners'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as HomeBanner));
      }
    } catch {
      // fallback
    }
    return getStorageItem<HomeBanner[]>('banners', defaultBanners);
  },

  async saveBanner(banner: HomeBanner): Promise<void> {
    const banners = await this.getBanners();
    const index = banners.findIndex(b => b.id === banner.id);
    let updated: HomeBanner[];
    if (index >= 0) {
      updated = [...banners];
      updated[index] = banner;
    } else {
      updated = [...banners, banner];
    }
    setStorageItem('banners', updated);
    await this.logAction('Banner Saved', `Title: ${banner.title}`, banner.id);

    try {
      await setDoc(doc(db, 'banners', banner.id), banner);
    } catch {
      // safe fallback
    }
  },

  async deleteBanner(id: string): Promise<void> {
    const banners = await this.getBanners();
    const updated = banners.filter(b => b.id !== id);
    setStorageItem('banners', updated);
    await this.logAction('Banner Deleted', `ID: ${id}`, id);

    try {
      await deleteDoc(doc(db, 'banners', id));
    } catch {
      // safe fallback
    }
  },

  // Creator Profile
  async getCreatorProfile(): Promise<CreatorProfile> {
    try {
      const snap = await getDoc(doc(db, 'settings', 'creatorProfile'));
      if (snap.exists()) {
        return snap.data() as CreatorProfile;
      }
    } catch {
      // fallback
    }
    return getStorageItem<CreatorProfile>('creatorProfile', defaultCreatorProfile);
  },

  async saveCreatorProfile(profile: CreatorProfile): Promise<void> {
    setStorageItem('creatorProfile', profile);
    await this.logAction('Creator Profile Updated', `Updated by Hardik`);

    try {
      await setDoc(doc(db, 'settings', 'creatorProfile'), profile);
    } catch {
      // safe fallback
    }
  },

  // Social Links
  async getSocialLinks(): Promise<SocialLink[]> {
    try {
      const snap = await getDocs(collection(db, 'socialLinks'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as SocialLink));
      }
    } catch {
      // fallback
    }
    return getStorageItem<SocialLink[]>('socialLinks', defaultSocialLinks);
  },

  async saveSocialLinks(links: SocialLink[]): Promise<void> {
    setStorageItem('socialLinks', links);
    await this.logAction('Social Links Updated', `Configured ${links.length} platforms`);

    try {
      for (const link of links) {
        await setDoc(doc(db, 'socialLinks', link.id), link);
      }
    } catch {
      // safe fallback
    }
  },

  // Users
  async getUsers(): Promise<UserAccount[]> {
    return getStorageItem<UserAccount[]>('users', defaultUsers);
  },

  async updateUserStatus(id: string, status: 'active' | 'disabled'): Promise<void> {
    const users = await this.getUsers();
    const updated = users.map(u => u.id === id ? { ...u, status } : u);
    setStorageItem('users', updated);
    await this.logAction('User Status Changed', `User: ${id} -> ${status}`, id);
  },

  async updateUserRole(id: string, role: 'admin' | 'user' | 'editor' | 'moderator'): Promise<void> {
    const users = await this.getUsers();
    const updated = users.map(u => u.id === id ? { ...u, role } : u);
    setStorageItem('users', updated);
    await this.logAction('User Role Changed', `User: ${id} -> ${role}`, id);
  },

  // Push Notifications
  async getNotifications(): Promise<PushNotification[]> {
    return getStorageItem<PushNotification[]>('notifications', [
      {
        id: 'notif_1',
        title: 'New Mountain Vlog is Live! 🏔️',
        message: 'Watch our 3-Day road trip through northern valley with drone 4K views!',
        targetType: 'vlog',
        targetId: 'vlog_001',
        sentAt: Date.now() - 172800000,
        sentBy: 'Hardik'
      }
    ]);
  },

  async sendNotification(notification: PushNotification): Promise<void> {
    const notifs = await this.getNotifications();
    const updated = [notification, ...notifs];
    setStorageItem('notifications', updated);
    await this.logAction('Notification Broadcasted', `Title: ${notification.title}`, notification.targetId);

    try {
      await setDoc(doc(db, 'notifications', notification.id), notification);
    } catch {
      // safe fallback
    }
  },

  // App Settings
  async getAppSettings(): Promise<AppSettings> {
    return getStorageItem<AppSettings>('settings', defaultAppSettings);
  },

  async saveAppSettings(settings: AppSettings): Promise<void> {
    setStorageItem('settings', settings);
    await this.logAction('App Settings Updated', `Maintenance: ${settings.maintenanceMode}`);

    try {
      await setDoc(doc(db, 'settings', 'appConfig'), settings);
    } catch {
      // safe fallback
    }
  }
};
