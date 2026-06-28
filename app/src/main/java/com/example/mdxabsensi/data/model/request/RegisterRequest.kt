package com.example.mdxabsensi.data.model.request

data class RegisterRequest(
    val nik: String,
    val nama: String,
    val email: String,
    val password: String,
    val foto_base64: String = ""
)
