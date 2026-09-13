package com.example.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("hardik_vlog_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getSavedThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(getSavedLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _autoplayEnabled = MutableStateFlow(prefs.getBoolean(KEY_AUTOPLAY, true))
    val autoplayEnabled: StateFlow<Boolean> = _autoplayEnabled.asStateFlow()

    private val _videoQuality = MutableStateFlow(prefs.getString(KEY_VIDEO_QUALITY, "Auto") ?: "Auto")
    val videoQuality: StateFlow<String> = _videoQuality.asStateFlow()

    private fun getSavedThemeMode(): AppThemeMode {
        return when (prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name)) {
            AppThemeMode.LIGHT.name -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM.name -> AppThemeMode.SYSTEM
            else -> AppThemeMode.DARK // Default cinematic dark theme
        }
    }

    private fun getSavedLanguage(): AppLanguage {
        return when (prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.name)) {
            AppLanguage.GUJARATI.name -> AppLanguage.GUJARATI
            AppLanguage.HINDI.name -> AppLanguage.HINDI
            else -> AppLanguage.ENGLISH
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.name).apply()
        _language.value = lang
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun setAutoplayEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTOPLAY, enabled).apply()
        _autoplayEnabled.value = enabled
    }

    fun setVideoQuality(quality: String) {
        prefs.edit().putString(KEY_VIDEO_QUALITY, quality).apply()
        _videoQuality.value = quality
    }

    fun clearUserData() {
        prefs.edit()
            .remove(KEY_NOTIFICATIONS)
            .remove(KEY_AUTOPLAY)
            .apply()
    }

    companion object {
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_LANGUAGE = "pref_language"
        private const val KEY_NOTIFICATIONS = "pref_notifications"
        private const val KEY_AUTOPLAY = "pref_autoplay"
        private const val KEY_VIDEO_QUALITY = "pref_video_quality"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreferences(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
