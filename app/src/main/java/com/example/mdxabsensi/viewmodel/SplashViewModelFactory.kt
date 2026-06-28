package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.datastore.UserPreferences

class SplashViewModelFactory(
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                SplashViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(
                userPreferences
            ) as T

        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}