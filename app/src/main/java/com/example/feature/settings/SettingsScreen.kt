package com.example.feature.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.AppTopBar
import com.example.core.preferences.AppPreferences
import com.example.data.repository.InteractionRepository
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.ui.theme.BrandOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appPreferences: AppPreferences,
    interactionRepository: InteractionRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentTheme by appPreferences.themeMode.collectAsState()
    val currentLanguage by appPreferences.language.collectAsState()
    val notificationsEnabled by appPreferences.notificationsEnabled.collectAsState()
    val autoplayEnabled by appPreferences.autoplayEnabled.collectAsState()
    val videoQuality by appPreferences.videoQuality.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings_title),
                showBack = true,
                onBackClick = onBackClick
            )
        },
        modifier = modifier.testTag("settings_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Appearance & Display
            item {
                SettingsSection(title = stringResource(R.string.appearance_section)) {
                    SettingsRow(
                        title = stringResource(R.string.theme_setting),
                        subtitle = when (currentTheme) {
                            AppThemeMode.DARK -> stringResource(R.string.theme_dark)
                            AppThemeMode.LIGHT -> stringResource(R.string.theme_light)
                            AppThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                        },
                        icon = Icons.Outlined.DarkMode,
                        onClick = { showThemeDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        title = stringResource(R.string.language_setting),
                        subtitle = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "English"
                            AppLanguage.GUJARATI -> "ગુજરાતી (Gujarati)"
                            AppLanguage.HINDI -> "हिन्दी (Hindi)"
                        },
                        icon = Icons.Outlined.Translate,
                        onClick = { showLanguageDialog = true }
                    )
                }
            }

            // Playback & Data
            item {
                SettingsSection(title = stringResource(R.string.playback_section)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayCircle,
                            contentDescription = null,
                            tint = BrandOrange
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.autoplay_setting),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Play next video automatically",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoplayEnabled,
                            onCheckedChange = { appPreferences.setAutoplayEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandOrange)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = BrandOrange
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.notifications_setting),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Receive alerts for new vlogs and shorts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { appPreferences.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandOrange)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        title = "Default Video Quality",
                        subtitle = videoQuality,
                        icon = Icons.Outlined.HighQuality,
                        onClick = { showQualityDialog = true }
                    )
                }
            }

            // Storage & Cache
            item {
                SettingsSection(title = "Storage & Cache") {
                    SettingsRow(
                        title = stringResource(R.string.cache_setting),
                        subtitle = "Clear temporary thumbnail and video cache",
                        icon = Icons.Outlined.CleaningServices,
                        onClick = {
                            Toast.makeText(context, context.getString(R.string.cache_cleared_msg), Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        title = stringResource(R.string.history_setting),
                        subtitle = "Clear your local watch progress history",
                        icon = Icons.Outlined.DeleteSweep,
                        onClick = {
                            coroutineScope.launch {
                                interactionRepository.clearWatchHistory()
                                Toast.makeText(context, context.getString(R.string.history_cleared_msg), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Legal & About
            item {
                SettingsSection(title = stringResource(R.string.about_section)) {
                    SettingsRow(
                        title = stringResource(R.string.privacy_policy),
                        icon = Icons.Outlined.Security,
                        onClick = { showPrivacyDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        title = stringResource(R.string.terms_service),
                        icon = Icons.Outlined.Description,
                        onClick = { showTermsDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        title = stringResource(R.string.contact_us),
                        icon = Icons.Outlined.Email,
                        onClick = { showContactDialog = true }
                    )
                }
            }

            // App Version Footer
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.app_version_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Theme Picker Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.theme_setting)) },
            text = {
                Column {
                    listOf(
                        Pair(AppThemeMode.DARK, stringResource(R.string.theme_dark)),
                        Pair(AppThemeMode.LIGHT, stringResource(R.string.theme_light)),
                        Pair(AppThemeMode.SYSTEM, stringResource(R.string.theme_system))
                    ).forEach { (mode, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    appPreferences.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = currentTheme == mode,
                                onClick = {
                                    appPreferences.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Language Picker Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.language_setting)) },
            text = {
                Column {
                    listOf(
                        Pair(AppLanguage.ENGLISH, "English (Default)"),
                        Pair(AppLanguage.GUJARATI, "ગુજરાતી (Gujarati)"),
                        Pair(AppLanguage.HINDI, "हिन्दी (Hindi)")
                    ).forEach { (lang, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    appPreferences.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick = {
                                    appPreferences.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Quality Picker Dialog
    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Default Video Quality") },
            text = {
                Column {
                    listOf("Auto (Recommended)", "1080p (FHD)", "720p (HD)", "480p (Data Saver)").forEach { q ->
                        val clean = q.substringBefore(" ")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    appPreferences.setVideoQuality(clean)
                                    showQualityDialog = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = videoQuality == clean,
                                onClick = {
                                    appPreferences.setVideoQuality(clean)
                                    showQualityDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(q)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.privacy_policy), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Hardik Life Vlog is dedicated to safeguarding user privacy. We do not sell personal data. Watch history and bookmarks are safely saved on your local device. Analytics are strictly anonymous and used to enhance creator video experience.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Terms of Service Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text(stringResource(R.string.terms_service), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "All video footage, photos, and music belong to Hardik Life Vlog and respective license owners. Content is for personal entertainment. Commercial re-uploading without written permission is prohibited.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Contact Us Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text(stringResource(R.string.contact_us), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("For business collaborations, brand sponsorships, or fan queries:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📧 Email: contact@hardiklifevlog.com", fontWeight = FontWeight.SemiBold)
                    Text("📸 Instagram: @hardik.lifevlog", fontWeight = FontWeight.SemiBold)
                    Text("📍 Studio: Ahmedabad, Gujarat, India", fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BrandOrange
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BrandOrange
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}
