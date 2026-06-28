package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.repository.AbsensiRepository

class RiwayatViewModelFactory(
    private val repository: AbsensiRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiwayatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RiwayatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
