package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.core.database.AppDatabase
import com.example.core.preferences.AppPreferences
import com.example.data.repository.AuthRepository
import com.example.data.repository.ContentRepository
import com.example.data.repository.InteractionRepository
import com.example.domain.model.AppThemeMode
import com.example.navigation.HardikVlogApp
import com.example.ui.theme.HardikVlogTheme

class MainActivity : ComponentActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var appPreferences: AppPreferences
    private lateinit var contentRepository: ContentRepository
    private lateinit var interactionRepository: InteractionRepository
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize core Singletons
        appDatabase = AppDatabase.getInstance(applicationContext)
        appPreferences = AppPreferences.getInstance(applicationContext)
        contentRepository = ContentRepository.getInstance(applicationContext, appDatabase)
        interactionRepository = InteractionRepository.getInstance(appDatabase)
        authRepository = AuthRepository.getInstance(applicationContext)

        setContent {
            val themeMode by appPreferences.themeMode.collectAsState()
            val isDark = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            HardikVlogTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HardikVlogApp(
                        contentRepository = contentRepository,
                        interactionRepository = interactionRepository,
                        authRepository = authRepository,
                        appPreferences = appPreferences
                    )
                }
            }
        }
    }
}

