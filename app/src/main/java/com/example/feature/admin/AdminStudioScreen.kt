package com.example.feature.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthRepository
import com.example.data.repository.ContentRepository
import com.example.domain.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudioScreen(
    authRepository: AuthRepository,
    contentRepository: ContentRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by authRepository.currentUser.collectAsState()

    // If user does not have confirmed admin role in Firestore, show secure login gate
    if (!currentUser.isAdmin) {
        AdminGateScreen(
            currentUser = currentUser,
            authRepository = authRepository,
            onBackClick = onBackClick
        )
    } else {
        AdminDashboardScreen(
            currentUser = currentUser,
            authRepository = authRepository,
            contentRepository = contentRepository,
            onBackClick = onBackClick
        )
    }
}

/**
 * Gatekeeper screen that verifies Firebase Authentication and Firestore role = "admin"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminGateScreen(
    currentUser: UserProfile,
    authRepository: AuthRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Portal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = DarkTextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BrandAmberContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = BrandAmber,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Hardik Creator Admin",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Secure Admin-Only Access • Firestore RBAC Protected",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Non-admin warning if already logged in as regular user
            if (!currentUser.isGuest && !currentUser.isAdmin) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ColorError.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorError.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ColorError, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Access Denied (Role: ${currentUser.role})", fontWeight = FontWeight.Bold, color = ColorError)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Account '${currentUser.email}' is not registered as an Administrator in Firestore. Please log in with an authorized admin account.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            errorMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorError.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = ColorError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Admin Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BrandOrange) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_email_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandOrange) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both admin email and password."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        val result = authRepository.signIn(email, password)
                        isLoading = false
                        result.fold(
                            onSuccess = { user ->
                                if (!user.isAdmin) {
                                    errorMessage = "Authenticated, but role '${user.role}' is not authorized as Admin in Cloud Firestore."
                                    Toast.makeText(context, "Access Denied: Admin role required", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Welcome Admin ${user.name}!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onFailure = { ex ->
                                errorMessage = ex.message ?: "Authentication failed. Check credentials."
                            }
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_login_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In to Admin Dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Protected by Firebase Auth and Firestore RBAC. Normal viewers cannot access creator controls.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextTertiary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Full Admin Dashboard Screen for authorized creator administrators
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboardScreen(
    currentUser: UserProfile,
    authRepository: AuthRepository,
    contentRepository: ContentRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val vlogs by contentRepository.vlogs.collectAsState()
    val shorts by contentRepository.shorts.collectAsState()
    val photos by contentRepository.photos.collectAsState()
    val categories by contentRepository.categories.collectAsState()
    val banners by contentRepository.banners.collectAsState()
    val notifications by contentRepository.notifications.collectAsState()
    val appSettings by contentRepository.appSettings.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Overview", "Vlogs", "Shorts", "Photos", "Users", "Banners", "Notify", "Settings")

    // Modals
    var showAddVlogDialog by remember { mutableStateOf(false) }
    var editingVlog by remember { mutableStateOf<Vlog?>(null) }
    var showAddShortDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var showAddBannerDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    // User management state
    var usersList by remember { mutableStateOf<List<UserAccount>>(emptyList()) }
    var isLoadingUsers by remember { mutableStateOf(false) }

    fun refreshUsers() {
        isLoadingUsers = true
        scope.launch {
            authRepository.fetchAllUsers().fold(
                onSuccess = { list ->
                    usersList = list
                    isLoadingUsers = false
                },
                onFailure = {
                    isLoadingUsers = false
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        refreshUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Creator Studio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    color = BrandAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(currentUser.email, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authRepository.logout()
                        Toast.makeText(context, "Admin logged out", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = ColorError)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = BrandOrange,
                edgePadding = 12.dp
            ) {
                tabTitles.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> AdminOverviewTab(
                    vlogs = vlogs,
                    shorts = shorts,
                    photos = photos,
                    usersCount = usersList.size,
                    onAddVlog = { showAddVlogDialog = true },
                    onAddShort = { showAddShortDialog = true },
                    onAddPhoto = { showAddPhotoDialog = true },
                    onBroadcast = { showBroadcastDialog = true },
                    onTogglePublish = { id, pub -> scope.launch { contentRepository.togglePublishVlog(id, pub) } },
                    onToggleFeatured = { id, feat -> scope.launch { contentRepository.toggleFeaturedVlog(id, feat) } },
                    onDeleteVlog = { id -> scope.launch { contentRepository.deleteVlog(id) } }
                )
                1 -> AdminVlogsTab(
                    vlogs = vlogs,
                    categories = categories,
                    onAddVlog = { showAddVlogDialog = true },
                    onEditVlog = { vlog -> editingVlog = vlog },
                    onDeleteVlog = { id -> scope.launch { contentRepository.deleteVlog(id) } },
                    onTogglePublish = { id, pub -> scope.launch { contentRepository.togglePublishVlog(id, pub) } },
                    onToggleFeatured = { id, feat -> scope.launch { contentRepository.toggleFeaturedVlog(id, feat) } }
                )
                2 -> AdminShortsTab(
                    shorts = shorts,
                    onAddShort = { showAddShortDialog = true },
                    onDeleteShort = { id -> scope.launch { contentRepository.deleteShort(id) } },
                    onTogglePublish = { id, pub -> scope.launch { contentRepository.togglePublishShort(id, pub) } }
                )
                3 -> AdminPhotosTab(
                    photos = photos,
                    onAddPhoto = { showAddPhotoDialog = true },
                    onDeletePhoto = { id -> scope.launch { contentRepository.deletePhoto(id) } }
                )
                4 -> AdminUsersTab(
                    users = usersList,
                    isLoading = isLoadingUsers,
                    onRefresh = { refreshUsers() },
                    onToggleSuspension = { uid, susp ->
                        scope.launch {
                            authRepository.toggleUserSuspension(uid, susp)
                            refreshUsers()
                        }
                    },
                    onToggleRole = { uid, role ->
                        scope.launch {
                            authRepository.updateUserRole(uid, role)
                            refreshUsers()
                        }
                    }
                )
                5 -> AdminCategoriesAndBannersTab(
                    categories = categories,
                    banners = banners,
                    onAddCategory = { showAddCategoryDialog = true },
                    onDeleteCategory = { id -> scope.launch { contentRepository.deleteCategory(id) } },
                    onAddBanner = { showAddBannerDialog = true },
                    onToggleBanner = { id, act -> scope.launch { contentRepository.toggleBannerActive(id, act) } },
                    onDeleteBanner = { id -> scope.launch { contentRepository.deleteBanner(id) } }
                )
                6 -> AdminNotificationsTab(
                    notifications = notifications,
                    onBroadcast = { showBroadcastDialog = true },
                    onDeleteNotification = { id -> scope.launch { contentRepository.deleteNotification(id) } }
                )
                7 -> AdminSettingsTab(
                    settings = appSettings,
                    onSaveSettings = { updated ->
                        scope.launch {
                            contentRepository.updateAppSettings(updated)
                            Toast.makeText(context, "Settings saved to Firestore", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    // Dialogs
    if (showAddVlogDialog || editingVlog != null) {
        VlogFormDialog(
            vlogToEdit = editingVlog,
            categories = categories,
            onDismiss = {
                showAddVlogDialog = false
                editingVlog = null
            },
            onSave = { newOrUpdatedVlog ->
                scope.launch {
                    if (editingVlog != null) {
                        contentRepository.updateVlog(newOrUpdatedVlog)
                        Toast.makeText(context, "Vlog updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        contentRepository.addVlog(newOrUpdatedVlog)
                        Toast.makeText(context, "Vlog published to Firestore", Toast.LENGTH_SHORT).show()
                    }
                    showAddVlogDialog = false
                    editingVlog = null
                }
            }
        )
    }

    if (showAddShortDialog) {
        ShortFormDialog(
            onDismiss = { showAddShortDialog = false },
            onSave = { newShort ->
                scope.launch {
                    contentRepository.addShort(newShort)
                    Toast.makeText(context, "Short added to Firestore", Toast.LENGTH_SHORT).show()
                    showAddShortDialog = false
                }
            }
        )
    }

    if (showAddPhotoDialog) {
        PhotoFormDialog(
            categories = categories,
            onDismiss = { showAddPhotoDialog = false },
            onSave = { newPhoto ->
                scope.launch {
                    contentRepository.addPhoto(newPhoto)
                    Toast.makeText(context, "Photo published to Firestore", Toast.LENGTH_SHORT).show()
                    showAddPhotoDialog = false
                }
            }
        )
    }

    if (showAddCategoryDialog) {
        CategoryFormDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSave = { newCategory ->
                scope.launch {
                    contentRepository.addCategory(newCategory)
                    Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show()
                    showAddCategoryDialog = false
                }
            }
        )
    }

    if (showAddBannerDialog) {
        BannerFormDialog(
            onDismiss = { showAddBannerDialog = false },
            onSave = { newBanner ->
                scope.launch {
                    contentRepository.addBanner(newBanner)
                    Toast.makeText(context, "Banner saved to Firestore", Toast.LENGTH_SHORT).show()
                    showAddBannerDialog = false
                }
            }
        )
    }

    if (showBroadcastDialog) {
        BroadcastNotificationDialog(
            onDismiss = { showBroadcastDialog = false },
            onBroadcast = { title, message, targetType, targetId ->
                scope.launch {
                    contentRepository.broadcastNotification(title, message, targetType, targetId, currentUser.email)
                    Toast.makeText(context, "Notification broadcasted", Toast.LENGTH_SHORT).show()
                    showBroadcastDialog = false
                }
            }
        )
    }
}

// ==========================================
// TAB 0: OVERVIEW & KPIS
// ==========================================
@Composable
private fun AdminOverviewTab(
    vlogs: List<Vlog>,
    shorts: List<ShortVideo>,
    photos: List<GalleryPhoto>,
    usersCount: Int,
    onAddVlog: () -> Unit,
    onAddShort: () -> Unit,
    onAddPhoto: () -> Unit,
    onBroadcast: () -> Unit,
    onTogglePublish: (String, Boolean) -> Unit,
    onToggleFeatured: (String, Boolean) -> Unit,
    onDeleteVlog: (String) -> Unit
) {
    val publishedVlogs = vlogs.count { it.status == "published" }
    val draftVlogs = vlogs.count { it.status != "published" }
    val totalViews = vlogs.sumOf { it.views } + shorts.sumOf { it.views }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // KPI Metric Cards Grid
        item {
            Text("Platform Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Total Vlogs", value = "${vlogs.size}", icon = Icons.Default.VideoLibrary, color = BrandOrange, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Published", value = "$publishedVlogs", icon = Icons.Default.CheckCircle, color = ColorSuccess, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Drafts", value = "$draftVlogs", icon = Icons.Default.EditNote, color = ColorWarning, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Total Users", value = "$usersCount", icon = Icons.Default.People, color = BrandAmber, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Total Views", value = formatMetricNumber(totalViews), icon = Icons.Default.Visibility, color = ColorInfo, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Shorts", value = "${shorts.size}", icon = Icons.Default.SlowMotionVideo, color = BrandOrangeLight, modifier = Modifier.weight(1f))
                }
            }
        }

        // Quick Action Buttons
        item {
            Text("Quick Creator Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddVlog,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Vlog", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAddShort,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Short", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAddPhoto,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onBroadcast,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Broadcast", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        // Recent Uploads
        item {
            Text("Recent Uploads", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(vlogs.take(5), key = { it.id }) { vlog ->
            VlogAdminItemCard(
                vlog = vlog,
                onEdit = null,
                onDelete = { onDeleteVlog(vlog.id) },
                onTogglePublish = { pub -> onTogglePublish(vlog.id, pub) },
                onToggleFeatured = { feat -> onToggleFeatured(vlog.id, feat) }
            )
        }
    }
}

// ==========================================
// TAB 1: MANAGE VLOGS
// ==========================================
@Composable
private fun AdminVlogsTab(
    vlogs: List<Vlog>,
    categories: List<ContentCategory>,
    onAddVlog: () -> Unit,
    onEditVlog: (Vlog) -> Unit,
    onDeleteVlog: (String) -> Unit,
    onTogglePublish: (String, Boolean) -> Unit,
    onToggleFeatured: (String, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Vlog Catalog (${vlogs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Manage titles, videos, publish status & featured flags", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                }
                Button(
                    onClick = onAddVlog,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Vlog")
                }
            }
        }

        items(vlogs, key = { it.id }) { vlog ->
            VlogAdminItemCard(
                vlog = vlog,
                onEdit = { onEditVlog(vlog) },
                onDelete = { onDeleteVlog(vlog.id) },
                onTogglePublish = { pub -> onTogglePublish(vlog.id, pub) },
                onToggleFeatured = { feat -> onToggleFeatured(vlog.id, feat) }
            )
        }
    }
}

@Composable
private fun VlogAdminItemCard(
    vlog: Vlog,
    onEdit: (() -> Unit)?,
    onDelete: () -> Unit,
    onTogglePublish: (Boolean) -> Unit,
    onToggleFeatured: (Boolean) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (vlog.status == "published") ColorSuccess.copy(alpha = 0.2f) else ColorWarning.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (vlog.status == "published") "PUBLISHED" else "DRAFT",
                                color = if (vlog.status == "published") ColorSuccess else ColorWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (vlog.isFeatured) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = BrandAmber.copy(alpha = 0.2f)) {
                                Text(
                                    text = "FEATURED",
                                    color = BrandAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(vlog.categoryName, style = MaterialTheme.typography.bodySmall, color = DarkTextTertiary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(vlog.title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${formatMetricNumber(vlog.views)} views • ${formatMetricNumber(vlog.likes)} likes • ${vlog.duration}", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                }

                if (onEdit != null) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandOrange)
                    }
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorError)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DarkBorderSubtle)

            // Action toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = vlog.status == "published",
                        onCheckedChange = onTogglePublish,
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorSuccess, checkedTrackColor = ColorSuccess.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Published", style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = vlog.isFeatured,
                        onCheckedChange = onToggleFeatured,
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandAmber, checkedTrackColor = BrandAmber.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Featured", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Vlog?") },
            text = { Text("Are you sure you want to delete '${vlog.title}' from Cloud Firestore?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Delete", color = ColorError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

// ==========================================
// TAB 2: SHORTS MANAGEMENT
// ==========================================
@Composable
private fun AdminShortsTab(
    shorts: List<ShortVideo>,
    onAddShort: () -> Unit,
    onDeleteShort: (String) -> Unit,
    onTogglePublish: (String, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Shorts (${shorts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onAddShort,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Short")
                }
            }
        }

        items(shorts, key = { it.id }) { short ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(short.title, fontWeight = FontWeight.Bold, maxLines = 2)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${formatMetricNumber(short.views)} views • ${formatMetricNumber(short.likes)} likes", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                    }
                    Switch(
                        checked = short.status == "published",
                        onCheckedChange = { pub -> onTogglePublish(short.id, pub) }
                    )
                    IconButton(onClick = { onDeleteShort(short.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorError)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: PHOTOS MANAGEMENT
// ==========================================
@Composable
private fun AdminPhotosTab(
    photos: List<GalleryPhoto>,
    onAddPhoto: () -> Unit,
    onDeletePhoto: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gallery Photos (${photos.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onAddPhoto,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo")
                }
            }
        }

        items(photos, key = { it.id }) { photo ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(photo.caption, fontWeight = FontWeight.Bold, maxLines = 2)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${photo.categoryName} • ${photo.location}", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                    }
                    IconButton(onClick = { onDeletePhoto(photo.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorError)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 4: USERS & SECURITY MANAGEMENT
// ==========================================
@Composable
private fun AdminUsersTab(
    users: List<UserAccount>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onToggleSuspension: (String, Boolean) -> Unit,
    onToggleRole: (String, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("User Directory (${users.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Manage roles, accounts and suspension status", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = BrandOrange)
                }
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            }
        } else if (users.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No registered users recorded in Firestore yet.", style = MaterialTheme.typography.bodyMedium, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(onClick = onRefresh) { Text("Reload Directory") }
                    }
                }
            }
        } else {
            items(users, key = { it.uid }) { user ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (user.role == "admin") BrandAmber else DarkSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.role == "admin") Color.Black else DarkTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.name, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (user.role == "admin") BrandAmber.copy(alpha = 0.2f) else DarkSurfaceVariant
                                    ) {
                                        Text(
                                            text = user.role.uppercase(),
                                            color = if (user.role == "admin") BrandAmber else DarkTextSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(user.email, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                            }

                            // Status badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (user.status == "active") ColorSuccess.copy(alpha = 0.2f) else ColorError.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = user.status.uppercase(),
                                    color = if (user.status == "active") ColorSuccess else ColorError,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DarkBorderSubtle)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            if (user.status == "active") {
                                OutlinedButton(
                                    onClick = { onToggleSuspension(user.uid, true) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorError)
                                ) {
                                    Text("Suspend", fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { onToggleSuspension(user.uid, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess)
                                ) {
                                    Text("Unsuspend", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (user.role == "admin") {
                                OutlinedButton(onClick = { onToggleRole(user.uid, "user") }) {
                                    Text("Demote to User", fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { onToggleRole(user.uid, "admin") },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                                ) {
                                    Text("Grant Admin", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 5: CATEGORIES & BANNERS
// ==========================================
@Composable
private fun AdminCategoriesAndBannersTab(
    categories: List<ContentCategory>,
    banners: List<PromoBanner>,
    onAddCategory: () -> Unit,
    onDeleteCategory: (String) -> Unit,
    onAddBanner: () -> Unit,
    onToggleBanner: (String, Boolean) -> Unit,
    onDeleteBanner: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Banners
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Promo Banners (${banners.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onAddBanner,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Banner")
                }
            }
        }

        if (banners.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No promotional banners created yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(banners, key = { it.id }) { banner ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(banner.title, fontWeight = FontWeight.Bold)
                            Text(banner.subtitle, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                        }
                        Switch(
                            checked = banner.isActive,
                            onCheckedChange = { onToggleBanner(banner.id, it) }
                        )
                        IconButton(onClick = { onDeleteBanner(banner.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorError)
                        }
                    }
                }
            }
        }

        // Categories
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Content Categories (${categories.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onAddCategory,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Category")
                }
            }
        }

        items(categories, key = { it.id }) { cat ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(cat.name, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { onDeleteCategory(cat.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorError)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 6: NOTIFICATIONS
// ==========================================
@Composable
private fun AdminNotificationsTab(
    notifications: List<AppNotification>,
    onBroadcast: () -> Unit,
    onDeleteNotification: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Broadcast Alerts (${notifications.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Send alerts directly to viewers' notification inboxes", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                }
                Button(
                    onClick = onBroadcast,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Alert", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(notifications, key = { it.id }) { notif ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(notif.title, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(notif.message, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                    }
                    IconButton(onClick = { onDeleteNotification(notif.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorError)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 7: CREATOR PROFILE & APP SETTINGS
// ==========================================
@Composable
private fun AdminSettingsTab(
    settings: GlobalAppSettings,
    onSaveSettings: (GlobalAppSettings) -> Unit
) {
    var creatorName by remember { mutableStateOf(settings.creatorName) }
    var creatorBio by remember { mutableStateOf(settings.creatorBio) }
    var creatorSubscribers by remember { mutableStateOf(settings.creatorSubscribers) }
    var maintenanceMode by remember { mutableStateOf(settings.maintenanceMode) }
    var maintenanceMsg by remember { mutableStateOf(settings.maintenanceMessage) }
    var supportEmail by remember { mutableStateOf(settings.supportEmail) }
    var termsUrl by remember { mutableStateOf(settings.termsUrl) }
    var privacyUrl by remember { mutableStateOf(settings.privacyUrl) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Creator Profile Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = creatorName,
                onValueChange = { creatorName = it },
                label = { Text("Creator Display Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = creatorBio,
                onValueChange = { creatorBio = it },
                label = { Text("Creator Bio & Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = creatorSubscribers,
                onValueChange = { creatorSubscribers = it },
                label = { Text("Subscriber Count Tag") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text("System & Maintenance Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Maintenance Mode", fontWeight = FontWeight.Bold)
                        Text("Puts user app in temporary maintenance state", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                    }
                    Switch(checked = maintenanceMode, onCheckedChange = { maintenanceMode = it })
                }
            }
            if (maintenanceMode) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = maintenanceMsg,
                    onValueChange = { maintenanceMsg = it },
                    label = { Text("Maintenance Message") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Text("App Contact & Legal URLs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = supportEmail,
                onValueChange = { supportEmail = it },
                label = { Text("Support Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = termsUrl,
                onValueChange = { termsUrl = it },
                label = { Text("Terms of Service URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = privacyUrl,
                onValueChange = { privacyUrl = it },
                label = { Text("Privacy Policy URL") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    onSaveSettings(
                        settings.copy(
                            creatorName = creatorName,
                            creatorBio = creatorBio,
                            creatorSubscribers = creatorSubscribers,
                            maintenanceMode = maintenanceMode,
                            maintenanceMessage = maintenanceMsg,
                            supportEmail = supportEmail,
                            termsUrl = termsUrl,
                            privacyUrl = privacyUrl
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save All Settings to Firestore", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// FORMS / DIALOGS
// ==========================================

@Composable
private fun VlogFormDialog(
    vlogToEdit: Vlog?,
    categories: List<ContentCategory>,
    onDismiss: () -> Unit,
    onSave: (Vlog) -> Unit
) {
    var title by remember { mutableStateOf(vlogToEdit?.title ?: "") }
    var desc by remember { mutableStateOf(vlogToEdit?.description ?: "") }
    var thumb by remember { mutableStateOf(vlogToEdit?.thumbnailUrl ?: "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1200&q=80") }
    var videoUrl by remember { mutableStateOf(vlogToEdit?.videoUrl ?: "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    var categoryId by remember { mutableStateOf(vlogToEdit?.categoryId ?: "cat_travel") }
    var categoryName by remember { mutableStateOf(vlogToEdit?.categoryName ?: "Travel") }
    var duration by remember { mutableStateOf(vlogToEdit?.duration ?: "15:00") }
    var isFeatured by remember { mutableStateOf(vlogToEdit?.isFeatured ?: false) }
    var isPublished by remember { mutableStateOf(vlogToEdit?.status != "draft") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (vlogToEdit != null) "Edit Vlog" else "Add New Vlog", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = thumb, onValueChange = { thumb = it }, label = { Text("Thumbnail URL") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video Stream URL") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (e.g. 18:45)") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Published")
                        Switch(checked = isPublished, onCheckedChange = { isPublished = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Featured")
                        Switch(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val vlog = Vlog(
                        id = vlogToEdit?.id ?: "vlog_${System.currentTimeMillis()}",
                        title = title.trim(),
                        description = desc.trim(),
                        thumbnailUrl = thumb.trim(),
                        videoUrl = videoUrl.trim(),
                        categoryId = categoryId,
                        categoryName = categoryName,
                        publishedAt = vlogToEdit?.publishedAt ?: "Just now",
                        timestamp = vlogToEdit?.timestamp ?: System.currentTimeMillis(),
                        duration = duration.trim(),
                        isFeatured = isFeatured,
                        status = if (isPublished) "published" else "draft"
                    )
                    onSave(vlog)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ShortFormDialog(
    onDismiss: () -> Unit,
    onSave: (ShortVideo) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4") }
    var thumb by remember { mutableStateOf("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&q=80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Short", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Short Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = thumb, onValueChange = { thumb = it }, label = { Text("Thumbnail URL") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    onSave(
                        ShortVideo(
                            id = "short_${System.currentTimeMillis()}",
                            title = title.trim(),
                            videoUrl = videoUrl.trim(),
                            thumbnailUrl = thumb.trim(),
                            publishedAt = "Just now",
                            status = "published"
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("Add Short")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun PhotoFormDialog(
    categories: List<ContentCategory>,
    onDismiss: () -> Unit,
    onSave: (GalleryPhoto) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1200&q=80") }
    var location by remember { mutableStateOf("Ahmedabad, Gujarat") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Gallery Photo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Caption") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (caption.isBlank()) return@Button
                    onSave(
                        GalleryPhoto(
                            id = "photo_${System.currentTimeMillis()}",
                            imageUrl = imageUrl.trim(),
                            caption = caption.trim(),
                            categoryId = "cat_travel",
                            categoryName = "Travel",
                            publishedAt = "Just now",
                            location = location.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("Add Photo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CategoryFormDialog(
    onDismiss: () -> Unit,
    onSave: (ContentCategory) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Category Name") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    onSave(
                        ContentCategory(
                            id = "cat_${name.lowercase().replace(" ", "_")}",
                            name = name.trim(),
                            iconName = "folder",
                            count = 0
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun BannerFormDialog(
    onDismiss: () -> Unit,
    onSave: (PromoBanner) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1200&q=80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Promo Banner", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    onSave(
                        PromoBanner(
                            id = "banner_${System.currentTimeMillis()}",
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            imageUrl = imageUrl.trim(),
                            targetType = "vlog",
                            targetId = "",
                            isActive = true
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("Add Banner")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun BroadcastNotificationDialog(
    onDismiss: () -> Unit,
    onBroadcast: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var targetType by remember { mutableStateOf("vlog") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Broadcast Notification Alert", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notification Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message Body") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || message.isBlank()) return@Button
                    onBroadcast(title.trim(), message.trim(), targetType, "")
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
            ) {
                Text("Broadcast Now", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = DarkTextPrimary)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
        }
    }
}

private fun formatMetricNumber(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format("%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
