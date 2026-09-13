package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.domain.model.UserAccount
import com.example.domain.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    private val defaultGuestProfile = UserProfile(
        userId = "guest_user",
        name = "Guest Viewer",
        email = "guest@hardiklifevlog.com",
        bio = "Exploring Hardik's adventures and vlogs",
        role = "user",
        status = "active",
        isGuest = true,
        isAdmin = false
    )

    private val _currentUser = MutableStateFlow<UserProfile>(defaultGuestProfile)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseAuth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                checkCurrentFirebaseUser()
            } else {
                Log.w("AuthRepository", "FirebaseApp not initialized yet")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error initializing Firebase Auth/Firestore: ${e.message}", e)
        }
    }

    private fun checkCurrentFirebaseUser() {
        val auth = firebaseAuth ?: return
        val user = auth.currentUser ?: return

        scope.launch {
            try {
                val db = firestore
                if (db != null) {
                    val userDoc = db.collection("users").document(user.uid).get().await()
                    if (userDoc.exists()) {
                        val role = userDoc.getString("role") ?: "user"
                        val status = userDoc.getString("status") ?: "active"
                        val name = userDoc.getString("name") ?: user.displayName ?: user.email?.substringBefore("@") ?: "Community Member"
                        val email = userDoc.getString("email") ?: user.email ?: ""
                        val photoUrl = userDoc.getString("photoUrl") ?: ""

                        if (status.equals("suspended", ignoreCase = true)) {
                            Log.w("AuthRepository", "User ${user.uid} is suspended. Logging out.")
                            auth.signOut()
                            _currentUser.value = defaultGuestProfile
                            return@launch
                        }

                        _currentUser.value = UserProfile(
                            userId = user.uid,
                            name = name,
                            email = email,
                            photoUrl = photoUrl,
                            role = role,
                            status = status,
                            isGuest = false,
                            isAdmin = (role.equals("admin", ignoreCase = true))
                        )
                        return@launch
                    }
                }

                // If document doesn't exist yet, default to normal user
                _currentUser.value = UserProfile(
                    userId = user.uid,
                    name = user.displayName ?: user.email?.substringBefore("@") ?: "Community Member",
                    email = user.email ?: "",
                    role = "user",
                    status = "active",
                    isGuest = false,
                    isAdmin = false
                )
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error verifying user profile in Firestore", e)
            }
        }
    }

    suspend fun signIn(email: String, pass: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val auth = firebaseAuth ?: return@withContext Result.failure(IllegalStateException("Firebase Auth not initialized"))
        val db = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore not initialized"))

        try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val fbUser = authResult.user ?: return@withContext Result.failure(IllegalStateException("Failed to retrieve user"))

            // Fetch user record from Firestore to verify role and account status
            val userDocRef = db.collection("users").document(fbUser.uid)
            val docSnapshot = userDocRef.get().await()

            val role: String
            val status: String
            val name: String

            if (docSnapshot.exists()) {
                role = docSnapshot.getString("role") ?: "user"
                status = docSnapshot.getString("status") ?: "active"
                name = docSnapshot.getString("name") ?: fbUser.displayName ?: email.substringBefore("@")

                if (status.equals("suspended", ignoreCase = true)) {
                    auth.signOut()
                    return@withContext Result.failure(IllegalStateException("Your account has been suspended by the administrator."))
                }
            } else {
                // First-time record initialization in Firestore
                role = "user"
                status = "active"
                name = fbUser.displayName ?: email.substringBefore("@")

                val newUserData = mapOf(
                    "uid" to fbUser.uid,
                    "name" to name,
                    "email" to email.trim(),
                    "photoUrl" to "",
                    "role" to role,
                    "status" to status,
                    "createdAt" to System.currentTimeMillis()
                )
                userDocRef.set(newUserData).await()
            }

            val profile = UserProfile(
                userId = fbUser.uid,
                name = name,
                email = email.trim(),
                role = role,
                status = status,
                isGuest = false,
                isAdmin = (role.equals("admin", ignoreCase = true))
            )
            _currentUser.value = profile
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, pass: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val auth = firebaseAuth ?: return@withContext Result.failure(IllegalStateException("Firebase Auth not initialized"))
        val db = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore not initialized"))

        try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val fbUser = authResult.user ?: return@withContext Result.failure(IllegalStateException("User creation failed"))

            // Every new registered user is created with role: "user" and status: "active"
            val newUserData = mapOf(
                "uid" to fbUser.uid,
                "name" to name.trim(),
                "email" to email.trim(),
                "photoUrl" to "",
                "role" to "user",
                "status" to "active",
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("users").document(fbUser.uid).set(newUserData).await()

            val profile = UserProfile(
                userId = fbUser.uid,
                name = name.trim(),
                email = email.trim(),
                role = "user",
                status = "active",
                isGuest = false,
                isAdmin = false
            )
            _currentUser.value = profile
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val auth = firebaseAuth ?: return@withContext Result.failure(IllegalStateException("Firebase Auth not initialized"))
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Password reset failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Admin-only user management functions
    suspend fun fetchAllUsers(): Result<List<UserAccount>> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore not initialized"))
        try {
            val snapshot = db.collection("users").get().await()
            val users = snapshot.documents.map { doc ->
                UserAccount(
                    uid = doc.getString("uid") ?: doc.id,
                    name = doc.getString("name") ?: "Community Member",
                    email = doc.getString("email") ?: "",
                    photoUrl = doc.getString("photoUrl") ?: "",
                    role = doc.getString("role") ?: "user",
                    status = doc.getString("status") ?: "active",
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
            Result.success(users)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching user list: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun toggleUserSuspension(userId: String, suspend: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore not initialized"))
        try {
            val newStatus = if (suspend) "suspended" else "active"
            db.collection("users").document(userId).update("status", newStatus).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user suspension: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserRole(userId: String, role: String): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore not initialized"))
        try {
            db.collection("users").document(userId).update("role", role).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user role: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun continueAsGuest() {
        _currentUser.value = defaultGuestProfile
    }

    fun logout() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign out error", e)
        }
        _currentUser.value = defaultGuestProfile
    }

    fun deleteAccount() {
        try {
            val user = firebaseAuth?.currentUser
            val uid = user?.uid
            user?.delete()
            if (uid != null) {
                scope.launch {
                    try {
                        firestore?.collection("users")?.document(uid)?.delete()?.await()
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Error deleting user doc", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Delete user error", e)
        }
        _currentUser.value = defaultGuestProfile
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
