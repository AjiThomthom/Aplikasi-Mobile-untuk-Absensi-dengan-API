package com.example.mdxabsensi.data.model.request

data class ChangePasswordRequest(
    val nik: String,
    val old_password: String,
    val new_password: String
)
