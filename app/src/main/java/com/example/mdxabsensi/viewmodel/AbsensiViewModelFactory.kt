package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.repository.AbsensiRepository
import com.example.mdxabsensi.utils.NotificationHelper

class AbsensiViewModelFactory(
    private val repository: AbsensiRepository,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AbsensiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AbsensiViewModel(repository, notificationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
