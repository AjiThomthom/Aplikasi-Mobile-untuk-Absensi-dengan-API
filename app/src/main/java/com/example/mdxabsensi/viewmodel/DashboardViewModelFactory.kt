package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.AbsensiRepository

class DashboardViewModelFactory(
    private val absensiRepository: AbsensiRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                DashboardViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                absensiRepository,
                userPreferences
            ) as T

        }

        throw IllegalArgumentException()

    }
}