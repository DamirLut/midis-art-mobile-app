package com.damirlutdev.artapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damirlutdev.artapp.model.Image
import com.damirlutdev.artapp.network.api.ApiRepository
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {
    private val _photo = mutableStateOf<Image?>(null)

    private var _isLoading = mutableStateOf(false)
    private val apiRepo = ApiRepository()

    val photo: Image? get() = _photo.value
    val isLoading: Boolean get() = _isLoading.value

    fun getImage(id: String) {
        viewModelScope.launch {
            _isLoading.value = true;
            val data = apiRepo.getImage(id)
            _photo.value = data.data;
            _isLoading.value = false;
        }
    }
}