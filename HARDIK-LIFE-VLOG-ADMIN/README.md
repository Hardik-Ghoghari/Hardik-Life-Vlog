# Hardik Life Vlog — Professional Web Admin Panel

This is the official creator web control console for **Hardik Life Vlog**. It interfaces directly with the same Google Cloud Firestore & Firebase backend utilized by the **Hardik Life Vlog Android App**.

---

## 🚀 Key Features

1. **Role-Based Security**:
   - Strictly enforced via client-side routing and Firestore Security Rules.
   - Only authenticated accounts with `role: "admin"` can access the dashboard.
   - Viewer or guest accounts attempting to access `/admin` are immediately routed to the `Access Denied` security screen.

2. **Full CRUD Modules**:
   - **Vlogs Management**: Add, edit, delete, publish/unpublish, feature episodes, and preview playback.
   - **Shorts Management**: Vertical 9:16 reels management with tags and live preview.
   - **Gallery Management**: Photo moments, location tags, captions, and simulated Firebase Storage uploads.
   - **Categories**: Dynamic category management that powers the Android carousel tabs.
   - **Featured Content**: Pin hero vlogs dynamically on the Android Home Screen without releasing a new APK.
   - **Promotional Banners**: Carousel promo cards with deep link targets.
   - **Creator Profile**: Update Hardik's bio, avatar, subscriber counts, and about story live.
   - **Social Links**: Manage YouTube, Instagram, Facebook, Telegram, WhatsApp, and X links.
   - **User Accounts**: Browse viewer accounts, inspect metadata, and suspend/reactivate accounts.
   - **Push Notifications (FCM)**: Broadcast notifications with deep link URLs (`hardiklifevlog://vlog/{vlogId}`).
   - **Analytics**: Watch hours, average retention, daily active users, and top video metrics.
   - **Audit Logs**: Immutable log of administrative actions with timestamp and IP.
   - **Global App Settings**: Maintenance mode toggle, force update flag, minimum app version, support email, and legal URLs.

---

## 🛠️ How to Run Locally

```bash
# Navigate to admin panel directory
cd HARDIK-LIFE-VLOG-ADMIN

# Install dependencies (React 18, Vite, Tailwind CSS, Lucide Icons, Firebase SDK)
npm install

# Run local development server
npm run dev
```

The admin panel runs on `http://localhost:5173`.

### Default Admin Credentials (Pre-configured)
- **Email**: `hardik@lifevlog.com`
- **Password**: `hardik@2026`
- **Role**: `admin`

---

## 🔒 Firebase Integration

Both the Android App and this Admin Panel share the identical Firestore collections:
- `vlogs`
- `shorts`
- `gallery`
- `categories`
- `banners`
- `creator_profile`
- `social_links`
- `app_settings`
- `users`
- `notifications`
- `admin_logs`
