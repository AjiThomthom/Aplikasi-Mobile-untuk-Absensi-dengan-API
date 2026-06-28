package com.example.mdxabsensi.viewmodel

import com.example.mdxabsensi.data.model.response.UserData

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: UserData? = null
)
