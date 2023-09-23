package com.damirlutdev.artapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damirlutdev.artapp.model.Image
import com.damirlutdev.artapp.network.api.ApiRepository
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    private val _photos = mutableStateListOf<Image>()
    private var _isLoading = mutableStateOf(false)
    private val apiRepo = ApiRepository()

    val photos: List<Image> get() = _photos
    val isLoading: Boolean get() = _isLoading.value

    fun getRandom() {
        viewModelScope.launch {
            _isLoading.value = true;
            val data = apiRepo.getImages()
            _photos.clear()
            _photos.addAll(data.data)
            _isLoading.value = false;
        }
    }
}