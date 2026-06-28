package com.example.mdxabsensi.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mdxabsensi.data.model.request.RegisterRequest
import com.example.mdxabsensi.repository.AuthRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState>
            = _uiState

    fun register(
        nik: String,
        nama: String,
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            try {

                _uiState.value =
                    RegisterUiState(
                        isLoading = true
                    )

                val response =
                    repository.register(
                        RegisterRequest(
                            nik,
                            nama,
                            email,
                            password,
                        )
                    )

                if (response.success) {

                    _uiState.value =
                        RegisterUiState(
                            isSuccess = true,
                            message = response.message
                        )

                } else {

                    _uiState.value =
                        RegisterUiState(
                            error = response.message
                        )
                }

            } catch (e: Exception) {

                _uiState.value =
                    RegisterUiState(
                        error = e.message
                    )
            }
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}