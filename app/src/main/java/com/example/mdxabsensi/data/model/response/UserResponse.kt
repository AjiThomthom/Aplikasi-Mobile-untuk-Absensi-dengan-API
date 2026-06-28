package com.example.mdxabsensi.data.model.response


data class UserResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)