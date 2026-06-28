package com.example.mdxabsensi.data.model.response
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)