package com.example.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.ContentRepository
import com.example.data.repository.InteractionRepository
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val totalVlogs: Int = 0,
    val totalShorts: Int = 0,
    val savedCount: Int = 0,
    val historyCount: Int = 0
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val contentRepository: ContentRepository,
    private val interactionRepository: InteractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(userProfile = user)
            }
        }

        viewModelScope.launch {
            contentRepository.vlogs.collect { vlogs ->
                _uiState.value = _uiState.value.copy(totalVlogs = vlogs.size)
            }
        }

        viewModelScope.launch {
            contentRepository.shorts.collect { shorts ->
                _uiState.value = _uiState.value.copy(totalShorts = shorts.size)
            }
        }

        viewModelScope.launch {
            interactionRepository.bookmarks.collect { bookmarks ->
                _uiState.value = _uiState.value.copy(savedCount = bookmarks.size)
            }
        }

        viewModelScope.launch {
            interactionRepository.watchHistory.collect { history ->
                _uiState.value = _uiState.value.copy(historyCount = history.size)
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun deleteAccount() {
        authRepository.deleteAccount()
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val contentRepository: ContentRepository,
        private val interactionRepository: InteractionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(authRepository, contentRepository, interactionRepository) as T
        }
    }
}
