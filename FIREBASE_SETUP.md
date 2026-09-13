# Hardik Life Vlog — Firebase Architecture & Security Rules

This document contains the complete Firebase configuration, Firestore Security Rules, Storage Security Rules, and FCM deep-linking structure used by both the **Hardik Life Vlog Android App** and the **Hardik Life Vlog Web Admin Panel**.

---

## 1. Cloud Firestore Security Rules (`firestore.rules`)

Deploy these rules to your Firebase project to guarantee strict Role-Based Access Control (RBAC):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }

    function isAdmin() {
      return isAuthenticated() && (
        request.auth.token.role == 'admin' ||
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin'
      );
    }

    // Public read-only content collections
    match /vlogs/{vlogId} {
      allow read: if resource.data.status == 'published' || isAdmin();
      allow write: if isAdmin();
    }

    match /shorts/{shortId} {
      allow read: if resource.data.status == 'published' || isAdmin();
      allow write: if isAdmin();
    }

    match /gallery/{photoId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /categories/{categoryId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /banners/{bannerId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /creator_profile/{docId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /social_links/{linkId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /app_settings/{docId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    // User accounts
    match /users/{userId} {
      allow read: if isAuthenticated() && (request.auth.uid == userId || isAdmin());
      allow create: if isAuthenticated();
      allow update: if isAuthenticated() && (
        (request.auth.uid == userId && !request.resource.data.diff(resource.data).affectedKeys().hasAny(['role'])) ||
        isAdmin()
      );
      allow delete: if isAdmin();
    }

    // Broadcast notifications & audit logs
    match /notifications/{notifId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /admin_logs/{logId} {
      allow read, write: if isAdmin();
    }
  }
}
```

---

## 2. Firebase Storage Security Rules (`storage.rules`)

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    function isAdmin() {
      return request.auth != null && (
        request.auth.token.role == 'admin' ||
        firestore.get(/databases/(default)/documents/users/$(request.auth.uid)).data.role == 'admin'
      );
    }

    // Public read for images and video streams
    match /vlogs/{allPaths=**} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /shorts/{allPaths=**} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /gallery/{allPaths=**} {
      allow read: if true;
      allow write: if isAdmin();
    }

    // User profile pictures
    match /avatars/{userId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 3. Deep Linking Scheme

- **Base Scheme**: `hardiklifevlog://`
- **Vlog Episode**: `hardiklifevlog://vlog/{vlogId}`
- **Shorts Feed**: `hardiklifevlog://shorts` or `hardiklifevlog://shorts?shortId={shortId}`
- **Gallery**: `hardiklifevlog://gallery`
- **Profile**: `hardiklifevlog://profile`
