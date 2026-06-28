package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdxabsensi.repository.AuthRepository

class RegisterViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                RegisterViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(
                repository
            ) as T

        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}