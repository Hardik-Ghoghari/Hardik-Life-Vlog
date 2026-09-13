package com.example.feature.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ContentRepository
import com.example.domain.model.ShortVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShortsUiState(
    val shorts: List<ShortVideo> = emptyList(),
    val isMuted: Boolean = false,
    val likedShortIds: Set<String> = emptySet(),
    val initialShortId: String? = null
)

class ShortsViewModel(
    private val contentRepository: ContentRepository,
    initialShortId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShortsUiState(initialShortId = initialShortId))
    val uiState: StateFlow<ShortsUiState> = _uiState.asStateFlow()

    init {
        loadShorts()
    }

    private fun loadShorts() {
        viewModelScope.launch {
            contentRepository.shorts.collect { list ->
                _uiState.value = _uiState.value.copy(
                    shorts = list,
                    likedShortIds = contentRepository.likedShortIds.value
                )
            }
        }
    }

    fun toggleLike(shortId: String) {
        contentRepository.toggleLikeShort(shortId)
        _uiState.value = _uiState.value.copy(
            likedShortIds = contentRepository.likedShortIds.value
        )
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
    }

    class Factory(
        private val contentRepository: ContentRepository,
        private val initialShortId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShortsViewModel(contentRepository, initialShortId) as T
        }
    }
}
