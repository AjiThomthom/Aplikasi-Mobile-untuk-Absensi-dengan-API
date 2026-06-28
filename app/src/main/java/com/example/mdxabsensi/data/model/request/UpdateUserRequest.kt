package com.example.mdxabsensi.data.model.request

data class UpdateUserRequest(
    val nik: String,
    val nama: String,
    val email: String,
    val foto_base64: String
)
