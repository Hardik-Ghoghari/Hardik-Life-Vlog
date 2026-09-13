package com.example.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ContentRepository
import com.example.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredVlog: Vlog? = null,
    val latestVlogs: List<Vlog> = emptyList(),
    val popularVlogs: List<Vlog> = emptyList(),
    val shorts: List<ShortVideo> = emptyList(),
    val photos: List<GalleryPhoto> = emptyList(),
    val socialLinks: List<SocialPlatformLink> = emptyList(),
    val unreadNotificationsCount: Int = 0,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                contentRepository.vlogs.collect { vlogs ->
                    val featured = vlogs.find { it.isFeatured } ?: vlogs.firstOrNull()
                    val latest = vlogs.sortedByDescending { it.timestamp }
                    val popular = vlogs.sortedByDescending { it.views }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        featuredVlog = featured,
                        latestVlogs = latest,
                        popularVlogs = popular,
                        shorts = contentRepository.shorts.value,
                        photos = contentRepository.photos.value,
                        socialLinks = contentRepository.socialLinks.value,
                        unreadNotificationsCount = contentRepository.notifications.value.count { !it.isRead }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load content"
                )
            }
        }
    }

    class Factory(private val contentRepository: ContentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(contentRepository) as T
        }
    }
}
