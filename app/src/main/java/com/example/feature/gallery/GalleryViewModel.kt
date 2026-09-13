package com.example.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.HardikContentDataSource
import com.example.data.repository.ContentRepository
import com.example.domain.model.GalleryPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GalleryUiState(
    val photos: List<GalleryPhoto> = emptyList(),
    val filteredPhotos: List<GalleryPhoto> = emptyList(),
    val categories: List<String> = HardikContentDataSource.galleryCategories,
    val selectedCategory: String = "All",
    val selectedPhoto: GalleryPhoto? = null
)

class GalleryViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            contentRepository.photos.collect { list ->
                _uiState.value = _uiState.value.copy(
                    photos = list,
                    filteredPhotos = filterPhotos(list, _uiState.value.selectedCategory)
                )
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredPhotos = filterPhotos(_uiState.value.photos, category)
        )
    }

    fun openPhoto(photo: GalleryPhoto) {
        _uiState.value = _uiState.value.copy(selectedPhoto = photo)
    }

    fun closePhoto() {
        _uiState.value = _uiState.value.copy(selectedPhoto = null)
    }

    private fun filterPhotos(photos: List<GalleryPhoto>, category: String): List<GalleryPhoto> {
        return if (category == "All") photos else photos.filter { it.categoryName.equals(category, ignoreCase = true) }
    }

    class Factory(private val contentRepository: ContentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GalleryViewModel(contentRepository) as T
        }
    }
}
