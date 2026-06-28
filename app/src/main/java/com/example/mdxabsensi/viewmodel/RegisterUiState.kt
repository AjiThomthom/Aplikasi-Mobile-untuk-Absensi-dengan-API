package com.example.mdxabsensi.viewmodel

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String = "",
    val error: String? = null
)