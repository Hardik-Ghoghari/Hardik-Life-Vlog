package com.example.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ContentRepository
import com.example.domain.model.GalleryPhoto
import com.example.domain.model.ShortVideo
import com.example.domain.model.Vlog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedTab: Int = 0, // 0: All, 1: Vlogs, 2: Shorts, 3: Photos
    val matchedVlogs: List<Vlog> = emptyList(),
    val matchedShorts: List<ShortVideo> = emptyList(),
    val matchedPhotos: List<GalleryPhoto> = emptyList(),
    val recentSearches: List<String> = listOf("Tokyo", "Ladakh", "Ahmedabad", "Food Tour", "Studio Setup")
)

class SearchViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateQuery(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        performSearch(newQuery)
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun selectRecentSearch(term: String) {
        updateQuery(term)
    }

    fun clearRecentSearches() {
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                matchedVlogs = emptyList(),
                matchedShorts = emptyList(),
                matchedPhotos = emptyList()
            )
            return
        }

        val q = query.trim().lowercase()

        val vlogs = contentRepository.vlogs.value.filter {
            it.title.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.categoryName.lowercase().contains(q)
        }

        val shorts = contentRepository.shorts.value.filter {
            it.title.lowercase().contains(q) ||
                    it.soundTitle.lowercase().contains(q)
        }

        val photos = contentRepository.photos.value.filter {
            it.caption.lowercase().contains(q) ||
                    it.location.lowercase().contains(q) ||
                    it.categoryName.lowercase().contains(q)
        }

        _uiState.value = _uiState.value.copy(
            matchedVlogs = vlogs,
            matchedShorts = shorts,
            matchedPhotos = photos
        )
    }

    class Factory(private val contentRepository: ContentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(contentRepository) as T
        }
    }
}
