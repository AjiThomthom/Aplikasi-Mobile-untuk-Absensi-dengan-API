package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.AuthRepository

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                LoginViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                repository,
                userPreferences
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel"
        )
    }
}