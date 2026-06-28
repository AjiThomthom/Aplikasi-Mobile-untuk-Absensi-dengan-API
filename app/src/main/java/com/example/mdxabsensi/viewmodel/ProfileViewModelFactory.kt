package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.UserRepository

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                ProfileViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(
                userRepository,
                userPreferences
            ) as T

        }

        throw IllegalArgumentException()

    }

}