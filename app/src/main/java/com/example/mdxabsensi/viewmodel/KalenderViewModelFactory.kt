package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.AbsensiRepository

class KalenderViewModelFactory(
    private val absensiRepository: AbsensiRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(KalenderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KalenderViewModel(absensiRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
