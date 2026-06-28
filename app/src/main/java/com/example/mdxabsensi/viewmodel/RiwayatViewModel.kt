package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdxabsensi.data.model.response.RiwayatItem
import com.example.mdxabsensi.repository.AbsensiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RiwayatViewModel(
    private val repository: AbsensiRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _riwayatList = MutableStateFlow<List<RiwayatItem>>(emptyList())
    val riwayatList: StateFlow<List<RiwayatItem>> = _riwayatList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchRiwayat(nik: String) {
        if (nik.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getRiwayat(nik)
                if (response.success) {
                    // Sort descending by timeinout (latest logs first)
                    _riwayatList.value = response.data.sortedByDescending { it.timeinout }
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Koneksi internet bermasalah"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
