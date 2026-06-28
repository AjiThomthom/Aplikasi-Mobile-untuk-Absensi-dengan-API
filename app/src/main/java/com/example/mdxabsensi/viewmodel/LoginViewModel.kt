package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mdxabsensi.data.model.request.LoginRequest
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.AuthRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState

    fun login(
        nik: String,
        password: String
    ) {

        viewModelScope.launch {

            _uiState.value =
                LoginUiState(isLoading = true)

            try {

                val response =
                    repository.login(
                        LoginRequest(
                            nik,
                            password
                        )
                    )

                if (response.success &&
                    response.data != null
                ) {

                    userPreferences.saveUser(
                        nik = response.data.nik,
                        nama = response.data.nama,
                        email = response.data.email,
                        foto = response.data.foto
                    )

                    _uiState.value =
                        LoginUiState(
                            isSuccess = true,
                            user = response.data
                        )

                } else {
                    val errorMsg = if (response.message.contains("401") || response.message.contains("Unauthorized", ignoreCase = true)) {
                        "NIK atau password yang anda masukan salah"
                    } else {
                        response.message
                    }
                    _uiState.value =
                        LoginUiState(
                            error = errorMsg
                        )
                }

            } catch (e: retrofit2.HttpException) {
                val errorMsg = if (e.code() == 401) {
                    "NIK atau password yang anda masukan salah"
                } else {
                    e.message() ?: "Terjadi kesalahan server"
                }
                _uiState.value = LoginUiState(
                    isLoading = false, // Pastikan loading dihentikan
                    error = errorMsg
                )
            } catch (e: Exception) {
                val errorMsg = if (e.message?.contains("401") == true || e.message?.contains("Unauthorized", ignoreCase = true) == true) {
                    "NIK atau password yang anda masukan salah"
                } else {
                    e.message
                }
                _uiState.value =
                    LoginUiState(
                        error = errorMsg
                    )
            }
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}