package com.example.feature.vlogdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ContentRepository
import com.example.data.repository.InteractionRepository
import com.example.domain.model.Vlog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VlogDetailUiState(
    val vlog: Vlog? = null,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val relatedVlogs: List<Vlog> = emptyList(),
    val initialProgressSeconds: Long = 0,
    val isReported: Boolean = false,
    val isDescriptionExpanded: Boolean = false
)

class VlogDetailViewModel(
    private val vlogId: String,
    private val contentRepository: ContentRepository,
    private val interactionRepository: InteractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VlogDetailUiState())
    val uiState: StateFlow<VlogDetailUiState> = _uiState.asStateFlow()

    init {
        loadVlogDetails()
        observeBookmarkStatus()
        observeLikeStatus()
    }

    private fun loadVlogDetails() {
        val vlog = contentRepository.getVlogById(vlogId)
        if (vlog != null) {
            val related = contentRepository.getRelatedVlogs(vlogId, vlog.categoryId)
            _uiState.value = _uiState.value.copy(
                vlog = vlog,
                isLiked = contentRepository.likedVlogIds.value.contains(vlogId),
                relatedVlogs = related
            )
        }
    }

    private fun observeBookmarkStatus() {
        viewModelScope.launch {
            interactionRepository.isVlogBookmarked(vlogId).collect { isBookmarked ->
                _uiState.value = _uiState.value.copy(isBookmarked = isBookmarked)
            }
        }
    }

    private fun observeLikeStatus() {
        viewModelScope.launch {
            contentRepository.likedVlogIds.collect { likedSet ->
                _uiState.value = _uiState.value.copy(isLiked = likedSet.contains(vlogId))
            }
        }
    }

    fun toggleLike() {
        contentRepository.toggleLikeVlog(vlogId)
        val currentVlog = _uiState.value.vlog
        if (currentVlog != null) {
            val updated = contentRepository.getVlogById(vlogId)
            if (updated != null) {
                _uiState.value = _uiState.value.copy(vlog = updated)
            }
        }
    }

    fun toggleBookmark() {
        val currentVlog = _uiState.value.vlog ?: return
        viewModelScope.launch {
            interactionRepository.toggleBookmark(currentVlog)
        }
    }

    fun toggleDescriptionExpanded() {
        _uiState.value = _uiState.value.copy(
            isDescriptionExpanded = !_uiState.value.isDescriptionExpanded
        )
    }

    fun updateWatchProgress(currentSeconds: Long, totalDurationSeconds: Long) {
        val currentVlog = _uiState.value.vlog ?: return
        viewModelScope.launch {
            interactionRepository.recordWatchProgress(currentVlog, currentSeconds, totalDurationSeconds)
        }
    }

    fun reportContent() {
        _uiState.value = _uiState.value.copy(isReported = true)
    }

    class Factory(
        private val vlogId: String,
        private val contentRepository: ContentRepository,
        private val interactionRepository: InteractionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VlogDetailViewModel(vlogId, contentRepository, interactionRepository) as T
        }
    }
}
