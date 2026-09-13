package com.example.feature.vlogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ContentRepository
import com.example.data.repository.InteractionRepository
import com.example.domain.model.ContentCategory
import com.example.domain.model.Vlog
import com.example.domain.model.VlogSortOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VlogsUiState(
    val isLoading: Boolean = false,
    val allVlogs: List<Vlog> = emptyList(),
    val filteredVlogs: List<Vlog> = emptyList(),
    val categories: List<ContentCategory> = emptyList(),
    val selectedCategoryId: String = "cat_all",
    val sortOrder: VlogSortOrder = VlogSortOrder.NEWEST,
    val searchQuery: String = "",
    val bookmarkedVlogIds: Set<String> = emptySet(),
    val errorMessage: String? = null
)

class VlogsViewModel(
    private val contentRepository: ContentRepository,
    private val interactionRepository: InteractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VlogsUiState())
    val uiState: StateFlow<VlogsUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeBookmarks()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                contentRepository.vlogs,
                contentRepository.categories
            ) { vlogs, cats ->
                Pair(vlogs, cats)
            }.collect { (vlogs, cats) ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allVlogs = vlogs,
                    categories = cats
                )
                applyFiltersAndSorting()
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            interactionRepository.bookmarks.collect { bookmarks ->
                _uiState.value = _uiState.value.copy(
                    bookmarkedVlogIds = bookmarks.map { it.contentId }.toSet()
                )
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        applyFiltersAndSorting()
    }

    fun setSortOrder(order: VlogSortOrder) {
        _uiState.value = _uiState.value.copy(sortOrder = order)
        applyFiltersAndSorting()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSorting()
    }

    fun toggleBookmark(vlog: Vlog) {
        viewModelScope.launch {
            interactionRepository.toggleBookmark(vlog)
        }
    }

    private fun applyFiltersAndSorting() {
        val state = _uiState.value
        var result = state.allVlogs

        // Category filter
        if (state.selectedCategoryId != "cat_all") {
            result = result.filter { it.categoryId == state.selectedCategoryId }
        }

        // Search query filter
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.trim().lowercase()
            result = result.filter {
                it.title.lowercase().contains(query) ||
                        it.description.lowercase().contains(query) ||
                        it.categoryName.lowercase().contains(query)
            }
        }

        // Sorting
        result = when (state.sortOrder) {
            VlogSortOrder.NEWEST -> result.sortedByDescending { it.timestamp }
            VlogSortOrder.POPULAR -> result.sortedByDescending { it.views }
        }

        _uiState.value = _uiState.value.copy(filteredVlogs = result)
    }

    class Factory(
        private val contentRepository: ContentRepository,
        private val interactionRepository: InteractionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VlogsViewModel(contentRepository, interactionRepository) as T
        }
    }
}
