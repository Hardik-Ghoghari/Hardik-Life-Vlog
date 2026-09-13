package com.example.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.core.designsystem.AppBottomNavigationBar
import com.example.core.preferences.AppPreferences
import com.example.data.repository.AuthRepository
import com.example.data.repository.ContentRepository
import com.example.data.repository.InteractionRepository
import com.example.feature.admin.AdminStudioScreen
import com.example.feature.auth.AuthScreen
import com.example.feature.gallery.GalleryScreen
import com.example.feature.gallery.GalleryViewModel
import com.example.feature.history.WatchHistoryScreen
import com.example.feature.home.HomeScreen
import com.example.feature.home.HomeViewModel
import com.example.feature.notifications.NotificationsScreen
import com.example.feature.profile.ProfileScreen
import com.example.feature.profile.ProfileViewModel
import com.example.feature.saved.SavedVlogsScreen
import com.example.feature.search.SearchScreen
import com.example.feature.search.SearchViewModel
import com.example.feature.settings.SettingsScreen
import com.example.feature.shorts.ShortsScreen
import com.example.feature.shorts.ShortsViewModel
import com.example.feature.vlogdetail.VlogDetailScreen
import com.example.feature.vlogdetail.VlogDetailViewModel
import com.example.feature.vlogs.VlogsScreen
import com.example.feature.vlogs.VlogsViewModel

object AppRoutes {
    const val HOME = "home"
    const val VLOGS = "vlogs"
    const val SHORTS = "shorts"
    const val SHORTS_WITH_ID = "shorts?shortId={shortId}"
    const val GALLERY = "gallery"
    const val PROFILE = "profile"
    const val VLOG_DETAIL = "vlog_detail/{vlogId}"
    const val SEARCH = "search"
    const val SAVED_VLOGS = "saved_vlogs"
    const val WATCH_HISTORY = "watch_history"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"
    const val ADMIN_STUDIO = "admin_studio"
    const val AUTH = "auth"
}

@Composable
fun HardikVlogApp(
    contentRepository: ContentRepository,
    interactionRepository: InteractionRepository,
    authRepository: AuthRepository,
    appPreferences: AppPreferences
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Top-level destinations that show BottomNavigationBar
    val isTopLevelDestination = currentRoute in listOf(
        AppRoutes.HOME,
        AppRoutes.VLOGS,
        AppRoutes.SHORTS,
        AppRoutes.SHORTS_WITH_ID,
        AppRoutes.GALLERY,
        AppRoutes.PROFILE
    )

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                val activeTab = when {
                    currentRoute?.startsWith(AppRoutes.SHORTS) == true -> AppRoutes.SHORTS
                    currentRoute != null -> currentRoute
                    else -> AppRoutes.HOME
                }
                AppBottomNavigationBar(
                    currentRoute = activeTab,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(AppRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home Destination
            composable(AppRoutes.HOME) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(contentRepository)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToVlogDetail = { vlogId ->
                        navController.navigate("vlog_detail/$vlogId")
                    },
                    onNavigateToShorts = { shortId ->
                        if (shortId != null) {
                            navController.navigate("shorts?shortId=$shortId")
                        } else {
                            navController.navigate(AppRoutes.SHORTS)
                        }
                    },
                    onNavigateToGallery = {
                        navController.navigate(AppRoutes.GALLERY)
                    },
                    onNavigateToSearch = {
                        navController.navigate(AppRoutes.SEARCH)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(AppRoutes.NOTIFICATIONS)
                    },
                    onNavigateToVlogsList = {
                        navController.navigate(AppRoutes.VLOGS)
                    }
                )
            }

            // Vlogs Destination
            composable(AppRoutes.VLOGS) {
                val vlogsViewModel: VlogsViewModel = viewModel(
                    factory = VlogsViewModel.Factory(contentRepository, interactionRepository)
                )
                VlogsScreen(
                    viewModel = vlogsViewModel,
                    onNavigateToVlogDetail = { vlogId ->
                        navController.navigate("vlog_detail/$vlogId")
                    },
                    onNavigateToSearch = {
                        navController.navigate(AppRoutes.SEARCH)
                    }
                )
            }

            // Shorts Destination (with optional shortId)
            composable(
                route = AppRoutes.SHORTS_WITH_ID,
                arguments = listOf(
                    navArgument("shortId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                val shortId = entry.arguments?.getString("shortId")
                val shortsViewModel: ShortsViewModel = viewModel(
                    key = "shorts_$shortId",
                    factory = ShortsViewModel.Factory(contentRepository, shortId)
                )
                ShortsScreen(viewModel = shortsViewModel)
            }

            composable(AppRoutes.SHORTS) {
                val shortsViewModel: ShortsViewModel = viewModel(
                    key = "shorts_default",
                    factory = ShortsViewModel.Factory(contentRepository, null)
                )
                ShortsScreen(viewModel = shortsViewModel)
            }

            // Gallery Destination
            composable(AppRoutes.GALLERY) {
                val galleryViewModel: GalleryViewModel = viewModel(
                    factory = GalleryViewModel.Factory(contentRepository)
                )
                GalleryScreen(viewModel = galleryViewModel)
            }

            // Profile Destination
            composable(AppRoutes.PROFILE) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory(authRepository, contentRepository, interactionRepository)
                )
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToSaved = { navController.navigate(AppRoutes.SAVED_VLOGS) },
                    onNavigateToHistory = { navController.navigate(AppRoutes.WATCH_HISTORY) },
                    onNavigateToNotifications = { navController.navigate(AppRoutes.NOTIFICATIONS) },
                    onNavigateToSettings = { navController.navigate(AppRoutes.SETTINGS) },
                    onNavigateToAdmin = { navController.navigate(AppRoutes.ADMIN_STUDIO) },
                    onNavigateToAuth = { navController.navigate(AppRoutes.AUTH) }
                )
            }

            // Vlog Detail Destination (Deep Link Support)
            composable(
                route = AppRoutes.VLOG_DETAIL,
                arguments = listOf(
                    navArgument("vlogId") { type = NavType.StringType }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "hardiklifevlog://vlog/{vlogId}" }
                )
            ) { backStackEntry ->
                val vlogId = backStackEntry.arguments?.getString("vlogId") ?: ""
                val vlogDetailViewModel: VlogDetailViewModel = viewModel(
                    key = "detail_$vlogId",
                    factory = VlogDetailViewModel.Factory(vlogId, contentRepository, interactionRepository)
                )
                VlogDetailScreen(
                    viewModel = vlogDetailViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToVlogDetail = { newVlogId ->
                        navController.navigate("vlog_detail/$newVlogId")
                    }
                )
            }

            // Search Destination
            composable(AppRoutes.SEARCH) {
                val searchViewModel: SearchViewModel = viewModel(
                    factory = SearchViewModel.Factory(contentRepository)
                )
                SearchScreen(
                    viewModel = searchViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToVlogDetail = { vlogId ->
                        navController.navigate("vlog_detail/$vlogId")
                    },
                    onNavigateToShorts = { shortId ->
                        navController.navigate("shorts?shortId=$shortId")
                    }
                )
            }

            // Saved Vlogs Destination
            composable(AppRoutes.SAVED_VLOGS) {
                SavedVlogsScreen(
                    interactionRepository = interactionRepository,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToVlogDetail = { vlogId ->
                        navController.navigate("vlog_detail/$vlogId")
                    }
                )
            }

            // Watch History Destination
            composable(AppRoutes.WATCH_HISTORY) {
                WatchHistoryScreen(
                    interactionRepository = interactionRepository,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToVlogDetail = { vlogId ->
                        navController.navigate("vlog_detail/$vlogId")
                    }
                )
            }

            // Notifications Destination
            composable(AppRoutes.NOTIFICATIONS) {
                NotificationsScreen(
                    contentRepository = contentRepository,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToVlogDetail = { vlogId ->
                        navController.navigate("vlog_detail/$vlogId")
                    },
                    onNavigateToShorts = { shortId ->
                        navController.navigate("shorts?shortId=$shortId")
                    },
                    onNavigateToGallery = {
                        navController.navigate(AppRoutes.GALLERY)
                    }
                )
            }

            // Settings Destination
            composable(AppRoutes.SETTINGS) {
                SettingsScreen(
                    appPreferences = appPreferences,
                    interactionRepository = interactionRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Creator Admin Studio Destination
            composable(AppRoutes.ADMIN_STUDIO) {
                AdminStudioScreen(
                    authRepository = authRepository,
                    contentRepository = contentRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Auth Destination
            composable(AppRoutes.AUTH) {
                AuthScreen(
                    authRepository = authRepository,
                    onAuthSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
